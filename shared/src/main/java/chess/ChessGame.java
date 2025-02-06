package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean gameOver;
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : pieceMoves) {
            ChessBoard tempBoard = simulateMove(board, move);
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(tempBoard);
            if (!tempGame.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private ChessBoard simulateMove(ChessBoard originalBoard, ChessMove move) {
        ChessBoard simulatedBoard = new ChessBoard();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = originalBoard.getPiece(position);
                if (piece != null) {simulatedBoard.addPiece(position, piece);}
            }
        }
        ChessPiece pieceToMove = simulatedBoard.getPiece(move.getStartPosition());
        simulatedBoard.addPiece(move.getStartPosition(), null);
        simulatedBoard.addPiece(move.getEndPosition(), pieceToMove);
        return simulatedBoard;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece selected");
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not team's turn");
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || isInCheck(teamTurn) || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int promotionRow = (piece.getTeamColor() == TeamColor.WHITE) ? 8 : 1;
            if (move.getEndPosition().getRow() == promotionRow) {
                ChessPiece.PieceType promotion = move.getPromotionPiece();
                if (promotion != null) {
                    board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), promotion));
                }
            }
        }
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = locateKing(teamColor);
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPiece piece = board.getPiece(new ChessPosition(r, c));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    if (canAttackKing(piece, new ChessPosition(r, c), kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canAttackKing(ChessPiece piece, ChessPosition position, ChessPosition kingPosition) {
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private ChessPosition locateKing(TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return checkBoard(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return checkBoard(teamColor);
    }

    public boolean checkBoard(TeamColor teamColor){
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(new ChessPosition(row, col));
                    if (!validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
