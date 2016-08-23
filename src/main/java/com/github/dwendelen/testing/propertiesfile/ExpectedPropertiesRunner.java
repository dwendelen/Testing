package com.github.dwendelen.testing.propertiesfile;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.core.io.support.ResourcePropertySource;

import java.util.Arrays;
import java.util.List;

public class ExpectedPropertiesRunner extends ParentRunner<String> {
    private List<ResourcePropertySource> propertySources;
    private String propertySourceName;
    private String[] expectedAnnotations;


    public ExpectedPropertiesRunner(Class<?> klass, List<ResourcePropertySource> propertySources, String propertySourceName, String[] expectedAnnotations) throws InitializationError {
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
        for (ResourcePropertySource propertySource : propertySources) {
            if (propertySource.containsProperty(key)) {
                notifier.fireTestFinished(description);
                return;
            }
        }

        notifier.fireTestFailure(new Failure(description, new AssertionError(key + " could not be found")));
        notifier.fireTestFinished(description);
    }
}