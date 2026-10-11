package com.amazonas;

import java.util.*;

/** TEMPORAL: mide nodos expandidos (BFS vs A* con desempate por orden) por mapa. */
public class TempTestMapa {
    static class N { Posicion p; int g, h, f, orden; N(Posicion p,int g,int h,int o){this.p=p;this.g=g;this.h=h;this.f=g+h;this.orden=o;} }

    public static void main(String[] args) {
        probar("Río serpenteante", MapaAmazonas.crearEjemplo());
        probar("Delta con afluentes", MapaAmazonas.crearConAfluentes());
        probar("Río simple 5x5", MapaAmazonas.crearSimple());
    }

    static void probar(String nombre, MapaAmazonas m) {
        int[] bfs = ejecutar(m, false);
        int[] ast = ejecutar(m, true);
        System.out.printf("%-22s  BFS: %2d nodos / %2d pasos   A*: %2d nodos / %2d pasos%n",
            nombre, bfs[0], bfs[1], ast[0], ast[1]);
    }

    /** @return {nodosExpandidos, pasos} o {-1,-1} si no hay camino */
    static int[] ejecutar(MapaAmazonas mapa, boolean aStar) {
        Queue<N> frontera = aStar
            ? new PriorityQueue<>(Comparator.comparingInt((N n) -> n.f).thenComparingInt(n -> n.orden))
            : new PriorityQueue<>(Comparator.comparingInt(n -> n.orden));
        Set<Posicion> visitados = new HashSet<>();
        Map<Posicion, Posicion> padre = new HashMap<>();
        Map<Posicion, Integer> costoG = new HashMap<>();
        Posicion inicio = mapa.getInicio();
        visitados.add(inicio); costoG.put(inicio, 0);
        frontera.add(new N(inicio, 0, h(inicio, mapa), 0));
        int orden = 1, nodos = 0;

        while (!frontera.isEmpty()) {
            N actual = frontera.poll();
            nodos++;
            if (mapa.esMar(actual.p)) {
                int pasos = 0; Posicion c = actual.p;
                while (c != null) { pasos++; c = padre.get(c); }
                return new int[]{nodos, pasos - 1};
            }
            for (Posicion v : actual.p.getVecinos()) {
                if (!mapa.esRio(v)) continue;
                int ng = actual.g + 1;
                boolean nuevo = !visitados.contains(v);
                if (aStar) {
                    if (nuevo || ng < costoG.getOrDefault(v, Integer.MAX_VALUE/2)) {
                        visitados.add(v); costoG.put(v, ng); padre.put(v, actual.p);
                        frontera.add(new N(v, ng, h(v, mapa), orden++));
                    }
                } else if (nuevo) {
                    visitados.add(v); costoG.put(v, ng); padre.put(v, actual.p);
                    frontera.add(new N(v, ng, h(v, mapa), orden++));
                }
            }
        }
        return new int[]{-1, -1};
    }

    static int h(Posicion a, MapaAmazonas m) {
        return Math.abs(a.getX()-m.getMar().getX()) + Math.abs(a.getY()-m.getMar().getY());
    }
}
