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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AnnotationAnalyser extends AnnotationVisitor {
    private TypeAnalyser typeAnalyser;

    public AnnotationAnalyser(TypeAnalyser typeAnalyser) {
        super(Opcodes.ASM5);

        this.typeAnalyser = typeAnalyser;
    }


    @Override
    public void visit(String name, Object value) {
        if (value instanceof Type) {
            Type type = (Type) value;
            typeAnalyser.analyse(type);
        }
        super.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        this.typeAnalyser.analyse(desc);

        super.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        this.typeAnalyser.analyse(desc);

        super.visitAnnotation(name, desc); //TODO CHAINING RESPONSE
        return this;
    }
}
