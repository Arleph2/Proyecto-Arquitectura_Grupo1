# ADR-002: Adoptar PostgreSQL como Base de Datos Principal con Redis para Caché

**Estado:** Aceptado  
**Fecha:** 22/03/2026  

---

## Contexto y Problema

La plataforma maneja distintos tipos de datos con necesidades muy diferentes: datos estructurados con relaciones complejas (usuarios, cursos, inscripciones, calificaciones), datos de sesión y caché (tokens, disponibilidad de contenido), y datos de actividad de aprendizaje que pueden crecer rápidamente (logs de actividad, métricas de progreso).

El motor de aprendizaje adaptativo requiere consultas relacionales complejas para determinar el estado de avance de un estudiante, las reglas de adaptación configuradas por el docente y qué materiales recomendar. La analítica requiere agregaciones sobre históricos de calificaciones y tiempo de uso. La plataforma debe garantizar consistencia de datos (un estudiante no puede ver una calificación incorrecta en su dashboard) y durabilidad (las calificaciones de evaluaciones no se pueden perder).

Al mismo tiempo, operaciones frecuentes como consultar la disponibilidad de contenido o verificar el estado de autenticación de un usuario deben responder en milisegundos para cumplir con DR-01 (≤ 2 segundos P95). Estas operaciones no pueden golpear la base de datos en cada petición bajo carga de 5,000 usuarios concurrentes.

Necesitamos decidir la estrategia de persistencia que garantice consistencia, rendimiento bajo alta carga, durabilidad de datos académicos y costo razonable de operación.

---

## Drivers de Decisión

- **DR-01:** Rendimiento ≤ 2 segundos P95 - las consultas de datos deben ser rápidas (Prioridad: Alta)
- **DR-03:** Disponibilidad ≥ 99.5% - la base de datos no puede ser punto único de fallo (Prioridad: Alta)
- **DR-04:** Seguridad - datos personales y académicos requieren controles de acceso robustos (Prioridad: Alta)
- **DR-05:** Mantenibilidad - el esquema debe evolucionar fácilmente con el sistema (Prioridad: Media)
- **DR-07:** Motor Adaptativo - requiere consultas relacionales complejas con joins y transacciones (Prioridad: Alta)

---

## Alternativas Consideradas

### Alternativa 1: PostgreSQL (SQL Relacional)

**Descripción:**  
Sistema de gestión de base de datos relacional open-source, altamente confiable y con soporte completo para ACID, transacciones complejas, joins, constraints e índices avanzados. Ampliamente usado en producción a escala por empresas como Instagram, Airbnb y Notion.

**Pros:**
- ACID completo: garantiza consistencia de datos académicos (calificaciones, progreso).
- Consultas relacionales complejas con JOINs optimizados: ideal para el motor adaptativo.
- Schemas separados por dominio dentro de una sola instancia (útil para Service-Based Architecture).
- Soporte robusto para datos JSON (JSONB) cuando se necesite flexibilidad de esquema.
- Maduro, bien documentado y con amplia comunidad.
- Disponible como servicio administrado (AWS RDS, Google Cloud SQL) con replicación y backups automáticos.

**Contras:**
- Escalabilidad horizontal de escrituras es compleja (requiere sharding manual o Citus).
- No es la mejor opción para datos de alta velocidad sin esquema fijo (ej. logs de actividad en tiempo real).
- Requiere migraciones de esquema cuidadosas cuando el modelo de datos evoluciona.

---

### Alternativa 2: MongoDB (NoSQL Documental)

**Descripción:**  
Base de datos NoSQL orientada a documentos. Almacena datos en formato BSON (similar a JSON). Ofrece alta flexibilidad de esquema y escala horizontalmente mediante sharding nativo.

**Pros:**
- Flexibilidad de esquema: ideal cuando la estructura de datos puede variar (ej. distintos tipos de preguntas en evaluaciones).
- Escala horizontal nativa mediante sharding.
- Rendimiento de escritura alto para datos de alta velocidad.
- Ideal para datos jerárquicos naturales (ej. un curso con sus módulos y lecciones anidados).

**Contras:**
- Soporte limitado para transacciones multi-documento (aunque mejoró en versiones recientes, es más complejo).
- No soporta JOINs nativos eficientes: el motor adaptativo requeriría múltiples consultas o lógica de aplicación compleja.
- Consistencia eventual en configuraciones distribuidas puede ser problemática para datos de calificaciones.
- Mayor costo de licenciamiento en producción con MongoDB Atlas a escala.
- El equipo requiere curva de aprendizaje adicional para modelado de datos NoSQL.

---

### Alternativa 3: DynamoDB (NoSQL Key-Value/Documental Administrado)

**Descripción:**  
Servicio de base de datos NoSQL completamente administrado de AWS. Ofrece escalabilidad automática, disponibilidad multi-región y latencia de milisegundos a cualquier escala.

**Pros:**
- Latencia garantizada en milisegundos a cualquier escala.
- Escalabilidad automática sin gestión de infraestructura.
- Alta disponibilidad multi-región nativa.
- Modelo de pago por uso (pay-per-request).

**Contras:**
- Muy limitado para consultas relacionales: sin JOINs ni transacciones complejas entre tablas.
- El diseño de acceso patterns debe definirse desde el inicio y es difícil de cambiar.
- Vendor lock-in con AWS: migrar a otro proveedor cloud es costoso.
- El costo escala con el volumen de lectura/escritura y puede ser impredecible.
- No es adecuado para el motor adaptativo que requiere consultas analíticas complejas.

---

## Decisión

Adoptamos **PostgreSQL 16** como base de datos principal y **Redis 7** como capa de caché y almacenamiento de sesiones:

**PostgreSQL 16** (AWS RDS Multi-AZ):
- Un único clúster con **schemas separados por servicio**: `schema_users`, `schema_courses`, `schema_assessments`, `schema_collaboration`, `schema_analytics`.
- Configuración Multi-AZ para alta disponibilidad (DR-03).
- Read replicas para consultas de analítica, separando carga de lectura de escritura.
- Backups automáticos cada 24 horas con retención de 7 días.

**Redis 7** (AWS ElastiCache):
- Caché de sesiones de usuario (tokens JWT activos, 8 horas de TTL).
- Caché de contenido frecuentemente consultado (listas de cursos, materiales, hasta 5 minutos de TTL).
- Almacenamiento de estado de evaluaciones en progreso (previene pérdida de datos si el estudiante cierra el navegador).
- Caché de resultados del motor adaptativo por estudiante (hasta 1 minuto de TTL).

---

## Justificación

### Por qué esta opción (y no las otras):

Elegimos PostgreSQL sobre MongoDB porque el núcleo del sistema —el motor de aprendizaje adaptativo— requiere consultas relacionales complejas: determinar el estado de avance de un estudiante implica relacionar inscripciones, lecciones completadas, calificaciones obtenidas y reglas de adaptación configuradas por el docente. Estas consultas son naturalmente relacionales y con MongoDB requerirían múltiples viajes a la base de datos o lógica compleja en la capa de aplicación, incrementando la latencia y violando DR-01.

Adicionalmente, los datos académicos requieren consistencia ACID: una calificación no puede perderse ni duplicarse bajo ninguna circunstancia. PostgreSQL garantiza esto con transacciones nativas. MongoDB en configuraciones distribuidas ofrece consistencia eventual que es inaceptable para datos de calificaciones.

Elegimos PostgreSQL sobre DynamoDB porque DynamoDB impone un modelo de acceso rígido que debe definirse al inicio y es muy difícil de cambiar. En una plataforma en evolución, los patrones de consulta del motor adaptativo y la analítica cambiarán a medida que añadimos funcionalidades. PostgreSQL permite adaptar las consultas sin rediseñar la estructura de datos.

Redis complementa a PostgreSQL aliviando la carga de lecturas frecuentes y repetitivas, lo que permite cumplir DR-01 sin sobrecargar la base de datos principal bajo 5,000 usuarios concurrentes.

### Cómo cumple con los drivers:

| Driver | Cómo esta decisión lo cumple |
|--------|------------------------------|
| DR-01 (Rendimiento ≤ 2 seg) | Redis cachea respuestas frecuentes (listas de cursos, sesiones); PostgreSQL con índices optimizados y read replicas reduce la latencia de consultas complejas. |
| DR-03 (Disponibilidad 99.5%) | RDS Multi-AZ hace failover automático en < 60 segundos; Redis ElastiCache con replicación garantiza continuidad del caché. |
| DR-04 (Seguridad) | RDS cifra datos en reposo (AES-256) y en tránsito (TLS); acceso restringido por VPC y security groups. |
| DR-05 (Mantenibilidad) | Schemas separados por servicio permiten evolucionar el modelo de datos de cada dominio independientemente. |
| DR-07 (Motor Adaptativo) | PostgreSQL soporta consultas relacionales complejas con JOINs y transacciones ACID necesarias para calcular el estado de adaptación. |

---

## Consecuencias

### Positivas:

1. **Consistencia garantizada para datos académicos:** Las calificaciones y el progreso del estudiante se almacenan con garantías ACID completas. Ningún estudiante verá una calificación incorrecta o perderá su progreso.
2. **Rendimiento de lectura mejorado con Redis:** Las operaciones de lectura frecuente (verificar sesión, cargar lista de cursos) se resuelven desde caché en < 10ms, sin impactar la base de datos principal.
3. **Alta disponibilidad con RDS Multi-AZ:** El failover automático garantiza que la plataforma se recupere de fallas de infraestructura en menos de 60 segundos, contribuyendo al 99.5% de disponibilidad requerido.
4. **Separación de carga:** Las read replicas de PostgreSQL atienden las consultas analíticas sin competir con las escrituras del motor adaptativo y las evaluaciones.

### Negativas (y mitigaciones):

1. **Base de datos compartida entre servicios crea acoplamiento**
   - **Riesgo:** Un cambio de schema en el dominio de evaluaciones podría afectar al dominio de analítica si comparten tablas.
   - **Mitigación:** Cada schema es propiedad exclusiva de su servicio. Ningún servicio puede escribir en el schema de otro. Las dependencias de datos entre servicios se resuelven mediante eventos asíncronos (RabbitMQ), nunca mediante consultas cross-schema directas.

2. **Costo adicional de Redis**
   - **Riesgo:** Agregar Redis suma costo de infraestructura mensual.
   - **Mitigación:** El costo de una instancia Redis ElastiCache cache.t3.medium (~$25/mes) es mínimo comparado con el costo de escalar RDS para atender lecturas sin caché. El ahorro en RDS compensa el costo de Redis.

3. **Gestión de invalidación de caché**
   - **Riesgo:** Si Redis tiene datos obsoletos, un estudiante podría ver información desactualizada (ej. una calificación que ya fue modificada).
   - **Mitigación:** Definir TTLs conservadores (máximo 5 minutos para datos de contenido, 1 minuto para datos de progreso). Los datos críticos (calificaciones finales) no se cachean; siempre se consultan directamente de PostgreSQL.

---

## Alternativas Descartadas (Detalle)

### Por qué se descartó MongoDB:

MongoDB fue descartado porque el modelo de datos de la plataforma es fundamentalmente relacional. La relación entre estudiantes, inscripciones, cursos, módulos, lecciones, evaluaciones, calificaciones y reglas de adaptación forma un grafo complejo de entidades relacionadas. En PostgreSQL, estas relaciones se modelan con claves foráneas y se consultan con JOINs eficientes. En MongoDB, el mismo tipo de consulta requeriría o bien desnormalizar agresivamente los datos (creando duplicidad y problemas de consistencia) o bien realizar múltiples consultas en la aplicación (aumentando la latencia y la complejidad del código).

**Cuándo sería mejor:**
- Si los datos de la plataforma fueran predominantemente documentales y poco relacionados entre sí.
- Si el equipo tuviera experiencia previa en modelado NoSQL y los patrones de acceso estuvieran completamente definidos desde el inicio.

### Por qué se descartó DynamoDB:

DynamoDB fue descartado principalmente por el vendor lock-in con AWS y la inflexibilidad del modelo de acceso. El motor adaptativo y la analítica requieren patrones de consulta que evolucionarán a medida que se agregan funcionalidades. DynamoDB fuerza a definir los access patterns desde el inicio, y cambiarlos posteriormente implica rediseñar las tablas y migrar los datos. Para una plataforma en construcción y evolución, esta rigidez representa un riesgo técnico alto.

**Cuándo sería mejor:**
- Si el sistema estuviera completamente en AWS y los patrones de acceso estuvieran 100% definidos y fueran simples (lectura/escritura por clave primaria).
- Si el requisito de latencia fuera de microsegundos (DynamoDB garantiza latencia de 1 dígito en milisegundos a cualquier escala).

---

## Validación

- [x] Cumple con DR-01 (Rendimiento ≤ 2 seg): Redis cachea operaciones frecuentes; índices PostgreSQL + read replicas para analítica.
- [x] Cumple con DR-03 (Disponibilidad 99.5%): RDS Multi-AZ con failover automático < 60 seg; Redis con replicación.
- [x] Cumple con DR-04 (Seguridad): Cifrado en reposo y tránsito; acceso restringido por VPC.
- [x] Cumple con DR-05 (Mantenibilidad): Schemas separados por servicio; migraciones independientes por dominio.
- [x] Cumple con DR-07 (Motor Adaptativo): PostgreSQL soporta las consultas relacionales complejas del motor.

---

## Notas Adicionales

- Si el volumen de datos de analítica supera los 100 millones de registros, se evaluará la migración de ese schema a un data warehouse (ej. AWS Redshift) para separar la carga analítica de la operacional.
- Esta decisión se revisará en el Mes 12 cuando se evalúe la carga real de datos de actividad de aprendizaje.

---

## Referencias

- [SRS] Software Requirements Specification - Plataforma de Aprendizaje Adaptativo y Colaborativo
- [ADR-001] Service-Based Architecture - Plataforma de Aprendizaje
- PostgreSQL Documentation: https://www.postgresql.org/docs/
- Redis Documentation: https://redis.io/docs/
- AWS RDS Multi-AZ: https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.MultiAZ.html

---

