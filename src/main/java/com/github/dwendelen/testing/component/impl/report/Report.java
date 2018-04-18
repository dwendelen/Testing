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
package com.github.dwendelen.testing.component.impl.report;

import io.reactivex.Observable;

public class Report {
    private Observable<UnexpectedDependency> unexpectedDependencies;
    private Observable<UnusedDepedency> unusedDependencies;
    private Observable<NoAccess> noAccess;
    private Runnable connect;

    public Report(
            Observable<UnexpectedDependency> unexpectedDependencies,
            Observable<UnusedDepedency> unusedDependencies,
            Observable<NoAccess> noAccess,
            Runnable connect
    ) {
        this.unexpectedDependencies = unexpectedDependencies;
        this.unusedDependencies = unusedDependencies;
        this.noAccess = noAccess;
        this.connect = connect;
    }

    public Observable<UnexpectedDependency> getUnexpectedDependencies() {
        return unexpectedDependencies;
    }

    public Observable<UnusedDepedency> getUnusedDependencies() {
        return unusedDependencies;
    }

    public Observable<NoAccess> getNoAccess() {
        return noAccess;
    }

    public void generateReport() {
        connect.run();
    }
}
