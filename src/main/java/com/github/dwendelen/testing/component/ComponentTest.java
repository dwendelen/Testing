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

public class ComponentTest {
    private Analyser analyser = new Analyser();
    private ReportPrinter reportPrinter = new ReportPrinter();
    private Runnable onError = () -> {};

    public static ComponentTest newComponentTest() {
        return new ComponentTest();
    }

    public ComponentTest scanning(Class<?> clazz) {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        return scanning(location);
    }

    public ComponentTest scanning(URL url) {
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

    public ComponentTest scanning(String directoryOrJar) {
        if (directoryOrJar.endsWith(".jar")) {
            analyser.addScanJar(directoryOrJar);
        } else {
            analyser.addScanDirectory(directoryOrJar);
        }

        return this;
    }

    public ComponentTest withRootComponent(String name, Function<ComponentConfigurer, ComponentConfigurer> configurer) {
        Component rootModule = new Component(null, name);
        Implementation implementation = new Implementation(rootModule);
        Api api = new Api(rootModule);

        DefaultApiConfigurer apiConfigurer = new DefaultApiConfigurer(api, analyser.getCodeTree());
        DefaultComponentConfigurer componentConfigurer = new DefaultComponentConfigurer(apiConfigurer, implementation, rootModule, analyser.getComponentRegistry(), analyser.getCodeTree());

        configurer.apply(componentConfigurer);
        componentConfigurer.getDependencyInitialiser().run();

        return this;
    }

    public ComponentTest onError(Runnable onError) {
        this.onError = onError;

        return this;
    }

    public void validate() {
        Report report = analyser.analyse();
        reportPrinter.print(report);

        report.generateReport();

        if(reportPrinter.wereErrorsFound()) {
            onError.run();
        }
    }
}
