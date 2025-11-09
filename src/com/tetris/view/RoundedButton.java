package com.tetris.view;

import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.Font;

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private Color borderColor;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    public void setColors(Color background, Color border) {
        this.backgroundColor = background;
        this.borderColor = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenha o fundo arredondado
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        // Desenha a borda
        g2d.setColor(borderColor);
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

        // Centraliza e desenha o texto
        FontMetrics metrics = g2d.getFontMetrics(getFont());
        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        
        g2d.setColor(getForeground());
        g2d.drawString(getText(), x, y);

        g2d.dispose();
    }
}