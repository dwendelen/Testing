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

import com.github.dwendelen.testing.component.impl.component.Component;
import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

public class ComponentRegistry {
    private TreeMap<String, Component> componentsByName = new TreeMap<>();

    public Component getComponentByName(String name) {
        if(!componentsByName.containsKey(name)) {
            throw new IllegalArgumentException("Unknown component: " + name);
        }
        return componentsByName.get(name);
    }

    public void addComponent(Component component) {
        String name = component.getName();

        if(componentsByName.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate component name: " + name);
        }

        componentsByName.put(name, component);
    }

    public Collection<Component> getAllComponents() {
        return new ArrayList<>(componentsByName.values());
    }
}
