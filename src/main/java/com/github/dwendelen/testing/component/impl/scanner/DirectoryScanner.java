package com.github.dwendelen.testing.component.impl.scanner;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class DirectoryScanner  {
    public Observable<InputStream> scan(String directory) {
        return Observable.create(sub -> {
            Path path = FileSystems.getDefault().getPath(directory);
            Files.walkFileTree(path, new Walker(sub));
            sub.onComplete();
        });
    }

    private static class Walker extends SimpleFileVisitor<Path> {
        private final ObservableEmitter<InputStream> subscriber;

        public Walker(ObservableEmitter<InputStream> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (!file.toString().endsWith(".class")) {
                return FileVisitResult.CONTINUE;
            }

            InputStream byteCode = Files.newInputStream(file);
            subscriber.onNext(byteCode);

            return FileVisitResult.CONTINUE;
        }
    }
}
