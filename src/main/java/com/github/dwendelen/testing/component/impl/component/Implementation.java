package com.github.dwendelen.testing.component.impl.component;

public class Implementation extends AbstractCodeContainer {
    public Implementation(Component owner) {
        super(owner);
    }

    @Override
    protected boolean isAccessibleFrom(Component module) {
            return module.isPartOf(owner);
    }

    @Override
    public String toString() {
        return "Implementation of " + owner;
    }
}
