package net.bodemann.threegames.models;

import net.bodemann.threegames.R;

public enum Medal {
    GOLD(R.drawable.medal_gold),
    SILVER(R.drawable.medal_silver),
    BRONZE(R.drawable.medal_bronze),
    STONE(R.drawable.medal_stone);

    private final int mResource;

    Medal(int resource) {
        mResource = resource;
    }

    public int getmResource() {
        return mResource;
    }
}
