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
