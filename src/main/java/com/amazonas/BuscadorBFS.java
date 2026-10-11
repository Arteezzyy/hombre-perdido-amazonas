package com.amazonas;

import java.util.*;

/**
 * Resuelve el problema "Hombre perdido en el Amazonas" usando BFS (Búsqueda en Anchura).
 * <p>
 * ¿Por qué BFS?
 * - Garantiza encontrar el camino MÁS CORTO (óptimo para costos uniformes)
 * - Completa: siempre encuentra solución si existe
 * - Fácil de implementar con una Queue
 * - Adecuado para grillas de tamaño moderado (el río no es infinito)
 * <p>
 * Complejidad: O(V + E) donde V = celdas de río, E = conexiones entre celdas adyacentes
 * Espacio: O(V) para la cola y el conjunto de visitados
 */
public class BuscadorBFS {

    private final MapaAmazonas mapa;

    public BuscadorBFS(MapaAmazonas mapa) {
        this.mapa = mapa;
    }

    /**
     * Ejecuta BFS desde la posición inicial hasta el mar.
     * @return Lista de posiciones del camino (incluye inicio y mar), o lista vacía si no hay solución
     */
    public List<Posicion> buscar() {
        Queue<Nodo> frontera = new LinkedList<>();
        Set<Posicion> visitados = new HashSet<>();
        Map<Posicion, Posicion> padre = new HashMap<>();  // Para reconstruir camino

        Posicion inicio = mapa.getInicio();
        frontera.add(new Nodo(inicio, null));
        visitados.add(inicio);

        System.out.println("=== Iniciando BFS ===");
        System.out.println("Inicio: " + inicio);
        System.out.println("Objetivo (Mar): " + mapa.getMar());
        System.out.println();

        int nodosExpandidos = 0;

        while (!frontera.isEmpty()) {
            Nodo actual = frontera.poll();
            nodosExpandidos++;

            // Test de objetivo
            if (mapa.esMar(actual.posicion)) {
                System.out.println("¡Objetivo encontrado!");
                System.out.println("Nodos expandidos: " + nodosExpandidos);
                return reconstruirCamino(padre, actual.posicion);
            }

            // Expandir sucesores (vecinos por el río)
            for (Posicion vecino : actual.posicion.getVecinos()) {
                if (mapa.esRio(vecino) && !visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padre.put(vecino, actual.posicion);
                    frontera.add(new Nodo(vecino, actual.posicion));
                }
            }
        }

        System.out.println("No se encontró camino al mar.");
        System.out.println("Nodos expandidos: " + nodosExpandidos);
        return Collections.emptyList();
    }

    /** Reconstruye el camino desde el mapa de padres */
    private List<Posicion> reconstruirCamino(Map<Posicion, Posicion> padre, Posicion objetivo) {
        List<Posicion> camino = new ArrayList<>();
        Posicion actual = objetivo;
        while (actual != null) {
            camino.add(actual);
            actual = padre.get(actual);
        }
        Collections.reverse(camino);
        return camino;
    }

    /** Nodo interno para la búsqueda */
    private static class Nodo {
        final Posicion posicion;
        final Posicion padre;

        Nodo(Posicion posicion, Posicion padre) {
            this.posicion = posicion;
            this.padre = padre;
        }
    }
}