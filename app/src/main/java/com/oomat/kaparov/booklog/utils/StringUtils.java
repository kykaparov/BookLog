package com.oomat.kaparov.booklog.utils;

import java.util.List;

public class StringUtils {

    private StringUtils() {
    }

    public static String stringFromList(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String item : list) {
            builder.append(item).append(" ");
        }
        return builder.toString();
    }
}
