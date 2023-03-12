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
    private Integer[] clickIndicatorLocation = {null, null};
    private Integer[] previousClickIndicatorLocation = {null, null};
    Board chessBoard;
    BufferedImage boardImage = ImageGetter.tryGetImage("/img/board.png", getClass());
    boolean flipped;

    ChessBoardPanel(Board board)
    {
        chessBoard = board;
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int extraTopMargin = (getHeight() - boardSize)/2;
                int extraSideMargin = (getWidth() - boardSize)/2;
                int columnBoard = mouseEvent.getX() - extraSideMargin;
                int rowBoard = mouseEvent.getY() - extraTopMargin;

                int columnIdx = columnBoard / 64;
                int rowIdx = rowBoard / 64;

                if ((columnIdx >= 0 && columnIdx < 8) && (rowIdx >= 0 && rowIdx < 8))
                {
                    clickIndicatorLocation[0] = rowIdx;
                    clickIndicatorLocation[1] = columnIdx;
                }
                else
                {
                    clickIndicatorLocation[0] = null;
                    clickIndicatorLocation[1] = null;
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                Integer[] newClickIndicatorLocation = new Integer[] {null, null};

                int extraTopMargin = (getHeight() - boardSize)/2;
                int extraSideMargin = (getWidth() - boardSize)/2;
                int columnBoard = mouseEvent.getX() - extraSideMargin;
                int rowBoard = mouseEvent.getY() - extraTopMargin;

                int columnIdx = columnBoard / 64;
                int rowIdx = rowBoard / 64;

                if ((columnIdx >= 0 && columnIdx < 8) && (rowIdx >= 0 && rowIdx < 8))
                {
                    newClickIndicatorLocation[0] = rowIdx;
                    newClickIndicatorLocation[1] = columnIdx;
                }
                if (Arrays.equals(clickIndicatorLocation, newClickIndicatorLocation))
                {
                    if (Objects.equals(clickIndicatorLocation[0], previousClickIndicatorLocation[0]) && Objects.equals(clickIndicatorLocation[1], previousClickIndicatorLocation[1])) clickIndicatorLocation = new Integer[] {null, null};

                    paintComponent(getGraphics());
                    previousClickIndicatorLocation = clickIndicatorLocation.clone();
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
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
            if (flipped) currBitBoard = chessBoard.flippedBitBoard(currBitBoard);
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

        if (clickIndicatorLocation[0] != null && clickIndicatorLocation[1] != null)
        {
            // Draw click indicator
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.BLUE);
            g2.drawArc(
                    marginSize + extraSideMargin + 24 + (clickIndicatorLocation[1] * 64),
                    marginSize + extraTopMargin + 24 + (clickIndicatorLocation[0] * 64),
                    16,
                    16,
                    0,
                    360
            );

            // Draw move indicator(s)
            int currSquare;
            if (flipped) currSquare = (clickIndicatorLocation[0]) * 8 + (7 - clickIndicatorLocation[1]);
            else currSquare = (7 - clickIndicatorLocation[0]) * 8 + (7 - clickIndicatorLocation[1]);
            List<int[]> moves = chessBoard.possibleMoves();
            moves.removeIf(move -> move[0] != currSquare);
            g2.setColor(Color.RED);
            if (moves.size() > 0) for (int[] move: moves)
            {
                if (flipped) move[1] = (7 - (move[1] / 8)) * 8 + move[1] % 8;
                g2.drawArc(
                        marginSize + extraSideMargin + 24 + ((7 - (move[1] % 8)) * 64),
                        marginSize + extraTopMargin + 24 + ((7 - (move[1] / 8)) * 64),
                        16,
                        16,
                        0,
                        360
                );
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

