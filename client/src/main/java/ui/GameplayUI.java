package ui;
import chess.*;

import java.util.Objects;

import static ui.EscapeSequences.*;
public class GameplayUI {

    ChessBoard board;
    GameplayUI(ChessBoard board) {
        this.board = board;
    }

    void printBoard() {
        var board = new StringBuilder();
        board.append(letterRow("black"));

        board.append(letterRow("black"));

        board.append(letterRow("white"));

        board.append(letterRow("white"));
        System.out.println(board);
    }

    private String letterRow(String color) {
        var board = new StringBuilder();
        board.append(SET_BG_COLOR_DARK_GREY);
        board.append(SET_TEXT_COLOR_BLACK);
        if (Objects.equals(color, "white")) {
            board.append("    a  b  c  d  e  f  g  h    ");
            board.append("\n");
        } else if (Objects.equals(color, "black")) {
            board.append("    h  g  f  e  d  c  b  a    ");
            board.append("\n");
        }
        board.append(RESET_BG_COLOR);
        board.append(RESET_TEXT_COLOR);
        return board.toString();
    }

    private String gameRow(int row, String color) {
        var board = new StringBuilder();
        board.append(SET_BG_COLOR_DARK_GREY);
        board.append(SET_TEXT_COLOR_BLACK);
        board.append(String.format(" %d ", row));
        for (int i = 1; i < 9; i++) {
            if (Objects.equals(color, "black")) {
                i = i * -1 + 9;
                board.append(squareColor(row, i));
                board.append(piece(row, i));
            }
        }
        board.append(SET_BG_COLOR_DARK_GREY);
        board.append(SET_TEXT_COLOR_BLACK);
        board.append(String.format(" %d ", row));
        board.append("\n");
        board.append(RESET_BG_COLOR);
        board.append(RESET_TEXT_COLOR);
        return board.toString();
    }

    private String squareColor(int row, int col) {
        return null;
    }

    private String piece(int row, int col) {
        return null;
    }
}