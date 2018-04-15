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
