package com.github.dwendelen.testing.component;

public interface ApiConfigurer {
    ApiConfigurer addPackage(String packageName);
    ApiConfigurer addPackage(Class<?> clazz);
    ApiConfigurer addClass(String className);
    ApiConfigurer addClass(Class<?> clazz);
}
