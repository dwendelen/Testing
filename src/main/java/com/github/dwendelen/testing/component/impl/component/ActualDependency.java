package com.github.dwendelen.testing.component.impl.component;

public class ActualDependency {
    private Component from;
    private AbstractCodeContainer to;
    private String className;

    public ActualDependency(Component from, AbstractCodeContainer to, String className) {
        this.from = from;
        this.to = to;
        this.className = className;
    }

    public Component getFrom() {
        return from;
    }

    public AbstractCodeContainer getTo() {
        return to;
    }

    public String getClassName() {
        return className;
    }
}
