package com.tetris;

import com.tetris.controller.GameController;
import com.tetris.model.Board;
import com.tetris.view.GameFrame;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada principal da aplicação.
 * Responsável por instanciar e conectar o Model, a View e o Controller.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Cria o Model
            Board board = new Board();

            // 2. Cria a View
            GameFrame gameFrame = new GameFrame();

            // 3. Cria o Controller e conecta o Model e a View
            GameController gameController = new GameController(gameFrame, board);

            // Conecta o controller à frame para que a UI (ex: OverlayPanel) possa usá-lo
            gameFrame.setController(gameController);

            // 4. Inicia o controller (timer & view) e exibe a janela
            gameController.start();
            gameFrame.setVisible(true);
        });
    }
}

