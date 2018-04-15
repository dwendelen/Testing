package com.github.dwendelen.testing.component.impl;

import java.util.Arrays;
import java.util.List;

public class Utils {
    public static List<String> internalNameToCodePath(String internalName) {
        int dollar = internalName.indexOf("$");
        if(dollar != -1) {
            internalName = internalName.substring(0, dollar);
        }

        String[] pieces = internalName.split("/");
        return Arrays.asList(pieces);
    }

    public static String internalNameToFullyQualifiedName(String internalName) {
        return internalName.replace("/", ".");
    }

    public static <T> T head(List<T> list) {
        return list.get(0);
    }

    public static <T> List<T> tail(List<T> list) {
        return list.subList(1, list.size());
    }
}
