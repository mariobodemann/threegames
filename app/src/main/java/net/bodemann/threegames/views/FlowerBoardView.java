package net.bodemann.threegames.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.bodemann.threegames.R;
import net.bodemann.threegames.models.CellBoardPosition;

import static net.bodemann.threegames.util.DrawableUtils.getRandomBitmapStartingWith;

public class FlowerBoardView extends FrameLayout {

    public static interface CellListener {
        public void onCellClicked(CellView celLView);

        public int getCellValue(CellBoardPosition position);

        public int getCellRowCount();

        public int getCellColumnCount();
    }

    private CellListener mListener;

    public FlowerBoardView(Context context) {
        super(context);
    }

    public FlowerBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowerBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setListener(CellListener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        update(w, h, true);
    }

    public void repaint() {
        update(getWidth(), getHeight(), false);
    }

    private void update(int width, int height, boolean recreate) {
        if (mListener == null) {
            return;
        }

        final int borderSize = getResources().getDimensionPixelSize(R.dimen.game_border);

        width -= 2 * borderSize;
        height -= 2 * borderSize;

        final int rows = mListener.getCellRowCount();
        final int columns = mListener.getCellColumnCount();

        final float cellWidth = width / rows;
        final float cellHeight = height / columns;

        final float cellSize = Math.min(cellWidth, cellHeight);

        final int allCellsCombinedWidth = (int) (cellSize) * columns;
        final int xCentering = (width - allCellsCombinedWidth) / 2;

        final int allCellsCombinedHeight = (int) (cellSize) * rows;
        final int yCentering = (height - allCellsCombinedHeight) / 2;

        int cellIndex = 0;
        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < columns; ++x, ++cellIndex) {
                final CellView cell;
                if (recreate) {
                    cell = new CellView(getContext());
                    cell.setBoardCellPosition(x, y);
                    cell.setLayoutParams(new LayoutParams((int) cellSize, (int) cellSize));
                    cell.setX(xCentering + borderSize + x * cellSize);
                    cell.setY(yCentering + borderSize + y * cellSize);

                    post(new Runnable() {
                        @Override
                        public void run() {
                            addView(cell);
                        }
                    });
                } else {
                    cell = (CellView) getChildAt(cellIndex);
                }
                cell.setBackground(createCellDrawable(cell));
            }
        }
    }

    private Drawable createCellDrawable(CellView cell) {
        final Resources resources = getResources();
        final int cellValue = mListener.getCellValue(cell.getBoardCellPosition());
        final String tilePrefix = String.format("tile_%02d", cellValue);
        final Bitmap tileBitmap = getRandomBitmapStartingWith(resources, tilePrefix);
        return new BitmapDrawable(resources, tileBitmap);
    }

}
