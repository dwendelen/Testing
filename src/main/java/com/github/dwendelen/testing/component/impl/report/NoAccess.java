package com.github.dwendelen.testing.component.impl.report;

import com.github.dwendelen.testing.component.impl.component.Component;

public class NoAccess {
    private Component from;
    private Component to;
    private String className;

    public NoAccess(Component from, Component to, String className) {
        this.from = from;
        this.to = to;
        this.className = className;
    }

    public Component getFrom() {
        return from;
    }

    public Component getTo() {
        return to;
    }

    public String getClassName() {
        return className;
    }
}
