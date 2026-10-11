package com.amazonas;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Visualización gráfica comparativa BFS vs A* en el mapa del Amazonas.
 * <p>
 * Interfaz mejorada con:
 * - <b>FlatLaf</b>: look &amp; feel moderno (tema claro/oscuro conmutable).
 * - <b>Ikonli</b> (Material Design 2): iconos en botones y etiquetas.
 * <p>
 * Paleta natural del mapa (canvas siempre claro):
 * - Árboles (tierra): verde selva, Río: azul agua, Mar: azul profundo
 * - Visitados: ámbar claro, Frontera: rojo (con anillo), Camino: blanco.
 */
public class VisualizadorBFS extends JFrame {

    // ===== PALETA NATURAL DEL MAPA =====
    private static final Color COLOR_TIERRA       = new Color(0x2E7D32); // Verde selva (árboles)
    private static final Color COLOR_RIO          = new Color(0x42A5F5); // Azul agua (río)
    private static final Color COLOR_MAR          = new Color(0x0D47A1); // Azul profundo (mar)
    private static final Color COLOR_INICIO       = new Color(0xFB8C00); // Naranja fuerte (hombre)
    private static final Color COLOR_VISITADOS    = new Color(0xFFCC80); // Ámbar claro (visitados, color base de referencia)
    private static final Color COLOR_VISITADO_INI = new Color(0xFFE0B2); // Explorado primero (claro)
    private static final Color COLOR_VISITADO_FIN = new Color(0xBF360C); // Explorado después (oscuro)
    private static final Color COLOR_FRONTERA     = new Color(0xE53935); // Rojo (frontera)
    private static final Color COLOR_CAMINO       = new Color(0xFFFFFF); // Blanco (camino)
    private static final Color COLOR_LINEA_CAMINO = new Color(0x212121); // Línea oscura del camino
    private static final Color COLOR_FONDO_MAPA   = new Color(0xFFFFFF); // Fondo del canvas (mapa)
    private static final Color COLOR_BORDE_CELDA  = new Color(0x9E9E9E); // Gris borde de celda

    // ===== COLORES DE MARCA =====
    private static final Color ACENTO       = new Color(0x1E88E5); // Azul agua (acento global)
    private static final Color VERDE_TITULO = new Color(0x2E7D32);
    private static final Color AMBAR_ASTAR  = new Color(0xF9A825);

    private MapaAmazonas mapa;

    // Paneles para cada algoritmo
    private final PanelMapa panelBFS;
    private final PanelMapa panelAStar;

    // Controles
    private JButton btnIniciar;
    private JButton btnPausar;
    private JButton btnPaso;
    private JButton btnReiniciar;
    private JButton btnComparar;
    private JButton btnTema;
    private JComboBox<OpcionMapa> comboMapas;
    private JSlider sliderVelocidad;
    private JLabel lblVel;
    private JLabel lblResumen;
    private Timer timer;
    private boolean animacionActiva = false;
    private boolean comparando = false; // false = solo BFS; true = BFS + A*
    private int velocidadMs = 100;

    // Contenedores de layout
    private JPanel panelCentral;
    private JPanel wrapperBFS;
    private JPanel wrapperAStar;

    // Iconos que deben seguir el color de texto del tema actual
    private final List<FontIcon> iconosTema = new ArrayList<>();
    private FontIcon iconTema;

    public VisualizadorBFS(MapaAmazonas mapa) {
        // Garantiza FlatLaf aunque se instancie directamente (p. ej. desde el IDE)
        if (!(UIManager.getLookAndFeel() instanceof FlatLaf)) {
            aplicarTema(false);
        }

        this.mapa = mapa;
        this.panelBFS = new PanelMapa("BFS");
        this.panelAStar = new PanelMapa("A*");

        setTitle("Hombre Perdido en el Amazonas — BFS");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cabecera + barra de controles (apiladas arriba)
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(crearHeader());
        top.add(crearPanelControles());
        add(top, BorderLayout.NORTH);

        // Paneles de cada algoritmo (se muestran según el modo de comparación)
        wrapperBFS = crearPanelAlgoritmo(
            "BFS  ·  Óptimo, sin heurística",
            FontIcon.of(MaterialDesignS.SOURCE_BRANCH, 18, new Color(0x1565C0)),
            panelBFS);
        wrapperAStar = crearPanelAlgoritmo(
            "A*  ·  Heurística Manhattan",
            FontIcon.of(MaterialDesignL.LIGHTNING_BOLT, 18, AMBAR_ASTAR),
            panelAStar);

        panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(panelCentral, BorderLayout.CENTER);

        // Abajo: resumen
        add(crearPanelResumen(), BorderLayout.SOUTH);

        recolorearIconos();
        actualizarLayout();
        ajustarAlTamanoPantalla();
        setLocationRelativeTo(null);
        setVisible(true);

        reiniciarAnimacion();
    }

    // ==================== TEMA (FlatLaf) ====================

    /** Aplica el tema claro ({@code false}) u oscuro ({@code true}) a toda la aplicación. */
    static void aplicarTema(boolean oscuro) {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#1E88E5"));
        if (oscuro) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        // Ajustes globales de estilo de FlatLaf
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 14);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 1);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ToolTip.background", oscuro ? new Color(0x37474F) : new Color(0xECEFF1));
        FlatLaf.updateUI();
    }

    private void alternarTema() {
        boolean nuevoOscuro = !FlatLaf.isLafDark();
        aplicarTema(nuevoOscuro); // se pierde la referencia al icono anterior
        iconTema.setIkon(nuevoOscuro ? MaterialDesignW.WHITE_BALANCE_SUNNY : MaterialDesignW.WEATHER_NIGHT);
        btnTema.setIcon(iconTema);
        btnTema.setToolTipText(nuevoOscuro ? "Cambiar a tema claro" : "Cambiar a tema oscuro");
        recolorearIconos();
        repaint();
    }

    /** Recolorea todos los iconos registrados con el color de texto del tema actual. */
    private void recolorearIconos() {
        Color fg = UIManager.getColor("Label.foreground");
        if (fg == null) fg = new Color(0x212121);
        for (FontIcon fi : iconosTema) fi.setIconColor(fg);
        if (iconTema != null) iconTema.setIconColor(fg);
        // El color de texto del botón puede diferir ligeramente del de label
        Color btnFg = UIManager.getColor("Button.foreground");
        if (btnFg != null) {
            if (btnIniciar != null) recolorearBoton(btnIniciar, btnFg);
            if (btnPausar != null) recolorearBoton(btnPausar, btnFg);
            if (btnPaso != null) recolorearBoton(btnPaso, btnFg);
            if (btnReiniciar != null) recolorearBoton(btnReiniciar, btnFg);
            if (lblVel != null) recolorearBoton(lblVel, btnFg);
        }
    }

    private void recolorearBoton(JComponent c, Color color) {
        Icon ic = null;
        if (c instanceof AbstractButton) ic = ((AbstractButton) c).getIcon();
        else if (c instanceof JLabel) ic = ((JLabel) c).getIcon();
        if (ic instanceof FontIcon) {
            ((FontIcon) ic).setIconColor(color);
        }
    }

    // ==================== UI ====================

    /** Crea un FontIcon y lo registra para recolorearse con el tema. */
    private FontIcon icono(Ikon ikon, int tam) {
        FontIcon fi = FontIcon.of(ikon, tam);
        iconosTema.add(fi);
        return fi;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));

        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setOpaque(false);

        JLabel t1 = new JLabel("Hombre Perdido en el Amazonas",
            FontIcon.of(MaterialDesignF.FOREST, 24, VERDE_TITULO), SwingConstants.LEADING);
        t1.setFont(t1.getFont().deriveFont(Font.BOLD, 20f));
        t1.setIconTextGap(10);
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t2 = new JLabel("Búsqueda no informada (BFS)  vs  A* con heurística Manhattan");
        t2.setFont(t2.getFont().deriveFont(Font.PLAIN, 12.5f));
        t2.setForeground(UIManager.getColor("Label.disabledForeground"));
        t2.setAlignmentX(Component.LEFT_ALIGNMENT);

        titulos.add(t1);
        titulos.add(Box.createVerticalStrut(2));
        titulos.add(t2);
        header.add(titulos, BorderLayout.WEST);

        iconTema = FontIcon.of(MaterialDesignW.WEATHER_NIGHT, 18);
        btnTema = new JButton(iconTema);
        btnTema.setToolTipText("Cambiar a tema oscuro");
        btnTema.putClientProperty("JButton.buttonType", "toolBarButton");
        btnTema.setFocusable(false);
        btnTema.addActionListener(e -> alternarTema());

        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        east.setOpaque(false);

        // Selector de mapas
        OpcionMapa[] opciones = {
            new OpcionMapa("Delta con afluentes", MapaAmazonas::crearConAfluentes),
            new OpcionMapa("Río serpenteante", MapaAmazonas::crearEjemplo),
            new OpcionMapa("Río simple 5x5", MapaAmazonas::crearSimple)
        };
        comboMapas = new JComboBox<>(opciones);
        comboMapas.setToolTipText("Elige el mapa a explorar");
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].nombre().equals(mapa.getNombre())) {
                comboMapas.setSelectedIndex(i);
                break;
            }
        }
        // El listener se conecta DESPUÉS de fijar la selección inicial
        comboMapas.addActionListener(e -> {
            OpcionMapa op = (OpcionMapa) comboMapas.getSelectedItem();
            if (op != null) cambiarMapa(op.fabrica().get());
        });

        JLabel lblMapa = new JLabel("Mapa:", icono(MaterialDesignM.MAP, 16), SwingConstants.LEADING);
        lblMapa.setIconTextGap(6);
        lblMapa.setFont(lblMapa.getFont().deriveFont(Font.BOLD, 12.5f));

        east.add(lblMapa);
        east.add(comboMapas);
        east.add(btnTema);
        header.add(east, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 6, 12));

        btnIniciar = crearBoton("Iniciar", icono(MaterialDesignP.PLAY, 16), "Iniciar ejecución automática de ambos algoritmos");
        btnPausar = crearBoton("Pausar", icono(MaterialDesignP.PAUSE, 16), "Pausar la ejecución automática");
        btnPaso = crearBoton("Paso", icono(MaterialDesignS.SKIP_NEXT, 16), "Avanzar un paso en ambos algoritmos");
        btnReiniciar = crearBoton("Reiniciar", icono(MaterialDesignR.RESTART, 16), "Reiniciar la búsqueda");
        btnComparar = crearBoton("Comparar con A*", icono(MaterialDesignC.COMPARE, 16), "Mostrar u ocultar el panel de A* para comparar");

        btnIniciar.addActionListener(e -> iniciarAuto());
        btnPausar.addActionListener(e -> pausarAuto());
        btnPaso.addActionListener(e -> pasoManual());
        btnReiniciar.addActionListener(e -> reiniciarAnimacion());
        btnComparar.addActionListener(e -> alternarComparacion());

        // Botón principal resaltado con el color de acento
        getRootPane().setDefaultButton(btnIniciar);

        sliderVelocidad = new JSlider(10, 1000, 100);
        sliderVelocidad.setMajorTickSpacing(200);
        sliderVelocidad.setMinorTickSpacing(50);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPaintLabels(true);
        sliderVelocidad.setPreferredSize(new Dimension(230, 45));
        sliderVelocidad.addChangeListener(e -> {
            velocidadMs = sliderVelocidad.getValue();
            lblVel.setText("Velocidad: " + velocidadMs + " ms");
            if (timer != null) timer.setDelay(velocidadMs);
        });

        lblVel = new JLabel("Velocidad: 100 ms", icono(MaterialDesignS.SPEEDOMETER, 16), SwingConstants.LEADING);
        lblVel.setIconTextGap(6);
        lblVel.setFont(lblVel.getFont().deriveFont(Font.BOLD, 12.5f));

        panel.add(btnIniciar);
        panel.add(btnPausar);
        panel.add(btnPaso);
        panel.add(btnReiniciar);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnComparar);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblVel);
        panel.add(sliderVelocidad);

        return panel;
    }

    private JButton crearBoton(String texto, Icon icono, String tooltip) {
        JButton btn = new JButton(texto, icono);
        btn.setToolTipText(tooltip);
        btn.setFocusable(false);
        btn.setIconTextGap(8);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
        return btn;
    }

    private JPanel crearPanelAlgoritmo(String titulo, Icon icono, PanelMapa panelMapa) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x607D8B), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitulo = new JLabel(titulo, icono, SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 15.5f));
        lblTitulo.setIconTextGap(10);
        wrapper.add(lblTitulo, BorderLayout.NORTH);

        panelMapa.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_CELDA, 1));
        wrapper.add(panelMapa, BorderLayout.CENTER);

        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 5));
        for (JLabel lbl : new JLabel[] { panelMapa.lblEstado, panelMapa.lblPasos, panelMapa.lblNodosExpandidos }) {
            lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12.5f));
            panelInfo.add(lbl);
        }
        wrapper.add(panelInfo, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 15, 6, 15));
        lblResumen = new JLabel(
            "Ejecuta la búsqueda para comparar cuántos nodos expande cada algoritmo.",
            icono(MaterialDesignE.EQUALIZER, 16), SwingConstants.LEADING);
        lblResumen.setFont(lblResumen.getFont().deriveFont(Font.PLAIN, 12.5f));
        lblResumen.setIconTextGap(8);
        panel.add(lblResumen);
        return panel;
    }

    /** Actualiza el resumen inferior con los nodos expandidos por cada algoritmo. */
    private void actualizarResumen() {
        if (lblResumen == null) return;
        if (!comparando) {
            lblResumen.setText(panelBFS.terminado
                ? "BFS: " + panelBFS.nodosExpandidos + " nodos expandidos  ·  pulsa «Comparar con A*» para ver el contraste"
                : "Ejecuta la búsqueda para ver cuántos nodos expande BFS.");
            return;
        }
        if (panelBFS.terminado && panelAStar.terminado) {
            int nb = panelBFS.nodosExpandidos;
            int na = panelAStar.nodosExpandidos;
            String extra;
            if (nb > 0 && na < nb) {
                int pct = (int) Math.round(100.0 * (nb - na) / nb);
                extra = "   →   A* expandió " + (nb - na) + " nodos menos (" + pct + "%)";
            } else if (na == nb) {
                extra = "   →   Ambos expandieron los mismos nodos";
            } else {
                extra = "";
            }
            lblResumen.setText("BFS: " + nb + " nodos  ·  A*: " + na + " nodos" + extra);
        } else {
            lblResumen.setText("Ejecuta la búsqueda para comparar cuántos nodos expande cada algoritmo.");
        }
    }

    // ==================== CONTROL DE LA ANIMACIÓN ====================

    private boolean ambosTerminados() {
        return comparando ? (panelBFS.terminado && panelAStar.terminado) : panelBFS.terminado;
    }

    private void actualizarBotones() {
        boolean fin = ambosTerminados();
        btnIniciar.setEnabled(!animacionActiva && !fin);
        btnPausar.setEnabled(animacionActiva);
        btnPaso.setEnabled(!animacionActiva && !fin);
        btnReiniciar.setEnabled(true);
        actualizarResumen();
    }

    /** Cambia el mapa en ambos paneles y reinicia la búsqueda. */
    private void cambiarMapa(MapaAmazonas nuevo) {
        if (nuevo == null) return;
        if (timer != null) timer.stop();
        animacionActiva = false;
        this.mapa = nuevo;
        panelBFS.actualizarMapa();
        panelAStar.actualizarMapa();
        actualizarBotones();
        revalidate();
        repaint();
    }

    /** Muestra u oculta el panel de A*. A* solo se ejecuta en modo comparación. */
    private void alternarComparacion() {
        comparando = !comparando;
        if (timer != null) timer.stop();
        animacionActiva = false;
        if (comparando) {
            // Comparación limpia: ambos arrancan en blanco y la animación se ejecuta
            panelBFS.reiniciar();
            panelAStar.reiniciar();
            actualizarLayout();
            actualizarBotones();
            iniciarAuto();
        } else {
            // Vuelve a solo BFS: A* deja de ejecutarse
            actualizarLayout();
            actualizarBotones();
        }
    }

    /** Reorganiza el área central: solo BFS, o BFS + A* lado a lado. */
    private void actualizarLayout() {
        panelCentral.removeAll();
        if (comparando) {
            JPanel fila = new JPanel(new GridLayout(1, 2, 12, 0));
            fila.setOpaque(false);
            fila.add(wrapperBFS);
            fila.add(wrapperAStar);
            panelCentral.add(fila, BorderLayout.CENTER);
        } else {
            panelCentral.add(wrapperBFS, BorderLayout.CENTER);
        }
        setTitle("Hombre Perdido en el Amazonas — BFS" + (comparando ? " vs A*" : ""));
        if (btnComparar != null) {
            btnComparar.setText(comparando ? "Solo BFS" : "Comparar con A*");
            btnComparar.setToolTipText(comparando
                ? "Ocultar el panel de A*" : "Mostrar el panel de A* para comparar");
            Color btnFg = UIManager.getColor("Button.foreground");
            if (btnFg != null) recolorearBoton(btnComparar, btnFg);
        }
        panelCentral.revalidate();
        panelCentral.repaint();
        repaint();
    }

    /** Ajusta la ventana a la pantalla para que todo se vea sin scroll. */
    private void ajustarAlTamanoPantalla() {
        Rectangle b = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int w = Math.min(1320, Math.max(820, b.width - 40));
        int h = Math.min(920, Math.max(560, b.height - 40));
        setMinimumSize(new Dimension(Math.min(820, w), Math.min(560, h)));
        setSize(w, h);
    }

    private void reiniciarAnimacion() {
        if (timer != null) timer.stop();
        animacionActiva = false;
        panelBFS.reiniciar();
        panelAStar.reiniciar();
        actualizarBotones();
    }

    private void iniciarAuto() {
        if (animacionActiva || ambosTerminados()) return;
        animacionActiva = true;
        actualizarBotones();

        timer = new Timer(velocidadMs, e -> {
            boolean bfsSigue = panelBFS.paso();
            boolean aStarSigue = comparando && panelAStar.paso();
            if (!bfsSigue && !aStarSigue) {
                timer.stop();
                animacionActiva = false;
                actualizarBotones();
            }
        });
        timer.start();
    }

    private void pausarAuto() {
        if (timer != null) timer.stop();
        animacionActiva = false;
        actualizarBotones();
    }

    private void pasoManual() {
        if (animacionActiva) return;
        panelBFS.paso();
        if (comparando) panelAStar.paso();
        actualizarBotones();
    }

    // ==================== PANEL DE MAPA (UN ALGORITMO) ====================

    /**
     * Panel que dibuja el mapa y ejecuta su propio algoritmo paso a paso.
     * Cada instancia encapsula su estado de búsqueda (frontera, visitados, etc.).
     */
    private class PanelMapa extends JPanel {
        private static final int CELL_SIZE = 30;
        private static final int MAX_CELL = 42;   // tamaño máximo de celda al escalar
        private static final int MARGIN = 20;
        private static final int LEGEND_SPACE = 60; // espacio inferior reservado para la leyenda

        final String tipo; // "BFS" o "A*"

        final JLabel lblEstado = new JLabel("Estado: Listo");
        final JLabel lblPasos = new JLabel("Pasos: 0", icono(MaterialDesignM.MAP_MARKER, 14), SwingConstants.LEADING);
        final JLabel lblNodosExpandidos = new JLabel("Nodos: 0", icono(MaterialDesignM.MAGNIFY, 14), SwingConstants.LEADING);

        private Queue<NodoAnim> frontera;
        private Set<Posicion> visitados;
        private Map<Posicion, Posicion> padre;
        private Map<Posicion, Integer> costoG;
        private Map<Posicion, Integer> ordenExpansion; // orden en que se expandió cada celda (para el degradado)
        private List<Posicion> caminoFinal;
        private boolean terminado;
        private int nodosExpandidos;
        private int ordenInsercion;

        PanelMapa(String tipo) {
            this.tipo = tipo;
            setPreferredSize(new Dimension(
                mapa.getColumnas() * CELL_SIZE + 2 * MARGIN,
                mapa.getFilas() * CELL_SIZE + 2 * MARGIN + LEGEND_SPACE
            ));
            setBackground(COLOR_FONDO_MAPA);
            lblPasos.setIconTextGap(6);
            lblNodosExpandidos.setIconTextGap(6);
            reiniciar();
        }

        /** Reajusta el panel a un mapa nuevo (tamaño + reinicio de la búsqueda). */
        void actualizarMapa() {
            setPreferredSize(new Dimension(
                mapa.getColumnas() * CELL_SIZE + 2 * MARGIN,
                mapa.getFilas() * CELL_SIZE + 2 * MARGIN + LEGEND_SPACE
            ));
            reiniciar();
        }

        void reiniciar() {
            if ("A*".equals(tipo)) {
                frontera = new PriorityQueue<>(Comparator
                    .comparingInt((NodoAnim n) -> n.f)
                    .thenComparingInt(n -> n.orden));
            } else {
                frontera = new PriorityQueue<>(Comparator.comparingInt(n -> n.orden)); // FIFO
            }
            visitados = new HashSet<>();
            padre = new HashMap<>();
            costoG = new HashMap<>();
            ordenExpansion = new HashMap<>();
            caminoFinal = null;
            terminado = false;
            nodosExpandidos = 0;
            ordenInsercion = 0;

            Posicion inicio = mapa.getInicio();
            visitados.add(inicio);
            costoG.put(inicio, 0);
            frontera.add(new NodoAnim(inicio, 0, heuristica(inicio), ordenInsercion++));

            lblEstado.setText("Estado: Listo");
            lblPasos.setText("Pasos: 0");
            lblNodosExpandidos.setText("Nodos: 0");
            repaint();
        }

        /** Ejecuta un paso del algoritmo. @return true si debe continuar. */
        boolean paso() {
            if (terminado) return false;
            if (frontera.isEmpty()) { terminar(false); return false; }

            NodoAnim actual = frontera.poll();
            nodosExpandidos++;
            ordenExpansion.put(actual.posicion, nodosExpandidos);
            lblNodosExpandidos.setText("Nodos: " + nodosExpandidos);

            if (mapa.esMar(actual.posicion)) {
                caminoFinal = reconstruirCamino(actual.posicion);
                terminar(true);
                return false;
            }

            for (Posicion vecino : actual.posicion.getVecinos()) {
                if (!mapa.esRio(vecino)) continue;
                int nuevoG = actual.g + 1;
                boolean esNuevo = !visitados.contains(vecino);

                if ("A*".equals(tipo)) {
                    int gPrevio = costoG.getOrDefault(vecino, Integer.MAX_VALUE / 2);
                    if (esNuevo || nuevoG < gPrevio) {
                        visitados.add(vecino);
                        costoG.put(vecino, nuevoG);
                        padre.put(vecino, actual.posicion);
                        frontera.add(new NodoAnim(vecino, nuevoG, heuristica(vecino), ordenInsercion++));
                    }
                } else {
                    if (esNuevo) {
                        visitados.add(vecino);
                        costoG.put(vecino, nuevoG);
                        padre.put(vecino, actual.posicion);
                        frontera.add(new NodoAnim(vecino, nuevoG, heuristica(vecino), ordenInsercion++));
                    }
                }
            }
            repaint();
            return true;
        }

        private void terminar(boolean exito) {
            terminado = true;
            if (exito) {
                int pasos = caminoFinal.size() - 1;
                lblEstado.setText("¡Objetivo! " + pasos + " pasos");
                lblPasos.setText("Pasos: " + pasos);
            } else {
                lblEstado.setText("Sin camino al mar");
                lblPasos.setText("Pasos: —");
            }
            repaint();
        }

        private int heuristica(Posicion p) {
            Posicion mar = mapa.getMar();
            return Math.abs(p.getX() - mar.getX()) + Math.abs(p.getY() - mar.getY());
        }

        private List<Posicion> reconstruirCamino(Posicion objetivo) {
            List<Posicion> camino = new ArrayList<>();
            Posicion actual = objetivo;
            while (actual != null) {
                camino.add(actual);
                actual = padre.get(actual);
            }
            Collections.reverse(camino);
            return camino;
        }

        private boolean esCaminoFinal(Posicion p) {
            return caminoFinal != null && caminoFinal.contains(p);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cols = mapa.getColumnas();
            int rows = mapa.getFilas();
            boolean esBFS = "BFS".equals(tipo);

            // Tamaño de celda dinámico: encaja el mapa completo en el espacio disponible
            int legendSpace = esBFS ? LEGEND_SPACE : 0;
            int usableW = getWidth() - 2 * MARGIN;
            int usableH = getHeight() - 2 * MARGIN - legendSpace;
            int cell = Math.max(10, Math.min(MAX_CELL,
                Math.min(usableW / Math.max(1, cols), usableH / Math.max(1, rows))));

            int mapW = cols * cell;
            int mapH = rows * cell;
            int offX = MARGIN + Math.max(0, (usableW - mapW) / 2); // centra el mapa
            int offY = MARGIN + Math.max(0, (usableH - mapH) / 2);

            // Tamaños proporcionales al de la celda
            int vInset = Math.max(2, cell / 6);    // visitados
            int fInset = Math.max(1, cell / 10);   // frontera
            int cInset = Math.max(1, cell / 12);   // camino
            float ring = Math.max(1.5f, cell / 15f);

            // 1) Celdas base
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    Posicion p = new Posicion(x, y);
                    int px = offX + x * cell;
                    int py = offY + y * cell;

                    Color colorBase;
                    if (p.equals(mapa.getInicio())) colorBase = COLOR_INICIO;
                    else if (p.equals(mapa.getMar())) colorBase = COLOR_MAR;
                    else if (mapa.esRio(p)) colorBase = COLOR_RIO;
                    else colorBase = COLOR_TIERRA;

                    g2.setColor(colorBase);
                    g2.fillRect(px, py, cell, cell);
                    g2.setColor(COLOR_BORDE_CELDA);
                    g2.drawRect(px, py, cell, cell);
                }
            }

            // 2) Visitados: degradado según el orden de expansión (frente de onda)
            int maxOrden = Math.max(1, nodosExpandidos);
            for (Posicion p : visitados) {
                if (p.equals(mapa.getInicio()) || p.equals(mapa.getMar()) || esCaminoFinal(p)) continue;
                int px = offX + p.getX() * cell;
                int py = offY + p.getY() * cell;
                Integer orden = ordenExpansion.get(p);
                float t = (orden == null) ? 0f : Math.min(1f, (float) orden / maxOrden);
                g2.setColor(interpolarColor(COLOR_VISITADO_INI, COLOR_VISITADO_FIN, t));
                g2.fillRect(px + vInset, py + vInset, cell - 2 * vInset, cell - 2 * vInset);
            }

            // 3) Frontera (rojo + anillo)
            for (NodoAnim n : frontera) {
                Posicion p = n.posicion;
                if (p.equals(mapa.getInicio()) || p.equals(mapa.getMar()) || esCaminoFinal(p)) continue;
                int px = offX + p.getX() * cell;
                int py = offY + p.getY() * cell;
                g2.setColor(COLOR_FRONTERA);
                g2.fillRect(px + fInset, py + fInset, cell - 2 * fInset, cell - 2 * fInset);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(ring));
                g2.drawRect(px + fInset, py + fInset, cell - 2 * fInset, cell - 2 * fInset);
            }

            // 4) Camino final (blanco + línea)
            if (caminoFinal != null) {
                g2.setColor(COLOR_CAMINO);
                for (Posicion p : caminoFinal) {
                    if (p.equals(mapa.getInicio()) || p.equals(mapa.getMar())) continue;
                    int px = offX + p.getX() * cell;
                    int py = offY + p.getY() * cell;
                    g2.fillRect(px + cInset, py + cInset, cell - 2 * cInset, cell - 2 * cInset);
                }
                g2.setColor(COLOR_LINEA_CAMINO);
                g2.setStroke(new BasicStroke(Math.max(2f, cell / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < caminoFinal.size() - 1; i++) {
                    Posicion a = caminoFinal.get(i);
                    Posicion b = caminoFinal.get(i + 1);
                    int x1 = offX + a.getX() * cell + cell / 2;
                    int y1 = offY + a.getY() * cell + cell / 2;
                    int x2 = offX + b.getX() * cell + cell / 2;
                    int y2 = offY + b.getY() * cell + cell / 2;
                    g2.drawLine(x1, y1, x2, y2);
                }
            }

            // 5) Etiquetas H y M
            g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, cell / 2)));
            g2.setColor(Color.BLACK);
            drawCenteredText(g2, "H", offX + mapa.getInicio().getX() * cell, offY + mapa.getInicio().getY() * cell, cell);
            g2.setColor(Color.WHITE);
            drawCenteredText(g2, "M", offX + mapa.getMar().getX() * cell, offY + mapa.getMar().getY() * cell, cell);

            // 6) Leyenda (solo en el panel BFS para no duplicar)
            if (esBFS) {
                dibujarLeyenda(g2, cell, offX, offY + rows * cell + 16);
            }
        }

        private void drawCenteredText(Graphics2D g2, String text, int x, int y, int cellSize) {
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (cellSize - fm.stringWidth(text)) / 2;
            int ty = y + (cellSize + fm.getAscent()) / 2 - 2;
            g2.drawString(text, tx, ty);
        }

        /** Interpola linealmente entre dos colores (t en [0,1]). */
        private Color interpolarColor(Color a, Color b, float t) {
            t = Math.max(0f, Math.min(1f, t));
            int r  = (int) Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t);
            int gr = (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
            int bl = (int) Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
            return new Color(r, gr, bl);
        }

        private void dibujarLeyenda(Graphics2D g2, int cell, int x0, int y0) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));

            String[] labels = {"Inicio (H)", "Mar (M)", "Árboles", "Río", "Visitado (orden)", "Frontera", "Camino"};
            Color[] colors = {
                COLOR_INICIO, COLOR_MAR, COLOR_TIERRA, COLOR_RIO,
                COLOR_VISITADO_INI, COLOR_FRONTERA, COLOR_CAMINO
            };

            int sw = 16;          // tamaño del cuadro de color
            int itemGap = 18;     // separación entre items
            int maxX = x0 + mapa.getColumnas() * cell; // borde derecho del mapa
            int x = x0, y = y0;

            for (int i = 0; i < labels.length; i++) {
                int itemW = sw + 6 + g2.getFontMetrics().stringWidth(labels[i]);
                if (x > x0 && x + itemW > maxX) { x = x0; y += sw + 10; } // salto de línea

                if ("Visitado (orden)".equals(labels[i])) {
                    // Barra degradada: primero explorado (claro) → último (oscuro)
                    for (int px = 0; px < sw; px++) {
                        g2.setColor(interpolarColor(COLOR_VISITADO_INI, COLOR_VISITADO_FIN, px / (float) (sw - 1)));
                        g2.fillRect(x + px, y, 1, sw);
                    }
                } else {
                    g2.setColor(colors[i]);
                    g2.fillRect(x, y, sw, sw);
                }
                g2.setColor(new Color(0x212121));
                g2.drawRect(x, y, sw, sw);
                g2.drawString(labels[i], x + sw + 6, y + sw - 3);
                x += itemW + itemGap;
            }
        }
    }

    // ==================== NODO DE ANIMACIÓN ====================

    /** Opción del selector de mapas: nombre visible + fábrica del mapa. */
    private record OpcionMapa(String nombre, Supplier<MapaAmazonas> fabrica) {
        @Override
        public String toString() {
            return nombre;
        }
    }

    /** Nodo genérico usado por ambos algoritmos durante la animación. */
    private static class NodoAnim {
        final Posicion posicion;
        final int g;      // costo real desde el inicio
        final int h;      // heurística (Manhattan al mar)
        final int f;      // g + h  (usado por A*)
        final int orden;  // orden de inserción (desempate y FIFO para BFS)

        NodoAnim(Posicion posicion, int g, int h, int orden) {
            this.posicion = posicion;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.orden = orden;
        }
    }

    /** Método main para probar la visualización directamente. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            aplicarTema(false); // tema claro por defecto
            new VisualizadorBFS(MapaAmazonas.crearConAfluentes());
        });
    }
}
