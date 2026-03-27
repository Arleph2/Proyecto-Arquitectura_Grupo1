# Software Architecture Document (SAD)
## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Version:** 2.0
**Fecha:** 24/03/2026
**Basado en:** SRS 1.0

---

## 1. PATRONES ARQUITECTONICOS Y RNF

### 1.1 Estilo Arquitectural Principal

La plataforma adopta una **Service-Based Architecture** con 5 servicios de grano grueso: User Service, Course Service, Assessment & Adaptive Service, Collaboration Service y Analytics Service. Un API Gateway centraliza el punto de entrada y un broker de mensajeria asincrona desacopla los servicios productores de los consumidores. Los servicios comparten una instancia de base de datos relacional con schemas logicamente separados por dominio.

### 1.2 Drivers Arquitecturales (ASRs)

| ID | Driver | Valor/Metrica | Prioridad | Patron Principal |
|----|--------|---------------|-----------|-----------------|
| DR-01 | Rendimiento | ≤ 2 seg P95 | Alta | Cache-Aside + Sincrono ligero |
| DR-02 | Escalabilidad | ≥ 5,000 usuarios concurrentes | Alta | Horizontal Scaling + Publish/Subscribe |
| DR-03 | Disponibilidad | ≥ 99.5% uptime mensual | Alta | Redundancia Activa + Circuit Breaker |
| DR-04 | Seguridad | RBAC + TLS + JWT + Ley 1581 | Alta | API Gateway + RBAC |
| DR-05 | Mantenibilidad | Modulos independientes, CI/CD | Media | Service-Based + Rules Engine + Blue-Green |
| DR-06 | Interoperabilidad | APIs REST + OpenAPI 3.0 + xAPI | Media | REST + OpenAPI 3.0 |
| DR-07 | Motor Adaptativo | Reglas configurables por docente | Alta | Rules Engine con persistencia en BD |

### 1.3 Tabla de Patrones Arquitectonicos y RNF

| Patron | Descripcion | RNF que satisface |
|--------|-------------|-------------------|
| **Service-Based Architecture** | El sistema se divide en 5 servicios de grano grueso, cada uno responsable de un dominio funcional. Permite escalado independiente y aislamiento de fallos.| RNF-03 (Escalabilidad), RNF-02 (Disponibilidad), RNF-05 (Mantenibilidad) |
| **API Gateway** | Punto de entrada unico para todos los clientes. Centraliza autenticacion JWT, rate limiting y enrutamiento. | RNF-04 (Seguridad), RNF-06 (Interoperabilidad) |
| **Role-Based Access Control (RBAC)** | Verificacion de permisos por rol en dos capas: API Gateway (primera linea) y cada servicio backend (defensa en profundidad). | RNF-04 (Seguridad) |
| **Cache-Aside (Lazy Loading)** | Los servicios consultan primero la cache; ante un miss, consultan la base de datos y almacenan el resultado. TTLs por tipo de dato: sesiones=8h, cursos=3600s, reglas=60s. | RNF-01 (Rendimiento), RNF-03 (Escalabilidad) |
| **Publish/Subscribe + Outbox Pattern + DLQ** | El Assessment Service publica eventos `evaluation.completed`; Analytics y Collaboration los consumen de forma asincrona. El Outbox Pattern garantiza entrega at-least-once; la DLQ captura mensajes que fallan 3 veces.| RNF-01 (Rendimiento), RNF-02 (Disponibilidad), RNF-03 (Escalabilidad), RNF-05 (Mantenibilidad) |
| **Rules Engine con persistencia en BD** | Las reglas de adaptacion (umbrales, materiales de refuerzo, contenido avanzado) se almacenan en la tabla `adaptation_rules` y se evaluan en tiempo de ejecucion. Los docentes las modifican sin redespliegue.| RNF-05 (Mantenibilidad), DR-07 (Motor Adaptativo) |
| **Redundancia Activa (Active Redundancy)** | La base de datos relacional opera en configuracion Multi-AZ: replica sincrona en segunda zona de disponibilidad con failover automatico en ~60 segundos. | RNF-02 (Disponibilidad) |
| **Schemas separados por servicio (BD compartida)** | Cada servicio accede exclusivamente a su propio schema de PostgreSQL. Politica "no cross-schema queries": la comunicacion entre dominios ocurre solo por eventos o REST. | RNF-05 (Mantenibilidad) |
| **Horizontal Scaling + Stateless Services** | Las instancias de cada servicio escalan de forma independiente. El estado de sesion se almacena en la cache, no en memoria de instancia. | RNF-03 (Escalabilidad), RNF-02 (Disponibilidad) |
| **Blue-Green Deployment** | El nuevo conjunto de instancias (Green) se aprovisiona y valida antes de recibir trafico. El entorno Blue se mantiene 30 minutos como respaldo para rollback inmediato. | RNF-05 (Mantenibilidad), RNF-02 (Disponibilidad) |
| **Connection Pool (Repository Pattern)** | PgBouncer actua como proxy de conexiones entre los servicios y PostgreSQL. Cada servicio configura un pool de maximo 20 conexiones activas.| RNF-03 (Escalabilidad), RNF-01 (Rendimiento) |
| **Read Replica / CQRS parcial** | El Analytics Service ejecuta consultas de lectura sobre una replica de lectura separada, sin competir con operaciones transaccionales. Lag aceptado: ~5 segundos.  | RNF-01 (Rendimiento), RNF-05 (Mantenibilidad) |
| **Circuit Breaker** | Si un servicio supera el umbral de errores (>50% en 60s), el Gateway responde con HTTP 503 sin enrutar al servicio degradado. Tras un periodo de espera, envia una peticion de prueba antes de cerrar el circuito. | RNF-02 (Disponibilidad) |
| **Health Check / Liveness & Readiness Probes** | Cada servicio expone `/health/live` (proceso activo) y `/health/ready` (dependencias disponibles). ECS evalua cada 30s y reemplaza instancias no saludables automaticamente.| RNF-02 (Disponibilidad) |
| **Distributed Tracing + Correlation ID** | El API Gateway genera un Correlation ID (UUID v4) por peticion, propagado en headers HTTP y en todos los logs JSON estructurados. | RNF-05 (Mantenibilidad) |


---

## 2. DESCRIPCIoN DE COMPONENTES

### 2.1 Estructura de Repositorio (Monorepo)

```
plataforma-adaptativa/
├── apps/
│   ├── web-client/              # React.js 18 + TypeScript SPA
│   │   ├── src/
│   │   │   ├── components/      # Componentes reutilizables UI
│   │   │   ├── pages/           # Paginas por ruta
│   │   │   ├── hooks/           # Custom hooks (useAuth, useCourse, etc.)
│   │   │   ├── services/        # Clientes HTTP hacia API Gateway
│   │   │   └── store/           # Estado global (Redux/Zustand)
│   │   └── public/
│   │
│   ├── api-gateway/             # AWS API Gateway + Kong plugins
│   │   ├── config/              # Configuracion de rutas y rate limiting
│   │   └── plugins/             # Plugin JWT validation, Circuit Breaker
│   │
│   ├── user-service/            # Node.js + Express
│   │   ├── src/
│   │   │   ├── controllers/     # HTTP request handlers
│   │   │   ├── services/        # Logica de negocio
│   │   │   ├── repositories/    # Acceso a datos (Prisma ORM)
│   │   │   ├── domain/          # Entidades y reglas de dominio
│   │   │   └── events/          # Emision de eventos al broker
│   │   └── tests/
│   │
│   ├── course-service/          # Misma estructura que user-service
│   ├── assessment-service/      # + rules-engine/ para motor adaptativo
│   ├── collaboration-service/   # + websocket/ para chat en tiempo real
│   └── analytics-service/       # + consumers/ para eventos del broker
│
├── libs/
│   ├── common/                  # Utilidades compartidas
│   │   ├── logging/             # Logger estructurado JSON
│   │   ├── errors/              # Error handling centralizado
│   │   └── validation/          # Schemas Joi/Zod comunes
│   ├── events/                  # Contratos de eventos del broker
│   │   └── schemas/             # event.evaluation.completed.ts, etc.
│   └── auth/                    # Middleware JWT compartido (RBAC)
│
├── infra/
│   ├── terraform/               # IaC para AWS
│   │   ├── modules/             # VPC, ECS, RDS, ElastiCache, MQ
│   │   ├── environments/        # staging/, production/
│   │   └── variables.tf
│   └── docker/
│       └── [service]/Dockerfile
│
└── docs/
    ├── openapi/                 # Specs OpenAPI 3.0 por servicio
    └── architecture/            # Diagramas y ADRs
```

### 2.3 Justificacion de Decisiones de Modularidad

**Monorepo con servicios independientes:** Facilita el desarrollo en equipo pequeño (3-4 personas), permite compartir librerias comunes (`libs/`) sin publicar paquetes privados, y mantiene un unico punto de control de versiones. Cada servicio es desplegable de forma independiente mediante Docker.

**Separacion Controller → Service → Repository → Domain:** Aplica el patron Repository para desacoplar la logica de negocio del mecanismo de persistencia, facilitando las pruebas unitarias con mocks y el cambio de tecnologia de BD sin afectar la logica de negocio.

**Libreria `libs/events` con contratos de eventos:** Define los esquemas de mensajes del broker como tipos TypeScript compartidos entre publisher (Assessment Service) y consumers (Analytics, Collaboration). Previene inconsistencias de contrato entre servicios sin necesidad de un schema registry externo en el MVP.

---

## 3. DOCUMENTACIoN DE LA ARQUITECTURA: VISTAS ARQUITECToNICAS (4+1)

### 3.1 Vista Logica

ver diagramaLogico.svg

**Dominios del sistema:**

| Dominio | Servicio responsable | Entidades principales |
|---------|---------------------|----------------------|
| **Usuarios y Autenticacion** | User Service | User, Role, Session, Permission |
| **Contenidos Educativos** | Course Service | Course, Module, Lesson, AdaptationRule, ContentURL |
| **Evaluacion y Adaptacion** | Assessment & Adaptive Service | Quiz, Question, EvaluationAttempt, AdaptiveEngine |
| **Colaboracion** | Collaboration Service | Forum, Thread, Post, StudyGroup, ChatMessage |
| **Analitica** | Analytics Service | ProgressRecord, AnalyticsReport, Alert |

**Relaciones entre dominios:**
- El Assessment Service consulta `AdaptationRule` del dominio de Cursos (via cache o BD).
- El Analytics Service consume eventos del Assessment Service para actualizar `ProgressRecord`.
- El Collaboration Service consume eventos del Assessment Service para identificar candidatos a tutores.
- Todos los dominios dependen del User Service para autenticacion y autorizacion.


---

### 3.2 Vista de Desarrollo

Ver diagramaComponentes.svg

- Cada servicio bajo `apps/` es un modulo Node.js desplegable de forma independiente.
- Las librerias bajo `libs/` son compartidas entre servicios como dependencias locales del workspace.
- La carpeta `infra/` contiene la definicion declarativa de toda la infraestructura (IaC con Terraform).
- Cada servicio sigue la estructura interna: `controllers/` → `services/` → `repositories/` → `domain/`.

---

### 3.3 Vista de Procesos

ver diagramaActividad.svg

#### Modelo de Concurrencia

El sistema opera bajo dos modos de comunicacion:

- **Sincrono (camino critico):** Peticiones HTTP del estudiante → API Gateway → Servicio → Respuesta directa. Optimizado para latencia minima. Aplica los patrones Cache-Aside y Connection Pool.
- **Asincrono (procesamiento secundario):** Eventos publicados en el broker → Consumidos por Analytics y Collaboration. Aplica el patron Publish/Subscribe. Fuera del camino critico; no bloquean la respuesta al usuario.

#### Analisis de Rendimiento y Cuellos de Botella

| Operacion | Latencia estimada | Componente limitante | Patron de mitigacion |
|-----------|-------------------|---------------------|----------------------|
| Login | ~150ms | bcrypt costo=12 (deliberado) | Rate limiting anti-brute force |
| Carga de curso | ~80ms P95 | PostgreSQL + Cache | Cache-Aside TTL=3600s |
| Envio de evaluacion | ~120ms P95 | Assessment Service (CPU) | Horizontal Scaling + Cache-Aside de reglas |
| Dashboard progreso | ~200ms P95 | Consultas de analitica | Read Replica + CQRS parcial |
| Post en foro | ~60ms P95 | PostgreSQL write | Connection Pool (PgBouncer) |
| Reporte CSV docente | ~800ms | Joins de analitica | Read Replica + indices optimizados |

**Cuello de botella principal identificado:** El Assessment Service durante picos de evaluacion simultanea (DR-02). Mitigado con: minimo 3 instancias ECS, auto-scaling hasta 10, Cache-Aside para reglas de adaptacion, y Publish/Subscribe para desacoplar el procesamiento post-evaluacion.

---

### 3.4 Vista Fisica

ver diagramaDespliegue.svg

---

### 3.5 Vista de Escenarios (+1)

ver DiagramaCasosdeUso.svg


a continuacion vamos a ver diagramas de secuencias para escenarios elegidos por nosotros para entender mejor la comunicacion y relaciones de los componentes
#### Escenario 1: Autenticacion de Usuario (UC-01, DR-04, DR-01)

**Descripcion:** Un usuario ingresa sus credenciales. El API Gateway enruta al User Service, que valida contra la base de datos con bcrypt, genera un JWT RS256 y registra la sesion en la cache.

ver DiagramaCaso1.svg

---

#### Escenario 2: Envio de Evaluacion con Motor Adaptativo (UC-03, DR-01, DR-07)

**Descripcion:** El estudiante envia una evaluacion. El Assessment Service califica sincronamente, aplica las reglas de adaptacion (Cache-Aside TTL=60s) y publica un evento asincrono al broker. La respuesta al estudiante no espera el procesamiento del Analytics Service.

ver DiagramaCaso2.svg

---

#### Escenario 3: Configuracion de Reglas de Adaptacion (UC-06, DR-07, DR-05)

**Descripcion:** El docente actualiza el umbral y materiales de una leccion. La cache expira naturalmente en ≤ 60 segundos (TTL), sin invalidacion explicita.

ver DiagramaCaso3.svg

---

#### Escenario 4: Circuit Breaker — Degradacion Controlada (AP Circuit Breaker, DR-03)

**Descripcion:** El Analytics Service falla. El Circuit Breaker del API Gateway detecta la degradacion y responde fail-fast con HTTP 503, protegiendo los demas servicios. El Assessment Service continua publicando eventos al broker, que los acumula hasta que el Analytics Service se recupere.

ver DiagramaCaso4.svg

---

#### Escenario 5: Publicacion en Foro con Notificacion Asincrona (UC-05, DR-03)

**Descripcion:** Un usuario publica en el foro. La respuesta es inmediata; las notificaciones a suscriptores se procesan de forma asincrona via Publish/Subscribe.

ver DiagramaCaso5.svg

---

**Fin del Documento SAD v2.1**
