package net.bodemann.threegames.models;

public class CellBoardPosition {
    public int x, y;

    public CellBoardPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CellBoardPosition) {
            CellBoardPosition other = (CellBoardPosition) o;
            return other.x == this.x && other.y == this.y;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 31;
        hash *= x;
        hash *= y;
        return hash;
    }
}
