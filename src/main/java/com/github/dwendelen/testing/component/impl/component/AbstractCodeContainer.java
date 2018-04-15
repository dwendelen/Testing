package com.github.dwendelen.testing.component.impl.component;

public abstract class AbstractCodeContainer {
    protected Component owner;

    public AbstractCodeContainer(Component owner) {
        this.owner = owner;
    }

    public Component getComponent() {
        return owner;
    }

    protected abstract boolean isAccessibleFrom(Component module);
}
