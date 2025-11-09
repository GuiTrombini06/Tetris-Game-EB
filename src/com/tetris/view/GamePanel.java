package com.tetris.view;

import com.tetris.model.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Painel principal que contém e organiza o tabuleiro e as informações do jogo.
 * Inclui botão de histórico com área de fundo militar estilizada.
 */
public class GamePanel extends JPanel {

    private BoardPanel boardPanel;
    private InfoPanel infoPanel;
    private JButton historyButton;

    public GamePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        boardPanel = new BoardPanel();
        infoPanel = new InfoPanel();

        // Painel lateral que contém informações e botões
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.add(infoPanel, BorderLayout.CENTER);

        // Painel inferior com fundo camuflado e moldura dourada
        JPanel bottomPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                Color verde1 = new Color(60, 68, 50);
                Color verde2 = new Color(40, 46, 35);
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        g2.setColor((i + j) % 80 == 0 ? verde1 : verde2);
                        g2.fillRect(i, j, 40, 40);
                    }
                }
                // Moldura dourada em volta do painel
                g2.setColor(new Color(212, 175, 55));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
        bottomPanel.setPreferredSize(new Dimension(250, 80));
        bottomPanel.setOpaque(false);

        // Botão de histórico (mantido o mesmo estilo anterior)
        historyButton = new JButton("Histórico de Missões");
        historyButton.setFont(new Font("Consolas", Font.BOLD, 12));
        historyButton.setForeground(new Color(212, 175, 55));
        historyButton.setBackground(new Color(40, 46, 35));
        historyButton.setFocusPainted(false);
        historyButton.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2, true));
        historyButton.setPreferredSize(new Dimension(180, 40));
        historyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        historyButton.setOpaque(true);

        // Abre o histórico quando clicado
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame top = (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
                HistoryDialog dlg = new HistoryDialog(top);
                dlg.setModal(false);
                dlg.setVisible(true);
                SwingUtilities.invokeLater(() -> GamePanel.this.requestFocusInWindow());
            }
        });

        bottomPanel.add(historyButton);
        sidePanel.add(bottomPanel, BorderLayout.SOUTH);

        add(boardPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        setBackground(new Color(50, 55, 45));
    }

    public void updateTheme(Theme theme) {
        infoPanel.updateTheme(theme);
        boardPanel.updateTheme(theme);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
}
