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

public class FieldAnalyser extends FieldVisitor {
    private AnnotationAnalyser annotationAnalyser;
    private TypeAnalyser typeAnalyser;

    public FieldAnalyser(AnnotationAnalyser annotationAnalyser, TypeAnalyser typeAnalyser) {
        super(Opcodes.ASM5);
        this.annotationAnalyser = annotationAnalyser;
        this.typeAnalyser = typeAnalyser;
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
}
