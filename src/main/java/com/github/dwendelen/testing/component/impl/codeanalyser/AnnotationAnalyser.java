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
