package com.github.dwendelen.testing.component.impl.component;

import com.github.dwendelen.testing.component.impl.report.*;
import io.reactivex.Observable;

import java.util.*;

public class Component {
    private static Comparator<Component> COMPONENT_COMPARATOR = Comparator.comparing(Component::getName);

    private String name;
    private Component parent;

    private TreeSet<Component> expectedDependencies = new TreeSet<>(COMPONENT_COMPARATOR);

    public Component(Component parent, String name) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public Component getParent() {
        return parent;
    }

    public void addExpectedDependency(Component component) {
        expectedDependencies.add(component);
    }

    public Report analyse(Observable<ActualDependency> actualDependencies) {
        Observable<UnexpectedDependency> unexpectedDependencies = actualDependencies
                .filter(d -> !expectedDependencies.contains(d.getTo().getComponent()))
                .map(d -> new UnexpectedDependency(this, d.getTo().getComponent(), d.getClassName()));

        Observable<UnusedDepedency> unusedDependencies = actualDependencies
                .reduce(new TreeSet<>(expectedDependencies), (unused, dep) -> {
                    unused.remove(dep.getTo().getComponent());
                    return unused;
                })
                .toObservable()
                .flatMap(Observable::fromIterable)
                .map(dep -> new UnusedDepedency(this, dep));

        Observable<NoAccess> noAccess = actualDependencies
                .filter(dep -> !dep.getTo().isAccessibleFrom(this))
                .map(dep -> new NoAccess(this, dep.getTo().getComponent(), dep.getClassName()));

        return new Report(unexpectedDependencies, unusedDependencies, noAccess, null);
    }

    public boolean isPartOf(Component module) {
        if (module == this) {
            return true;
        }

        if(parent == null) {
            return false;
        }

        return parent.isPartOf(module);
    }

    @Override
    public String toString() {
        return getName();
    }
}
