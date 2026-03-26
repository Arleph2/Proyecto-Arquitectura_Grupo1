# Software Requirements Specification (SRS)
## Plataforma de Aprendizaje Adaptativo y Colaborativo

**Versión:** 1.0  
**Fecha:** 22/03/2026  
---

## 1. INTRODUCCIÓN

### 1.1 Propósito

Este documento describe los requisitos funcionales y no funcionales de la **Plataforma de Aprendizaje Adaptativo y Colaborativo**. Su propósito es establecer de forma clara qué debe hacer el sistema y como debe hacerlo antes de comenzar el diseño arquitectónico.

### 1.2 Alcance

La plataforma permite a estudiantes y docentes interactuar en un entorno digital de aprendizaje. El sistema adapta el recorrido educativo de cada estudiante en función de su progreso y resultados en evaluaciones, y fomenta la colaboración mediante foros, grupos de estudio y tutorías entre pares. Los profesores pueden diseñar cursos, publicar materiales, definir evaluaciones y monitorear el progreso del grupo. La plataforma genera analítica de desempeño para apoyar la toma de decisiones pedagógicas.

### 1.3 Definiciones, Acrónimos y Abreviaciones

| Término | Definición |
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

### 1.4 Referencias

- Enunciado del proyecto: *Plataforma de Aprendizaje Adaptativo y Colaborativo* - Arquitectura de Software, Versión 0.9, Pontificia Universidad Javeriana, 2026.
- ISO/IEC 25010:2011 – Modelo de calidad de software.
- Ley 1581 de 2012 – Protección de datos personales en Colombia.

---

## 2. DESCRIPCIÓN GENERAL DEL SISTEMA

### 2.1 Perspectiva del Producto

La plataforma es un sistema independiente que se integrará en el futuro con otras plataformas educativas institucionales (interoperabilidad). No reemplaza el sistema de registro académico existente, pero puede consumir datos de este mediante APIs. Opera sobre infraestructura cloud y es accesible desde navegadores web y dispositivos móviles modernos.

### 2.2 Funciones del Producto

1. Gestión de cursos: creación, edición y publicación de contenidos educativos por parte de docentes.
2. Aprendizaje adaptativo: ajuste automático del recorrido educativo según el desempeño del estudiante.
3. Evaluaciones: creación, presentación y calificación automática de quizzes y exámenes.
4. Colaboración entre pares: foros de discusión, grupos de estudio y tutorías entre estudiantes.
5. Seguimiento del progreso: dashboards de progreso para estudiantes y analítica de desempeño para docentes.
6. Gestión de usuarios: registro, autenticación y control de acceso basado en roles.

### 2.3 Características de Usuarios

| Tipo de Usuario | Descripción | Nivel de Expertise |
|-----------------|-------------|--------------------|
| **Estudiante** | Accede a contenidos, realiza evaluaciones, participa en grupos y foros, consulta su progreso | Básico - no técnico |
| **Docente** | Crea y gestiona cursos, publica materiales, define evaluaciones, monitorea el progreso del grupo | Medio - maneja herramientas digitales básicas |
| **Administrador** | Gestiona usuarios, configura la plataforma, supervisa el uso del sistema | Alto - perfil técnico-administrativo |

### 2.4 Restricciones del Sistema

**Restricciones regulatorias:**
- Cumplimiento con la Ley 1581 de 2012 (protección de datos personales en Colombia).
- Los datos de los estudiantes no pueden compartirse con terceros sin consentimiento explícito.

---

## 3. REQUISITOS FUNCIONALES

### RF-01: Gestión de Cursos
**Prioridad:** Alta  
**Descripción:** El sistema debe permitir a los docentes crear, editar, organizar y publicar cursos con módulos, lecciones y materiales educativos (videos, PDFs, enlaces externos).

**Criterios de aceptación:**
- El docente puede crear un curso con título, descripción, imagen de portada y módulos en menos de 10 pasos.
- Los materiales pueden ser archivos PDF, videos o textos.
- El curso puede estar en estado borrador (solo visible para el docente) o publicado (visible para estudiantes inscritos).

---

### RF-02: Evaluaciones y Calificación Automática
**Prioridad:** Alta  
**Descripción:** El sistema debe permitir a los docentes crear evaluaciones con preguntas de opción múltiple, verdadero/falso y respuesta corta, y al sistema calificar automáticamente las respuestas cuando corresponda.

**Criterios de aceptación:**
- El docente puede crear quizzes con mínimo 5 tipos de pregunta distintos.
- El sistema califica automáticamente preguntas de opción múltiple y verdadero/falso al momento de envío.
- El estudiante recibe retroalimentación inmediata con la nota y las respuestas correctas tras completar la evaluación.
- El docente puede configurar intentos máximos, tiempo límite y fecha de disponibilidad.

---

### RF-03: Aprendizaje Adaptativo
**Prioridad:** Alta  
**Descripción:** El sistema debe adaptar el recorrido de aprendizaje del estudiante según su desempeño en evaluaciones. Si el estudiante obtiene una calificación inferior al umbral definido, el sistema sugiere materiales de refuerzo; si supera el umbral, habilita contenidos de mayor complejidad.

**Criterios de aceptación:**
- El docente puede configurar un umbral de aprobación por lección (valor entre 0 y 100).
- Si el estudiante no supera el umbral, el sistema muestra automáticamente materiales de refuerzo relacionados con los temas con menor calificación.
- Si el estudiante supera el umbral, el sistema desbloquea la siguiente lección o un contenido de mayor complejidad.
- Las reglas de adaptación son configurables por el docente por curso y módulo.

---

### RF-04: Foros de Discusión y Colaboración
**Prioridad:** Media  
**Descripción:** El sistema debe proporcionar foros de discusión asociados a cada curso donde estudiantes y docentes puedan publicar preguntas, respuestas y comentarios, fomentando el aprendizaje colaborativo.

**Criterios de aceptación:**
- Cada curso tiene al menos un foro general y puede tener foros por módulo.
- Los usuarios pueden crear hilos, responder y dar "me gusta" a publicaciones.
- El docente puede moderar el foro: fijar publicaciones, eliminar contenido inapropiado y cerrar hilos.
- El sistema envía notificaciones al usuario cuando alguien responde a su publicación.

---

### RF-05: Grupos de Estudio y Tutorías entre Pares
**Prioridad:** Media  
**Descripción:** El sistema debe permitir la formación de grupos de estudio dentro de un curso, así como la identificación de estudiantes con alto dominio en un tema para ofrecer tutorías a compañeros con dificultades.

**Criterios de aceptación:**
- Un estudiante o docente puede crear un grupo de estudio con nombre, descripción y máximo de integrantes.
- El sistema identifica automáticamente estudiantes con calificación promedio superior al 85% en un tema y les ofrece la opción de ser tutor.
- Los grupos de estudio tienen un espacio de chat privado y pueden compartir archivos.
- El docente puede ver la lista de grupos activos y su composición.

---

### RF-06: Seguimiento del Progreso del Estudiante
**Prioridad:** Alta  
**Descripción:** El sistema debe presentar a cada estudiante su progreso detallado por curso: porcentaje de avance y calificaciones por evaluación.

**Criterios de aceptación:**
- El sistema muestra el porcentaje de avance del estudiante en cada curso inscrito.
- El estudiante puede ver el historial de calificaciones con fecha, nota y retroalimentación.
- La información se actualiza en tiempo real tras cada actividad completada.

---

### RF-07: Analítica para Docentes
**Prioridad:** Media  
**Descripción:** El sistema debe proveer a los docentes herramientas de analítica que les permitan identificar patrones de desempeño, temas con mayor dificultad y el estado de las actividades colaborativas.

**Criterios de aceptación:**
- El docente accede a un reporte por curso con calificación promedio, tasa de aprobación y temas más fallidos.
- El sistema genera alertas automáticas cuando más del 30% de los estudiantes reprueba una evaluación.
- El sistema genera un reporte por estudiante con resultados y materiales utilizados.
- El docente puede exportar reportes en formato CSV o PDF.
- Los datos se actualizan máximo cada 24 horas para reportes consolidados y en tiempo real para vistas individuales.

---

### RF-08: Gestión de Usuarios y Roles
**Prioridad:** Alta  
**Descripción:** El sistema debe gestionar el registro, autenticación y autorización de usuarios mediante roles definidos (estudiante, docente, administrador), con soporte para inicio de sesión con credenciales institucionales.

**Criterios de aceptación:**
- El sistema soporta registro con correo institucional y contraseña.
- Los roles son: estudiante, docente y administrador, con permisos diferenciados.
- El administrador puede crear, editar y desactivar cuentas de usuario.
- Las sesiones expiran tras 8 horas de inactividad y el token JWT se invalida al cerrar sesión.

---

## 4. REQUISITOS NO FUNCIONALES

### RNF-01: Rendimiento (Performance)
**ID:** RNF-01  
**Categoría:** Performance  
**Descripción:** Las operaciones principales del sistema deben responder dentro de tiempos aceptables para garantizar una experiencia de usuario fluida.

**Métricas:**
- Tiempo de respuesta de operaciones principales (carga de curso, envío de evaluación, consulta de progreso): ≤ 2 segundos en P95.
- Tiempo de carga inicial de la aplicación web: ≤ 2 segundos en P95.
- Throughput mínimo: ≥ 500 peticiones por segundo en el pico de uso.

**Justificación:** El enunciado establece explícitamente que las operaciones principales deben responder en menos de 2 segundos. Tiempos superiores generan abandono y frustración en contextos educativos donde los estudiantes acceden masivamente antes de exámenes.

---

### RNF-02: Disponibilidad (Availability)
**ID:** RNF-02  
**Categoría:** Availability  
**Descripción:** La plataforma debe estar disponible de forma continua, especialmente durante períodos de evaluación.

**Métricas:**
- Disponibilidad mínima: 99.5% mensual (máximo ~3.6 horas de downtime/mes).
- RTO : ≤ 1 hora ante fallas de infraestructura.
- RPO : ≤ 15 minutos de pérdida de datos ante falla.

**Justificación:** El enunciado establece disponibilidad mínima del 99.5%. Las instituciones educativas no pueden permitirse caídas durante períodos de evaluación.

---

### RNF-03: Escalabilidad (Scalability)
**ID:** RNF-03  
**Categoría:** Scalability  
**Descripción:** El sistema debe soportar el crecimiento en número de usuarios y cursos sin degradación del rendimiento.

**Métricas:**
- Soporte para al menos 5,000 usuarios concurrentes sin degradación del rendimiento.
- Capacidad de escalar horizontalmente hasta 10,000 usuarios concurrentes con ajuste de infraestructura.
- El sistema debe soportar al menos 500 cursos activos simultáneamente.
- El tiempo de escala automática ante picos de carga debe ser ≤ 5 minutos.

**Justificación:** El enunciado establece que el sistema debe soportar múltiples cursos y miles de usuarios concurrentes. En contextos educativos, los picos de carga coinciden con períodos de entrega y evaluación.

---

### RNF-04: Seguridad (Security)
**ID:** RNF-04  
**Categoría:** Security  
**Descripción:** El sistema debe proteger los datos personales de los usuarios y garantizar que solo usuarios autorizados accedan a recursos según su rol.

**Métricas:**
- Autenticación mediante JWT con expiración configurable (máximo 8 horas).
- Todas las comunicaciones deben usar TLS 1.2 o superior.
- RBAC con verificación en cada petición al backend.
- Los datos sensibles (contraseñas) se almacenan con hash bcrypt (factor de costo ≥ 12).
- Cumplimiento con Ley 1581 de 2012: consentimiento explícito, política de privacidad visible.

**Justificación:** El enunciado requiere autenticación segura, protección de datos personales y RBAC. La naturaleza educativa del sistema implica datos de menores de edad .

---

### RNF-05: Mantenibilidad (Maintainability)
**ID:** RNF-05  
**Categoría:** Maintainability  
**Descripción:** La arquitectura debe permitir incorporar nuevos módulos o funcionalidades sin afectar el funcionamiento del sistema existente.

**Métricas:**
- El tiempo para agregar un nuevo módulo independiente al sistema no debe superar 2 semanas de desarrollo.
- El sistema debe contar con pipelines de CI/CD que permitan despliegues sin tiempo de inactividad.
- Los logs del sistema deben estructurarse en formato JSON con niveles INFO, WARN, ERROR y trazas de correlación.

**Justificación:** El enunciado establece que la arquitectura debe facilitar la incorporación futura de módulos. Un equipo pequeño requiere alta automatización del proceso de desarrollo y despliegue.

---

### RNF-06: Interoperabilidad (Interoperability)
**ID:** RNF-06  
**Categoría:** Interoperability  
**Descripción:** El sistema debe permitir integraciones futuras con otras plataformas educativas.

**Métricas:**
- Todas las APIs del sistema deben seguir el estándar REST con respuestas en JSON.
- Las APIs deben estar documentadas con OpenAPI 3.0.
- El sistema debe soportar el estándar xAPI (Tin Can API) para registro de actividades de aprendizaje.
- Las APIs públicas deben tener versionado (ej: /api/v1/) para garantizar retrocompatibilidad.

**Justificación:** El enunciado establece explícitamente que el sistema debe permitir integraciones futuras con otras plataformas educativas. La adopción de estándares abiertos reduce el costo de integración.

---

## 5. DRIVERS ARQUITECTURALES IDENTIFICADOS

| ID | Driver | Valor/Métrica | Prioridad |
|----|--------|---------------|-----------|
| **DR-01** | Rendimiento | Operaciones principales ≤ 2 seg (P95) | Alta |
| **DR-02** | Escalabilidad | ≥ 5,000 usuarios concurrentes | Alta |
| **DR-03** | Disponibilidad | ≥ 99.5% uptime mensual | Alta |
| **DR-04** | Seguridad | RBAC, TLS, JWT, Ley 1581 | Alta |
| **DR-05** | Mantenibilidad | Módulos independientes, CI/CD, cobertura ≥ 70% | Media |
| **DR-06** | Interoperabilidad | APIs REST + OpenAPI 3.0 + xAPI | Media |
| **DR-07** | Motor Adaptativo | Reglas de adaptación configurables por docente | Alta |


**Fin del Documento SRS**