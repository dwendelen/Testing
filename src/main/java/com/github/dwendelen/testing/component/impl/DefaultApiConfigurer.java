package com.github.dwendelen.testing.component.impl;

import com.github.dwendelen.testing.component.ApiConfigurer;
import com.github.dwendelen.testing.component.impl.component.Api;

public class DefaultApiConfigurer extends AbstractConfigurer implements ApiConfigurer {
    private Api api;

    public DefaultApiConfigurer(Api api, CodeTree codeTree) {
        super(codeTree);
        this.api = api;
    }

    @Override
    public ApiConfigurer addPackage(String packageName) {
        addToTree(packageName, api);
        return this;
    }

    @Override
    public ApiConfigurer addPackage(Class<?> clazz) {
        return addPackage(clazz.getPackage().getName());
    }

    @Override
    public ApiConfigurer addClass(String className) {
        addToTree(className, api);
        return this;
    }

    @Override
    public ApiConfigurer addClass(Class<?> clazz) {
        return addClass(clazz.getName());
    }
}
