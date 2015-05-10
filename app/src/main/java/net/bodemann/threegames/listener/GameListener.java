package net.bodemann.threegames.listener;

import net.bodemann.threegames.models.Medal;

public interface GameListener {
    void onGameWon(int doneMoves, int expectedMoves, Medal medal);
}
