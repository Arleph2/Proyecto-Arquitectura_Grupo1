# Software Architecture Document (SAD) — Modelo 4+1
## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Versión:** 2.0 (Revisión con Modelo 4+1 Completo)
**Fecha:** 24/03/2026
**Basado en:** SRS v1.0 + SAD v1.0
**Arquitecto revisor:** Análisis independiente

---

## TABLA DE CONTENIDOS

1. [Patrones Arquitectónicos y Atributos de Calidad](#1-patrones-arquitectónicos-y-atributos-de-calidad)
2. [Descripción de Componentes](#2-descripción-de-componentes)
3. [Documentación de la Arquitectura: Vistas Arquitectónicas (4+1)](#3-documentación-de-la-arquitectura-vistas-arquitectónicas-41)
   - [Vista Lógica](#31-vista-lógica)
   - [Vista de Desarrollo](#32-vista-de-desarrollo)
   - [Vista de Procesos](#33-vista-de-procesos)
   - [Vista Física](#34-vista-física)
   - [Vista de Escenarios (+1)](#35-vista-de-escenarios-1)

---

## 1. PATRONES ARQUITECTÓNICOS Y ATRIBUTOS DE CALIDAD

### 1.1 Estilo Arquitectural

La plataforma adopta una **Service-Based Architecture** con 5 servicios de grano grueso (User, Course, Assessment & Adaptive, Collaboration, Analytics), un API Gateway como punto de entrada único, y RabbitMQ como broker de mensajería asíncrona. Los servicios comparten una instancia de PostgreSQL con schemas lógicamente separados por dominio (deuda técnica aceptada para el MVP).

### 1.2 Drivers Arquitecturales (ASRs)

| ID | Driver | Valor/Métrica | Prioridad | Táctica Principal |
|----|--------|---------------|-----------|-------------------|
| DR-01 | Rendimiento | ≤ 2 seg P95 | Alta | Caché Redis + camino crítico síncrono ligero |
| DR-02 | Escalabilidad | ≥ 5,000 usuarios concurrentes | Alta | Auto-scaling ECS + RabbitMQ como buffer |
| DR-03 | Disponibilidad | ≥ 99.5% uptime mensual | Alta | Multi-AZ RDS + aislamiento de fallos por servicio |
| DR-04 | Seguridad | RBAC + TLS + JWT + Ley 1581 | Alta | JWT RS256 + RBAC en cada servicio |
| DR-05 | Mantenibilidad | Módulos independientes, CI/CD | Media | Service-Based + Reglas en BD + Blue-Green Deploy |
| DR-06 | Interoperabilidad | APIs REST + OpenAPI 3.0 + xAPI | Media | REST + OpenAPI 3.0 (xAPI pospuesto a V2) |
| DR-07 | Motor Adaptativo | Reglas configurables por docente | Alta | Rules Engine con reglas en PostgreSQL |

### 1.3 Matriz de Atributos de Calidad vs. Tácticas (ISO/IEC 25010)

| Atributo ISO 25010 | Driver | Táctica Aplicada | Componentes involucrados | ¿Cumple? |
|-------------------|--------|-----------------|------------------------|----------|
| **Performance Efficiency** | DR-01 | Cache-Aside (Redis) | Assessment Service ↔ Redis |  Parcial* |
| **Performance Efficiency** | DR-01 | Camino crítico síncrono ligero | Assessment Service (async post-eval) | si |
| **Reliability/Availability** | DR-03 | Redundancy Multi-AZ | RDS PostgreSQL Multi-AZ | si |
| **Reliability/Availability** | DR-03 | Fault Isolation | Service-Based Architecture | si |
| **Reliability/Availability** | DR-03 | Message Queue (RabbitMQ) | Outbox Pattern + DLQ | si |
| **Scalability** | DR-02 | Horizontal Scaling | ECS Fargate Auto-Scaling | si |
| **Scalability** | DR-02 | Load Leveling | RabbitMQ como buffer | si |
| **Scalability** | DR-02 | Stateless Services | JWT sin estado en servidor | si |
| **Security** | DR-04 | Authenticate Actors (JWT RS256) | User Service + API Gateway | si |
| **Security** | DR-04 | Authorize Actors (RBAC) | Cada servicio verifica roles | si |
| **Security** | DR-04 | Encrypt Data | TLS 1.3 + AES-256 at rest | si |
| **Security** | DR-04 | Ley 1581 compliance | Consentimiento explícito + anonimización | si |
| **Maintainability** | DR-05 | Modularidad (servicios) | 5 servicios independientes | si |
| **Maintainability** | DR-05 | CI/CD pipeline | GitHub Actions + Blue-Green | si |
| **Maintainability** | DR-05 | Rules in DB (no hardcoded) | AdaptationRule en PostgreSQL | si |
| **Interoperability** | DR-06 | REST + OpenAPI 3.0 | API Gateway + cada servicio | si |
| **Functional Suitability** | DR-07 | Rules Engine | AdaptiveEngine + AdaptationRule | si |

---

## 2. DESCRIPCIÓN DE COMPONENTES

### 2.1 Estructura de Repositorio (Monorepo)

```
plataforma-adaptativa/
├── apps/
│   ├── web-client/              # React.js 18 + TypeScript SPA
│   │   ├── src/
│   │   │   ├── components/      # Componentes reutilizables UI
│   │   │   ├── pages/           # Páginas por ruta
│   │   │   ├── hooks/           # Custom hooks (useAuth, useCourse, etc.)
│   │   │   ├── services/        # Clientes HTTP hacia API Gateway
│   │   │   └── store/           # Estado global (Redux/Zustand)
│   │   └── public/
│   │
│   ├── api-gateway/             # AWS API Gateway + Kong plugins
│   │   ├── config/              # Configuración de rutas y rate limiting
│   │   └── plugins/             # Plugin JWT validation
│   │
│   ├── user-service/            # Node.js + Express
│   │   ├── src/
│   │   │   ├── controllers/     # HTTP request handlers
│   │   │   ├── services/        # Lógica de negocio
│   │   │   ├── repositories/    # Acceso a datos (Prisma)
│   │   │   ├── domain/          # Entidades y reglas de dominio
│   │   │   └── events/          # Emisión de eventos a RabbitMQ
│   │   └── tests/
│   │
│   ├── course-service/          # Misma estructura que user-service
│   ├── assessment-service/      # + rules-engine/ para motor adaptativo
│   ├── collaboration-service/   # + websocket/ para chat en tiempo real
│   └── analytics-service/       # + consumers/ para eventos RabbitMQ
│
├── libs/
│   ├── common/                  # Utilidades compartidas
│   │   ├── logging/             # Logger estructurado JSON
│   │   ├── errors/              # Error handling centralizado
│   │   └── validation/          # Schemas Joi/Zod comunes
│   ├── events/                  # Contratos de eventos RabbitMQ
│   │   └── schemas/             # event.evaluation.completed.ts, etc.
│   └── auth/                    # Middleware JWT compartido
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

### 2.2 Diagrama de Componentes

ver el diagramaComponentes.svg

### 2.3 Justificación de Decisiones de Modularidad

**Monorepo con servicios independientes:** Facilita el desarrollo en equipo pequeño (3-4 personas), permite compartir librerías comunes (`libs/`) sin publicar paquetes npm privados, y mantiene un único punto de control de versiones. Cada servicio es desplegable de forma independiente mediante Docker.

**Separación Controller → Service → Repository → Domain:** Aplica el patrón Repository para desacoplar la lógica de negocio del mecanismo de persistencia, facilitando las pruebas unitarias con mocks y el cambio de tecnología de BD sin afectar la lógica de negocio.

**Librería `libs/events` con contratos de eventos:** Define los esquemas de mensajes RabbitMQ como tipos TypeScript compartidos entre publisher (Assessment Service) y consumers (Analytics, Collaboration). Previene inconsistencias de contrato entre servicios sin necesidad de un schema registry externo en el MVP.

### 2.4 Configuración de Infraestructura por Componente

| Componente | Servicio AWS | Configuración | Propósito de DR |
|-----------|-------------|---------------|-----------------|
| Frontend | S3 + CloudFront | CDN global, activos cacheados | DR-01 (performance) |
| API Gateway | AWS API GW + Kong ECS | Rate limit, JWT, routing | DR-04 (seguridad) |
| User Service | ECS Fargate | 2-6 instancias (2 AZs) | DR-03 (disponibilidad) |
| Course Service | ECS Fargate | 2-6 instancias (2 AZs) | DR-03 |
| Assessment Service | ECS Fargate | **3-10 instancias** (2 AZs) | DR-01 + DR-02 |
| Collaboration Service | ECS Fargate | 1-4 instancias (2 AZs) | DR-03 |
| Analytics Service | ECS Fargate | 1-4 instancias (2 AZs) | DR-05 |
| Base de datos | RDS PostgreSQL 16 Multi-AZ | db.t3.large, 100GB SSD, backups 7d | DR-03 |
| Caché | ElastiCache Redis 7 | cache.t3.medium, cluster mode | DR-01 |
| Message Broker | AWS MQ RabbitMQ | mq.m5.large, persistencia | DR-02 + DR-03 |
| Storage | S3 | Durabilidad 99.999999999% | DR-03 |
| Monitoreo | CloudWatch + X-Ray | Logs JSON, métricas, trazas | DR-05 |

---

## 3. DOCUMENTACIÓN DE LA ARQUITECTURA: VISTAS ARQUITECTÓNICAS (4+1)

### 3.1 Vista Lógica

ver la vista en diagramaLogico.svg

---

### 3.2 Vista de Desarrollo

ver el diagramaComponentes.svg

---

### 3.3 Vista de Procesos

#### Modelo de Concurrencia

El sistema opera bajo dos modos de comunicación:

- **Síncrono (camino crítico):** Peticiones HTTP del estudiante → API Gateway → Servicio → Respuesta directa. Optimizado para latencia mínima.
- **Asíncrono (procesamiento secundario):** Eventos publicados en RabbitMQ → Consumidos por Analytics y Collaboration. Fuera del camino crítico; no bloquean la respuesta al usuario.

#### Diagrama de Actividad

ver el diagramaActividad

#### Análisis de Rendimiento y Cuellos de Botella

| Operación | Latencia estimada | Componente limitante | Táctica de mitigación |
|-----------|-------------------|---------------------|----------------------|
| Login | ~150ms | bcrypt (deliberado) | Rate limiting anti-brute force |
| Carga de curso | ~80ms P95 | PostgreSQL + Redis | Redis caché TTL=3600s |
| Envío de evaluación | ~120ms P95 | Assessment Service (CPU) | Auto-scaling + Redis caché de reglas |
| Dashboard progreso | ~200ms P95 | Analytics queries | Read replica PostgreSQL |
| Post en foro | ~60ms P95 | PostgreSQL write | PgBouncer connection pooling |
| Reporte CSV docente | ~800ms | Analytics join queries | Read replica + índices optimizados |

**Cuello de botella principal identificado:** El Assessment Service durante picos de evaluación simultánea (DR-02). Mitigado con: mínimo 3 instancias ECS, auto-scaling hasta 10, Redis para caché de configuraciones, y RabbitMQ para desacoplar el procesamiento post-evaluación.

---

### 3.4 Vista Física

ver el diagramaDespliegue.svg

---

### 3.5 Vista de Escenarios (+1)

ver el diagramaCasos.svg

