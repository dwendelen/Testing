package com.github.dwendelen.testing.component.impl.component;

public class Api extends AbstractCodeContainer {

    public Api(Component owner) {
        super(owner);
    }

    @Override
    protected boolean isAccessibleFrom(Component module) {
        Component grandParent = owner.getParent();
        if(grandParent == null) {
            grandParent = owner;
        }

        return module.isPartOf(grandParent);
    }

    @Override
    public String toString() {
        return "API of " + owner;
    }
}
