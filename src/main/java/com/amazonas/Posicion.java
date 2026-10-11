package com.amazonas;

/**
 * Representa una posición (x, y) en la grilla del Amazonas.
 * Inmutable y comparable para uso en conjuntos y mapas.
 */
public final class Posicion {
    private final int x;
    private final int y;

    public Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    /** Vecinos ortogonales (arriba, abajo, izquierda, derecha) */
    public Posicion[] getVecinos() {
        return new Posicion[] {
            new Posicion(x, y - 1),  // Norte
            new Posicion(x, y + 1),  // Sur
            new Posicion(x - 1, y),  // Oeste
            new Posicion(x + 1, y)   // Este
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posicion)) return false;
        Posicion that = (Posicion) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}