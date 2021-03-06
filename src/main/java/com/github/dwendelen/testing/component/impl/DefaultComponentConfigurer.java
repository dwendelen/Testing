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

import com.github.dwendelen.testing.component.ApiConfigurer;
import com.github.dwendelen.testing.component.ComponentConfigurer;
import com.github.dwendelen.testing.component.impl.component.Api;
import com.github.dwendelen.testing.component.impl.component.Component;
import com.github.dwendelen.testing.component.impl.component.Implementation;

import java.util.function.Consumer;

public class DefaultComponentConfigurer extends AbstractConfigurer implements ComponentConfigurer {
    private ApiConfigurer apiConfigurer;
    private Implementation implementation;
    private Component component;
    private ComponentRegistry componentRegistry;
    private Runnable dependencyInitialiser = () -> {};

    public DefaultComponentConfigurer(ApiConfigurer apiConfigurer, Implementation implementation, Component component, ComponentRegistry componentRegistry, CodeTree codeTree) {
        super(codeTree);
        this.apiConfigurer = apiConfigurer;
        this.implementation = implementation;
        this.component = component;
        this.componentRegistry = componentRegistry;
    }

    public Runnable getDependencyInitialiser() {
        return dependencyInitialiser;
    }

    @Override
    public DefaultComponentConfigurer api(Consumer<ApiConfigurer> configurer) {
        configurer.accept(apiConfigurer);
        return this;
    }

    @Override
    public DefaultComponentConfigurer subComponent(String name, Consumer<ComponentConfigurer> configurer) {
        Component newComponent = new Component(component, name);
        Implementation implementation = new Implementation(newComponent);
        Api api = new Api(newComponent);

        DefaultApiConfigurer apiConfigurer = new DefaultApiConfigurer(api, codeTree);
        DefaultComponentConfigurer componentConfigurer = new DefaultComponentConfigurer(apiConfigurer, implementation, newComponent, componentRegistry, codeTree);

        componentRegistry.addComponent(newComponent);

        configurer.accept(componentConfigurer);

        final Runnable oldInitialiser = dependencyInitialiser;

        dependencyInitialiser = () -> {
            oldInitialiser.run();
            componentConfigurer.dependencyInitialiser.run();
        };

        return this;
    }

    @Override
    public DefaultComponentConfigurer dependsOn(String componentName) {
        final Runnable oldInitialiser = dependencyInitialiser;

        dependencyInitialiser = () -> {
            oldInitialiser.run();
            Component component = componentRegistry.getComponentByName(componentName);
            this.component.addExpectedDependency(component);
        };

        return this;
    }

    @Override
    public ComponentConfigurer addPackage(String packageName) {
        addToTree(packageName, implementation);
        return this;
    }

    @Override
    public ComponentConfigurer addPackage(Class<?> clazz) {
        return addPackage(clazz.getPackage().getName());
    }

    @Override
    public ComponentConfigurer addClass(String className) {
        addToTree(className, implementation);
        return this;
    }

    @Override
    public ComponentConfigurer addClass(Class<?> clazz) {
        return addClass(clazz.getName());
    }
}
