# Software Architecture Document

## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Version:** 3.0
**Fecha:** 26/04/2026
**Basado en:** SRS v2.0

---

## Document Approver(s):

| Approver Name | Role |
|---------------|------|
| TBD | Director del Proyecto |

## Document Reviewers:

| Reviewer Name | Role |
|---------------|------|
| TBD | Lider Tecnico |
| TBD | Docente del Curso |

## Summary of Changes:

| Version | Date | Created by | Short Description of Changes |
|---------|------|------------|------------------------------|
| 1.0 | 24/03/2026 | Equipo | Borrador inicial |
| 2.0 | 24/03/2026 | Equipo | Vistas 4+1 y escenarios |
| 3.0 | 26/04/2026 | Equipo | Seccion 3 nueva: justificacion de seleccion de estilo con tabla de decision. Trade-offs explicitos por patron. Vistas corregidas segun Kruchten: logica=componentes/subsistemas, procesos=procesos computacionales, desarrollo=paquetes/modulos/capas, fisica=despliegue. Escenarios movidos al SRS. Rol Directivo incorporado |

---

## Table of Contents

1. Introduction
2. Architectural Representation
3. Architectural Goals and Constraints
4. Security
5. Use-Case View
6. Logical View
7. Deployment View
8. Implementation View
9. Data View
10. Size and Performance
11. Quality
12. Selection of Architectural Style
13. Architectural Patterns and Tactics
14. Contact Information

---

# 1. INTRODUCTION

## 1.1 Purpose

Este documento describe la arquitectura de la Plataforma de Aprendizaje Adaptativo y Colaborativo desde varias vistas complementarias. Su objetivo es registrar las decisiones arquitectonicas significativas, incluida la justificacion del estilo seleccionado y de cada patron aplicado.

## 1.2 Scope

La arquitectura descrita abarca la plataforma completa: el cliente web MPA (Jakarta Faces), los 5 servicios backend de grano grueso (User Service, Course Service, Assessment & Adaptive Service, Collaboration Service, Analytics Service), la base de datos compartida con schemas logicamente separados, la cache distribuida, el broker de mensajes asincrono y la infraestructura cloud. Este documento no explica los estandares educativos (xAPI), el modelo de cuatro roles ni otros conceptos de dominio ya descritos en el SRS.

## 1.3 References

| # | Document | Contents outline |
|---|----------|-----------------|
| REF-01 | SRS v2.0 — Plataforma Adaptativa | Requisitos funcionales, no funcionales, drivers y escenarios de calidad |
| REF-02 | ISO/IEC 25010:2011 | Modelo de calidad de software |
| REF-03 | Bass, Clements & Kazman (2012). Software Architecture in Practice, 3ra ed. | Tacticas y atributos de calidad, metodo ADD |
| REF-04 | Kruchten, P. (1995). Architectural Blueprints — The 4+1 View Model | Modelo de vistas 4+1 |
| REF-05 | Richards, M. & Ford, N. (2020). Fundamentals of Software Architecture | Estilos arquitectonicos, tablas de caracteristicas |
| REF-06 | Jakarta Security 3.0 + MicroProfile JWT 2.1 | Especificacion de autenticacion y autorizacion en Jakarta EE; base conceptual para RBAC y JWT |
| REF-07 | PostgreSQL 15 Documentation | Base de datos relacional, schemas, replicacion |

## 1.4 Document Content Overview

Tras presentar la representacion arquitectonica y las restricciones del sistema, este documento describe el modelo de seguridad y luego el sistema en varias vistas (Use Case, logica, despliegue, implementacion y datos). Las secciones de tamaño, rendimiento y calidad cierran el nucleo del documento. La seleccion del estilo arquitectonico con su tabla de decision, y los patrones y tacticas detallados, se presentan al final porque dependen de lo descrito en las vistas anteriores.

---

# 2. ARCHITECTURAL REPRESENTATION

Las secciones siguientes describen las vistas con que se representa la arquitectura:

- La **vista logica** muestra la estructura del sistema a traves de sus subsistemas, componentes e interacciones. Los diagramas trabajan al nivel de subsistemas y componentes, no de clases.
- La **vista de implementacion** describe las capas de software, paquetes, modulos y componentes principales. Usa diagramas de componentes y paquetes con la jerarquia: sistema > subsistema > componente > modulo > paquete.
- La **vista de despliegue** describe los nodos hardware, contenedores y como se conectan entre si, junto con los protocolos utilizados.
- La **vista de datos** muestra la persistencia de informacion mediante diagramas de entidad por dominio.
- La **vista de procesos** describe los procesos computacionales en ejecucion, sus mecanismos de comunicacion y el modelo de concurrencia. No es un diagrama de actividad; muestra los procesos reales (contenedores Docker, motores de base de datos, servidores).

Se usan diagramas UML para representar cada vista.

---

# 3. ARCHITECTURAL GOALS AND CONSTRAINTS

Los siguientes requisitos no funcionales condicionan la solucion arquitectonica. Se derivan del SRS v2.0, secciones 5 y 7.

| Non-functional requirement | Description |
|---------------------------|-------------|
| Rendimiento (DR-01) | Las operaciones principales responden en <= 2 seg P95. Login <= 500ms P95. Throughput >= 500 req/s. |
| Seguridad (DR-04) | TLS 1.2+, JWT RS256, RBAC en dos capas, bcrypt costo >= 12, Ley 1581/2012, log de auditoria. |
| Motor Adaptativo (DR-07) | Reglas de adaptacion configurables por docente sin redespliegue. Evaluacion en runtime. Efecto <= 60s. |
| Disponibilidad (DR-03) | 99.5% uptime mensual. RTO <= 1h. RPO <= 15 min. Failover BD <= 60s. Degradacion controlada ante falla de servicio no critico. |
| Escalabilidad (DR-02) | >= 5,000 concurrentes. Auto-scale <= 5 min. Horizontal scaling por servicio. |
| Mantenibilidad (DR-05) | Modulos independientes. CI/CD Blue-Green. Nuevo modulo <= 2 semanas. Max 3 dependencias entre servicios. |
| Interoperabilidad (DR-06) | APIs REST/JSON, OpenAPI 3.0, versionado /api/v1/, xAPI futuro. |

**Restricciones del proyecto:**

| Constraint | Impact on Architecture |
|------------|----------------------|
| Equipo de 3-4 personas | Descarta microservicios. Favorece Service-Based con monorepo |
| Presupuesto limitado | BD compartida con schemas logicos, no BD por servicio |
| Ley 1581/2012 | Datos en region America. Consentimiento. Auditoria |
| AWS como proveedor cloud | Diseño sobre ECS, RDS, ElastiCache, SNS/SQS, S3 |
| Tecnologias fijas | Jakarta Faces (MPA), Jakarta EE 10 / WildFly 31, PostgreSQL 15 |

---

# 4. SECURITY

## 4.1 Introduction

La plataforma implementa seguridad en profundidad (defense-in-depth) con controles en multiples capas, de acuerdo con el driver DR-04 y la Ley 1581/2012.

## 4.2 Client — Backend Services Communication

Toda comunicacion entre el cliente MPA (Jakarta Faces) y los servicios backend usa HTTPS con TLS 1.2 o superior. Cada servicio expone sus endpoints JAX-RS directamente, sin API Gateway intermedio. La validacion del JWT, el rate limiting por IP y la proteccion ante servicios degradados se implementan en un filtro JAX-RS compartido (`ContainerRequestFilter`) incluido en cada servicio via libreria comun.

## 4.3 Inter-Service Communication

La comunicacion entre servicios backend ocurre por HTTP dentro de una VPC privada, sin exposicion a internet. Cada servicio aplica su propio filtro RBAC como segunda capa de verificacion (defense-in-depth). Ningun servicio es directamente accesible desde internet; el ALB termina TLS y enruta al servicio correspondiente segun el path.

## 4.4 Authentication and Authorization

La autenticacion usa tokens JWT firmados con RS256. El payload contiene: userId, email, role y expiracion (max 8 horas). Las contraseñas se almacenan con bcrypt (factor de costo >= 12). Tras 5 intentos de login fallidos consecutivos, la cuenta queda bloqueada durante 15 minutos.

La autorizacion usa RBAC con cuatro roles: STUDENT, INSTRUCTOR, DIRECTOR, ADMIN. La verificacion ocurre en el `ContainerRequestFilter` de cada servicio, que aplica la politica RBAC antes de despachar la peticion al endpoint JAX-RS correspondiente.

## 4.5 Data Protection

En cumplimiento de la Ley 1581/2012: el consentimiento es explicito en el registro, la politica de privacidad es visible, los datos personales no se comparten con terceros sin consentimiento, y todos los datos residen en la region America de AWS.

## 4.6 Auditing

Cada login (exitoso y fallido), logout y operacion de escritura queda registrado con timestamp, userId e IP de origen en formato JSON estructurado. Los logs de auditoria se retienen minimo 1 año.

## 4.7 Security Disclaimer

Ademas de los controles que implementa la plataforma, la institucion que la despliegue debe tomar medidas adicionales segun las mejores practicas y regulaciones vigentes: firewalls, listas blancas de IP y cifrado de base de datos, entre otras. El equipo de desarrollo no asume responsabilidad por brechas de seguridad derivadas de que la institucion no adopte estas recomendaciones.

---

# 5. USE-CASE VIEW

## 5.1 Selection Rationale

Se seleccionaron los casos de uso relevantes para la arquitectura con base en dos criterios: que involucren intercambio entre cliente y servicios backend, y que representen partes criticas de la arquitectura donde es necesario atender los riesgos tecnicos (drivers) de forma temprana.

Los casos seleccionados del SRS v2.0, Seccion 3 son:

- Envio de evaluacion por el estudiante (UC-03): ejercita el flujo sincrono completo y el motor adaptativo.
- Evento de evaluacion completada consumido por Analytics y Collaboration: ejercita el procesamiento asincrono.
- Autenticacion (UC-01): ejercita la seguridad, generacion de JWT y RBAC.
- Configuracion de reglas de adaptacion (UC-06): ejercita la persistencia de reglas y la invalidacion de cache.
- Publicacion en foro con notificacion asincrona (UC-05): ejercita el patron Publish/Subscribe.
- Acceso del Directivo al tablero institucional (UC-10): ejercita la replica de lectura y la agregacion de datos.

[ver diagrama_casos_uso.svg]

Las especificaciones detalladas (precondiciones, postcondiciones, flujos y flujos alternativos) estan en el SRS v2.0, Seccion 3. Los diagramas de secuencia con la interaccion entre componentes para cada caso seleccionado se presentan en la Seccion 6 de este documento.

---

# 6. LOGICAL VIEW

## 6.1 Overview

Este capitulo describe los subsistemas principales de la plataforma, como interactuan y como implementan la especificacion del SRS.

## 6.2 Architecturally Significant Design Packages

[ver diagramaLogico.svg]

### 6.2.1 Web Client (MPA)

El cliente web es una aplicacion multi-pagina construida con Jakarta Faces (JSF) desplegada en WildFly. Cada vista es un archivo `.xhtml` renderizado en el servidor. Gestiona: renderizado de la interfaz, validacion de formularios via Bean Validation, estado de sesion en el servidor (CDI `@SessionScoped`), y persistencia local de respuestas de evaluacion en progreso via Jakarta WebStorage o cookie de sesion para recuperacion ante desconexion.

### 6.2.2 ALB (Application Load Balancer)

El ALB de AWS es el unico punto de entrada para el trafico externo. Termina TLS y enruta las peticiones al servicio backend correspondiente segun el path (/users/*, /courses/*, /assessments/*, /collaboration/*, /analytics/*). No contiene logica de negocio ni autenticacion; esas responsabilidades recaen en el filtro JAX-RS de cada servicio.

### 6.2.3 User Service

Responsable del registro de usuarios, autenticacion (generacion de JWT RS256), gestion de sesiones, RBAC y administracion de cuentas. Propietario del schema `users` en PostgreSQL.

### 6.2.4 Course Service

Responsable del CRUD de cursos, modulos, lecciones y materiales educativos. Tambien almacena las reglas de adaptacion (tabla `adaptation_rules`) configuradas por los docentes. Propietario del schema `courses`.

### 6.2.5 Assessment & Adaptive Service

Servicio central. Gestiona los quizzes, realiza la calificacion automatica y ejecuta el motor de reglas adaptativo (lee las reglas desde cache o BD, evalua la nota del estudiante contra el umbral y determina el siguiente contenido). Es el publicador principal de eventos asincronos (`evaluation.completed`). Propietario del schema `assessments`.

### 6.2.6 Collaboration Service

Responsable de foros, hilos, publicaciones, grupos de estudio, chat en tiempo real (WebSocket) y envio de notificaciones. Consume eventos del broker para identificar candidatos a tutor. Propietario del schema `collaboration`.

### 6.2.7 Analytics Service

Responsable del seguimiento del progreso, reportes por curso, alertas automaticas, exportacion CSV/PDF y el tablero institucional para Directivos. Consume eventos del broker para actualizar los registros de progreso de forma asincrona. Ejecuta consultas de lectura sobre una replica para no competir con las operaciones transaccionales. Propietario del schema `analytics`.

### 6.2.8 Relationships between subsystems

- El cliente MPA (Jakarta Faces) se comunica con los servicios backend via HTTP/JAX-RS directamente a traves del ALB.
- El ALB enruta hacia los 5 servicios backend segun el path de la URL.
- El Assessment Service lee las reglas de adaptacion del schema del Course Service via cache (Cache-Aside) o llamada REST; nunca por query cross-schema directa.
- El Assessment Service publica eventos al broker; Analytics y Collaboration los consumen de forma asincrona.
- Todos los servicios incluyen el modulo comun (`libs/security`) con el `ContainerRequestFilter` de Jakarta EE para verificacion del JWT y RBAC.

---

## 6.3 Sequence Diagrams for Selected Scenarios

### Sequence Diagram 1: User Authentication (UC-01, DR-04, DR-01)

El usuario ingresa sus credenciales en la vista Jakarta Faces. La peticion llega al User Service via ALB, que valida con bcrypt, genera el JWT RS256 y registra la sesion en cache.

[ver DiagramaCaso1.svg]

### Sequence Diagram 2: Evaluation Submission with Adaptive Engine (UC-03, DR-01, DR-07)

El estudiante envia una evaluacion. El Assessment Service califica de forma sincrona, aplica las reglas de adaptacion (Cache-Aside TTL=60s) y publica un evento asincrono al broker. La respuesta al estudiante no espera a que Analytics procese el evento.

[ver DiagramaCaso2.svg]

### Sequence Diagram 3: Adaptation Rules Configuration (UC-06, DR-07, DR-05)

El docente actualiza el umbral y los materiales de una leccion. La cache expira naturalmente dentro de <= 60 segundos por TTL, sin invalidacion explicita.

[ver DiagramaCaso3.svg]

### Sequence Diagram 4: Circuit Breaker — Controlled Degradation (DR-03)

El Analytics Service falla. El Circuit Breaker detecta la degradacion y responde fail-fast con HTTP 503, protegiendo a los demas servicios. El Assessment Service sigue publicando eventos al broker, que los acumula hasta que Analytics se recupera.

[ver DiagramaCaso4.svg]

### Sequence Diagram 5: Forum Post with Async Notification (UC-05, DR-03)

Un usuario publica en el foro. La respuesta al usuario es inmediata; las notificaciones a los suscriptores se procesan de forma asincrona via Publish/Subscribe.

[ver DiagramaCaso5.svg]

---

# 7. DEPLOYMENT VIEW

[ver diagramaDespliegue.svg]

Se muestra un entorno de produccion con redundancia. Para desarrollo y pruebas, un despliegue de nodo unico es suficiente.

No todos los nodos fisicos estan representados. Los balanceadores de carga, servidores de base de datos y brokers de mensajes pueden duplicarse para escalar. Los mecanismos de seguridad como firewalls y WAF se omiten del diagrama por claridad.

Nodos identificados:

- **ALB (Application Load Balancer):** Termina TLS y enruta peticiones hacia los servicios backend segun el path. Multi-AZ. No hay API Gateway separado; el ALB es el unico punto de entrada externo.
- **ECS Fargate Cluster — Frontend:** Ejecuta contenedores WildFly sirviendo la aplicacion Jakarta Faces (MPA).
- **ECS Fargate Cluster — Services:** Ejecuta los 5 servicios backend como tareas ECS independientes, cada uno en su propio contenedor WildFly. Min 2, max 10 instancias por servicio. Distribuidos entre AZ-1 y AZ-2.
- **RDS PostgreSQL Primary (AZ-1):** Almacena todos los datos de la aplicacion en schemas logicamente separados. Atiende operaciones de lectura y escritura.
- **RDS PostgreSQL Standby (AZ-2):** Replica sincrona Multi-AZ. Failover automatico en ~60 segundos.
- **RDS PostgreSQL Read Replica (AZ-1):** Replica asincrona de lectura usada exclusivamente por el Analytics Service para consultas de reporte. Lag aceptado: ~5 segundos.
- **ElastiCache Redis (AZ-1):** Cache distribuida para datos de cursos (TTL=3600s) y reglas de adaptacion (TTL=60s).
- **PgBouncer (por servicio):** Proxy de pool de conexiones entre cada servicio y PostgreSQL. Max 20 conexiones por servicio.
- **AWS SNS + SQS:** Broker de mensajes asincrono. Topico SNS para fan-out de eventos; colas SQS por consumidor (Analytics, Collaboration).
- **AWS S3:** Almacenamiento de materiales educativos (PDFs, videos). Servidos via URLs prefirmadas.
- **AWS CloudWatch:** Logs centralizados (JSON estructurado), metricas y alarmas.

---

# 8. IMPLEMENTATION VIEW

## 8.1 Overview

[ver diagramaCapas.svg]

**Capa 1 — Presentation Layer (Web Client):**
La aplicacion MPA Jakarta Faces se despliega en WildFly. Componentes: vistas `.xhtml` (Facelets), Managed Beans CDI (`@Named`, `@ViewScoped`, `@SessionScoped`), y clientes HTTP JAX-RS para llamadas a servicios backend cuando se requiere comunicacion entre dominios.

**Capa 2 — Security Filter Layer:**
Filtro JAX-RS (`ContainerRequestFilter`) incluido en cada servicio via modulo comun (`libs/security`). Aplica: verificacion de firma JWT (RS256 con clave publica), extraccion del rol del claim, y verificacion RBAC por endpoint. Equivale funcionalmente a lo que el API Gateway centralizaba antes; ahora cada servicio lo ejecuta de forma autonoma.

**Capa 3 — Service Layer:**
Los 5 servicios backend, cada uno desplegado en su propio contenedor WildFly. Cada servicio sigue capas internas Jakarta EE estandar: `resources/` (endpoints JAX-RS) -> `services/` (logica de negocio con CDI `@ApplicationScoped`) -> `repositories/` (acceso a datos via JPA 3.1 / Hibernate 6) -> `domain/` (entidades JPA y reglas de dominio).

**Capa 4 — Integration Layer:**
Modulos JAR compartidos en `libs/`: `libs/security` (filtro JWT + RBAC), `libs/events` (POJOs de contratos de eventos para SNS/SQS, serializados con Jackson), `libs/common` (logging estructurado, manejo de errores, utilidades).

**Capa 5 — Domain Layer:**
Entidades JPA (`@Entity`) por schema de servicio. Las relaciones entre entidades del mismo schema se modelan con JPA; las referencias cross-domain usan IDs sin FK foraneas entre schemas.

**Capa 6 — Data Persistence Layer:**
PostgreSQL (schemas por servicio) y S3 (almacenamiento de archivos). PgBouncer gestiona los pools de conexiones JDBC. Hibernate 6 como proveedor JPA.

Todas estas capas corren en contenedores Docker desplegados sobre AWS ECS Fargate.

## 8.2 Repository Structure (Monorepo)

```
plataforma-adaptativa/
+-- apps/
|   +-- web-client/              # Jakarta Faces MPA — WAR desplegado en WildFly
|   |   +-- src/main/
|   |       +-- java/.../beans/  # CDI Managed Beans (@Named, @ViewScoped)
|   |       +-- java/.../client/ # Clientes JAX-RS para llamadas a servicios
|   |       +-- resources/       # Configuracion (beans.xml, faces-config.xml)
|   |       +-- webapp/          # Vistas Facelets (.xhtml), CSS, JS minimo
|   +-- user-service/            # WAR Jakarta EE / WildFly
|   |   +-- src/main/java/.../
|   |       +-- resources/       # Endpoints JAX-RS
|   |       +-- services/        # Logica de negocio (CDI @ApplicationScoped)
|   |       +-- repositories/    # Acceso a datos (JPA EntityManager)
|   |       +-- domain/          # Entidades JPA (@Entity) y reglas de dominio
|   |       +-- events/          # Publicacion de eventos a SNS/SQS
|   +-- course-service/          # Misma estructura
|   +-- assessment-service/      # + rules-engine/ (evaluador de reglas CDI)
|   +-- collaboration-service/   # + websocket/ (Jakarta WebSocket @ServerEndpoint)
|   +-- analytics-service/       # + consumers/ (lectores SQS via hilo MDB)
+-- libs/
|   +-- common/                  # JAR: logging estructurado, errores, utilidades
|   +-- events/                  # JAR: POJOs de contratos de eventos (Jackson)
|   +-- security/                # JAR: ContainerRequestFilter JWT + RBAC
+-- infra/
|   +-- terraform/               # IaC para AWS
|   +-- docker/                  # Dockerfiles por servicio (WildFly base image)
+-- docs/
    +-- openapi/                 # OpenAPI 3.0 specs
    +-- architecture/            # Diagramas y ADRs
```

## 8.3 Justification of Modularity Decisions

**Monorepo con servicios independientes:** Facilita el trabajo en equipo reducido (3-4 personas), permite compartir modulos comunes (libs/) como dependencias Maven sin publicar artefactos privados en un repositorio externo, y mantiene un unico punto de control de versiones. Cada servicio se empaqueta como WAR y se despliega de forma independiente en su propio contenedor WildFly.

**Resources -> Services -> Repositories -> Domain:** Aplica el patron Repository para desacoplar la logica de negocio (CDI beans) de la persistencia (JPA/Hibernate). Esto permite escribir pruebas unitarias con mocks del EntityManager y cambiar la implementacion de persistencia sin afectar la logica de negocio.

**libs/events con POJOs de contratos:** Define los mensajes del broker como clases Java serializadas con Jackson. Los publicadores y consumidores comparten el mismo JAR, lo que previene inconsistencias de contrato sin necesitar un schema registry externo en el MVP.

**libs/security con ContainerRequestFilter:** Al extraer el filtro JWT y RBAC a un JAR compartido se garantiza que todos los servicios aplican exactamente la misma logica de autenticacion y autorizacion, eliminando la necesidad de un API Gateway para esa responsabilidad.

---

# 9. DATA VIEW

## 9.1 Data Model

El modelo de datos se organiza por schema de servicio. Cada servicio es propietario de su schema en PostgreSQL y la comunicacion entre dominios ocurre exclusivamente por REST o eventos, nunca por queries cross-schema.

[ver diagramaDatos.svg]

**Schema: users (User Service)**
- User (id, name, email, password_hash, role, status, created_at, updated_at)
- Session (id, user_id, token_ref, expires_at, status)
- AuditLog (id, user_id, action, ip, timestamp)

**Schema: courses (Course Service)**
- Course (id, instructor_id, title, description, cover_image_url, status, created_at, updated_at)
- Module (id, course_id, name, description, sort_order)
- Lesson (id, module_id, title, content, sort_order)
- Material (id, lesson_id, type, url, filename, size_bytes)
- AdaptationRule (id, lesson_id, threshold, reinforcement_materials, advanced_content, updated_at)

**Schema: assessments (Assessment & Adaptive Service)**
- Quiz (id, lesson_id, max_attempts, time_limit_minutes, available_from, available_until)
- Question (id, quiz_id, type, text, options, correct_answer, points, sort_order)
- EvaluationAttempt (id, quiz_id, student_id, answers, score, adaptive_decision, started_at, submitted_at)

**Schema: collaboration (Collaboration Service)**
- Forum (id, course_id, module_id, name, status)
- Thread (id, forum_id, author_id, title, is_pinned, is_closed, created_at)
- Post (id, thread_id, author_id, content, status, created_at)
- StudyGroup (id, course_id, name, description, max_members, tutor_id)
- GroupMember (id, group_id, user_id, joined_at)
- ChatMessage (id, group_id, author_id, content, created_at)

**Schema: analytics (Analytics Service)**
- ProgressRecord (id, student_id, course_id, lesson_id, completion_pct, last_score, adaptive_decisions, updated_at)
- CourseReport (id, course_id, avg_score, pass_rate, topics_with_errors, generated_at)
- Alert (id, course_id, quiz_id, type, message, is_read, created_at)

## 9.2 State Machines

### 9.2.1 Course State Machine

[ver diagramaEstadosCurso.svg]

DRAFT -> [Publicar (>= 1 modulo con >= 1 leccion)] -> PUBLISHED -> [Despublicar] -> DRAFT

### 9.2.2 Evaluation Attempt State Machine

[ver diagramaEstadosEvaluacion.svg]

STARTED -> [Estudiante envia O tiempo agotado] -> SUBMITTED -> [Calificacion automatica] -> GRADED -> [Motor adaptativo] -> COMPLETED

## 9.3 Data Auditing

Todas las tablas de base de datos tienen columnas de auditoria: CREATED_BY, MODIFIED_BY, CREATION_TIME, MODIFICATION_TIME. Los datos creados por usuarios usan su userId; los creados por procesos asincronos usan el nombre del servicio. La auditoria de operaciones de negocio (login, logout, escrituras) se registra via un interceptor CDI (`@AroundInvoke`) que escribe en la tabla AuditLog.

---

# 10. SIZE AND PERFORMANCE

## 10.1 Size

Los materiales educativos se almacenan en S3 y se sirven via URLs prefirmadas para soportar archivos de gran tamaño (max 50 MB, configurable). Los payloads binarios nunca se almacenan en PostgreSQL; solo se persisten metadatos y referencias.

## 10.2 Performance

La decision mas importante para el rendimiento es el desacoplamiento del camino critico sincrono (cliente -> ALB -> Servicio JAX-RS -> respuesta) del procesamiento secundario asincrono (eventos -> Analytics, Collaboration). La respuesta al estudiante no espera a que Analytics procese el evento, por eso los tiempos de respuesta percibidos son bajos incluso en pico de carga.

El Assessment Service es el cuello de botella principal durante picos de evaluaciones simultaneas (DR-02). Las mitigaciones son: auto-scaling horizontal (3-10 instancias), Cache-Aside para reglas de adaptacion (TTL=60s) y Pub/Sub para desacoplar el procesamiento post-evaluacion.

Latencias estimadas bajo carga normal:

| Operation | Estimated P95 | Limiting component | Mitigation |
|-----------|--------------|-------------------|------------|
| Login | ~150ms | bcrypt costo 12 (deliberado) | Rate limiting en ContainerRequestFilter |
| Course load | ~80ms | PostgreSQL + Cache | Cache-Aside TTL=3600s |
| Evaluation submit | ~120ms | Assessment CPU (JPA + reglas) | Horizontal scaling + Cache |
| Progress dashboard | ~200ms | Analytics queries | Read Replica |
| Forum post | ~60ms | PostgreSQL write | Connection Pool PgBouncer |
| CSV report | ~800ms | Analytics joins | Read Replica + indexes |

---

# 11. QUALITY

La arquitectura aporta a tres atributos de calidad de manera concreta: extensibilidad, confiabilidad y portabilidad.

## 11.1 Extensibility

El estilo Service-Based con schemas separados permite agregar nuevos servicios de dominio sin tocar los existentes. La libreria libs/events define contratos de eventos que nuevos consumidores pueden suscribir sin modificar los publicadores.

## 11.2 Reliability

La confiabilidad descansa en cinco mecanismos complementarios: el broker de mensajes (los eventos sobreviven a la falla del consumidor), el Circuit Breaker (proteccion ante cascada de fallas), el patron Outbox (entrega at-least-once), la DLQ (captura de mensajes que fallan 3 veces) y la redundancia Multi-AZ de la base de datos.

## 11.3 Portability

El sistema corre sobre contenedores Docker, lo que facilita la migracion a otro proveedor cloud. PostgreSQL es open-source. Los servicios gestionados de AWS (ECS, RDS, ElastiCache) tienen equivalentes en otros proveedores. La infraestructura esta definida como codigo (Terraform), lo que simplifica cualquier migracion.

---

# 12. SELECTION OF ARCHITECTURAL STYLE

## 12.1 Process

Siguiendo el paso 5 del metodo ADD (REF-03), se evaluaron tres estilos candidatos contra los drivers priorizados del SRS. La evaluacion usa las tablas de caracteristicas de Richards (REF-05) y el juicio del equipo sobre el contexto del proyecto.

## 12.2 Candidate Styles

| Style | Description |
|-------|-------------|
| **Monolith Modular** | Unidad desplegable unica con modulos internos bien definidos |
| **Service-Based Architecture** | 4-12 servicios de grano grueso, cada uno responsable de un dominio funcional, desplegables de forma independiente |
| **Microservices** | Muchos servicios de grano fino, cada uno con su propia BD, alta autonomia y sobrecarga operativa significativa |

## 12.3 Decision Table

Cada estilo evaluado contra los 7 drivers. Escala: ++ (soporte fuerte), + (soporta), 0 (neutral), - (perjudica), -- (perjudica fuertemente).

| Driver (priority) | Monolith Modular | Service-Based | Microservices |
|--------------------|------------------|---------------|---------------|
| DR-01 Rendimiento (1) | ++ (sin latencia de red) | + (minima con 5 servicios) | 0 (hops acumulados) |
| DR-04 Seguridad (2) | + (perimetro unico) | ++ (filtro compartido + defense-in-depth por servicio) | ++ (maxima aislacion) |
| DR-07 Motor Adaptativo (3) | + (reglas en BD, evaluacion local) | + (reglas en BD, evaluacion en Assessment) | + (servicio dedicado de reglas) |
| DR-03 Disponibilidad (4) | -- (falla total ante caida) | + (falla de servicio aislada) | ++ (maxima aislacion de fallas) |
| DR-02 Escalabilidad (5) | -- (escala todo o nada) | + (escala por servicio) | ++ (escala por funcion) |
| DR-05 Mantenibilidad (6) | + (simple para equipo pequeño) | ++ (modular, ops manejables) | -- (ops excesivas para 3-4 personas) |
| DR-06 Interoperabilidad (7) | + (REST APIs) | + (REST por servicio) | + (REST por servicio) |
| **Weighted Total** | **Medio** | **Alto** | **Medio-Alto (pero inviable por restriccion del equipo)** |

## 12.4 Decision

**Estilo seleccionado: Service-Based Architecture con 5 servicios de grano grueso.**

**Justificacion por driver:**

- **DR-01 Rendimiento:** La latencia de red entre 5 servicios es minima (~10-20ms por hop) y se mitiga con Cache-Aside. El monolito seria marginalmente mas rapido, pero la diferencia es despreciable frente a los beneficios del estilo seleccionado.
- **DR-04 Seguridad:** Sin API Gateway, la seguridad se centraliza en el modulo `libs/security` que cada servicio incluye como dependencia Maven. El `ContainerRequestFilter` verifica el JWT y aplica RBAC antes de cada peticion, con el mismo codigo en todos los servicios. El ALB termina TLS y es el unico punto expuesto a internet. Este enfoque elimina el hop adicional del gateway sin sacrificar la consistencia de la politica de seguridad.
- **DR-03 Disponibilidad:** El aislamiento de fallas entre servicios permite que el Analytics Service falle sin afectar las evaluaciones criticas. El monolito no ofrece esta garantia.
- **DR-02 Escalabilidad:** Cada servicio escala de forma independiente. El Assessment Service (cuello de botella identificado) puede escalar hasta 10 instancias sin afectar a los demas.
- **DR-05 Mantenibilidad:** Se descarto Microservices por la restriccion del equipo (3-4 personas). Service-Based ofrece un equilibrio entre modularidad y complejidad operativa manejable con monorepo.

**Trade-off aceptado:** Sin API Gateway, el rate limiting se implementa en cada servicio dentro del `ContainerRequestFilter`. Esto distribuye la responsabilidad pero elimina el hop adicional y el punto unico de falla que el gateway representaba.

---

# 13. ARCHITECTURAL PATTERNS AND TACTICS

Cada patron incluye: drivers que beneficia, trade-offs y configuracion.

| Pattern | Description | Drivers Benefited | Trade-off / Cost |
|---------|-------------|-------------------|-----------------| 
| **ContainerRequestFilter (libs/security)** | Filtro JAX-RS compartido en cada servicio. Verifica firma JWT RS256, extrae rol y aplica RBAC antes de cada peticion. Equivale a la capa de seguridad del gateway, sin hop de red adicional | DR-04 Seguridad (defense-in-depth distribuida), DR-01 Rendimiento (elimina hop de gateway) | DR-05: el filtro debe mantenerse sincronizado con la politica RBAC; todos los servicios deben incluir libs/security |
| **Cache-Aside (Lazy Loading)** | El servicio consulta cache primero; ante miss, consulta BD y almacena. TTLs: cursos=3600s, reglas=60s | DR-01 Rendimiento, DR-02 Escalabilidad (reduce carga en BD) | DR-05 Mantenibilidad: complejidad de invalidacion de cache. Riesgo de datos obsoletos (aceptable con TTLs) |
| **Publish/Subscribe + Outbox + DLQ** | Assessment publica eventos; Analytics y Collaboration consumen asincrono. Outbox garantiza at-least-once. DLQ captura mensajes con 3 fallos | DR-01 (desacopla procesamiento secundario), DR-03 (broker acumula ante falla), DR-02 (consumidores escalan independientemente) | DR-05: complejidad del Outbox y la DLQ. Consistencia eventual (~5s de lag en analytics) |
| **Rules Engine con persistencia en BD** | Reglas de adaptacion en tabla `adaptation_rules`, evaluadas en runtime. Los docentes las modifican sin redespliegue | DR-07 Motor Adaptativo, DR-05 Mantenibilidad | DR-01: consulta a BD/cache por evaluacion (mitigado con Cache-Aside TTL=60s) |
| **Active Redundancy (Multi-AZ)** | PostgreSQL con replica sincrona en segunda AZ, failover automatico ~60s | DR-03 Disponibilidad (RPO <= 15min, RTO <= 1h) | Costo de infraestructura (RDS duplicado). Latencia de escritura +1-2ms |
| **Schemas separados por servicio** | Cada servicio accede solo a su schema PostgreSQL. Politica de no cross-schema queries | DR-05 Mantenibilidad (aislamiento logico de datos) | DR-01: sin joins entre dominios. Consistencia eventual |
| **Horizontal Scaling + Stateless** | Las instancias de servicio escalan de forma independiente. El estado de sesion vive en cache, no en memoria de instancia | DR-02 Escalabilidad, DR-03 Disponibilidad | DR-05: disciplina necesaria para mantener stateless |
| **Blue-Green Deployment** | Se provisiona y valida Green antes de derivar trafico. Blue se mantiene 30 min para rollback | DR-05 Mantenibilidad (deploys sin downtime), DR-03 | Costo: infraestructura doble durante 30 min |
| **Connection Pool (PgBouncer)** | Proxy entre servicios y PostgreSQL. Max 20 conexiones activas por servicio | DR-02 Escalabilidad, DR-01 Rendimiento | Punto adicional de falla (mitigado con health checks) |
| **Read Replica / Partial CQRS** | Analytics lee desde replica separada. Lag ~5s aceptado | DR-01 Rendimiento, DR-02 Escalabilidad | Datos de analytics con ~5s de retraso. Costo de la replica |
| **Circuit Breaker** | >50% errores en 60s -> Gateway responde HTTP 503 sin enrutar. Peticion de prueba antes de cerrar el circuito | DR-03 Disponibilidad (proteccion ante cascada) | Complejidad en la configuracion del umbral |
| **Health Check (Liveness + Readiness)** | Cada servicio expone /health/live y /health/ready. ECS evalua cada 30s y reemplaza instancias no saludables | DR-03 Disponibilidad | Overhead minimo |
| **Distributed Tracing + Correlation ID** | El Gateway genera un UUID v4 por peticion, propagado en headers y logs JSON | DR-05 Mantenibilidad (trazabilidad completa de peticiones) | Overhead minimo en headers |

---

# 14. CONTACT INFORMATION

Equipo de Arquitectura de Software
Pontificia Universidad Javeriana
Curso: Arquitectura de Software, 2026-I

---

**Fin del Documento SAD v3.0**
