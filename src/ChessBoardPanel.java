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
    BufferedImage boardImage = ImageGetter.tryGetImage("/img/board.png", getClass());

    ChessBoardPanel(Board board)
    {

    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw board
        g.setColor(Color.DARK_GRAY);

        int extraTopMargin = (getHeight() - boardSize - (marginSize * 2))/2;
        int extraSideMargin = (getWidth() - boardSize - (marginSize * 2))/2;
        extraTopMargin = Math.max(extraTopMargin, 0);
        extraSideMargin = Math.max(extraSideMargin, 0);

        g.drawImage(boardImage, marginSize + extraSideMargin, marginSize + extraTopMargin, null);
    }
}

