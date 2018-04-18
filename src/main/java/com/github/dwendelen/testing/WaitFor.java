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
