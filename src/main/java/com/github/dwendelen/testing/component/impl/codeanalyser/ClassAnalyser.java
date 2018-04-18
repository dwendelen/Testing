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
package com.github.dwendelen.testing.component.impl.codeanalyser;

import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassAnalyser extends ClassVisitor {
    private String className;
    private Set<String> dependencies = new HashSet<>();

    private AnnotationAnalyser annotationAnalyser;
    private TypeAnalyser typeAnalyser;
    private FieldAnalyser fieldAnalyser;
    private MethodAnalyser methodAnalyser;

    public ClassAnalyser() {
        super(Opcodes.ASM5);

        this.typeAnalyser = new TypeAnalyser(dependencies);
        this.annotationAnalyser = new AnnotationAnalyser(typeAnalyser);
        this.fieldAnalyser = new FieldAnalyser(annotationAnalyser, typeAnalyser);
        this.methodAnalyser = new MethodAnalyser(dependencies, typeAnalyser, annotationAnalyser);
    }

    public void analyse(InputStream byteCode) throws IOException {
        new ClassReader(byteCode)
                .accept(this, 0);
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;

        if(superName != null) {
            dependencies.add(superName);
        }
        if(interfaces != null) {
            dependencies.addAll(Arrays.asList(interfaces));
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        typeAnalyser.analyse(desc);

        super.visitAnnotation(desc, visible); //TODO CHAINING RESPONSE
        return annotationAnalyser;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        typeAnalyser.analyse(desc);

        super.visitTypeAnnotation(typeRef, typePath, desc, visible); //TODO CHAINING RESPONSE
        return annotationAnalyser;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        typeAnalyser.analyse(desc);

        super.visitField(access, name, desc, signature, value); //TODO CHAINING RESPONSE
        return fieldAnalyser;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        typeAnalyser.analyse(desc);
        if(exceptions != null) {
            dependencies.addAll(Arrays.asList(exceptions));
        }

        super.visitMethod(access, name, desc, signature, exceptions); //TODO CHAINING RESPONSE
        return methodAnalyser;
    }
}
