import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class PieceImages {
    private static final Map<EnumPiece, BufferedImage> images = new HashMap<>()

    {{
        put(EnumPiece.whitePawns, ImageGetter.tryGetImage("/img/white_P.png", getClass()));
        put(EnumPiece.whiteKnights, ImageGetter.tryGetImage("/img/white_N.png", getClass()));
        put(EnumPiece.whiteBishops, ImageGetter.tryGetImage("/img/white_B.png", getClass()));
        put(EnumPiece.whiteRooks, ImageGetter.tryGetImage("/img/white_R.png", getClass()));
        put(EnumPiece.whiteQueens, ImageGetter.tryGetImage("/img/white_Q.png", getClass()));
        put(EnumPiece.whiteKing, ImageGetter.tryGetImage("/img/white_K.png", getClass()));
        put(EnumPiece.blackPawns, ImageGetter.tryGetImage("/img/black_P.png", getClass()));
        put(EnumPiece.blackKnights, ImageGetter.tryGetImage("/img/black_N.png", getClass()));
        put(EnumPiece.blackBishops, ImageGetter.tryGetImage("/img/black_B.png", getClass()));
        put(EnumPiece.blackRooks, ImageGetter.tryGetImage("/img/black_R.png", getClass()));
        put(EnumPiece.blackQueens, ImageGetter.tryGetImage("/img/black_Q.png", getClass()));
        put(EnumPiece.blackKing, ImageGetter.tryGetImage("/img/black_K.png", getClass()));
    }};
    public static BufferedImage getImage(EnumPiece piece)
    {
        return images.get(piece);
    }
}
