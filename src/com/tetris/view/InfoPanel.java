package com.tetris.view;

import com.tetris.controller.GameController;
import com.tetris.model.Board;
import com.tetris.model.Piece;
import com.tetris.model.Shape;
import com.tetris.model.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Painel lateral militar estilizado – exibe informações de pontuação, nível,
 * linhas e próxima peça, além do botão "Reiniciar Missão".
 */
public class InfoPanel extends JPanel {

    private static final int PANEL_WIDTH = 250;
    private static final int SQUARE_PREVIEW_SIZE = 20;

    private Board board;
    private Theme currentTheme;
    private GameController controller;

    private RoundedButton resetButton;

    public InfoPanel() {
        this.currentTheme = Theme.AVAILABLE_THEMES[0];
        setPreferredSize(new Dimension(PANEL_WIDTH, 1));
        setBackground(new Color(60, 68, 50)); // verde-oliva
        setLayout(null);

        initResetButton();
    }

    private void initResetButton() {
        resetButton = new RoundedButton("Reiniciar Missão");
        resetButton.setFont(new Font("Consolas", Font.BOLD, 13));
        resetButton.setForeground(new Color(212, 175, 55)); // dourado EB
        resetButton.setColors(new Color(40, 46, 35), new Color(212, 175, 55));
        resetButton.setBounds(40, 470, 170, 40);
        resetButton.addActionListener(e -> {
            if (controller != null) {
                controller.resetGame();
            }
        });
        add(resetButton);
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void updateInfo(Board board) {
        this.board = board;
    }

    public void updateTheme(Theme theme) {
        this.currentTheme = theme;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground((Graphics2D) g);
        if (board != null && board.isStarted()) {
            drawGameInfo((Graphics2D) g);
        }
    }

    private void drawBackground(Graphics2D g2d) {
        Color verdeOliva = new Color(60, 68, 50);
        Color verdeEscuro = new Color(40, 46, 35);
        for (int i = 0; i < getWidth(); i += 40) {
            for (int j = 0; j < getHeight(); j += 40) {
                g2d.setColor((i + j) % 80 == 0 ? verdeOliva : verdeEscuro);
                g2d.fillRect(i, j, 40, 40);
            }
        }
        g2d.setColor(new Color(212, 175, 55));
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    private void drawGameInfo(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 20;
        int blockWidth = PANEL_WIDTH - (2 * padding);
        int blockHeight = 55;
        int spacing = 12;
        int y = 35;

        // Recorde e Pontuação
        y = drawInfoBlock(g2d, "RECORD DE OPERAÇÃO", String.format("%06d", board.getHighScore()),
                padding, y, blockWidth, blockHeight);
        y += spacing;
        y = drawInfoBlock(g2d, "MISSÃO ATUAL", String.format("%06d", board.getScore()),
                padding, y, blockWidth, blockHeight);
        y += spacing;

        // Nível e Inimigos Neutralizados (em duas linhas)
        int halfWidth = (blockWidth - spacing) / 2;

        // Esquerda — Inimigos Neutralizados (corrigido para getLinesCleared)
        drawInfoBlockMultiLine(g2d, "INIMIGOS", "NEUTRALIZADOS",
                String.format("%03d", board.getLinesCleared()),
                padding, y, halfWidth, blockHeight);

        // Direita — Nível de Alerta (usa o level)
        drawInfoBlockMultiLine(g2d, "NÍVEL", "DE ALERTA",
                String.format("%02d", board.getLevel()),
                padding + halfWidth + spacing, y, halfWidth, blockHeight);
        y += blockHeight + spacing;

        // Próxima peça
        y = drawNextPiecePanel(g2d, "PRÓXIMO REFORÇO", padding, y, blockWidth, 110);

        // Texto da pausa (sem box)
        g2d.setFont(new Font("Consolas", Font.BOLD, 14));
        g2d.setColor(new Color(212, 175, 55));
        g2d.drawString("PAUSAR MISSÃO: [P]", padding + 30, getHeight() - 25);
    }

    // 🔹 Caixa normal
    private int drawInfoBlock(Graphics2D g, String title, String value, int x, int y, int width, int height) {
        Color bg = new Color(45, 52, 40);
        Color border = new Color(212, 175, 55);
        g.setColor(bg);
        g.fillRoundRect(x, y, width, height, 10, 10);
        g.setColor(border);
        g.drawRoundRect(x, y, width, height, 10, 10);

        g.setFont(new Font("Consolas", Font.PLAIN, 13));
        g.setColor(new Color(180, 255, 180));
        g.drawString(title, x + 10, y + 20);

        g.setFont(new Font("Consolas", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        g.drawString(value, x + 10, y + 45);

        return y + height;
    }

    // 🔹 Caixa com título em duas linhas (para “Inimigos Neutralizados” e “Nível de Alerta”)
    private void drawInfoBlockMultiLine(Graphics2D g, String line1, String line2, String value,
                                        int x, int y, int width, int height) {
        Color bg = new Color(45, 52, 40);
        Color border = new Color(212, 175, 55);
        g.setColor(bg);
        g.fillRoundRect(x, y, width, height, 10, 10);
        g.setColor(border);
        g.drawRoundRect(x, y, width, height, 10, 10);

        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        g.setColor(new Color(180, 255, 180));
        int line1Width = g.getFontMetrics().stringWidth(line1);
        int line2Width = g.getFontMetrics().stringWidth(line2);
        g.drawString(line1, x + (width - line1Width) / 2, y + 16);
        g.drawString(line2, x + (width - line2Width) / 2, y + 30);

        g.setFont(new Font("Consolas", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        int valueWidth = g.getFontMetrics().stringWidth(value);
        g.drawString(value, x + (width - valueWidth) / 2, y + 52);
    }

    // 🔹 Caixa da próxima peça
    private int drawNextPiecePanel(Graphics2D g, String title, int x, int y, int width, int height) {
        Color bg = new Color(45, 52, 40);
        Color border = new Color(212, 175, 55);
        g.setColor(bg);
        g.fillRoundRect(x, y, width, height, 10, 10);
        g.setColor(border);
        g.drawRoundRect(x, y, width, height, 10, 10);

        g.setFont(new Font("Consolas", Font.PLAIN, 13));
        g.setColor(new Color(180, 255, 180));
        g.drawString(title, x + 10, y + 20);

        if (board != null && board.getNextPiece() != null) {
            Piece nextPiece = board.getNextPiece();
            int previewX = x + (width / 2) - (2 * SQUARE_PREVIEW_SIZE);
            int previewY = y + 45;
            for (int i = 0; i < 4; i++) {
                int px = previewX + (nextPiece.x(i) + 1) * SQUARE_PREVIEW_SIZE;
                int py = previewY + (1 - nextPiece.y(i)) * SQUARE_PREVIEW_SIZE;
                drawSquare(g, px, py, nextPiece.getShape(), SQUARE_PREVIEW_SIZE);
            }
        }
        return y + height;
    }

    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoe shape, int size) {
        Color[] colors = currentTheme.pieceColors();
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, size - 2, size - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y, x + size - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + size - 1, y + size - 1, x, y + size - 1);
    }
}
