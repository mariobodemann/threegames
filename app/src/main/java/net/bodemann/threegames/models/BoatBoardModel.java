package net.bodemann.threegames.models;

public class BoatBoardModel extends BoardModel {

    private static final int BOAT = 1;

    private final int mBoats;

    public BoatBoardModel(int columns, int rows, int boats) {
        super(columns, rows);
        mBoats = boats;
        fillCellStructure();
    }

    @Override
    protected void createCellStructure() {
        final int cellCount = mRows * mColumns;

        mCells = new int[cellCount];
    }

    @Override
    protected void fillCellStructure() {
        for (int boat = 0; boat < mBoats; ++boat) {
            int index;
            final int count = getCellCount();
            int tries = count;
            do {
                index = (int) (Math.random() * count);
            } while (mCells[index] != 0 && tries-- > 0);

            if (tries > 0) {
                mCells[index] = BOAT;
            }
        }
    }

    public int getBoatCount() {
        return mBoats;
    }
}
