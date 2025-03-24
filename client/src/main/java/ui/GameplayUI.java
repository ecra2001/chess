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
        for (int i = 8; i > 0; i--) {
            board.append(gameRow(i, "black"));
        }
        board.append(letterRow("black"));

        board.append(letterRow("white"));
        for (int i = 1; i < 9; i++) {
            board.append(gameRow(i, "white"));
        }
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
        boolean darkSquare = (row + col) % 2 == 0;
        if (darkSquare) {
            return SET_BG_COLOR_DARK_GREEN;
        } else {
            return SET_BG_COLOR_GREEN;
        }
    }

    private String piece(int row, int column) {
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            return EMPTY;
        }

        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case QUEEN -> WHITE_QUEEN;
                case KING -> WHITE_KING;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case QUEEN -> BLACK_QUEEN;
                case KING -> BLACK_KING;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        };
    }
}