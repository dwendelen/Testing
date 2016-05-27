package com.github.dwendelen.testing;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public abstract class VerificationMatcher<T extends Exception> extends BaseMatcher<T> {
    @Override
    public boolean matches(Object item) {
        verify((T) item);
        return true;
    }

    public abstract void verify(T exception);

    @Override
    public void describeTo(Description description) {
    }
}
