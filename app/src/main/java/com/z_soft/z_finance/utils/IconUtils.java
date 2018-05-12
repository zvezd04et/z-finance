package com.z_soft.z_finance.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.z_soft.z_finance.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IconUtils {

    private static final String TAG="IconUtils";
    private static final int ICONS_COUNT = 182;

    public static Map<String, Drawable> iconsMap = new HashMap<>();
    public static List<String> iconsList = new ArrayList<>();

    // заполняем карту иконок
    static void fillIcons(Context context) {

        for (int i = 1; i <= ICONS_COUNT; i++) {

            try {

                String name="icon" + i;

                Class res = R.drawable.class;
                Field field = res.getField(name);
                int resId = field.getInt(null);

                iconsMap.put(name, ResourcesCompat.getDrawable(context.getResources(), resId, null));
                iconsList.add(name);


            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }


}