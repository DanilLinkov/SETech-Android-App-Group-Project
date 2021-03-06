package com.project.setech.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class providing useful general static methods that are used throughout the codebase
 * and don't necessarily need a separate class.
 */
public class Util {

    /**
     * Takes in a list of image strings and maps them to their respective integer ids in the drawable
     * resource package
     */
    public static List<Integer> formatDrawableStringList(List<String> images, Context context) {
        List<Integer> formatedImages= new ArrayList<>();

        for (String i : images) {
            formatedImages.add(context.getResources().getIdentifier(i,"drawable",context.getPackageName()));
        }

        return formatedImages;
    }

    /**
     * Turns a camel case string into a normal string
     * e.g. testCamel = Test camel
     * Used to easily turn specification map ids for items into
     * meaningful/better formatted specification row name
     */
    public static String splitCamelCase(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1);

        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }
}
