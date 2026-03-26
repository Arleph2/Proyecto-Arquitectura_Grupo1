# ADR-001: Adoptar Arquitectura Orientada a Servicios (Service-Based Architecture)

**Estado:** Aceptado  
**Fecha:** 22/03/2026  
**Decisores:** [Nombre 1], [Nombre 2], [Nombre 3], [Nombre 4]  
**Relacionado con:** DR-01, DR-02, DR-03, DR-05, RNF-01, RNF-03, RNF-05  
**Grupo:** [Número de grupo]

---

## Contexto y Problema

La Plataforma de Aprendizaje Adaptativo y Colaborativo debe soportar múltiples dominios funcionales con lógicas muy distintas: gestión de cursos, motor de aprendizaje adaptativo, evaluaciones, colaboración entre pares, analítica y gestión de usuarios. Cada uno de estos dominios tiene patrones de carga diferentes; por ejemplo, el módulo de evaluaciones experimenta picos masivos al inicio y al final del semestre, mientras que la analítica opera de forma más estable y puede tolerar cierta latencia.

El sistema debe estar disponible al 99.5% (DR-03) y soportar hasta 5,000 usuarios concurrentes (DR-02), lo que significa que no podemos permitir que la falla de un módulo derrumbe toda la plataforma. Al mismo tiempo, el equipo de desarrollo es pequeño (3-4 personas), lo que limita la complejidad operacional que podemos asumir en el MVP.

Necesitamos elegir un estilo arquitectural que balancee la capacidad de escalar selectivamente los componentes de mayor carga, la facilidad de mantenimiento a largo plazo (DR-05), la incorporación de nuevas funcionalidades sin afectar el sistema existente, y el costo de operación e infraestructura dentro del presupuesto disponible.

La decisión es crítica porque impacta todas las demás decisiones del proyecto: cómo se despliega el sistema, cómo se comunican los componentes, cómo se escala y cómo evoluciona en el tiempo.

---

## Drivers de Decisión

- **DR-01:** Rendimiento ≤ 2 segundos P95 en operaciones principales (Prioridad: Alta)
- **DR-02:** Escalabilidad para ≥ 5,000 usuarios concurrentes (Prioridad: Alta)
- **DR-03:** Disponibilidad ≥ 99.5% mensual (Prioridad: Alta)
- **DR-05:** Mantenibilidad: nuevos módulos sin afectar el sistema existente (Prioridad: Media)
- **DR-06:** Interoperabilidad: APIs REST + OpenAPI 3.0 (Prioridad: Media)
- **Restricción de equipo:** 3-4 desarrolladores, overhead operacional limitado (Hard constraint)

---

## Alternativas Consideradas

### Alternativa 1: Arquitectura Monolítica (Monolith)

**Descripción:**  
Todo el sistema se construye como una única aplicación desplegable. Un solo servidor de aplicaciones maneja todos los módulos: cursos, evaluaciones, motor adaptativo, foros, analítica y usuarios. La base de datos es única y compartida por todos los módulos.

**Pros:**
- ✅ Simplicidad de desarrollo y despliegue: un solo repositorio, un solo pipeline de CI/CD.
- ✅ Menor overhead operacional: no se requiere orquestación de múltiples servicios.
- ✅ Latencia de comunicación interna mínima: llamadas a funciones en lugar de llamadas de red.
- ✅ Más fácil de depurar: el stack trace es completo y en un solo lugar.

**Contras:**
- ❌ Escalabilidad limitada: no se puede escalar selectivamente el módulo de evaluaciones sin escalar todo el monolito.
- ❌ Un bug en un módulo puede afectar a toda la plataforma (fallo en cascada).
- ❌ El despliegue de un cambio pequeño requiere redesplegar toda la aplicación.
- ❌ A medida que crece el sistema, la base de código se vuelve más difícil de mantener (big ball of mud).

---

### Alternativa 2: Arquitectura de Microservicios (Microservices)

**Descripción:**  
Cada función del sistema se divide en servicios extremadamente granulares y autónomos: servicio de autenticación, servicio de cursos, servicio de lecciones, servicio de evaluaciones, servicio de adaptación, servicio de foros, servicio de notificaciones, servicio de analítica, etc. Cada microservicio tiene su propia base de datos y se despliega independientemente.

**Pros:**
- ✅ Máxima escalabilidad selectiva: cada microservicio escala de forma independiente.
- ✅ Aislamiento de fallos extremo: la caída de un servicio no afecta los demás.
- ✅ Tecnología heterogénea: cada servicio puede usar el lenguaje y base de datos más adecuados.
- ✅ Despliegues completamente independientes.

**Contras:**
- ❌ Altísima complejidad operacional: requiere service mesh, service discovery, distributed tracing, API gateway, etc.
- ❌ El overhead de gestionar 10+ servicios con un equipo de 3-4 personas es inmanejable en el MVP.
- ❌ La comunicación entre servicios agrega latencia de red que puede violar DR-01 (≤ 2 seg).
- ❌ La consistencia de datos entre servicios es compleja (eventual consistency, sagas pattern).
- ❌ Costo de infraestructura significativamente mayor.

---

### Alternativa 3: Service-Based Architecture (Arquitectura Orientada a Servicios)

**Descripción:**  
El sistema se divide en un conjunto reducido de servicios de grano grueso (4-6 servicios), cada uno responsable de un dominio funcional bien delimitado. Los servicios comparten una base de datos relacional con schemas separados por dominio. Se expone un API Gateway como punto de entrada único para los clientes. El frontend es una SPA que consume los servicios a través del gateway.

**Pros:**
- ✅ Balance óptimo entre independencia y complejidad operacional.
- ✅ Escalabilidad selectiva por servicio según la carga del dominio.
- ✅ Aislamiento de fallos a nivel de dominio: la caída del servicio de analítica no afecta el motor adaptativo.
- ✅ Complejidad operacional manejable por un equipo de 3-4 personas.
- ✅ Despliegues independientes por servicio.
- ✅ API Gateway centraliza autenticación, rate limiting y enrutamiento.

**Contras:**
- ❌ La base de datos compartida crea cierto acoplamiento entre servicios.
- ❌ Se requiere gestión de comunicación entre servicios (REST síncrono + mensajería asíncrona).
- ❌ Más complejo que un monolito, aunque significativamente menos que microservicios.

---

## Decisión

Adoptamos **Service-Based Architecture** con los siguientes servicios principales:

1. **User Service** - Gestión de usuarios, autenticación y autorización (JWT + RBAC).
2. **Course Service** - Gestión de cursos, módulos, lecciones y materiales educativos.
3. **Assessment & Adaptive Service** - Evaluaciones, calificación automática y motor adaptativo.
4. **Collaboration Service** - Foros de discusión, grupos de estudio y tutorías entre pares.
5. **Analytics Service** - Seguimiento del progreso, dashboards y reportes de desempeño.

Todos los servicios se comunican a través de un **API Gateway** (Kong o AWS API Gateway) que centraliza autenticación, enrutamiento y rate limiting. Los servicios comparten una base de datos PostgreSQL con **schemas separados por dominio** para reducir el acoplamiento. Para operaciones asíncronas (notificaciones, actualización de métricas de analítica), se usa un message broker (RabbitMQ o AWS SQS). El frontend es una SPA (React.js) que consume todos los servicios a través del API Gateway.

---

## Justificación

### Por qué esta opción (y no las otras):

Elegimos Service-Based sobre el **Monolito** porque necesitamos escalar selectivamente los componentes de mayor carga. El Assessment & Adaptive Service recibirá picos masivos de carga al inicio y final del semestre (todos los estudiantes tomando evaluaciones al mismo tiempo), mientras que el Analytics Service puede tolerar cierta latencia en sus cálculos. Con un monolito, tendríamos que escalar toda la aplicación para atender el pico de evaluaciones, lo que no es costo-eficiente y no cumple con DR-02.

Elegimos Service-Based sobre **Microservicios** porque nuestro equipo es de 3-4 personas y el overhead operacional de gestionar 10+ microservicios (service mesh, distributed tracing, múltiples bases de datos, sagas para transacciones distribuidas) excede nuestra capacidad para el MVP. Los microservicios también implicarían un costo de infraestructura significativamente mayor. Con 4-6 servicios de grano grueso, mantenemos la capacidad de despliegue independiente y escalabilidad selectiva sin la complejidad extrema de los microservicios.

La Service-Based Architecture representa el punto óptimo en el espectro monolito-microservicios para nuestro contexto: equipo pequeño, presupuesto ajustado, alta disponibilidad requerida y necesidad de evolución futura del sistema.

### Cómo cumple con los drivers:

| Driver | Cómo esta decisión lo cumple |
|--------|------------------------------|
| DR-01 (Rendimiento ≤ 2 seg) | El API Gateway con caching reduce la latencia; la granularidad de servicios permite optimizar cada uno independientemente. |
| DR-02 (Escalabilidad 5,000 usuarios) | Cada servicio escala horizontalmente de forma independiente; el Assessment Service puede escalar en picos sin afectar otros servicios. |
| DR-03 (Disponibilidad 99.5%) | El aislamiento de servicios evita fallos en cascada; el API Gateway con health checks redirige tráfico ante fallos. |
| DR-04 (Seguridad) | El API Gateway centraliza la autenticación JWT y verificación de roles en un único punto. |
| DR-05 (Mantenibilidad) | Los servicios son independientes; agregar un nuevo módulo implica crear un nuevo servicio sin modificar los existentes. |
| DR-06 (Interoperabilidad) | Cada servicio expone APIs REST documentadas con OpenAPI 3.0 a través del API Gateway. |

---

## Consecuencias

### ✅ Positivas:

1. **Escalabilidad selectiva:** El Assessment & Adaptive Service puede escalar a 10 instancias durante períodos de evaluación sin escalar el resto de servicios, reduciendo costos en momentos de baja carga.
2. **Aislamiento de fallos:** Una falla en el Collaboration Service (foros) no impide que los estudiantes accedan a contenidos y evaluaciones.
3. **Despliegues independientes:** Un bug fix en el Analytics Service puede desplegarse sin afectar a los demás servicios ni requerir ventana de mantenimiento extendida.
4. **Evolución incremental:** Nuevas funcionalidades (ej. videollamadas, IA generativa) pueden agregarse como nuevos servicios sin modificar los existentes.

### ⚠️ Negativas (y mitigaciones):

1. **Base de datos compartida crea acoplamiento**
   - **Riesgo:** Cambios en el schema de un dominio pueden afectar otros servicios que comparten la misma base de datos.
   - **Mitigación:** Usar schemas separados por servicio en PostgreSQL (ej. `schema_users`, `schema_courses`). Las migraciones de cada schema son responsabilidad exclusiva del servicio propietario.

2. **Complejidad de comunicación entre servicios**
   - **Riesgo:** Las llamadas entre servicios (ej. el Analytics Service consultando datos del Course Service) agregan latencia y puntos de fallo.
   - **Mitigación:** Usar comunicación asíncrona mediante eventos (RabbitMQ) para operaciones que no requieren respuesta inmediata. Solo usar llamadas síncronas REST para operaciones en el camino crítico del usuario.

3. **Consistencia de datos eventual en operaciones distribuidas**
   - **Riesgo:** Al completar una evaluación, el Assessment Service debe actualizar el Analytics Service; si este proceso falla, los dashboards pueden mostrar datos desactualizados.
   - **Mitigación:** Usar el patrón Outbox para garantizar que los eventos se publiquen de forma confiable. Los dashboards del estudiante indican cuándo fueron actualizados por última vez.

---

## Alternativas Descartadas (Detalle)

### Por qué se descartó la Arquitectura Monolítica:

El monolito fue descartado principalmente porque no cumple con DR-02 (escalabilidad para 5,000 usuarios concurrentes) de forma costo-eficiente. Al inicio y final del semestre, el módulo de evaluaciones experimenta picos que pueden ser 10x el tráfico normal. Con un monolito, tendríamos que escalar toda la aplicación, incluyendo módulos que no están bajo carga (ej. analítica, foros), desperdiciando recursos.

Adicionalmente, el monolito viola DR-03 en contextos de alta carga: un bug en el módulo de evaluaciones podría tumbar toda la plataforma, dejando a estudiantes sin acceso a contenidos ni a sus dashboards de progreso.

**Cuándo sería mejor:**
- Si el equipo fuera de 1-2 personas con tiempo muy limitado y el MVP tuviera horizonte de menos de 6 meses.
- Si la carga esperada fuera menor a 500 usuarios concurrentes con patrones uniformes.

### Por qué se descartaron los Microservicios:

Los microservicios fueron descartados porque el overhead operacional es incompatible con nuestro equipo de 3-4 personas. Gestionar 10+ servicios requiere infraestructura especializada (service mesh tipo Istio, distributed tracing con Jaeger/Zipkin, múltiples bases de datos, configuración de red compleja) que representaría más tiempo de DevOps que de desarrollo de funcionalidades.

El costo de infraestructura también es significativamente mayor: cada microservicio requiere al menos una instancia dedicada, lo que podría duplicar o triplicar el gasto mensual en cloud.

**Cuándo sería mejor:**
- Si el equipo superara los 10+ desarrolladores organizados en squads por dominio.
- Si hubiera requisitos de tecnologías heterogéneas por dominio (ej. el motor adaptativo en Python, el servicio de pagos en Go, el frontend en Node.js).
- Si la escala del sistema superara las 50,000 transacciones por segundo.

---

## Validación

- [x] Cumple con DR-01 (Rendimiento ≤ 2 seg): El API Gateway con caching + servicios optimizados por dominio permiten cumplir el SLA de latencia.
- [x] Cumple con DR-02 (Escalabilidad 5,000 usuarios): Auto-scaling independiente por servicio permite absorber picos sin escalar toda la plataforma.
- [x] Cumple con DR-03 (Disponibilidad 99.5%): El aislamiento de servicios y el API Gateway con health checks garantizan continuidad ante fallos parciales.
- [x] Cumple con DR-04 (Seguridad): El API Gateway centraliza autenticación JWT y verificación RBAC.
- [x] Cumple con DR-05 (Mantenibilidad): Servicios independientes permiten agregar nuevos módulos sin afectar los existentes.
- [x] Cumple con DR-06 (Interoperabilidad): APIs REST + OpenAPI 3.0 expuestas a través del API Gateway.

---

## Notas Adicionales

- Esta decisión se revisará cuando el sistema supere los 10,000 usuarios concurrentes, momento en el que algunos servicios podrían subdividirse en microservicios.
- El Assessment & Adaptive Service es candidato a subdividirse en el futuro si el motor adaptativo requiere cómputo intensivo (ej. ML models), separándose en un servicio dedicado de inferencia.

---

## Referencias

- [SRS] Software Requirements Specification - Plataforma de Aprendizaje Adaptativo y Colaborativo
- Richards, M. & Ford, N. (2020). *Fundamentals of Software Architecture*. O'Reilly Media. Cap. 13: Service-Based Architecture.
- Enunciado del proyecto - Arquitectura de Software, Pontificia Universidad Javeriana, 2026.

---

**Estado final:** ACEPTADO ✅

**Firmas del equipo:**
- [Nombre 1]: __________ - Fecha: ___/___/___
- [Nombre 2]: __________ - Fecha: ___/___/___
- [Nombre 3]: __________ - Fecha: ___/___/___
- [Nombre 4]: __________ - Fecha: ___/___/___
