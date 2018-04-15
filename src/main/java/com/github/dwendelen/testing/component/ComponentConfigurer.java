package com.github.dwendelen.testing.component;

import java.util.function.Consumer;

public interface ComponentConfigurer {
    ComponentConfigurer api(Consumer<ApiConfigurer> configurer);
    ComponentConfigurer subComponent(String name, Consumer<ComponentConfigurer> configurer);

    ComponentConfigurer addPackage(String packageName);
    ComponentConfigurer addPackage(Class<?> clazz);
    ComponentConfigurer addClass(String className);
    ComponentConfigurer addClass(Class<?> clazz);

    ComponentConfigurer dependsOn(String moduleName);
}
