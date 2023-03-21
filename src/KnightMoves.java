public final class KnightMoves {
    final static long[] rawMoves = getKnightMoves();

    private static long[] getKnightMoves()
    {
        long currBB = 1L;
        long[] moves = new long[64];
        for (int sqr = 0; sqr < 64; sqr++)
        {
            /* Attacks directions are defined by:
                        noNoWe    noNoEa
                            +15  +17
                             |     |
                noWeWe  +6 __|     |__+10  noEaEa
                              \   /
                               >0<
                           __ /   \ __
                soWeWe -10   |     |   -6  soEaEa
                             |     |
                            -17  -15
                        soSoWe    soSoEa
            */
            moves[sqr] = (
                    noNoEa(currBB) |
                    noEaEa(currBB) |
                    soEaEa(currBB) |
                    soSoEa(currBB) |
                    noNoWe(currBB) |
                    noWeWe(currBB) |
                    soWeWe(currBB) |
                    soSoWe(currBB));
            currBB <<= 1;
        }
        return moves;
    }

    static long noNoEa(long bb) {return (bb & ~BoardConsts.FILE_H) << 17;}
    static long noEaEa(long bb) {return (bb & ~BoardConsts.FILE_GH) << 10;}
    static long soEaEa(long bb) {return (bb & ~BoardConsts.FILE_GH) >>  6;}
    static long soSoEa(long bb) {return (bb & ~BoardConsts.FILE_H) >> 15;}
    static long noNoWe(long bb) {return (bb & ~BoardConsts.FILE_A) << 15;}
    static long noWeWe(long bb) {return (bb & ~BoardConsts.FILE_AB) <<  6;}
    static long soWeWe(long bb) {return (bb & ~BoardConsts.FILE_AB) >> 10;}
    static long soSoWe(long bb) {return (bb & ~BoardConsts.FILE_A) >> 17;}


}