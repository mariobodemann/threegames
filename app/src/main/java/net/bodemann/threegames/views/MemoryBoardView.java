package net.bodemann.threegames.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;

import net.bodemann.threegames.R;
import net.bodemann.threegames.models.BoardModel;
import net.bodemann.threegames.models.CellBoardPosition;
import net.bodemann.threegames.util.DrawableUtils;

public class MemoryBoardView extends FrameLayout {

    public static final long BASE_ANIMATION_DURATION = 300L;
    public static final long BASE_ANIMATION_DURATION_HALF = BASE_ANIMATION_DURATION / 2;
    public static final float RECT_ROUNDNESS = 20.0f;

    public static interface Listener {
        void onGameWon(int moves);

        PointF getFinalCellPosition(CellView cellView);
    }

    private class CellClickedListener implements OnClickListener {
        @Override
        public void onClick(final View view) {
            if ((view == mLastClickedCellView)
                    || (mSelectedCells >= 2)
                    || !(view instanceof CellView)) {
                return;
            }

            final CellView cellView = (CellView) view;

            mSelectedCells++;
            mMoves++;

            final Drawable frontDrawable = createFrontDrawable(cellView);

            bringChildToFront(cellView);

            Runnable revealingCellDoneAction = new Runnable() {
                @Override
                public void run() {
                    if (mLastClickedCellView != null) {
                        onTwoCellsTurned(mLastClickedCellView, cellView);
                    } else {
                        mLastClickedCellView = cellView;
                    }
                }
            };

            rotateCellRevealingBack(view,
                    mBackDrawable,
                    frontDrawable,
                    revealingCellDoneAction);
        }

    }

    private CellView mLastClickedCellView = null;

    private int mSelectedCells;

    private int mMoves;
    private int mMatchedCells;

    private Listener mListener;

    private BoardModel mBoardModel;

    private Drawable mBackDrawable;


    public MemoryBoardView(Context context) {
        super(context);
    }

    public MemoryBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MemoryBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBoardModel(BoardModel boardModel) {
        mBoardModel = boardModel;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBoardModel == null) {
            return;
        }

        final int borderSize = getResources().getDimensionPixelSize(R.dimen.game_border);

        w -= borderSize;
        h -= borderSize;

        final float cellWidth = w / mBoardModel.getColumnCount();
        final float cellHeight = h / mBoardModel.getRowCount();

        final float cellSizeWithSpacing = Math.min(cellWidth, cellHeight);
        final float cellSize = cellSizeWithSpacing - borderSize;

        final int allCellsCombinedWidth = (int) (cellSizeWithSpacing) * mBoardModel.getColumnCount();
        final int xCentering = (w - allCellsCombinedWidth) / 2;

        createBackDrawable((int) cellSize);

        final int rows = mBoardModel.getRowCount();
        final int columns = mBoardModel.getColumnCount();
        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < columns; ++x) {
                final CellView cell = new CellView(getContext());
                cell.setBoardCellPosition(x, y);
                cell.setBackground(mBackDrawable);
                cell.setLayoutParams(new LayoutParams((int) cellSize, (int) cellSize));

                final float startX = getOutsideCoordinate(w);
                final float startY = getOutsideCoordinate(h);
                final float endX = xCentering + borderSize + x * cellSizeWithSpacing;
                final float endY = borderSize + y * cellSizeWithSpacing;

                cell.setX(startX);
                cell.setY(startY);
                cell.setAlpha(0);

                cell.animate()
                        .alpha(1)
                        .x(endX)
                        .y(endY)
                        .setStartDelay((long) (Math.random() * 250))
                        .setDuration(BASE_ANIMATION_DURATION)
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                addView(cell);
                            }
                        })
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                cell.setOnClickListener(new CellClickedListener());
                            }
                        })
                        .start();
            }
        }
    }

    private void rotateCellRevealingBack(final View view, final Drawable frontDrawable, final Drawable backDrawable,
                                         final Runnable endAction) {

        view.setBackground(frontDrawable);
        view.animate()
                .rotationYBy(90)
                .setDuration(BASE_ANIMATION_DURATION_HALF)
                .setListener(null)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackground(backDrawable);
                        ViewPropertyAnimator animation = view.animate();
                        animation.rotationYBy(90)
                                .setDuration(BASE_ANIMATION_DURATION_HALF);

                        if (endAction != null) {
                            animation.withEndAction(endAction);
                        }

                        animation.start();
                    }
                })
                .start();
    }

    private float getOutsideCoordinate(int max) {
        double distance = Math.random();
        double beforeOrAfter = Math.random();
        return (float) (beforeOrAfter > 0.5 ? max + distance * max : -distance * max);
    }

    private void cleanUp() {
        mLastClickedCellView = null;
        mSelectedCells = 0;
    }

    private Drawable createFrontDrawable(CellView cellView) {
        final int width = cellView.getWidth();
        final int height = cellView.getHeight();
        final Resources resources = getResources();
        final Bitmap frontBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(frontBitmap);
        final RectF CellRect = new RectF(0, 0, width, height);
        final Paint rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(0xFFFDF6DB);
        rectPaint.setAntiAlias(true);
        canvas.drawRoundRect(CellRect, RECT_ROUNDNESS, RECT_ROUNDNESS, rectPaint);

        canvas.scale(-1.0f, 1.0f, width / 2.0f, height / 2.0f);

        final Bitmap memoryBitmap = DrawableUtils.getRandomBitmapStartingWith(
                resources,
                String.format("memory_%02d", mBoardModel.getCellValue(cellView.getBoardCellPosition())));

        final float maxCellDimension = Math.max(width, height);
        final int maxBitmapDimension = Math.max(memoryBitmap.getWidth(), memoryBitmap.getHeight());
        final float scale = maxCellDimension / maxBitmapDimension;
        final Rect source = new Rect(0, 0, memoryBitmap.getWidth(), memoryBitmap.getHeight());
        final RectF dest = new RectF(0, 0, memoryBitmap.getWidth() * scale, memoryBitmap.getHeight() * scale);

        if (maxBitmapDimension == memoryBitmap.getWidth()) {
            // height must be centered
            dest.offset(0, 0.5f * Math.abs(height - dest.height()));
        } else {
            // width must be centered
            dest.offset(0.5f * Math.abs(width - dest.width()), 0);
        }

        dest.inset(10, 10);
        canvas.drawBitmap(memoryBitmap, source, dest, null);

        return DrawableUtils.createTileableDrawableFromBitmap(resources, frontBitmap);
    }

    private void createBackDrawable(int cellSize) {
        final Resources resources = getResources();
        final Bitmap flowerBitmap = BitmapFactory.decodeResource(resources, R.drawable.flower_background);
        final Bitmap backgroundBitmap = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888);
        final RectF backgroundRect = new RectF(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
        final Canvas backgroundCanvas = new Canvas(backgroundBitmap);
        final Paint backPaint = new Paint();
        final BitmapShader repeatShader = new BitmapShader(flowerBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        backPaint.setShader(repeatShader);
        backPaint.setAntiAlias(true);
        backgroundCanvas.drawRoundRect(backgroundRect, RECT_ROUNDNESS, RECT_ROUNDNESS, backPaint);

        mBackDrawable = new BitmapDrawable(resources, backgroundBitmap);
    }

    private void onTwoCellsTurned(CellView firstCell, CellView secondCell) {
        final CellBoardPosition firstPosition = firstCell.getBoardCellPosition();
        final int firstValue = mBoardModel.getCellValue(firstPosition);

        final CellBoardPosition secondPosition = secondCell.getBoardCellPosition();
        final int secondValue = mBoardModel.getCellValue(secondPosition);

        // match found?
        if (firstValue == secondValue) {
            final float outWidth;
            final float outHeight;

            if (mListener == null) {
                outWidth = getOutsideCoordinate(MemoryBoardView.this.getWidth());
                outHeight = getOutsideCoordinate(MemoryBoardView.this.getHeight());
            } else {
                PointF point = mListener.getFinalCellPosition(firstCell);
                outWidth = point.x;
                outHeight = point.y;
            }

            firstCell.animate()
                    .x(outWidth)
                    .y(outHeight)
                    .rotation((float) (Math.random() * 90))
                    .setDuration(BASE_ANIMATION_DURATION)
                    .start();
            secondCell.animate()
                    .x(outWidth)
                    .y(outHeight)
                    .rotation((float) (Math.random() * 90))
                    .setDuration(BASE_ANIMATION_DURATION)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            cleanUp();
                            if (mListener != null) {
                                mMatchedCells++;

                                if (mMatchedCells * 2 == mBoardModel.getCellCount()) {
                                    mListener.onGameWon(mMoves);
                                }
                            }
                        }
                    }).start();
        } else {
            // no
            final Drawable firstFront = createFrontDrawable(firstCell);
            rotateCellRevealingBack(firstCell, firstFront, mBackDrawable, null);

            final Drawable secondFront = createFrontDrawable(secondCell);
            rotateCellRevealingBack(secondCell, secondFront, mBackDrawable, new Runnable() {
                @Override
                public void run() {
                    cleanUp();
                }
            });
        }
    }

}
