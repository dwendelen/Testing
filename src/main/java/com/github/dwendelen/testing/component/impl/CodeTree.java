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

import com.github.dwendelen.testing.component.impl.component.AbstractCodeContainer;

import java.util.HashMap;
import java.util.List;

public class CodeTree {
    private AbstractCodeContainer codeContainer;
    private HashMap<String, CodeTree> children = new HashMap<>();

    public void setCodeContainer(List<String> codePath, AbstractCodeContainer newModule) {
        if (codePath.isEmpty()) {
            if (codeContainer != null) {
                throw new IllegalStateException("Multiple containers found with the same code prefix: " + codeContainer + ", " + newModule);
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
