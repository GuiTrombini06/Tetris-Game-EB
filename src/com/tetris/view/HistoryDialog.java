package com.tetris.view;

import com.tetris.db.Database;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HistoryDialog extends JDialog {

    private JTextArea textArea;

    public HistoryDialog(Frame owner) {
        super(owner, "Histórico de Missões", false); // "Missões" dá um toque temático
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- Cores inspiradas no Exército Brasileiro ---
        Color verdeOliva = new Color(60, 68, 50);
        Color verdeEscuro = new Color(40, 46, 35);
        Color verdeClaro = new Color(135, 145, 110);
        Color amareloEB = new Color(212, 175, 55);
        Color textoVerde = new Color(180, 255, 180);

        // --- Painel principal com textura de “camuflado” simples ---
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // fundo com textura simples tipo camuflagem
                Graphics2D g2 = (Graphics2D) g;
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        g2.setColor((i + j) % 80 == 0 ? verdeOliva : verdeEscuro);
                        g2.fillRect(i, j, 40, 40);
                    }
                }
            }
        };
        contentPanel.setBorder(BorderFactory.createLineBorder(amareloEB, 3));

        // --- Área de texto ---
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(textoVerde);
        textArea.setFont(new Font("Consolas", Font.BOLD, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        atualizarTexto();

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(verdeClaro, 2));
        scroll.getVerticalScrollBar().setBackground(verdeOliva);

        // --- Botão "Apagar histórico" ---
        JButton clearButton = new JButton("Apagar histórico");
        clearButton.setBackground(verdeEscuro);
        clearButton.setForeground(amareloEB);
        clearButton.setFont(new Font("Consolas", Font.BOLD, 13));
        clearButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(amareloEB, 2),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito “hover” (muda cor ao passar o mouse)
        clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearButton.setBackground(verdeClaro);
                clearButton.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearButton.setBackground(verdeEscuro);
                clearButton.setForeground(amareloEB);
            }
        });

        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Confirmar exclusão de todas as missões registradas?",
                    "Apagar Histórico",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                Database.clearHistory();
                atualizarTexto();
                JOptionPane.showMessageDialog(this, "Histórico apagado com sucesso!");
            }
        });

        // --- Painel inferior de botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(clearButton);

        contentPanel.add(scroll, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        // --- Configurações do diálogo ---
        setFocusableWindowState(false);
        setAlwaysOnTop(true);
        setSize(440, 320);
        setLocationRelativeTo(getOwner());
    }

    private void atualizarTexto() {
        List<String> history = Database.getGameHistory();
        if (history.isEmpty()) {
            textArea.setText("Nenhuma missão registrada até o momento.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String line : history) {
                sb.append("• ").append(line).append('\n');
            }
            textArea.setText(sb.toString());
        }
    }
}
