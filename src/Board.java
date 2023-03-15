import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Board
{
    long[] bitBoards = new long[12];
    long enPassantTargetSquare;
    int halfMoveClock;
    int fullMoveNumber;
    boolean isWhitesMove;
    boolean whiteKingSideCastle;
    boolean whiteQueenSideCastle;
    boolean blackKingSideCastle;
    boolean blackQueenSideCastle;

    Board()
    {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }
    Board(String FENString)
    {
        String[] data = FENString.split(" ");
        // Adding pieces to bitboards
        Arrays.fill(bitBoards, 0);
        for (char pieceChar: data[0].toCharArray())
        {
            if (pieceChar == '/') continue;
            for (EnumPiece piece: EnumPiece.values()) bitBoards[piece.ordinal()] <<= 1;
            switch (pieceChar)
            {
                case '1', '2', '3', '4', '5', '6', '7', '8' -> {
                    for (EnumPiece piece: EnumPiece.values()) bitBoards[piece.ordinal()] <<= Integer.parseInt(String.valueOf(pieceChar)) - 1;
                }
                case 'p' -> bitBoards[EnumPiece.blackPawns.ordinal()] += 1;
                case 'r' -> bitBoards[EnumPiece.blackRooks.ordinal()] += 1;
                case 'n' -> bitBoards[EnumPiece.blackKnights.ordinal()] += 1;
                case 'b' -> bitBoards[EnumPiece.blackBishops.ordinal()] += 1;
                case 'q' -> bitBoards[EnumPiece.blackQueens.ordinal()] += 1;
                case 'k' -> bitBoards[EnumPiece.blackKing.ordinal()] += 1;
                case 'P' -> bitBoards[EnumPiece.whitePawns.ordinal()] += 1;
                case 'R' -> bitBoards[EnumPiece.whiteRooks.ordinal()] += 1;
                case 'N' -> bitBoards[EnumPiece.whiteKnights.ordinal()] += 1;
                case 'B' -> bitBoards[EnumPiece.whiteBishops.ordinal()] += 1;
                case 'Q' -> bitBoards[EnumPiece.whiteQueens.ordinal()] += 1;
                case 'K' -> bitBoards[EnumPiece.whiteKing.ordinal()] += 1;
            }
        }

        // Finding sides move
        isWhitesMove = Objects.equals(data[1], "w");

        // Get castling rights.
        whiteKingSideCastle = false;
        whiteQueenSideCastle = false;
        blackKingSideCastle = false;
        blackQueenSideCastle = false;
        if (!Objects.equals(data[2], "-")) for (char castleChar: data[2].toCharArray())
        {
            switch (castleChar)
            {
                case 'K' -> whiteKingSideCastle = true;
                case 'Q' -> whiteQueenSideCastle = true;
                case 'k' -> blackKingSideCastle = true;
                case 'q' -> blackQueenSideCastle = true;
            }
        }

        // Getting en passant square
        if (!Objects.equals(data[3], "-")) enPassantTargetSquare = 1L << getBitIndex(data[3]);
        else enPassantTargetSquare = 0;

        // Get half move clock number.
        halfMoveClock = Integer.parseInt(data[4]);

        // Get full move number.
        fullMoveNumber = Integer.parseInt(data[5]);
    }

    List<int[]> possibleMoves()
    {
        BoardConsts.WHITE_PIECES =
                        bitBoards[EnumPiece.whitePawns.ordinal()] |
                        bitBoards[EnumPiece.whiteKnights.ordinal()] |
                        bitBoards[EnumPiece.whiteBishops.ordinal()] |
                        bitBoards[EnumPiece.whiteRooks.ordinal()] |
                        bitBoards[EnumPiece.whiteQueens.ordinal()] |
                        bitBoards[EnumPiece.whiteKing.ordinal()];
        BoardConsts.BLACK_PIECES =
                bitBoards[EnumPiece.blackPawns.ordinal()] |
                        bitBoards[EnumPiece.blackKnights.ordinal()] |
                        bitBoards[EnumPiece.blackBishops.ordinal()] |
                        bitBoards[EnumPiece.blackRooks.ordinal()] |
                        bitBoards[EnumPiece.blackQueens.ordinal()] |
                        bitBoards[EnumPiece.blackKing.ordinal()];
        BoardConsts.OCCUPIED_SQUARES = BoardConsts.BLACK_PIECES | BoardConsts.WHITE_PIECES;
        BoardConsts.EMPTY_SQUARES = ~BoardConsts.OCCUPIED_SQUARES;
        if (isWhitesMove) return possibleMovesW();
        return possibleMovesB();
    }

    private List<int[]> possibleMovesW() // Returns all possible moves with format {from, to}
    {
        List<int[]> moves = new ArrayList<>();
        moves.addAll(possibleMovesWP());
        // moves.addAll(possibleMovesWN());
        // moves.addAll(possibleMovesWB());
        // moves.addAll(possibleMovesWR());
        // moves.addAll(possibleMovesWQ());
        // moves.addAll(possibleMovesWK());
        return moves;
    }

    private List<int[]> possibleMovesB()
    {
        List<int[]> moves = new ArrayList<>();
        moves.addAll(possibleMovesBP());
        // moves.addAll(possibleMovesBN());
        // moves.addAll(possibleMovesBB());
        // moves.addAll(possibleMovesBR());
        // moves.addAll(possibleMovesBQ());
        // moves.addAll(possibleMovesBK());
        return moves;
    }

    private List<int[]> possibleMovesWP()
    {
        long curr;
        List<int[]> moves = new ArrayList<>();

        // Moves
        curr = possibleMovesWPSinglePush();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr - 8, sqr});

        curr = possibleMovesWPDoublePush();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr - 16, sqr});

        // Captures and en passant
        curr = possibleMovesWPEastCapture();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr, sqr + 9});

        curr = possibleMovesWPWestCapture();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr, sqr + 7});

        return moves;
    }

    private List<int[]> possibleMovesBP()
    {
        long curr;
        List<int[]> moves = new ArrayList<>();

        // Moves
        curr = possibleMovesBPSinglePush();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr + 8, sqr});

        curr = possibleMovesBPDoublePush();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr + 16, sqr});

        // Captures and en passant
        curr = possibleMovesBPEastCapture();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr, sqr - 9});

        curr = possibleMovesBPWestCapture();
        for (int sqr = 0; sqr < 64; sqr++) if (((curr >> sqr) & 1) == 1) moves.add(new int[] {sqr, sqr - 7});

        return moves;
    }

    private long possibleMovesWPSinglePush()
    {
        return (bitBoards[EnumPiece.whitePawns.ordinal()] << 8) & BoardConsts.EMPTY_SQUARES;
    }

    private long possibleMovesWPDoublePush()
    {
        return (possibleMovesWPSinglePush() << 8) & BoardConsts.EMPTY_SQUARES & BoardConsts.RANK_4;
    }

    private long possibleMovesBPSinglePush()
    {
        return (bitBoards[EnumPiece.blackPawns.ordinal()] >> 8) & BoardConsts.EMPTY_SQUARES;
    }

    private long possibleMovesBPDoublePush()
    {
        return (possibleMovesBPSinglePush() >> 8) & BoardConsts.EMPTY_SQUARES & BoardConsts.RANK_5;
    }

    private long possibleMovesWPEastCapture()
    {
        return bitBoards[EnumPiece.whitePawns.ordinal()] & (((BoardConsts.BLACK_PIECES | enPassantTargetSquare) & ~BoardConsts.FILE_A) >> 9);
    }

    private long possibleMovesWPWestCapture()
    {
        return bitBoards[EnumPiece.whitePawns.ordinal()] & (((BoardConsts.BLACK_PIECES | enPassantTargetSquare) & ~BoardConsts.FILE_H) >> 7);
    }

    private long possibleMovesBPEastCapture()
    {
        return bitBoards[EnumPiece.blackPawns.ordinal()] & (((BoardConsts.WHITE_PIECES | enPassantTargetSquare) & ~BoardConsts.FILE_H) << 9);
    }

    private long possibleMovesBPWestCapture()
    {
        return bitBoards[EnumPiece.blackPawns.ordinal()] & (((BoardConsts.WHITE_PIECES | enPassantTargetSquare) & ~BoardConsts.FILE_A) << 7);
    }

    long getPieceSet(int pieceSetEnum)
    {
        return bitBoards[pieceSetEnum];
    }

    int getBitIndex(String square)
    {
        int idx = 0;
        idx += 104 - (int) (square.charAt(0));
        idx += (Integer.parseInt(square.substring(1)) - 1) * 8;
        return idx;
    }

    long flippedBitBoard(long bitBoard)
    {
        return Long.reverseBytes(bitBoard);
    }
}
