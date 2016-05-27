package com.github.dwendelen.testing;

import static org.junit.Assert.fail;

public class WaitFor {
    public  static void waitFor(AssertBlock theAssert) throws Exception {
        waitFor(30, theAssert);
    }
    public static void waitFor(int seconds, AssertBlock theAssert) throws Exception {
        waitFor(seconds, 0.5, theAssert);
    }

    public static void waitFor(int seconds, double stepInSeconds, AssertBlock theAssert) throws Exception {
        int TIMEOUT_IN_MS = seconds * 1000;
        int STEP_IN_MS = (int) (stepInSeconds * 1000);

        int waiting = 0;
        while (true) {
            try {
                theAssert.doAssert();
                break;
            } catch (AssertionError e) {
                if (waiting >= TIMEOUT_IN_MS) {
                    throw e;
                }
            }
            waiting += STEP_IN_MS;

            try {
                Thread.sleep(STEP_IN_MS);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
    }
}
