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
package com.github.dwendelen.testing.inject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class InjectUtil {
    public static void injectDependencies(Object test) {
        Map<Class, Object> dependencies = new HashMap<>();

        for (Field field : test.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Dependency.class) != null) {
                dependencies.put(field.getType(), get(test, field));
            }
        }

        for (Field field : test.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Tested.class) != null) {
                inject(get(test, field), dependencies);
            }
        }
    }

    private static void inject(Object testedClass, Map<Class, Object> dependencies) {
        for (Field field : testedClass.getClass().getDeclaredFields()) {
            if(get(testedClass, field) != null) {
                continue;
            }

            Object dependency = dependencies.get(field.getType());
            if (dependency == null) {
                continue;
            }

            set(testedClass, field, dependency);
        }
    }

    private static Object get(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Programming mistake", e);
        }
    }

    private static void set(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Programming mistake", e);
        }
    }
}
