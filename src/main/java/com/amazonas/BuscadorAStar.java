package com.amazonas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Resuelve el problema "Hombre perdido en el Amazonas" usando A* (A-Estrella).
 * <p>
 * A* combina el coste real desde el inicio {@code g} con una heurística {@code h}:
 * <ul>
 *   <li>{@code g} = pasos dados desde el inicio (coste uniforme = 1).</li>
 *   <li>{@code h} = distancia <b>Manhattan</b> hasta el mar (admisible: nunca sobreestima).</li>
 * </ul>
 * Al guiarse hacia el objetivo, A* expande muchos menos nodos que BFS sin perder la optimalidad.
 */
public class BuscadorAStar {

    private final MapaAmazonas mapa;
    private int nodosExpandidos;

    public BuscadorAStar(MapaAmazonas mapa) {
        this.mapa = mapa;
    }

    /** Ejecuta A* desde la posición inicial hasta el mar. */
    public List<Posicion> buscar() {
        PriorityQueue<Nodo> frontera = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<Posicion> visitados = new HashSet<>();
        Map<Posicion, Posicion> padre = new HashMap<>();
        Map<Posicion, Integer> costoG = new HashMap<>();
        nodosExpandidos = 0;

        Posicion inicio = mapa.getInicio();
        frontera.add(new Nodo(inicio, 0, heuristica(inicio)));
        costoG.put(inicio, 0);
        visitados.add(inicio);

        while (!frontera.isEmpty()) {
            Nodo actual = frontera.poll();
            nodosExpandidos++;

            if (mapa.esMar(actual.posicion)) {
                return reconstruirCamino(padre, actual.posicion);
            }

            for (Posicion vecino : actual.posicion.getVecinos()) {
                if (!mapa.esRio(vecino)) continue;

                int nuevoG = actual.g + 1;
                boolean esNuevo = !visitados.contains(vecino);
                if (esNuevo || nuevoG < costoG.getOrDefault(vecino, Integer.MAX_VALUE / 2)) {
                    visitados.add(vecino);
                    costoG.put(vecino, nuevoG);
                    padre.put(vecino, actual.posicion);
                    frontera.add(new Nodo(vecino, nuevoG, heuristica(vecino)));
                }
            }
        }
        return Collections.emptyList();
    }

    /** Número de nodos expandidos en la última búsqueda. */
    public int getNodosExpandidos() {
        return nodosExpandidos;
    }

    /** Heurística admisible: distancia Manhattan desde {@code p} hasta el mar. */
    private int heuristica(Posicion p) {
        Posicion mar = mapa.getMar();
        return Math.abs(p.getX() - mar.getX()) + Math.abs(p.getY() - mar.getY());
    }

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

    /** Nodo interno para A*. */
    private static class Nodo {
        final Posicion posicion;
        final int g;
        final int h;
        final int f;

        Nodo(Posicion posicion, int g, int h) {
            this.posicion = posicion;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }
}
