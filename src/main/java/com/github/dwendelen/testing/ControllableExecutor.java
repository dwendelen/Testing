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

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

public class ControllableExecutor implements Executor {
    private Queue<Runnable> queue = new LinkedList<>();

    @Override
    public void execute(Runnable command) {
        queue.add(command);
    }

    public void runNextTask() {
        Runnable runnable = queue.poll();
        if(runnable == null) {
            throw new RuntimeException("No task to run");
        }
        runnable.run();
    }

    public void runAllTask() {
        for (Runnable runnable : queue) {
            runnable.run();
        }
    }

    public int getNbOfPendingTasks() {
        return queue.size();
    }
}
