package com.tetris.view;

import com.tetris.controller.GameController;
import com.tetris.model.Board;
import com.tetris.model.Theme;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Painel de overlay do jogo (menu, pausa, game over) — versão com fontes Arial.
 */
public class OverlayPanel extends JPanel {

    private Board board;
    private GameController controller;
    private JButton startButton;
    private BufferedImage backgroundImage;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private java.awt.Color menuTitleColor = java.awt.Color.WHITE;
    private java.awt.Color menuTextColor = java.awt.Color.WHITE;

    private enum MenuState { ENTER_NAME, SHOW_INFO }
    private MenuState menuState = MenuState.ENTER_NAME;
    private boolean menuPreviouslyVisible = false;

    public OverlayPanel() {
        setOpaque(false);
        loadBackgroundImage();
        initStartButton();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutStartButton();
            }
        });
    }

    private void loadBackgroundImage() {
        try {
            java.net.URL url = getClass().getResource("/com/tetris/view/resources/menu.jpeg");
            if (url == null) url = getClass().getResource("/resources/menu.jpeg");

            if (url != null) {
                backgroundImage = ImageIO.read(url);
                System.out.println("OverlayPanel: loaded background image from: " + url);
            } else {
                backgroundImage = null;
                System.out.println("OverlayPanel: no background image found.");
            }
        } catch (IOException e) {
            backgroundImage = null;
            System.out.println("OverlayPanel: error loading background image: " + e.getMessage());
        }
    }

    private void initStartButton() {
        startButton = new JButton("Start");
        startButton.setFocusable(true);
        startButton.setVisible(false);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBorderPainted(false);
        startButton.setBackground(new Color(50, 50, 50));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller == null) return;

                if (menuState == MenuState.ENTER_NAME) {
                    menuState = MenuState.SHOW_INFO;
                    if (nameField != null) controller.setPlayerName(nameField.getText());
                    if (nameField != null) nameField.setVisible(false);
                    if (nameLabel != null) nameLabel.setVisible(false);
                    startButton.setText("Iniciar");
                    layoutStartButton();
                    repaint();
                } else if (menuState == MenuState.SHOW_INFO) {
                    controller.startGameFromUI();
                }
            }
        });
        setLayout(null);

        nameLabel = new javax.swing.JLabel("Nome:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setVisible(false);
        add(nameLabel);

        nameField = new javax.swing.JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setVisible(false);
        add(nameField);

        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String s = nameField.getText();
                startButton.setEnabled(s != null && !s.trim().isEmpty());
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        add(startButton);
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void requestFocusForName() {
        if (nameField != null) nameField.requestFocusInWindow();
    }

    public void updateTheme(Theme theme) {
        if (theme == null) return;
        Color bg = theme.uiBackground().darker();
        Color fg = (bg.getRed() < 128) ? Color.WHITE : Color.BLACK;
        startButton.setBackground(bg);
        startButton.setForeground(fg);

        String tname = theme.name() == null ? "" : theme.name().toLowerCase();
        if (tname.contains("oficiais") || tname.contains("r2")) {
            menuTitleColor = new Color(255, 215, 0);
        } else if (tname.contains("exército") || tname.contains("exercito")) {
            menuTitleColor = new Color(255, 204, 51);
        } else {
            menuTitleColor = fg;
        }

        double lum = (0.299 * theme.uiBackground().getRed())
                + (0.587 * theme.uiBackground().getGreen())
                + (0.114 * theme.uiBackground().getBlue());
        menuTextColor = (lum < 128) ? Color.WHITE : Color.BLACK;

        startButton.repaint();
        repaint();
    }

    private void layoutStartButton() {
        if (startButton == null) return;
        int w = 180;
        int h = 48;
        int x = (getWidth() - w) / 2;
        int y = Math.max(20, getHeight() - h - 40);
        int nameW = 200;
        int nameH = 28;
        int nameX = (getWidth() - nameW) / 2;
        int nameY = y - nameH - 12;
        nameLabel.setBounds(nameX, nameY - 18, nameW, 18);
        nameField.setBounds(nameX, nameY, nameW, nameH);

        startButton.setBounds(x, y, w, h);
        String s = nameField.getText();
        startButton.setEnabled(s != null && !s.trim().isEmpty());
    }

    public void updateBoard(Board board) {
        this.board = board;
        if (startButton != null) {
            boolean showMenu = (board != null && !board.isStarted());
            startButton.setVisible(showMenu);
            if (showMenu && !menuPreviouslyVisible) {
                menuState = MenuState.ENTER_NAME;
                if (nameField != null && nameLabel != null) {
                    nameField.setVisible(true);
                    nameLabel.setVisible(true);
                }
                startButton.setText("Próximo");
            }
            menuPreviouslyVisible = showMenu;
            layoutStartButton();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        if (!board.isStarted()) {
            drawStartScreen(g);
        } else if (board.isGameOver()) {
            drawGameOver(g);
        } else if (board.isPaused()) {
            drawPaused(g);
        }
    }

    private void drawStartScreen(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        g.setColor(menuTitleColor);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("TETRIS", getWidth() / 2 - 60, getHeight() / 2 - 150);

        g.setColor(menuTextColor);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int y = getHeight() / 2 - 80;
        int x = getWidth() / 2 - 110;

        if (menuState == MenuState.ENTER_NAME) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            y = getHeight() - 150;
            String text = "Digite seu nome e pressione Próximo";
            int stringWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - stringWidth) / 2, y);
        } else if (menuState == MenuState.SHOW_INFO) {
            drawControls(g, x, y);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            y = getHeight() - 150;
            String text = "Revise as informações e pressione Iniciar";
            int stringWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - stringWidth) / 2, y);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(menuTitleColor);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("GAME OVER", getWidth() / 2 - 80, getHeight() / 2);
        g.setColor(menuTextColor);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("ENTER para reiniciar", getWidth() / 2 - 110, getHeight() / 2 + 40);
    }

    private void drawPaused(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(menuTitleColor);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("PAUSADO", getWidth() / 2 - 70, getHeight() / 2 - 150);

        g.setColor(menuTextColor);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int y = getHeight() / 2 - 80;
        int x = getWidth() / 2 - 110;
        drawControls(g, x, y);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        y = getHeight() - 150;
        g.drawString("Pressione P para continuar", getWidth() / 2 - 125, y);
    }

    private void drawControls(Graphics g, int x, int y) {
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("MANUAL DE CONTROLES", x, y);
        y += 30;
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString("←   Mover Esquerda", x, y);
        y += 20;
        g.drawString("→   Mover Direita", x, y);
        y += 20;
        g.drawString("↑   Girar (Horário)", x, y);
        y += 20;
        g.drawString("Z   Girar (Anti-horário)", x, y);
        y += 20;
        g.drawString("↓   Acelerar Queda", x, y);
        y += 20;
        g.drawString("Espaço   Cair Imediatamente", x, y);
        y += 20;
        g.drawString("P   Pausar Jogo", x, y);
        y += 20;
        g.drawString("T   Mudar Tema Visual", x, y);
        y += 20;
        g.drawString("G   Ativar/Desativar Prévia", x, y);
    }
}
