package net.bodemann.threegames.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bodemann.threegames.R;
import net.bodemann.threegames.listener.GameListener;
import net.bodemann.threegames.models.BoardModel;
import net.bodemann.threegames.models.Medal;
import net.bodemann.threegames.models.MemoryBoardModel;
import net.bodemann.threegames.views.CellView;
import net.bodemann.threegames.views.MemoryBoardView;

import static net.bodemann.threegames.constants.ConstantPreferences.Keys.MEMORY_LEVEL_KEY;
import static net.bodemann.threegames.constants.ConstantPreferences.NAME;

public class MemoryFragment extends GameFragment {

    private static final String TAG = MemoryFragment.class.getCanonicalName();

    @SuppressWarnings("UncheckedAssignment")
    private static final Pair<Integer, Integer>[] BOARD_SIZES = new Pair[]{
            Pair.create(1, 2),
            Pair.create(2, 2),
            Pair.create(2, 3),
            Pair.create(3, 4),
            Pair.create(4, 4),
            Pair.create(4, 5),
    };

    private GameListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_memory, container, false);

        final SharedPreferences preferences = getActivity().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        final int level = preferences.getInt(MEMORY_LEVEL_KEY.toString(), 0);

        Pair<Integer, Integer> size = null;
        if (level >= 0 && level < BOARD_SIZES.length) {
            size = BOARD_SIZES[level];
        }

        if (size == null) {
            size = BOARD_SIZES[BOARD_SIZES.length - 1];
        }

        final BoardModel boardModel = new MemoryBoardModel(size.first, size.second);

        final MemoryBoardView board = (MemoryBoardView) view.findViewById(R.id.memory_board);
        board.setBoardModel(boardModel);
        board.setListener(new MemoryBoardView.Listener() {
            @Override
            public void onGameWon(int moves) {
                if (mListener != null) {
                    final int tileCount = boardModel.getCellCount();
                    final Medal medal;

                    if (moves <= tileCount * 2.1) {
                        medal = Medal.GOLD;
                    } else if (moves <= tileCount * 3.0) {
                        medal = Medal.SILVER;
                    } else {
                        medal = Medal.BRONZE;
                    }

                    mListener.onGameWon(moves, tileCount, medal);
                }
            }

            @Override
            public PointF getFinalCellPosition(CellView cell) {
                final Rect rect = new Rect();
                view.getDrawingRect(rect);

                return new PointF(rect.exactCenterX(), rect.bottom + cell.getWidth());
            }
        });
        return view;
    }

    public void setListener(GameListener listener) {
        mListener = listener;
    }

    public String getBackStackHint() {
        return TAG;
    }
}
