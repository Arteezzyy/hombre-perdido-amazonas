package com.amazonas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** TEMPORAL: renderiza la GUI (con selector de mapas) a PNG. */
public class RenderPreview2 {
    public static void main(String[] args) throws Exception {
        render();
        System.exit(0);
    }

    static void render() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                VisualizadorBFS.aplicarTema(false);
                VisualizadorBFS v = new VisualizadorBFS(MapaAmazonas.crearConAfluentes());

                // 1) Vista por defecto: solo BFS
                resolver(v, "panelBFS");
                actualizarBotones(v);
                guardar(v, "preview_bfs.png");
                System.out.println("Resumen BFS: " + resumen(v));

                // 2) Comparación BFS vs A* (equivalente al botón "Comparar con A*") en el Delta
                toggleComparacion(v);
                resolver(v, "panelBFS");
                resolver(v, "panelAStar");
                actualizarBotones(v);
                guardar(v, "preview_delta.png");
                System.out.println("Resumen delta: " + resumen(v));

                // 3) Cambiar a "Río serpenteante" vía el combo (índice 1)
                Field comboF = VisualizadorBFS.class.getDeclaredField("comboMapas");
                comboF.setAccessible(true);
                JComboBox<?> combo = (JComboBox<?>) comboF.get(v);
                combo.setSelectedIndex(1);
                resolver(v, "panelBFS");
                resolver(v, "panelAStar");
                actualizarBotones(v);
                guardar(v, "preview_serp.png");
                System.out.println("Resumen final: " + resumen(v));

                v.dispose();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    static String resumen(VisualizadorBFS v) throws Exception {
        Field f = VisualizadorBFS.class.getDeclaredField("lblResumen");
        f.setAccessible(true);
        return ((JLabel) f.get(v)).getText();
    }

    static void toggleComparacion(VisualizadorBFS v) throws Exception {
        Method m = VisualizadorBFS.class.getDeclaredMethod("alternarComparacion");
        m.setAccessible(true);
        m.invoke(v);
    }

    static void guardar(VisualizadorBFS v, String file) throws Exception {
        v.pack();
        Container c = v.getContentPane();
        int w = Math.max(c.getWidth(), 1240);
        int h = Math.max(c.getHeight(), 760);
        c.setSize(w, h);
        c.doLayout();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        c.paintAll(g);
        g.dispose();
        ImageIO.write(img, "png", new File(file));
        System.out.println("PNG: " + new File(file).getAbsolutePath());
    }

    static void resolver(VisualizadorBFS v, String campo) throws Exception {
        Field f = VisualizadorBFS.class.getDeclaredField(campo);
        f.setAccessible(true);
        Object panel = f.get(v);
        Method paso = panel.getClass().getDeclaredMethod("paso");
        paso.setAccessible(true);
        boolean sigue = true;
        int guard = 0;
        while (sigue && guard++ < 100000) {
            sigue = (boolean) paso.invoke(panel);
        }
    }

    static void actualizarBotones(VisualizadorBFS v) throws Exception {
        Method m = VisualizadorBFS.class.getDeclaredMethod("actualizarBotones");
        m.setAccessible(true);
        m.invoke(v);
    }
}
