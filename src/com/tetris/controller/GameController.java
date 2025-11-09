package com.tetris.controller;

import com.tetris.db.Database;
import com.tetris.model.Board;
import com.tetris.model.Theme;
import com.tetris.view.GameFrame;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * O Controller no padrão MVC.
 * Faz a ponte entre o Model (Board) e a View (GameFrame).
 * Contém o game loop (Timer) e gere os inputs do utilizador.
 */
public class GameController extends KeyAdapter implements ActionListener {

    private static final int INITIAL_DELAY = 400;

    private final GameFrame gameFrame;
    private final Board board;
    private final Timer timer;
    private int currentThemeIndex = 0;
    private String playerName = "";

    public GameController(GameFrame gameFrame, Board board) {
        this.gameFrame = gameFrame;
        this.board = board;
        this.timer = new Timer(getDelayForLevel(), this);
        this.gameFrame.getGamePanel().addKeyListener(this);
        this.gameFrame.getGamePanel().setFocusable(true);
    }

    /**
     * Método público para ser chamado pela UI (botão Start) para iniciar o jogo.
     */
    public void startGameFromUI() {
        if ((!board.isStarted() || board.isGameOver())) {
            if (playerName == null || playerName.trim().isEmpty()) {
                if (gameFrame.getOverlayPanel() != null) {
                    gameFrame.getOverlayPanel().requestFocusForName();
                }
                return;
            }

            board.start();
            if (!timer.isRunning()) {
                timer.start();
            }

            gameFrame.getGamePanel().requestFocusInWindow();
            updateView();
        }
    }

    public void setPlayerName(String name) {
        this.playerName = name == null ? "" : name.trim();
    }

    public void start() {
        timer.start();
        gameFrame.getGamePanel().requestFocusInWindow();
        updateView();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (board.isStarted() && !board.isPaused() && !board.isGameOver()) {
            board.movePieceDown();
        }

        if (board.isGameOver()) {
            timer.stop();
            Database.createTable();
            String playerToSave = (playerName == null || playerName.trim().isEmpty()) ? "Jogador1" : playerName;
            Database.saveGame(playerToSave, board.getScore(), board.getLevel(), board.getLinesCleared());
            updateView();
            return;
        }

        timer.setDelay(getDelayForLevel());
        updateView();
    }

    private void updateView() {
        gameFrame.getGamePanel().getBoardPanel().updateBoard(board);
        gameFrame.getGamePanel().getInfoPanel().updateInfo(board);
        gameFrame.getOverlayPanel().updateBoard(board);

        Theme currentTheme = Theme.AVAILABLE_THEMES[currentThemeIndex];
        gameFrame.getGamePanel().updateTheme(currentTheme);
        gameFrame.getOverlayPanel().updateTheme(currentTheme);

        gameFrame.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();

        if (keycode == KeyEvent.VK_T) {
            currentThemeIndex = (currentThemeIndex + 1) % Theme.AVAILABLE_THEMES.length;
            updateView();
            return;
        }

        // Alterna peça fantasma
        if (keycode == KeyEvent.VK_G) {
            board.toggleGhostPiece();
            updateView();
            return;
        }

        // ENTER: iniciar o jogo
        if ((!board.isStarted() || board.isGameOver()) && keycode == KeyEvent.VK_ENTER) {
            if (playerName == null || playerName.trim().isEmpty()) {
                if (gameFrame.getOverlayPanel() != null) {
                    gameFrame.getOverlayPanel().requestFocusForName();
                }
                return;
            }

            board.start();
            if (!timer.isRunning()) {
                timer.start();
            }
            updateView();
            return;
        }

        if (!board.isStarted() || board.isGameOver()) {
            return;
        }

        // PAUSE (P)
        if (keycode == KeyEvent.VK_P) {
            board.togglePause();
            updateView();
            return;
        }

        if (board.isPaused()) {
            return;
        }

        switch (keycode) {
            case KeyEvent.VK_LEFT:
                board.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                board.moveRight();
                break;
            case KeyEvent.VK_DOWN:
                board.movePieceDown();
                break;
            case KeyEvent.VK_UP:
                board.rotateRight();
                break;
            case KeyEvent.VK_Z:
                board.rotateLeft();
                break;
            case KeyEvent.VK_SPACE:
                board.dropDown();
                break;
        }

        updateView();
    }

    private int getDelayForLevel() {
        return Math.max(100, INITIAL_DELAY - (board.getLevel() - 1) * 30);
    }

    /**
     * Reinicia completamente o jogo (usado pelo botão "Reiniciar Missão").
     * Corrigido para não depender de setPaused(boolean) inexistente.
     */
    public void resetGame() {
        try {
            // Para o timer se estiver rodando
            if (timer.isRunning()) {
                timer.stop();
            }

            // Se o board estiver pausado, despausa usando o método público que já existe
            try {
                if (board.isPaused()) {
                    board.togglePause();
                }
            } catch (Exception ex) {
                // fallback silencioso caso o Board se comporte diferente — não queremos quebrar o reset
                System.err.println("Aviso: não foi possível verificar/despausar board via isPaused/togglePause: " + ex.getMessage());
            }

            // Reinicia o tabuleiro (usa board.start() conforme seu código existente)
            board.start();

            // Reinicia o timer e o foco
            timer.setDelay(getDelayForLevel());
            timer.start();

            // Atualiza a tela
            updateView();

            // Garante que o teclado volte a funcionar
            gameFrame.getGamePanel().requestFocusInWindow();

            System.out.println("Missão reiniciada com sucesso!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
