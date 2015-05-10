package net.bodemann.threegames.fragments;

import android.app.Fragment;

import net.bodemann.threegames.listener.GameListener;

public abstract class GameFragment extends Fragment {

    protected GameListener mListener;

    public void setListener(GameListener listener) {
        mListener = listener;
    }

    public abstract String getBackStackHint();

}
