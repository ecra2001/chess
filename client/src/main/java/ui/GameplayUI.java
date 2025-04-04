package ui;
import chess.*;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import com.google.gson.Gson;
import model.*;
import exception.ResponseException;
import client.ServerFacade;
import java.util.Objects;
import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.*;
public class GameplayUI {

    static ChessGame game;
    int gameID;
    private final State state;
    ServerFacade facade;
    GameplayUI(ServerFacade facade, State state, GameData gameData) {
        this.facade = facade;
        this.game = gameData.getGame();
        this.gameID = gameData.getGameID();

        this.state = state;
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

    public static void printBoard(ChessGame.TeamColor color, ChessPosition selectedPos) {
        var board = new StringBuilder();

        Collection<ChessMove> possibleMoves = selectedPos != null ? game.validMoves(selectedPos) : null;
        HashSet<ChessPosition> possibleSquares = new HashSet<>(possibleMoves != null ? possibleMoves.size() : 0);
        if (possibleMoves != null) {
            for (ChessMove move : possibleMoves) {
                possibleSquares.add(move.getEndPosition());
            }
        }

        if (color == ChessGame.TeamColor.BLACK) {
            board.append(letterRow("black"));
            for (int i = 1; i < 9; i++) {
                board.append(gameRow(i, "black", possibleSquares));
            }
            board.append(letterRow("black"));
        } else {
            board.append(letterRow("white"));
            for (int i = 8; i > 0; i--) {
                board.append(gameRow(i, "white", possibleSquares));
            }
            board.append(letterRow("white"));
        }
        System.out.println(board);
    }

    private static String letterRow(String color) {
        var board = new StringBuilder();
        board.append(SET_BG_COLOR_DARK_GREY);
        board.append(SET_TEXT_COLOR_WHITE);
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

    private static String gameRow(int row, String color, HashSet<ChessPosition> highlightedSquares) {
        var board = new StringBuilder();
        board.append(SET_BG_COLOR_DARK_GREY);
        board.append(SET_TEXT_COLOR_WHITE);
        board.append(String.format(" %d ", row));
        for (int i = 1; i < 9; i++) {
            if (Objects.equals(color, "black")) {
                int col = i * -1 + 9;
                board.append(squareColor(row, col, highlightedSquares));
                board.append(piece(row, col));
            } else {
                board.append(squareColor(row, i, highlightedSquares));
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

    private static String squareColor(int row, int col, HashSet<ChessPosition> highlightedSquares) {
        ChessPosition square = new ChessPosition(row, col);
        boolean darkSquare = (row + col) % 2 == 0;
        if (darkSquare) {
            if (highlightedSquares.contains(square)) {
                return SET_BG_COLOR_DARK_GREEN;
            }
            return SET_BG_COLOR_LIGHT_GREY;
        } else {
            if (highlightedSquares.contains(square)) {
                return SET_BG_COLOR_GREEN;
            }
            return SET_BG_COLOR_WHITE;
        }
    }

    private static String piece(int row, int column) {
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = game.getBoard().getPiece(position);
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