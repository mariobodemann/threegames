package net.bodemann.threegames.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;

import net.bodemann.threegames.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DrawableUtils {
    public static List<String> getDrawableNamesStartingWith(String prefix) {
        List<String> ids = new ArrayList<String>();
        for (final Field field : R.drawable.class.getFields()) {
            if (field.getName().startsWith(prefix)) {
                ids.add(field.getName());
            }
        }

        return ids;
    }

    public static Bitmap getRandomBitmapStartingWith(Resources resources, String prefix) {
        List<Integer> ids = new ArrayList<Integer>();
        for (final Field field : R.drawable.class.getFields()) {
            if (field.getName().startsWith(prefix)) {
                try {
                    ids.add((Integer) field.get(R.drawable.class));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        final int index = (int) (Math.random() * ids.size());
        if (ids.size() > index) {
            final int resourceId = ids.get(index);
            return BitmapFactory.decodeResource(resources, resourceId);
        } else {
            Log.d(DrawableUtils.class.getCanonicalName(), "Could not find index index");
            return BitmapFactory.decodeResource(resources, android.R.drawable.stat_notify_error);
        }
    }

    public static Drawable createTileableDrawableFromBitmap(Resources resources, Bitmap bitmap) {
        final BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        return drawable;
    }

    public static Point getDisplaySize(Activity activity) {
        final Display display = activity.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static double distanceTo(Point a, Point b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

}
