package com.github.dwendelen.testing.component.impl;

import com.github.dwendelen.testing.component.impl.component.AbstractCodeContainer;

import java.util.HashMap;
import java.util.List;

public class CodeTree {
    private AbstractCodeContainer codeContainer;
    private HashMap<String, CodeTree> children = new HashMap<>();

    public void setCodeContainer(List<String> codePath, AbstractCodeContainer newModule) {
        if (codePath.isEmpty()) {
            if (codeContainer != null) {
                throw new IllegalStateException("Two containers found with the same code prefix: " + codeContainer + ", " + newModule);
            }
            codeContainer = newModule;
            return;
        }

        String head = Utils.head(codePath);
        List<String> tail = Utils.tail(codePath);

        children.computeIfAbsent(head, k -> new CodeTree())
                .setCodeContainer(tail, newModule);
    }

    public AbstractCodeContainer getCodeContainer(List<String> codePath, AbstractCodeContainer lastVisitedCodeContainer) {
        if(codeContainer != null) {
            lastVisitedCodeContainer = codeContainer;
        }

        if (codePath.isEmpty()) {
            return lastVisitedCodeContainer;
        }

        String head = Utils.head(codePath);
        List<String> tail = Utils.tail(codePath);

        if (!children.containsKey(head)) {
            return lastVisitedCodeContainer;
        }

        return children.get(head)
                .getCodeContainer(tail, lastVisitedCodeContainer);
    }
}
