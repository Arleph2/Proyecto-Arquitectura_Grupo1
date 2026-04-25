-- =========================
-- COURSE
-- =========================
INSERT INTO courses VALUES
(1,'Estructuras de Datos','Curso completo de estructuras de datos','intermediate','es',true,NOW(),NOW());

-- =========================
-- MODULES
-- =========================
INSERT INTO modules VALUES
(1,1,'Fundamentos de Complejidad',1,NOW()),
(2,1,'Estructuras Lineales',2,NOW()),
(3,1,'Árboles',3,NOW()),
(4,1,'Grafos',4,NOW());

-- =========================
-- LESSONS
-- =========================
INSERT INTO lessons VALUES
(1,1,'Introducción a la complejidad algorítmica','Fundamentos',1,600,true,NOW()),
(2,1,'Notación Big-O','Análisis asintótico',2,650,false,NOW()),
(3,1,'Peor, mejor y caso promedio','Casos',3,600,false,NOW()),
(4,1,'Complejidad espacial','Memoria',4,550,false,NOW()),
(5,1,'Ejercicios de complejidad','Práctica',5,800,false,NOW()),

(6,2,'Secuencias','Concepto',1,600,false,NOW()),
(7,2,'Vectores','Arreglos',2,650,false,NOW()),
(8,2,'Listas enlazadas','Dinámicas',3,700,false,NOW()),
(9,2,'Pilas','LIFO',4,500,false,NOW()),
(10,2,'Colas','FIFO',5,500,false,NOW()),
(11,2,'Multilistas','Composición',6,700,false,NOW()),
(12,2,'Plantillas','Genéricos',7,650,false,NOW()),
(13,2,'Ejercicios estructuras lineales','Práctica',8,900,false,NOW()),

(14,3,'Fundamentos de árboles','Conceptos',1,600,false,NOW()),
(15,3,'Árbol binario','Base',2,700,false,NOW()),
(16,3,'BST','Búsqueda',3,800,false,NOW()),
(17,3,'AVL','Balanceo',4,850,false,NOW()),
(18,3,'Red-Black Trees','Balanceados',5,900,false,NOW()),
(19,3,'Montículos (Heap)','Prioridad',6,600,false,NOW()),
(20,3,'Árboles de decisión','IA',7,700,false,NOW()),
(21,3,'Quadtrees','Espacial',8,650,false,NOW()),
(22,3,'Octrees','3D',9,650,false,NOW()),
(23,3,'Kd-Trees','Multidimensional',10,700,false,NOW()),
(24,3,'Aplicaciones de árboles','Casos',11,800,false,NOW()),

(25,4,'Tablas de dispersión','Hashing',1,700,false,NOW()),
(26,4,'Fundamentos de grafos','Nodos',2,600,false,NOW()),
(27,4,'Representación de grafos','Matrices/Listas',3,700,false,NOW()),
(28,4,'Recorridos DFS y BFS','Algoritmos',4,800,false,NOW()),
(29,4,'Grafos bipartitos','Teoría',5,600,false,NOW()),
(30,4,'Euler y Hamilton','Caminos',6,750,false,NOW()),
(31,4,'Dijkstra','Shortest path',7,850,false,NOW()),
(32,4,'Prim y Kruskal','MST',8,850,false,NOW()),
(33,4,'Floyd-Warshall','All pairs',9,900,false,NOW()),
(34,4,'Aplicaciones de grafos','Casos reales',10,900,false,NOW());

-- =========================
-- CONTENT (3 PER LESSON)
-- =========================
INSERT INTO contents (id, lesson_id, type, position, created_at) VALUES
(1,1,'VIDEO',1,NOW()),(2,1,'ARTICLE',2,NOW()),(3,1,'QUIZ',3,NOW()),
(4,2,'VIDEO',1,NOW()),(5,2,'ARTICLE',2,NOW()),(6,2,'QUIZ',3,NOW()),
(7,3,'VIDEO',1,NOW()),(8,3,'ARTICLE',2,NOW()),(9,3,'QUIZ',3,NOW()),
(10,4,'VIDEO',1,NOW()),(11,4,'ARTICLE',2,NOW()),(12,4,'QUIZ',3,NOW()),
(13,5,'VIDEO',1,NOW()),(14,5,'ARTICLE',2,NOW()),(15,5,'QUIZ',3,NOW()),

(16,6,'VIDEO',1,NOW()),(17,6,'ARTICLE',2,NOW()),(18,6,'QUIZ',3,NOW()),
(19,7,'VIDEO',1,NOW()),(20,7,'ARTICLE',2,NOW()),(21,7,'QUIZ',3,NOW()),
(22,8,'VIDEO',1,NOW()),(23,8,'ARTICLE',2,NOW()),(24,8,'QUIZ',3,NOW()),
(25,9,'VIDEO',1,NOW()),(26,9,'ARTICLE',2,NOW()),(27,9,'QUIZ',3,NOW()),
(28,10,'VIDEO',1,NOW()),(29,10,'ARTICLE',2,NOW()),(30,10,'QUIZ',3,NOW()),
(31,11,'VIDEO',1,NOW()),(32,11,'ARTICLE',2,NOW()),(33,11,'QUIZ',3,NOW()),
(34,12,'VIDEO',1,NOW()),(35,12,'ARTICLE',2,NOW()),(36,12,'QUIZ',3,NOW()),
(37,13,'VIDEO',1,NOW()),(38,13,'ARTICLE',2,NOW()),(39,13,'QUIZ',3,NOW()),

(40,14,'VIDEO',1,NOW()),(41,14,'ARTICLE',2,NOW()),(42,14,'QUIZ',3,NOW()),
(43,15,'VIDEO',1,NOW()),(44,15,'ARTICLE',2,NOW()),(45,15,'QUIZ',3,NOW()),
(46,16,'VIDEO',1,NOW()),(47,16,'ARTICLE',2,NOW()),(48,16,'QUIZ',3,NOW()),
(49,17,'VIDEO',1,NOW()),(50,17,'ARTICLE',2,NOW()),(51,17,'QUIZ',3,NOW()),
(52,18,'VIDEO',1,NOW()),(53,18,'ARTICLE',2,NOW()),(54,18,'QUIZ',3,NOW()),
(55,19,'VIDEO',1,NOW()),(56,19,'ARTICLE',2,NOW()),(57,19,'QUIZ',3,NOW()),
(58,20,'VIDEO',1,NOW()),(59,20,'ARTICLE',2,NOW()),(60,20,'QUIZ',3,NOW()),
(61,21,'VIDEO',1,NOW()),(62,21,'ARTICLE',2,NOW()),(63,21,'QUIZ',3,NOW()),
(64,22,'VIDEO',1,NOW()),(65,22,'ARTICLE',2,NOW()),(66,22,'QUIZ',3,NOW()),
(67,23,'VIDEO',1,NOW()),(68,23,'ARTICLE',2,NOW()),(69,23,'QUIZ',3,NOW()),
(70,24,'VIDEO',1,NOW()),(71,24,'ARTICLE',2,NOW()),(72,24,'QUIZ',3,NOW()),

(73,25,'VIDEO',1,NOW()),(74,25,'ARTICLE',2,NOW()),(75,25,'QUIZ',3,NOW()),
(76,26,'VIDEO',1,NOW()),(77,26,'ARTICLE',2,NOW()),(78,26,'QUIZ',3,NOW()),
(79,27,'VIDEO',1,NOW()),(80,27,'ARTICLE',2,NOW()),(81,27,'QUIZ',3,NOW()),
(82,28,'VIDEO',1,NOW()),(83,28,'ARTICLE',2,NOW()),(84,28,'QUIZ',3,NOW()),
(85,29,'VIDEO',1,NOW()),(86,29,'ARTICLE',2,NOW()),(87,29,'QUIZ',3,NOW()),
(88,30,'VIDEO',1,NOW()),(89,30,'ARTICLE',2,NOW()),(90,30,'QUIZ',3,NOW()),
(91,31,'VIDEO',1,NOW()),(92,31,'ARTICLE',2,NOW()),(93,31,'QUIZ',3,NOW()),
(94,32,'VIDEO',1,NOW()),(95,32,'ARTICLE',2,NOW()),(96,32,'QUIZ',3,NOW()),
(97,33,'VIDEO',1,NOW()),(98,33,'ARTICLE',2,NOW()),(99,33,'QUIZ',3,NOW()),
(100,34,'VIDEO',1,NOW()),(101,34,'ARTICLE',2,NOW()),(102,34,'QUIZ',3,NOW());

-- =========================
-- VIDEO CONTENT
-- =========================
INSERT INTO video_contents VALUES
(1,1,'https://video.com/1',600,'internal'),
(2,4,'https://video.com/2',600,'internal'),
(3,7,'https://video.com/3',600,'internal'),
(4,10,'https://video.com/4',600,'internal'),
(5,13,'https://video.com/5',600,'internal');

-- =========================
-- QUIZZES
-- =========================
INSERT INTO quizzes VALUES
(1,3,'Quiz 1'),
(2,6,'Quiz 2'),
(3,9,'Quiz 3'),
(4,12,'Quiz 4'),
(5,15,'Quiz 5');

-- =========================
-- QUESTIONS & ANSWERS
-- =========================
INSERT INTO questions VALUES
(1,1,'¿Qué es Big-O?','MULTIPLE_CHOICE');

INSERT INTO answers VALUES
(1,1,'Complejidad',true),
(2,1,'Lenguaje',false);

-- =========================
-- REINFORCEMENT CONTENT (one VIDEO + one ARTICLE per lesson, for lessons 1-5)
-- =========================
INSERT INTO contents (id, lesson_id, type, position, purpose, created_at) VALUES
(103, 1, 'VIDEO',   1, 'REINFORCEMENT', NOW()),
(104, 1, 'ARTICLE', 2, 'REINFORCEMENT', NOW()),
(105, 2, 'VIDEO',   1, 'REINFORCEMENT', NOW()),
(106, 2, 'ARTICLE', 2, 'REINFORCEMENT', NOW()),
(107, 3, 'VIDEO',   1, 'REINFORCEMENT', NOW()),
(108, 3, 'ARTICLE', 2, 'REINFORCEMENT', NOW()),
(109, 4, 'VIDEO',   1, 'REINFORCEMENT', NOW()),
(110, 4, 'ARTICLE', 2, 'REINFORCEMENT', NOW()),
(111, 5, 'VIDEO',   1, 'REINFORCEMENT', NOW()),
(112, 5, 'ARTICLE', 2, 'REINFORCEMENT', NOW());

INSERT INTO video_contents (id, content_id, url, duration, provider) VALUES
(6,  103, 'https://reinforce.com/complejidad-intro',    900, 'internal'),
(7,  105, 'https://reinforce.com/big-o-profundo',       720, 'internal'),
(8,  107, 'https://reinforce.com/casos-complejidad',    840, 'internal'),
(9,  109, 'https://reinforce.com/complejidad-espacial', 660, 'internal'),
(10, 111, 'https://reinforce.com/ejercicios-guiados',   960, 'internal');

INSERT INTO article_contents (id, content_id, body) VALUES
(1, 104, 'Repaso profundo de complejidad algorítmica. La complejidad mide cuántos recursos (tiempo, memoria) consume un algoritmo en función del tamaño de la entrada. Dominar este concepto es fundamental para analizar y diseñar algoritmos eficientes.'),
(2, 106, 'Guía de referencia sobre notación Big-O. O(1) es constante, O(log n) logarítmica, O(n) lineal, O(n log n) cuasilineal, O(n²) cuadrática. Identificar la notación correcta requiere analizar los lazos y llamadas recursivas del algoritmo.'),
(3, 108, 'Análisis de casos en algoritmos. El peor caso define la cota superior de tiempo de ejecución. El mejor caso rara vez es relevante en la práctica. El caso promedio requiere análisis probabilístico sobre las posibles entradas.'),
(4, 110, 'Complejidad espacial: cómo medir el uso de memoria. Incluye el espacio de la pila de llamadas en algoritmos recursivos. Un algoritmo puede ser O(n) en tiempo pero O(1) en espacio si procesa los datos en flujo.'),
(5, 112, 'Estrategias para resolver ejercicios de complejidad. Identifica los lazos anidados, reduce el problema a subproblemas conocidos y verifica con ejemplos concretos antes de generalizar la notación.');

-- =========================
-- RESET SEQUENCES
-- =========================
SELECT setval(pg_get_serial_sequence('courses',        'id'), (SELECT MAX(id) FROM courses));
SELECT setval(pg_get_serial_sequence('modules',        'id'), (SELECT MAX(id) FROM modules));
SELECT setval(pg_get_serial_sequence('lessons',        'id'), (SELECT MAX(id) FROM lessons));
SELECT setval(pg_get_serial_sequence('contents',       'id'), (SELECT MAX(id) FROM contents));
SELECT setval(pg_get_serial_sequence('video_contents', 'id'), (SELECT MAX(id) FROM video_contents));
SELECT setval(pg_get_serial_sequence('quizzes',        'id'), (SELECT MAX(id) FROM quizzes));
SELECT setval(pg_get_serial_sequence('questions',      'id'), (SELECT MAX(id) FROM questions));
SELECT setval(pg_get_serial_sequence('answers',        'id'), (SELECT MAX(id) FROM answers));
