package com.tetris.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Representa o estado completo do tabuleiro de jogo.
 * Contém a grelha de peças, a peça atual, a pontuação e toda a lógica principal do jogo.
 * Esta classe é o coração do "Model" no padrão MVC.
 */
public class Board {

    // --- Constantes do Jogo ---
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    private static final int LEVEL_UP_LINES = 10;
    private static final String HIGHSCORE_FILE = "highscore.txt";

    // --- Estado do Jogo ---
    private boolean isStarted = false;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean isGhostPieceEnabled = true; // Novo: Peça fantasma ativa por padrão

    private int score = 0;
    private int highScore = 0;
    private int level = 1;
    private int linesCleared = 0;

    private Piece currentPiece;
    private Piece nextPiece;
    private Shape.Tetrominoe[] boardGrid; 

    public Board() {
        boardGrid = new Shape.Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        currentPiece = new Piece();
        nextPiece = new Piece();
        loadHighScore();
        clearBoard();
    }

    public void start() {
        isStarted = true;
        isGameOver = false;
        isPaused = false;
        score = 0;
        level = 1;
        linesCleared = 0;
        clearBoard();
        
        nextPiece.setRandomShape();
        newPiece();
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_WIDTH * BOARD_HEIGHT; i++) {
            boardGrid[i] = Shape.Tetrominoe.NoShape;
        }
    }

    private void newPiece() {
        currentPiece = nextPiece;
        currentPiece.setX(BOARD_WIDTH / 2);
        currentPiece.setY(BOARD_HEIGHT - 1 + currentPiece.minY());

        nextPiece = new Piece();
        nextPiece.setRandomShape();

        if (!tryMove(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            isGameOver = true;
            currentPiece.setShape(Shape.Tetrominoe.NoShape);
            
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
        }
    }

    private boolean tryMove(Piece piece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + piece.x(i);
            int y = newY - piece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0) {
                return false;
            }

            if (y < BOARD_HEIGHT && shapeAt(x, y) != Shape.Tetrominoe.NoShape) {
                return false;
            }
        }

        currentPiece = piece;
        currentPiece.setX(newX);
        currentPiece.setY(newY);
        return true;
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = currentPiece.getX() + currentPiece.x(i);
            int y = currentPiece.getY() - currentPiece.y(i);
            if (y >= 0 && y < BOARD_HEIGHT) {
                boardGrid[y * BOARD_WIDTH + x] = currentPiece.getShape();
            }
        }
        removeFullLines();
        if (!isGameOver) {
            newPiece();
        }
    }

    private void removeFullLines() {
        int numFullLines = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Shape.Tetrominoe.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        boardGrid[k * BOARD_WIDTH + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            updateScore(numFullLines);
            linesCleared += numFullLines;
            
            if (linesCleared / LEVEL_UP_LINES >= level) {
                level++;
            }
        }
    }

    // --- Ações do Jogador ---

    public void moveLeft() {
        tryMove(currentPiece, currentPiece.getX() - 1, currentPiece.getY());
    }

    public void moveRight() {
        tryMove(currentPiece, currentPiece.getX() + 1, currentPiece.getY());
    }

    public void rotateLeft() {
        tryMove(currentPiece.rotateLeft(), currentPiece.getX(), currentPiece.getY());
    }

    public void rotateRight() {
        tryMove(currentPiece.rotateRight(), currentPiece.getX(), currentPiece.getY());
    }

    public void dropDown() {
        int newY = getGhostPieceY();
        tryMove(currentPiece, currentPiece.getX(), newY);
        pieceDropped();
    }
    
    public void movePieceDown() {
        if (!tryMove(currentPiece, currentPiece.getX(), currentPiece.getY() - 1)) {
            pieceDropped();
        }
    }

    // --- Gestão de Estado do Jogo ---

    public void togglePause() {
        if (!isStarted || isGameOver) return;
        isPaused = !isPaused;
    }

    // Novo: Alterna a visibilidade da peça fantasma
    public void toggleGhostPiece() {
        isGhostPieceEnabled = !isGhostPieceEnabled;
    }

    // Novo: Calcula a posição Y final da peça atual (para a peça fantasma)
    public int getGhostPieceY() {
        int y = currentPiece.getY();
        while (true) {
            if (!canMoveTo(currentPiece, currentPiece.getX(), y - 1)) {
                return y;
            }
            y--;
        }
    }

    // Novo: Método auxiliar para verificar se a peça pode mover-se para uma posição
    private boolean canMoveTo(Piece piece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + piece.x(i);
            int y = newY - piece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0) return false;
            if (y < BOARD_HEIGHT && shapeAt(x, y) != Shape.Tetrominoe.NoShape) return false;
        }
        return true;
    }

    // --- Pontuação ---

    private void updateScore(int lines) {
        int[] points = {0, 40, 100, 300, 1200};
        score += points[lines] * level;
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            highScore = Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Erro ao salvar o high score: " + e.getMessage());
        }
    }

    // --- Getters para o View e Controller ---

    public Shape.Tetrominoe shapeAt(int x, int y) {
        return boardGrid[y * BOARD_WIDTH + x];
    }

    public boolean isStarted() { return isStarted; }
    public boolean isPaused() { return isPaused; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isGhostPieceEnabled() { return isGhostPieceEnabled; } // Novo
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
    public Piece getCurrentPiece() { return currentPiece; }
    public Piece getNextPiece() { return nextPiece; }
}

