import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ChessBoardPanel extends JPanel
{
    private final int boardSize = 512;
    private final int marginSize = 16;
    Board chessBoard;
    BufferedImage boardImage = ImageGetter.tryGetImage("/img/board.png", getClass());

    ChessBoardPanel(Board board)
    {
        chessBoard = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw board.
        g.setColor(Color.DARK_GRAY);

        int extraTopMargin = (getHeight() - boardSize - (marginSize * 2))/2;
        int extraSideMargin = (getWidth() - boardSize - (marginSize * 2))/2;
        extraTopMargin = Math.max(extraTopMargin, 0);
        extraSideMargin = Math.max(extraSideMargin, 0);

        g.drawImage(boardImage, marginSize + extraSideMargin, marginSize + extraTopMargin, null);

        // Draw pieces.
        for (EnumPiece piece: EnumPiece.values())
        {
            long currBitBoard = chessBoard.bitBoards[piece.ordinal()];
            for (int square = 63; square >= 0; square--)
            {
                int row = square / 8;
                int column = square % 8;
                if ((currBitBoard & 1) == 1)
                {
                    drawPiece(g, piece, extraSideMargin, extraTopMargin, row, column);
                }
                currBitBoard >>= 1;
            }
        }
    }

    private void drawPiece(Graphics g, EnumPiece piece, int extraSideMargin, int extraTopMargin, int row, int column)
    {
        g.drawImage(
                PieceImages.getImage(piece),
                marginSize + extraSideMargin + (column * 64) + 1,
                marginSize + extraTopMargin + (row * 64) + 1,
                null
        );
    }

}

