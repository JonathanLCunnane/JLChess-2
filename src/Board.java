import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Board
{
    long[] bitBoards = new long[12];
    int enPassantBit;
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
        if (!Objects.equals(data[3], "-")) enPassantBit = getBitIndex(data[3]);
        else enPassantBit = -1;

        // Get half move clock number.
        halfMoveClock = Integer.parseInt(data[4]);

        // Get full move number.
        fullMoveNumber = Integer.parseInt(data[5]);
    }

    List<int[]> possibleMoves(boolean flipped)
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
        if (isWhitesMove) return possibleMovesW(flipped);
        return possibleMovesB(flipped);
    }

    private List<int[]> possibleMovesW(boolean flipped) // Returns all possible moves with format {from, to}
    {
        List<int[]> moves = new ArrayList<>();
        moves.addAll(possibleMovesWP(flipped));
        // moves.addAll(possibleMovesWN(flipped));
        // moves.addAll(possibleMovesWB(flipped));
        // moves.addAll(possibleMovesWR(flipped));
        // moves.addAll(possibleMovesWQ(flipped));
        // moves.addAll(possibleMovesWK(flipped));
        return moves;
    }

    private List<int[]> possibleMovesB(boolean flipped)
    {
        List<int[]> moves = new ArrayList<>();
        moves.addAll(possibleMovesBP(flipped));
        // moves.addAll(possibleMovesBN(flipped));
        // moves.addAll(possibleMovesBB(flipped));
        // moves.addAll(possibleMovesBR(flipped));
        // moves.addAll(possibleMovesBQ(flipped));
        // moves.addAll(possibleMovesBK(flipped));
        return moves;
    }

    private List<int[]> possibleMovesWP(boolean flipped)
    {
        long mvs;
        List<int[]> moves = new ArrayList<>();

        mvs = possibleMovesWPSinglePush();
        if (flipped) {
            mvs = flippedBitBoard(mvs);
            for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr + 8, sqr});
        }
        for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr - 8, sqr});

        mvs = possibleMovesWPDoublePush();
        if (flipped) {
            mvs = flippedBitBoard(mvs);
            for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr + 16, sqr});
        }
        for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr - 16, sqr});
        return moves;
    }

    private List<int[]> possibleMovesBP(boolean flipped)
    {
        long mvs;
        List<int[]> moves = new ArrayList<>();

        mvs = possibleMovesBPSinglePush();
        if (flipped)
        {
            mvs = flippedBitBoard(mvs);
            for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr - 8, sqr});
        }
        else for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr + 8, sqr});

        mvs = possibleMovesBPDoublePush();
        if (flipped) {
            mvs = flippedBitBoard(mvs);
            for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr - 16, sqr});
        }
        else for (int sqr = 0; sqr < 64; sqr++) if (((mvs >> sqr) & 1) == 1) moves.add(new int[] {sqr + 16, sqr});
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

    long getPieceSet(int pieceSetEnum)
    {
        return bitBoards[pieceSetEnum];
    }

    int getBitIndex(String square)
    {
        int idx = 0;
        idx += (int) (square.charAt(0)) - 97;
        idx += (square.charAt(1) - 1) * 8;
        return idx;
    }

    long flippedBitBoard(long bitBoard)
    {
        return Long.reverseBytes(bitBoard);
    }
}
