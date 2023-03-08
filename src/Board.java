public class Board
{
    long[] bitBoards;
    boolean isWhitesMove;


    Board()
    {
        new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }
    Board(String FENString)
    {

    }

    long getPieceSet (int pieceSetEnum)
    {
        return bitBoards[pieceSetEnum];
    }
}
