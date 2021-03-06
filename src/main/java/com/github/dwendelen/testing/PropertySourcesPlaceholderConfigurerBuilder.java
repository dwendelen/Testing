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
package com.github.dwendelen.testing;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertySourcesPlaceholderConfigurerBuilder {
    private Map<String, String> properties = new HashMap<>();

    public static PropertySourcesPlaceholderConfigurerBuilder newPlaceholders() {
        return new PropertySourcesPlaceholderConfigurerBuilder();
    }

    public PropertySourcesPlaceholderConfigurerBuilder withProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public PropertySourcesPlaceholderConfigurer build() {
        Properties properties = new Properties();
        properties.putAll(this.properties);

        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setProperties(properties);
        return propertySourcesPlaceholderConfigurer;
    }
}