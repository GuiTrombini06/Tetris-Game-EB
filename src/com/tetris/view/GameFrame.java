package com.tetris.view;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import java.awt.Dimension;

/**
 * A janela principal do jogo (o JFrame).
 * Utiliza um JLayeredPane para sobrepor o painel do jogo e o painel de overlays.
 */
public class GameFrame extends JFrame {

    private GamePanel gamePanel;
    private OverlayPanel overlayPanel;
    private JLayeredPane layeredPane;
    private com.tetris.controller.GameController controller;

    public GameFrame() {
        initComponents();
    }

    private void initComponents() {
        // Cria o painel em camadas
        layeredPane = new JLayeredPane();

        // Cria os nossos painéis
        gamePanel = new GamePanel();
        overlayPanel = new OverlayPanel();

        // Define o tamanho dos painéis para que ocupem toda a janela
        // O tamanho é baseado nas preferências do gamePanel
        Dimension size = gamePanel.getPreferredSize();
        layeredPane.setPreferredSize(size);
        gamePanel.setBounds(0, 0, size.width, size.height);
        overlayPanel.setBounds(0, 0, size.width, size.height);

        // Adiciona os painéis ao JLayeredPane em camadas diferentes
        // DEFAULT_LAYER é a camada de baixo, PALETTE_LAYER fica por cima
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

        // Adiciona o JLayeredPane à janela
        add(layeredPane);

        setTitle("Tetris - Exército Brasileiro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    // --- Métodos de acesso para o Controller ---

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public OverlayPanel getOverlayPanel() {
        return overlayPanel;
    }

    /**
     * Conecta o GameController à frame para que componentes (ex: OverlayPanel e InfoPanel)
     * possam chamar ações do controller (ex: iniciar jogo ou reiniciar missão).
     */
    public void setController(com.tetris.controller.GameController controller) {
        this.controller = controller;

        // Conecta controller ao OverlayPanel (tela de início/pausa)
        if (this.overlayPanel != null) {
            this.overlayPanel.setController(controller);
        }

        // 🔰 Conecta controller ao InfoPanel (painel lateral)
        if (this.gamePanel != null && this.gamePanel.getInfoPanel() != null) {
            this.gamePanel.getInfoPanel().setController(controller);
        }
    }

    public com.tetris.controller.GameController getController() {
        return controller;
    }
}
