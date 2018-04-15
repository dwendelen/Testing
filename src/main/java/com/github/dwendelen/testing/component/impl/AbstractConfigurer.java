package com.github.dwendelen.testing.component.impl;

import com.github.dwendelen.testing.component.impl.component.AbstractCodeContainer;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractConfigurer {
    protected CodeTree codeTree;

    public AbstractConfigurer(CodeTree codeTree) {
        this.codeTree = codeTree;
    }

    protected void addToTree(String codePath, AbstractCodeContainer codeContainer) {
        List<String> packageName = Arrays.asList(codePath.split("\\."));
        codeTree.setCodeContainer(packageName, codeContainer);
    }
}
