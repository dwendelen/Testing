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
package com.github.dwendelen.testing.component.example;

import com.github.dwendelen.testing.*;
import com.github.dwendelen.testing.component.ApiConfigurer;
import com.github.dwendelen.testing.component.ComponentConfigurer;
import com.github.dwendelen.testing.component.ComponentAnalyser;

public class Example {
    private static final String ASYNC = "Async";
    private static final String WAIT_FOR = "WaitFor";
    private static final String PROPERTY_SOURCE_PLACEHOLDER_CONFIGURER_BUILDER = "Property Source Placeholder Configurer Builder";
    private static final String INJECT = "Inject";
    private static final String COMPONENT = "Component";
    private static final String PROPERTIES_FILE = "Properties File";

    public static void main(String[] args) {
        ComponentAnalyser.newComponentAnalyser()
                .scanning(Example.class)
                .withRootComponent("Testing", root -> root
                        .addPackage("com.github.dwendelen.testing")
                        .subComponent(COMPONENT, comp -> comp
                                .addPackage("com.github.dwendelen.testing.component")
                                .api(api -> api
                                        .addClass(ApiConfigurer.class)
                                        .addClass(ComponentConfigurer.class)
                                        .addClass(ComponentAnalyser.class)
                                )
                                .subComponent("Example", example -> example
                                        .addClass(Example.class)
                                        .dependsOn(COMPONENT)
                                        .dependsOn(ASYNC)
                                        .dependsOn(WAIT_FOR)
                                        .dependsOn(PROPERTY_SOURCE_PLACEHOLDER_CONFIGURER_BUILDER)
                                )
                        )
                        .subComponent(INJECT, inject -> inject
                                .api(api -> api
                                        .addPackage("com.github.dwendelen.testing.inject")
                                )
                        )
                        .subComponent(PROPERTIES_FILE, properties -> properties
                                .addPackage("com.github.dwendelen.testing.propertiesfile")
                        )
                        .subComponent(ASYNC, async -> async
                                .api(api -> api
                                        .addClass(Async.class)
                                        .addClass(ControllableExecutor.class)
                                )
                        )
                        .subComponent(PROPERTY_SOURCE_PLACEHOLDER_CONFIGURER_BUILDER, properties -> properties
                                .api(api -> api
                                        .addClass(PropertySourcesPlaceholderConfigurerBuilder.class)
                                )
                        )
                        .subComponent(WAIT_FOR, waitFor -> waitFor
                                .api(api -> api
                                        .addClass(WaitFor.class)
                                        .addClass(AssertBlock.class)
                                )
                        )
                )
                .onError(() -> System.exit(1))
                .analyse();
    }
}
