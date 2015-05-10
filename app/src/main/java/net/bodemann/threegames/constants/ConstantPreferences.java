package net.bodemann.threegames.constants;

public class ConstantPreferences {
    public static final String NAME = "game";

    public static enum Keys {
        MEMORY_LEVEL_KEY("MEMORY_LEVEL_KEY"),
        FLOWER_LEVEL_KEY("FLOWER_LEVEL_KEY"),
        BOATS_LEVEL_KEY("BOATS_LEVEL_KEY");

        final String mName;
        Keys(String name) {
            mName = name;
        }

        public String toString() {
            return mName;
        }
    }
}
