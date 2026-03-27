# ADR-002: Persistencia Relacional con Cache-Aside para Rendimiento

**Estado:** Aceptado
**Fecha:** 26/03/2026
**Patrones adoptados:** Repository Pattern (persistencia relacional), Cache-Aside, Redundancia Activa, Connection Pool, Read Replica / CQRS parcial

---

## Contexto y Problema

La plataforma maneja distintos tipos de datos con necesidades muy diferentes: datos estructurados con relaciones complejas (usuarios, cursos, inscripciones, calificaciones), datos de sesion y cache (tokens, disponibilidad de contenido), y datos de actividad de aprendizaje que pueden crecer rapidamente.

El motor de aprendizaje adaptativo requiere consultas relacionales complejas para determinar el estado de avance de un estudiante, las reglas de adaptacion configuradas por el docente y que materiales recomendar. La analitica requiere agregaciones sobre historicos de calificaciones.

Al mismo tiempo, operaciones frecuentes como consultar la disponibilidad de contenido o verificar el estado de autenticacion de un usuario deben responder en milisegundos para cumplir con DR-01 (≤ 2 segundos P95). Estas operaciones no pueden presionar la base de datos en cada peticion bajo carga de 5,000 usuarios concurrentes.

Necesitamos decidir la estrategia de persistencia que garantice consistencia, rendimiento bajo alta carga, durabilidad de datos academicos y costo razonable de operacion.

---

## Drivers de Decision

- **DR-01:** Rendimiento ≤ 2 segundos P95 — las consultas de datos deben ser rapidas (Prioridad: Alta)
- **DR-03:** Disponibilidad ≥ 99.5% — la base de datos no puede ser punto unico de fallo (Prioridad: Alta)
- **DR-04:** Seguridad — datos personales y academicos requieren controles de acceso robustos (Prioridad: Alta)
- **DR-05:** Mantenibilidad — el esquema debe evolucionar facilmente con el sistema (Prioridad: Media)
- **DR-07:** Motor Adaptativo — requiere consultas relacionales complejas con joins y transacciones (Prioridad: Alta)

---

## Alternativas Consideradas

### Alternativa 1: PostgreSQL (Persistencia Relacional) ELEGIDA

**Descripcion:**
Sistema de gestion de base de datos relacional con soporte completo para ACID, transacciones complejas, joins, constraints e indices avanzados.

**Pros:**
- ACID completo: garantiza consistencia de datos academicos (calificaciones, progreso).
- Consultas relacionales complejas con JOINs optimizados: ideal para el motor adaptativo.
- Schemas separados por dominio dentro de una sola instancia.
- Disponible como servicio administrado con replicacion y backups automaticos.

**Contras:**
- Escalabilidad horizontal de escrituras es compleja.
- Requiere migraciones de esquema cuidadosas.

---

### Alternativa 2: MongoDB (NoSQL Documental)

**Pros:**
- Flexibilidad de esquema.
- Escala horizontal nativa.

**Contras:**
- Soporte limitado para transacciones multi-documento.
- No soporta JOINs nativos eficientes: el motor adaptativo requeriria multiples consultas.
- Consistencia eventual problematica para datos de calificaciones.

---

### Alternativa 3: DynamoDB (NoSQL Key-Value Administrado)

**Pros:**
- Latencia garantizada en milisegundos.
- Escalabilidad automatica.

**Contras:**
- Muy limitado para consultas relacionales.
- El modelo de acceso debe definirse desde el inicio y es dificil de cambiar.
- Vendor lock-in con AWS.

---

## Decision

Adoptamos la siguiente estrategia de persistencia, implementando los patrones descritos:

### Patron 1: Repository Pattern — Persistencia Relacional

**Tecnologia:** PostgreSQL 16 (AWS RDS Multi-AZ)
**Implementacion:**
- Un unico cluster con **schemas separados por servicio**: `schema_users`, `schema_courses`, `schema_assessments`, `schema_collaboration`, `schema_analytics`.
- Acceso desde cada servicio mediante Prisma ORM como capa Repository.
- Politica "no cross-schema queries": ningun servicio lee ni escribe en el schema de otro.

**Por que PostgreSQL sobre las alternativas:** El nucleo del sistema —el motor adaptativo— requiere consultas relacionales complejas con JOINs entre inscripciones, lecciones, calificaciones y reglas de adaptacion. MongoDB requeriria multiples viajes a la BD o desnormalizacion agresiva. DynamoDB impone un modelo de acceso rigido incompatible con los patrones de consulta evolutivos del sistema. Los datos academicos requieren garantias ACID que PostgreSQL provee nativamente.

---

### Patron 2: Cache-Aside (Lazy Loading)


**Implementacion:**
- Los servicios consultan primero la cache; ante un miss, consultan PostgreSQL y almacenan el resultado.
- TTLs por tipo de dato:
  - Sesiones activas (JWT): TTL = 8 horas
  - Listas de cursos y materiales: TTL = 3,600 segundos (1 hora)
  - Reglas de adaptacion: TTL = 60 segundos (permite que cambios del docente tengan efecto rapido)
- Estado de evaluaciones en progreso: almacenado en cache para prevenir perdida de datos.

**Por que Cache-Aside:** Reduce la latencia de operaciones de lectura frecuente de ~500ms a ~5-80ms, cumpliendo DR-01 bajo la carga de 5,000 usuarios concurrentes sin sobredimensionar PostgreSQL.

---

### Patron 3: Redundancia Activa (Active Redundancy)


**Implementacion:**
- mantiener una replica sincrona en una segunda zona de disponibilidad.
- Failover automatico en ~60 segundos ante falla de la instancia primaria.
- Backups automaticos cada 24 horas con retencion de 7 dias (RPO ≤ 15 min con Point-in-Time Recovery).

**Por que Redundancia Activa:** PostgreSQL es el componente de estado critico. Sin failover automatico, una falla de la BD implicaria downtime superior al RTO de 1 hora (RNF-02).

---

### Patron 4: Connection Pool

**Tecnologia:** PgBouncer (sidecar)
**Implementacion:**
- PgBouncer actua como proxy entre los servicios ECS y RDS PostgreSQL.
- Cada servicio configura un pool de maximo 20 conexiones activas.
- Serializa solicitudes que excedan el pool, evitando saturar el limite de conexiones de PostgreSQL (~100 en db.t3.large).

**Por que Connection Pool:** Sin este patron, 10 instancias del Assessment Service podrian intentar 200-500 conexiones simultaneas a PostgreSQL, superando el limite de la instancia.

---

### Patron 5: Read Replica / CQRS parcial

**Implementacion:**
- El Analytics Service ejecuta consultas de lectura exclusivamente sobre la replica de lectura.
- Las consultas de analitica son complejas (joins multiples, agregaciones) y de larga duracion.
- Lag de replicacion aceptado: ~5 segundos (coherente con RF-07: datos consolidados actualizados cada 24h).

**Por que Read Replica:** Aislar la carga de lectura del Analytics Service protege el rendimiento de las operaciones transaccionales criticas (envio de evaluaciones, actualizacion de progreso).

---

## Como cumple con los drivers:

| Driver | Como esta decision lo cumple |
|--------|------------------------------|
| DR-01 (Rendimiento ≤ 2 seg) | Cache-Aside sirve lecturas frecuentes en <10ms; Read Replica aisla carga analitica; Connection Pool reutiliza conexiones. |
| DR-03 (Disponibilidad 99.5%) | Redundancia Activa Multi-AZ con failover automatico < 60s; Cache-Aside reduce presion sobre BD bajo picos. |
| DR-04 (Seguridad) | RDS cifra datos en reposo (AES-256) y en transito (TLS); acceso restringido por VPC y security groups. |
| DR-05 (Mantenibilidad) | Schemas separados por servicio permiten evolucionar el modelo de datos de cada dominio independientemente. |
| DR-07 (Motor Adaptativo) | PostgreSQL soporta las consultas relacionales complejas con JOINs y transacciones ACID necesarias para el motor. |

---

## Consecuencias

### Positivas:

1. **Consistencia garantizada para datos academicos:** ACID en PostgreSQL garantiza que ningun estudiante vea calificaciones incorrectas.
2. **Rendimiento de lectura mejorado:** Cache-Aside resuelve lecturas frecuentes en <10ms sin impactar PostgreSQL.
3. **Alta disponibilidad:** Redundancia Activa Multi-AZ garantiza recuperacion en <60s.
4. **Separacion de carga:** Read Replica atiende analitica sin competir con transacciones criticas.

### Negativas (y mitigaciones):

1. **Base de datos compartida entre servicios**
   - **Riesgo:** Cambios de schema pueden afectar otros dominios.
   - **Mitigacion:** Schemas separados + politica "no cross-schema queries". Dependencias resueltas solo por eventos Publish/Subscribe.

2. **Costo adicional de la cache**
   - **Riesgo:** ElastiCache suma costo mensual.
   - **Mitigacion:** El costo (~$25/mes en cache.t3.medium) es minimo comparado con el costo de sobredimensionar RDS para lecturas sin cache.

3. **Gestion de invalidacion de cache**
   - **Riesgo:** Datos obsoletos en cache.
   - **Mitigacion:** TTLs conservadores. Datos criticos (calificaciones finales) no se cachean; siempre se consultan directamente de PostgreSQL.

---

## Notas Adicionales

- Si el volumen de datos de analitica supera los 100 millones de registros, se evaluara la migracion de ese schema a un data warehouse (ej. AWS Redshift).


---

