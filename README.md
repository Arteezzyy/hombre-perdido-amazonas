# Hombre Perdido en el Amazonas 🌊

> **Ejercicio académico** - Resolución mediante **búsqueda no informada (BFS)**
> Basado en el Capítulo 3 de *Inteligencia Artificial: Un Enfoque Moderno* (Russell & Norvig)

---

## 📖 Descripción del Problema

> *"Se encuentra un hombre perdido en el Amazonas, a lo lejos escucha el sonido de un río y desea llegar al mar."*

**Modelado como problema de búsqueda:**
- **Estado inicial**: Posición del hombre en el río
- **Acciones**: Moverse a celdas adyacentes del río (Norte, Sur, Este, Oeste)
- **Test objetivo**: Llegar a la desembocadura del río (el mar)
- **Costo de paso**: 1 (uniforme) → BFS garantiza optimalidad

---

## 🧠 Algoritmo Elegido: BFS (Búsqueda en Anchura)

| Criterio | BFS | Por qué |
|----------|-----|---------|
| **Completo** | ✅ Sí | Siempre encuentra solución si existe (factor de ramificación finito) |
| **Óptimo** | ✅ Sí | Costos uniformes → primer objetivo = camino más corto |
| **Tiempo** | O(bᵈ) | b = ramificación (≤4), d = profundidad solución |
| **Espacio** | O(bᵈ) | Cola + visitados |

**Alternativas del Cap. 3 consideradas:**
- **DFS**: Poca memoria, pero **no completo** (puede irse por rama infinita) ni **óptimo**
- **Profundidad Iterativa**: Buena opción si memoria es crítica, pero más compleja
- **Bidireccional**: Requiere saber predecesores desde el mar; más compleja de implementar
- **Costo Uniforme**: Equivale a BFS aquí (costos = 1)

> **Conclusión**: BFS es la opción **más simple y adecuada** para este problema.

---

## 🏗️ Estructura del Proyecto

```
src/main/java/com/amazonas/
├── Main.java           # Punto de entrada, ejecuta y muestra resultados
├── Posicion.java       # Coordenada (x,y) inmutable + vecinos
├── MapaAmazonas.java   # Grilla del río + fábrica de mapas de ejemplo
└── BuscadorBFS.java    # Implementación BFS con reconstrucción de camino
```

---

## ▶️ Cómo Ejecutar

### Requisitos
- Java 17+ (compatible con Java 21)

### Compilar y ejecutar
```bash
# Desde la raíz del proyecto
javac -d out src/main/java/com/amazonas/*.java
java -cp out com.amazonas.Main
```

### O con Maven (si se añade pom.xml)
```bash
mvn compile exec:java -Dexec.mainClass=com.amazonas.Main
```

---

## 🗺️ Mapas Incluidos

### 1. `MapaAmazonas.crearEjemplo()` (por defecto)
Río serpenteante 15×15:
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
- **H** = Hombre (inicio)
- **M** = Mar (objetivo)
- **~** = Río (transitable)
- **.** = Tierra (bloqueado)

### 2. `MapaAmazonas.crearSimple()`
Río recto 5×5 para pruebas rápidas.

---

## 📊 Salida de Ejemplo

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
Nodos expandidos: 47

¡SOLUCIÓN ENCONTRADA!
Pasos totales: 26
Camino: [(1,1), (2,1), (3,1), (4,1), (5,1), (5,2), (5,3), (6,3), (7,3), (8,3), (9,3), (9,4), (9,5), (10,5), (11,5), (12,5), (13,5), (13,6), (13,7), (13,8), (14,8), (14,9), (14,10), (14,11), (14,12), (14,13), (14,14)]

MAPA CON CAMINO (*):
. . . . . . . . . . . . . . .
H * * * * * . . . . . . . . .
. . . . . * . . . . . . . . .
. . . . . * * * * * . . . . .
. . . . . . . . . * . . . . .
. . . . . . . . . * * * * * .
. . . . . . . . . . . . . * .
. . . . . . . . . . . . . * .
. . . . . . . . . . . . . * *
. . . . . . . . . . . . . . *
. . . . . . . . . . . . . . *
. . . . . . . . . . . . . . *
. . . . . . . . . . . . . . *
. . . . . . . . . . . . . . *
. . . . . . . . . . . . . . M
```

---

## 📚 Referencia Teórica (Cap. 3 - Russell & Norvig)

### 3.1 Agentes Resolventes-Problemas
- Formulación del objetivo → Formulación del problema → Búsqueda

### 3.3 Definición Formal del Problema
- **Estado inicial**: `Posicion(1,1)`
- **Acciones**: `getVecinos()` → 4 direcciones
- **Modelo de transición**: `esRio(vecino)` → solo río válido
- **Test objetivo**: `esMar(posicion)`
- **Costo de camino**: 1 por paso

### 3.4 Estrategias de Búsqueda No Informada
| Estrategia | Completa | Óptima | Tiempo | Espacio |
|------------|----------|--------|--------|---------|
| **BFS** | ✅ | ✅* | O(bᵈ⁺¹) | O(bᵈ⁺¹) |
| Costo Uniforme | ✅ | ✅ | O(bᶜ*) | O(bᶜ*) |
| DFS | ❌ | ❌ | O(bᵐ) | O(bm) |
| Prof. Limitada | ❌** | ❌ | O(bˡ) | O(bl) |
| **Prof. Iterativa** | ✅ | ✅* | O(bᵈ) | O(bd) |
| Bidireccional | ✅ | ✅* | O(bᵈ/²) | O(bᵈ/²) |

* Costos uniformes / ** Si límite < profundidad solución

### 3.5 Evitar Estados Repetidos
- **Lista cerrada** (`visitados` Set) → evita ciclos y re-expansión
- Convierte búsqueda en árbol → búsqueda en grafo
- Esencial en grillas (múltiples caminos a misma celda)

---

## 🔧 Extensiones Posibles

1. **Visualización GUI** (JavaFX/Swing) - animar expansión BFS
2. **Múltiples algoritmos** - comparar BFS vs DFS vs Iterativo vs A*
3. **Heurística (Cap. 4)** - A* con distancia Manhattan al mar
4. **Río con corrientes** - costos variables → Costo Uniforme
5. **Mapa dinámico** - río que cambia (búsqueda en tiempo real)
6. **Modo offline** - serializar mapa y camino (JSON)

---

## 📝 Licencia

Proyecto académico - Libre uso educativo.

---

## 👨‍💻 Autor

Ejercicio resuelto para materia de **Inteligencia Artificial / Búsqueda**
Universidad Mayor de San Simón - Equipo: [GRUPO#1]
