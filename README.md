# 🌊 Hombre Perdido en el Amazonas

**Universidad Mayor de San Simón (UMSS)**
Facultad de Ciencias y Tecnología — Carrera de Ingeniería de Sistemas
Materia: **Inteligencia Artificial**
Docente: **[Ing. Nombre del docente]**
**Grupo 1** — Gestión 2026 · Cochabamba, Bolivia

**Integrantes del Grupo 1:**
- [Nombre completo 1]
- [Nombre completo 2]
- [Nombre completo 3]

> Trabajo práctico del Capítulo 3 de *Inteligencia Artificial: Un Enfoque Moderno*
> (Russell & Norvig). Resolvemos el problema con **BFS** y lo comparamos con **A\***.

---

## 📌 Introducción

El enunciado del ejercicio dice:

> *"Se encuentra un hombre perdido en el Amazonas, a lo lejos escucha el sonido de un río y desea llegar al mar."*

La idea del problema es sencilla: el hombre no sabe dónde está el mar, pero escucha el río y
lo sigue hasta la desembocadura. Nosotros lo representamos como una **grilla (matriz)** donde
las celdas de río son las que se pueden recorrer, y el mar es el objetivo a alcanzar.

Para resolverlo usamos **búsqueda no informada (BFS)**, que es el tema que nos tocó ver, y de
paso lo comparamos con **A\*** (búsqueda informada) para mostrar la diferencia en la cantidad
de nodos que cada uno explora.

---

## 📖 Descripción del problema

**Modelado como un problema de búsqueda:**

- **Estado inicial**: posición del hombre en el río.
- **Acciones**: moverse a las celdas de río adyacentes (Norte, Sur, Este, Oeste).
- **Test objetivo**: llegar a la desembocadura del río (el mar).
- **Costo de paso**: 1 (uniforme) → BFS garantiza el camino más corto.

El hombre **solo puede caminar por el río**; las celdas de tierra son obstáculos que no se
pueden pisar. Así el problema queda como un grafo de celdas de río conectadas entre sí.

---

## 🧠 ¿Por qué usamos BFS?

| Criterio | BFS | Por qué |
|----------|-----|---------|
| **Completo** | ✅ Sí | Siempre encuentra solución si existe (el río es finito) |
| **Óptimo** | ✅ Sí | Como todos los pasos cuestan 1, el primer objetivo es el camino más corto |
| **Tiempo** | O(bᵈ) | b = ramificación (≤ 4), d = profundidad de la solución |
| **Espacio** | O(bᵈ) | Guardamos la cola (frontera) y los visitados |

**Otras estrategias del Cap. 3 que miramos:**
- **DFS**: usa poca memoria, pero **no es completo** ni **óptimo** (se puede ir por una rama sin fin).
- **Profundidad Iterativa**: buena si la memoria es un problema, pero más complicada de escribir.
- **Bidireccional**: necesitaría saber los predecesores desde el mar, es más lío.
- **Costo Uniforme**: aquí sería igual que BFS, porque todos los pasos cuestan 1.

> **Conclusión**: para este problema, BFS es lo **más simple y correcto**.

---

## 🏗️ Estructura del proyecto

```
hombre-perdido-amazonas/
├── src/main/java/com/amazonas/
│   ├── Main.java            # Programa de consola: ejecuta la búsqueda y muestra el resultado
│   ├── Posicion.java        # Coordenada (x,y) + sus vecinos
│   ├── MapaAmazonas.java     # La grilla del río y los mapas de ejemplo
│   ├── BuscadorBFS.java     # BFS con reconstrucción del camino
│   ├── BuscadorAStar.java   # A* con heurística Manhattan (para comparar)
│   └── VisualizadorBFS.java # Interfaz gráfica (Swing) que compara BFS vs A*
├── lib/                     # Librerías (FlatLaf e Ikonli)
├── ejecutar-gui.bat         # Doble clic: compila y abre la interfaz
└── ejecutar-consola.bat     # Doble clic: compila y corre la consola
```

---

## ▶️ Cómo ejecutar

**Requisitos:** Java 17 o superior (probado con Java 21). Las librerías ya vienen en `lib/`.

### Por consola

```bash
javac -cp "lib/*" -d out src/main/java/com/amazonas/*.java
java -cp "out;lib/*" com.amazonas.Main
```

> En Linux/macOS el separador del classpath es `:` →
> `java -cp "out:lib/*" com.amazonas.Main`

### Interfaz gráfica

```bash
javac -cp "lib/*" -d out src/main/java/com/amazonas/*.java
java -cp "out;lib/*" com.amazonas.VisualizadorBFS
```

También se puede abrir directamente con **`ejecutar-gui.bat`** o **`ejecutar-consola.bat`**
(en Windows).

---

## 🖥️ La interfaz gráfica

La ventana abre mostrando **solo BFS** (con todo el espacio para el mapa). Recién al pulsar
**«Comparar con A\*»** aparece el segundo mapa y corren los dos **al mismo tiempo** uno al lado
del otro. El botón cambia a «Solo BFS» para volver a la vista simple.

**Controles:**
- ▶ **Iniciar** · ⏸ **Pausar** · ⏭ **Paso** · ↻ **Reiniciar**
- 🆚 **Comparar con A\***
- 🗺️ **Selector de mapa** (Delta con afluentes, Río serpenteante, Río simple 5×5)
- 🎚️ **Velocidad** (10–1000 ms)
- 🌙/☀️ **Tema claro/oscuro**

El mapa se **escala solo** al tamaño de la ventana, así que **toda la información queda a la
vista y sin barras de desplazamiento**.

**¿Qué significa cada color?**

| Color | Elemento |
|-------|----------|
| 🟠 Naranja `#FB8C00` | **Inicio** (el hombre, **H**) |
| 🌊 Azul profundo `#0D47A1` | **Mar** (meta, **M**) |
| 🔵 Azul agua `#42A5F5` | **Río** (transitable) |
| 🟢 Verde selva `#2E7D32` | **Tierra** (no transitable) |
| 🟡→🟫 Ámbar→marrón | Celdas **visitadas**, con degradado según el **orden en que se exploraron** |
| 🔴 Rojo (con anillo) | **Frontera** actual |
| ⚪ Blanco | **Camino** solución |

Usamos **FlatLaf** para que la ventana se vea moderna (temas claro/oscuro) e **Ikonli**
(Material Design) para los iconos de los botones.

---

## 🗺️ Mapas de ejemplo

Leyenda: **H** = hombre · **M** = mar · **~** = río · **.** = tierra.

### 1. Delta con afluentes (`crearConAfluentes`) — el que más se nota la diferencia

Tiene tres afluentes que van **en contra** del objetivo. BFS los inunda todos porque no tiene
forma de saber que no llevan al mar; A\* los ignora porque la heurística le dice que va en la
dirección equivocada. Es el mapa que abre por defecto la interfaz.

```
. . . . . . . . . . . . . . .
. . . . . . . . . . . . . . .
. . . . . . . . . . . . . . .
. . . . ~ . . . . . . . . . .
. . . . ~ . . . . . . . . . .
. . . . ~ . . ~ . . . . . . .
. . . . ~ . . ~ . . ~ . . . .
. . . . ~ . . ~ . . ~ . . . .
. . . . ~ . . ~ . . ~ . . . .
. . . . ~ . . ~ . . ~ . . . .
. . . . ~ . . ~ . . ~ . . . .
. . . . ~ . . ~ . . ~ . . . .
. . H ~ ~ ~ ~ ~ . ~ . . . . .
. . . . . . . . ~ ~ ~ ~ ~ . .
. . . . . . . . . . . . ~ ~ M
```

### 2. Río serpenteante (`crearEjemplo`) — mapa por defecto de la consola

```
. . . . . . . . . . . . . . .
H ~ ~ ~ ~ ~ . . . . . . . . .
. . . . . ~ . . . . . . . . .
. . . . . ~ ~ ~ ~ ~ . . . . .
. . . . . . . . . ~ . . . . .
. . . . . . . . . ~ ~ ~ ~ ~ .
. . . . . . . . . . . . . ~ .
. . . . . . . . . . . . . ~ .
. . . . . . . . . . . . . ~ ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . M
```

### 3. Río simple 5×5 (`crearSimple`)

Un río recto, para pruebas rápidas.

---

## 📊 Resultados (BFS vs A\*)

Contamos cuántas celdas expande cada algoritmo para llegar al mismo camino óptimo:

| Mapa | BFS (nodos / pasos) | A\* (nodos / pasos) |
|------|---------------------|---------------------|
| Delta con afluentes | 36 / 14 | **15 / 14** |
| Río serpenteante | 27 / 26 | 27 / 26 |
| Río simple 5×5 | 9 / 8 | 9 / 8 |

> En el **Delta con afluentes** se ve clarita la ventaja de A\*: expande **15 nodos contra 36
> de BFS** para el mismo camino (14 pasos). En los ríos de un solo carril (serpenteante y
> simple) los dos expanden casi lo mismo, porque no hay bifurcaciones donde A\* pueda ahorrar.

### Ejemplo de salida por consola

```
╔══════════════════════════════════════════════════════════╗
║  HOMBRE PERDIDO EN EL AMAZONAS - Búsqueda BFS            ║
║  Basado en Cap. 3: Russell & Norvig - IA Un Enfoque Moderno║
╚══════════════════════════════════════════════════════════╝

MAPA INICIAL:
. . . . . . . . . . . . . . .
H ~ ~ ~ ~ ~ . . . . . . . . .
. . . . . ~ . . . . . . . . .
. . . . . ~ ~ ~ ~ ~ . . . . .
. . . . . . . . . ~ . . . . .
. . . . . . . . . ~ ~ ~ ~ ~ .
. . . . . . . . . . . . . ~ .
. . . . . . . . . . . . . ~ .
. . . . . . . . . . . . . ~ ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . ~
. . . . . . . . . . . . . . M

=== Iniciando BFS ===
Inicio: (1,1)
Objetivo (Mar): (14,14)

¡Objetivo encontrado!
Nodos expandidos: 27

¡SOLUCIÓN ENCONTRADA!
Pasos totales: 26
Camino: [(1,1), (2,1), (3,1), (4,1), (5,1), (5,2), (5,3), (6,3), (7,3), (8,3), (9,3), (9,4), (9,5), (10,5), (11,5), (12,5), (13,5), (13,6), (13,7), (13,8), (14,8), (14,9), (14,10), (14,11), (14,12), (14,13), (14,14)]
```

---

## 📚 Base teórica (Cap. 3 - Russell & Norvig)

### Formulación del problema
- **Estado inicial**: `Posicion(1,1)`
- **Acciones**: `getVecinos()` → 4 direcciones
- **Modelo de transición**: `esRio(vecino)` → solo nos movemos por río
- **Test objetivo**: `esMar(posicion)`
- **Costo de camino**: 1 por paso

### Estrategias de búsqueda no informada

| Estrategia | Completa | Óptima | Tiempo | Espacio |
|------------|----------|--------|--------|---------|
| **BFS** | ✅ | ✅* | O(bᵈ⁺¹) | O(bᵈ⁺¹) |
| Costo Uniforme | ✅ | ✅ | O(bᶜ*) | O(bᶜ*) |
| DFS | ❌ | ❌ | O(bᵐ) | O(bm) |
| Profundidad Limitada | ❌** | ❌ | O(bˡ) | O(bl) |
| **Profundidad Iterativa** | ✅ | ✅* | O(bᵈ) | O(bd) |
| Bidireccional | ✅ | ✅* | O(bᵈ/²) | O(bᵈ/²) |

\* Con costos uniformes. \*\* Si el límite es menor que la profundidad de la solución.

### Evitar estados repetidos
- Guardamos una **lista cerrada** (`visitados`) para no volver a expandir una celda.
- Así convertimos la búsqueda de árbol en búsqueda de **grafo** (evita ciclos).
- En una grilla esto es clave, porque hay varios caminos que llegan a la misma celda.

### Heurística de A\* (Cap. 4)
- Usamos la **distancia Manhattan** al mar: `h = |x - x_mar| + |y - y_mar|`.
- Es **admisible** (nunca sobreestima), por eso A\* sigue encontrando el camino óptimo.

---

## ✅ Conclusiones

- Con BFS **siempre llegamos** al mar por el camino más corto (con costos iguales).
- A\* llega al **mismo camino**, pero **explora menos celdas** cuando el mapa tiene
  bifurcaciones (afluentes), porque se guía con la heurística.
- En ríos de un solo carril, BFS y A\* rinden parecido (no hay nada que "ahorrar").
- La visualización ayuda bastante a **entender** cómo busca cada algoritmo: BFS "inunda"
  el mapa y A\* "va derecho" hacia el objetivo.

---

## 🔧 Cosas que se podrían mejorar

1. Comparar también con **DFS** y **Profundidad Iterativa**.
2. Poner **corrientes** en el río con costos distintos de 1 (ahí ya no usaríamos BFS puro,
   sino Costo Uniforme/Dijkstra).
3. Un **mapa dinámico** donde el río o los obstáculos cambien.
4. **Guardar/cargar** el mapa y el camino en un archivo (JSON).

---

**Universidad Mayor de San Simón (UMSS)**
**Grupo 1** — Inteligencia Artificial · Gestión 2026
Cochabamba, Bolivia 🇧🇴
