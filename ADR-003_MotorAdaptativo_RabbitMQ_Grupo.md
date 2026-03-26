# ADR-003: Implementar Motor Adaptativo con Reglas Configurables y Mensajería Asíncrona (RabbitMQ)

---

## Contexto y Problema

El diferenciador central de la plataforma es su capacidad de adaptar el recorrido educativo de cada estudiante en función de su desempeño. Cuando un estudiante completa una evaluación, el sistema debe: (1) calcular la calificación, (2) evaluar si el estudiante supera el umbral de aprobación, (3) determinar el siguiente paso del recorrido (material de refuerzo o contenido avanzado), (4) actualizar el progreso en el dashboard del estudiante, y (5) actualizar las métricas del docente.

Este flujo involucra múltiples servicios: el Assessment & Adaptive Service procesa la evaluación, el Course Service provee los metadatos de los contenidos, el Analytics Service actualiza el progreso y el Collaboration Service puede recibir alertas para activar tutorías entre pares. La pregunta clave es: ¿cómo orquestamos estas interacciones de forma que el estudiante reciba respuesta rápida (DR-01: ≤ 2 seg) sin crear acoplamiento fuerte entre servicios (DR-05) y sin bloquear la experiencia del usuario mientras se actualizan los sistemas secundarios?

Adicionalmente, el motor de adaptación debe ser configurable por los docentes (RF-03), lo que significa que las reglas no pueden estar hardcodeadas en el código sino persistidas en la base de datos y evaluadas en tiempo de ejecución. La decisión sobre cómo diseñar este motor y la comunicación entre servicios impacta directamente la escalabilidad bajo picos de carga (5,000 usuarios simultáneos enviando evaluaciones al final del semestre).

---

## Drivers de Decisión

- **DR-01:** Rendimiento ≤ 2 seg P95 - el estudiante no debe esperar mientras el sistema actualiza analytics (Prioridad: Alta)
- **DR-02:** Escalabilidad ≥ 5,000 usuarios concurrentes - picos de evaluaciones simultáneas (Prioridad: Alta)
- **DR-05:** Mantenibilidad - agregar nuevas reglas de adaptación sin redesplegar el sistema (Prioridad: Media)
- **DR-07:** Motor Adaptativo configurable por docentes con reglas de umbral (Prioridad: Alta)
- **RF-03:** El sistema adapta el recorrido según calificaciones y umbrales configurados (Prioridad: Alta)

---

## Alternativas Consideradas

### Alternativa 1: Motor Adaptativo con Reglas Hardcodeadas y Comunicación Síncrona REST

**Descripción:**  
Las reglas de adaptación se definen directamente en el código del servicio. Cuando el estudiante envía una evaluación, el Assessment Service llama síncronamente (REST) al Analytics Service y al Course Service antes de responder al estudiante. El resultado de la adaptación se computa en el mismo hilo de la petición.

**Pros:**
- Simplicidad: no se requiere infraestructura adicional (no hay message broker).
- Consistencia inmediata: cuando el estudiante recibe la respuesta, todos los sistemas están actualizados.
- Trazabilidad simple: el flujo es lineal y fácil de depurar.

**Contras:**
- Latencia acumulada: si el Assessment Service llama a 3 servicios en secuencia, la latencia total puede superar los 2 segundos bajo carga (viola DR-01).
- Acoplamiento temporal fuerte: si el Analytics Service cae, el estudiante no puede recibir su calificación (viola DR-03).
- Las reglas hardcodeadas requieren redespliegue para cada cambio de configuración (viola DR-05 y RF-03).
- No escala bien bajo picos: 5,000 peticiones simultáneas generan un fan-out de llamadas REST que puede saturar los servicios dependientes.

---

### Alternativa 2: Motor Adaptativo con Reglas en Base de Datos y Mensajería Asíncrona (RabbitMQ)

**Descripción:**  
Las reglas de adaptación se almacenan en PostgreSQL y se evalúan en tiempo de ejecución. El flujo síncrono se limita a lo esencial: calcular calificación + determinar el siguiente paso del recorrido (respuesta al estudiante en < 2 seg). Todo lo que no es crítico para la respuesta inmediata (actualizar analytics, notificar al docente, evaluar candidatos a tutores) se delega a eventos asíncronos mediante RabbitMQ. Los consumidores de los eventos procesan la información en background.

**Pros:**
- Latencia controlada: el camino crítico (calificación + siguiente paso) se resuelve síncronamente; el resto es asíncrono.
- Desacoplamiento entre servicios: si el Analytics Service cae, el estudiante igual recibe su calificación; los datos se actualizarán cuando el servicio se recupere.
- Reglas configurables por docentes sin redespliegue: las reglas están en la base de datos y se leen en tiempo de ejecución.
- Escalabilidad del motor: RabbitMQ actúa como buffer durante picos; los consumidores procesan a su ritmo.
-  Resiliencia: RabbitMQ persiste los mensajes, garantizando que los eventos se procesen aunque un consumidor falle temporalmente.

**Contras:**
- Mayor complejidad: se requiere operar RabbitMQ (o usar un servicio administrado).
- Consistencia eventual: el dashboard del estudiante puede tardar unos segundos en reflejar el resultado de la evaluación (aunque el estudiante ya conoce su calificación).
- Debugging más complejo: el flujo distribuido requiere correlación de logs para rastrear el ciclo de vida de un evento.

---

### Alternativa 3: Motor Adaptativo basado en Machine Learning (ML)

**Descripción:**  
En lugar de reglas configuradas por docentes, el sistema usa modelos de Machine Learning entrenados con datos históricos de aprendizaje para predecir el siguiente paso óptimo para cada estudiante (sistemas como DKT - Deep Knowledge Tracing).

**Pros:**
- Adaptación personalizada más precisa que las reglas manuales.
- Puede descubrir patrones no evidentes para los docentes.
- El modelo mejora con el tiempo a medida que acumula datos.

**Contras:**
- Requiere grandes volúmenes de datos de entrenamiento que no existen en el MVP.
- Complejidad técnica muy alta: requiere infraestructura de ML (entrenamiento, serving, monitoreo de drift).
- Los docentes no pueden interpretar ni configurar las decisiones del modelo (caja negra).
-  Completamente fuera del alcance del MVP con un equipo de 3-4 personas.

---

## Decisión

Adoptamos el **Motor Adaptativo con Reglas Configurables en Base de Datos** y **RabbitMQ como message broker para comunicación asíncrona entre servicios**.

**Motor Adaptativo:**  
Las reglas de adaptación se modelan con los siguientes atributos por lección/módulo:
- `umbral_aprobacion` (0-100): calificación mínima para avanzar.
- `material_refuerzo_ids`: lista de IDs de materiales sugeridos si no se supera el umbral.
- `contenido_avanzado_id`: ID del contenido desbloqueado al superar el umbral.
- `intentos_maximos`: número de intentos permitidos antes de escalar al docente.

El `Assessment & Adaptive Service` evalúa estas reglas en tiempo de ejecución contra los resultados de la evaluación. La respuesta al estudiante incluye la calificación y el siguiente paso del recorrido (síncronamente, en < 2 seg).

**Flujo de eventos asíncronos con RabbitMQ:**  
Tras responder al estudiante, el Assessment & Adaptive Service publica los siguientes eventos en RabbitMQ:
- `evaluation.completed` → consumido por Analytics Service para actualizar dashboards.
- `evaluation.failed_threshold` → consumido por Collaboration Service para identificar candidatos a tutorías.
- `evaluation.completed` → consumido por el Notification Service para alertar al docente.

**Infraestructura:** AWS MQ (RabbitMQ administrado) o CloudAMQP para reducir overhead operacional.

---

## Justificación

### Por qué esta opción (y no las otras):

Elegimos el motor basado en reglas configurables sobre el modelo de reglas hardcodeadas porque RF-03 establece explícitamente que los docentes deben poder configurar las reglas de adaptación por curso y módulo. Las reglas hardcodeadas son una violación directa de este requisito y de DR-05 (mantenibilidad), ya que cada cambio de configuración requeriría un redespliegue del sistema.

Elegimos la mensajería asíncrona sobre la comunicación REST síncrona porque el camino crítico de la experiencia del estudiante (recibir calificación + saber qué hacer a continuación) no requiere que los sistemas secundarios (analytics, notificaciones) estén actualizados en ese instante. Al separar el camino crítico del secundario, garantizamos que la respuesta al estudiante llegue en < 2 segundos (DR-01) incluso cuando el sistema está bajo carga de 5,000 usuarios concurrentes.

La mensajería asíncrona también mejora la resiliencia: si el Analytics Service cae durante un período de evaluaciones masivas, los eventos se acumulan en la cola de RabbitMQ y se procesan cuando el servicio se recupera, sin afectar la experiencia del estudiante (DR-03).

Descartamos el motor de ML porque no existe la infraestructura de datos ni la capacidad técnica para implementarlo en el MVP con un equipo de 3-4 personas, y porque los docentes necesitan entender y controlar las reglas de adaptación, no delegar esa decisión a un modelo de caja negra.

### Cómo cumple con los drivers:

| Driver | Cómo esta decisión lo cumple |
|--------|------------------------------|
| DR-01 (Rendimiento ≤ 2 seg) | El camino crítico es síncrono y ligero (evaluación de reglas en PostgreSQL); todo lo secundario es asíncrono y no bloquea la respuesta al usuario. |
| DR-02 (Escalabilidad 5,000 usuarios) | RabbitMQ actúa como buffer durante picos de evaluaciones; los consumidores procesan los eventos a su ritmo sin saturar los servicios dependientes. |
| DR-05 (Mantenibilidad) | Las reglas de adaptación están en la base de datos; el docente las configura desde la UI sin intervención del equipo técnico ni redespliegue. |
| DR-07 (Motor Adaptativo) | El motor evalúa reglas configurables por docente (umbral, materiales de refuerzo, contenido avanzado) en tiempo de ejecución. |

---

## Consecuencias

### Positivas:

1. **Experiencia de usuario fluida bajo alta carga:** El estudiante recibe su calificación y el siguiente paso del recorrido en < 2 segundos, incluso cuando 5,000 usuarios envían evaluaciones simultáneamente.
2. **Configurabilidad por docentes:** Los docentes pueden ajustar umbrales, materiales de refuerzo y contenidos avanzados desde la interfaz de administración de cursos, sin depender del equipo técnico.
3. **Resiliencia del sistema:** La caída temporal del Analytics Service o del Collaboration Service no impide que los estudiantes completen evaluaciones ni reciban retroalimentación inmediata.
4. **Buffer de carga con RabbitMQ:** Durante picos de evaluaciones (inicio/fin de semestre), RabbitMQ absorbe el volumen de eventos y los distribuye en el tiempo, evitando saturación de los servicios consumidores.

### Negativas (y mitigaciones):

1. **Consistencia eventual del dashboard de progreso**
   - **Riesgo:** El dashboard del estudiante puede tardar de 5 a 30 segundos en reflejar el resultado de la evaluación tras recibirlo.
   - **Mitigación:** Mostrar en la UI un indicador de "actualizando progreso..." que desaparece cuando el dashboard se actualiza. En el camino crítico (respuesta inmediata de la evaluación), el estudiante ya tiene su calificación y el siguiente paso; el dashboard es información secundaria.

2. **Complejidad operacional de RabbitMQ**
   - **Riesgo:** RabbitMQ es una pieza de infraestructura adicional que requiere monitoreo, configuración de dead letter queues y gestión de mensajes no procesados.
   - **Mitigación:** Usar AWS MQ (RabbitMQ administrado) que elimina la gestión de la infraestructura subyacente. Configurar dead letter queues y alertas automáticas en CloudWatch para mensajes que fallen repetidamente.

3. **Dificultad de debugging en flujos distribuidos**
   - **Riesgo:** Rastrear el ciclo de vida de un evento desde que se publica hasta que se procesa por todos los consumidores es más complejo que en un flujo síncrono.
   - **Mitigación:** Implementar correlation IDs en todos los eventos y logs. Usar AWS X-Ray o un sistema de distributed tracing para visualizar el flujo completo de una evaluación a través de los servicios.

---

## Alternativas Descartadas (Detalle)

### Por qué se descartó la comunicación REST síncrona:

La comunicación síncrona fue descartada porque en un pico de 5,000 evaluaciones simultáneas, cada petición de evaluación dispararía llamadas síncronas en cadena: Assessment → Analytics → Collaboration → Notification. El tiempo de respuesta total se acumularía hasta violar DR-01 (≤ 2 seg) y la falla de cualquier servicio en la cadena propagaría el error al estudiante, violando DR-03.

**Cuándo sería mejor:**
- Si el volumen de usuarios concurrentes fuera bajo (< 100 usuarios simultáneos) y todos los servicios dependientes fueran altamente confiables.
- En flujos donde la consistencia inmediata es absolutamente crítica (ej. transacciones financieras), no el caso para actualizaciones de dashboards.

### Por qué se descartó el Motor de ML:

El motor de ML fue descartado porque requiere un volumen mínimo de datos históricos de aprendizaje para entrenar modelos efectivos (se estiman al menos 10,000 sesiones de aprendizaje completas), que no existen en el MVP. Adicionalmente, la complejidad de implementar, desplegar y monitorear modelos de ML (infraestructura de entrenamiento, serving, monitoreo de drift) excede la capacidad del equipo de 3-4 personas en el tiempo disponible.

**Cuándo sería mejor:**
- En la versión 2.0 o 3.0 de la plataforma, cuando se hayan acumulado suficientes datos de aprendizaje y el equipo haya crecido para incluir un perfil de Data Science/ML.
- Si el presupuesto permitiera usar servicios de ML administrados como AWS SageMaker con modelos preentrenados para recomendación educativa.

---

## Validación

- [x] Cumple con DR-01 (Rendimiento ≤ 2 seg): Camino crítico síncrono y ligero; operaciones secundarias asíncronas no bloquean la respuesta al usuario.
- [x] Cumple con DR-02 (Escalabilidad 5,000 usuarios): RabbitMQ como buffer de picos; consumidores escalan independientemente.
- [x] Cumple con DR-05 (Mantenibilidad): Reglas en base de datos configurables sin redespliegue; eventos desacoplan servicios.
- [x] Cumple con DR-07 (Motor Adaptativo): Reglas configurables por docente (umbral, refuerzo, avanzado) evaluadas en tiempo de ejecución.
- [x] Cumple con RF-03: Docentes pueden configurar umbrales y materiales desde la UI del curso.

---

## Notas Adicionales

- Si en el futuro se decide implementar un motor de ML, la arquitectura de eventos de RabbitMQ facilita la integración: el servicio de ML puede suscribirse a los eventos `evaluation.completed` y `learning.session.completed` sin modificar el Assessment & Adaptive Service.
- Considerar Apache Kafka como alternativa a RabbitMQ si el volumen de eventos supera los 100,000 mensajes por hora, por su mayor throughput y capacidad de replay de eventos.

---

## Referencias

- [SRS] Software Requirements Specification - Plataforma de Aprendizaje Adaptativo y Colaborativo
- [ADR-001] Service-Based Architecture - Plataforma de Aprendizaje
- [ADR-002] PostgreSQL como Base de Datos Principal
- RabbitMQ Documentation: https://www.rabbitmq.com/documentation.html
- AWS MQ: https://aws.amazon.com/amazon-mq/
- Kizilcec, R., Pérez-Sanagustín, M. & Maldonado, J. (2017). *Self-regulated learning strategies predict learner behavior and goal attainment in Massive Open Online Courses*. Computers & Education, 104.

---

- [Nombre 3]: __________ - Fecha: ___/___/___
- [Nombre 4]: __________ - Fecha: ___/___/___
