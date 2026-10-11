package com.amazonas;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa el mapa del Amazonas con el río.
 * El río fluye desde una posición inicial hacia el mar (objetivo).
 * <p>
 * Modelado como una grilla donde:
 * - 0 = tierra (no transitable)
 * - 1 = río (transitable)
 * - El hombre solo puede moverse por celdas de río
 * - El río fluye en una dirección general hacia el mar
 */
public class MapaAmazonas {
    private final int[][] grilla;
    private final int filas;
    private final int columnas;
    private final Posicion inicio;
    private final Posicion mar;  // Objetivo: desembocadura del río en el mar
    private final String nombre;

    public MapaAmazonas(int[][] grilla, Posicion inicio, Posicion mar) {
        this(grilla, inicio, mar, "Mapa");
    }

    public MapaAmazonas(int[][] grilla, Posicion inicio, Posicion mar, String nombre) {
        this.grilla = grilla;
        this.filas = grilla.length;
        this.columnas = grilla[0].length;
        this.inicio = inicio;
        this.mar = mar;
        this.nombre = nombre;
    }

    /** Crea un mapa de ejemplo: río serpenteante hacia el mar */
    public static MapaAmazonas crearEjemplo() {
        // 15x15 grilla: 1 = río, 0 = tierra
        int[][] grilla = {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,1,1,1,1,1,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1,1,1,1,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}   // Mar en (14,14)
        };
        return new MapaAmazonas(grilla, new Posicion(1, 1), new Posicion(14, 14), "Río serpenteante");
    }

    /** Crea un mapa simple: río recto hacia el mar */
    public static MapaAmazonas crearSimple() {
        int[][] grilla = {
            {1,0,0,0,0},
            {1,0,0,0,0},
            {1,1,1,1,1},
            {0,0,0,0,1},
            {0,0,0,0,1}
        };
        return new MapaAmazonas(grilla, new Posicion(0, 0), new Posicion(4, 4), "Río simple 5x5");
    }

    /**
     * Crea el mapa "Delta con afluentes": un río principal que baja al mar y tres afluentes
     * que suben (en contra del objetivo). Los afluentes son callejones sin salida que BFS
     * inunda y A* ignora: resalta la diferencia entre ambos. Es el mapa por defecto de la GUI.
     */
    public static MapaAmazonas crearConAfluentes() {
        int[][] g = new int[15][15];
        // Río principal hacia el mar
        for (int x = 2; x <= 7; x++) g[12][x] = 1;
        for (int x = 7; x <= 12; x++) g[13][x] = 1;
        for (int x = 12; x <= 14; x++) g[14][x] = 1;
        // Afluentes (ramas que van en contra del objetivo)
        for (int y = 3; y <= 12; y++) g[y][4] = 1;
        for (int y = 5; y <= 12; y++) g[y][7] = 1;
        for (int y = 6; y <= 13; y++) g[y][10] = 1;
        return new MapaAmazonas(g, new Posicion(2, 12), new Posicion(14, 14), "Delta con afluentes");
    }

    public boolean esRio(Posicion p) {
        return p.getX() >= 0 && p.getX() < columnas
            && p.getY() >= 0 && p.getY() < filas
            && grilla[p.getY()][p.getX()] == 1;
    }

    public boolean esMar(Posicion p) {
        return p.equals(mar);
    }

    public Posicion getInicio() { return inicio; }
    public Posicion getMar() { return mar; }
    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public String getNombre() { return nombre; }

    /** Imprime el mapa en consola */
    public void imprimir() {
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                Posicion p = new Posicion(x, y);
                if (p.equals(inicio)) {
                    System.out.print("H ");  // Hombre
                } else if (p.equals(mar)) {
                    System.out.print("M ");  // Mar
                } else if (grilla[y][x] == 1) {
                    System.out.print("~ ");  // Río
                } else {
                    System.out.print(". ");  // Tierra
                }
            }
            System.out.println();
        }
    }

    /** Imprime el mapa con el camino solución marcado */
    public void imprimirConCamino(java.util.List<Posicion> camino) {
        Set<Posicion> caminoSet = new HashSet<>(camino);
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                Posicion p = new Posicion(x, y);
                if (p.equals(inicio)) {
                    System.out.print("H ");
                } else if (p.equals(mar)) {
                    System.out.print("M ");
                } else if (caminoSet.contains(p)) {
                    System.out.print("* ");  // Camino
                } else if (grilla[y][x] == 1) {
                    System.out.print("~ ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
}