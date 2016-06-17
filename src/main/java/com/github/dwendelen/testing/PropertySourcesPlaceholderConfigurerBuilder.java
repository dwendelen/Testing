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