# ADR-003: Motor Adaptativo con Rules Engine y Publish/Subscribe para Comunicacion Asincrona

**Estado:** Aceptado
**Fecha:** 26/03/2026
**Patrones adoptados:** Rules Engine (persistencia en BD), Publish/Subscribe, Outbox Pattern, Dead Letter Queue (DLQ)

---

## Contexto y Problema

El diferenciador central de la plataforma es su capacidad de adaptar el recorrido educativo de cada estudiante en funcion de su desempeño. Cuando un estudiante completa una evaluacion, el sistema debe: (1) calcular la calificacion, (2) evaluar si el estudiante supera el umbral de aprobacion, (3) determinar el siguiente paso del recorrido (material de refuerzo o contenido avanzado), (4) actualizar el progreso en el dashboard del estudiante, y (5) actualizar las metricas del docente.

Este flujo involucra multiples servicios. La pregunta clave es: ¿como orquestamos estas interacciones de forma que el estudiante reciba respuesta rapida (DR-01: ≤ 2 seg) sin crear acoplamiento fuerte entre servicios (DR-05) y sin bloquear la experiencia del usuario mientras se actualizan los sistemas secundarios?

Adicionalmente, el motor de adaptacion debe ser configurable por los docentes (RF-03), lo que significa que las reglas no pueden estar hardcodeadas en el codigo sino persistidas en la base de datos y evaluadas en tiempo de ejecucion.

---

## Drivers de Decision

- **DR-01:** Rendimiento ≤ 2 seg P95 — el estudiante no debe esperar mientras el sistema actualiza analytics (Prioridad: Alta)
- **DR-02:** Escalabilidad ≥ 5,000 usuarios concurrentes — picos de evaluaciones simultaneas (Prioridad: Alta)
- **DR-05:** Mantenibilidad — agregar nuevas reglas de adaptacion sin redesplegar el sistema (Prioridad: Media)
- **DR-07:** Motor Adaptativo configurable por docentes con reglas de umbral (Prioridad: Alta)
- **RF-03:** El sistema adapta el recorrido segun calificaciones y umbrales configurados (Prioridad: Alta)

---

## Alternativas Consideradas

### Alternativa 1: Reglas Hardcodeadas + Comunicacion Sincrona REST

**Descripcion:**
Las reglas de adaptacion se definen directamente en el codigo. Cuando el estudiante envia una evaluacion, el Assessment Service llama sincronamente (REST) al Analytics Service y al Course Service antes de responder al estudiante.

**Pros:**
- Simplicidad: no se requiere infraestructura adicional.
- Consistencia inmediata.

**Contras:**
- Latencia acumulada: llamadas en cadena pueden superar 2 segundos (viola DR-01).
- Si el Analytics Service cae, el estudiante no puede recibir su calificacion (viola DR-03).
- Las reglas hardcodeadas requieren redespliegue para cada cambio (viola DR-05 y RF-03).

---

### Alternativa 2: Rules Engine en BD + Publish/Subscribe ELEGIDA

**Descripcion:**
Las reglas de adaptacion se almacenan en PostgreSQL y se evaluan en tiempo de ejecucion. El flujo sincrono se limita a lo esencial: calcular calificacion + determinar el siguiente paso. Todo lo no critico (actualizar analytics, notificar al docente, evaluar candidatos a tutores) se delega a eventos asincronos mediante el patron Publish/Subscribe.

**Pros:**
- Latencia controlada: el camino critico es sincrono y ligero.
- Desacoplamiento entre servicios.
- Reglas configurables por docentes sin redespliegue.
- Publish/Subscribe actua como buffer durante picos.

**Contras:**
- Mayor complejidad: se requiere operar un broker de mensajeria.
- Consistencia eventual del dashboard.

---

### Alternativa 3: Motor de Machine Learning

**Descripcion:**
En lugar de reglas configuradas por docentes, el sistema usa modelos de ML entrenados con datos historicos.

**Contras:**
- Requiere grandes volumenes de datos que no existen en el MVP.
- Complejidad tecnica muy alta para un equipo de 3-4 personas.
- Los docentes no pueden interpretar ni configurar las decisiones del modelo.
- Completamente fuera del alcance del MVP.

---

## Decision

Adoptamos los siguientes patrones para implementar el motor adaptativo y la comunicacion entre servicios:

### Patron 1: Rules Engine con Persistencia en Base de Datos

**Descripcion:** Las reglas de adaptacion se modelan como registros en la tabla `adaptation_rules` de PostgreSQL con los atributos:
- `umbral_aprobacion` (0-100): calificacion minima para avanzar.
- `material_refuerzo_ids`: lista de IDs de materiales sugeridos si no se supera el umbral.
- `contenido_avanzado_id`: ID del contenido desbloqueado al superar el umbral.
- `intentos_maximos`: numero de intentos permitidos antes de escalar al docente.

El Assessment & Adaptive Service evalua estas reglas en tiempo de ejecucion. Las reglas se cachean con TTL=60s (patron Cache-Aside) para reducir la presion sobre PostgreSQL.

**Por que Rules Engine en BD:** RF-03 establece explicitamente que los docentes deben poder configurar las reglas sin redespliegue. Las reglas hardcodeadas violan directamente este requisito y DR-05.

---

### Patron 2: Publish/Subscribe

**Descripcion:** Tras responder sincronamente al estudiante, el Assessment & Adaptive Service publica los siguientes eventos en el broker:
- `evaluation.completed` → consumido por Analytics Service (actualiza dashboards y progreso).
- `evaluation.failed_threshold` → consumido por Collaboration Service (identifica candidatos a tutorias).

El broker de mensajeria (AWS MQ RabbitMQ) actua como intermediario desacoplando productores de consumidores.

**Por que Publish/Subscribe y no Point-to-Point (SQS):** El patron Publish/Subscribe permite que multiples consumidores (Analytics y Collaboration) reciban el mismo evento `evaluation.completed` sin que el productor (Assessment Service) necesite conocerlos. AWS SQS implementa el patron Point-to-Point (un mensaje, un consumidor), lo que requeriria publicar el evento a multiples colas individuales, creando acoplamiento. Por esta razon, AWS SQS fue descartado en favor de RabbitMQ que implementa Publish/Subscribe nativamente.

---

### Patron 3: Outbox Pattern

**Descripcion:** Antes de publicar el evento al broker, el Assessment Service persiste el evento en una tabla `outbox` de PostgreSQL en la misma transaccion que la escritura de `evaluation_attempts`. Un proceso background lee la tabla outbox y publica los eventos al broker, marcando cada uno como publicado.

**Por que Outbox Pattern:** Sin este patron, existe una ventana de fallo entre la escritura en PostgreSQL y la publicacion al broker: si el servicio falla despues de escribir en BD pero antes de publicar el evento, el evento se perderia permanentemente. El Outbox Pattern garantiza entrega **at-least-once** mediante la atomicidad de la transaccion de BD.

---

### Patron 4: Dead Letter Queue (DLQ)

**Descripcion:** Se configura una Dead Letter Queue en el broker con TTL=24 horas. Los mensajes que fallen en ser procesados 3 veces consecutivas se mueven automaticamente a la DLQ. Un operador puede revisar y reprocesar manualmente los mensajes en la DLQ.

**Por que DLQ:** Sin DLQ, los mensajes que fallen repetidamente bloquearian la cola principal, impidiendo el procesamiento de nuevos eventos. La DLQ los aisla para analisis sin afectar el flujo normal.

---

## Como cumple con los drivers:

| Driver | Como esta decision lo cumple |
|--------|------------------------------|
| DR-01 (Rendimiento ≤ 2 seg) | El camino critico es sincrono y ligero (Rules Engine + Cache-Aside); las operaciones secundarias son asincronas (Publish/Subscribe) y no bloquean la respuesta. |
| DR-02 (Escalabilidad 5,000 usuarios) | Publish/Subscribe actua como buffer durante picos de evaluaciones; los consumidores procesan eventos a su ritmo. |
| DR-05 (Mantenibilidad) | Rules Engine en BD: los docentes configuran reglas desde la UI sin intervencion del equipo tecnico ni redespliegue. Publish/Subscribe: nuevos consumidores se agregan sin modificar el Assessment Service. |
| DR-07 (Motor Adaptativo) | Rules Engine evalua reglas configurables (umbral, materiales de refuerzo, contenido avanzado) en tiempo de ejecucion. |
| RF-03 | Docentes pueden configurar umbrales y materiales desde la UI del curso. |

---

## Consecuencias

### Positivas:

1. **Experiencia de usuario fluida:** El estudiante recibe calificacion + siguiente paso en <2 segundos.
2. **Configurabilidad por docentes:** Umbrales y materiales se ajustan desde la UI sin intervencion tecnica.
3. **Resiliencia:** La caida del Analytics Service no impide completar evaluaciones; los eventos se persisten y procesan cuando el servicio se recupera.
4. **Buffer de carga:** Publish/Subscribe absorbe picos de evaluaciones simultaneas.

### Negativas (y mitigaciones):

1. **Consistencia eventual del dashboard**
   - **Riesgo:** El dashboard puede tardar 5-30 segundos en reflejar el resultado de la evaluacion.
   - **Mitigacion:** Mostrar indicador "Actualizando progreso..." en la UI. El estudiante ya tiene su calificacion y siguiente paso; el dashboard es informacion secundaria.

2. **Complejidad operacional del broker**
   - **Riesgo:** El broker requiere monitoreo, configuracion de DLQ y gestion de mensajes no procesados.
   - **Mitigacion:** Usar AWS MQ (servicio administrado) que elimina la gestion de infraestructura subyacente. Configurar alertas en CloudWatch cuando la profundidad de cola supere 5,000 mensajes.

3. **Dificultad de debugging en flujos distribuidos**
   - **Riesgo:** Rastrear el ciclo de vida de un evento a traves de multiples servicios es complejo.
   - **Mitigacion:** Implementar Correlation IDs en todos los eventos y logs (patron Distributed Tracing). Usar AWS X-Ray para visualizar el flujo completo.

---

## Notas Adicionales

- Si en el futuro se decide implementar un motor de ML, el patron Publish/Subscribe facilita la integracion: el servicio de ML puede suscribirse a `evaluation.completed` sin modificar el Assessment Service.
- Considerar Apache Kafka como alternativa al broker si el volumen de eventos supera los 100,000 mensajes por hora, por su mayor throughput y capacidad de replay de eventos.

---

