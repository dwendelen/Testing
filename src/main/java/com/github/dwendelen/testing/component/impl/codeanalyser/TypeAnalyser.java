package com.github.dwendelen.testing.component.impl.codeanalyser;

import org.objectweb.asm.Type;

import java.util.Set;

public class TypeAnalyser {
    private Set<String> dependencies;

    public TypeAnalyser(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    public void analyse(String desc) {
        Type type = Type.getType(desc);
        analyse(type);
    }

    public void analyse(Type type) {
        switch (type.getSort()) {
            case Type.ARRAY:
                analyse(type.getElementType());
                break;
            case Type.OBJECT:
                dependencies.add(type.getInternalName());
                break;
            case Type.METHOD:
                analyse(type.getReturnType());
                for (Type argumentType : type.getArgumentTypes()) {
                    analyse(argumentType);
                }
        }
    }
}
