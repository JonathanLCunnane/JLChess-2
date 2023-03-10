import java.util.Arrays;
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
        halfMoveClock = Integer.parseInt(data[5]);
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

    void flipBoard()
    {
        for (EnumPiece piece: EnumPiece.values()) bitBoards[piece.ordinal()] = Long.reverseBytes(bitBoards[piece.ordinal()]);
    }
}
