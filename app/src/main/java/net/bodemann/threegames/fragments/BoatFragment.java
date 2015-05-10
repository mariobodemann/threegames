package net.bodemann.threegames.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.bodemann.threegames.R;
import net.bodemann.threegames.models.BoatBoardModel;
import net.bodemann.threegames.models.Medal;
import net.bodemann.threegames.views.BoatBoardView;
import net.bodemann.threegames.views.CellView;

import static net.bodemann.threegames.constants.ConstantPreferences.Keys.BOATS_LEVEL_KEY;
import static net.bodemann.threegames.constants.ConstantPreferences.NAME;

public class BoatFragment extends GameFragment {

    private static final int[] BOAT_COUNTS = new int[]{
            15,
            10,
            7,
            5,
            2,
    };

    private int mMoves;
    private int mBoatsHit;

    private BoatBoardView mBoardView;
    private BoatBoardModel mBoardModel;
    private TextView mBoatsLeftText;

    final BoatBoardView.Listener listener = new BoatBoardView.Listener() {

        @Override
        public void onTileClicked(CellView cell) {
            mMoves++;

            if (cell.isEnabled()) {
                final int value = mBoardModel.getCellValue(cell.getBoardCellPosition());
                if (value == 0) {
                    mBoardView.revealWater(cell);
                } else {
                    // Boat
                    mBoardView.revealBoat(cell);
                    mBoatsHit++;
                    updateBoatAmountText();
                    cell.setEnabled(false);
                }
            } else {
                Toast.makeText(getActivity(), "Already pressed", Toast.LENGTH_LONG).show();
            }

            checkForWin();
        }

    };

    private void updateBoatAmountText() {
        mBoatsLeftText.setText((mBoardModel.getBoatCount() - mBoatsHit) + "x");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_boat, container, false);

        final SharedPreferences preferences = getActivity().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        final int level = preferences.getInt(BOATS_LEVEL_KEY.toString(), 0);

        int count = 0;
        if (level >= 0 && level < BOAT_COUNTS.length) {
            count = BOAT_COUNTS[level];
        }

        if (count == 0) {
            count = BOAT_COUNTS[BOAT_COUNTS.length - 1];
        }

        mBoardView = (BoatBoardView) view.findViewById(R.id.boat_board);
        mBoardModel = new BoatBoardModel(5, 5, count);
        mBoardView.setBoardModel(mBoardModel);
        mBoardView.setListener(listener);

        mBoatsLeftText = (TextView) view.findViewById(R.id.boats_left_text);
        updateBoatAmountText();

        return view;
    }

    @Override
    public String getBackStackHint() {
        return BoatFragment.class.getCanonicalName();
    }

    private void checkForWin() {
        if (mListener != null && mBoatsHit == mBoardModel.getBoatCount()) {
            int tileCount = mBoardModel.getCellCount();

            final Medal medal;
            if (mMoves <= mBoardModel.getBoatCount() * 2) {
                medal = Medal.GOLD;
            } else if (mMoves <= mBoardModel.getBoatCount() * 5) {
                medal = Medal.SILVER;
            } else {
                medal = Medal.BRONZE;
            }

            mListener.onGameWon(mMoves, tileCount, medal);
        }
    }

}
