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
