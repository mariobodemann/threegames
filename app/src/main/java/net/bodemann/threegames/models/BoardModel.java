package net.bodemann.threegames.models;

public abstract class BoardModel {
    protected int[] mCells;
    protected int mRows;
    protected int mColumns;
    protected int mErrorCellValue = 0;

    public BoardModel(int columns, int rows) {
        mColumns = columns;
        mRows = rows;

        createCellStructure();
        fillCellStructure();
    }

    abstract protected void createCellStructure();

    abstract protected void fillCellStructure();

    public boolean setCellValue(CellBoardPosition position, int value) {
        int x = position.x;
        int y = position.y;

        if (x >= 0 && x < getColumnCount() && y >= 0 && y < getRowCount() ) {
            final int index = x + y * getColumnCount();
            mCells[index] = value;
            return true;
        } else {
            return false;
        }
    }

    public int getCellValue(CellBoardPosition position) {
        int x = position.x;
        int y = position.y;

        if (x >= 0 && x < getColumnCount() && y >= 0 && y < getRowCount() ) {
            final int index = x + y * getColumnCount();
            return mCells[index];
        } else {
            return mErrorCellValue;
        }
    }

    public int getCellCount() {
        return mCells.length;
    }

    public int getColumnCount() {
        return mColumns;
    }

    public int getRowCount() {
        return mRows;
    }
}
