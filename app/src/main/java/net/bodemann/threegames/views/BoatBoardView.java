package net.bodemann.threegames.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import net.bodemann.threegames.R;
import net.bodemann.threegames.models.BoatBoardModel;

public class BoatBoardView extends FrameLayout {

    public static interface Listener {

        void onTileClicked(CellView view);
    }

    private class CellClickedListener implements OnClickListener {

        @Override
        public void onClick(final View view) {
            if (!(view instanceof CellView)) {
                return;
            }

            if (mListener != null) {
                mListener.onTileClicked((CellView) view);
            }
        }
    }

    private Listener mListener;

    private BoatBoardModel mModel;

    public BoatBoardView(Context context) {
        super(context);
    }

    public BoatBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoatBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBoardModel(BoatBoardModel boardModel) {
        mModel = boardModel;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mModel == null) {
            return;
        }

        final int borderSize = getResources().getDimensionPixelSize(R.dimen.game_border);

        w -= 2 * borderSize;
        h -= 2 * borderSize;

        final float cellWidth = w / mModel.getColumnCount();
        final float cellHeight = h / mModel.getRowCount();

        final float cellSize = Math.min(cellWidth, cellHeight);

        final int rows = mModel.getRowCount();
        final int columns = mModel.getColumnCount();

        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < columns; ++x) {
                final CellView cell = new CellView(getContext());
                cell.setBoardCellPosition(x, y);
                cell.setLayoutParams(new LayoutParams((int) cellSize, (int) cellSize));
                cell.setBackgroundResource(R.drawable.water_undiscovered);

                final float endX = borderSize + x * cellSize;
                final float endY = borderSize + y * cellSize;

                cell.setX(endX);
                cell.setY(endY);

                post(new Runnable() {
                    @Override
                    public void run() {
                        addView(cell);
                        cell.setOnClickListener(new CellClickedListener());
                    }
                });
            }
        }
    }

    public void revealWater(View cell) {
        cell.setBackgroundResource(R.drawable.water);
    }

    public void revealBoat(View cell) {
        cell.setBackgroundResource(R.drawable.water_with_boat);
    }
}
