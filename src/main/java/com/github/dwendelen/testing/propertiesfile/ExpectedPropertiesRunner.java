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

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.core.env.MapPropertySource;

import java.util.Arrays;
import java.util.List;

public class ExpectedPropertiesRunner extends ParentRunner<String> {
    private List<MapPropertySource> propertySources;
    private String propertySourceName;
    private String[] expectedAnnotations;


    public ExpectedPropertiesRunner(Class<?> klass, List<MapPropertySource> propertySources, String propertySourceName, String[] expectedAnnotations) throws InitializationError {
        super(klass);
        this.propertySources = propertySources;
        this.propertySourceName = propertySourceName;
        this.expectedAnnotations = expectedAnnotations;

    }

    @Override
    protected List<String> getChildren() {
        return Arrays.asList(expectedAnnotations);
    }

    @Override
    protected Description describeChild(String child) {
        Class<?> testClass = super.getTestClass().getJavaClass();
        return Description.createTestDescription(testClass, propertySourceName + " " + child + " exists");
    }

    @Override
    protected void runChild(String key, RunNotifier notifier) {
        final Description description = describeChild(key);
        notifier.fireTestStarted(description);
        for (MapPropertySource propertySource : propertySources) {
            if (propertySource.containsProperty(key)) {
                notifier.fireTestFinished(description);
                return;
            }
        }

        notifier.fireTestFailure(new Failure(description, new AssertionError(key + " could not be found")));
        notifier.fireTestFinished(description);
    }
}