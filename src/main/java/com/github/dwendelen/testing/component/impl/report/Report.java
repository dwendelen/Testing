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
