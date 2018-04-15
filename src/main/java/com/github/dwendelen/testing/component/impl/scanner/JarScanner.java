package com.github.dwendelen.testing.component.impl.scanner;

import io.reactivex.Observable;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarScanner {
    public Observable<InputStream> scan(String jar) {
        return Observable.create(sub -> {
            JarFile jarFile = new JarFile(jar);

            Enumeration<JarEntry> enumeration = jarFile.entries();
            while(enumeration.hasMoreElements() && !sub.isDisposed()) {
                JarEntry jarEntry = enumeration.nextElement();

                if(jarEntry.isDirectory()) {
                    continue;
                }

                if(!jarEntry.getName().endsWith(".class")) {
                    continue;
                }

                sub.onNext(jarFile.getInputStream(jarEntry));
            }
            sub.onComplete();
        });
    }
}
