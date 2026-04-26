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
-- VIDEO CONTENT (all 34 lessons)
-- =========================
INSERT INTO video_contents (id, content_id, url, duration, provider) VALUES
(1,  1,   'https://video.com/complejidad-intro',     600, 'internal'),
(2,  4,   'https://video.com/big-o',                 600, 'internal'),
(3,  7,   'https://video.com/casos',                 600, 'internal'),
(4,  10,  'https://video.com/complejidad-espacial',  600, 'internal'),
(5,  13,  'https://video.com/ejercicios-complejidad',600, 'internal'),
(11, 16,  'https://video.com/secuencias',            540, 'internal'),
(12, 19,  'https://video.com/vectores',              600, 'internal'),
(13, 22,  'https://video.com/listas-enlazadas',      660, 'internal'),
(14, 25,  'https://video.com/pilas',                 480, 'internal'),
(15, 28,  'https://video.com/colas',                 480, 'internal'),
(16, 31,  'https://video.com/multilistas',           600, 'internal'),
(17, 34,  'https://video.com/plantillas',            560, 'internal'),
(18, 37,  'https://video.com/ejercicios-lineales',   780, 'internal'),
(19, 40,  'https://video.com/fundamentos-arboles',   540, 'internal'),
(20, 43,  'https://video.com/arbol-binario',         620, 'internal'),
(21, 46,  'https://video.com/bst',                   720, 'internal'),
(22, 49,  'https://video.com/avl',                   800, 'internal'),
(23, 52,  'https://video.com/red-black',             850, 'internal'),
(24, 55,  'https://video.com/heap',                  560, 'internal'),
(25, 58,  'https://video.com/arboles-decision',      640, 'internal'),
(26, 61,  'https://video.com/quadtrees',             580, 'internal'),
(27, 64,  'https://video.com/octrees',               580, 'internal'),
(28, 67,  'https://video.com/kd-trees',              640, 'internal'),
(29, 70,  'https://video.com/aplicaciones-arboles',  720, 'internal'),
(30, 73,  'https://video.com/hashing',               620, 'internal'),
(31, 76,  'https://video.com/fundamentos-grafos',    540, 'internal'),
(32, 79,  'https://video.com/representacion-grafos', 620, 'internal'),
(33, 82,  'https://video.com/dfs-bfs',               720, 'internal'),
(34, 85,  'https://video.com/grafos-bipartitos',     540, 'internal'),
(35, 88,  'https://video.com/euler-hamilton',        680, 'internal'),
(36, 91,  'https://video.com/dijkstra',              780, 'internal'),
(37, 94,  'https://video.com/prim-kruskal',          780, 'internal'),
(38, 97,  'https://video.com/floyd-warshall',        820, 'internal'),
(39, 100, 'https://video.com/aplicaciones-grafos',   820, 'internal');

-- =========================
-- QUIZZES (all 34 lessons)
-- =========================
INSERT INTO quizzes (id, content_id, title) VALUES
(1,  3,   'Quiz: Complejidad algorítmica'),
(2,  6,   'Quiz: Notación Big-O'),
(3,  9,   'Quiz: Casos de complejidad'),
(4,  12,  'Quiz: Complejidad espacial'),
(5,  15,  'Quiz: Ejercicios de complejidad'),
(6,  18,  'Quiz: Secuencias'),
(7,  21,  'Quiz: Vectores'),
(8,  24,  'Quiz: Listas enlazadas'),
(9,  27,  'Quiz: Pilas'),
(10, 30,  'Quiz: Colas'),
(11, 33,  'Quiz: Multilistas'),
(12, 36,  'Quiz: Plantillas'),
(13, 39,  'Quiz: Estructuras lineales'),
(14, 42,  'Quiz: Fundamentos de árboles'),
(15, 45,  'Quiz: Árbol binario'),
(16, 48,  'Quiz: BST'),
(17, 51,  'Quiz: AVL'),
(18, 54,  'Quiz: Red-Black Trees'),
(19, 57,  'Quiz: Montículos'),
(20, 60,  'Quiz: Árboles de decisión'),
(21, 63,  'Quiz: Quadtrees'),
(22, 66,  'Quiz: Octrees'),
(23, 69,  'Quiz: Kd-Trees'),
(24, 72,  'Quiz: Aplicaciones de árboles'),
(25, 75,  'Quiz: Tablas hash'),
(26, 78,  'Quiz: Fundamentos de grafos'),
(27, 81,  'Quiz: Representación de grafos'),
(28, 84,  'Quiz: DFS y BFS'),
(29, 87,  'Quiz: Grafos bipartitos'),
(30, 90,  'Quiz: Euler y Hamilton'),
(31, 93,  'Quiz: Dijkstra'),
(32, 96,  'Quiz: Prim y Kruskal'),
(33, 99,  'Quiz: Floyd-Warshall'),
(34, 102, 'Quiz: Aplicaciones de grafos');

-- =========================
-- QUESTIONS & ANSWERS (one per quiz)
-- =========================
INSERT INTO questions (id, quiz_id, question_text, type) VALUES
(1,  1,  'Que es Big-O?',                                               'MULTIPLE_CHOICE'),
(2,  2,  'Cual notacion representa crecimiento lineal?',                'MULTIPLE_CHOICE'),
(3,  3,  'Que caso define la cota superior de ejecucion?',              'MULTIPLE_CHOICE'),
(4,  4,  'Que incluye la complejidad espacial recursiva?',              'MULTIPLE_CHOICE'),
(5,  5,  'Complejidad de dos lazos anidados O(n) cada uno?',            'MULTIPLE_CHOICE'),
(6,  6,  'Que caracteriza a una secuencia?',                            'MULTIPLE_CHOICE'),
(7,  7,  'Complejidad de acceso por indice en un vector?',              'MULTIPLE_CHOICE'),
(8,  8,  'Que contiene cada nodo de una lista enlazada?',               'MULTIPLE_CHOICE'),
(9,  9,  'Principio de una pila?',                                      'MULTIPLE_CHOICE'),
(10, 10, 'Principio de una cola?',                                      'MULTIPLE_CHOICE'),
(11, 11, 'Para que sirven las multilistas?',                            'MULTIPLE_CHOICE'),
(12, 12, 'Que permiten los generics?',                                  'MULTIPLE_CHOICE'),
(13, 13, 'Que estructura es eficiente para insercion al inicio?',       'MULTIPLE_CHOICE'),
(14, 14, 'Cuantos nodos raiz tiene un arbol?',                          'MULTIPLE_CHOICE'),
(15, 15, 'Cuantos hijos max tiene un nodo en arbol binario?',           'MULTIPLE_CHOICE'),
(16, 16, 'Donde se inserta un valor menor en un BST?',                  'MULTIPLE_CHOICE'),
(17, 17, 'Que garantiza un arbol AVL?',                                 'MULTIPLE_CHOICE'),
(18, 18, 'Que usan Red-Black trees para balance?',                      'MULTIPLE_CHOICE'),
(19, 19, 'En un max-heap la raiz contiene?',                            'MULTIPLE_CHOICE'),
(20, 20, 'Para que se usan arboles de decision en IA?',                 'MULTIPLE_CHOICE'),
(21, 21, 'En cuantos cuadrantes divide un Quadtree?',                   'MULTIPLE_CHOICE'),
(22, 22, 'En cuantos octantes divide un Octree?',                       'MULTIPLE_CHOICE'),
(23, 23, 'Para que sirven los arboles Kd?',                             'MULTIPLE_CHOICE'),
(24, 24, 'Que estructura usa Huffman?',                                 'MULTIPLE_CHOICE'),
(25, 25, 'Complejidad promedio de una tabla hash?',                     'MULTIPLE_CHOICE'),
(26, 26, 'Que es un grafo dirigido?',                                   'MULTIPLE_CHOICE'),
(27, 27, 'Espacio de una lista de adyacencia?',                         'MULTIPLE_CHOICE'),
(28, 28, 'Que estructura usa BFS?',                                     'MULTIPLE_CHOICE'),
(29, 29, 'Cuantos conjuntos tiene un grafo bipartito?',                 'MULTIPLE_CHOICE'),
(30, 30, 'Que recorre un ciclo Euleriano?',                             'MULTIPLE_CHOICE'),
(31, 31, 'Dijkstra funciona con pesos?',                                'MULTIPLE_CHOICE'),
(32, 32, 'Para que sirven Prim y Kruskal?',                             'MULTIPLE_CHOICE'),
(33, 33, 'Complejidad de Floyd-Warshall?',                              'MULTIPLE_CHOICE'),
(34, 34, 'Que algoritmo usa Google Maps?',                              'MULTIPLE_CHOICE');

INSERT INTO answers (id, question_id, answer_text, is_correct) VALUES
(1,  1,  'Complejidad',                               true),
(2,  1,  'Lenguaje',                                  false),
(3,  2,  'O(n)',                                      true),
(4,  2,  'O(n2)',                                     false),
(5,  3,  'El peor caso',                              true),
(6,  3,  'El mejor caso',                             false),
(7,  4,  'La pila de llamadas',                       true),
(8,  4,  'Solo las variables locales',                false),
(9,  5,  'O(n2)',                                     true),
(10, 5,  'O(2n)',                                     false),
(11, 6,  'Elementos ordenados por posicion',          true),
(12, 6,  'Elementos sin orden definido',              false),
(13, 7,  'O(1)',                                      true),
(14, 7,  'O(n)',                                      false),
(15, 8,  'Dato y puntero al siguiente nodo',          true),
(16, 8,  'Solo el dato',                              false),
(17, 9,  'LIFO: ultimo en entrar primero en salir',   true),
(18, 9,  'FIFO: primero en entrar primero en salir',  false),
(19, 10, 'FIFO: primero en entrar primero en salir',  true),
(20, 10, 'LIFO: ultimo en entrar primero en salir',   false),
(21, 11, 'Un elemento pertenece a multiples listas',  true),
(22, 11, 'Lista con multiples tipos de datos',        false),
(23, 12, 'Reutilizar codigo para distintos tipos',    true),
(24, 12, 'Hacer el codigo mas seguro pero lento',     false),
(25, 13, 'Lista enlazada',                            true),
(26, 13, 'Arreglo',                                   false),
(27, 14, 'Exactamente uno',                           true),
(28, 14, 'Puede tener varios',                        false),
(29, 15, 'Dos',                                       true),
(30, 15, 'Cuatro',                                    false),
(31, 16, 'En el subarbol izquierdo',                  true),
(32, 16, 'En el subarbol derecho',                    false),
(33, 17, 'Diferencia de altura entre subarboles 1',   true),
(34, 17, 'Todos los nodos del mismo color',           false),
(35, 18, 'Reglas de coloracion rojo y negro',         true),
(36, 18, 'Rotaciones solo hacia la derecha',          false),
(37, 19, 'El valor maximo del conjunto',              true),
(38, 19, 'El valor minimo del conjunto',              false),
(39, 20, 'Clasificacion y prediccion',                true),
(40, 20, 'Almacenamiento de grafos',                  false),
(41, 21, 'Cuatro',                                    true),
(42, 21, 'Ocho',                                      false),
(43, 22, 'Ocho',                                      true),
(44, 22, 'Cuatro',                                    false),
(45, 23, 'Busqueda de vecinos mas cercanos',          true),
(46, 23, 'Compresion de imagenes 2D',                 false),
(47, 24, 'Arbol binario de frecuencias',              true),
(48, 24, 'Arbol AVL',                                 false),
(49, 25, 'O(1)',                                      true),
(50, 25, 'O(log n)',                                  false),
(51, 26, 'Las aristas tienen direccion',              true),
(52, 26, 'Todos los nodos estan conectados',          false),
(53, 27, 'O(V+E)',                                    true),
(54, 27, 'O(V2)',                                     false),
(55, 28, 'Una cola (queue)',                          true),
(56, 28, 'Una pila (stack)',                          false),
(57, 29, 'Dos',                                       true),
(58, 29, 'Tres',                                      false),
(59, 30, 'Todas las aristas una vez',                 true),
(60, 30, 'Todos los vertices una vez',                false),
(61, 31, 'Pesos no negativos',                        true),
(62, 31, 'Solo grafos no dirigidos',                  false),
(63, 32, 'Construir el arbol de expansion minima',    true),
(64, 32, 'Encontrar el camino mas corto',             false),
(65, 33, 'O(V3)',                                     true),
(66, 33, 'O(V2)',                                     false),
(67, 34, 'Dijkstra',                                  true),
(68, 34, 'Floyd-Warshall',                            false);

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
-- ARTICLE CONTENT (all 34 lessons — content_ids 2,5,8,...,101)
-- =========================
INSERT INTO article_contents (id, content_id, body) VALUES
(6,  2,   'La complejidad algorítmica mide la eficiencia de un algoritmo en función del tamaño de la entrada. Se expresa con notación asintótica: O(1) constante, O(log n) logarítmica, O(n) lineal, O(n²) cuadrática. Analizar la complejidad permite elegir el algoritmo más adecuado para cada problema.'),
(7,  5,   'La notación Big-O describe el comportamiento de un algoritmo en el peor caso. O(1) indica tiempo constante (acceso a un arreglo por índice). O(log n) aparece en búsqueda binaria. O(n) en recorridos lineales. O(n log n) en algoritmos de ordenamiento eficientes como Merge Sort. O(n²) en algoritmos con dos lazos anidados.'),
(8,  8,   'Todo algoritmo tiene tres casos de análisis: mejor caso (entrada óptima), caso promedio (entrada típica) y peor caso (entrada más desfavorable). La notación Big-O se usa para el peor caso, Big-Omega para el mejor y Big-Theta cuando ambos coinciden. En la práctica se optimiza el peor caso.'),
(9,  11,  'La complejidad espacial mide la memoria total que consume un algoritmo: espacio de entrada, espacio auxiliar y pila de llamadas. Un algoritmo recursivo de profundidad n usa O(n) en la pila aunque no use variables extra. Es posible optimizar espacio reutilizando variables o convirtiendo recursión en iteración.'),
(10, 14,  'Para analizar complejidad: identifica los lazos y determina cuántas veces se ejecuta el cuerpo. Un lazo simple sobre n elementos es O(n). Dos lazos anidados sobre n son O(n²). Si el índice se divide entre 2 en cada iteración el lazo es O(log n). La recursión se analiza con el Teorema Maestro.'),
(11, 17,  'Una secuencia es una colección ordenada de elementos donde el orden de inserción importa. Las operaciones básicas son: acceso por índice O(1) en arreglos, inserción al final O(1) amortizado, inserción al inicio O(n). Las secuencias son la base de estructuras más complejas como pilas, colas y listas.'),
(12, 20,  'Un vector (arreglo dinámico) almacena elementos contiguos en memoria. Permite acceso aleatorio en O(1). Cuando se llena, se redimensiona copiando los elementos a un nuevo bloque del doble de tamaño, dando O(1) amortizado para inserción al final. Inserción o eliminación en el medio cuesta O(n) por el desplazamiento.'),
(13, 23,  'Una lista enlazada simple tiene nodos con un dato y un puntero al siguiente. Inserción o eliminación al inicio es O(1). Búsqueda es O(n). La lista doblemente enlazada añade un puntero al nodo anterior, facilitando recorrido bidireccional. No requiere memoria contigua, pero tiene overhead por los punteros.'),
(14, 26,  'Una pila (stack) sigue el principio LIFO: el último elemento insertado es el primero en salir. Operaciones push y pop en O(1). Se usa para: evaluación de expresiones, retroceso en algoritmos (backtracking), gestión del call stack, y verificación de paréntesis balanceados. Se implementa con arreglos o listas enlazadas.'),
(15, 29,  'Una cola (queue) sigue el principio FIFO: el primer elemento insertado es el primero en salir. Operaciones enqueue y dequeue en O(1). Variantes: cola de prioridad (min-heap o max-heap), deque (doble extremo), cola circular. Se usa en planificación de procesos, BFS y sistemas de mensajería.'),
(16, 32,  'Las multilistas permiten que un mismo nodo pertenezca a varias listas simultáneamente mediante múltiples punteros. Son útiles para representar relaciones muchos-a-muchos sin duplicar datos. Ejemplo: una base de datos de estudiantes y cursos donde cada estudiante puede estar en varios cursos y cada curso tiene varios estudiantes.'),
(17, 35,  'Los genéricos (templates en C++, generics en Java) permiten escribir estructuras de datos y algoritmos que funcionan con cualquier tipo. Se define el tipo como parámetro: List<T>, Stack<T>. Ventajas: reutilización de código, detección de errores en compilación y eliminación de castings explícitos. Fundamental para colecciones tipadas.'),
(18, 38,  'Las estructuras lineales organizan los datos en secuencia: arreglos, listas enlazadas, pilas y colas. Se diferencian por sus restricciones de acceso: arreglos permiten acceso aleatorio, pilas solo por el tope, colas por los extremos. La elección depende del patrón de acceso: aleatorio, LIFO o FIFO.'),
(19, 41,  'Un árbol es una estructura jerárquica con un nodo raíz del que descienden subárboles. Terminología: nodo raíz (sin padre), hojas (sin hijos), altura (longitud del camino más largo a una hoja), profundidad (distancia a la raíz). Los árboles modelan jerarquías: sistemas de archivos, DOM HTML, expresiones algebraicas.'),
(20, 44,  'Un árbol binario tiene como máximo dos hijos por nodo: izquierdo y derecho. Recorridos: preorden (raíz-izq-der), inorden (izq-raíz-der), postorden (izq-der-raíz), por niveles (BFS). El inorden de un BST produce los elementos ordenados. Un árbol binario completo tiene todos los niveles llenos excepto posiblemente el último.'),
(21, 47,  'Un árbol de búsqueda binaria (BST) mantiene la invariante: nodo izquierdo menor que la raíz, nodo derecho mayor. Búsqueda, inserción y eliminación son O(h) donde h es la altura. En el peor caso (árbol degenerado) h = n. Por eso existen variantes auto-balanceadas como AVL y Red-Black Trees.'),
(22, 50,  'Un árbol AVL mantiene la altura balanceada: para cada nodo la diferencia de alturas de sus subárboles es como máximo 1. Tras insertar o eliminar se verifican los factores de balance y se aplican rotaciones (simple o doble) para restaurar el equilibrio. Garantiza O(log n) para búsqueda, inserción y eliminación.'),
(23, 53,  'Los árboles Rojo-Negro son BST balanceados con propiedades adicionales de coloración: cada nodo es rojo o negro, la raíz es negra, no hay dos nodos rojos consecutivos y todos los caminos a hojas nulas tienen el mismo número de nodos negros. Garantizan altura O(log n) con rotaciones más simples que AVL.'),
(24, 56,  'Un montículo (heap) es un árbol binario completo que satisface la propiedad de montículo: en un max-heap cada nodo es mayor o igual que sus hijos. Permite extraer el máximo en O(log n). Se implementa eficientemente con un arreglo. Es la base de HeapSort y de las colas de prioridad.'),
(25, 59,  'Los árboles de decisión son modelos de aprendizaje automático que dividen el espacio de características en regiones mediante preguntas binarias. Cada nodo interno contiene un criterio de división, cada hoja una predicción. Se construyen con criterios como Gini o entropía. Son interpretables pero propensos al sobreajuste.'),
(26, 62,  'Un Quadtree divide recursivamente un espacio 2D en cuatro cuadrantes cuando el número de puntos supera un umbral. Se usa en: compresión de imágenes, detección de colisiones en videojuegos, indexación geoespacial. La búsqueda de vecinos cercanos es más eficiente que una búsqueda lineal en datos espaciales dispersos.'),
(27, 65,  'Un Octree extiende el Quadtree a tres dimensiones dividiendo el espacio en ocho octantes. Fundamental en gráficos 3D para: frustum culling (descartar objetos fuera de la cámara), ray tracing, detección de colisiones en simulaciones 3D. Reduce la complejidad de operaciones espaciales de O(n) a O(log n).'),
(28, 68,  'Un árbol Kd (k-dimensional) particiona el espacio k-dimensional alternando la dimensión de corte en cada nivel. Eficiente para búsqueda del vecino más cercano y búsquedas por rango en espacios multidimensionales. Se usa en visión por computador, aprendizaje automático (KNN) y motores de bases de datos espaciales.'),
(29, 71,  'Los árboles tienen múltiples aplicaciones: el árbol de Huffman comprime datos asignando códigos cortos a símbolos frecuentes. Los árboles de expresión representan fórmulas matemáticas. Los tries almacenan cadenas para búsqueda rápida de prefijos. Los árboles de segmentos permiten consultas de rango en O(log n).'),
(30, 74,  'Una tabla hash mapea claves a valores mediante una función hash. Operaciones de búsqueda, inserción y eliminación en O(1) promedio. Colisiones se manejan con encadenamiento (listas en cada bucket) o direccionamiento abierto (sondeo lineal, cuadrático o doble hash). La carga (n/m) determina el rendimiento.'),
(31, 77,  'Un grafo G = (V, E) consiste en vértices V y aristas E. Puede ser dirigido (aristas con dirección) o no dirigido. Ponderado (aristas con peso) o no ponderado. Conceptos: grado de un vértice, camino, ciclo, componente conexa, grafo acíclico dirigido (DAG). Los grafos modelan redes, dependencias y mapas.'),
(32, 80,  'Representaciones de grafos: matriz de adyacencia O(V²) en espacio, O(1) para verificar arista; lista de adyacencia O(V+E) en espacio, O(grado) para verificar arista. La lista es preferible para grafos dispersos. La matriz para grafos densos. La representación afecta la complejidad de los algoritmos.'),
(33, 83,  'DFS (búsqueda en profundidad) explora lo más lejos posible antes de retroceder, usando una pila. Complejidad O(V+E). Usos: detectar ciclos, ordenamiento topológico, componentes fuertemente conexas. BFS (búsqueda en anchura) explora nivel a nivel, usando una cola. Usos: camino más corto en grafos no ponderados, niveles de separación.'),
(34, 86,  'Un grafo bipartito tiene vértices divididos en dos conjuntos A y B donde todas las aristas van de A a B. Se puede verificar con BFS o DFS en O(V+E). Aplicaciones: emparejamiento (matching), asignación de tareas a trabajadores, recomendaciones usuario-ítem. Todo árbol es bipartito.'),
(35, 89,  'Un camino Euleriano recorre todas las aristas exactamente una vez. Existe si exactamente 0 o 2 vértices tienen grado impar. Un ciclo Hamiltoniano visita todos los vértices exactamente una vez (NP-completo). El problema del vendedor viajero (TSP) busca el ciclo hamiltoniano de menor peso. Tiene aplicaciones en logística y diseño de circuitos.'),
(36, 92,  'El algoritmo de Dijkstra encuentra el camino más corto desde un nodo fuente a todos los demás en grafos con pesos no negativos. Complejidad O((V+E) log V) con heap de prioridad. Usa relajación de aristas: si dist[u] + w(u,v) < dist[v] actualiza dist[v]. No funciona con pesos negativos (usar Bellman-Ford).'),
(37, 95,  'Prim construye un árbol de expansión mínima (MST) añadiendo en cada paso la arista de menor peso que conecta el árbol actual con un vértice no incluido. Kruskal ordena las aristas por peso y las añade si no forman ciclo (usando Union-Find). Ambos son O(E log E). El MST minimiza el costo total de conexión.'),
(38, 98,  'Floyd-Warshall calcula los caminos más cortos entre todos los pares de vértices. Complejidad O(V³). Permite pesos negativos (pero no ciclos negativos). Usa programación dinámica: dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j]) para cada vértice intermedio k. Útil cuando se necesitan todos los caminos mínimos.'),
(39, 101, 'Los grafos tienen aplicaciones en: Google Maps (Dijkstra/A* para rutas), redes sociales (BFS para grados de separación), compiladores (DAGs para dependencias), redes de flujo (Ford-Fulkerson para flujo máximo), PageRank (grafos dirigidos ponderados). Dominar grafos es esencial para resolver problemas de optimización en sistemas reales.');

-- =========================
-- RESET SEQUENCES
-- =========================
SELECT setval(pg_get_serial_sequence('courses',        'id'), (SELECT MAX(id) FROM courses));
SELECT setval(pg_get_serial_sequence('modules',        'id'), (SELECT MAX(id) FROM modules));
SELECT setval(pg_get_serial_sequence('lessons',        'id'), (SELECT MAX(id) FROM lessons));
SELECT setval(pg_get_serial_sequence('contents',       'id'), (SELECT MAX(id) FROM contents));
SELECT setval(pg_get_serial_sequence('video_contents',   'id'), (SELECT MAX(id) FROM video_contents));
SELECT setval(pg_get_serial_sequence('article_contents', 'id'), (SELECT MAX(id) FROM article_contents));
SELECT setval(pg_get_serial_sequence('quizzes',        'id'), (SELECT MAX(id) FROM quizzes));
SELECT setval(pg_get_serial_sequence('questions',      'id'), (SELECT MAX(id) FROM questions));
SELECT setval(pg_get_serial_sequence('answers',        'id'), (SELECT MAX(id) FROM answers));
