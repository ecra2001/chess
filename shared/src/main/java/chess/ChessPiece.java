package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        switch (this.type) {
            case KING:
                kingMoves(board, myPosition, moves);
                break;
            case QUEEN:
                //queen moves
                break;
            case KNIGHT:
                knightMoves(board, myPosition, moves);
                break;
            case BISHOP:
                bishopMoves(board, myPosition, moves);
                break;
            case ROOK:
                rookMoves(board, myPosition, moves);
                break;
            case PAWN:
                //pawn moves
                break;
        }
        return moves;
    }

    private boolean validPosition(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }

    private void addMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int[][] moveList) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int[] set : moveList) {
            int newRow = row + set[0];
            int newCol = col + set[1];
            if (validPosition(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece destination = board.getPiece(newPosition);
                if (destination == null || destination.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private void kingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] kingMoves = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };
        addMoves(board, myPosition, moves, kingMoves);
    }

    private void knightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        addMoves(board, myPosition, moves, knightMoves);
    }

    private void rookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] knightMoves = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };
        addMoves(board, myPosition, moves, knightMoves);
    }

    private void bishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] knightMoves = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addMoves(board, myPosition, moves, knightMoves);
    }
}
