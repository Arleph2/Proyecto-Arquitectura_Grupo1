# Software Architecture Document (SAD)
## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Versión:** 1.0  
**Fecha:** 22/03/2026  
**Grupo:** [Número de grupo]  
**Preparado por:**
- [Nombre 1] - [Código]
- [Nombre 2] - [Código]
- [Nombre 3] - [Código]
- [Nombre 4] - [Código]

---

## CONTROL DE VERSIONES

| Versión | Fecha | Autor | Cambios |
|---------|-------|-------|---------|
| 0.1 | 15/03/2026 | [Nombre 1] | Borrador inicial - estructura del documento |
| 0.5 | 19/03/2026 | [Grupo completo] | Secciones de ADRs, vistas C4 y tecnologías |
| 1.0 | 22/03/2026 | [Grupo completo] | Versión final para entrega |

---

## TABLA DE CONTENIDOS

1. [Introducción](#1-introducción)
2. [Descripción General de la Arquitectura](#2-descripción-general-de-la-arquitectura)
3. [Vistas Arquitecturales (4+1)](#3-vistas-arquitecturales-41)
4. [Decisiones Arquitecturales (ADRs)](#4-decisiones-arquitecturales-adrs)
5. [Tecnologías y Herramientas](#5-tecnologías-y-herramientas)
6. [Seguridad](#6-seguridad)
7. [Despliegue e Infraestructura](#7-despliegue-e-infraestructura)
8. [Calidad y Atributos](#8-calidad-y-atributos)
9. [Riesgos y Deuda Técnica](#9-riesgos-y-deuda-técnica)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito del Documento

Este documento describe la arquitectura de software de la **Plataforma de Aprendizaje Adaptativo y Colaborativo**, un sistema educativo digital diseñado para la que adapta el recorrido de aprendizaje de cada estudiante según su desempeño y fomenta la colaboración entre pares.

Este documento sirve como:
- Referencia técnica para el equipo de desarrollo durante la implementación del MVP.
- Evidencia de las decisiones arquitecturales tomadas y su justificación ante los interesados del proyecto.
- Guía de evaluación para la aplicación del método ATAM (Architecture Tradeoff Analysis Method).

### 1.2 Audiencia

| Rol | Uso de este documento |
|-----|----------------------|
| **Desarrolladores** | Guía de implementación: qué servicios construir, cómo se comunican y qué tecnologías usar. |
| **Arquitectos** | Referencia de decisiones arquitecturales y sus justificaciones (ADRs). |
| **Docentes / Evaluadores** | Evaluación del diseño arquitectónico y su alineación con los atributos de calidad requeridos. |
| **Operadores** | Guía de despliegue, configuración de infraestructura y monitoreo del sistema. |

### 1.3 Referencias

- **[SRS]** `Taller_SRS_PlataformaAdaptativa_GrupoX.md` - Documento de requisitos
- **[ADR-001]** `Taller_ADR-001_ServiceBasedArchitecture_GrupoX.md` - Adoptar Service-Based Architecture
- **[ADR-002]** `Taller_ADR-002_PostgreSQL_Redis_GrupoX.md` - PostgreSQL como Base de Datos Principal con Redis
- **[ADR-003]** `Taller_ADR-003_MotorAdaptativo_RabbitMQ_GrupoX.md` - Motor Adaptativo con RabbitMQ

### 1.4 Alcance

Este documento cubre la arquitectura del **MVP** de la Plataforma de Aprendizaje Adaptativo y Colaborativo.

**Dentro de alcance:**
- Gestión de usuarios con autenticación (estudiante, docente, administrador).
- Gestión de cursos con módulos, lecciones y materiales educativos.
- Evaluaciones con calificación automática y motor adaptativo basado en reglas.
- Foros de discusión y grupos de estudio.
- Dashboards de progreso del estudiante y analítica para docentes.
- Despliegue en infraestructura cloud.

**Fuera de alcance:**
- Aplicación móvil nativa.
- Motor de recomendación basado en Machine Learning.
- Integración con sistemas de registro académico institucional.
- Videoconferencia en tiempo real.

---

## 2. DESCRIPCIÓN GENERAL DE LA ARQUITECTURA

### 2.1 Filosofía de Diseño

La arquitectura de la Plataforma de Aprendizaje Adaptativo y Colaborativo sigue estos principios:

1. **Separación por dominio:** Cada servicio es responsable de un dominio funcional bien delimitado. No hay servicios "dios" que manejen todo.
2. **Camino crítico optimizado:** Las operaciones que el usuario experimenta directamente (recibir calificación, cargar un curso) son síncronas y altamente optimizadas. Las operaciones secundarias (actualizar analítica, enviar notificaciones) son asíncronas y no bloquean la experiencia del usuario.
3. **Configurabilidad sin código:** Las reglas de negocio que cambian con frecuencia (umbrales de adaptación, configuración de evaluaciones) están en la base de datos, no en el código.
4. **Resiliencia ante fallos parciales:** La caída de un servicio no debe impedir que los estudiantes accedan al contenido educativo y completen evaluaciones.
5. **Observabilidad desde el inicio:** Logs estructurados, métricas y trazas de correlación son parte del diseño desde el MVP, no un añadido posterior.

### 2.2 Estilo Arquitectural Principal

**Service-Based Architecture** (ver [ADR-001])

La plataforma se divide en 5 servicios de grano grueso, cada uno responsable de un dominio funcional: User Service, Course Service, Assessment & Adaptive Service, Collaboration Service y Analytics Service. Los servicios se comunican a través de un API Gateway para operaciones síncronas y mediante RabbitMQ para eventos asíncronos.

Este estilo permite escalar selectivamente los servicios de mayor carga (especialmente el Assessment & Adaptive Service durante períodos de evaluación), mantener el aislamiento de fallos entre dominios y agregar nuevos servicios sin afectar los existentes. El balance entre la simplicidad del monolito y la granularidad de los microservicios lo hace apropiado para un equipo de 3-4 personas (ver justificación completa en [ADR-001]).

**Justificación:** La Service-Based Architecture cumple simultáneamente con DR-01 (rendimiento), DR-02 (escalabilidad), DR-03 (disponibilidad) y DR-05 (mantenibilidad) con un nivel de complejidad operacional manejable para el equipo.

### 2.3 Drivers Arquitecturales (ASRs)

Los siguientes Architecturally Significant Requirements guiaron las decisiones:

| ID | Driver | Valor | Prioridad |
|----|--------|-------|-----------|
| **DR-01** | Rendimiento (Performance) | Operaciones principales ≤ 2 seg P95 | Alta |
| **DR-02** | Escalabilidad (Scalability) | ≥ 5,000 usuarios concurrentes | Alta |
| **DR-03** | Disponibilidad (Availability) | ≥ 99.5% uptime mensual | Alta |
| **DR-04** | Seguridad (Security) | RBAC + TLS + JWT + Ley 1581 | Alta |
| **DR-05** | Mantenibilidad (Maintainability) | Módulos independientes, CI/CD, cobertura ≥ 70% | Media |
| **DR-06** | Interoperabilidad (Interoperability) | APIs REST + OpenAPI 3.0 + xAPI | Media |
| **DR-07** | Motor Adaptativo | Reglas configurables por docente | Alta |

---

## 3. VISTAS ARQUITECTURALES (4+1)

Este documento utiliza el **modelo 4+1** de Kruchten para describir la arquitectura desde múltiples perspectivas. Los diagramas están disponibles en el archivo `Diagramas.pdf`.


---

### 3.1 Vista Lógica (Logical View)

**Propósito:** Muestra la organización lógica del sistema en términos de paquetes, módulos y sus relaciones. Orientada a los desarrolladores para entender la estructura funcional del sistema.

**Descripción:**  
El sistema se organiza en 5 dominios funcionales principales, cada uno encapsulando su lógica de negocio, modelos de datos y reglas específicas:

- **Dominio de Usuarios:** Gestión de identidad, autenticación y autorización.
- **Dominio de Cursos:** Gestión de contenido educativo: cursos, módulos y lecciones.
- **Dominio de Evaluaciones y Adaptación:** Evaluaciones, motor de reglas adaptativas y retroalimentación.
- **Dominio de Colaboración:** Foros, grupos de estudio y tutorías entre pares.
- **Dominio de Analítica:** Seguimiento de progreso, métricas de desempeño y reportes.

Cada dominio se implementa como un servicio independiente con sus propias capas internas: API Controller, Application Service (lógica de negocio), Repository (acceso a datos) y Domain Model (entidades y reglas de dominio).

**Ver diagrama:** `Vista_Logica`

---

### 3.2 Vista de Desarrollo / Implementación (Development View)

**Propósito:** Muestra la organización del código fuente, paquetes y módulos desde la perspectiva del desarrollador. Orienta la estructura de repositorios y la división del trabajo en equipo.

**Descripción:**  
El sistema se organiza en un monorepo con los siguientes módulos/paquetes:

```
plataforma-adaptativa/
├── apps/
│   ├── web-client/          # React.js SPA - Frontend
│   ├── api-gateway/         # Kong configuration + custom plugins
│   ├── user-service/        # Node.js - Gestión de usuarios
│   ├── course-service/      # Node.js - Gestión de cursos
│   ├── assessment-service/  # Node.js - Evaluaciones y motor adaptativo
│   ├── collaboration-service/ # Node.js - Foros y grupos
│   └── analytics-service/   # Node.js - Progreso y reportes
├── libs/
│   ├── common/              # Utilidades compartidas (logging, errors, validation)
│   ├── events/              # Definición de eventos de RabbitMQ (contratos)
│   └── auth/                # Middleware de autenticación JWT compartido
├── infra/
│   ├── terraform/           # IaC para AWS
│   └── docker/              # Dockerfiles por servicio
└── docs/
    ├── openapi/             # Especificaciones OpenAPI 3.0 por servicio
    └── architecture/        # Diagramas y documentación
```

**Ver diagrama:** `Vista_Desarrollo`

---

### 3.3 Vista de Procesos (Process View)

**Propósito:** Muestra los flujos de ejecución, concurrencia e interacción entre componentes en tiempo de ejecución. Especialmente relevante para entender el flujo del motor adaptativo y la gestión de picos de carga.

**Descripción:**  
Los dos flujos principales del sistema son:

**Flujo 1: Estudiante completa una evaluación (camino crítico)**
1. Estudiante envía respuestas → API Gateway (valida JWT, 10ms).
2. API Gateway → Assessment Service (enruta la petición).
3. Assessment Service calcula calificación (consulta PostgreSQL, 50ms).
4. Assessment Service evalúa reglas de adaptación (consulta Redis caché o PostgreSQL, 20ms).
5. Assessment Service persiste resultado en PostgreSQL (30ms).
6. Assessment Service publica evento `evaluation.completed` en RabbitMQ (10ms).
7. Assessment Service responde al estudiante con calificación + siguiente paso (~120ms total).

**Flujo 2: Procesamiento asíncrono post-evaluación (background)**
- Analytics Service consume `evaluation.completed` → actualiza dashboard (200ms, fuera del camino crítico del usuario).
- Collaboration Service consume `evaluation.failed_threshold` → evalúa candidatos a tutores.
- Notification Service consume `evaluation.completed` → envía notificación al docente.

Esta separación entre camino crítico síncrono y procesamiento asíncrono es la táctica clave para cumplir DR-01 bajo carga de DR-02.

**Ver diagrama:** `Vista_Procesos`

---

### 3.4 Vista Física / Despliegue (Physical View)

**Propósito:** Muestra cómo los componentes de software se mapean a la infraestructura física (nodos, servidores, contenedores, servicios cloud). Orientada al equipo de operaciones e infraestructura.

**Descripción:**  
La plataforma se despliega en **AWS** con los siguientes componentes de infraestructura:

| Componente | Tecnología AWS | Configuración |
|------------|---------------|---------------|
| Frontend SPA | S3 + CloudFront | CDN global, caché de activos estáticos |
| API Gateway | AWS API Gateway / Kong en ECS | Rate limiting, JWT validation, routing |
| User Service | ECS Fargate | 2 instancias mínimo, auto-scaling hasta 6 |
| Course Service | ECS Fargate | 2 instancias mínimo, auto-scaling hasta 6 |
| Assessment Service | ECS Fargate | 3 instancias mínimo, auto-scaling hasta 10 (picos de evaluación) |
| Collaboration Service | ECS Fargate | 1 instancia mínimo, auto-scaling hasta 4 |
| Analytics Service | ECS Fargate | 1 instancia mínimo, auto-scaling hasta 4 |
| Base de datos | AWS RDS PostgreSQL 16 (Multi-AZ) | db.t3.large, 100GB SSD |
| Caché | AWS ElastiCache Redis 7 | cache.t3.medium, cluster mode |
| Message Broker | AWS MQ (RabbitMQ) | Instancia administrada mq.m5.large |
| Almacenamiento de archivos | AWS S3 | Bucket para materiales educativos |
| Monitoreo | AWS CloudWatch + X-Ray | Logs, métricas, tracing distribuido |

**VPC y redes:**
- Los servicios de backend se despliegan en subnets privadas (no accesibles desde internet).
- Solo el API Gateway y CloudFront tienen endpoints públicos.
- La comunicación interna entre servicios ocurre dentro de la VPC.

**Ver diagrama:** `Vista_Fisica`

---

### 3.5 Vista de Escenarios / Casos de Uso (Scenarios View - "+1")

**Propósito:** Valida la arquitectura contra los casos de uso más críticos del sistema. Es el "+1" del modelo 4+1 de Kruchten.

**Escenario 1: Pico de evaluaciones al final del semestre**  
500 estudiantes envían evaluaciones simultáneamente. El Assessment Service auto-escala de 3 a 8 instancias en < 5 minutos. RabbitMQ absorbe el volumen de eventos. El tiempo de respuesta se mantiene < 2 seg P95. La base de datos usa connection pooling para gestionar las conexiones concurrentes.

**Escenario 2: Falla del Analytics Service**  
El Analytics Service cae. Los eventos `evaluation.completed` se acumulan en la cola de RabbitMQ (persistente). Los estudiantes siguen completando evaluaciones y recibiendo calificaciones sin interrupción. Cuando el Analytics Service se recupera, procesa la cola acumulada. Los dashboards se actualizan con retraso, pero los datos no se pierden.

**Escenario 3: Docente configura nuevas reglas de adaptación**  
El docente accede al panel de administración del curso, modifica el umbral de aprobación de 70% a 80% y agrega nuevos materiales de refuerzo. Los cambios se persisten en PostgreSQL. La próxima evaluación que complete cualquier estudiante usará las nuevas reglas sin necesidad de redespliegue.

**Ver diagrama:** `Vista_Escenarios`

---

## 4. DECISIONES ARQUITECTURALES (ADRs)

### 4.1 ADR-001: Adoptar Service-Based Architecture

**Estado:** Aceptado  
**Archivo:** `Taller_ADR-001_ServiceBasedArchitecture_GrupoX.md`

**Resumen:** El sistema se divide en 5 servicios de grano grueso (User, Course, Assessment & Adaptive, Collaboration, Analytics), comunicados a través de un API Gateway y un message broker (RabbitMQ). Comparten una base de datos PostgreSQL con schemas separados por dominio.

**Alternativas consideradas:** Arquitectura Monolítica, Microservicios.

**Trade-off aceptado:** La base de datos compartida crea cierto acoplamiento entre servicios (mitigado con schemas separados y sin cross-schema queries). Mayor complejidad que un monolito, menor que microservicios.

**Ver documento completo:** [ADR-001]

---

### 4.2 ADR-002: PostgreSQL como Base de Datos Principal con Redis para Caché

**Estado:** Aceptado  
**Archivo:** `Taller_ADR-002_PostgreSQL_Redis_GrupoX.md`

**Resumen:** PostgreSQL 16 en AWS RDS Multi-AZ como base de datos principal con schemas separados por servicio. Redis 7 (ElastiCache) como capa de caché para sesiones, contenido frecuente y estado de evaluaciones en progreso. Read replicas para consultas analíticas.

**Alternativas consideradas:** MongoDB, DynamoDB.

**Trade-off aceptado:** Consistencia eventual del dashboard del estudiante (puede tardar hasta 30 segundos en actualizarse) a cambio de desacoplamiento entre el camino crítico y las actualizaciones secundarias.

**Ver documento completo:** [ADR-002]

---

### 4.3 ADR-003: Motor Adaptativo con Reglas Configurables y RabbitMQ

**Estado:** Aceptado  
**Archivo:** `Taller_ADR-003_MotorAdaptativo_RabbitMQ_GrupoX.md`

**Resumen:** Las reglas de adaptación (umbral, materiales de refuerzo, contenido avanzado) se almacenan en PostgreSQL y se evalúan en tiempo de ejecución. El camino crítico (calificación + siguiente paso) es síncrono. Las operaciones secundarias (analytics, notificaciones, tutorías) son asíncronas mediante RabbitMQ.

**Alternativas consideradas:** Reglas hardcodeadas con REST síncrono, Motor de Machine Learning.

**Trade-off aceptado:** Consistencia eventual del dashboard a cambio de respuesta inmediata al estudiante (< 2 seg). Complejidad operacional adicional de RabbitMQ a cambio de resiliencia y desacoplamiento.

**Ver documento completo:** [ADR-003]

---

## 5. TECNOLOGÍAS Y HERRAMIENTAS

### 5.1 Stack Tecnológico

| Capa | Tecnología | Versión | Justificación |
|------|------------|---------|---------------|
| **Frontend** | React.js + TypeScript | 18.x | Ecosistema maduro, SPA para experiencia fluida, tipado estático reduce bugs |
| **Backend (servicios)** | Node.js + Express | 20 LTS | Performance para I/O intensivo, ecosistema npm amplio, mismo lenguaje que frontend |
| **API Gateway** | AWS API Gateway | - | Administrado, integración nativa con AWS, rate limiting y auth incluidos |
| **Base de Datos** | PostgreSQL | 16 | Ver ADR-002: ACID, JOINs complejos, schemas por dominio |
| **Caché** | Redis | 7 | Ver ADR-002: sesiones, caché de contenido, estado de evaluaciones |
| **Message Broker** | RabbitMQ (AWS MQ) | 3.12 | Ver ADR-003: eventos asíncronos, buffer de picos |
| **Almacenamiento** | AWS S3 | - | Archivos de materiales educativos, durabilidad 99.999999999% |
| **Contenedores** | Docker + ECS Fargate | - | Despliegue sin gestión de servidores, auto-scaling |
| **IaC** | Terraform | 1.7.x | Infraestructura como código, reproducible y versionada |
| **CI/CD** | GitHub Actions | - | Pipelines de integración y despliegue continuo |
| **Monitoreo** | CloudWatch + X-Ray | - | Logs, métricas, tracing distribuido en AWS |

### 5.2 Servicios Cloud

**Proveedor:** AWS (Amazon Web Services)

| Servicio | Uso | Costo estimado/mes USD |
|----------|-----|-------------------|
| **ECS Fargate** (5 servicios, ~15 instancias) | Hosting de microservicios | ~$180 |
| **RDS PostgreSQL Multi-AZ** (db.t3.large) | Base de datos principal | ~$150 |
| **ElastiCache Redis** (cache.t3.medium) | Caché y sesiones | ~$30 |
| **AWS MQ RabbitMQ** (mq.m5.large) | Message broker | ~$80 |
| **S3** (~100GB materiales) | Almacenamiento de archivos | ~$5 |
| **CloudFront** (CDN frontend) | Distribución SPA | ~$20 |
| **API Gateway** | Punto de entrada | ~$15 |
| **CloudWatch + X-Ray** | Monitoreo y trazas | ~$20 |
| **TOTAL** | | **~$500/mes** |


### 5.3 Servicios Externos

| Servicio | Uso | Costo |
|----------|-----|-------|
| **SendGrid / AWS SES** | Notificaciones por correo electrónico | ~$0-15/mes según volumen |
| **YouTube / Vimeo (embed)** | Reproducción de videos en materiales | Gratuito (embed público) |
| **Google OAuth 2.0** | Inicio de sesión con cuenta institucional (opcional) | Gratuito |

---

## 6. SEGURIDAD

### 6.1 Autenticación y Autorización

**Mecanismo:** JWT (JSON Web Tokens) con RBAC (Role-Based Access Control)

**Flujo de autenticación:**
1. Usuario envía credenciales (email + password) al User Service vía API Gateway.
2. User Service valida credenciales contra PostgreSQL (password con bcrypt, costo 12).
3. User Service genera JWT firmado con clave privada RS256 (expiración: 8 horas).
4. JWT se almacena en memoria del cliente (no en localStorage) para evitar XSS.
5. Cada petición incluye el JWT en el header `Authorization: Bearer <token>`.
6. El API Gateway valida la firma del JWT antes de enrutar la petición.
7. Cada servicio verifica los roles del JWT antes de ejecutar la operación.

**Roles y permisos:**
- `student`: Accede a cursos inscritos, completa evaluaciones, participa en foros y consulta su progreso.
- `instructor`: Crea y gestiona cursos, define evaluaciones, monitorea el progreso del grupo.
- `admin`: Gestión completa de usuarios, configuración de la plataforma, acceso a todos los reportes.

### 6.2 Protección de Datos

**Cumplimiento Ley 1581 de 2012 (Colombia):**
- [x] Consentimiento explícito en el registro (checkbox obligatorio con enlace a política de privacidad).
- [x] Política de privacidad visible y accesible desde todas las páginas.
- [x] Encriptación en tránsito: TLS 1.3 en todas las comunicaciones.
- [x] Encriptación en reposo: RDS con cifrado AES-256, S3 con SSE-S3.
- [x] Derecho al olvido: los administradores pueden eliminar datos personales de usuarios inactivos.
- [x] Los datos de los estudiantes no se comparten con terceros sin consentimiento explícito.

**Datos sensibles:**
- Contraseñas: hash bcrypt con factor de costo 12, nunca almacenadas en texto plano.
- Datos de identificación: acceso restringido por VPC y roles de base de datos separados por servicio.

### 6.3 Protección de APIs

| Medida | Implementación |
|--------|----------------|
| **HTTPS/TLS** | TLS 1.3 obligatorio en API Gateway; certificados administrados por AWS Certificate Manager |
| **Rate limiting** | 100 req/min por usuario autenticado; 20 req/min para rutas de autenticación (anti-brute force) |
| **Input validation** | Joi/Zod en cada servicio para validar esquemas de entrada; sanitización de HTML en contenido de foros |
| **CORS** | Solo dominios de confianza configurados en API Gateway |
| **SQL injection** | ORM con prepared statements (Prisma); sin concatenación de queries |
| **Dependency scanning** | GitHub Actions ejecuta `npm audit` en cada PR |

---

## 7. DESPLIEGUE E INFRAESTRUCTURA

### 7.1 Ambientes

| Ambiente | Propósito | URL |
|----------|-----------|-----|
| **Development** | Desarrollo local con Docker Compose | localhost:3000 |
| **Staging** | Pruebas de integración y QA antes de producción | staging.plataforma-edu.com |
| **Production** | Ambiente productivo para usuarios reales | plataforma-edu.com |

### 7.2 Estrategia de Despliegue

**Método:** Blue-Green Deployment para producción.

**Proceso:**
1. El desarrollador hace push a la rama `main` → GitHub Actions ejecuta el pipeline.
2. Pipeline: lint → unit tests → build Docker image → push a ECR → deploy a ECS (Staging).
3. Pruebas automáticas de smoke test en Staging (5 minutos).
4. Aprobación manual (o automática si todos los tests pasan) → deploy a Production (Blue-Green).
5. El API Gateway enruta tráfico al nuevo ambiente (Green). El anterior (Blue) permanece activo 10 minutos para rollback.
6. Si hay error, el rollback es automático en < 2 minutos.

### 7.3 Configuración de Infraestructura

**Servicios ECS (configuración base, escalan con demanda):**

| Servicio | Instancias mín. | Instancias máx. | CPU | RAM |
|---------|----------------|----------------|-----|-----|
| User Service | 2 | 6 | 0.5 vCPU | 1 GB |
| Course Service | 2 | 6 | 0.5 vCPU | 1 GB |
| Assessment Service | 3 | 10 | 1 vCPU | 2 GB |
| Collaboration Service | 1 | 4 | 0.5 vCPU | 1 GB |
| Analytics Service | 1 | 4 | 1 vCPU | 2 GB |

**Base de Datos (RDS PostgreSQL):**
- Instancia: db.t3.large (2 vCPU, 8 GB RAM)
- Storage: 100 GB SSD (gp3), auto-scaling hasta 500 GB
- Multi-AZ: Sí (failover automático < 60 seg)
- Read Replicas: 1 réplica para consultas analíticas
- Backups: Automáticos cada 24 horas, retención 7 días

**Connection Pooling:** PgBouncer en modo transaction pooling para gestionar conexiones concurrentes de ECS hacia RDS.

### 7.4 Monitoreo y Alertas

**Métricas clave:**

| Métrica | Threshold | Alerta |
|---------|-----------|--------|
| Latencia P95 API Gateway | > 1.5 seg | PagerDuty → equipo de desarrollo |
| CPU Assessment Service | > 80% por 5 min | Auto-scaling trigger |
| Error rate (5xx) | > 1% por 5 min | PagerDuty + Slack |
| RabbitMQ queue depth | > 10,000 mensajes | Slack → revisar consumidores |
| RDS CPU | > 70% por 10 min | Slack → revisar queries |
| RDS Storage | > 80% | PagerDuty → expandir storage |

**Herramientas:** AWS CloudWatch (métricas e infraestructura), AWS X-Ray (distributed tracing), CloudWatch Logs Insights (análisis de logs estructurados JSON).

---

## 8. CALIDAD Y ATRIBUTOS

### 8.1 Mapa de Atributos a Decisiones

| Atributo | Objetivo (del SRS) | Decisión arquitectural |
|----------|-------------------|------------------------|
| **Performance** | RNF-01: ≤ 2 seg P95 | ADR-003: camino crítico síncrono ligero; ADR-002: Redis para caché de operaciones frecuentes |
| **Availability** | RNF-02: ≥ 99.5% | ADR-001: aislamiento de fallos por servicio; ADR-002: RDS Multi-AZ; ADR-003: RabbitMQ como buffer ante caídas |
| **Scalability** | RNF-03: ≥ 5,000 usuarios concurrentes | ADR-001: auto-scaling independiente por servicio; ADR-003: RabbitMQ desacopla picos de carga |
| **Security** | RNF-04: RBAC + TLS + Ley 1581 | Sección 6: JWT + RBAC + cifrado en reposo y tránsito |
| **Maintainability** | RNF-05: nuevos módulos sin afectar sistema | ADR-001: servicios independientes; ADR-003: reglas de adaptación configurables sin código |
| **Interoperability** | RNF-06: APIs REST + OpenAPI 3.0 | Cada servicio expone API REST documentada con OpenAPI 3.0 a través del API Gateway |

### 8.2 Tácticas Arquitecturales Aplicadas

**Para Performance (DR-01):**
- Caché de lado del servidor (Redis): operaciones frecuentes se resuelven sin consultar la base de datos.
- Separación del camino crítico: solo las operaciones esenciales son síncronas.
- Read replicas en PostgreSQL: las consultas analíticas no compiten con las escrituras operacionales.
- CDN (CloudFront): activos estáticos del frontend servidos desde edge locations.

**Para Availability (DR-03):**
- Redundancia con Multi-AZ: failover automático en < 60 segundos.
- Aislamiento de fallos por servicio: la caída de Analytics no afecta las evaluaciones.
- Mensajería persistente (RabbitMQ): los eventos no se pierden si un consumidor falla.
- Health checks y circuit breakers en el API Gateway.

**Para Scalability (DR-02):**
- Auto-scaling horizontal independiente por servicio.
- RabbitMQ como buffer de picos de carga.
- Stateless services: los servicios no guardan estado en memoria, facilitando el escalado horizontal.
- Connection pooling (PgBouncer) para gestionar conexiones concurrentes a la base de datos.

**Para Maintainability (DR-05):**
- Servicios con responsabilidades claras y bien delimitadas.
- Reglas de negocio en base de datos.
- Pipelines CI/CD con zero-downtime deployment.

### 8.3 Patrones Arquitectónicos Aplicados

| Patrón | Dónde se aplica | Atributo de calidad que mejora |
|--------|-----------------|-------------------------------|
| **API Gateway** | Punto de entrada único | Seguridad, Mantenibilidad |
| **Event-Driven Architecture** | Comunicación entre servicios vía RabbitMQ | Disponibilidad, Escalabilidad, Mantenibilidad |
| **CQRS (parcial)** | Read replicas para analítica vs. escrituras operacionales | Performance |
| **Outbox Pattern** | Publicación confiable de eventos desde Assessment Service | Disponibilidad |
| **Circuit Breaker** | API Gateway → servicios backend | Disponibilidad |
| **Repository Pattern** | Capa de acceso a datos en cada servicio | Mantenibilidad |
| **Rules Engine** | Motor adaptativo con reglas en BD | Mantenibilidad, Configurabilidad |

### 8.4 Testing Strategy

| Tipo | Herramienta | Coverage objetivo |
|------|-------------|-------------------|
| **Unit tests** | Jest + supertest | ≥ 70% en servicios críticos (Assessment, User) |
| **Integration tests** | Jest + TestContainers | Flujos principales por servicio |
| **E2E tests** | Playwright | Flujos críticos: registro, completar evaluación, ver progreso |
| **Load tests** | k6 | 5,000 usuarios concurrentes, P95 ≤ 2 seg |
| **Security tests** | OWASP ZAP | OWASP Top 10 antes de cada release mayor |

---

## 9. RIESGOS Y DEUDA TÉCNICA

### 9.1 Riesgos Técnicos Identificados

| ID | Riesgo | Probabilidad | Impacto | Mitigación |
|----|--------|--------------|---------|------------|
| **R-01** | Pico de evaluaciones simultáneas supera la capacidad de auto-scaling | Media | Alto | Pruebas de carga anticipadas con k6; configurar auto-scaling agresivo para Assessment Service; RabbitMQ como buffer |
| **R-02** | Falla de RDS PostgreSQL en período de evaluaciones | Baja | Alto | Multi-AZ garantiza failover < 60 seg; backups diarios; Read replicas para continuidad de lecturas |
| **R-03** | Acumulación excesiva de mensajes en RabbitMQ por caída del Analytics Service | Media | Medio | Dead letter queues; alertas de monitoreo cuando queue depth > 10,000; Auto-restart de contenedores ECS |
| **R-04** | El equipo (3-4 personas) subestima el tiempo de configuración de infraestructura AWS | Alta | Medio | Usar Terraform para IaC reproducible; comenzar con infraestructura mínima y escalar gradualmente |
| **R-05** | Inconsistencia de datos entre el estado real de adaptación y el dashboard del estudiante | Media | Medio | TTLs conservadores en Redis; indicador visual de "actualizando" en la UI del dashboard |

### 9.2 Deuda Técnica Aceptada

| Ítem | Justificación | Plan de resolución |
|------|---------------|-------------------|
| **Base de datos compartida entre servicios** | El costo de operar 5 instancias de BD separadas excede el presupuesto del MVP y la capacidad del equipo | En V2.0 se evaluará separar la BD del Analytics Service a Redshift cuando el volumen supere 50M registros |
| **Sin xAPI en MVP** | La implementación del estándar xAPI requiere tiempo adicional que no está disponible para el MVP | Se implementará en la siguiente iteración tras el MVP, habilitando interoperabilidad con LRS externos |
| **Motor adaptativo con reglas simples (sin ML)** | El ML requiere datos históricos suficientes que no existen en el MVP | En V2.0 o V3.0 se evaluará un motor de recomendación basado en ML cuando se acumulen +10,000 sesiones |

### 9.3 Supuestos Críticos

1. Los usuarios tienen acceso a internet con velocidad mínima de 5 Mbps (del SRS, sección 6.1).
2. Todos los usuarios tienen correo institucional válido para el registro (del SRS, sección 6.1).
3. El crecimiento de usuarios es gradual: 500 en el primer mes, hasta 5,000 en el primer año.
4. Los videos son embedidos desde YouTube/Vimeo; la plataforma no almacena ni sirve videos directamente.
5. El equipo de 3-4 desarrolladores tiene experiencia con Node.js, React y servicios cloud básicos de AWS.

---

## APROBACIONES

| Rol | Nombre | Firma | Fecha |
|-----|--------|-------|-------|
| **Líder del Grupo** | [Nombre] | __________ | ___/___/___ |
| **Arquitecto** | [Nombre] | __________ | ___/___/___ |
| **Desarrollador 1** | [Nombre] | __________ | ___/___/___ |
| **Desarrollador 2** | [Nombre] | __________ | ___/___/___ |

---


