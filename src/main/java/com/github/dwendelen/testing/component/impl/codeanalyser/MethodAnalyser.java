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

import java.util.Set;

public class MethodAnalyser extends MethodVisitor {
    private Set<String> dependencies;
    private TypeAnalyser typeAnalyser;
    private AnnotationAnalyser annotationAnalyser;

    public MethodAnalyser(Set<String> dependencies, TypeAnalyser typeAnalyser, AnnotationAnalyser annotationAnalyser) {
        super(Opcodes.ASM5);
        this.dependencies = dependencies;
        this.typeAnalyser = typeAnalyser;
        this.annotationAnalyser = annotationAnalyser;
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        super.visitAnnotationDefault(); //TODO CHAINING RESPONSE
        return annotationAnalyser;
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
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        typeAnalyser.analyse(desc);

        super.visitParameterAnnotation(parameter, desc, visible); //TODO CHAINING RESPONSE
        return annotationAnalyser;
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        //TODO FIGURE THIS OUT
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        dependencies.add(type);

        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        dependencies.add(owner);
        typeAnalyser.analyse(desc);

        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        dependencies.add(owner);
        typeAnalyser.analyse(desc);

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        typeAnalyser.analyse(desc);

        typeAnalyser.analyse(bsm.getDesc());
        dependencies.add(bsm.getOwner());

        for (Object bsmArg : bsmArgs) {
            if(bsmArg instanceof Handle) {
                Handle handle = (Handle)bsmArg;
                typeAnalyser.analyse(handle.getDesc());
                dependencies.add(handle.getOwner());
            } else if( bsmArg instanceof Type) {
                typeAnalyser.analyse((Type) bsmArg);
            }
        }

        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }


    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public void visitLdcInsn(Object cst) {
        if (cst instanceof Integer) {
        } else if (cst instanceof Float) {
        } else if (cst instanceof Long) {
        } else if (cst instanceof Double) {
        } else if (cst instanceof String) {
        } else if (cst instanceof Handle) {
            Handle handle = (Handle)cst;
            typeAnalyser.analyse(handle.getDesc());
            dependencies.add(handle.getOwner());
        } else if (cst instanceof Type) {
            Type type = (Type) cst;
            typeAnalyser.analyse(type);
        } else {
            throw new UnsupportedOperationException("Unknown type");
        }
        super.visitLdcInsn(cst);
    }


    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        typeAnalyser.analyse(desc);

        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        typeAnalyser.analyse(desc);

        super.visitInsnAnnotation(typeRef, typePath, desc, visible); //TODO CHAINING RESPONSE
        return annotationAnalyser;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        if(type != null) {
            dependencies.add(type);
        }

        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        typeAnalyser.analyse(desc);

        super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);  //TODO CHAINING RESPONSE
        return annotationAnalyser;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        typeAnalyser.analyse(desc);

        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        typeAnalyser.analyse(desc);

        super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);  //TODO CHAINING RESPONSE
        return annotationAnalyser;
    }
}
