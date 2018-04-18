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

import com.github.dwendelen.testing.component.impl.codeanalyser.ClassAnalyser;
import com.github.dwendelen.testing.component.impl.component.AbstractCodeContainer;
import com.github.dwendelen.testing.component.impl.component.ActualDependency;
import com.github.dwendelen.testing.component.impl.component.Component;
import com.github.dwendelen.testing.component.impl.report.NoAccess;
import com.github.dwendelen.testing.component.impl.report.Report;
import com.github.dwendelen.testing.component.impl.report.UnexpectedDependency;
import com.github.dwendelen.testing.component.impl.report.UnusedDepedency;
import com.github.dwendelen.testing.component.impl.scanner.DirectoryScanner;
import com.github.dwendelen.testing.component.impl.scanner.JarScanner;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.observables.ConnectableObservable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Analyser {
    private ComponentRegistry componentRegistry = new ComponentRegistry();
    private CodeTree codeTree = new CodeTree();

    private Set<String> directoriesToScan = new HashSet<>();
    private Set<String> jarsToScan = new HashSet<>();

    private DirectoryScanner directoryScanner = new DirectoryScanner();
    private JarScanner jarScanner = new JarScanner();

    public Analyser() {
    }

    public CodeTree getCodeTree() {
        return codeTree;
    }

    public void addScanDirectory(String directory) {
        directoriesToScan.add(directory);
    }

    public void addScanJar(String jar) {
        jarsToScan.add(jar);
    }

    public Report analyse() {
        Observable<InputStream> byteCodes = scanCode();

        ConnectableObservable<ActualDependency> actualDependencies =
                byteCodes
                        .flatMap(this::parseDependenciesFromByteCode)
                        .publish();

        Set<Report> allReports = componentRegistry.getAllComponents()
                .stream()
                .map(component -> {
                    Observable<ActualDependency> actualDependencyOfModule =
                            actualDependencies.filter(a -> a.getFrom() == component);
                    return component.analyse(actualDependencyOfModule);
                }).collect(Collectors.toSet());

        Set<Observable<NoAccess>> noAccess = allReports.stream()
                .map(Report::getNoAccess)
                .collect(Collectors.toSet());

        Set<Observable<UnexpectedDependency>> unexpectedDependencies = allReports.stream()
                .map(Report::getUnexpectedDependencies)
                .collect(Collectors.toSet());

        Set<Observable<UnusedDepedency>> unusedDependencies = allReports.stream()
                .map(Report::getUnusedDependencies)
                .collect(Collectors.toSet());

        return new Report(
                Observable.merge(unexpectedDependencies),
                Observable.merge(unusedDependencies),
                Observable.merge(noAccess),
                actualDependencies::connect
        );
    }

    private ObservableSource<ActualDependency> parseDependenciesFromByteCode(InputStream inputStream) throws IOException {
        ClassAnalyser classAnalyser = new ClassAnalyser();
        classAnalyser.analyse(inputStream);

        AbstractCodeContainer codeContainer = getCodeContainer(classAnalyser.getClassName());
        Component from = codeContainer.getComponent();

        return Observable.fromIterable(classAnalyser.getDependencies())
                .map(toInternalClass -> {
                    AbstractCodeContainer to = getCodeContainer(toInternalClass);
                    String toFullyQualified = Utils.internalNameToFullyQualifiedName(toInternalClass);

                    return new ActualDependency(from, to, toFullyQualified);
                })
                .filter(a -> a.getFrom() != null && a.getTo() != null && a.getFrom() != a.getTo().getComponent());
    }

    private Observable<InputStream> scanCode() {
        List<Observable<InputStream>> streams = new ArrayList<>();
        for (String directory : directoriesToScan) {
            streams.add(directoryScanner.scan(directory));
        }
        for (String jar : jarsToScan) {
            streams.add(jarScanner.scan(jar));
        }

        return Observable.merge(streams);
    }

    private AbstractCodeContainer getCodeContainer(String internalName) {
        List<String> codePath = Utils.internalNameToCodePath(internalName);
        return codeTree.getCodeContainer(codePath, null);
    }

    public ComponentRegistry getComponentRegistry() {
        return componentRegistry;
    }
}
