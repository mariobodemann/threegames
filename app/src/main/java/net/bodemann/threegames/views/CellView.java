package net.bodemann.threegames.views;

import android.content.Context;
import android.view.View;

import net.bodemann.threegames.models.CellBoardPosition;

public class CellView extends View {

    private CellBoardPosition mPosition;

    public CellView(Context context) {
        super(context);
    }

    public void setBoardCellPosition(int x, int y) {
        setBoardCellPosition(new CellBoardPosition(x, y));
    }

    public void setBoardCellPosition(CellBoardPosition position) {
        mPosition = position;
    }

    public CellBoardPosition getBoardCellPosition() {
        return mPosition;
    }
}
