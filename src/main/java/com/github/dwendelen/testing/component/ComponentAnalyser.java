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
package com.github.dwendelen.testing.component;

import com.github.dwendelen.testing.component.impl.Analyser;
import com.github.dwendelen.testing.component.impl.DefaultApiConfigurer;
import com.github.dwendelen.testing.component.impl.DefaultComponentConfigurer;
import com.github.dwendelen.testing.component.impl.ReportPrinter;
import com.github.dwendelen.testing.component.impl.component.Api;
import com.github.dwendelen.testing.component.impl.component.Component;
import com.github.dwendelen.testing.component.impl.component.Implementation;
import com.github.dwendelen.testing.component.impl.report.Report;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

public class ComponentAnalyser {
    private Analyser analyser = new Analyser();
    private ReportPrinter reportPrinter = new ReportPrinter();
    private Runnable onError = () -> {};

    public static ComponentAnalyser newComponentAnalyser() {
        return new ComponentAnalyser();
    }

    public ComponentAnalyser scanning(Class<?> clazz) {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        return scanning(location);
    }

    public ComponentAnalyser scanning(URL url) {
        switch (url.getProtocol()) {
            case "file":
                return scanning(url.getPath());
            case "jar":
                String[] urlPieces = url.getPath().split("!");
                try {
                    return scanning(new URL(urlPieces[0]));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(url.toString() + " is unsupported", e);
                }
            default:
                throw new UnsupportedOperationException(url.toString() + " is unsupported");
        }
    }

    public ComponentAnalyser scanning(String directoryOrJar) {
        if (directoryOrJar.endsWith(".jar")) {
            analyser.addScanJar(directoryOrJar);
        } else {
            analyser.addScanDirectory(directoryOrJar);
        }

        return this;
    }

    public ComponentAnalyser withRootComponent(String name, Function<ComponentConfigurer, ComponentConfigurer> configurer) {
        Component rootModule = new Component(null, name);
        Implementation implementation = new Implementation(rootModule);
        Api api = new Api(rootModule);

        DefaultApiConfigurer apiConfigurer = new DefaultApiConfigurer(api, analyser.getCodeTree());
        DefaultComponentConfigurer componentConfigurer = new DefaultComponentConfigurer(apiConfigurer, implementation, rootModule, analyser.getComponentRegistry(), analyser.getCodeTree());

        configurer.apply(componentConfigurer);
        componentConfigurer.getDependencyInitialiser().run();

        return this;
    }

    public ComponentAnalyser onError(Runnable onError) {
        this.onError = onError;

        return this;
    }

    public void analyse() {
        Report report = analyser.analyse();
        reportPrinter.print(report);

        report.generateReport();

        if(reportPrinter.wereErrorsFound()) {
            onError.run();
        }
    }
}
