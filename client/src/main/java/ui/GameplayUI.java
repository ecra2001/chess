package ui;
import chess.*;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import com.google.gson.Gson;
import model.*;
import exception.ResponseException;
import client.ServerFacade;
import java.util.Objects;

import static ui.EscapeSequences.*;
public class GameplayUI {

    ChessBoard board;
    //private final ServerFacade facade;
    private final State state;
    GameplayUI(String serverUrl, State state, ChessBoard board) {
        this.board = board;
        this.state = state;
        //facade = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        // try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> leave();
                case "quit" -> "quit";
                default -> help();
            };
        // } catch (ResponseException | DataFormatException ex) {
        //     return ex.getMessage();
        // }
    }

    void printBoard() {
        var board = new StringBuilder();
        board.append(letterRow("black"));
        for (int i = 1; i < 9; i++) {
            board.append(gameRow(i, "black"));
        }
        board.append(letterRow("black"));
        board.append("\n");
        board.append(letterRow("white"));
        for (int i = 8; i > 0; i--) {
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
        } else if (Objects.equals(color, "black")) {
            board.append("    h  g  f  e  d  c  b  a    ");
        }
        board.append(RESET_BG_COLOR);
        board.append(RESET_TEXT_COLOR);
        board.append("\n");
        return board.toString();
    }

    private String gameRow(int row, String color) {
        var board = new StringBuilder();
        board.append(SET_BG_COLOR_DARK_GREY);
        board.append(SET_TEXT_COLOR_BLACK);
        board.append(String.format(" %d ", row));
        for (int i = 1; i < 9; i++) {
            if (Objects.equals(color, "black")) {
                int col = i * -1 + 9;
                board.append(squareColor(row, col));
                board.append(piece(row, col));
            } else {
                board.append(squareColor(row, i));
                board.append(piece(row, i));
            }
        }
        board.append(SET_BG_COLOR_DARK_GREY);
        board.append(SET_TEXT_COLOR_BLACK);
        board.append(String.format(" %d ", row));
        board.append(RESET_BG_COLOR);
        board.append(RESET_TEXT_COLOR);
        board.append("\n");
        return board.toString();
    }

    private String squareColor(int row, int col) {
        boolean darkSquare = (row + col) % 2 == 0;
        if (darkSquare) {
            return SET_BG_COLOR_LIGHT_GREY;
        } else {
            return SET_BG_COLOR_WHITE;
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

    public String leave() {
        state.setInGame(false);
        return "left game";
    }

    public String help() {
        return """
                redraw
                leave
                move
                resign
                highlight
                quit
                help
                """;
    }
}