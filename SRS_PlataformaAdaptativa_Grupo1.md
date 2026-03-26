# Software Requirements Specification (SRS)
## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Version:** 2.0  
**Fecha:** 26/03/2026  
**Cambios respecto a v1.0:** Se incorporan los capitulos 6 (Patrones Arquitectonicos) y 7 (Casos de Uso).

---

## 1. INTRODUCCION

### 1.1 Proposito

Este documento describe los requisitos funcionales y no funcionales de la **Plataforma de Aprendizaje Adaptativo y Colaborativo**. Su proposito es establecer de forma clara que debe hacer el sistema y como debe hacerlo antes de comenzar el diseno arquitectonico.

### 1.2 Alcance

La plataforma permite a estudiantes y docentes interactuar en un entorno digital de aprendizaje. El sistema adapta el recorrido educativo de cada estudiante en funcion de su progreso y resultados en evaluaciones, y fomenta la colaboracion mediante foros, grupos de estudio y tutorias entre pares. Los profesores pueden disenar cursos, publicar materiales, definir evaluaciones y monitorear el progreso del grupo. La plataforma genera analitica de desempeno para apoyar la toma de decisiones pedagogicas.

### 1.3 Definiciones, Acronimos y Abreviaciones

| Termino | Definicion |
|---------|------------|
| RF | Requisito Funcional |
| RNF | Requisito No Funcional |
| DR | Driver Arquitectural |
| API | Application Programming Interface |
| JWT | JSON Web Token |
| RBAC | Role-Based Access Control |
| SPA | Single Page Application |
| ATAM | Architecture Tradeoff Analysis Method |
| RTO | Recovery Time Objective |
| RPO | Recovery Point Objective |
| UC | Use Case (Caso de Uso) |
| AP | Architectural Pattern (Enfoque Arquitectonico) |
| ORM | Object-Relational Mapping |
| TTL | Time To Live |
| DLQ | Dead Letter Queue |
| CDN | Content Delivery Network |
| WAF | Web Application Firewall |
| CI/CD | Continuous Integration / Continuous Delivery |
| AZ | Availability Zone |

### 1.4 Referencias

- Enunciado del proyecto: *Plataforma de Aprendizaje Adaptativo y Colaborativo* - Arquitectura de Software, Version 0.9, Pontificia Universidad Javeriana, 2026.
- ISO/IEC 25010:2011 - Modelo de calidad de software.
- Ley 1581 de 2012 - Proteccion de datos personales en Colombia.
- Bass, L., Clements, P. & Kazman, R. (2012). *Software Architecture in Practice* (3ra ed.). Addison-Wesley.
- Kazman, R., Klein, M. & Clements, P. (2000). *ATAM: Method for Architecture Evaluation*. CMU/SEI-2000-TR-004.

---

## 2. DESCRIPCION GENERAL DEL SISTEMA

### 2.1 Perspectiva del Producto

La plataforma es un sistema independiente que se integrara en el futuro con otras plataformas educativas institucionales (interoperabilidad). No reemplaza el sistema de registro academico existente, pero puede consumir datos de este mediante APIs. Opera sobre infraestructura cloud y es accesible desde navegadores web y dispositivos moviles modernos.

### 2.2 Funciones del Producto

1. Gestion de cursos: creacion, edicion y publicacion de contenidos educativos por parte de docentes.
2. Aprendizaje adaptativo: ajuste automatico del recorrido educativo segun el desempeno del estudiante.
3. Evaluaciones: creacion, presentacion y calificacion automatica de quizzes y examenes.
4. Colaboracion entre pares: foros de discusion, grupos de estudio y tutorias entre estudiantes.
5. Seguimiento del progreso: dashboards de progreso para estudiantes y analitica de desempeno para docentes.
6. Gestion de usuarios: registro, autenticacion y control de acceso basado en roles.

### 2.3 Caracteristicas de Usuarios

| Tipo de Usuario | Descripcion | Nivel de Expertise |
|-----------------|-------------|--------------------|
| **Estudiante** | Accede a contenidos, realiza evaluaciones, participa en grupos y foros, consulta su progreso | Basico - no tecnico |
| **Docente** | Crea y gestiona cursos, publica materiales, define evaluaciones, monitorea el progreso del grupo | Medio - maneja herramientas digitales basicas |
| **Administrador** | Gestiona usuarios, configura la plataforma, supervisa el uso del sistema | Alto - perfil tecnico-administrativo |

### 2.4 Restricciones del Sistema

**Restricciones regulatorias:**
- Cumplimiento con la Ley 1581 de 2012 (proteccion de datos personales en Colombia).
- Los datos de los estudiantes no pueden compartirse con terceros sin consentimiento explicito.

---

## 3. REQUISITOS FUNCIONALES

### RF-01: Gestion de Cursos
**Prioridad:** Alta  
**Descripcion:** El sistema debe permitir a los docentes crear, editar, organizar y publicar cursos con modulos, lecciones y materiales educativos (videos, PDFs, enlaces externos).

**Criterios de aceptacion:**
- El docente puede crear un curso con titulo, descripcion, imagen de portada y modulos en menos de 10 pasos.
- Los materiales pueden ser archivos PDF, videos o textos.
- El curso puede estar en estado borrador (solo visible para el docente) o publicado (visible para estudiantes inscritos).

---

### RF-02: Evaluaciones y Calificacion Automatica
**Prioridad:** Alta  
**Descripcion:** El sistema debe permitir a los docentes crear evaluaciones con preguntas de opcion multiple, verdadero/falso y respuesta corta, y al sistema calificar automaticamente las respuestas cuando corresponda.

**Criterios de aceptacion:**
- El docente puede crear quizzes con minimo 5 tipos de pregunta distintos.
- El sistema califica automaticamente preguntas de opcion multiple y verdadero/falso al momento de envio.
- El estudiante recibe retroalimentacion inmediata con la nota y las respuestas correctas tras completar la evaluacion.
- El docente puede configurar intentos maximos, tiempo limite y fecha de disponibilidad.

---

### RF-03: Aprendizaje Adaptativo
**Prioridad:** Alta  
**Descripcion:** El sistema debe adaptar el recorrido de aprendizaje del estudiante segun su desempeno en evaluaciones. Si el estudiante obtiene una calificacion inferior al umbral definido, el sistema sugiere materiales de refuerzo; si supera el umbral, habilita contenidos de mayor complejidad.

**Criterios de aceptacion:**
- El docente puede configurar un umbral de aprobacion por leccion (valor entre 0 y 100).
- Si el estudiante no supera el umbral, el sistema muestra automaticamente materiales de refuerzo relacionados con los temas con menor calificacion.
- Si el estudiante supera el umbral, el sistema desbloquea la siguiente leccion o un contenido de mayor complejidad.
- Las reglas de adaptacion son configurables por el docente por curso y modulo.

---

### RF-04: Foros de Discusion y Colaboracion
**Prioridad:** Media  
**Descripcion:** El sistema debe proporcionar foros de discusion asociados a cada curso donde estudiantes y docentes puedan publicar preguntas, respuestas y comentarios, fomentando el aprendizaje colaborativo.

**Criterios de aceptacion:**
- Cada curso tiene al menos un foro general y puede tener foros por modulo.
- Los usuarios pueden crear hilos, responder y dar "me gusta" a publicaciones.
- El docente puede moderar el foro: fijar publicaciones, eliminar contenido inapropiado y cerrar hilos.
- El sistema envia notificaciones al usuario cuando alguien responde a su publicacion.

---

### RF-05: Grupos de Estudio y Tutorias entre Pares
**Prioridad:** Media  
**Descripcion:** El sistema debe permitir la formacion de grupos de estudio dentro de un curso, asi como la identificacion de estudiantes con alto dominio en un tema para ofrecer tutorias a companeros con dificultades.

**Criterios de aceptacion:**
- Un estudiante o docente puede crear un grupo de estudio con nombre, descripcion y maximo de integrantes.
- El sistema identifica automaticamente estudiantes con calificacion promedio superior al 85% en un tema y les ofrece la opcion de ser tutor.
- Los grupos de estudio tienen un espacio de chat privado y pueden compartir archivos.
- El docente puede ver la lista de grupos activos y su composicion.

---

### RF-06: Seguimiento del Progreso del Estudiante
**Prioridad:** Alta  
**Descripcion:** El sistema debe presentar a cada estudiante su progreso detallado por curso: porcentaje de avance y calificaciones por evaluacion.

**Criterios de aceptacion:**
- El sistema muestra el porcentaje de avance del estudiante en cada curso inscrito.
- El estudiante puede ver el historial de calificaciones con fecha, nota y retroalimentacion.
- La informacion se actualiza en tiempo real tras cada actividad completada.

---

### RF-07: Analitica para Docentes
**Prioridad:** Media  
**Descripcion:** El sistema debe proveer a los docentes herramientas de analitica que les permitan identificar patrones de desempeno, temas con mayor dificultad y el estado de las actividades colaborativas.

**Criterios de aceptacion:**
- El docente accede a un reporte por curso con calificacion promedio, tasa de aprobacion y temas mas fallidos.
- El sistema genera alertas automaticas cuando mas del 30% de los estudiantes reprueba una evaluacion.
- El sistema genera un reporte por estudiante con resultados y materiales utilizados.
- El docente puede exportar reportes en formato CSV o PDF.
- Los datos se actualizan maximo cada 24 horas para reportes consolidados y en tiempo real para vistas individuales.

---

### RF-08: Gestion de Usuarios y Roles
**Prioridad:** Alta  
**Descripcion:** El sistema debe gestionar el registro, autenticacion y autorizacion de usuarios mediante roles definidos (estudiante, docente, administrador), con soporte para inicio de sesion con credenciales institucionales.

**Criterios de aceptacion:**
- El sistema soporta registro con correo institucional y contrasena.
- Los roles son: estudiante, docente y administrador, con permisos diferenciados.
- El administrador puede crear, editar y desactivar cuentas de usuario.
- Las sesiones expiran tras 8 horas de inactividad y el token JWT se invalida al cerrar sesion.

---

## 4. REQUISITOS NO FUNCIONALES

### RNF-01: Rendimiento (Performance)
**ID:** RNF-01  
**Categoria:** Performance  
**Descripcion:** Las operaciones principales del sistema deben responder dentro de tiempos aceptables para garantizar una experiencia de usuario fluida.

**Metricas:**
- Tiempo de respuesta de operaciones principales (carga de curso, envio de evaluacion, consulta de progreso): <= 2 segundos en P95.
- Tiempo de carga inicial de la aplicacion web: <= 2 segundos en P95.
- Throughput minimo: >= 500 peticiones por segundo en el pico de uso.

**Justificacion:** El enunciado establece explicitamente que las operaciones principales deben responder en menos de 2 segundos. Tiempos superiores generan abandono y frustracion en contextos educativos donde los estudiantes acceden masivamente antes de examenes.

---

### RNF-02: Disponibilidad (Availability)
**ID:** RNF-02  
**Categoria:** Availability  
**Descripcion:** La plataforma debe estar disponible de forma continua, especialmente durante periodos de evaluacion.

**Metricas:**
- Disponibilidad minima: 99.5% mensual (maximo ~3.6 horas de downtime/mes).
- RTO: <= 1 hora ante fallas de infraestructura.
- RPO: <= 15 minutos de perdida de datos ante falla.

**Justificacion:** El enunciado establece disponibilidad minima del 99.5%. Las instituciones educativas no pueden permitirse caidas durante periodos de evaluacion.

---

### RNF-03: Escalabilidad (Scalability)
**ID:** RNF-03  
**Categoria:** Scalability  
**Descripcion:** El sistema debe soportar el crecimiento en numero de usuarios y cursos sin degradacion del rendimiento.

**Metricas:**
- Soporte para al menos 5,000 usuarios concurrentes sin degradacion del rendimiento.
- Capacidad de escalar horizontalmente hasta 10,000 usuarios concurrentes con ajuste de infraestructura.
- El sistema debe soportar al menos 500 cursos activos simultaneamente.
- El tiempo de escala automatica ante picos de carga debe ser <= 5 minutos.

**Justificacion:** El enunciado establece que el sistema debe soportar multiples cursos y miles de usuarios concurrentes. En contextos educativos, los picos de carga coinciden con periodos de entrega y evaluacion.

---

### RNF-04: Seguridad (Security)
**ID:** RNF-04  
**Categoria:** Security  
**Descripcion:** El sistema debe proteger los datos personales de los usuarios y garantizar que solo usuarios autorizados accedan a recursos segun su rol.

**Metricas:**
- Autenticacion mediante JWT con expiracion configurable (maximo 8 horas).
- Todas las comunicaciones deben usar TLS 1.2 o superior.
- RBAC con verificacion en cada peticion al backend.
- Los datos sensibles (contrasenas) se almacenan con hash bcrypt (factor de costo >= 12).
- Cumplimiento con Ley 1581 de 2012: consentimiento explicito, politica de privacidad visible.

**Justificacion:** El enunciado requiere autenticacion segura, proteccion de datos personales y RBAC. La naturaleza educativa del sistema implica datos de menores de edad.

---

### RNF-05: Mantenibilidad (Maintainability)
**ID:** RNF-05  
**Categoria:** Maintainability  
**Descripcion:** La arquitectura debe permitir incorporar nuevos modulos o funcionalidades sin afectar el funcionamiento del sistema existente.

**Metricas:**
- El tiempo para agregar un nuevo modulo independiente al sistema no debe superar 2 semanas de desarrollo.
- El sistema debe contar con pipelines de CI/CD que permitan despliegues sin tiempo de inactividad.
- Los logs del sistema deben estructurarse en formato JSON con niveles INFO, WARN, ERROR y trazas de correlacion.

**Justificacion:** El enunciado establece que la arquitectura debe facilitar la incorporacion futura de modulos. Un equipo pequeno requiere alta automatizacion del proceso de desarrollo y despliegue.

---

### RNF-06: Interoperabilidad (Interoperability)
**ID:** RNF-06  
**Categoria:** Interoperability  
**Descripcion:** El sistema debe permitir integraciones futuras con otras plataformas educativas.

**Metricas:**
- Todas las APIs del sistema deben seguir el estandar REST con respuestas en JSON.
- Las APIs deben estar documentadas con OpenAPI 3.0.
- El sistema debe soportar el estandar xAPI (Tin Can API) para registro de actividades de aprendizaje.
- Las APIs publicas deben tener versionado (ej: /api/v1/) para garantizar retrocompatibilidad.

**Justificacion:** El enunciado establece explicitamente que el sistema debe permitir integraciones futuras con otras plataformas educativas. La adopcion de estandares abiertos reduce el costo de integracion.

---

## 5. DRIVERS ARQUITECTURALES IDENTIFICADOS

| ID | Driver | Valor/Metrica | Prioridad |
|----|--------|---------------|-----------|
| **DR-01** | Rendimiento | Operaciones principales <= 2 seg (P95) | Alta |
| **DR-02** | Escalabilidad | >= 5,000 usuarios concurrentes | Alta |
| **DR-03** | Disponibilidad | >= 99.5% uptime mensual | Alta |
| **DR-04** | Seguridad | RBAC, TLS, JWT, Ley 1581 | Alta |
| **DR-05** | Mantenibilidad | Modulos independientes, CI/CD, cobertura >= 70% | Media |
| **DR-06** | Interoperabilidad | APIs REST + OpenAPI 3.0 + xAPI | Media |
| **DR-07** | Motor Adaptativo | Reglas de adaptacion configurables por docente | Alta |

---

## 6. PATRONES ARQUITECTONICOS

Esta seccion documenta los patrones y tacticas arquitectonicas adoptadas en la plataforma, su justificacion, los atributos de calidad que satisfacen y los trade-offs que introducen. Cada patron se vincula explicitamente a los drivers arquitecturales del capitulo 5.

---

### AP-01: Service-Based Architecture (Arquitectura Basada en Servicios)

**Descripcion**

La plataforma se estructura en cinco servicios de grano grueso desplegables de forma independiente: User Service, Course Service, Assessment & Adaptive Service, Collaboration Service y Analytics Service. Cada servicio encapsula un subdominio del negocio y expone su funcionalidad mediante APIs REST. Los servicios se comunican a traves de un API Gateway como punto de entrada unico y mediante RabbitMQ para eventos asincronos.

**Justificacion**

Se prefirio Service-Based Architecture sobre una arquitectura de microservicios pura dado el tamano reducido del equipo de desarrollo (3-4 personas) y el alcance del MVP. Esta decision reduce la complejidad operacional sin sacrificar la escalabilidad selectiva por servicio. Sobre una arquitectura monolitica, ofrece aislamiento de fallos y la posibilidad de escalar independientemente los servicios de mayor carga, en particular el Assessment Service durante periodos de evaluacion.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-02 Escalabilidad | Permite escalar cada servicio de forma independiente segun su demanda |
| DR-03 Disponibilidad | Aislamiento de fallos: la caida de un servicio no compromete a los demas |
| DR-05 Mantenibilidad | Cada servicio puede desplegarse, modificarse y probarse de forma autonoma |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Escalabilidad, Disponibilidad | Complejidad operacional | Requiere orquestacion de contenedores (ECS), monitoreo distribuido y gestion de pipelines CI/CD por servicio |
| Mantenibilidad | Latencia de red | Las llamadas entre servicios introducen latencia adicional respecto a llamadas en proceso |

**Componentes involucrados:** Todos los servicios del backend, API Gateway, RabbitMQ.

---

### AP-02: API Gateway con Validacion JWT

**Descripcion**

Se utiliza un API Gateway (AWS API Gateway con plugins Kong) como punto de entrada unico para todas las peticiones de los clientes. El Gateway valida el token JWT en cada peticion antes de enrutarla al servicio correspondiente, aplica rate limiting por usuario/IP y expone las APIs con documentacion OpenAPI 3.0.

**Justificacion**

Centralizar la autenticacion en el Gateway evita duplicar logica de validacion de tokens en cada servicio y reduce la superficie de ataque. La documentacion OpenAPI 3.0 en el Gateway facilita la integracion con plataformas educativas externas (DR-06).

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-04 Seguridad | Valida firma JWT RS256 y extrae el rol del usuario antes de enrutar cualquier peticion |
| DR-06 Interoperabilidad | Expone toda la API bajo versionado /api/v1/ con especificacion OpenAPI 3.0 |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Seguridad, Interoperabilidad | Latencia | Cada peticion incurre en ~10ms adicionales por la validacion JWT en el Gateway |
| Mantenibilidad | Punto unico de falla potencial | El Gateway se convierte en un componente critico; su caida afecta a todos los servicios |

**Componentes involucrados:** AWS API Gateway, Kong plugins, User Service (emision de tokens).

---

### AP-03: Control de Acceso Basado en Roles (RBAC) en cada Servicio

**Descripcion**

Ademas de la validacion JWT en el API Gateway, cada servicio verifica que el rol extraido del token tenga permiso para ejecutar la operacion solicitada. La verificacion de rol se realiza en el middleware de cada controlador mediante la libreria compartida `libs/auth`. Los roles definidos son: STUDENT, INSTRUCTOR y ADMIN, con permisos diferenciados por endpoint.

**Justificacion**

El RBAC en dos niveles (Gateway + Servicio) implementa el principio de defensa en profundidad. Aunque el Gateway rechaza peticiones con roles insuficientes, el servicio actua como segunda linea de defensa ante configuraciones incorrectas del Gateway o tokens manipulados que pasen la validacion de firma.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-04 Seguridad | Garantiza que ningun usuario pueda ejecutar operaciones fuera de su rol, incluso si elude el Gateway |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Seguridad | Rendimiento | Cada peticion incurre en una verificacion de rol adicional en el servicio (~2-5ms) |
| Seguridad | Mantenibilidad | Los cambios en los permisos de un rol deben propagarse en cada servicio que los implementa |

**Componentes involucrados:** Todos los servicios backend, libreria `libs/auth`.

---

### AP-04: Cache-Aside con Redis (Sesiones, Contenido y Reglas de Adaptacion)

**Descripcion**

Se utiliza ElastiCache Redis 7 como capa de cache mediante el patron Cache-Aside. Los servicios consultan primero Redis antes de acceder a PostgreSQL. Si el dato no esta en cache (cache miss), se consulta la base de datos y el resultado se almacena en Redis con un TTL configurado por tipo de dato: sesiones activas (TTL=8h), listas de cursos (TTL=3600s), reglas de adaptacion (TTL=60s).

**Justificacion**

La consulta directa a PostgreSQL para cada peticion es el cuello de botella de rendimiento mas significativo bajo alta concurrencia. Redis permite servir la mayoria de las peticiones de lectura sin presionar la base de datos, reduciendo la latencia de P95 de ~500ms a ~80ms para la carga de cursos.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-01 Rendimiento | Reduce la latencia de operaciones de lectura frecuentes de ~500ms a ~5-80ms |
| DR-02 Escalabilidad | Reduce la carga sobre PostgreSQL, permitiendo soportar mayor concurrencia con la misma infraestructura de BD |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Rendimiento, Escalabilidad | Consistencia | Se acepta consistencia eventual: un cambio en PostgreSQL puede tardar hasta el TTL en reflejarse en Redis |
| Rendimiento | Costo de infraestructura | ElastiCache Redis representa un costo adicional mensual (~$50-200 USD segun instancia) |
| Rendimiento | Complejidad | Se requiere logica de invalidacion de cache ante actualizaciones criticas (ej. reglas de adaptacion) |

**Nota sobre el TTL de reglas de adaptacion:** Se configura TTL=60s (1 minuto) deliberadamente corto para que los cambios de umbral realizados por un docente tengan efecto en la siguiente evaluacion con un retraso maximo de 60 segundos, sin requerir invalidacion explicita.

**Componentes involucrados:** User Service, Course Service, Assessment & Adaptive Service, ElastiCache Redis.

---

### AP-05: Mensajeria Asincrona con RabbitMQ (Patron Outbox + DLQ)

**Descripcion**

La comunicacion entre el Assessment Service (productor) y los servicios Analytics y Collaboration (consumidores) se realiza de forma asincrona mediante RabbitMQ (AWS MQ). El Assessment Service publica un evento `evaluation.completed` tras calificar una evaluacion. Analytics actualiza el progreso del estudiante y genera alertas de forma diferida. Se implementa el patron Outbox para garantizar la entrega al menos una vez, y se configura una Dead Letter Queue (DLQ) con TTL=24h para mensajes que fallen tres veces consecutivas.

**Justificacion**

El procesamiento de analitica y notificaciones no debe bloquear la respuesta al estudiante. Al desacoplar estas operaciones del camino critico sincrono, la latencia del endpoint de envio de evaluacion se reduce de ~800ms a ~120ms P95, cumpliendo el SLA de 2 segundos incluso bajo alta concurrencia.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-01 Rendimiento | Elimina del camino critico las operaciones de analitica y notificacion (operaciones lentas) |
| DR-02 Escalabilidad | RabbitMQ actua como buffer ante picos de trafico, nivelando la carga sobre los consumidores |
| DR-03 Disponibilidad | La caida del Analytics Service no impide completar evaluaciones; los eventos se persisten en la cola |
| DR-05 Mantenibilidad | Los consumidores pueden agregarse o modificarse sin cambiar el productor |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Rendimiento, Disponibilidad | Consistencia eventual del dashboard | El progreso del estudiante en el dashboard puede estar desactualizado hasta que el Analytics Service procese el evento (segundos a minutos) |
| Escalabilidad | Complejidad operacional | Se requiere monitorear la profundidad de las colas, configurar alertas y gestionar la DLQ |

**Riesgo asociado (R-03):** Si el Analytics Service cae por mas de 30 minutos durante un periodo de evaluaciones masivas, la acumulacion de mensajes en la cola puede saturar el servicio al recuperarse. Mitigacion: rate limiting en el consumidor (max 50 mensajes/seg durante recuperacion) y alerta CloudWatch cuando la profundidad de cola supere 5,000 mensajes.

**Componentes involucrados:** Assessment & Adaptive Service (productor), Analytics Service (consumidor), Collaboration Service (consumidor), AWS MQ RabbitMQ.

---

### AP-06: Motor de Reglas con Persistencia en Base de Datos (Rules Engine)

**Descripcion**

Las reglas de adaptacion del recorrido de aprendizaje (umbrales de aprobacion, materiales de refuerzo asociados, contenido de mayor complejidad a desbloquear) se almacenan como registros en la tabla `adaptation_rules` del schema de PostgreSQL, no como logica codificada en el servicio. El Assessment & Adaptive Service consulta estas reglas al evaluar el desempeno del estudiante y determina la siguiente accion: desbloquear contenido o presentar materiales de refuerzo.

**Justificacion**

Almacenar las reglas en la base de datos en lugar de en el codigo permite que los docentes las modifiquen desde la interfaz sin requerir un redespliegue del servicio. Esto es el mecanismo central del driver DR-07 (Motor Adaptativo) y del RNF-05 (Mantenibilidad).

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-07 Motor Adaptativo | Las reglas configuradas por el docente tienen efecto en la siguiente evaluacion sin redespliegue |
| DR-05 Mantenibilidad | Los cambios en las reglas pedagogicas no requieren intervencion del equipo de desarrollo |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Mantenibilidad, Motor Adaptativo | Rendimiento | Cada evaluacion requiere una consulta a PostgreSQL para cargar las reglas (mitigado con cache Redis TTL=60s) |
| Flexibilidad de reglas | Complejidad de validacion | Las reglas en BD requieren validacion de integridad (umbrales validos, referencias a contenido existente) en el nivel de aplicacion |

**Componentes involucrados:** Assessment & Adaptive Service, PostgreSQL (tabla `adaptation_rules`), ElastiCache Redis (cache de reglas).

---

### AP-07: Alta Disponibilidad de Base de Datos con RDS Multi-AZ

**Descripcion**

La instancia PostgreSQL 16 se despliega en configuracion Multi-AZ de AWS RDS. AWS mantiene una replica sincrona en una segunda zona de disponibilidad. Ante una falla de la instancia primaria, el failover automatico promueve la replica a primaria en un tiempo de ~60 segundos, sin intervencion manual.

**Justificacion**

PostgreSQL es el componente de estado critico de la plataforma. Una falla de la base de datos sin failover automatico implicaria downtime superior al permitido por el RTO de 1 hora (RNF-02). La configuracion Multi-AZ garantiza recuperacion automatica dentro del SLA.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-03 Disponibilidad | Failover automatico en ~60 segundos ante falla de la instancia primaria de BD |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Disponibilidad | Costo | La instancia Multi-AZ cuesta aproximadamente el doble que una instancia Single-AZ |
| Disponibilidad | Latencia de escritura | Las escrituras esperan confirmacion de la replica sincrona (~1-2ms adicionales) |

**Componentes involucrados:** RDS PostgreSQL 16 Multi-AZ, todos los servicios backend (a traves de PgBouncer).

---

### AP-08: Schemas Separados por Servicio en Base de Datos Compartida

**Descripcion**

Dado que la arquitectura Service-Based comparte una unica instancia RDS (decision de MVP para reducir costos operacionales), cada servicio accede exclusivamente a su propio schema de PostgreSQL: `schema_users`, `schema_courses`, `schema_assessments`, `schema_collaboration`, `schema_analytics`. Se establece la politica de "no cross-schema queries": ningun servicio puede leer o escribir en el schema de otro servicio. La comunicacion de datos entre dominios se realiza unicamente mediante eventos RabbitMQ o llamadas REST.

**Justificacion**

Esta decision equilibra el costo operacional del MVP (una sola instancia RDS) con el principio de aislamiento logico de dominios. Aunque los schemas comparten recursos de I/O y CPU, el aislamiento logico facilita la eventual migracion a bases de datos separadas por servicio en versiones futuras.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-05 Mantenibilidad | El aislamiento logico de schemas previene acoplamiento implicito entre servicios a nivel de datos |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Mantenibilidad (aislamiento logico) | Disponibilidad (acoplamiento residual) | Una saturacion de I/O en un schema puede degradar el rendimiento de los otros schemas en la misma instancia |
| Costo (instancia unica) | Escalabilidad de BD | No es posible escalar la base de datos de un solo servicio sin afectar a los demas |

**Nota tecnica:** Se configura `lock_timeout = 5s` y `statement_timeout = 10s` por schema para evitar que transacciones largas en un schema bloqueen recursos compartidos.

**Componentes involucrados:** RDS PostgreSQL 16, todos los servicios backend.

---

### AP-09: Auto-scaling Horizontal con ECS Fargate

**Descripcion**

Cada servicio se despliega como un conjunto de tareas ECS Fargate con politicas de auto-scaling independientes basadas en metricas de CPU y uso de memoria. El Assessment Service tiene configuracion de escala minima en 3 instancias (en lugar de 2 como los otros servicios) dado que es el componente de mayor carga durante periodos de evaluacion. Todas las instancias operan en configuracion sin estado (stateless): el estado de sesion se almacena en Redis, no en memoria de la instancia.

**Justificacion**

El auto-scaling horizontal permite absorber los picos de carga que caracterizan los contextos educativos (inicio de semestre, periodos de evaluacion) sin sobreprovisionar infraestructura durante periodos de baja demanda. El caracter stateless de los servicios es el prerequisito para que el escalado horizontal sea efectivo.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-02 Escalabilidad | Las instancias de cada servicio escalan de forma independiente segun la demanda real |
| DR-03 Disponibilidad | Multiples instancias por servicio en dos zonas de disponibilidad eliminan puntos unicos de falla |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Escalabilidad | Tiempo de calentamiento | El auto-scaling tarda hasta 5 minutos en aprovisionar nuevas instancias (cold start); un pico abrupto puede degradar brevemente el rendimiento antes de que el escalado sea efectivo |
| Disponibilidad | Costo variable | El costo de infraestructura varia con la carga; picos sostenidos incrementan el costo mensual |

**Mitigacion del tiempo de calentamiento:** Para el Assessment Service se implementa pre-escalado programado antes de los periodos de evaluacion conocidos.

**Componentes involucrados:** AWS ECS Fargate, todos los servicios backend.

---

### AP-10: Despliegue Blue-Green con CI/CD

**Descripcion**

El pipeline de CI/CD se implementa con GitHub Actions. Ante cada merge a la rama principal, el pipeline ejecuta: pruebas unitarias e integracion, construccion de imagen Docker, despliegue a staging y, tras aprobacion, despliegue a produccion mediante estrategia Blue-Green. En Blue-Green, el nuevo conjunto de instancias (Green) se aprovisiona y calienta antes de recibir trafico. Una vez que los health checks del Green son exitosos, el enrutador de trafico cambia del Blue al Green. El entorno Blue se mantiene como respaldo por 30 minutos antes de ser terminado.

**Justificacion**

Los estudiantes no deben experimentar interrupciones durante el uso de la plataforma, incluso durante actualizaciones de software. La estrategia Blue-Green garantiza tiempo de inactividad cero en despliegues (RNF-05), con posibilidad de rollback inmediato si el entorno Green presenta problemas.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-05 Mantenibilidad | Permite despliegues frecuentes sin afectar la disponibilidad del servicio |
| DR-03 Disponibilidad | Elimina el downtime asociado a los despliegues de nuevas versiones |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Disponibilidad, Mantenibilidad | Costo | Durante el despliegue se incurre temporalmente en el doble del costo de infraestructura (entornos Blue y Green activos simultaneamente) |
| Mantenibilidad | Complejidad de configuracion | Requiere gestion de migraciones de base de datos compatibles con ambas versiones del servicio durante el periodo de transicion |

**Componentes involucrados:** GitHub Actions, AWS ECS Fargate, AWS ALB (Application Load Balancer).

---

### AP-11: Autenticacion Segura con bcrypt y worker_threads

**Descripcion**

Las contrasenas se almacenan utilizando el algoritmo bcrypt con factor de costo 12, que introduce deliberadamente una latencia de computo de ~100-300ms por operacion de hash. Dado que Node.js es de un solo hilo, el calculo de bcrypt se ejecuta en worker_threads para no bloquear el event loop durante el procesamiento de otras peticiones concurrentes.

**Justificacion**

bcrypt con factor de costo 12 hace computacionalmente inviable un ataque de fuerza bruta incluso con hardware especializado (ASIC/GPU). El uso de worker_threads es el mecanismo que permite mantener esta seguridad sin sacrificar el rendimiento del User Service bajo carga concurrente.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-04 Seguridad | Las contrasenas no pueden recuperarse de la base de datos aun en caso de brecha de seguridad |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Seguridad | Rendimiento en login masivo | 500 logins simultaneos generan alta presion de CPU en el User Service; el auto-scaling puede no completarse antes de que la latencia supere 500ms (punto de sensibilidad SP-01) |

**Punto de sensibilidad SP-01:** Este patron es el punto de sensibilidad mas critico de la arquitectura. Aumentar el factor de costo de bcrypt mejora la seguridad pero deteriora el rendimiento de forma exponencial. Reducirlo mejora el rendimiento pero debilita la proteccion de contrasenas. El valor 12 es el minimo recomendado por OWASP para sistemas modernos.

**Componentes involucrados:** User Service (modulo de autenticacion).

---

### AP-12: Pool de Conexiones con PgBouncer

**Descripcion**

PgBouncer actua como proxy de pool de conexiones entre los servicios ECS y la instancia RDS PostgreSQL. Cada servicio configura un pool de conexiones de maximo 20 conexiones activas. PgBouncer serializa las solicitudes de conexion que excedan el pool, evitando saturar el limite de conexiones simultaneas de PostgreSQL (~100 conexiones en db.t3.large).

**Justificacion**

Cada instancia ECS del Assessment Service puede recibir decenas de peticiones concurrentes. Sin pool de conexiones, 10 instancias del servicio podrian intentar abrir 200-500 conexiones simultaneas a PostgreSQL, superando el limite de la instancia y causando errores de conexion.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-02 Escalabilidad | Permite soportar mas instancias ECS sin superar el limite de conexiones de PostgreSQL |
| DR-01 Rendimiento | Reutiliza conexiones existentes en lugar de establecer nuevas conexiones por cada peticion |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Escalabilidad, Rendimiento | Complejidad de configuracion | Una configuracion incorrecta del pool puede causar timeouts o degradar el rendimiento en lugar de mejorarlo |

**Componentes involucrados:** PgBouncer (desplegado como contenedor sidecar o instancia dedicada), RDS PostgreSQL, todos los servicios backend.

---

### AP-13: Read Replica de PostgreSQL para Analytics

**Descripcion**

El Analytics Service ejecuta consultas de lectura sobre una replica de lectura (Read Replica) de PostgreSQL, separada de la instancia primaria. Las consultas de analitica son tipicamente complejas (joins multiples, agregaciones, generacion de reportes) y de larga duracion; ejecutarlas en la instancia primaria competiria con las operaciones transaccionales de los otros servicios. La replica acepta un retraso de replicacion de hasta 5 segundos, coherente con el requisito de RF-07 (datos actualizados maximo cada 24 horas para reportes consolidados).

**Justificacion**

Aislar la carga de lectura del Analytics Service en una replica protege el rendimiento de las operaciones transaccionales (envio de evaluaciones, actualizacion de progreso) que si impactan directamente la experiencia del estudiante.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-01 Rendimiento | Separa la carga de lectura del Analytics Service de las operaciones transaccionales criticas |
| DR-05 Mantenibilidad | El Analytics Service puede optimizarse y escalarse independientemente sin afectar la BD primaria |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Rendimiento de operaciones criticas | Consistencia de Analytics | Los reportes del Analytics Service pueden reflejar un estado de la BD con hasta ~5 segundos de retraso respecto a la primaria |
| Escalabilidad de lectura | Costo | La replica de lectura representa un costo adicional de infraestructura mensual |

**Componentes involucrados:** Analytics Service, RDS PostgreSQL Read Replica.

---

### AP-14: Circuit Breaker en API Gateway

**Descripcion**

El API Gateway implementa un patron Circuit Breaker para cada servicio backend. Si un servicio supera el umbral de errores configurado (ej. >50% de respuestas con error en 60 segundos), el Gateway abre el circuito y responde directamente con HTTP 503 sin intentar enrutar al servicio degradado. Tras un periodo de espera, el Gateway envia una peticion de prueba para verificar si el servicio se ha recuperado antes de cerrar el circuito.

**Justificacion**

Sin Circuit Breaker, las peticiones a un servicio degradado acumulan conexiones y timeouts que pueden propagarse y degradar el rendimiento del Gateway y de otros servicios que comparten recursos. El Circuit Breaker aisla rapidamente el servicio afectado, protegiendola estabilidad del sistema completo.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-03 Disponibilidad | Previene la propagacion en cascada de fallos desde un servicio degradado al resto del sistema |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Disponibilidad del sistema | Disponibilidad del servicio especifico | Cuando el circuito esta abierto, las peticiones al servicio afectado fallan inmediatamente (fail-fast) en lugar de esperar una respuesta lenta |
| Estabilidad | Complejidad de configuracion | Los umbrales del Circuit Breaker deben calibrarse para evitar aperturas de circuito por falsos positivos (picos transitorios normales) |

**Componentes involucrados:** AWS API Gateway (plugin Kong Circuit Breaker).

---

### AP-15: Health Checks Activos (/live, /ready)

**Descripcion**

Cada servicio expone dos endpoints de health check estandarizados: `/health/live` (liveness probe: el proceso esta activo y no en deadlock) y `/health/ready` (readiness probe: el servicio esta listo para recibir trafico, es decir, sus dependencias como BD y Redis estan disponibles). ECS evalua estos endpoints cada 30 segundos y reinicia automaticamente los contenedores que fallen la prueba de liveness, o los retira del load balancer si fallan la prueba de readiness.

**Justificacion**

La deteccion rapida de instancias degradadas y su reemplazo automatico es el mecanismo principal para mantener la disponibilidad del 99.5% sin intervencion manual del equipo de operaciones.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-03 Disponibilidad | ECS detecta y reemplaza instancias no saludables automaticamente, sin intervencion manual |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Disponibilidad | Overhead minimo de red | Los health checks generan trafico de red constante (~2 peticiones/minuto por instancia) |
| Disponibilidad | Falsos positivos | Un health check demasiado sensible puede reiniciar instancias que estaban procesando correctamente peticiones bajo carga alta |

**Componentes involucrados:** Todos los servicios ECS, AWS ALB.

---

### AP-16: Trazabilidad Distribuida con AWS X-Ray y Correlation ID

**Descripcion**

Cada peticion entrante recibe un Correlation ID unico (UUID v4) generado en el API Gateway. Este ID se propaga en los headers HTTP de todas las llamadas entre servicios y se incluye en todos los registros de log en formato JSON estructurado. AWS X-Ray instrumenta automaticamente los servicios Node.js para generar trazas de extremo a extremo, permitiendo identificar cual servicio introduce latencia en una operacion multi-servicio.

**Justificacion**

En una arquitectura de multiples servicios, diagnosticar un problema de rendimiento o un error requiere correlacionar logs y trazas de varios servicios. Sin Correlation ID y trazabilidad distribuida, esta tarea puede tardar horas; con ella, se reduce a minutos.

**Drivers que satisface**

| Driver | Como lo satisface |
|--------|-------------------|
| DR-05 Mantenibilidad | Reduce significativamente el tiempo de diagnostico y resolucion de incidentes en produccion |

**Trade-offs**

| Atributo mejorado | Atributo deteriorado | Descripcion del trade-off |
|-------------------|---------------------|---------------------------|
| Mantenibilidad (observabilidad) | Rendimiento | La instrumentacion de X-Ray introduce un overhead de aproximadamente 1-3% de latencia adicional por peticion |
| Observabilidad | Costo | AWS X-Ray y CloudWatch Logs generan costos adicionales proporcionales al volumen de trazas y logs |

**Componentes involucrados:** API Gateway (generacion del Correlation ID), todos los servicios backend (propagacion), AWS X-Ray, AWS CloudWatch Logs.

---

### Resumen de Patrones y Trazabilidad con Drivers

| ID | Patron | Drivers que satisface | Trade-off principal |
|----|--------|-----------------------|---------------------|
| AP-01 | Service-Based Architecture | DR-02, DR-03, DR-05 | Complejidad operacional |
| AP-02 | API Gateway con JWT | DR-04, DR-06 | Latencia +10ms; punto unico de falla |
| AP-03 | RBAC en cada servicio | DR-04 | Verificacion adicional por peticion |
| AP-04 | Cache-Aside Redis | DR-01, DR-02 | Consistencia eventual |
| AP-05 | Mensajeria asincrona RabbitMQ | DR-01, DR-02, DR-03, DR-05 | Consistencia eventual del dashboard |
| AP-06 | Rules Engine en BD | DR-07, DR-05 | Consulta adicional a BD por evaluacion |
| AP-07 | RDS Multi-AZ | DR-03 | Costo +50% vs. Single-AZ |
| AP-08 | Schemas separados en BD compartida | DR-05 | Acoplamiento residual de recursos |
| AP-09 | Auto-scaling ECS Fargate | DR-02, DR-03 | Tiempo de calentamiento <=5 min |
| AP-10 | Blue-Green Deployment CI/CD | DR-05, DR-03 | Costo temporal de infraestructura doble |
| AP-11 | bcrypt + worker_threads | DR-04 | Latencia de login bajo carga masiva |
| AP-12 | PgBouncer Connection Pooling | DR-02, DR-01 | Complejidad de configuracion |
| AP-13 | Read Replica para Analytics | DR-01, DR-05 | Retraso de replicacion ~5s en Analytics |
| AP-14 | Circuit Breaker en Gateway | DR-03 | Fail-fast en servicio afectado |
| AP-15 | Health Checks /live /ready | DR-03 | Overhead minimo de polling |
| AP-16 | X-Ray + Correlation ID | DR-05 | Overhead ~1-3% de latencia |

---

## 7. CASOS DE USO

Esta seccion documenta los casos de uso principales del sistema en formato extendido (fully-dressed). Los casos de uso se derivan directamente de los requisitos funcionales del capitulo 3 y se vinculan a los actores identificados en la seccion 2.3.

---

### UC-01: Iniciar Sesion en la Plataforma

**ID:** UC-01  
**Nombre:** Iniciar Sesion en la Plataforma  
**Actor principal:** Estudiante, Docente, Administrador  
**Actores secundarios:** User Service, Redis (cache de sesiones)  
**Requisito relacionado:** RF-08  
**Driver relacionado:** DR-04 (Seguridad), DR-01 (Rendimiento)  
**Prioridad:** Alta  

**Descripcion breve:** El usuario ingresa sus credenciales institucionales y el sistema valida su identidad, generando un token JWT para las siguientes interacciones.

**Precondiciones:**
- El usuario tiene una cuenta registrada en el sistema con correo institucional y contrasena.
- El sistema esta disponible y el User Service responde a peticiones.

**Postcondiciones:**
- El usuario recibe un token JWT firmado con RS256 valido por 8 horas.
- La sesion activa se registra en Redis con TTL=8h.
- El usuario es redirigido a su dashboard segun su rol (estudiante, docente o administrador).

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Usuario | Accede a la URL de la plataforma y selecciona "Iniciar sesion" |
| 2 | Sistema | Presenta el formulario de inicio de sesion con campos de correo y contrasena |
| 3 | Usuario | Ingresa su correo institucional y contrasena y confirma |
| 4 | Sistema (API Gateway) | Recibe la peticion POST /api/v1/auth/login y la enruta al User Service |
| 5 | Sistema (User Service) | Busca el usuario en PostgreSQL por correo electronico |
| 6 | Sistema (User Service) | Verifica la contrasena contra el hash bcrypt almacenado usando worker_threads |
| 7 | Sistema (User Service) | Genera un token JWT RS256 con claims: userId, email, role, exp (8h) |
| 8 | Sistema (User Service) | Almacena la referencia de sesion activa en Redis con TTL=8h |
| 9 | Sistema | Retorna HTTP 200 con el token JWT al cliente |
| 10 | Sistema (Web Client) | Almacena el JWT en memoria de la aplicacion y redirige al dashboard correspondiente al rol |

**Flujos alternos:**

*A1: Credenciales incorrectas*
- En el paso 6, si la verificacion bcrypt falla, el sistema retorna HTTP 401 con mensaje generico "Credenciales invalidas" (sin indicar si el error es en el correo o la contrasena).
- El sistema registra el intento fallido en logs con timestamp, IP y correo utilizado.
- Tras 5 intentos fallidos consecutivos, el sistema aplica un bloqueo temporal de 15 minutos para esa cuenta.

*A2: Cuenta desactivada*
- En el paso 5, si el usuario existe pero su cuenta esta en estado INACTIVE, el sistema retorna HTTP 403 con mensaje "Cuenta desactivada. Contacte al administrador".

*A3: Sesion ya activa*
- Si el usuario ya tiene una sesion valida en Redis, el sistema permite crear una nueva sesion sin invalidar la anterior (soporte a multiples dispositivos).

**Requisitos no funcionales aplicables:**
- La latencia de este caso de uso debe ser <= 500ms en P95 bajo 500 logins simultaneos (RNF-01).
- La contrasena viaja cifrada mediante TLS 1.3 y nunca se almacena en texto plano (RNF-04).

**Punto de sensibilidad:** El calculo bcrypt costo=12 tarda ~100-300ms. Bajo 500 logins simultaneos, el User Service puede saturar CPU antes de que el auto-scaling sea efectivo (ver AP-11, SP-01 del ATAM).

---

### UC-02: Crear un Curso

**ID:** UC-02  
**Nombre:** Crear un Curso  
**Actor principal:** Docente  
**Actores secundarios:** Course Service, S3 (almacenamiento de archivos)  
**Requisito relacionado:** RF-01  
**Driver relacionado:** DR-05 (Mantenibilidad), DR-04 (Seguridad)  
**Prioridad:** Alta  

**Descripcion breve:** El docente crea un nuevo curso en la plataforma, definiendo su estructura de modulos y lecciones, y subiendo los materiales educativos iniciales.

**Precondiciones:**
- El docente tiene una sesion activa con rol INSTRUCTOR.
- El sistema esta disponible y el Course Service responde a peticiones.

**Postcondiciones:**
- El curso queda registrado en PostgreSQL con estado DRAFT (solo visible para el docente).
- Los materiales educativos subidos se almacenan en S3.
- El docente puede continuar editando el curso antes de publicarlo.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Docente | Selecciona "Crear nuevo curso" en su panel de docente |
| 2 | Sistema | Presenta el formulario de creacion de curso |
| 3 | Docente | Ingresa titulo, descripcion e imagen de portada del curso |
| 4 | Docente | Agrega al menos un modulo con nombre y descripcion |
| 5 | Docente | Agrega lecciones dentro del modulo con titulo y materiales (PDF, video, texto) |
| 6 | Sistema (Web Client) | Sube los archivos de materiales a S3 mediante URL prefirmada y registra las referencias |
| 7 | Docente | Confirma la creacion del curso |
| 8 | Sistema (API Gateway) | Valida el JWT del docente y verifica rol INSTRUCTOR |
| 9 | Sistema (Course Service) | Crea el registro del curso en `schema_courses` con estado DRAFT |
| 10 | Sistema (Course Service) | Crea los registros de modulos y lecciones asociados al curso |
| 11 | Sistema | Retorna HTTP 201 con el ID del curso creado y redirige al editor del curso |

**Flujos alternos:**

*A1: Archivo de material demasiado grande*
- En el paso 6, si el archivo supera el limite configurado (ej. 500 MB para videos, 50 MB para PDFs), el sistema rechaza la subida con mensaje de error y solicitud de reduccion de tamano.

*A2: Titulo duplicado*
- En el paso 9, si el docente ya tiene un curso con el mismo titulo, el sistema muestra una advertencia (no bloquea la creacion, ya que distintos docentes pueden tener cursos con nombres similares).

*A3: Sesion expirada durante la creacion*
- Si el JWT expira durante el proceso (la sesion dura 8h), el sistema solicita al usuario volver a autenticarse. Los datos del formulario se conservan localmente en la SPA hasta 30 minutos.

**Requisitos no funcionales aplicables:**
- La respuesta a la confirmacion de creacion (paso 11) debe ser <= 2 segundos en P95 (RNF-01).
- Solo usuarios con rol INSTRUCTOR o ADMIN pueden acceder a este caso de uso (RNF-04).

---

### UC-03: Presentar y Enviar una Evaluacion

**ID:** UC-03  
**Nombre:** Presentar y Enviar una Evaluacion  
**Actor principal:** Estudiante  
**Actores secundarios:** Assessment & Adaptive Service, RabbitMQ, Analytics Service  
**Requisito relacionado:** RF-02, RF-03, RF-06  
**Driver relacionado:** DR-01 (Rendimiento), DR-02 (Escalabilidad), DR-07 (Motor Adaptativo)  
**Prioridad:** Alta  

**Descripcion breve:** El estudiante accede a una evaluacion disponible dentro de un curso, responde las preguntas y envia sus respuestas. El sistema califica automaticamente la evaluacion, aplica las reglas de adaptacion y determina el siguiente paso en el recorrido de aprendizaje.

**Precondiciones:**
- El estudiante tiene sesion activa con rol STUDENT.
- El estudiante esta inscrito en el curso al que pertenece la evaluacion.
- La evaluacion esta en estado AVAILABLE (dentro de la fecha y hora configuradas por el docente).
- El estudiante no ha agotado el numero maximo de intentos configurado.

**Postcondiciones:**
- El intento de evaluacion queda registrado en PostgreSQL con las respuestas, nota obtenida y timestamp.
- El motor adaptativo determina el siguiente paso y lo registra en el progreso del estudiante.
- Se publica un evento `evaluation.completed` en RabbitMQ para actualizacion asincrona del dashboard de analitica.
- El estudiante recibe retroalimentacion inmediata con su nota, las respuestas correctas y la indicacion del siguiente contenido.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Estudiante | Selecciona la evaluacion disponible dentro de la leccion del curso |
| 2 | Sistema (Assessment Service) | Verifica el estado de la evaluacion, la inscripcion del estudiante y el numero de intentos |
| 3 | Sistema | Presenta las preguntas de la evaluacion (opcion multiple, verdadero/falso, respuesta corta) |
| 4 | Estudiante | Responde las preguntas dentro del tiempo limite configurado |
| 5 | Estudiante | Confirma el envio de la evaluacion |
| 6 | Sistema (Assessment Service) | Califica automaticamente las preguntas de opcion multiple y verdadero/falso |
| 7 | Sistema (Assessment Service) | Consulta las reglas de adaptacion desde Redis (cache TTL=60s) o PostgreSQL si no estan en cache |
| 8 | Sistema (Assessment Service) | Evalua el resultado del estudiante contra el umbral de aprobacion configurado por el docente |
| 9a | Sistema | Si el estudiante supero el umbral: desbloquea la siguiente leccion o contenido de mayor complejidad |
| 9b | Sistema | Si el estudiante no supero el umbral: identifica los temas con menor calificacion y selecciona materiales de refuerzo asociados |
| 10 | Sistema (Assessment Service) | Persiste el intento de evaluacion en PostgreSQL con nota, respuestas y siguiente paso |
| 11 | Sistema (Assessment Service) | Publica evento `evaluation.completed` en RabbitMQ (operacion asincrona, no bloquea la respuesta) |
| 12 | Sistema | Retorna HTTP 200 con: nota obtenida, respuestas correctas, retroalimentacion y siguiente contenido desbloqueado o materiales de refuerzo |
| 13 | Sistema (Analytics Service) | Consume el evento de RabbitMQ de forma asincrona y actualiza el dashboard de progreso del estudiante y los reportes del docente |

**Flujos alternos:**

*A1: Tiempo limite agotado*
- En el paso 4, si el estudiante no envia la evaluacion antes de que expire el temporizador, el sistema envia automaticamente las respuestas registradas hasta ese momento. Las preguntas sin responder se marcan como incorrectas.

*A2: Ultimo intento agotado*
- En el paso 2, si el estudiante ya consumio el numero maximo de intentos, el sistema informa al estudiante que no puede volver a intentar la evaluacion y muestra su mejor calificacion anterior.

*A3: Evaluacion fuera del periodo de disponibilidad*
- En el paso 2, si la evaluacion esta fuera del periodo configurado (fecha de inicio o cierre), el sistema muestra un mensaje indicando que la evaluacion no esta disponible y la fecha de disponibilidad.

*A4: Perdida de conexion durante la evaluacion*
- El sistema mantiene las respuestas del estudiante en estado local de la SPA. Al recuperar la conexion, las respuestas guardadas localmente se sincronizan con el servidor. Si no se puede sincronizar antes de que expire el tiempo, se aplica el flujo A1.

**Requisitos no funcionales aplicables:**
- La latencia del envio y calificacion (pasos 5 a 12) debe ser <= 2 segundos en P95 (RNF-01).
- El sistema debe soportar 500 envios simultaneos de evaluaciones sin degradacion (RNF-03).
- Los eventos de evaluacion no deben perderse aunque el Analytics Service este temporalmente caido (RNF-02, garantizado por la persistencia de RabbitMQ).

---

### UC-04: Consultar el Progreso Personal

**ID:** UC-04  
**Nombre:** Consultar el Progreso Personal  
**Actor principal:** Estudiante  
**Actores secundarios:** Analytics Service, PostgreSQL Read Replica  
**Requisito relacionado:** RF-06  
**Driver relacionado:** DR-01 (Rendimiento)  
**Prioridad:** Alta  

**Descripcion breve:** El estudiante accede a su dashboard personal para revisar su avance en los cursos inscritos, el historial de calificaciones y los materiales de refuerzo pendientes.

**Precondiciones:**
- El estudiante tiene sesion activa con rol STUDENT.
- El estudiante esta inscrito en al menos un curso.

**Postcondiciones:**
- El estudiante visualiza su porcentaje de avance por curso, historial de evaluaciones y siguiente contenido sugerido.
- No se modifica ninguna informacion en la base de datos (operacion de solo lectura).

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Estudiante | Accede a la seccion "Mi progreso" desde el menu principal |
| 2 | Sistema (API Gateway) | Valida el JWT y enruta la peticion al Analytics Service |
| 3 | Sistema (Analytics Service) | Consulta el progreso del estudiante en la Read Replica de PostgreSQL |
| 4 | Sistema (Analytics Service) | Recupera: porcentaje de avance por curso, historial de calificaciones con fecha y retroalimentacion, y siguiente contenido sugerido por el motor adaptativo |
| 5 | Sistema | Retorna HTTP 200 con el resumen de progreso |
| 6 | Sistema (Web Client) | Presenta el dashboard con graficas de avance y listado de evaluaciones recientes |

**Flujos alternos:**

*A1: Sin actividad registrada*
- Si el estudiante esta inscrito pero no ha completado ninguna actividad, el sistema muestra el dashboard con avance al 0% y el primer contenido disponible del curso.

**Requisitos no funcionales aplicables:**
- La latencia de carga del dashboard debe ser <= 2 segundos en P95 (RNF-01).
- La informacion se actualiza automaticamente al completar cualquier actividad (RF-06, actualizacion asincronamente via RabbitMQ).

---

### UC-05: Participar en el Foro de Discusion

**ID:** UC-05  
**Nombre:** Participar en el Foro de Discusion  
**Actor principal:** Estudiante, Docente  
**Actores secundarios:** Collaboration Service, RabbitMQ (para notificaciones)  
**Requisito relacionado:** RF-04  
**Driver relacionado:** DR-03 (Disponibilidad)  
**Prioridad:** Media  

**Descripcion breve:** El usuario crea un nuevo hilo de discusion o responde a uno existente en el foro de un curso. El sistema notifica a los participantes relevantes sobre la nueva actividad.

**Precondiciones:**
- El usuario tiene sesion activa (rol STUDENT, INSTRUCTOR o ADMIN).
- El usuario esta inscrito en el curso o es el docente del curso.
- El foro del curso o modulo esta activo (no cerrado por el docente).

**Postcondiciones:**
- La publicacion queda registrada en PostgreSQL en `schema_collaboration`.
- Los usuarios suscritos al hilo reciben una notificacion (por canal de notificaciones o correo).
- La publicacion es visible para todos los participantes del curso.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Usuario | Accede al foro del curso o modulo desde la pagina del curso |
| 2 | Sistema | Presenta la lista de hilos existentes con paginacion |
| 3a | Usuario (nuevo hilo) | Selecciona "Crear nuevo hilo", ingresa titulo y contenido, y confirma |
| 3b | Usuario (respuesta) | Selecciona un hilo existente, escribe su respuesta y confirma |
| 4 | Sistema (API Gateway) | Valida el JWT y verifica la inscripcion al curso |
| 5 | Sistema (Collaboration Service) | Persiste la publicacion en PostgreSQL |
| 6 | Sistema (Collaboration Service) | Publica un evento `forum.post.created` en RabbitMQ |
| 7 | Sistema (Collaboration Service) | Retorna HTTP 201 con la publicacion creada |
| 8 | Sistema (asincrono) | El consumidor de notificaciones procesa el evento y envia notificaciones a los usuarios suscritos al hilo |

**Flujos alternos:**

*A1: Foro cerrado por el docente*
- En el paso 1, si el docente cerro el hilo o el foro, el sistema muestra las publicaciones existentes en modo solo lectura con el mensaje "Este foro esta cerrado".

*A2: Contenido inapropiado detectado*
- Si el sistema detecta (o el docente reporta) contenido inapropiado, el docente puede eliminar la publicacion desde su panel de moderacion. La publicacion se marca como DELETED sin eliminarse fisicamente de la BD (para auditorias).

**Requisitos no funcionales aplicables:**
- La publicacion de un mensaje debe responder en <= 2 segundos (RNF-01, latencia estimada ~60ms).
- Las notificaciones se entregan de forma asincrona; no impactan la latencia de la publicacion (AP-05).

---

### UC-06: Configurar Reglas de Adaptacion

**ID:** UC-06  
**Nombre:** Configurar Reglas de Adaptacion de un Curso  
**Actor principal:** Docente  
**Actores secundarios:** Course Service, Assessment & Adaptive Service, PostgreSQL, Redis  
**Requisito relacionado:** RF-03  
**Driver relacionado:** DR-07 (Motor Adaptativo), DR-05 (Mantenibilidad)  
**Prioridad:** Alta  

**Descripcion breve:** El docente configura o modifica las reglas de adaptacion de una leccion especifica: umbral de aprobacion, materiales de refuerzo para estudiantes que no superan el umbral y contenido de mayor complejidad para quienes lo superan.

**Precondiciones:**
- El docente tiene sesion activa con rol INSTRUCTOR.
- El curso y la leccion a configurar existen y pertenecen al docente.

**Postcondiciones:**
- La regla de adaptacion queda actualizada en PostgreSQL en `schema_courses.adaptation_rules`.
- La cache Redis de reglas para esa leccion se invalida (TTL natural de 60s garantiza la propagacion en la siguiente evaluacion).
- Los proximos estudiantes que completen una evaluacion de esa leccion utilizaran el nuevo umbral y los nuevos materiales asociados.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Docente | Accede al editor del curso y selecciona la leccion a configurar |
| 2 | Docente | Selecciona la opcion "Configurar reglas de adaptacion" |
| 3 | Sistema | Presenta el formulario de configuracion con el umbral actual y los materiales de refuerzo/avance actuales |
| 4 | Docente | Modifica el umbral de aprobacion (valor entre 0 y 100) |
| 5 | Docente | Selecciona o sube los materiales de refuerzo para estudiantes que no superen el umbral |
| 6 | Docente | Selecciona o indica el contenido de mayor complejidad a desbloquear para quienes lo superen |
| 7 | Docente | Confirma los cambios |
| 8 | Sistema (API Gateway) | Valida el JWT y verifica rol INSTRUCTOR y propiedad del curso |
| 9 | Sistema (Course Service) | Actualiza el registro de `adaptation_rules` en PostgreSQL |
| 10 | Sistema | Retorna HTTP 200 confirmando la actualizacion |
| 11 | Sistema | La cache Redis de reglas de esa leccion expirara en un maximo de 60 segundos (TTL natural), garantizando que la proxima evaluacion use el nuevo umbral |

**Flujos alternos:**

*A1: Umbral fuera de rango*
- En el paso 4, si el docente ingresa un valor fuera del rango 0-100, el sistema muestra validacion en tiempo real e impide el envio del formulario hasta que el valor sea valido.

*A2: Material de refuerzo no disponible*
- Si el docente selecciona un material que fue eliminado del curso, el sistema muestra un mensaje de error y solicita seleccionar un material disponible.

**Requisitos no funcionales aplicables:**
- La actualizacion de la regla debe persistir en <= 1 segundo (RNF-01).
- Los cambios tienen efecto en la siguiente evaluacion con un retraso maximo de 60 segundos (TTL de la cache Redis de reglas, AP-04 + AP-06).
- Solo el docente propietario del curso o un administrador puede modificar las reglas (RNF-04, RBAC).

---

### UC-07: Consultar Reporte de Analitica del Curso

**ID:** UC-07  
**Nombre:** Consultar Reporte de Analitica del Curso  
**Actor principal:** Docente  
**Actores secundarios:** Analytics Service, PostgreSQL Read Replica  
**Requisito relacionado:** RF-07  
**Driver relacionado:** DR-01 (Rendimiento), DR-05 (Mantenibilidad)  
**Prioridad:** Media  

**Descripcion breve:** El docente accede al dashboard de analitica de su curso para identificar patrones de desempeno, revisar los temas con mayor dificultad y exportar reportes.

**Precondiciones:**
- El docente tiene sesion activa con rol INSTRUCTOR.
- El curso tiene al menos un estudiante inscrito que haya completado alguna actividad evaluable.

**Postcondiciones:**
- El docente visualiza el reporte consolidado del curso.
- Si lo solicita, descarga el reporte en formato CSV o PDF.
- No se modifica informacion en la base de datos (operacion de solo lectura).

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Docente | Accede a "Analitica" desde el panel de su curso |
| 2 | Sistema (API Gateway) | Valida JWT, verifica rol INSTRUCTOR y propiedad del curso |
| 3 | Sistema (Analytics Service) | Ejecuta consultas de agregacion en la Read Replica de PostgreSQL |
| 4 | Sistema (Analytics Service) | Compila: calificacion promedio por evaluacion, tasa de aprobacion por leccion, temas con mayor tasa de error, lista de estudiantes con alertas de riesgo (< 30% de aprobacion) |
| 5 | Sistema | Retorna HTTP 200 con el reporte en formato JSON |
| 6 | Sistema (Web Client) | Presenta el dashboard con graficas interactivas de desempeno |
| 7 | Docente (opcional) | Selecciona "Exportar" en formato CSV o PDF |
| 8 | Sistema (Analytics Service) | Genera el archivo de exportacion y retorna la URL de descarga desde S3 |

**Flujos alternos:**

*A1: Sin datos suficientes*
- Si no hay evaluaciones completadas aun, el sistema muestra el dashboard vacio con el mensaje "No hay datos de evaluaciones para mostrar".

*A2: Alerta automatica activa*
- Si el sistema detecto que mas del 30% de los estudiantes reprobaron una evaluacion (RF-07), el docente ve una alerta destacada al inicio del dashboard con el nombre de la evaluacion y el porcentaje de reprobacion.

**Requisitos no funcionales aplicables:**
- La carga del dashboard debe responder en <= 2 segundos en P95 (RNF-01, consultas optimizadas en Read Replica con indices).
- La exportacion a CSV puede tardar hasta 800ms adicionales para cursos grandes (latencia aceptable documentada en el SAD).
- Los datos del reporte consolidado pueden tener hasta 24 horas de retraso respecto a la actividad mas reciente (RF-07, consistencia eventual de Analytics).

---

### UC-08: Gestionar Usuarios del Sistema

**ID:** UC-08  
**Nombre:** Gestionar Usuarios del Sistema  
**Actor principal:** Administrador  
**Actores secundarios:** User Service, PostgreSQL  
**Requisito relacionado:** RF-08  
**Driver relacionado:** DR-04 (Seguridad)  
**Prioridad:** Alta  

**Descripcion breve:** El administrador crea, edita, activa o desactiva cuentas de usuario en el sistema, asignando el rol correspondiente.

**Precondiciones:**
- El administrador tiene sesion activa con rol ADMIN.
- El sistema esta disponible y el User Service responde a peticiones.

**Postcondiciones:**
- Los cambios en las cuentas de usuario quedan registrados en PostgreSQL.
- Si una cuenta se desactiva, su token JWT activo se invalida en Redis en la proxima verificacion de sesion.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Administrador | Accede al panel de administracion de usuarios |
| 2 | Sistema | Presenta la lista paginada de usuarios con filtros por rol y estado |
| 3a | Administrador (creacion) | Selecciona "Nuevo usuario", ingresa nombre, correo institucional y rol, y confirma |
| 3b | Administrador (edicion) | Selecciona un usuario existente, modifica sus datos y confirma |
| 3c | Administrador (desactivacion) | Selecciona un usuario activo y selecciona "Desactivar cuenta" |
| 4 | Sistema (API Gateway) | Valida el JWT del administrador y verifica rol ADMIN |
| 5 | Sistema (User Service) | Ejecuta la operacion correspondiente en PostgreSQL |
| 6 | Sistema (User Service) | En caso de desactivacion, marca la sesion del usuario como REVOKED en Redis |
| 7 | Sistema | Retorna HTTP 200 o 201 confirmando la operacion |

**Flujos alternos:**

*A1: Correo ya registrado*
- En el paso 3a, si el correo ingresado ya existe en el sistema, el sistema muestra el error "El correo ya esta registrado" e impide la creacion de la cuenta duplicada.

*A2: Intento de desactivar la propia cuenta*
- El sistema impide que el administrador desactive su propia cuenta activa y muestra el mensaje "No es posible desactivar la cuenta con la que tienes sesion activa".

**Requisitos no funcionales aplicables:**
- La operacion de creacion o modificacion de usuario debe responder en <= 2 segundos en P95 (RNF-01).
- Solo usuarios con rol ADMIN pueden ejecutar este caso de uso (RNF-04, RBAC).
- Los datos personales de los usuarios se manejan conforme a la Ley 1581 de 2012 (RNF-04).

---

### Resumen de Casos de Uso

| ID | Nombre | Actor Principal | Requisito | Prioridad |
|----|--------|-----------------|-----------|-----------|
| UC-01 | Iniciar Sesion en la Plataforma | Estudiante, Docente, Administrador | RF-08 | Alta |
| UC-02 | Crear un Curso | Docente | RF-01 | Alta |
| UC-03 | Presentar y Enviar una Evaluacion | Estudiante | RF-02, RF-03, RF-06 | Alta |
| UC-04 | Consultar el Progreso Personal | Estudiante | RF-06 | Alta |
| UC-05 | Participar en el Foro de Discusion | Estudiante, Docente | RF-04 | Media |
| UC-06 | Configurar Reglas de Adaptacion | Docente | RF-03 | Alta |
| UC-07 | Consultar Reporte de Analitica del Curso | Docente | RF-07 | Media |
| UC-08 | Gestionar Usuarios del Sistema | Administrador | RF-08 | Alta |

---

**Fin del Documento SRS v2.0**
