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
package com.github.dwendelen.testing.propertiesfile;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.core.env.MapPropertySource;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Do not use with @RunWith(...)
 */
public class PropertySourceRunner extends BlockJUnit4ClassRunner {
    private List<MapPropertySource> propertySources;
    private String propertySourceName;

    public PropertySourceRunner(Class<?> klass, List<MapPropertySource> propertySources, String propertySourceName) throws InitializationError {
        super(klass);
        this.propertySources = propertySources;
        this.propertySourceName = propertySourceName;
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> testAnnotatedMethods = getTestClass().getAnnotatedMethods(Test.class);
        List<FrameworkMethod> testMethods = new ArrayList<>(testAnnotatedMethods);

        if(!testMethods.isEmpty()) {
            System.err.println("WARNING: tests found that are annotated with @Test, please change them to @PropertyTest");
        }

        List<FrameworkMethod> propertyTestAnnotated = getTestClass().getAnnotatedMethods(PropertyTest.class);
        for (FrameworkMethod frameworkMethod : propertyTestAnnotated) {
            PropertyTest annotation = frameworkMethod.getAnnotation(PropertyTest.class);

            if(annotation.runOnlyFor().length != 0 && annotation.doNotRunFor().length != 0) {
                System.err.println("WARNING: both runOnlyFor and doNotRunFor are set. The behaviour is undefined for this configuration");
            }

            if(annotation.runOnlyFor().length != 0) {
                //White list mode
                if (Arrays.asList(annotation.runOnlyFor()).contains(propertySourceName)) {
                    testMethods.add(frameworkMethod);
                }
            } else {
                //Black list mode
                if(!Arrays.asList(annotation.doNotRunFor()).contains(propertySourceName)) {
                    testMethods.add(frameworkMethod);
                }
            }
        }

        return testMethods;
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        List<FrameworkMethod> testMethods = getTestClass().getAnnotatedMethods(Test.class);
        List<FrameworkMethod> propertyTestMethods = getTestClass().getAnnotatedMethods(PropertyTest.class);

        List<FrameworkMethod> methods = new ArrayList<>();
        methods.addAll(testMethods);
        methods.addAll(propertyTestMethods);

        for (FrameworkMethod eachTestMethod : methods) {
            eachTestMethod.validatePublicVoid(false, errors);
        }
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        Description normalDescription = super.describeChild(method);

        return Description.createTestDescription(
                normalDescription.getTestClass(),
                propertySourceName + " " + normalDescription.getDisplayName()
        );
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
        Annotation[][] annotations = method.getMethod().getParameterAnnotations();
        final Object[] parameterInstances = new Object[annotations.length];

        for (int i = 0; i < annotations.length; i++) {
            Property annotation = getProperty(annotations[i]);
            if(annotation == null) {
                return new FailStatement("Not all parameters are annotated with @Property");
            }

            String key = annotation.value();
            boolean found = false;
            for (MapPropertySource propertySource : propertySources) {
                if (propertySource.containsProperty(key)) {
                    parameterInstances[i] = propertySource.getProperty(key);
                    found = true;
                    break; //This break is important to avoid overwrite from backup properties file
                }
            }

            if (!found) {
                return new FailStatement("Key " + key + " not found");
            }
        }

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                method.invokeExplosively(test, parameterInstances);
            }
        };
    }

    private Property getProperty(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if(annotation instanceof Property) {
                return (Property) annotation;
            }
        }

        return null;
    }

    private static class FailStatement extends Statement {
        private String reason;

        private FailStatement(String reason) {
            this.reason = reason;
        }

        @Override
        public void evaluate() throws Throwable {
            throw new AssertionError(reason);
        }
    }
}
