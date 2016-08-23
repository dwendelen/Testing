package com.github.dwendelen.testing.propertiesfile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertiesFileTest {
    String EXCLUDE_FILES_STARTING_WITH_DEFAULT_VALUE = "";
    String NO_MERGE = "";

    String filePattern();

    String excludeFilesStartingWith() default EXCLUDE_FILES_STARTING_WITH_DEFAULT_VALUE;
    String mergeWith() default NO_MERGE;
    String[] expectedProperties() default {};
}
