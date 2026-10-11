package com.amazonas;

import java.util.List;

/**
 * Programa principal: Hombre Perdido en el Amazonas.
 * <p>
 * Problema: Un hombre está perdido en el Amazonas, escucha un río y quiere llegar al mar.
 * Solución: Modelar el río como un grafo/grilla y usar BFS para encontrar el camino más corto.
 * <p>
 * Basado en el Capítulo 3 de "Inteligencia Artificial: Un Enfoque Moderno" (Russell & Norvig)
 * - Búsqueda no informada (BFS)
 * - Espacio de estados: celdas del río
 * - Acciones: moverse a celdas adyacentes del río (N, S, E, O)
 * - Test objetivo: llegar a la desembocadura (mar)
 * - Costo: 1 por paso (uniforme) → BFS es óptimo
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  HOMBRE PERDIDO EN EL AMAZONAS - Búsqueda BFS            ║");
        System.out.println("║  Basado en Cap. 3: Russell & Norvig - IA Un Enfoque Moderno║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Usar mapa de ejemplo (río serpenteante)
        MapaAmazonas mapa = MapaAmazonas.crearEjemplo();
        // MapaAmazonas mapa = MapaAmazonas.crearSimple();  // Alternativa simple

        System.out.println("MAPA INICIAL:");
        mapa.imprimir();
        System.out.println();

        // Resolver con BFS
        BuscadorBFS buscador = new BuscadorBFS(mapa);
        List<Posicion> camino = buscador.buscar();

        if (!camino.isEmpty()) {
            System.out.println("\n¡SOLUCIÓN ENCONTRADA!");
            System.out.println("Pasos totales: " + (camino.size() - 1));
            System.out.println("Camino: " + camino);
            System.out.println();
            System.out.println("MAPA CON CAMINO (*):");
            mapa.imprimirConCamino(camino);
        } else {
            System.out.println("\nNo hay camino posible al mar.");
        }

        System.out.println();
        System.out.println("=== ANÁLISIS ===");
        System.out.println("Algoritmo: BFS (Búsqueda en Anchura)");
        System.out.println("Completo: Sí (siempre encuentra solución si existe)");
        System.out.println("Óptimo: Sí (camino más corto, costos uniformes)");
        System.out.println("Complejidad tiempo: O(V + E) ≈ O(celdas de río)");
        System.out.println("Complejidad espacio: O(V) ≈ O(celdas de río)");
    }
}