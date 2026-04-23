# Software Requirements Specification (SRS)
## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Version:** 1.0
**Fecha:** 20/03/2026


---

## 1. INTRODUCCION

### 1.1 Proposito

Este documento describe los requisitos funcionales y no funcionales de la **Plataforma de Aprendizaje Adaptativo y Colaborativo**. Su proposito es establecer de forma clara que debe hacer el sistema y como debe hacerlo antes de comenzar el diseño arquitectonico.

### 1.2 Alcance

La plataforma permite a estudiantes y docentes interactuar en un entorno digital de aprendizaje. El sistema adapta el recorrido educativo de cada estudiante en funcion de su progreso y resultados en evaluaciones, y fomenta la colaboracion mediante foros, grupos de estudio y tutorias entre pares. Los profesores pueden diseñar cursos, publicar materiales, definir evaluaciones y monitorear el progreso del grupo. La plataforma genera analitica de desempeño para apoyar la toma de decisiones pedagogicas.

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
| ORM | Object-Relational Mapping |
| TTL | Time To Live |
| DLQ | Dead Letter Queue |
| CDN | Content Delivery Network |
| WAF | Web Application Firewall |
| CI/CD | Continuous Integration / Continuous Delivery |
| AZ | Availability Zone |

### 1.4 Referencias

- Enunciado del proyecto: *Plataforma de Aprendizaje Adaptativo y Colaborativo* — Arquitectura de Software, Version 0.9, Pontificia Universidad Javeriana, 2026.
- ISO/IEC 25010:2011 — Modelo de calidad de software.
- Ley 1581 de 2012 — Proteccion de datos personales en Colombia.
- Bass, L., Clements, P. & Kazman, R. (2012). *Software Architecture in Practice* (3ra ed.). Addison-Wesley.
- SAD_4+1_PlataformaAdaptativa.md — Documento de Arquitectura (define patrones y decisiones de diseño).

---

## 2. DESCRIPCION GENERAL DEL SISTEMA

### 2.1 Perspectiva del Producto

La plataforma es un sistema independiente que se integrara en el futuro con otras plataformas educativas institucionales (interoperabilidad). No reemplaza el sistema de registro academico existente, pero puede consumir datos de este mediante APIs. Opera sobre infraestructura cloud y es accesible desde navegadores web y dispositivos moviles modernos.

### 2.2 Funciones del Producto

1. Gestion de cursos: creacion, edicion y publicacion de contenidos educativos por parte de docentes.
2. Aprendizaje adaptativo: ajuste automatico del recorrido educativo segun el desempeño del estudiante.
3. Evaluaciones: creacion, presentacion y calificacion automatica de quizzes y examenes.
4. Colaboracion entre pares: foros de discusion, grupos de estudio y tutorias entre estudiantes.
5. Seguimiento del progreso: dashboards de progreso para estudiantes y analitica de desempeño para docentes.
6. Gestion de usuarios: registro, autenticacion y control de acceso basado en roles.

### 2.3 Caracteristicas de Usuarios

| Tipo de Usuario | Descripcion | Nivel de Expertise |
|-----------------|-------------|---------------------|
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
**Descripcion:** El sistema debe adaptar el recorrido de aprendizaje del estudiante segun su desempeño en evaluaciones. Si el estudiante obtiene una calificacion inferior al umbral definido, el sistema sugiere materiales de refuerzo; si supera el umbral, habilita contenidos de mayor complejidad.

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
**Descripcion:** El sistema debe permitir la formacion de grupos de estudio dentro de un curso, asi como la identificacion de estudiantes con alto dominio en un tema para ofrecer tutorias a compañeros con dificultades.

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
**Descripcion:** El sistema debe proveer a los docentes herramientas de analitica que les permitan identificar patrones de desempeño, temas con mayor dificultad y el estado de las actividades colaborativas.

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
- El sistema soporta registro con correo institucional y contraseña.
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
- Tiempo de respuesta de operaciones principales (carga de curso, envio de evaluacion, consulta de progreso): ≤ 2 segundos en P95.
- Tiempo de carga inicial de la aplicacion web: ≤ 2 segundos en P95.
- Throughput minimo: ≥ 500 peticiones por segundo en el pico de uso.

**Justificacion:** El enunciado establece explicitamente que las operaciones principales deben responder en menos de 2 segundos. Tiempos superiores generan abandono y frustracion en contextos educativos donde los estudiantes acceden masivamente antes de examenes.

---

### RNF-02: Disponibilidad (Availability)
**ID:** RNF-02
**Categoria:** Availability
**Descripcion:** La plataforma debe estar disponible de forma continua, especialmente durante periodos de evaluacion.

**Metricas:**
- Disponibilidad minima: 99.5% mensual (maximo ~3.6 horas de downtime/mes).
- RTO: ≤ 1 hora ante fallas de infraestructura.
- RPO: ≤ 15 minutos de perdida de datos ante falla.

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
- El tiempo de escala automatica ante picos de carga debe ser ≤ 5 minutos.

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
- Los datos sensibles (contraseñas) se almacenan con hash bcrypt (factor de costo ≥ 12).
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

**Justificacion:** El enunciado establece que la arquitectura debe facilitar la incorporacion futura de modulos. Un equipo pequeño requiere alta automatizacion del proceso de desarrollo y despliegue.

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
| **DR-01** | Rendimiento | Operaciones principales ≤ 2 seg (P95) | Alta |
| **DR-02** | Escalabilidad | ≥ 5,000 usuarios concurrentes | Alta |
| **DR-03** | Disponibilidad | ≥ 99.5% uptime mensual | Alta |
| **DR-04** | Seguridad | RBAC, TLS, JWT, Ley 1581 | Alta |
| **DR-05** | Mantenibilidad | Modulos independientes, CI/CD, cobertura ≥ 70% | Media |
| **DR-06** | Interoperabilidad | APIs REST + OpenAPI 3.0 + xAPI | Media |
| **DR-07** | Motor Adaptativo | Reglas de adaptacion configurables por docente | Alta |


---

## 6. CASOS DE USO

Esta seccion documenta los casos de uso principales del sistema en formato extendido. Los casos de uso se derivan directamente de los requisitos funcionales del capitulo 3 y se vinculan a los actores identificados en la seccion 2.3.

---

### UC-01: Iniciar Sesion en la Plataforma

**ID:** UC-01
**Nombre:** Iniciar Sesion en la Plataforma
**Actor principal:** Estudiante, Docente, Administrador
**Actores secundarios:** User Service
**Requisito relacionado:** RF-08
**Driver relacionado:** DR-04 (Seguridad), DR-01 (Rendimiento)
**Prioridad:** Alta

**Descripcion breve:** El usuario ingresa sus credenciales institucionales y el sistema valida su identidad, generando un token JWT para las siguientes interacciones.

**Precondiciones:**
- El usuario tiene una cuenta registrada en el sistema con correo institucional y contraseña.
- El sistema esta disponible y el User Service responde a peticiones.

**Postcondiciones:**
- El usuario recibe un token JWT firmado con RS256 valido por 8 horas.
- La sesion activa se registra en el sistema con TTL=8h.
- El usuario es redirigido a su dashboard segun su rol (estudiante, docente o administrador).

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Usuario | Accede a la URL de la plataforma y selecciona "Iniciar sesion" |
| 2 | Sistema | Presenta el formulario de inicio de sesion con campos de correo y contraseña |
| 3 | Usuario | Ingresa su correo institucional y contraseña y confirma |
| 4 | Sistema (API Gateway) | Recibe la peticion POST /api/v1/auth/login y la enruta al User Service |
| 5 | Sistema (User Service) | Busca el usuario en la base de datos por correo electronico |
| 6 | Sistema (User Service) | Verifica la contraseña contra el hash almacenado |
| 7 | Sistema (User Service) | Genera un token JWT RS256 con claims: userId, email, role, exp (8h) |
| 8 | Sistema (User Service) | Registra la referencia de sesion activa con TTL=8h |
| 9 | Sistema | Retorna HTTP 200 con el token JWT al cliente |
| 10 | Sistema (Web Client) | Almacena el JWT en memoria de la aplicacion y redirige al dashboard correspondiente al rol |

**Flujos alternos:**

*A1: Credenciales incorrectas*
- En el paso 6, si la verificacion falla, el sistema retorna HTTP 401 con mensaje generico "Credenciales invalidas".
- El sistema registra el intento fallido en logs con timestamp, IP y correo utilizado.
- Tras 5 intentos fallidos consecutivos, el sistema aplica un bloqueo temporal de 15 minutos para esa cuenta.

*A2: Cuenta desactivada*
- En el paso 5, si el usuario existe pero su cuenta esta en estado INACTIVE, el sistema retorna HTTP 403 con mensaje "Cuenta desactivada. Contacte al administrador".

*A3: Sesion ya activa*
- Si el usuario ya tiene una sesion valida, el sistema permite crear una nueva sesion sin invalidar la anterior (soporte a multiples dispositivos).

**Requisitos no funcionales aplicables:**
- La latencia de este caso de uso debe ser ≤ 500ms en P95 bajo 500 logins simultaneos (RNF-01).
- La contraseña viaja cifrada mediante TLS 1.3 y nunca se almacena en texto plano (RNF-04).

---

### UC-02: Crear un Curso

**ID:** UC-02
**Nombre:** Crear un Curso
**Actor principal:** Docente
**Actores secundarios:** Course Service, Almacenamiento de archivos
**Requisito relacionado:** RF-01
**Driver relacionado:** DR-05 (Mantenibilidad), DR-04 (Seguridad)
**Prioridad:** Alta

**Descripcion breve:** El docente crea un nuevo curso en la plataforma, definiendo su estructura de modulos y lecciones, y subiendo los materiales educativos iniciales.

**Precondiciones:**
- El docente tiene una sesion activa con rol INSTRUCTOR.
- El sistema esta disponible y el Course Service responde a peticiones.

**Postcondiciones:**
- El curso queda registrado en la base de datos con estado DRAFT (solo visible para el docente).
- Los materiales educativos subidos se almacenan en el servicio de archivos.
- El docente puede continuar editando el curso antes de publicarlo.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Docente | Selecciona "Crear nuevo curso" en su panel de docente |
| 2 | Sistema | Presenta el formulario de creacion de curso |
| 3 | Docente | Ingresa titulo, descripcion e imagen de portada del curso |
| 4 | Docente | Agrega al menos un modulo con nombre y descripcion |
| 5 | Docente | Agrega lecciones dentro del modulo con titulo y materiales (PDF, video, texto) |
| 6 | Sistema (Web Client) | Solicita URL prefirmada al Course Service y sube los archivos de materiales |
| 7 | Docente | Confirma la creacion del curso |
| 8 | Sistema (API Gateway) | Valida el JWT del docente y verifica rol INSTRUCTOR |
| 9 | Sistema (Course Service) | Crea el registro del curso en la base de datos con estado DRAFT |
| 10 | Sistema (Course Service) | Crea los registros de modulos y lecciones asociados al curso |
| 11 | Sistema | Retorna HTTP 201 con el ID del curso creado y redirige al editor del curso |

**Flujos alternos:**

*A1: Archivo de material demasiado grande*
- En el paso 6, si el archivo supera el limite configurado, el sistema rechaza la subida con mensaje de error.

*A2: Titulo duplicado*
- En el paso 9, si el docente ya tiene un curso con el mismo titulo, el sistema muestra una advertencia (no bloquea la creacion).

*A3: Sesion expirada durante la creacion*
- Si el JWT expira durante el proceso, el sistema solicita al usuario volver a autenticarse. Los datos del formulario se conservan localmente en la SPA hasta 30 minutos.

**Requisitos no funcionales aplicables:**
- La respuesta a la confirmacion de creacion debe ser ≤ 2 segundos en P95 (RNF-01).
- Solo usuarios con rol INSTRUCTOR o ADMIN pueden acceder a este caso de uso (RNF-04).

---

### UC-03: Presentar y Enviar una Evaluacion

**ID:** UC-03
**Nombre:** Presentar y Enviar una Evaluacion
**Actor principal:** Estudiante
**Actores secundarios:** Assessment & Adaptive Service, Sistema de Mensajeria, Analytics Service
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
- El intento de evaluacion queda registrado en la base de datos con las respuestas, nota obtenida y timestamp.
- El motor adaptativo determina el siguiente paso y lo registra en el progreso del estudiante.
- Se publica un evento asincrono para actualizacion del dashboard de analitica.
- El estudiante recibe retroalimentacion inmediata con su nota, las respuestas correctas y la indicacion del siguiente contenido.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Estudiante | Selecciona la evaluacion disponible dentro de la leccion del curso |
| 2 | Sistema (Assessment Service) | Verifica el estado de la evaluacion, la inscripcion del estudiante y el numero de intentos |
| 3 | Sistema | Presenta las preguntas de la evaluacion |
| 4 | Estudiante | Responde las preguntas dentro del tiempo limite configurado |
| 5 | Estudiante | Confirma el envio de la evaluacion |
| 6 | Sistema (Assessment Service) | Califica automaticamente las preguntas de opcion multiple y verdadero/falso |
| 7 | Sistema (Assessment Service) | Consulta las reglas de adaptacion (desde cache si disponible, o desde la base de datos) |
| 8 | Sistema (Assessment Service) | Evalua el resultado del estudiante contra el umbral de aprobacion |
| 9a | Sistema | Si el estudiante supero el umbral: desbloquea la siguiente leccion o contenido de mayor complejidad |
| 9b | Sistema | Si el estudiante no supero el umbral: selecciona materiales de refuerzo asociados |
| 10 | Sistema (Assessment Service) | Persiste el intento de evaluacion en la base de datos |
| 11 | Sistema (Assessment Service) | Publica evento asincrono de evaluacion completada (no bloquea la respuesta) |
| 12 | Sistema | Retorna HTTP 200 con: nota obtenida, respuestas correctas, retroalimentacion y siguiente contenido |
| 13 | Sistema (Analytics Service) | Consume el evento de forma asincrona y actualiza el dashboard de progreso |

**Flujos alternos:**

*A1: Tiempo limite agotado*
- En el paso 4, si el estudiante no envia la evaluacion antes de que expire el temporizador, el sistema envia automaticamente las respuestas registradas hasta ese momento.

*A2: ultimo intento agotado*
- En el paso 2, si el estudiante ya consumio el numero maximo de intentos, el sistema informa al estudiante que no puede volver a intentar la evaluacion.

*A3: Evaluacion fuera del periodo de disponibilidad*
- En el paso 2, si la evaluacion esta fuera del periodo configurado, el sistema muestra un mensaje indicando que la evaluacion no esta disponible.

*A4: Perdida de conexion durante la evaluacion*
- El sistema mantiene las respuestas del estudiante en estado local de la SPA. Al recuperar la conexion, las respuestas guardadas localmente se sincronizan con el servidor.

**Requisitos no funcionales aplicables:**
- La latencia del envio y calificacion (pasos 5 a 12) debe ser ≤ 2 segundos en P95 (RNF-01).
- El sistema debe soportar 500 envios simultaneos de evaluaciones sin degradacion (RNF-03).

---

### UC-04: Consultar el Progreso Personal

**ID:** UC-04
**Nombre:** Consultar el Progreso Personal
**Actor principal:** Estudiante
**Actores secundarios:** Analytics Service
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
| 3 | Sistema (Analytics Service) | Consulta el progreso del estudiante en la base de datos de lectura |
| 4 | Sistema (Analytics Service) | Recupera: porcentaje de avance por curso, historial de calificaciones y siguiente contenido sugerido |
| 5 | Sistema | Retorna HTTP 200 con el resumen de progreso |
| 6 | Sistema (Web Client) | Presenta el dashboard con graficas de avance y listado de evaluaciones recientes |

**Flujos alternos:**

*A1: Sin actividad registrada*
- Si el estudiante esta inscrito pero no ha completado ninguna actividad, el sistema muestra el dashboard con avance al 0% y el primer contenido disponible del curso.

**Requisitos no funcionales aplicables:**
- La latencia de carga del dashboard debe ser ≤ 2 segundos en P95 (RNF-01).

---

### UC-05: Participar en el Foro de Discusion

**ID:** UC-05
**Nombre:** Participar en el Foro de Discusion
**Actor principal:** Estudiante, Docente
**Actores secundarios:** Collaboration Service
**Requisito relacionado:** RF-04
**Driver relacionado:** DR-03 (Disponibilidad)
**Prioridad:** Media

**Descripcion breve:** El usuario crea un nuevo hilo de discusion o responde a uno existente en el foro de un curso. El sistema notifica a los participantes relevantes sobre la nueva actividad.

**Precondiciones:**
- El usuario tiene sesion activa (rol STUDENT, INSTRUCTOR o ADMIN).
- El usuario esta inscrito en el curso o es el docente del curso.
- El foro del curso o modulo esta activo (no cerrado por el docente).

**Postcondiciones:**
- La publicacion queda registrada en la base de datos.
- Los usuarios suscritos al hilo reciben una notificacion.
- La publicacion es visible para todos los participantes del curso.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Usuario | Accede al foro del curso o modulo desde la pagina del curso |
| 2 | Sistema | Presenta la lista de hilos existentes con paginacion |
| 3a | Usuario (nuevo hilo) | Selecciona "Crear nuevo hilo", ingresa titulo y contenido, y confirma |
| 3b | Usuario (respuesta) | Selecciona un hilo existente, escribe su respuesta y confirma |
| 4 | Sistema (API Gateway) | Valida el JWT y verifica la inscripcion al curso |
| 5 | Sistema (Collaboration Service) | Persiste la publicacion en la base de datos |
| 6 | Sistema (Collaboration Service) | Publica evento asincrono de nueva publicacion en foro |
| 7 | Sistema (Collaboration Service) | Retorna HTTP 201 con la publicacion creada |
| 8 | Sistema (asincrono) | El consumidor de notificaciones procesa el evento y envia notificaciones a los usuarios suscritos |

**Flujos alternos:**

*A1: Foro cerrado por el docente*
- En el paso 1, si el docente cerro el hilo o el foro, el sistema muestra las publicaciones existentes en modo solo lectura.

*A2: Contenido inapropiado*
- El docente puede eliminar la publicacion desde su panel de moderacion. La publicacion se marca como DELETED sin eliminarse fisicamente de la base de datos (para auditorias).

**Requisitos no funcionales aplicables:**
- La publicacion de un mensaje debe responder en ≤ 2 segundos (RNF-01).
- Las notificaciones se entregan de forma asincrona; no impactan la latencia de la publicacion.

---

### UC-06: Configurar Reglas de Adaptacion

**ID:** UC-06
**Nombre:** Configurar Reglas de Adaptacion de un Curso
**Actor principal:** Docente
**Actores secundarios:** Course Service, Assessment & Adaptive Service
**Requisito relacionado:** RF-03
**Driver relacionado:** DR-07 (Motor Adaptativo), DR-05 (Mantenibilidad)
**Prioridad:** Alta

**Descripcion breve:** El docente configura o modifica las reglas de adaptacion de una leccion especifica: umbral de aprobacion, materiales de refuerzo para estudiantes que no superan el umbral y contenido de mayor complejidad para quienes lo superan.

**Precondiciones:**
- El docente tiene sesion activa con rol INSTRUCTOR.
- El curso y la leccion a configurar existen y pertenecen al docente.

**Postcondiciones:**
- La regla de adaptacion queda actualizada en la base de datos.
- La cache de reglas para esa leccion expira en maximo 60 segundos (por TTL natural), garantizando que la proxima evaluacion use el nuevo umbral.
- Los proximos estudiantes que completen una evaluacion de esa leccion utilizaran el nuevo umbral.

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
| 9 | Sistema (Course Service) | Actualiza el registro de reglas de adaptacion en la base de datos |
| 10 | Sistema | Retorna HTTP 200 confirmando la actualizacion |

**Flujos alternos:**

*A1: Umbral fuera de rango*
- En el paso 4, si el docente ingresa un valor fuera del rango 0-100, el sistema muestra validacion en tiempo real e impide el envio del formulario.

*A2: Material de refuerzo no disponible*
- Si el docente selecciona un material que fue eliminado del curso, el sistema muestra un mensaje de error.

**Requisitos no funcionales aplicables:**
- La actualizacion de la regla debe persistir en ≤ 1 segundo (RNF-01).
- Los cambios tienen efecto en la siguiente evaluacion con un retraso maximo de 60 segundos.
- Solo el docente propietario del curso o un administrador puede modificar las reglas (RNF-04, RBAC).

---

### UC-07: Consultar Reporte de Analitica del Curso

**ID:** UC-07
**Nombre:** Consultar Reporte de Analitica del Curso
**Actor principal:** Docente
**Actores secundarios:** Analytics Service
**Requisito relacionado:** RF-07
**Driver relacionado:** DR-01 (Rendimiento), DR-05 (Mantenibilidad)
**Prioridad:** Media

**Descripcion breve:** El docente accede al dashboard de analitica de su curso para identificar patrones de desempeño, revisar los temas con mayor dificultad y exportar reportes.

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
| 3 | Sistema (Analytics Service) | Ejecuta consultas de agregacion en la base de datos de lectura |
| 4 | Sistema (Analytics Service) | Compila: calificacion promedio, tasa de aprobacion, temas con mayor tasa de error, alertas de riesgo |
| 5 | Sistema | Retorna HTTP 200 con el reporte en formato JSON |
| 6 | Sistema (Web Client) | Presenta el dashboard con graficas interactivas de desempeño |
| 7 | Docente (opcional) | Selecciona "Exportar" en formato CSV o PDF |
| 8 | Sistema (Analytics Service) | Genera el archivo de exportacion y retorna la URL de descarga |

**Flujos alternos:**

*A1: Sin datos suficientes*
- Si no hay evaluaciones completadas aun, el sistema muestra el dashboard vacio.

*A2: Alerta automatica activa*
- Si el sistema detecto que mas del 30% de los estudiantes reprobaron una evaluacion, el docente ve una alerta destacada al inicio del dashboard.

**Requisitos no funcionales aplicables:**
- La carga del dashboard debe responder en ≤ 2 segundos en P95 (RNF-01).
- Los datos del reporte consolidado pueden tener hasta 24 horas de retraso respecto a la actividad mas reciente (RF-07).

---

### UC-08: Gestionar Usuarios del Sistema

**ID:** UC-08
**Nombre:** Gestionar Usuarios del Sistema
**Actor principal:** Administrador
**Actores secundarios:** User Service
**Requisito relacionado:** RF-08
**Driver relacionado:** DR-04 (Seguridad)
**Prioridad:** Alta

**Descripcion breve:** El administrador crea, edita, activa o desactiva cuentas de usuario en el sistema, asignando el rol correspondiente.

**Precondiciones:**
- El administrador tiene sesion activa con rol ADMIN.
- El sistema esta disponible y el User Service responde a peticiones.

**Postcondiciones:**
- Los cambios en las cuentas de usuario quedan registrados en la base de datos.
- Si una cuenta se desactiva, su token JWT activo se invalida en la proxima verificacion de sesion.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Administrador | Accede al panel de administracion de usuarios |
| 2 | Sistema | Presenta la lista paginada de usuarios con filtros por rol y estado |
| 3a | Administrador (creacion) | Selecciona "Nuevo usuario", ingresa nombre, correo institucional y rol, y confirma |
| 3b | Administrador (edicion) | Selecciona un usuario existente, modifica sus datos y confirma |
| 3c | Administrador (desactivacion) | Selecciona un usuario activo y selecciona "Desactivar cuenta" |
| 4 | Sistema (API Gateway) | Valida el JWT del administrador y verifica rol ADMIN |
| 5 | Sistema (User Service) | Ejecuta la operacion correspondiente en la base de datos |
| 6 | Sistema (User Service) | En caso de desactivacion, marca la sesion del usuario como REVOKED |
| 7 | Sistema | Retorna HTTP 200 o 201 confirmando la operacion |

**Flujos alternos:**

*A1: Correo ya registrado*
- En el paso 3a, si el correo ingresado ya existe en el sistema, el sistema muestra el error "El correo ya esta registrado".

*A2: Intento de desactivar la propia cuenta*
- El sistema impide que el administrador desactive su propia cuenta activa.

**Requisitos no funcionales aplicables:**
- La operacion de creacion o modificacion de usuario debe responder en ≤ 2 segundos en P95 (RNF-01).
- Solo usuarios con rol ADMIN pueden ejecutar este caso de uso (RNF-04, RBAC).

---

### UC-09: Unirse o Crear un Grupo de Estudio

**ID:** UC-09
**Nombre:** Unirse o Crear un Grupo de Estudio
**Actor principal:** Estudiante
**Actores secundarios:** Collaboration Service, Analytics Service
**Requisito relacionado:** RF-05
**Driver relacionado:** DR-03 (Disponibilidad)
**Prioridad:** Media

**Descripcion breve:** El estudiante crea un grupo de estudio dentro de un curso o se une a uno existente. El sistema evalua automaticamente si el estudiante puede asumir el rol de tutor basandose en su desempeño.

**Precondiciones:**
- El estudiante tiene sesion activa con rol STUDENT.
- El estudiante esta inscrito en el curso donde quiere crear o unirse a un grupo.

**Postcondiciones:**
- El grupo queda registrado en la base de datos con el estudiante como miembro o creador.
- Si el estudiante tiene calificacion promedio > 85%, el sistema le ofrece el rol de tutor del grupo.
- El estudiante tiene acceso al espacio de chat privado del grupo.

**Flujo principal de eventos:**

| Paso | Actor | Accion |
|------|-------|--------|
| 1 | Estudiante | Accede a la seccion "Grupos de estudio" del curso |
| 2 | Sistema | Muestra grupos existentes con plazas disponibles y opcion "Crear grupo" |
| 3a | Estudiante (unirse) | Selecciona un grupo con plazas disponibles y confirma su incorporacion |
| 3b | Estudiante (crear) | Ingresa nombre, descripcion y maximo de integrantes del nuevo grupo |
| 4 | Sistema (API Gateway) | Valida el JWT y verifica inscripcion en el curso |
| 5 | Sistema (Collaboration Service) | Registra la operacion en la base de datos y crea el espacio de chat |
| 6 | Sistema (Analytics Service) | Evalua si el estudiante tiene calificacion promedio > 85% en el curso |
| 7 | Sistema | Si es candidato a tutor, envia notificacion al estudiante ofreciendo el rol |
| 8 | Sistema | Retorna HTTP 201 con el ID del grupo y acceso al chat |

**Flujos alternos:**

*A1: Grupo lleno*
- En el paso 3a, si el grupo ya alcanzo el maximo de integrantes, el sistema muestra el mensaje "Grupo completo" y sugiere otros grupos disponibles.

*A2: Asignacion manual de tutor por docente*
- El docente puede asignar un tutor especifico desde su panel, independientemente del umbral automatico del 85%.

**Requisitos no funcionales aplicables:**
- La creacion o adhesion al grupo debe responder en ≤ 2 segundos en P95 (RNF-01).
- Solo usuarios inscritos en el curso pueden unirse a sus grupos (RNF-04, RBAC).

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
| UC-09 | Unirse o Crear un Grupo de Estudio | Estudiante | RF-05 | Media |

---

