# Software Requirements Specification

## for

## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Version 2.0 approved**

**Prepared by** Equipo de Arquitectura de Software

**Pontificia Universidad Javeriana**

**26/04/2026**

---

## Revision History

| Name | Date | Reason For Changes | Version |
|------|------|--------------------|---------| 
| Equipo | 20/03/2026 | Borrador inicial | 1.0 |
| Equipo | 26/04/2026 | Reestructuracion completa segun plantilla IEEE; features con sub-requisitos REQ y secuencias estimulo/respuesta; seccion 4 interfaces externas; RNFs por sub-atributos ISO 25010; rol Directivo; escenarios de calidad al final | 2.0 |

---

# 1. Introduction

## 1.1 Purpose

Este documento cubre los requisitos de software de la **Plataforma de Aprendizaje Adaptativo y Colaborativo, version 2.0**: un LMS dirigido a instituciones educativas colombianas con motor de adaptacion basado en reglas configurables por docentes y herramientas de colaboracion entre pares (foros, grupos de estudio, tutorias).

El alcance incluye el modulo de gestion de cursos, evaluaciones con calificacion automatica, motor de aprendizaje adaptativo, colaboracion (foros, grupos y chat), seguimiento del progreso y analitica de desempeno, tableros de control para directivos, y gestion de usuarios con RBAC. El sistema de registro academico institucional y los modulos de facturacion o pagos quedan fuera del alcance.

## 1.2 Document Conventions

- Los requisitos funcionales se organizan por **system features** (seccion 3). Cada feature agrupa sub-requisitos identificados como REQ-X.Y.Z. Las prioridades de los sub-requisitos heredan la del feature padre, salvo indicacion contraria.
- Los requisitos no funcionales (seccion 5) siguen **ISO/IEC 25010:2011**, descompuestos por atributo y sub-atributo con metricas medibles.
- Las prioridades usan cuatro componentes: beneficio (1-9), penalidad (1-9), costo (1-9) y riesgo (1-9). Se clasifican en Alta (beneficio >= 7), Media (4-6) y Baja (1-3).
- Los textos en **negrita** indican nombres de roles, estados o valores de configuracion del sistema.
- "TBD" marca informacion aun no disponible.

## 1.3 Intended Audience and Reading Suggestions

- **Desarrolladores:** Leer completo con atencion especial a la seccion 3 (secuencias estimulo/respuesta y requisitos), seccion 4 (Interfaces) y seccion 5 (RNF).
- **Arquitecto de software:** Comenzar por 1.4 (Scope), luego 5 (RNF), 7 (Drivers) y 8 (Escenarios de Calidad), que orientan las decisiones del SAD.
- **Gerente de proyecto / Directivo institucional:** Secciones 1 y 2 para vision general; seccion 2.3 (User Classes) para entender los perfiles de usuario.
- **Testers / QA:** Seccion 3 (secuencias estimulo/respuesta y criterios) y seccion 8 (Escenarios de Calidad).
- **Docentes y usuarios finales:** Seccion 2 para contexto del producto y seccion 3 para funcionalidades.

El documento avanza de lo general a lo especifico: la seccion 1 presenta el proyecto, la 2 describe el contexto, la 3 detalla funcionalidades, la 4 cubre interfaces, la 5 define atributos de calidad, y las secciones 7 y 8 conectan los requisitos con la arquitectura.

## 1.4 Project Scope

La **Plataforma de Aprendizaje Adaptativo y Colaborativo** es un producto nuevo desarrollado como proyecto academico en la Pontificia Universidad Javeriana. Busca mejorar los resultados de aprendizaje mediante dos mecanismos diferenciadores:

1. **Motor de aprendizaje adaptativo basado en reglas:** Al completar una evaluacion, el sistema compara la calificacion del estudiante contra un umbral que el docente configura por leccion. Si la calificacion queda por debajo del umbral, el sistema entrega automaticamente materiales de refuerzo en los temas con menor puntaje; si alcanza o supera el umbral, desbloquea contenido de mayor complejidad o la siguiente leccion. El motor **no modifica el recorrido curricular** — la secuencia de modulos y lecciones la define el docente — sino que alimenta al estudiante con recursos adicionales donde muestra debilidades. El docente puede ajustar las reglas sin intervencion tecnica ni redespliegue del sistema.

2. **Herramientas de colaboracion entre pares:** Foros de discusion por curso y modulo, grupos de estudio con chat en tiempo real, e identificacion automatica de candidatos a tutor (estudiantes con calificacion promedio >85%) para asistir a companeros con dificultades.

**Beneficios esperados por perfil de usuario:**
- **Estudiantes:** retroalimentacion inmediata, materiales de refuerzo personalizados, colaboracion con pares y tutorias.
- **Docentes:** creacion y gestion de cursos, configuracion del motor adaptativo, visibilidad del progreso individual y grupal, alertas automaticas cuando mas del 30% de estudiantes reprueba una evaluacion, y exportacion de reportes.
- **Directivos:** tableros de control con metricas globales (tasa de aprobacion, cursos con mayor dificultad, estudiantes activos) para decisiones estrategicas. El directivo no accede al detalle de cursos individuales; su vision es institucional y agregada.
- **Administradores:** gestion centralizada de usuarios, roles y configuracion.

**En cuanto a restricciones:** la plataforma no reemplaza el sistema de registro academico institucional ni gestiona inscripciones oficiales, notas oficiales, certificados o facturacion. No implementa inteligencia artificial ni machine learning; el motor es determinista basado en reglas. SCORM no esta soportado en esta version. El cumplimiento de la Ley 1581 de 2012 es obligatorio porque el sistema gestiona datos de estudiantes que pueden ser menores de edad.

## 1.5 References

| # | Titulo | Autor | Version | Fecha | Fuente |
|---|--------|-------|---------|-------|--------|
| 1 | Enunciado del proyecto: Plataforma de Aprendizaje Adaptativo y Colaborativo | PUJ | 0.9 | 2026 | Curso Arquitectura de Software |
| 2 | ISO/IEC 25010:2011 — SQuaRE Quality Model | ISO/IEC | 2011 | 2011 | iso.org |
| 3 | Ley 1581 de 2012 — Proteccion de Datos Personales | Congreso de Colombia | - | 2012 | funcionpublica.gov.co |
| 4 | Software Architecture in Practice | Bass, Clements & Kazman | 3ra ed. | 2012 | Addison-Wesley |
| 5 | SAD de la Plataforma | Equipo | 3.0 | 26/04/2026 | Repositorio del proyecto |
| 6 | Fundamentals of Software Architecture | Richards & Ford | 1ra ed. | 2020 | O'Reilly |
| 7 | xAPI Specification | ADL Initiative | 2.0 | 2023 | adlnet.gov |

---

# 2. Overall Description

## 2.1 Product Perspective

La plataforma es un **producto nuevo e independiente**: no reemplaza ni extiende ningun sistema existente. En su primera version opera de forma autonoma; en versiones futuras se integrara con sistemas de registro academico institucionales mediante APIs REST y el protocolo xAPI.

Dentro del ecosistema educativo convive con el sistema de registro academico (matriculas y notas oficiales), plataformas de videoconferencia y repositorios documentales. La plataforma los complementa con aprendizaje adaptativo y colaboracion que los LMS tradicionales no ofrecen.

[Espacio para diagrama de contexto del sistema]

## 2.2 Product Features

Resumen de alto nivel; los detalles estan en la Seccion 3.

1. **Gestion de cursos:** Creacion, edicion, organizacion en modulos/lecciones, gestion de materiales, publicacion.
2. **Evaluaciones y calificacion automatica:** Quizzes con multiples tipos de pregunta, temporizador, calificacion automatica y retroalimentacion inmediata.
3. **Motor de aprendizaje adaptativo:** Reglas por leccion (umbral, refuerzo, avance), aplicacion automatica post-evaluacion. No modifica el recorrido curricular.
4. **Foros de discusion:** Foros por curso y modulo, hilos, respuestas, moderacion y notificaciones asincronas.
5. **Grupos de estudio y tutorias:** Grupos con chat, identificacion automatica de tutores (>85%), asignacion manual.
6. **Seguimiento del progreso:** Dashboard individual del estudiante y vista por estudiante para el docente.
7. **Analitica para docentes y directivos:** Reportes por curso, alertas automaticas, exportacion y tableros institucionales para directivos.
8. **Gestion de usuarios y roles:** Registro, JWT, RBAC con 4 roles (Estudiante, Docente, Directivo, Administrador).

[Espacio para diagrama de alto nivel de relaciones entre features]

## 2.3 User Classes and Characteristics

| Clase | Frecuencia | Funciones | Expertise | Privilegio | Importancia |
|-------|-----------|-----------|-----------|------------|-------------|
| **Estudiante** | Diaria. Picos antes de evaluaciones | Cursos, evaluaciones, materiales adaptativos, foros, grupos, progreso | Basico | STUDENT | Primario |
| **Docente** | Diaria, distribuida | Crear cursos, materiales, evaluaciones, reglas adaptativas, moderar foros, analitica | Medio | INSTRUCTOR | Primario |
| **Directivo** | Semanal/quincenal | Tableros institucionales con metricas agregadas. Sin acceso al detalle de cursos ni estudiantes individuales | Medio - estrategico | DIRECTOR | Secundario |
| **Administrador** | Segun necesidad | Gestion de cuentas, roles, configuracion, estado del sistema | Alto - tecnico | ADMIN | Secundario |

El **Estudiante** es el usuario mas importante, seguido del **Docente**.

## 2.4 Operating Environment

- **Cliente:** Navegadores Chrome, Firefox, Safari, Edge (ultimas 2 versiones). Windows 10+, macOS 11+, iOS 15+, Android 11+.
- **Servidor:** AWS (us-east-1 o sa-east-1). ECS Fargate, RDS PostgreSQL 15, ElastiCache Redis 7, SNS/SQS, S3, CloudWatch.
- **Backend:** Node.js 20 LTS sobre Docker.
- **BD:** PostgreSQL 15.
- **Coexistencia:** Opera junto al sistema de registro academico sin interferencia.

## 2.5 Design and Implementation Constraints

- **Regulatorias:** Ley 1581/2012. Datos en region America. Consentimiento explicito.
- **Equipo:** 3-4 personas. Descarta microservicios. Requiere alta automatizacion CI/CD.
- **Presupuesto:** BD compartida con schemas logicos.
- **Tecnologias fijas:** React.js 18, Node.js, PostgreSQL.
- **Comunicaciones:** REST/JSON, HTTPS (TLS 1.2+), OpenAPI 3.0.
- **Seguridad:** JWT RS256, bcrypt (costo >= 12), RBAC.

## 2.6 User Documentation

- Manual de usuario Estudiante (web, seccion "Ayuda").
- Manual de usuario Docente (web + PDF).
- Guia rapida Directivos (PDF, 2-3 paginas).
- Manual de administracion (Markdown, repositorio).
- Documentacion API: OpenAPI 3.0 en /docs de cada servicio.

## 2.7 Assumptions and Dependencies

**Suposiciones:** Usuarios con correo institucional; conexion internet estable (>= 1 Mbps); poblacion inicial <= 5,000 concurrentes (escala a 10,000); docentes dispuestos a configurar reglas; contenido provisto por docentes.

**Dependencias:** AWS; PostgreSQL 15; Node.js 20 LTS; servicio de correo institucional.

---

# 3. System Features

## 3.1 Gestion de Cursos

### 3.1.1 Description and Priority
Permite a docentes crear, editar, organizar y publicar cursos con modulos, lecciones y materiales educativos (PDFs, videos, textos, enlaces).
**Prioridad:** Alta — Beneficio: 9, Penalidad: 9, Costo: 5, Riesgo: 3.

### 3.1.2 Stimulus/Response Sequences
- Docente selecciona "Crear curso" -> Sistema presenta formulario -> Docente ingresa titulo, descripcion, portada -> Sistema crea curso en DRAFT.
- Docente selecciona "Agregar modulo" -> Ingresa nombre y descripcion -> Sistema registra modulo.
- Docente selecciona "Agregar leccion" en modulo -> Ingresa titulo, sube materiales -> Sistema almacena y asocia.
- Docente selecciona "Publicar" -> Sistema verifica >= 1 modulo con >= 1 leccion -> Estado cambia a PUBLISHED.
- Docente edita curso PUBLISHED -> Cambios visibles inmediatamente para inscritos.

### 3.1.3 Functional Requirements
REQ-3.1.1: El docente puede crear un curso con titulo (obligatorio, max 200 chars), descripcion (obligatoria, max 2000 chars) e imagen de portada (opcional, JPG/PNG, max 5 MB). Estado inicial: DRAFT.

REQ-3.1.2: El docente puede agregar, editar, reordenar y eliminar modulos (nombre obligatorio, descripcion opcional) y lecciones (titulo obligatorio) dentro de un curso.

REQ-3.1.3: El docente puede subir materiales asociados a lecciones. Formatos soportados: PDF, MP4, enlaces YouTube/Vimeo, texto enriquecido, enlaces externos. Max 50 MB por archivo. Los materiales se sirven via URLs prefirmadas.

REQ-3.1.4: El docente puede cambiar el estado entre DRAFT y PUBLISHED. Publicar requiere >= 1 modulo con >= 1 leccion. Despublicar un curso con inscritos activos requiere confirmacion.

REQ-3.1.5: Los cursos en DRAFT no son visibles en el catalogo. Los cursos PUBLISHED si aparecen y aceptan inscripciones.

REQ-3.1.6: Solo los roles INSTRUCTOR o ADMIN pueden gestionar cursos. Un docente unicamente gestiona sus propios cursos.

---

## 3.2 Evaluaciones y Calificacion Automatica

### 3.2.1 Description and Priority
Permite crear quizzes con multiples tipos de pregunta y calificar automaticamente respuestas objetivas con retroalimentacion inmediata.
**Prioridad:** Alta — Beneficio: 9, Penalidad: 8, Costo: 6, Riesgo: 4.

### 3.2.2 Stimulus/Response Sequences
- Docente crea evaluacion en leccion -> Agrega preguntas -> Configura intentos, tiempo, disponibilidad -> Sistema registra.
- Estudiante selecciona evaluacion -> Sistema verifica inscripcion, periodo, intentos -> Presenta preguntas con temporizador -> Estudiante responde y envia -> Sistema califica y muestra retroalimentacion + siguiente contenido.
- Tiempo agotado -> Sistema envia respuestas registradas y califica.
- Intentos agotados -> Sistema muestra "Intentos agotados".

### 3.2.3 Functional Requirements
REQ-3.2.1: Tipos de pregunta soportados: opcion multiple (unica), opcion multiple (multiple), verdadero/falso, respuesta corta, emparejamiento.

REQ-3.2.2: Configurable por evaluacion: intentos maximos (1 a ilimitados), tiempo limite (minutos, opcional), fecha inicio/fin de disponibilidad.

REQ-3.2.3: Calificacion automatica al envio para opcion multiple, verdadero/falso y emparejamiento. La respuesta corta queda pendiente de revision manual por el docente.

REQ-3.2.4: Al terminar, el estudiante ve: nota (%), respuestas correctas e incorrectas, y retroalimentacion por pregunta (si el docente la configuro).

REQ-3.2.5: Si el tiempo se agota, el sistema envia automaticamente las respuestas registradas hasta ese momento.

REQ-3.2.6: Las respuestas en progreso se mantienen en almacenamiento local (SPA) para recuperacion ante desconexion.

REQ-3.2.7: Cada intento registra: studentId, respuestas, nota, timestamp de inicio y timestamp de envio.

---

## 3.3 Motor de Aprendizaje Adaptativo

### 3.3.1 Description and Priority
Adapta los materiales entregados segun el desempeno en evaluaciones. Las reglas son deterministas y el docente las configura sin intervencion tecnica. El motor no modifica el recorrido curricular; entrega contenido complementario donde el estudiante muestra debilidades.
**Prioridad:** Alta — Beneficio: 9, Penalidad: 7, Costo: 5, Riesgo: 5. Es el diferenciador funcional del sistema.

### 3.3.2 Stimulus/Response Sequences
- Docente configura regla: selecciona leccion -> umbral (0-100) -> materiales de refuerzo -> contenido avanzado -> confirma -> Sistema almacena (efecto en <= 60s).
- Post-evaluacion: Sistema califica -> consulta regla -> Si nota < umbral: muestra refuerzo. Si nota >= umbral: desbloquea avance -> Registra decision en progreso.

### 3.3.3 Functional Requirements
REQ-3.3.1: Configurable por leccion: umbral (0-100), materiales de refuerzo (existentes o nuevos) y contenido avanzado a desbloquear.

REQ-3.3.2: Validacion en tiempo real del umbral (0-100); el formulario impide enviar valores fuera del rango.

REQ-3.3.3: Tras la calificacion, si existe una regla: nota < umbral -> incluir refuerzo en la respuesta; nota >= umbral -> incluir siguiente leccion o contenido avanzado.

REQ-3.3.4: La decision adaptativa queda registrada en el historial de progreso del estudiante.

REQ-3.3.5: Las reglas son modificables sin redespliegue. El cambio tiene efecto en la siguiente evaluacion, con un retraso maximo de 60s.

REQ-3.3.6: Solo el INSTRUCTOR propietario o un ADMIN pueden gestionar las reglas.

---

## 3.4 Foros de Discusion y Colaboracion

### 3.4.1 Description and Priority
Foros por curso y modulo para publicar preguntas, respuestas y comentarios.
**Prioridad:** Media — Beneficio: 6, Penalidad: 4, Costo: 4, Riesgo: 2.

### 3.4.2 Stimulus/Response Sequences
- Usuario crea hilo -> escribe titulo y contenido -> Sistema registra y muestra en el foro.
- Usuario responde a hilo -> Sistema registra y envia notificaciones asincronas.
- Docente modera -> fija, elimina o cierra un hilo -> Sistema ejecuta. La eliminacion usa soft delete.

### 3.4.3 Functional Requirements
REQ-3.4.1: Cada curso tiene al menos un foro general. El docente puede crear foros adicionales por modulo.

REQ-3.4.2: Los inscritos y el docente pueden crear hilos y responder dentro de cualquier foro del curso.

REQ-3.4.3: Se permite un "Me gusta" por publicacion y por usuario.

REQ-3.4.4: El docente puede fijar hilos (max 3 por foro), eliminarlos (soft delete) o cerrarlos (solo lectura).

REQ-3.4.5: Las notificaciones asincronas se envian al responder un hilo o por actividad en hilos suscritos, sin afectar la latencia de las operaciones principales.

REQ-3.4.6: Los foros e hilos cerrados se muestran en modo de solo lectura.

---

## 3.5 Grupos de Estudio y Tutorias entre Pares

### 3.5.1 Description and Priority
Grupos de estudio con chat y tutorias por pares.
**Prioridad:** Media — Beneficio: 5, Penalidad: 3, Costo: 5, Riesgo: 3.

### 3.5.2 Stimulus/Response Sequences
- Estudiante crea grupo -> nombre, descripcion, max integrantes -> Sistema crea grupo y habilita chat.
- Estudiante se une al grupo -> Sistema lo agrega y evalua si es candidato a tutor (>85%).
- Grupo lleno -> Sistema muestra "Grupo completo" y sugiere alternativas.

### 3.5.3 Functional Requirements
REQ-3.5.1: El estudiante puede crear un grupo con nombre, descripcion y maximo de integrantes (2-20).

REQ-3.5.2: Cualquier estudiante puede unirse a grupos con plazas disponibles. Se permiten multiples grupos por curso.

REQ-3.5.3: El sistema identifica automaticamente candidatos a tutor (calificacion promedio > 85%). La aceptacion es voluntaria.

REQ-3.5.4: El docente puede asignar un tutor manualmente.

REQ-3.5.5: El chat en tiempo real por grupo soporta texto y archivos (PDF e imagenes, max 10 MB). El historial es persistente.

REQ-3.5.6: El docente puede ver la lista de grupos, su composicion y el tutor asignado.

---

## 3.6 Seguimiento del Progreso

### 3.6.1 Description and Priority
Dashboard de progreso para estudiantes y vista por estudiante para docentes.
**Prioridad:** Alta — Beneficio: 7, Penalidad: 6, Costo: 3, Riesgo: 2.

### 3.6.2 Stimulus/Response Sequences
- Estudiante selecciona "Mi progreso" -> Sistema muestra avance, calificaciones y siguiente contenido recomendado.
- Docente selecciona un estudiante -> Sistema muestra su progreso individual detallado.

### 3.6.3 Functional Requirements
REQ-3.6.1: El estudiante ve: porcentaje de avance por curso, historial de calificaciones (fecha y nota), materiales de refuerzo pendientes y siguiente contenido disponible.

REQ-3.6.2: El progreso se actualiza en tiempo real tras cada actividad completada.

REQ-3.6.3: El docente puede consultar, por cada estudiante: avance, calificaciones, materiales de refuerzo utilizados y decisiones adaptativas tomadas por el motor.

---

## 3.7 Analitica para Docentes y Directivos

### 3.7.1 Description and Priority
Analitica de desempeno para docentes (a nivel de curso) y tableros de vision institucional para directivos.
**Prioridad:** Media — Beneficio: 6, Penalidad: 5, Costo: 5, Riesgo: 3.

### 3.7.2 Stimulus/Response Sequences
- Docente accede a "Analitica" -> Sistema muestra calificacion promedio, tasa de aprobacion, temas dificiles y alertas activas.
- Mas del 30% de estudiantes reprueba una evaluacion -> El sistema genera una alerta en el dashboard del docente.
- Docente selecciona "Exportar" -> Elige CSV o PDF -> Descarga inmediata.
- Directivo accede a "Tablero Institucional" -> Sistema muestra metricas globales agregadas.

### 3.7.3 Functional Requirements
REQ-3.7.1: El dashboard por curso muestra: calificacion promedio, tasa de aprobacion por evaluacion, temas con mayor tasa de error y estudiantes en riesgo.

REQ-3.7.2: El sistema genera una alerta automatica si mas del 30% reprueba una evaluacion (con minimo 10 intentos completados). La alerta indica la evaluacion, el porcentaje de reprobacion y los temas implicados.

REQ-3.7.3: El docente puede exportar el dashboard en formato CSV y PDF.

REQ-3.7.4: Los reportes consolidados pueden tener hasta 24 horas de retraso. Las vistas individuales son en tiempo real.

REQ-3.7.5: El directivo (DIRECTOR) accede a un tablero con: cursos activos, estudiantes activos, tasa de aprobacion global, cursos con mayor reprobacion y alertas globales.

REQ-3.7.6: El directivo no accede al detalle de cursos ni de estudiantes individuales. Su vision es exclusivamente agregada e institucional.

REQ-3.7.7: El tablero institucional puede tener hasta 24 horas de retraso y es filtrable por periodo (semestre o mes).

---

## 3.8 Gestion de Usuarios y Roles

### 3.8.1 Description and Priority
Registro, autenticacion y autorizacion con roles.
**Prioridad:** Alta — Beneficio: 9, Penalidad: 9, Costo: 3, Riesgo: 2.

### 3.8.2 Stimulus/Response Sequences
- Registro: nombre, correo, contraseña -> validacion -> bcrypt -> cuenta creada.
- Login: correo + contraseña -> validacion -> JWT RS256 -> redireccion al dashboard del rol.
- Login fallido -> HTTP 401. Tras 5 fallos consecutivos -> bloqueo por 15 min.
- Admin gestiona usuarios -> crea, edita o desactiva -> al desactivar, invalida sesiones activas.

### 3.8.3 Functional Requirements
REQ-3.8.1: Registro con correo institucional unico y contraseña con al menos 8 caracteres, 1 mayuscula y 1 numero.

REQ-3.8.2: Las contraseñas se almacenan con bcrypt, costo >= 12.

REQ-3.8.3: JWT RS256 con payload: userId, email, role, exp (max 8h).

REQ-3.8.4: Las sesiones expiran tras 8 horas de inactividad e invalidan al hacer logout.

REQ-3.8.5: Roles disponibles: STUDENT, INSTRUCTOR, DIRECTOR, ADMIN. El sistema verifica el rol en cada peticion.

REQ-3.8.6: El Admin puede crear, editar, activar y desactivar cuentas asignando el rol correspondiente.

REQ-3.8.7: Desactivar una cuenta invalida sus sesiones activas (estado REVOKED). No hay auto-desactivacion por inactividad.

REQ-3.8.8: El sistema registra en log de auditoria: login exitoso y fallido, logout, y operaciones de escritura. Cada entrada incluye timestamp, userId e IP de origen. Retencion minima: 1 año.

---

# 4. External Interface Requirements

## 4.1 User Interfaces

SPA React.js 18. Diseño responsive (desktop >= 1024px, tablet >= 768px, movil >= 375px). Menu lateral en desktop, hamburguesa en movil. Dashboard por rol al login. Validacion en tiempo real. Toasts para confirmaciones y errores. Contraste WCAG 2.1 AA. El detalle visual queda en el documento separado de UI specification.

## 4.2 Hardware Interfaces

Sin interfaces con hardware especializado. El acceso ocurre a traves del navegador sobre hardware estandar. Los archivos se almacenan en AWS S3.

## 4.3 Software Interfaces

| Componente | Version | Interfaz |
|------------|---------|----------|
| PostgreSQL | 15 | BD relacional. Cada servicio usa su schema via Prisma ORM. TCP. |
| Redis (ElastiCache) | 7 | Cache. Sesiones (TTL=8h), cursos (TTL=3600s), reglas (TTL=60s). TCP. |
| AWS S3 | - | Almacenamiento de materiales. URLs prefirmadas. HTTPS. |
| AWS SNS/SQS | - | Mensajeria asincrona. Assessment publica eventos JSON; Analytics y Collaboration consumen. |
| AWS CloudWatch | - | Logs JSON estructurados y metricas. |

## 4.4 Communications Interfaces

- HTTPS (TLS 1.2+) para toda comunicacion cliente-servidor.
- REST/JSON con OpenAPI 3.0. Versionado /api/v1/.
- WebSocket (WSS) para el chat en tiempo real de grupos de estudio.
- HTTP interno en VPC entre API Gateway y servicios backend.
- SNS/SQS para eventos asincronos internos. Formato JSON con esquema TypeScript.
- Throughput minimo: 500 req/s en pico.

---

# 5. Other Nonfunctional Requirements

Estandar de referencia: **ISO/IEC 25010:2011**. Cada atributo se descompone en sub-atributos con metricas verificables.

## 5.1 Performance Requirements

**Sub-atributo: Time Behaviour**
- RNF-01a: Operaciones principales <= 2 seg P95.
- RNF-01b: Login <= 500ms P95 bajo 500 logins simultaneos.
- RNF-01c: SPA first contentful paint <= 2 seg P95.

**Sub-atributo: Resource Utilization**
- RNF-01d: CPU por instancia <= 70%.
- RNF-01e: Memoria por instancia <= 512 MB.
- RNF-01f: Pool BD: max 20 conexiones por servicio (PgBouncer).

**Sub-atributo: Capacity**
- RNF-01g: Throughput >= 500 req/s en pico.
- RNF-01h: >= 500 cursos activos simultaneos.

## 5.2 Safety Requirements

- RNF-02a: Las respuestas de evaluacion se mantienen en almacenamiento local del navegador ante desconexion; el sistema las sincroniza automaticamente al restaurar la conexion.
- RNF-02b: Las eliminaciones usan soft delete.
- RNF-02c: Las operaciones destructivas requieren confirmacion explicita del usuario.

## 5.3 Security Requirements

**Sub-atributo: Resistir ataques (Confidentiality + Integrity)**
- RNF-03a: TLS 1.2+ en toda comunicacion externa.
- RNF-03b: Contraseñas con bcrypt, costo >= 12.
- RNF-03c: Proteccion contra inyeccion SQL mediante ORM.
- RNF-03d: Rate limiting: 100 req/min/IP en endpoints publicos.
- RNF-03e: Bloqueo por 15 min tras 5 intentos de login fallidos.

**Sub-atributo: Autenticar y autorizar actores**
- RNF-03f: JWT RS256, max 8h.
- RNF-03g: RBAC en dos capas (Gateway + servicio).
- RNF-03h: Log de auditoria: login, logout y escrituras con timestamp, userId e IP. Retencion >= 1 año.

**Sub-atributo: Proteccion de datos (Compliance)**
- RNF-03i: Ley 1581/2012: consentimiento explicito y politica de privacidad visible.
- RNF-03j: Prohibido compartir datos con terceros sin consentimiento.
- RNF-03k: Datos almacenados en region America.

## 5.4 Software Quality Attributes

**Disponibilidad (Reliability > Availability)**
- RNF-04a: 99.5% mensual (max ~3.6h downtime/mes).
- RNF-04b: La falla de un servicio no critico no afecta a los criticos (degradacion controlada).
- RNF-04c: Deteccion de fallo <= 30 seg.
- RNF-04d: RTO <= 1h.
- RNF-04e: RPO <= 15 min.
- RNF-04f: Failover de BD automatico <= 60 seg (Multi-AZ).

**Escalabilidad (Capacity)**
- RNF-05a: 5,000 concurrentes sin degradacion.
- RNF-05b: Escala a 10,000 con ajuste de configuracion.
- RNF-05c: Auto-scale en <= 5 min.
- RNF-05d: Auto-scaling activado si CPU > 70% por 3 min. Min 2 instancias en servicios criticos, max 10.

**Mantenibilidad (Maintainability)**
- RNF-06a: Modularidad: max 3 dependencias entre servicios. Comunicacion solo por REST o eventos.
- RNF-06b: Modificabilidad: un modulo independiente nuevo tarda <= 2 semanas. Las reglas adaptativas son modificables sin redespliegue.
- RNF-06c: Testabilidad: logs JSON con Correlation ID. Health checks en /health/live y /health/ready.
- RNF-06d: Desplegabilidad: CI/CD Blue-Green sin downtime. Rollback en <= 5 min.

**Interoperabilidad (Compatibility)**
- RNF-07a: REST/JSON, OpenAPI 3.0.
- RNF-07b: Versionado /api/v1/.
- RNF-07c: Soporte xAPI planificado para versiones futuras.

---

# 6. Other Requirements

- **Base de datos:** PostgreSQL 15 con schemas separados por dominio. Sin cross-schema queries.
- **Internacionalizacion:** Solo español (Colombia) en v1. La arquitectura debe permitir agregar otros idiomas.
- **Legal:** Ley 1581/2012 (ver RNF-03i a RNF-03k).
- **Reutilizacion:** Las librerias compartidas (libs/) se diseñan para reutilizacion en otros proyectos.

---

# 7. Drivers Arquitecturales

Priorizados por votacion del equipo (escala 1-5), de mayor a menor. Los drivers con puntuacion >= 4 determinan las principales decisiones arquitectonicas.

| Orden | ID | Driver | Tipo | Sub-atributo ISO 25010 | Metrica | Puntuacion |
|-------|----|--------|------|------------------------|---------|------------|
| 1 | DR-01 | Rendimiento | RNF | Time Behaviour | <= 2 seg P95 | 4.8 |
| 2 | DR-04 | Seguridad | RNF | Resistir ataques + Autenticar actores | TLS + JWT + RBAC + Ley 1581 | 4.7 |
| 3 | DR-07 | Motor Adaptativo | RF | Funcional | Reglas configurables, runtime, sin redespliegue | 4.6 |
| 4 | DR-03 | Disponibilidad | RNF | Fault Tolerance + Recoverability | 99.5%, RTO <= 1h, RPO <= 15min | 4.5 |
| 5 | DR-02 | Escalabilidad | RNF | Capacity | >= 5,000 concurrentes | 4.3 |
| 6 | DR-05 | Mantenibilidad | RNF | Modularity + Deployability | CI/CD, Blue-Green | 3.5 |
| 7 | DR-06 | Interoperabilidad | RNF | Interoperability | REST + OpenAPI + xAPI | 3.2 |

---

# 8. Escenarios de Calidad

Formato Bass et al.: fuente, estimulo, artefacto, entorno, respuesta, medida. Se definieron antes del diseño para evitar sesgo de confirmacion.

### ESC-01: Rendimiento en Envio de Evaluacion (DR-01)
| Elemento | Descripcion |
|----------|-------------|
| Fuente | 500 estudiantes concurrentes |
| Estimulo | Envio simultaneo de evaluaciones |
| Artefacto | Assessment & Adaptive Service |
| Entorno | Operacion normal, hora pico |
| Respuesta | Califica, aplica reglas, retorna resultado |
| Medida | <= 2 seg P95 |

### ESC-02: Disponibilidad ante Falla No Critica (DR-03)
| Elemento | Descripcion |
|----------|-------------|
| Fuente | Falla interna Analytics Service |
| Estimulo | Analytics deja de responder |
| Artefacto | API Gateway, Assessment Service |
| Entorno | Operacion normal |
| Respuesta | Circuit Breaker detecta la falla. Los servicios criticos siguen operando. HTTP 503 solo para analitica. Los eventos se acumulan en el broker sin perdida |
| Medida | Deteccion <= 30 seg. Servicios criticos al 99.5% |

### ESC-03: Seguridad ante Fuerza Bruta (DR-04)
| Elemento | Descripcion |
|----------|-------------|
| Fuente | Atacante externo |
| Estimulo | 100 intentos de login en 1 min |
| Artefacto | User Service, API Gateway |
| Entorno | Operacion normal |
| Respuesta | Bloqueo tras 5 intentos fallidos. Rate limiting activo |
| Medida | 0 accesos no autorizados. Bloqueo en <= 1 seg tras el 5to intento |

### ESC-04: Escalabilidad ante Pico (DR-02)
| Elemento | Descripcion |
|----------|-------------|
| Fuente | Inicio de examenes |
| Estimulo | Carga escala de 1,000 a 5,000 concurrentes en 10 min |
| Artefacto | Todos los servicios (ECS) |
| Entorno | De operacion normal a pico |
| Respuesta | Auto-scaling activa nuevas instancias |
| Medida | Instancias disponibles en <= 5 min. P95 <= 2 seg |

### ESC-05: Modificabilidad Motor Adaptativo (DR-07, DR-05)
| Elemento | Descripcion |
|----------|-------------|
| Fuente | Docente |
| Estimulo | Cambia el umbral de 70 a 80 |
| Artefacto | Course Service, Assessment Service |
| Entorno | Operacion normal, con estudiantes evaluando |
| Respuesta | Regla actualizada en BD. Cache expira por TTL. El proximo estudiante usa el nuevo umbral |
| Medida | Efecto en <= 60 seg. Sin redespliegue. Sin interrupcion del servicio |

---

## Appendix A: Glossary

| Termino | Definicion |
|---------|------------|
| DRAFT | Estado de curso no publicado |
| PUBLISHED | Estado de curso visible en catalogo |
| RBAC | Control de acceso basado en roles |
| JWT | Token firmado con identidad y rol |
| RS256 | Algoritmo RSA-SHA256 para firma JWT |
| bcrypt | Funcion hash para contraseñas |
| TTL | Tiempo de vida en cache |
| P95 | Percentil 95 |
| SPA | Single Page Application |
| Soft delete | Eliminacion logica sin borrado fisico |
| Motor adaptativo | Evaluador de reglas para entrega de refuerzo o avance |
| xAPI | Estandar para datos de actividades de aprendizaje |
| DLQ | Cola de mensajes fallidos |
| Multi-AZ | Despliegue en multiples zonas de disponibilidad |
| PgBouncer | Proxy de conexiones para PostgreSQL |
| RTO | Tiempo maximo para restaurar servicio |
| RPO | Maxima perdida de datos aceptable |

## Appendix B: Analysis Models

[Espacio reservado para: diagrama de casos de uso UML, diagrama de contexto, diagrama de dominio, diagramas de secuencia de escenarios]

## Appendix C: Issues List

| ID | Descripcion | Estado |
|----|-------------|--------|
| ISS-01 | Definir si el Directivo accede al detalle de cursos en versiones futuras | Pendiente |
| ISS-02 | Politica de retencion de auditoria superior a 1 año | Pendiente |
| ISS-03 | Integracion con registro academico via API | Pendiente |
| ISS-04 | Soporte SCORM en futuras versiones | Pendiente |
| ISS-05 | Workflow de calificacion manual para respuesta corta | TBD |

---

**Fin del Documento SRS v2.0**
