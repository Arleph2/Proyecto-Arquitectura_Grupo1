# Análisis de Arquitectura — Método ATAM
## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Versión:** 1.0  
**Fecha:** 26/03/2026  
**Grupo:** 4


**Documentos base:**
- SRS v1.0 — `SRS_PlataformaAdaptativa_GrupoX.md`
- SAD v2.0 — `SAD_PlataformaAdaptativa_GrupoX.md`
- ADR-001 — Service-Based Architecture
- ADR-002 — PostgreSQL + Redis
- ADR-003 — Motor Adaptativo + RabbitMQ

---

## TABLA DE CONTENIDOS

1. [Introducción al ATAM](#1-introducción-al-atam)
2. [Presentación de la Arquitectura](#2-presentación-de-la-arquitectura)
3. [Árbol de Utilidad (Utility Tree)](#3-árbol-de-utilidad-utility-tree)
4. [Análisis de Enfoques Arquitectónicos](#4-análisis-de-enfoques-arquitectónicos)
5. [Escenarios Priorizados](#5-escenarios-priorizados)
6. [Análisis de Sensibilidad, Trade-offs y Riesgos](#6-análisis-de-sensibilidad-trade-offs-y-riesgos)
7. [Puntos de Sensibilidad](#7-puntos-de-sensibilidad)
8. [Trade-offs Identificados](#8-trade-offs-identificados)
9. [Riesgos No Resueltos](#9-riesgos-no-resueltos)
10. [Resultados y Conclusiones del ATAM](#10-resultados-y-conclusiones-del-atam)

---

## 1. INTRODUCCIÓN AL ATAM

### 1.1 Propósito

Este documento aplica el **Architecture Tradeoff Analysis Method (ATAM)** a la arquitectura de la Plataforma de Aprendizaje Adaptativo y Colaborativo. El ATAM es un método de evaluación arquitectónica desarrollado por el Software Engineering Institute (SEI) que permite:

- Identificar los riesgos arquitecturales antes de la implementación.
- Detectar trade-offs entre atributos de calidad que compiten entre sí.
- Descubrir puntos de sensibilidad donde pequeños cambios tienen grandes impactos.
- Validar que la arquitectura cumple los requisitos de calidad del SRS.

El resultado del ATAM no es una validación de que la arquitectura es "correcta", sino un mapa documentado de qué decisiones son sólidas, cuáles presentan riesgos y dónde existen tensiones entre atributos de calidad que el equipo debe gestionar conscientemente.


### 1.2 Fases del ATAM Aplicadas

El ATAM completo consta de 9 pasos. En el contexto académico de este proyecto se aplican los pasos esenciales:

| Paso ATAM | Descripción | Sección en este documento |
|-----------|-------------|--------------------------|
| 1. Presentar el método ATAM | Explicar el método a los stakeholders | Sección 1 |
| 2. Presentar los drivers de negocio | Objetivos y contexto del sistema | Sección 2.1 |
| 3. Presentar la arquitectura | Descripción de la solución arquitectónica | Sección 2.2 |
| 4. Identificar enfoques arquitectónicos | Patrones y tácticas usadas | Sección 4 |
| 5. Generar el Árbol de Utilidad | QAs, atributos, escenarios priorizados | Sección 3 |
| 6. Analizar los enfoques (ATAM) | Sensibilidad, trade-offs, riesgos | Sección 6 |
| 7. Hacer Brainstorming de escenarios | Escenarios adicionales de stakeholders | Sección 5 |
| 8. Analizar los enfoques nuevamente | Análisis de escenarios adicionales | Sección 6 |
| 9. Presentar resultados | Resumen de hallazgos | Sección 10 |

### 1.3 Referencias

- Bass, L., Clements, P. & Kazman, R. (2012). *Software Architecture in Practice* (3rd ed.). Addison-Wesley. Cap. 21: ATAM.
- Kazman, R., Klein, M. & Clements, P. (2000). *ATAM: Method for Architecture Evaluation*. CMU/SEI-2000-TR-004.
- [SRS] Software Requirements Specification — Plataforma de Aprendizaje Adaptativo y Colaborativo, v1.0
- [SAD] Software Architecture Document — Plataforma de Aprendizaje Adaptativo y Colaborativo, v2.0

---

## 2. PRESENTACIÓN DE LA ARQUITECTURA

### 2.1 Drivers de Negocio

Los siguientes factores de negocio determinan los requisitos de calidad que la arquitectura debe satisfacer:

| Driver de Negocio | Descripción | Impacto en Arquitectura |
|-------------------|-------------|------------------------|
| **Institución educativa** | La institución necesita una plataforma confiable y disponible durante períodos de evaluación académica | Disponibilidad ≥ 99.5%, escalabilidad ante picos |
| **Equipo de desarrollo pequeño** | 3-4 estudiantes deben construir y mantener el MVP en un semestre | Complejidad operacional limitada, Service-Based sobre Microservicios |
| **Crecimiento gradual de usuarios** | 500 usuarios el primer mes, hasta 5,000 al primer año | Escalabilidad horizontal bajo demanda |
| **Aprendizaje adaptativo como diferenciador** | La adaptación del recorrido educativo es la funcionalidad de mayor valor | Motor de reglas configurable, correctitud del motor |
| **Cumplimiento regulatorio colombiano** | Ley 1581/2012 sobre protección de datos personales | Seguridad, cifrado, consentimiento explícito |
| **Extensibilidad futura** | Integración con otros LMS, potencial motor ML en V2 | APIs REST + OpenAPI, arquitectura extensible |

### 2.2 Arquitectura Presentada

La arquitectura es una **Service-Based Architecture** con los siguientes elementos principales:

**Servicios (5 de grano grueso):**

| Servicio | Responsabilidad | Entidades del Modelo de Dominio |
|----------|-----------------|--------------------------------|
| **User Service** | Autenticación, autorización RBAC | `Usuario`, `Sesion` |
| **Course Service** | Gestión de cursos, módulos, lecciones | `Curso`, `Modulo`, `Leccion`, `Inscripcion` |
| **Assessment & Adaptive Service** | Evaluaciones, motor de reglas adaptativas | `Evaluacion`, `Pregunta`, `Respuesta`, `IntentoEvaluacion`, `ReglaAdaptacion` |
| **Collaboration Service** | Foros, grupos de estudio, tutorías | `Foro`, `Hilo`, `Publicacion`, `GrupoEstudio`, `AsignacionTutor` |
| **Analytics Service** | Progreso, reportes, alertas | `ProgresoEstudiante`, `ReporteRendimiento`, `Alerta` |

**Infraestructura clave:**
- API Gateway (punto de entrada único, JWT validation, RBAC, rate limiting)
- PostgreSQL 16 con schemas separados por dominio (RDS Multi-AZ)
- Redis 7 (ElastiCache) para caché de sesiones, contenido y reglas de adaptación
- RabbitMQ (AWS MQ) para comunicación asíncrona entre servicios
- AWS ECS Fargate con auto-scaling independiente por servicio

**Decisiones arquitecturales relevantes para el ATAM:**

| ADR | Decisión | Trade-off Central |
|-----|----------|-------------------|
| ADR-001 | Service-Based Architecture | Escalabilidad selectiva vs. Complejidad operacional |
| ADR-002 | PostgreSQL + Redis | Consistencia ACID vs. Base de datos compartida |
| ADR-003 | Motor Adaptativo + RabbitMQ | Rendimiento (async) vs. Consistencia eventual del dashboard |

---

## 3. ÁRBOL DE UTILIDAD (UTILITY TREE)

El Árbol de Utilidad desglosa los atributos de calidad en escenarios concretos y los prioriza según su importancia para el negocio (H=Alta, M=Media, B=Baja) y la dificultad de implementación (H=Alta, M=Media, B=Baja).

La notación de prioridad es: **(Importancia para el negocio / Dificultad de implementación)**

```
UTILIDAD (Calidad de la Arquitectura)
│
├── RENDIMIENTO (Performance)
│   ├── RND-01: El sistema responde operaciones principales en ≤ 2 seg P95
│   │         bajo 500 usuarios concurrentes .......................... (H/M)
│   ├── RND-02: La carga de lista de cursos responde en < 200ms
│   │         gracias a caché Redis .................................. (H/B)
│   ├── RND-03: La calificación automática de una evaluación
│   │         se completa síncronamente en < 500ms ................... (H/M)
│   └── RND-04: El login con bcrypt no excede 500ms P95
│             bajo 500 logins simultáneos ........................... (M/H)
│
├── DISPONIBILIDAD (Availability)
│   ├── DISP-01: El sistema mantiene ≥ 99.5% uptime mensual ........... (H/M)
│   ├── DISP-02: Una falla del Analytics Service no impide completar
│   │           evaluaciones ........................................ (H/B)
│   ├── DISP-03: El sistema se recupera de una falla de RDS
│   │           en < 60 segundos (Multi-AZ failover) ................ (H/M)
│   └── DISP-04: Los eventos de RabbitMQ no se pierden si un
│               consumidor falla temporalmente ....................... (H/B)
│
├── ESCALABILIDAD (Scalability)
│   ├── ESC-01: El sistema soporta 5,000 usuarios concurrentes
│   │         sin degradación del rendimiento ....................... (H/H)
│   ├── ESC-02: El Assessment Service escala de 3 a 10 instancias
│   │         en ≤ 5 minutos ante pico de evaluaciones .............. (H/M)
│   └── ESC-03: El sistema soporta 500 cursos activos simultáneos ... (M/B)
│
├── SEGURIDAD (Security)
│   ├── SEG-01: Un estudiante no puede acceder a funciones de
│   │         docente ni administrador .............................. (H/B)
│   ├── SEG-02: Las credenciales y datos personales están
│   │         protegidos (bcrypt + TLS + AES-256) ................... (H/B)
│   ├── SEG-03: Un token JWT comprometido expira en máximo 8h ....... (H/B)
│   └── SEG-04: La plataforma cumple con Ley 1581/2012 .............. (H/M)
│
├── MANTENIBILIDAD (Maintainability)
│   ├── MNT-01: Un nuevo módulo se integra en ≤ 2 semanas sin
│   │         afectar servicios existentes .......................... (M/M)
│   ├── MNT-02: Un docente modifica reglas de adaptación sin
│   │         redespliegue del sistema .............................. (H/B)
│   ├── MNT-03: Un bug fix se despliega a producción en ≤ 30 min
│   │         con zero-downtime .................................... (M/B)
│   └── MNT-04: El código crítico tiene cobertura de tests ≥ 70% ... (M/M)
│
├── INTEROPERABILIDAD (Interoperability)
│   ├── INT-01: Un sistema externo consume la API REST de la
│   │         plataforma con documentación OpenAPI 3.0 .............. (M/B)
│   └── INT-02: El sistema registra actividades de aprendizaje
│               en formato compatible con xAPI (pospuesto V2) ....... (B/M)
│
└── MOTOR ADAPTATIVO (Functional Suitability)
    ├── ADT-01: El motor aplica correctamente las reglas de
    │         adaptación para ≥ 95% de los estudiantes .............. (H/M)
    └── ADT-02: Las reglas de adaptación configuradas por el
              docente tienen efecto inmediato sin redespliegue ...... (H/B)
```

### 3.1 Resumen del Árbol de Utilidad

| ID | Atributo | Escenario | Prioridad Negocio | Dificultad |
|----|----------|-----------|-------------------|------------|
| RND-01 | Performance | ≤ 2 seg P95 en operaciones principales | Alta | Media |
| RND-02 | Performance | Lista cursos < 200ms con Redis | Alta | Baja |
| RND-03 | Performance | Calificación automática < 500ms | Alta | Media |
| RND-04 | Performance | Login bcrypt < 500ms P95 | Media | Alta |
| DISP-01 | Availability | ≥ 99.5% uptime mensual | Alta | Media |
| DISP-02 | Availability | Falla Analytics no bloquea evaluaciones | Alta | Baja |
| DISP-03 | Availability | Recuperación RDS < 60 seg | Alta | Media |
| DISP-04 | Availability | Eventos RabbitMQ no se pierden | Alta | Baja |
| ESC-01 | Scalability | 5,000 usuarios concurrentes sin degradación | Alta | Alta |
| ESC-02 | Scalability | Auto-scaling Assessment Service ≤ 5 min | Alta | Media |
| SEG-01 | Security | RBAC correcto por rol | Alta | Baja |
| SEG-02 | Security | Datos protegidos (bcrypt + TLS) | Alta | Baja |
| SEG-03 | Security | JWT expira en 8h | Alta | Baja |
| SEG-04 | Security | Cumplimiento Ley 1581 | Alta | Media |
| MNT-01 | Maintainability | Nuevo módulo en ≤ 2 semanas | Media | Media |
| MNT-02 | Maintainability | Reglas adaptativas sin redespliegue | Alta | Baja |
| MNT-03 | Maintainability | Despliegue zero-downtime | Media | Baja |
| ADT-01 | Func. Suitability | Motor adaptativo correcto ≥ 95% | Alta | Media |
| ADT-02 | Func. Suitability | Reglas en BD con efecto inmediato | Alta | Baja |

---

## 4. ANÁLISIS DE ENFOQUES ARQUITECTÓNICOS

Esta sección documenta los enfoques (patrones y tácticas) usados en la arquitectura y los atributos de calidad que satisfacen.

### 4.1 Catálogo de Enfoques Arquitectónicos

| ID | Enfoque / Patrón | Dónde se aplica | Atributos que mejora | Atributos que puede deteriorar |
|----|-----------------|-----------------|---------------------|-------------------------------|
| **AP-01** | Service-Based Architecture (5 servicios) | Toda la plataforma | Escalabilidad, Mantenibilidad, Disponibilidad | Complejidad operacional |
| **AP-02** | API Gateway con JWT Validation | Punto de entrada único | Seguridad, Interoperabilidad | Latencia (+10ms por validación) |
| **AP-03** | RBAC en cada servicio | User, Course, Assessment, Collaboration, Analytics | Seguridad | Rendimiento (verificación en cada req.) |
| **AP-04** | Caché Redis (sesiones, contenido, reglas) | User Service, Course Service, Assessment Service | Rendimiento, Escalabilidad | Consistencia eventual, Costo |
| **AP-05** | Mensajería Asíncrona con RabbitMQ | Assessment → Analytics, Collaboration | Disponibilidad, Escalabilidad, Mantenibilidad | Consistencia eventual del dashboard |
| **AP-06** | Rules Engine con reglas en PostgreSQL | Assessment & Adaptive Service | Mantenibilidad, Motor Adaptativo | Rendimiento (consulta BD por evaluación) |
| **AP-07** | RDS Multi-AZ PostgreSQL | Capa de persistencia | Disponibilidad | Costo (+50% vs. Single-AZ) |
| **AP-08** | Schemas separados por servicio | PostgreSQL compartido | Mantenibilidad (aislamiento lógico) | Acoplamiento residual (BD compartida) |
| **AP-09** | Auto-scaling ECS Fargate | Todos los servicios | Escalabilidad | Tiempo de warm-up (≤ 5 min) |
| **AP-10** | Blue-Green Deployment | CI/CD GitHub Actions | Mantenibilidad (zero-downtime) | Costo (doble infraestructura temporal) |
| **AP-11** | bcrypt costo=12 + worker_threads | User Service (autenticación) | Seguridad | Rendimiento (latencia de login) |
| **AP-12** | Connection Pooling (PgBouncer) | ECS → RDS | Escalabilidad, Rendimiento | Complejidad de configuración |
| **AP-13** | Read Replica para Analytics | Analytics Service | Rendimiento (separa carga) | Consistencia eventual Analytics |
| **AP-14** | Circuit Breaker en API Gateway | API Gateway | Disponibilidad | Complejidad de configuración |
| **AP-15** | Health Checks (/live, /ready) | Todos los servicios ECS | Disponibilidad | Overhead mínimo por polling |
| **AP-16** | Distributed Tracing (X-Ray + Correlation ID) | Toda la plataforma | Mantenibilidad (observabilidad) | Overhead ~1-3% de latencia |

---

## 5. ESCENARIOS PRIORIZADOS

Los escenarios del ATAM describen situaciones concretas donde la arquitectura debe demostrar que satisface un atributo de calidad. Se priorizan por importancia y se vinculan a los enfoques arquitectónicos que los satisfacen.

### 5.1 Escenarios de Alta Prioridad

---

#### ESCENARIO-01: Pico de Evaluaciones al Final del Semestre
**Atributo:** Escalabilidad (DR-02) + Rendimiento (DR-01)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | 500 estudiantes envían sus evaluaciones simultáneamente (fin de semestre) |
| **Estímulo** | 500 peticiones POST /assessments/{id}/submit en un período de 5 minutos |
| **Artefacto** | Assessment & Adaptive Service + RabbitMQ + RDS PostgreSQL |
| **Entorno** | Sistema en producción, hora punta de evaluaciones |
| **Respuesta esperada** | El sistema califica y responde a cada estudiante con resultado + siguiente paso |
| **Medida de respuesta** | Latencia P95 ≤ 2 seg; 0% de evaluaciones perdidas; colas RabbitMQ procesadas en < 60 seg |

**Análisis:**

| Enfoques que satisfacen este escenario | Cómo |
|----------------------------------------|------|
| AP-09 (Auto-scaling ECS) | Assessment Service escala de 3 a 8 instancias en < 5 min |
| AP-05 (RabbitMQ Async) | Los eventos post-evaluación no bloquean la respuesta al estudiante |
| AP-04 (Redis caché reglas) | Las `ReglaAdaptacion` se leen de caché (< 5ms) sin golpear PostgreSQL |
| AP-12 (PgBouncer) | Gestiona 500 conexiones concurrentes sin saturar RDS |
| AP-06 (Rules Engine en BD) | Las reglas se evalúan síncronamente pero son ligeras (≤ 20ms) |

**Resultado:** La arquitectura satisface este escenario. El camino crítico síncrono (~120ms) cumple el SLA de 2 seg.

---

#### ESCENARIO-02: Falla del Analytics Service Durante Evaluaciones
**Atributo:** Disponibilidad (DR-03)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | Falla de infraestructura en el contenedor del Analytics Service |
| **Estímulo** | Analytics Service cae; sus colas RabbitMQ dejan de procesarse |
| **Artefacto** | Analytics Service + RabbitMQ (cola `evaluation.completed`) |
| **Entorno** | Sistema en producción durante período de evaluaciones |
| **Respuesta esperada** | Los estudiantes pueden seguir completando evaluaciones y recibiendo calificaciones; el dashboard se actualiza cuando el servicio se recupera |
| **Medida de respuesta** | 0% de evaluaciones fallidas; colas procesadas sin pérdida de datos cuando el servicio se recupera; dashboard actualizado en < 5 min post-recovery |

**Análisis:**

| Enfoques que satisfacen este escenario | Cómo |
|----------------------------------------|------|
| AP-01 (Service-Based) | Aislamiento de fallos: la caída de Analytics no afecta el Assessment Service |
| AP-05 (RabbitMQ Async) | Los mensajes se persisten en la cola; no se pierden aunque no haya consumidor activo |
| AP-15 (Health Checks) | ECS detecta la falla y reinicia el contenedor automáticamente (ECS task restart) |
| AP-14 (Circuit Breaker) | El API Gateway no enruta peticiones a Analytics hasta que pase el health check |

**Resultado:** La arquitectura satisface este escenario gracias al desacoplamiento asíncrono.

---

#### ESCENARIO-03: Docente Modifica Reglas de Adaptación en Producción
**Atributo:** Mantenibilidad (DR-05) + Motor Adaptativo (DR-07)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | Docente detecta que el umbral actual (70%) es muy bajo y lo modifica a 85% |
| **Estímulo** | PUT /api/v1/courses/{id}/lessons/{id}/adaptive-rules con body {threshold: 85} |
| **Artefacto** | Course Service + PostgreSQL (schema_courses.adaptation_rules) + Redis (caché de reglas) |
| **Entorno** | Sistema en producción; otros estudiantes están tomando evaluaciones del mismo curso |
| **Respuesta esperada** | La nueva regla se persiste en < 1 seg; las próximas evaluaciones usan el nuevo umbral; no se requiere redespliegue |
| **Medida de respuesta** | Latencia de actualización < 1 seg; próxima evaluación usa umbral=85%; sistema no se reinicia |

**Análisis:**

| Enfoques que satisfacen este escenario | Cómo |
|----------------------------------------|------|
| AP-06 (Rules Engine en BD) | Las `ReglaAdaptacion` están en PostgreSQL, no en el código |
| AP-04 (Redis caché reglas TTL=1min) | El caché de reglas expira en 1 minuto; la próxima evaluación carga la regla actualizada |
| AP-03 (RBAC) | Solo usuarios con rol INSTRUCTOR o ADMIN pueden modificar reglas |

**Resultado:** Satisfecho. Latencia de efecto de las nuevas reglas: máximo 1 minuto (TTL Redis).

---

#### ESCENARIO-04: Estudiante Accede a Funciones de Docente
**Atributo:** Seguridad (DR-04)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | Estudiante malicioso intenta crear un curso o modificar calificaciones |
| **Estímulo** | POST /api/v1/courses con JWT válido de rol STUDENT |
| **Artefacto** | API Gateway (JWT Validator + RBAC Enforcer) + Course Service |
| **Entorno** | Sistema en producción, usuario autenticado con rol STUDENT |
| **Respuesta esperada** | El sistema rechaza la petición con HTTP 403 Forbidden; el intento se registra en logs |
| **Medida de respuesta** | 100% de intentos de acceso no autorizado rechazados; log generado con userId, endpoint, timestamp |

**Análisis:**

| Enfoques que satisfacen este escenario | Cómo |
|----------------------------------------|------|
| AP-02 (API Gateway JWT) | Valida la firma del JWT y extrae el rol antes de enrutar |
| AP-03 (RBAC en cada servicio) | Course Service verifica `req.user.role === 'INSTRUCTOR'` antes de ejecutar |
| AP-11 (JWT con claims de rol) | El JWT firmado con RS256 incluye el campo `role`; no puede modificarse sin invalidar la firma |

**Resultado:** Satisfecho. El RBAC en dos niveles (Gateway + Servicio) proporciona defensa en profundidad.

---

#### ESCENARIO-05: Login de 500 Usuarios Simultáneos al Inicio de Clases
**Atributo:** Rendimiento (DR-01) + Escalabilidad (DR-02)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | Inicio del período académico: 500 estudiantes hacen login simultáneamente |
| **Estímulo** | 500 peticiones POST /auth/login en 2 minutos |
| **Artefacto** | User Service (bcrypt + worker_threads) + Redis (caché sesiones) |
| **Entorno** | Sistema en producción, inicio de semestre |
| **Respuesta esperada** | Todos los logins se procesan; latencia P95 ≤ 500ms |
| **Medida de respuesta** | P95 latencia login ≤ 500ms; 0% timeouts; User Service escala horizontalmente |

**Análisis:**

| Enfoques que satisfacen este escenario | Cómo |
|----------------------------------------|------|
| AP-11 (bcrypt + worker_threads) | bcrypt se ejecuta en worker threads, no bloqueando el event loop de Node.js |
| AP-09 (Auto-scaling ECS) | User Service escala a 6 instancias bajo carga |
| AP-04 (Redis sesiones) | Las sesiones activas se almacenan en Redis; lecturas de token posteriores no golpean PostgreSQL |

**Advertencia:** Este escenario es un **punto de sensibilidad**. bcrypt costo=12 tarda ~100-300ms por hash. Con 500 logins simultáneos y solo 2 instancias del User Service, la CPU puede saturarse antes de que el auto-scaling complete el escalado (≤ 5 min). Ver análisis de riesgo en Sección 6.

**Resultado:** Parcialmente satisfecho. Requiere mitigación activa (ver Sección 7, SP-01).

---

#### ESCENARIO-06: Inyección SQL en el Formulario de Evaluación
**Atributo:** Seguridad (DR-04)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | Atacante envía payload con inyección SQL como respuesta de evaluación |
| **Estímulo** | POST /assessments/{id}/submit con body `{"answers": [{"questionId": "1' OR '1'='1"}]}` |
| **Artefacto** | Assessment Service + ORM (Prisma) + PostgreSQL |
| **Entorno** | Sistema en producción |
| **Respuesta esperada** | El sistema rechaza el payload o lo procesa como texto sin ejecutar SQL; datos de BD protegidos |
| **Medida de respuesta** | 0% de inyecciones exitosas; input sanitizado antes de consultar BD |

**Análisis:**

| Enfoques que satisfacen este escenario | Cómo |
|----------------------------------------|------|
| API Gateway (WAF) | AWS WAF con reglas OWASP Top 10 detecta patrones de SQL injection |
| Prisma ORM (prepared statements) | El ORM usa parameterized queries automáticamente; nunca concatena strings en SQL |
| Input validation (Zod/Joi) | Schema validation en cada servicio rechaza UUIDs malformados antes de llegar al ORM |

**Resultado:** Satisfecho mediante defensa en capas (WAF → Validation → ORM).

---

### 5.2 Escenarios de Media Prioridad

#### ESCENARIO-07: Integración con LMS Externo
**Atributo:** Interoperabilidad (DR-06)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | Sistema LMS institucional solicita datos de progreso de un estudiante |
| **Estímulo** | GET /api/v1/analytics/students/{id}/progress con API key válida |
| **Artefacto** | API Gateway + Analytics Service |
| **Respuesta esperada** | El sistema responde con JSON estructurado según OpenAPI 3.0; documentación disponible en /api/docs |
| **Medida de respuesta** | Respuesta JSON válida; latencia ≤ 2 seg; documentación accesible |

**Resultado:** Satisfecho. APIs REST + OpenAPI 3.0 desde el MVP.  
**Nota:** xAPI no está implementado en el MVP (deuda técnica aceptada). Si el LMS requiere xAPI, se necesita la implementación de V2.

---

#### ESCENARIO-08: Actualización del Sistema sin Interrupción de Servicio
**Atributo:** Mantenibilidad (DR-05)

| Elemento | Descripción |
|----------|-------------|
| **Fuente del estímulo** | El equipo despliega una corrección de bug al Assessment Service |
| **Estímulo** | git push a main → GitHub Actions ejecuta el pipeline CI/CD |
| **Artefacto** | GitHub Actions + ECS Blue-Green Deployment |
| **Respuesta esperada** | El nuevo código está en producción en < 30 min; ningún estudiante experimenta error durante el despliegue |
| **Medida de respuesta** | Downtime = 0; tiempo total del pipeline < 30 min; rollback en < 2 min si falla |

**Resultado:** Satisfecho mediante Blue-Green Deployment.

---

## 6. ANÁLISIS DE SENSIBILIDAD, TRADE-OFFS Y RIESGOS

### 6.1 Metodología

Para cada par de decisiones arquitecturales que afectan atributos de calidad en tensión, se documenta:
- **Punto de sensibilidad (SP):** Un parámetro cuyo cambio afecta significativamente un atributo de calidad.
- **Trade-off (TO):** Una decisión que mejora un atributo a costa de otro.
- **Riesgo (R):** Una decisión cuya respuesta a un escenario es incierta o potencialmente inadecuada.

---

## 7. PUNTOS DE SENSIBILIDAD

Los Puntos de Sensibilidad son parámetros individuales de la arquitectura donde pequeños cambios tienen impacto desproporcionado en uno o más atributos de calidad.

### SP-01: Factor de Costo de bcrypt (User Service)

**Parámetro sensible:** `BCRYPT_COST_FACTOR` (actualmente = 12)

| Si el factor sube (13, 14...) | Si el factor baja (10, 11) |
|-------------------------------|---------------------------|
| Seguridad aumenta (más difícil de romper por fuerza bruta) | Seguridad disminuye |
| Latencia de login aumenta (~200ms por unidad adicional de costo) | Latencia de login disminuye |
| CPU del User Service se satura más rápido bajo carga | Escalabilidad del login mejora |

**Impacto:** El factor de costo actual (12) produce ~100-300ms por hash. Bajo 500 logins simultáneos, el User Service puede saturar la CPU antes de que el auto-scaling active más instancias. 

**Recomendación:** Usar worker_threads de Node.js para ejecutar bcrypt fuera del event loop principal. Monitorear CPU del User Service durante períodos de inicio de semestre. Considerar factor 11 si las métricas muestran saturación, documentando el trade-off de seguridad.

---

### SP-02: TTL del Caché de ReglaAdaptacion en Redis

**Parámetro sensible:** `REDIS_RULES_TTL` (actualmente = 60 segundos)

| Si el TTL sube (5 min, 10 min) | Si el TTL baja (10 seg, 30 seg) |
|--------------------------------|--------------------------------|
| Menos consultas a PostgreSQL → menor latencia | Más consultas a PostgreSQL → mayor latencia |
| Menor carga en Assessment Service bajo pico | Mayor carga en RDS durante picos |
| Una regla modificada por el docente tarda más en tener efecto | Las reglas modificadas tienen efecto casi inmediato |

**Impacto:** Si un docente modifica el umbral de adaptación con TTL=60s, los estudiantes que evalúen dentro de ese minuto usarán la regla antigua. Con TTL=5min, el efecto tarda más. Con TTL=10s, el sistema golpea la BD en cada evaluación bajo pico.

**Recomendación:** Implementar invalidación proactiva del caché cuando el docente modifica una regla (publicar evento `rule.updated` → Assessment Service invalida `rules:{lessonId}` en Redis). Esto elimina la dependencia del TTL para la correctitud del motor adaptativo.

---

### SP-03: Tamaño del Pool de Conexiones de PgBouncer

**Parámetro sensible:** `MAX_CONNECTIONS_PER_SERVICE` (actualmente = 20-30 por schema)

| Si el pool aumenta | Si el pool disminuye |
|-------------------|---------------------|
| Mayor throughput bajo carga | Menor throughput |
| RDS puede alcanzar max_connections (RDS t3.large ≈ 80 max connections) | Menos presión sobre RDS |
| Mayor consumo de RAM en RDS | Menor consumo de RAM |

**Impacto:** RDS t3.large tiene un límite de ~80 conexiones máximas de PostgreSQL. Con 5 servicios × 20 conexiones por pool = 100 conexiones teóricas, se puede alcanzar el límite bajo carga máxima.

**Recomendación:** Configurar `max_connections` de PgBouncer para que el total nunca supere 75 conexiones activas hacia RDS. Bajo pico, los servicios hacen cola en PgBouncer en lugar de sobrecargar RDS. Monitorear `pgbouncer_pool_size` en CloudWatch.

---

### SP-04: Número Mínimo de Instancias del Assessment Service

**Parámetro sensible:** `ECS_ASSESSMENT_MIN_TASKS` (actualmente = 3)

| Si el mínimo aumenta (5, 8) | Si el mínimo disminuye (1, 2) |
|-----------------------------|-------------------------------|
| Menor tiempo de respuesta bajo pico (ya hay capacidad disponible) | Mayor tiempo de warm-up ante pico súbito |
| Auto-scaling más rápido (ya hay más instancias base) | Menor costo en horas de baja concurrencia |
| Mayor costo mensual en ECS Fargate | Riesgo de saturación antes de que escale |

**Impacto:** Con mínimo=3 instancias, el sistema puede absorber ~150-200 evaluaciones simultáneas antes de necesitar escalar. Un pico repentino de 500 evaluaciones puede saturar las 3 instancias durante los 5 minutos que tarda el auto-scaling.

**Recomendación:** Programar el pre-escalado del Assessment Service antes de exámenes conocidos (usando AWS EventBridge Scheduler para aumentar `desiredCount` a 7 instancias 30 minutos antes del examen programado).

---

## 8. TRADE-OFFS IDENTIFICADOS

Los Trade-offs son decisiones donde la arquitectura mejora un atributo de calidad a expensas de otro. A diferencia de los puntos de sensibilidad (que afectan un solo atributo), los trade-offs afectan múltiples atributos en direcciones opuestas.

### TO-01: Comunicación Asíncrona (RabbitMQ) vs. Consistencia del Dashboard

**Decisión arquitectural:** ADR-003 — Motor Adaptativo + RabbitMQ

| Atributo mejorado | Atributo deteriorado |
|-------------------|---------------------|
| **Rendimiento** (DR-01): El estudiante recibe su calificación en < 2 seg | **Consistencia**: El dashboard puede tardar 5-30 seg en reflejar el resultado |
| **Disponibilidad** (DR-03): La caída del Analytics Service no bloquea evaluaciones | **Consistencia**: Si RabbitMQ está saturado, el retraso puede ser mayor |
| **Escalabilidad** (DR-02): RabbitMQ absorbe picos sin saturar los consumidores | **Observabilidad**: El flujo distribuido es más complejo de depurar |

**Análisis:** Este es el trade-off más importante de la arquitectura. La decisión es correcta para el contexto: el estudiante ya tiene su calificación y el siguiente paso del recorrido (lo más importante); el dashboard es información secundaria que puede tener un retraso breve. La mitigación (indicador visual "actualizando...") gestiona la expectativa del usuario.

**Veredicto:** Trade-off aceptable. El SRS no especifica consistencia en tiempo real del dashboard; solo especifica que las evaluaciones deben responder en ≤ 2 seg.

---

### TO-02: Base de Datos Compartida vs. Independencia de Servicios

**Decisión arquitectural:** ADR-002 — PostgreSQL con schemas separados (deuda técnica aceptada)

| Atributo mejorado | Atributo deteriorado |
|-------------------|---------------------|
| **Costo**: Una instancia RDS vs. cinco instancias separadas (~$750/mes vs. ~$150/mes) | **Mantenibilidad**: Migraciones de un schema pueden afectar la BD compartida |
| **Simplicidad operacional**: Un solo clúster RDS para gestionar | **Escalabilidad**: No se puede escalar el almacenamiento de Analytics independientemente |
| **Rendimiento**: Transacciones dentro del mismo servidor de BD son más eficientes | **Aislamiento**: Un proceso mal escrito puede agotar conexiones de RDS para todos los servicios |

**Análisis:** Para el MVP con un equipo de 3-4 personas y presupuesto limitado, la BD compartida es un trade-off razonable. La mitigación mediante schemas separados y la política "no cross-schema queries" reduce el riesgo de acoplamiento. La deuda técnica está documentada y tiene un plan de resolución (separar Analytics a Redshift en V2).

**Veredicto:** Trade-off aceptable para el MVP, con plan de resolución documentado.

---

### TO-03: JWT de 8 Horas vs. Seguridad ante Token Comprometido

**Decisión arquitectural:** Sección 6 del SAD — JWT con expiración de 8 horas

| Atributo mejorado | Atributo deteriorado |
|-------------------|---------------------|
| **Usabilidad**: El estudiante no necesita re-autenticarse durante una sesión de trabajo normal (≤ 8h) | **Seguridad**: Un token comprometido es válido hasta 8 horas |
| **Rendimiento**: Menos peticiones de refresco de token → menor carga en User Service | **Seguridad**: No hay mecanismo nativo de revocación inmediata de JWT (solo expiración) |

**Análisis:** La sesión activa en Redis (`session:{userId}`) actúa como registro de tokens válidos. Al hacer logout, el servicio elimina la entrada de Redis. El API Gateway puede consultar Redis para validar si la sesión sigue activa, proporcionando revocación efectiva aunque el JWT no haya expirado.

**Mejora propuesta (TO-03-MEJORA):** Implementar Access Token de 15 minutos + Refresh Token de 7 días (rotatable, almacenado en Redis). Reduce la ventana de exposición de 8 horas a 15 minutos sin impacto en la experiencia de usuario.

**Veredicto:** Trade-off funcional pero mejorable. La mejora de Access + Refresh Token se recomienda antes del lanzamiento a producción real.

---

### TO-04: Service-Based Architecture vs. Overhead de Comunicación

**Decisión arquitectural:** ADR-001 — Service-Based Architecture

| Atributo mejorado | Atributo deteriorado |
|-------------------|---------------------|
| **Escalabilidad** (DR-02): Escalado independiente por servicio | **Rendimiento**: Llamadas entre servicios agregan latencia de red (~5-15ms por llamada) |
| **Disponibilidad** (DR-03): Aislamiento de fallos por dominio | **Complejidad**: La depuración requiere correlación de logs entre servicios |
| **Mantenibilidad** (DR-05): Cada servicio se despliega independientemente | **Complejidad**: La configuración inicial (API Gateway, VPC, ECS) requiere más tiempo |

**Análisis:** La latencia de red interna en la VPC es típicamente < 1ms entre contenedores ECS. Las llamadas síncronas directas entre servicios (ej. verificar inscripción al entregar evaluación) agregan ~ 5-15ms, que es aceptable dentro del presupuesto de 2 segundos. La complejidad adicional está mitigada por Terraform IaC y las librerías compartidas en `libs/`.

**Veredicto:** Trade-off aceptable. El overhead de comunicación es manejable y los beneficios de escalabilidad y disponibilidad superan el costo.

---

### TO-05: Reglas Adaptativas Configurables vs. Consistencia Inmediata

**Decisión arquitectural:** ADR-003 — Motor Adaptativo con reglas en BD + caché Redis TTL=1min

| Atributo mejorado | Atributo deteriorado |
|-------------------|---------------------|
| **Mantenibilidad** (DR-05): Reglas cambian sin redespliegue | **Consistencia**: Hasta 1 minuto de lag en la aplicación de nuevas reglas |
| **Motor Adaptativo** (DR-07): Docentes configuran umbral, refuerzo y avance | **Rendimiento**: Consulta a Redis (o BD si miss) en cada evaluación |
| **Flexibilidad**: Diferentes reglas por curso, módulo y lección | **Complejidad**: La lógica de invalidación de caché debe ser correcta |

**Veredicto:** Trade-off aceptable. El lag de 1 minuto es irrelevante en el contexto educativo. La invalidación proactiva del caché (SP-02) elimina el problema.

---

## 9. RIESGOS NO RESUELTOS

Los Riesgos son decisiones arquitecturales cuya respuesta a algún escenario de calidad es incierta o potencialmente inadecuada. A diferencia de los trade-offs, los riesgos representan vulnerabilidades que podrían ser problemas reales.

### R-01: Saturación del User Service Bajo Login Masivo (ACTIVO)

**Descripción:** Durante el inicio del semestre, 500+ estudiantes pueden intentar hacer login simultáneamente. bcrypt costo=12 tarda ~100-300ms por hash y es intensivo en CPU. Con solo 2-3 instancias iniciales del User Service, el event loop de Node.js puede saturarse antes de que el auto-scaling complete el escalado.

**Probabilidad:** Alta (ocurre al inicio de cada semestre)  
**Impacto:** Medio (latencia de login > 2 seg; algunos logins pueden timeout)  
**Atributo afectado:** Rendimiento (DR-01), Disponibilidad (DR-03)

**Mitigación:**
1. Usar `worker_threads` de Node.js para ejecutar bcrypt fuera del event loop principal (implementar en el MVP).
2. Aumentar el mínimo de instancias del User Service a 3 durante las primeras semanas del semestre.
3. Documentar que el SLA de ≤ 2 seg aplica a "operaciones principales" y aclarar si login está incluido o excluido.

**Estado:** Riesgo activo — requiere implementación de worker_threads antes del despliegue.

---

### R-02: Lock Contention en PostgreSQL Compartido (ACTIVO)

**Descripción:** Durante un pico de evaluaciones, el Assessment Service puede generar cientos de INSERT en `schema_assessments.evaluation_attempts` simultáneamente. Si PgBouncer está mal configurado o si hay una transacción larga bloqueando una tabla, otros servicios que comparten la misma instancia RDS pueden verse afectados.

**Probabilidad:** Media  
**Impacto:** Medio-Alto (degradación de rendimiento en todos los servicios durante el bloqueo)  
**Atributo afectado:** Rendimiento (DR-01), Disponibilidad (DR-03)

**Mitigación:**
1. Configurar `lock_timeout = 5s` y `statement_timeout = 10s` en PostgreSQL por schema para evitar transacciones largas bloqueantes.
2. Monitorear `pg_stat_activity` en CloudWatch para detectar locks activos.
3. Política de "no cross-schema queries" verificada mediante análisis estático del ORM (Prisma lint).

**Estado:** Riesgo activo — requiere configuración de timeouts y monitoreo antes del despliegue.

---

### R-03: Acumulación de Mensajes en RabbitMQ ante Caída Sostenida de Analytics (ACTIVO)

**Descripción:** Si el Analytics Service cae durante 30+ minutos durante un período de evaluaciones masivas, la cola `evaluation.completed` puede acumular decenas de miles de mensajes. Cuando el servicio se recupere, el procesamiento masivo de mensajes acumulados puede saturar el Analytics Service y el Read Replica de PostgreSQL.

**Probabilidad:** Media  
**Impacto:** Medio  
**Atributo afectado:** Disponibilidad (DR-03), Rendimiento (DR-01 para Analytics)

**Mitigación:**
1. Configurar dead letter queue con TTL = 24h para mensajes que fallen 3 veces.
2. Implementar rate limiting en el consumidor de RabbitMQ del Analytics Service (max 50 mensajes/seg durante recovery).
3. Configurar alerta CloudWatch cuando `queue_depth > 5,000 mensajes`.

**Estado:**  Riesgo activo — configuración de DLQ y alertas requeridas antes del despliegue.

---

### R-04: Ausencia de xAPI en MVP (POSPUESTO)

**Descripción:** El SRS RNF-06 establece que el sistema debe soportar xAPI (Tin Can API) para registro de actividades de aprendizaje. El SAD pospone esta implementación a V2 sin una fecha comprometida.

**Probabilidad de impacto:** Baja en el MVP; Media si la PUJ requiere integración con LRS externo antes de V2  
**Impacto:** Medio (incumplimiento de RNF-06)  
**Atributo afectado:** Interoperabilidad (DR-06)

**Mitigación:**
1. Diseñar los eventos de RabbitMQ con campos compatibles con xAPI desde el MVP (actor, verb, object, result).
2. Documentar el plan de V2 con criterio de aceptación: "implementar xAPI cuando el sistema tenga > 1,000 sesiones activas" o antes de la siguiente iteración.

**Estado:** Riesgo de incumplimiento de RNF-06. Requiere diseño compatible desde el MVP.

---

### R-05: Redis como Punto Único de Falla para Sesiones (MITIGADO PARCIALMENTE)

**Descripción:** Si ElastiCache Redis falla, todas las validaciones de sesión activa fallan. Aunque el JWT sigue siendo criptográficamente válido, el API Gateway no puede verificar si la sesión fue revocada en Redis.

**Probabilidad:** Baja (ElastiCache con replicación)  
**Impacto:** Alto (los usuarios no pueden iniciar sesión; las sesiones activas no se pueden verificar)  
**Atributo afectado:** Disponibilidad (DR-03), Seguridad (DR-04)

**Mitigación:**
1. ElastiCache Redis con replicación Multi-AZ (ya implementado).
2. Implementar fallback: si Redis no responde, validar solo la firma JWT (sin verificar revocación en Redis). Documentar este modo degradado.
3. Monitorear disponibilidad de Redis con CloudWatch y alerta inmediata ante caída.

**Estado:** Riesgo mitigado mediante Multi-AZ. Fallback documentado como modo degradado aceptable.

---

## 10. RESULTADOS Y CONCLUSIONES DEL ATAM

### 10.1 Resumen de Hallazgos

| Categoría | Cantidad | Detalles |
|-----------|----------|----------|
| **Escenarios analizados** | 8 | 5 alta prioridad, 2 media prioridad + 1 seguridad |
| **Escenarios satisfechos** | 7 | ESCENARIO-01 a 04, 06, 07, 08 |
| **Escenarios parcialmente satisfechos** | 1 | ESCENARIO-05 (login masivo — SP-01) |
| **Puntos de sensibilidad** | 4 | SP-01 (bcrypt), SP-02 (TTL Redis), SP-03 (PgBouncer), SP-04 (min instancias) |
| **Trade-offs documentados** | 5 | TO-01 a TO-05 |
| **Riesgos activos** | 4 | R-01, R-02, R-03, R-04 |
| **Riesgos mitigados** | 1 | R-05 |

### 10.2 Evaluación Global de la Arquitectura

La arquitectura de la Plataforma de Aprendizaje Adaptativo y Colaborativo es **sólida en su conjunto** y satisface los drivers arquitecturales más críticos. Las decisiones principales (Service-Based Architecture, PostgreSQL + Redis, Motor Adaptativo con RabbitMQ) están bien justificadas y sus trade-offs son conocidos, documentados y manejables.

**Fortalezas arquitecturales:**
1. El aislamiento de fallos mediante Service-Based Architecture garantiza alta disponibilidad (DR-03).
2. La separación del camino crítico síncrono y las operaciones asíncronas (RabbitMQ) es la táctica más efectiva para cumplir rendimiento + disponibilidad simultáneamente (DR-01, DR-03).
3. Las reglas de adaptación configurables en la base de datos son la implementación correcta del motor adaptativo (DR-07), evitando el redepliegue ante cambios de configuración.
4. La seguridad en capas (WAF → API Gateway JWT → RBAC en servicio → ORM prepared statements) proporciona defensa en profundidad para DR-04.

**Áreas de mejora prioritarias (ordenadas por urgencia):**

| Prioridad | Mejora | Justificación |
|-----------|--------|---------------|
| Alta | SP-01: Implementar worker_threads para bcrypt | Riesgo R-01 activo; puede causar timeouts de login en producción |
| Alta | R-02: Configurar lock_timeout en PostgreSQL | Riesgo activo de degradación en todos los servicios |
| Media | TO-03: Migrar a Access Token (15min) + Refresh Token | Mejora de seguridad significativa con bajo costo de implementación |
| Media | SP-02: Invalidación proactiva del caché de reglas | Garantiza correctitud del motor adaptativo ante cambios del docente |
| Media | R-04: Diseñar eventos RabbitMQ compatibles con xAPI | Habilita RNF-06 en V2 sin rediseño de eventos |
| Baja | SP-04: Pre-escalado programado del Assessment Service | Mejora la experiencia en períodos de evaluación conocidos |

### 10.3 Veredicto del ATAM

**La arquitectura es APROBADA para desarrollo del MVP**, con las condiciones de que los riesgos R-01 y R-02 sean mitigados antes del primer despliegue a producción con usuarios reales. Los trade-offs identificados (TO-01 a TO-05) son aceptables para el contexto del MVP y están documentados con planes de resolución.

La arquitectura demuestra madurez en las siguientes áreas:
- Separación correcta entre operaciones síncronas críticas y asíncronas secundarias.
- Uso apropiado de caché para reducir la carga en la base de datos bajo alta concurrencia.
- Diseño de seguridad en capas que cumple con los requisitos regulatorios colombianos.
- Estrategia de despliegue sin tiempo de inactividad compatible con un entorno educativo de alta disponibilidad.

### 10.4 Tabla de Trazabilidad Final

| Driver | Escenario ATAM | Enfoques que lo satisfacen | Veredicto |
|--------|----------------|---------------------------|-----------|
| DR-01 Performance | ESC-01, ESC-03, ESC-05 | AP-04, AP-05, AP-06, AP-13 | Satisfecho (con SP-01 mitigado) |
| DR-02 Escalabilidad | ESC-01 | AP-01, AP-09, AP-05, AP-12 | Satisfecho |
| DR-03 Disponibilidad | ESC-02, ESC-03 | AP-01, AP-05, AP-07, AP-14, AP-15 | Satisfecho |
| DR-04 Seguridad | ESC-04, ESC-06 | AP-02, AP-03, AP-11, WAF | Satisfecho |
| DR-05 Mantenibilidad | ESC-03, ESC-08 | AP-01, AP-06, AP-10, AP-16 | Satisfecho |
| DR-06 Interoperabilidad | ESC-07 | AP-02 (OpenAPI 3.0) | Parcial (xAPI pospuesto) |
| DR-07 Motor Adaptativo | ESC-03, ESC-01 | AP-06, AP-04 | Satisfecho |

---


---

**Fin del Documento ATAM**

---


