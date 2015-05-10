package net.bodemann.threegames.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bodemann.threegames.R;
import net.bodemann.threegames.models.CellBoardPosition;
import net.bodemann.threegames.models.FlowerBoardModel;
import net.bodemann.threegames.models.Medal;
import net.bodemann.threegames.views.CellView;
import net.bodemann.threegames.views.FlowerBoardView;

import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static net.bodemann.threegames.constants.ConstantPreferences.Keys.FLOWER_LEVEL_KEY;
import static net.bodemann.threegames.constants.ConstantPreferences.NAME;

public class FlowerFragment extends GameFragment {

    final private FlowerBoardView.CellListener listener = new FlowerBoardView.CellListener() {
        @Override
        public void onCellClicked(CellView view) {

        }

        @Override
        public int getCellValue(CellBoardPosition boardPosition) {
            final int cellColumnCount = getCellColumnCount();
            final int cellRowCount = getCellRowCount();

            final int midColumn = cellColumnCount / 2;
            final int midRow = cellRowCount / 2;

            if (boardPosition.x == midColumn && boardPosition.y == midRow) {
                return mBoardModel.getPlayerCell();
            } else {
                int x = boardPosition.x - midColumn;
                int y = boardPosition.y - midRow;

                final int playerX = mPlayerPosition.x;
                final int playerY = mPlayerPosition.y;

                x = playerX + x;
                y = playerY + y;

                return mBoardModel.getCellValue(new CellBoardPosition(x, y));
            }
        }

        @Override
        public int getCellColumnCount() {
            return 5;
        }

        @Override
        public int getCellRowCount() {
            return 5;
        }
    };

    final private View.OnTouchListener mBoardTouched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_DOWN || mGameOver) {
                return false;
            }

            mMoves++;
            final int pointerId = event.getPointerId(0);
            final MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
            event.getPointerCoords(pointerId, coords);

            float x = coords.getAxisValue(MotionEvent.AXIS_X) - v.getWidth() / 2;
            float y = coords.getAxisValue(MotionEvent.AXIS_Y) - v.getHeight() / 2;

            final CellBoardPosition newPosition;
            if (abs(x) > abs(y)) {
                newPosition = new CellBoardPosition(mPlayerPosition.x + (int) signum(x), mPlayerPosition.y);
            } else {
                newPosition = new CellBoardPosition(mPlayerPosition.x, mPlayerPosition.y + (int) signum(y));
            }

            if (mBoardModel.isCellWalkable(newPosition)) {
                mPlayerPosition = newPosition;
                mBoardView.repaint();

                checkForFlower();
                checkForWin();
            }
            return mBoardView.performClick();
        }
    };

    private FlowerBoardModel mBoardModel;
    private FlowerBoardView mBoardView;

    private TextView mFlowerLeftText;

    private CellBoardPosition mPlayerPosition;
    private CellBoardPosition mShopPosition;

    private int mMoves = 0;

    private int mLevel;
    private boolean mGameOver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_flower, container, false);

        final SharedPreferences preferences = getActivity().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        mLevel = preferences.getInt(FLOWER_LEVEL_KEY.toString(), 0);
        mBoardModel = new FlowerBoardModel(mLevel);
        mPlayerPosition = mBoardModel.getInitialPlayerPosition();
        mShopPosition = mBoardModel.getInitialShopPosition();

        mFlowerLeftText = (TextView) view.findViewById(R.id.flowers_left_text);
        updateFlowerCounter();

        final FlowerBoardView board = (FlowerBoardView) view.findViewById(R.id.flower_board);
        board.setListener(listener);
        mBoardView = board;
        mBoardView.setOnTouchListener(mBoardTouched);
        return view;
    }

    @Override
    public String getBackStackHint() {
        return FlowerFragment.class.getCanonicalName();
    }

    private void checkForFlower() {
        if (mListener != null) {
            if (mBoardModel.isFlowerAt(mPlayerPosition)) {
                mBoardModel.pickFlowerAt(mPlayerPosition);
                updateFlowerCounter();
            }
        }
    }

    private void updateFlowerCounter() {
        mFlowerLeftText.setText(mBoardModel.getFlowersLeft() + "x");
    }

    private void checkForWin() {
        if (mListener != null && mPlayerPosition.equals(mShopPosition)) {
            mGameOver = true;

            final int left = mBoardModel.getFlowersLeft();
            final int total = mBoardModel.getInitialFlowerCount();
            final int picked = total - left;

            final Medal medal;
            switch (left) {
                case 0:
                case 1:
                    medal = Medal.GOLD;
                    break;

                case 2:
                case 3:
                    medal = Medal.SILVER;
                    break;

                case 4:
                case 5:
                    medal = Medal.BRONZE;
                    break;

                default:
                    medal = Medal.STONE;
                    break;
            }

            mListener.onGameWon(picked, total, medal);
        }

    }
}
