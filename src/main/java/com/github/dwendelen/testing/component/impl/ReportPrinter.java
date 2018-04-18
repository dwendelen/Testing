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
package com.github.dwendelen.testing.component.impl;

import com.github.dwendelen.testing.component.impl.report.Report;
import io.reactivex.Observable;

import java.io.PrintStream;
import java.util.function.Function;
import java.util.logging.Level;


public class ReportPrinter {
    private boolean errorsFound = false;

    private Level unexpectedDependency = Level.SEVERE;
    private Level unusedDependency = Level.WARNING;
    private Level noAccess = Level.SEVERE;

    public boolean wereErrorsFound() {
        return errorsFound;
    }

    public ReportPrinter setUnexpectedDependency(Level level) {
        if (!isValidLevel(level)) {
            throw new IllegalArgumentException("Invalid level");
        }

        this.unexpectedDependency = level;
        return this;
    }

    public ReportPrinter setUnusedDependency(Level level) {
        if (!isValidLevel(level)) {
            throw new IllegalArgumentException("Invalid level");
        }

        this.unusedDependency = level;
        return this;
    }

    public ReportPrinter setNoAccess(Level level) {
        if (!isValidLevel(level)) {
            throw new IllegalArgumentException("Invalid level");
        }

        this.noAccess = level;
        return this;
    }

    private boolean isValidLevel(Level level) {
        return level == Level.OFF || level == Level.WARNING || level == Level.SEVERE;
    }

    public void print(Report report) {
        print(unexpectedDependency, report.getUnexpectedDependencies(), dep ->
                "Unexpected dependency " +
                        "from \"" + dep.getFrom() + "\" " +
                        "to \"" + dep.getTo() + "\": " +
                        "from " + dep.getClassNameFrom() + " " +
                        "to " + dep.getClassNameTo()
        );
        print(unusedDependency, report.getUnusedDependencies(), dep ->
                "Unused dependency " +
                        "from \"" + dep.getFrom() + "\" " +
                        "to \"" + dep.getTo() + "\""
        );

        print(noAccess, report.getNoAccess(), dep ->
                "No access " +
                        "from " + dep.getFrom() + "\" " +
                        "to \"" + dep.getTo() + "\": " +
                        "from " + dep.getClassNameFrom() + " " +
                        "to " + dep.getClassNameTo()
        );
    }

    private <T> void print(Level level, Observable<T> observable, Function<T, String> message) {
        if (level == Level.OFF) {
            return;
        }

        PrintStream printStream = getPrintStream(level);
        String prefix = getPrefix(level);
        observable
                .subscribe(item -> {
                    printStream.println(prefix + message.apply(item));
                    if (level == Level.SEVERE) {
                        errorsFound = true;
                    }
                });

    }

    private PrintStream getPrintStream(Level level) {
        if (level == Level.WARNING) {
            return System.out;
        } else if (level == Level.SEVERE) {
            return System.err;
        } else {
            return null;
        }
    }

    private String getPrefix(Level level) {
        if (level == Level.WARNING) {
            return "WARNING ";
        } else if (level == Level.SEVERE) {
            return "ERROR   ";
        } else {
            return "";
        }
    }
}
