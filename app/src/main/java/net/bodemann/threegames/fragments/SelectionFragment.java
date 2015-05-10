package net.bodemann.threegames.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import net.bodemann.threegames.R;
import net.bodemann.threegames.util.DrawableUtils;

import static net.bodemann.threegames.util.DrawableUtils.distanceTo;
import static net.bodemann.threegames.util.DrawableUtils.getDisplaySize;

public class SelectionFragment extends Fragment {

    public interface SelectionResultListener {
        void onFlowerGameButtonClicked();

        void onMemoryGameButtonClicked();

        void onBoatGameButtonClicked();

        void onCheatMenuRequested();

        void onInfoClicked();
    }

    private abstract class AnimationRunnable implements Runnable {
        final FragmentManager mFragmentManager;

        public abstract void animate();

        public AnimationRunnable() {
            mFragmentManager = getFragmentManager();
        }

        @Override
        public void run() {
            if (mFragmentManager != null &&
                mFragmentManager.findFragmentById(R.id.main_fragment_container) == SelectionFragment.this) {
                animate();
            }
        }
    }


    private View.OnClickListener mInfoButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onInfoClicked();
            }
        }
    };

    private View.OnClickListener mMemoryGameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onMemoryGameButtonClicked();
            }
            cleanUpAnimations();
        }
    };

    private View.OnLongClickListener mCheatTouchListener = new View.OnLongClickListener() {
        private int longClickCounter = 0;

        @Override
        public boolean onLongClick(View v) {
            longClickCounter++;
            if (mListener != null && longClickCounter >= 3) {
                mListener.onCheatMenuRequested();
                longClickCounter = 0;
            }

            return true;
        }
    };

    private View.OnClickListener mFlowerGameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onFlowerGameButtonClicked();
            }
            cleanUpAnimations();
        }
    };

    private View.OnClickListener mShipGameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onBoatGameButtonClicked();
            }
            cleanUpAnimations();
        }
    };

    private SelectionResultListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();
        final View infoButton = view.findViewById(R.id.fake_action_bar_info);
        if (infoButton != null) {
            infoButton.setOnClickListener(mInfoButtonClicked);
        }

        setupMemoryGameButton(view);
        setupBoatGameButton(view);
        setupFlowerGameButton(view);
    }

    private void setupFlowerGameButton(View view) {
        view.findViewById(R.id.selector_flower_game_button).setOnClickListener(mFlowerGameButtonListener);

        final Point screenSize = getDisplaySize(getActivity());
        final int imageSize = getResources().getDimensionPixelSize(R.dimen.game_image_size);

        final float stageY = screenSize.y / 3 - imageSize;


        final View guy = view.findViewById(R.id.selector_flower_guy_image);
        guy.setX(3.0f * screenSize.x / 4.0f - imageSize / 2.0f);
        guy.setY(stageY);

        final View girl = view.findViewById(R.id.selector_flower_girl_image);
        girl.setX(screenSize.x / 4.0f - imageSize / 2.0f);
        girl.setY(stageY);

        final View flower = view.findViewById(R.id.selector_flower_flower_image);
        flower.setX(screenSize.x / 2.0f - imageSize / 2.0f);
        flower.setY(stageY);

        animateFlowerGameViews(guy, girl, flower);
    }

    private void animateFlowerGameViews(final View guy, final View girl, final View flower) {
        final float initialGirlX = girl.getX();
        final float initialGuyX = guy.getX();
        final float initialFlowerX = flower.getX();

        final AnimationRunnable reset = new AnimationRunnable() {
            @Override
            public void animate() {
                girl.setX(initialGirlX);
                guy.setX(initialGuyX);
                flower.setX(initialFlowerX);
                flower.setScaleX(1);
                flower.setScaleY(1);

                guy.animate().alpha(1).setDuration(1000L).start();
                flower.animate().alpha(1).setDuration(1000L).start();
                girl.animate().alpha(1).setDuration(1000L).withEndAction(new AnimationRunnable() {
                    @Override
                    public void animate() {
                        animateFlowerGameViews(girl, guy, flower);
                    }
                }).start();
            }
        };

        final AnimationRunnable fadeOut = new AnimationRunnable() {
            @Override
            public void animate() {
                girl.animate().alpha(0).setDuration(1000L).start();
                guy.animate().alpha(0).setDuration(1000L).withEndAction(reset).start();
            }
        };

        final AnimationRunnable girlAndGuyJumpDown = new AnimationRunnable() {
            @Override
            public void animate() {
                girl.animate()
                        .yBy(50)
                        .setDuration(500)
                        .start();
                guy.animate()
                        .yBy(50)
                        .setDuration(500)
                        .withEndAction(fadeOut)
                        .start();

            }
        };

        final AnimationRunnable girlAndGuyJumpUp = new AnimationRunnable() {
            @Override
            public void animate() {
                girl.animate()
                        .yBy(-50)
                        .setDuration(500)
                        .start();
                guy.animate()
                        .yBy(-50)
                        .setDuration(500)
                        .withEndAction(girlAndGuyJumpDown)
                        .start();

            }
        };

        final AnimationRunnable girlMoveToGuy = new AnimationRunnable() {
            @Override
            public void animate() {
                final float finalX = guy.getX() - 0.5f * girl.getWidth() * Math.signum(guy.getX() - girl.getX());

                girl.animate()
                        .x(finalX)
                        .setDuration(1000L)
                        .withEndAction(girlAndGuyJumpUp)
                        .start();
            }
        };

        final AnimationRunnable pickFlower = new AnimationRunnable() {
            @Override
            public void animate() {
                flower.animate()
                        .setDuration(500)
                        .yBy(-50)
                        .withEndAction(new AnimationRunnable() {
                            @Override
                            public void animate() {
                                flower.animate()
                                        .setDuration(500)
                                        .yBy(50)
                                        .scaleX(0.1f)
                                        .scaleY(0.1f)
                                        .alpha(0)
                                        .withEndAction(girlMoveToGuy)
                                        .start();
                            }
                        })
                        .start();
            }
        };

        final float finalX = flower.getX() - 0.5f * girl.getWidth() * Math.signum(flower.getX() - girl.getX());
        girl.animate()
                .x(finalX)
                .setDuration(1000L)
                .withEndAction(pickFlower)
                .start();

    }

    private void setupBoatGameButton(View view) {
        view.findViewById(R.id.selector_boat_game_button).setOnClickListener(mShipGameButtonListener);
        final View boat = view.findViewById(R.id.selector_boat_game_image);

        animateBoat(boat);
    }

    private void animateBoat(final View boat) {
        final Point currentPosition = new Point((int) boat.getX(), (int) boat.getY());
        final Point screenSize = getDisplaySize(getActivity());
        final Point targetPosition = new Point(
                (int) (200 + Math.random() * (screenSize.x - 400)),
                (int) (200 + Math.random() * (screenSize.y / 3.0) - 400));

        final float angle = (float) Math.atan2(targetPosition.y - currentPosition.y, targetPosition.x - currentPosition.x);
        rotateBoatGameImage(boat, angle, new AnimationRunnable() {
            @Override
            public void animate() {
                moveBoatGameImage(boat, targetPosition, new AnimationRunnable() {
                    @Override
                    public void animate() {
                        animateBoat(boat);
                    }
                });
            }
        });
    }

    private void rotateBoatGameImage(final View view, final float angle, final AnimationRunnable afterwards) {
        final long duration = (long) ((Math.abs(Math.toDegrees(angle)) / 90.0) * 1000.0);
        view.animate()
                .rotation((float) (120.0f + Math.toDegrees(angle)))
                .setDuration(duration)
                .withEndAction(afterwards)
                .start();

    }

    private void moveBoatGameImage(final View view, final Point endPosition, final AnimationRunnable afterwards) {
        Point startPosition = new Point((int) view.getX(), (int) view.getY());
        final float distance = (float) distanceTo(startPosition, endPosition);
        final long duration = (long) ((distance / 50.0f) * 1000);

        view.animate()
                .x(endPosition.x)
                .y(endPosition.y)
                .setDuration(duration)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(afterwards)
                .start();

    }

    private void setupMemoryGameButton(View view) {
        final View buttonView = view.findViewById(R.id.selector_memory_game_button);
        buttonView.setOnClickListener(mMemoryGameButtonListener);
        buttonView.setOnLongClickListener(mCheatTouchListener);

        animateMemoryGameImage(buttonView, view.findViewById(R.id.selector_memory_game_image_1));
        animateMemoryGameImage(buttonView, view.findViewById(R.id.selector_memory_game_image_2));
        animateMemoryGameImage(buttonView, view.findViewById(R.id.selector_memory_game_image_3));
        animateMemoryGameImage(buttonView, view.findViewById(R.id.selector_memory_game_image_4));
        animateMemoryGameImage(buttonView, view.findViewById(R.id.selector_memory_game_image_5));
    }

    private void animateMemoryGameImage(final View parentView, final View childView) {
        final Point size = getDisplaySize(getActivity());

        final int parentWidth = size.x;
        final int parentHeight = size.y / 3;

        final int childWidth = 165;

        final int startX = -childWidth;
        final int startY = (int) (parentHeight * Math.random());

        final int endX = parentWidth + childWidth;
        final int endY = startY;

        final float degree = ((endX - startX) / (float) childWidth) * 90;

        childView.setX(startX);
        childView.setY(startY);
        childView.animate()
                .xBy(endX - startX)
                .yBy(endY - startY)
                .setInterpolator(new LinearInterpolator())
                .rotationYBy(degree)
                .setDuration(4000L + (long) (2000L * Math.random()))
                .setStartDelay((int) (Math.random() * 4000.0f))
                .withEndAction(new AnimationRunnable() {
                    @Override
                    public void animate() {
                        animateMemoryGameImage(parentView, childView);
                    }
                })
                .start();
    }

    public void setListener(SelectionResultListener listener) {
        mListener = listener;
    }

    private void cleanUpAnimations() {
        cleanUpAnimations(getView());
    }

    private void cleanUpAnimations(View view) {
        if (view != null) {
            view.clearAnimation();

            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                    View child = viewGroup.getChildAt(i);
                    cleanUpAnimations(child);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        cleanUpAnimations();
        super.onDestroy();
    }
}
