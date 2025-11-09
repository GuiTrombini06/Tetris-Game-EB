package com.tetris.db;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Database {
    private static final String URL = "jdbc:sqlite:tetris.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS game_session (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_name TEXT,
                score INTEGER,
                level INTEGER,
                lines_cleared INTEGER,
                date_time TEXT
            );
        """;
        try (Connection conn = connect()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveGame(String player, int score, int level, int lines) {
        // Calcula a data/hora no fuso de Brasília (America/Sao_Paulo) e grava como texto
        String sql = "INSERT INTO game_session (player_name, score, level, lines_cleared, date_time) VALUES (?, ?, ?, ?, ?)";
        // Formato legível: yyyy-MM-dd HH:mm:ss
        String dateTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player);
            pstmt.setInt(2, score);
            pstmt.setInt(3, level);
            pstmt.setInt(4, lines);
            pstmt.setString(5, dateTime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 👇 INSIRA AQUI ESTE MÉTODO
    public static void listGames() {
        String sql = "SELECT player_name, score, level, lines_cleared, date_time FROM game_session ORDER BY id DESC";
        try (Connection conn = connect()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("\n=== Histórico de partidas ===");
                while (rs.next()) {
                    System.out.println(
                        rs.getString("player_name") + " - " +
                        rs.getInt("score") + " pontos, nível " +
                        rs.getInt("level") + " (" + rs.getString("date_time") + ")"
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retorna o histórico de partidas como uma lista de strings (cada entrada é uma linha formatada).
     */
    public static java.util.List<String> getGameHistory() {
        java.util.List<String> history = new java.util.ArrayList<>();
        String sql = "SELECT player_name, score, level, lines_cleared, date_time FROM game_session ORDER BY id DESC";
        try (Connection conn = connect()) {
            if (conn == null) return history;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String line = rs.getString("player_name") + " - " +
                            rs.getInt("score") + " pontos, nível " +
                            rs.getInt("level") + " (" + rs.getString("date_time") + ")";
                    history.add(line);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static void clearHistory() {
        try (Connection conn = connect()) {
            if (conn == null) return;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM game_session");
                stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='game_session'");
                System.out.println("Histórico limpo e contador de IDs reiniciado!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
