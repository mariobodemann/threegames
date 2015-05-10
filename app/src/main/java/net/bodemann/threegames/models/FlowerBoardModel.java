package net.bodemann.threegames.models;

public class FlowerBoardModel extends BoardModel {

    private static final int G = 0; // grass
    private static final int M = 1; // map ???
    private static final int F = 2; // flower
    private static final int P = 3; // player start
    private static final int O = 4; // obstacle
    private static final int S = 5; // shop

    private static int[][] LEVELS = new int[][]{
            {
                    // 2  3  4  5  6  7  8  9  0
                    3, 1,
                    P, F, S,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    2, 3,
                    P, G,
                    O, F,
                    O, S,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    5, 3,
                    P, G, O, O, O,
                    O, F, O, O, O,
                    O, G, F, S, O,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    4, 4,
                    P, F, G, F,
                    O, O, F, O,
                    O, G, G, O,
                    F, G, F, S,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    5, 4,
                    P, F, G, G, G,
                    F, O, O, G, G,
                    G, O, F, G, F,
                    G, F, O, G, S,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    5, 5,
                    O, F, G, G, O,
                    G, P, O, G, O,
                    G, O, O, G, G,
                    G, O, F, G, F,
                    G, G, O, G, S,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    10, 6,
                    P, F, G, O, G, G, G, G, G, O,
                    S, O, G, O, O, G, G, O, F, O,
                    G, O, G, O, O, G, G, O, F, O,
                    G, O, G, O, O, G, G, O, F, O,
                    G, O, G, G, O, G, G, O, F, O,
                    G, G, G, G, G, G, G, O, F, F,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    11, 7,
                    G, S, M, O, O, O, O, O, O, O, F,
                    G, O, O, O, F, G, G, F, G, O, G,
                    G, F, O, O, G, O, O, O, G, O, G,
                    O, F, G, O, G, O, P, O, G, O, F,
                    O, O, G, O, F, G, G, O, F, O, G,
                    O, F, G, O, O, O, O, O, G, O, G,
                    G, G, G, G, G, G, G, G, G, G, G,
            },
            {
                    // 2  3  4  5  6  7  8  9  0
                    11, 7,
                    F, G, G, G, F, G, G, G, G, G, G,
                    G, O, G, O, G, O, G, G, G, O, F,
                    G, O, O, O, G, O, O, F, O, O, O,
                    G, O, P, O, F, O, F, O, F, O, M,
                    F, O, G, O, G, O, F, F, F, O, O,
                    G, O, G, O, G, O, F, F, F, O, G,
                    O, G, G, G, F, G, G, F, G, G, S,
            }
    };

    private int mLevel;

    private int mFlowersLeft;
    private int mInitialFlowerCount;

    private CellBoardPosition mInitialPlayerPosition;
    private CellBoardPosition mInitialShopPosition;

    public FlowerBoardModel(int level) {
        super(0, 0);
        final int[] world;
        if (level >= 0 && level < LEVELS.length) {
            mLevel = level;
        } else {
            mLevel = LEVELS.length - 1;
        }
        world = LEVELS[mLevel];

        mColumns = world[0];
        mRows = world[1];
        mErrorCellValue = O;

        createCellStructure();
        fillCellStructure();
    }

    @Override
    protected void createCellStructure() {
        final int cellCount = mRows * mColumns;

        mCells = new int[cellCount];
    }

    @Override
    protected void fillCellStructure() {
        int i = 0;
        for (int y = 0; y < getRowCount(); ++y) {
            for (int x = 0; x < getColumnCount(); ++x, ++i) {
                final CellBoardPosition cellBoardPosition = new CellBoardPosition(x, y);
                int cellValue = LEVELS[mLevel][2 + i];
                switch (cellValue) {
                    case P:
                        mInitialPlayerPosition = cellBoardPosition;
                        cellValue = G;
                        break;
                    case S:
                        mInitialShopPosition = cellBoardPosition;
                        break;
                    case F:
                        mFlowersLeft ++;
                        mInitialFlowerCount ++;
                        break;
                }

                mCells[i] = cellValue;

            }
        }
    }

    public CellBoardPosition getInitialPlayerPosition() {
        return mInitialPlayerPosition;
    }

    public CellBoardPosition getInitialShopPosition() {
        return mInitialShopPosition;
    }

    public int getPlayerCell() {
        return P;
    }

    public boolean isCellWalkable(CellBoardPosition position) {
        final int cellValue = getCellValue(position);
        return cellValue == G
                || cellValue == S
                || cellValue == F
                || cellValue == M
                || cellValue == P;
    }

    public boolean isFlowerAt(CellBoardPosition position) {
        return getCellValue(position) == F;
    }

    public boolean pickFlowerAt(CellBoardPosition position) {
        mFlowersLeft --;
        return setCellValue(position, G);
    }

    public int getFlowersLeft() {
        return mFlowersLeft;
    }

    public int getInitialFlowerCount() {
        return mInitialFlowerCount;
    }
}
