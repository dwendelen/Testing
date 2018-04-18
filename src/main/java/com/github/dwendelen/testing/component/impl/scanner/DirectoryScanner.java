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
