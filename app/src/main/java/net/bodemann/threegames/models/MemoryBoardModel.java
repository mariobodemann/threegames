package net.bodemann.threegames.models;

import java.util.ArrayList;
import java.util.List;

import static net.bodemann.threegames.util.DrawableUtils.getDrawableNamesStartingWith;

public class MemoryBoardModel extends BoardModel {

    public MemoryBoardModel(int columns, int rows) {
        super(columns, rows);
    }

    @Override
    protected void createCellStructure() {
        mCells = new int[mRows * mColumns];
    }

    @Override
    protected void fillCellStructure() {
        final int cellCount = getCellCount();
        List<Integer> pairs = new ArrayList<Integer>(cellCount / 2);
        List<String> drawableNames = getDrawableNamesStartingWith("memory_");
        for (int i = 0; i < cellCount / 2; ++i) {
            int nextIndex = (int) (Math.random() * drawableNames.size());
            final String drawableName = drawableNames.get(nextIndex);
            drawableNames.remove(drawableName);

            final String id = drawableName.substring(1 + drawableName.lastIndexOf("_"));
            pairs.add(Integer.parseInt(id));
        }

        List<Integer> freeCells = new ArrayList<Integer>(cellCount);
        for (int i = 0; i < cellCount; ++i) {
            freeCells.add(i);
        }

        for (int i = 0; i < pairs.size(); ++i) {
            Integer firstFree = freeCells.get((int) (Math.random() * freeCells.size()));
            freeCells.remove(firstFree);

            Integer secondFree = freeCells.get((int) (Math.random() * freeCells.size()));
            freeCells.remove(secondFree);

            mCells[firstFree] = pairs.get(i);
            mCells[secondFree] = pairs.get(i);
        }
    }
}
