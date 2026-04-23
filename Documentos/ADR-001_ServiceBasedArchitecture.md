# ADR-001: Adoptar Arquitectura Orientada a Servicios (Service-Based Architecture)

**Estado:** Aceptado
**Fecha:** 24/03/2026
**Patron adoptado:** Service-Based Architecture

---

## Contexto y Problema

La Plataforma de Aprendizaje Adaptativo y Colaborativo debe soportar multiples dominios funcionales con logicas muy distintas: gestion de cursos, motor de aprendizaje adaptativo, evaluaciones, colaboracion entre pares, analitica y gestion de usuarios. Cada uno de estos dominios tiene patrones de carga diferentes; por ejemplo, el modulo de evaluaciones experimenta picos masivos al inicio y al final del semestre, mientras que la analitica opera de forma mas estable y puede tolerar cierta latencia.

El sistema debe estar disponible al 99.5% (DR-03) y soportar hasta 5,000 usuarios concurrentes (DR-02), lo que significa que no podemos permitir que la falla de un modulo derrumbe toda la plataforma. Al mismo tiempo, el equipo de desarrollo es pequeño (3-4 personas), lo que limita la complejidad operacional que podemos asumir en el MVP.

Necesitamos elegir un estilo arquitectural que balancee la capacidad de escalar selectivamente los componentes de mayor carga, la facilidad de mantenimiento a largo plazo (DR-05), la incorporacion de nuevas funcionalidades sin afectar el sistema existente, y el costo de operacion e infraestructura dentro del presupuesto disponible.

La decision es critica porque impacta todas las demas decisiones del proyecto: como se despliega el sistema, como se comunican los componentes, como se escala y como evoluciona en el tiempo.

---

## Drivers de Decision

- **DR-01:** Rendimiento ≤ 2 segundos P95 en operaciones principales (Prioridad: Alta)
- **DR-02:** Escalabilidad para ≥ 5,000 usuarios concurrentes (Prioridad: Alta)
- **DR-03:** Disponibilidad ≥ 99.5% mensual (Prioridad: Alta)
- **DR-05:** Mantenibilidad: nuevos modulos sin afectar el sistema existente (Prioridad: Media)
- **DR-06:** Interoperabilidad: APIs REST + OpenAPI 3.0 (Prioridad: Media)
- **Restriccion de equipo:** 3-4 desarrolladores, overhead operacional limitado

---

## Alternativas Consideradas

### Alternativa 1: Arquitectura Monolitica (Monolith)

**Descripcion:**
Todo el sistema se construye como una unica aplicacion desplegable. Un solo servidor de aplicaciones maneja todos los modulos. La base de datos es unica y compartida por todos los modulos.

**Pros:**
- Simplicidad de desarrollo y despliegue: un solo repositorio, un solo pipeline de CI/CD.
- Menor overhead operacional: no se requiere orquestacion de multiples servicios.
- Latencia de comunicacion interna minima: llamadas a funciones en lugar de llamadas de red.

**Contras:**
- Escalabilidad limitada: no se puede escalar selectivamente el modulo de evaluaciones sin escalar todo el monolito.
- Un bug en un modulo puede afectar a toda la plataforma (fallo en cascada).
- El despliegue de un cambio pequeño requiere redesplegar toda la aplicacion.

---

### Alternativa 2: Arquitectura de Microservicios (Microservices)

**Descripcion:**
Cada funcion del sistema se divide en servicios extremadamente granulares y autonomos. Cada microservicio tiene su propia base de datos y se despliega independientemente.

**Pros:**
- Maxima escalabilidad selectiva.
- Aislamiento de fallos extremo.
- Tecnologia heterogenea por servicio.

**Contras:**
- Altisima complejidad operacional: requiere service mesh, service discovery, distributed tracing, etc.
- El overhead de gestionar 10+ servicios con un equipo de 3-4 personas es inmanejable en el MVP.
- La comunicacion entre servicios agrega latencia de red.
- Costo de infraestructura significativamente mayor.

---

### Alternativa 3: Service-Based Architecture ELEGIDA

**Descripcion:**
El sistema se divide en un conjunto reducido de servicios de grano grueso (5 servicios), cada uno responsable de un dominio funcional bien delimitado. Los servicios comparten una base de datos relacional con schemas separados por dominio. Se expone un API Gateway como punto de entrada unico.

**Pros:**
- Balance optimo entre independencia y complejidad operacional.
- Escalabilidad selectiva por servicio segun la carga del dominio.
- Aislamiento de fallos a nivel de dominio.
- Complejidad operacional manejable por un equipo de 3-4 personas.

**Contras:**
- La base de datos compartida crea cierto acoplamiento entre servicios.
- Se requiere gestion de comunicacion entre servicios.

---

## Decision

Adoptamos **Service-Based Architecture** con los siguientes servicios principales:

1. **User Service** — Gestion de usuarios, autenticacion y autorizacion (JWT RS256 + RBAC).
2. **Course Service** — Gestion de cursos, modulos, lecciones, materiales y reglas de adaptacion.
3. **Assessment & Adaptive Service** — Evaluaciones, calificacion automatica y motor adaptativo (Rules Engine).
4. **Collaboration Service** — Foros de discusion, grupos de estudio y tutorias entre pares.
5. **Analytics Service** — Seguimiento del progreso, dashboards y reportes de desempeño.

**Patrones arquitectonicos que implementa esta decision:**

| Patron | Rol en la decision |
|--------|--------------------|
| **Service-Based Architecture** | Estilo arquitectural principal: 5 servicios de grano grueso |
| **API Gateway** | Punto de entrada unico; centraliza autenticacion y rate limiting |
| **Publish/Subscribe** | Comunicacion asincrona entre servicios (Assessment → Analytics, Collaboration) |
| **Schemas separados por servicio** | Aislamiento logico de datos en BD compartida |
| **Horizontal Scaling + Stateless** | Escalado independiente por servicio; estado en cache centralizada |


---

## Justificacion

Elegimos Service-Based sobre el **Monolito** porque necesitamos escalar selectivamente los componentes de mayor carga. El Assessment & Adaptive Service recibira picos masivos al inicio y final del semestre, mientras que el Analytics Service puede tolerar cierta latencia. Con un monolito, tendriamos que escalar toda la aplicacion, lo que no cumple con DR-02.

Elegimos Service-Based sobre **Microservicios** porque nuestro equipo es de 3-4 personas y el overhead operacional de gestionar 10+ microservicios excede nuestra capacidad para el MVP. La Service-Based Architecture representa el punto optimo en el espectro monolito-microservicios para nuestro contexto.

### Como cumple con los drivers:

| Driver | Como esta decision lo cumple |
|--------|------------------------------|
| DR-01 (Rendimiento ≤ 2 seg) | El API Gateway con patrones Cache-Aside reduce la latencia; la granularidad de servicios permite optimizar cada uno independientemente. |
| DR-02 (Escalabilidad 5,000 usuarios) | Cada servicio escala horizontalmente de forma independiente; el Assessment Service puede escalar en picos sin afectar otros servicios. |
| DR-03 (Disponibilidad 99.5%) | El aislamiento de servicios evita fallos en cascada; el API Gateway con Circuit Breaker y health checks redirige trafico ante fallos. |
| DR-04 (Seguridad) | El API Gateway centraliza la autenticacion JWT RS256 y RBAC. |
| DR-05 (Mantenibilidad) | Los servicios son independientes; agregar un nuevo modulo implica crear un nuevo servicio sin modificar los existentes. |
| DR-06 (Interoperabilidad) | Cada servicio expone APIs REST documentadas con OpenAPI 3.0 a traves del API Gateway. |

---

## Consecuencias

### Positivas:

1. **Escalabilidad selectiva:** El Assessment & Adaptive Service puede escalar a 10 instancias durante periodos de evaluacion sin escalar el resto de servicios.
2. **Aislamiento de fallos:** Una falla en el Collaboration Service no impide que los estudiantes accedan a contenidos y evaluaciones.
3. **Despliegues independientes:** Un bug fix en el Analytics Service puede desplegarse sin afectar a los demas servicios.
4. **Evolucion incremental:** Nuevas funcionalidades pueden agregarse como nuevos servicios sin modificar los existentes.

### Negativas (y mitigaciones):

1. **Base de datos compartida crea acoplamiento**
   - **Riesgo:** Cambios en el schema de un dominio pueden afectar otros servicios.
   - **Mitigacion:** Usar schemas separados por servicio. Politica "no cross-schema queries": las dependencias de datos entre dominios se resuelven solo mediante eventos Publish/Subscribe o llamadas REST.

2. **Complejidad de comunicacion entre servicios**
   - **Riesgo:** Las llamadas entre servicios agregan latencia y puntos de fallo.
   - **Mitigacion:** Usar comunicacion asincrona (patron Publish/Subscribe) para operaciones no criticas. Solo usar llamadas sincronas REST en el camino critico del usuario.

3. **Consistencia eventual en operaciones distribuidas**
   - **Riesgo:** Al completar una evaluacion, el Analytics Service puede reflejar datos con retraso.
   - **Mitigacion:** Usar el patron Outbox para garantizar entrega de eventos. Los dashboards indican cuando fueron actualizados por ultima vez.

---

## Notas Adicionales

- Esta decision se revisara cuando el sistema supere los 10,000 usuarios concurrentes, momento en el que algunos servicios podrian subdividirse.
- El Assessment & Adaptive Service es candidato a subdividirse si el motor adaptativo requiere computo intensivo de ML, separandose en un servicio dedicado de inferencia.

---
