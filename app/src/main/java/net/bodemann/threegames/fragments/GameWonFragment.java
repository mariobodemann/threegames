package net.bodemann.threegames.fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.bodemann.threegames.R;
import net.bodemann.threegames.models.Medal;

import static net.bodemann.threegames.util.DrawableUtils.distanceTo;
import static net.bodemann.threegames.util.DrawableUtils.getDisplaySize;

public class GameWonFragment extends DialogFragment {
    public static final String FRAGMENT_TAG = GameWonFragment.class.getCanonicalName();

    public static interface Listener {
        void onDone();
    }

    private FragmentManager.OnBackStackChangedListener mOnBackstackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if (stillActive()) {
                terminate();
            }
        }
    };

    private Listener mListener;

    private int mMoveCount;
    private int mTargetMoveCount;

    private Medal mMedal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_game_won, container, false);
        Point size = getDisplaySize(getActivity());
        view.setMinimumWidth(size.x);
        view.setMinimumHeight(size.y);

        getFragmentManager().addOnBackStackChangedListener(mOnBackstackChangedListener);

        final TextView wonText = (TextView) view.findViewById(R.id.won_target_text);
        wonText.setText(getString(R.string.game_won_medal_numbers, mMoveCount, mTargetMoveCount));

        final View medalView = view.findViewById(R.id.won_medal);
        medalView.setBackgroundResource(mMedal.getmResource());

        animateStar(view.findViewById(R.id.won_star_1));
        animateStar(view.findViewById(R.id.won_star_2));
        animateStar(view.findViewById(R.id.won_star_3));
        animateStar(view.findViewById(R.id.won_star_4));
        animateStar(view.findViewById(R.id.won_star_5));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminate();
            }
        });

        return view;
    }

    private void animateStar(final View star) {
        if (mMedal != Medal.GOLD && mMedal != Medal.SILVER) {
            star.setVisibility(View.GONE);
        } else {
            star.setVisibility(View.VISIBLE);
        }

        if (mMedal == Medal.GOLD) {
            star.setBackgroundResource(android.R.drawable.btn_star_big_on);
        } else {
            star.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }

        Point screenSize = getDisplaySize(getActivity());
        final int halfWidth = screenSize.x / 2 - star.getWidth() / 2;
        final int halfHeight = screenSize.y / 2 - star.getHeight() / 2;
        Point startPoint = new Point(halfWidth, halfHeight);

        star.setX(halfWidth);
        star.setY(halfHeight);

        Point endPoint = new Point((int) (screenSize.x * Math.random()),
                (int) (screenSize.y * Math.random()));
        final float distance = (float) distanceTo(startPoint, endPoint);
        final long duration = (long) ((distance / 300) * 1000);

        star.animate()
                .x(endPoint.x)
                .y(endPoint.y)
                .setDuration(duration)
                .setStartDelay((long) (1000 * Math.random()))
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (stillActive()) {
                            animateStar(star);
                        }
                    }
                })
                .start();
    }

    private boolean stillActive() {
        final FragmentManager fragmentManager = getFragmentManager();
        return fragmentManager != null &&
                fragmentManager.findFragmentByTag(FRAGMENT_TAG) == GameWonFragment.this;
    }

    public void setScore(int moveCount, int targetMoveCount, Medal medal) {
        mMoveCount = moveCount;
        mTargetMoveCount = targetMoveCount;
        mMedal = medal;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void terminate() {
        View view = getView();
        if (mListener != null && view != null) {
            view.findViewById(R.id.won_star_1).clearAnimation();
            view.findViewById(R.id.won_star_2).clearAnimation();
            view.findViewById(R.id.won_star_3).clearAnimation();
            view.findViewById(R.id.won_star_4).clearAnimation();
            view.findViewById(R.id.won_star_5).clearAnimation();

            dismiss();
            mListener.onDone();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mListener != null) {
            mListener.onDone();
        }
        terminate();
        super.onDismiss(dialog);
    }
}
