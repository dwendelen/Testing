/*
 * Copyright 2018 Daan Wendelen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
