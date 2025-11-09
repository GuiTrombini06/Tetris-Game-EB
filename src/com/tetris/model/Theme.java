package com.tetris.model;

import java.awt.Color;

/**
 * Representa um tema visual para o jogo, contendo todas as cores necessárias.
 * Usamos um 'record' para uma definição concisa e imutável de um tema.
 */
public record Theme(
    String name,
    Color uiBackground,
    Color boardBackground,
    Color grid,
    Color[] pieceColors // Array com 8 cores: a primeira é para 'NoShape', as outras 7 para as peças.
) {
    // --- Temas Pré-definidos ---

    /**
     * O tema escuro original do jogo.
     */
    public static final Theme CLASSIC_DARK = new Theme(
        "Exército Brasileiro",
        new Color(34, 85, 40),      // fundo UI - verde oliva mais claro para facilitar prévia
        new Color(16, 48, 24),      // fundo tabuleiro - verde ligeiramente mais claro para ver a prévia
        new Color(40, 70, 36),      // grid - verde oliva médio para linhas
        new Color[] {
            new Color(8, 10, 12),        // NoShape - quase preto
            new Color(85, 107, 47),      // ZShape - dark olive green
            new Color(107, 142, 35),     // SShape - olivedrab/verde militar
            new Color(139, 69, 19),      // LineShape - marrom (terra)
            new Color(194, 178, 128),    // TShape - cáqui/areia
            new Color(120, 140, 90),     // SquareShape - musgo
            new Color(60, 40, 20),       // LShape - marrom escuro
            new Color(46, 125, 50)       // MirroredLShape - verde militar mais vivo
        }
    );

    /**
     * Um tema claro, com cores vibrantes.
     */
  public static final Theme LIGHT = new Theme(
    "Oficiais R2",
    new Color(20, 30, 40),   // fundo UI - cinza azulado escuro
    new Color(18, 22, 28),   // fundo do tabuleiro - cinza carvão
    new Color(70, 85, 95),   // grid - cinza-azulado para linhas
    new Color[] {
        new Color(18, 18, 20),      // NoShape - quase preto
        new Color(25, 25, 112),     // Z - midnight blue
        new Color(70, 130, 180),    // S - steel blue
        new Color(255, 215, 0),     // Line - dourado/amarillo
        new Color(100, 149, 237),   // T - cornflower blue
        new Color(169, 169, 169),   // Square - dark gray
        new Color(112, 128, 144),   // L - slate gray
        new Color(240, 230, 140)    // MirroredL - khaki amarelo claro
    }
);
        
    

    // Array que contém todos os temas disponíveis para fácil acesso.
    public static final Theme[] AVAILABLE_THEMES = { CLASSIC_DARK, LIGHT, };
}
