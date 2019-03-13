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
package com.github.dwendelen.testing.propertiesfile;

import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PropertiesFileTestRunner extends Suite {
    private final List<Runner> runners;

    public PropertiesFileTestRunner(final Class<?> klass) throws InitializationError {
        super(klass, new ArrayList<Runner>());
        final PropertiesFileTest annotation = klass.getAnnotation(PropertiesFileTest.class);
        if (annotation == null) {
            throw new InitializationError("No @PropertyFileTest");
        }

        List<MapPropertySource> sourcesToMerge = getMergeSources(annotation.mergeWith());

        runners = new ArrayList<>();
        for (final Resource resource : getPropertySources(annotation.filePattern(), annotation.excludeFilesStartingWith())) {
            final List<MapPropertySource> propertySources = mapToSource(resource);
            propertySources.addAll(sourcesToMerge); //It is important that this comes last
            runners.add(new PropertySourceRunner(klass, propertySources, resource.getFilename()));
            runners.add(new ExpectedPropertiesRunner(klass, propertySources, resource.getFilename(), annotation.expectedProperties()));
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    private List<Resource> getPropertySources(String pattern, String[] excludes) {
        try {
            PathMatchingResourcePatternResolver resourceProvider = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourceProvider.getResources(pattern);
            return filterExcludedResources(resources, excludes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Resource> filterExcludedResources(Resource[] resources, String[] excludes) {
        List<Resource> filteredList = new ArrayList<>();
        for (Resource resource : resources) {
            if(fileNameIsExcluded(resource.getFilename(), excludes)) {
                continue;
            }

            filteredList.add(resource);
        }
        return filteredList;
    }

    private boolean fileNameIsExcluded(String filename, String[] excludes) {
        for (String exclude : excludes) {
            if(filename.startsWith(exclude)) {
                return true;
            }
        }

        return false;
    }

    private List<MapPropertySource> mapToSource(Resource resource) {
        List<MapPropertySource> propertySources = new ArrayList<>();

        if(resource.getFilename().endsWith(".yml") || resource.getFilename().endsWith(".yaml")) {
            YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
            factoryBean.setResources(resource);
            factoryBean.afterPropertiesSet();

            PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource(resource.getFilename(), factoryBean.getObject());
            propertySources.add(propertiesPropertySource);

        } else {
            try {
                ResourcePropertySource resourcePropertySource = new ResourcePropertySource(resource);

                propertySources.add(resourcePropertySource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return propertySources;
    }

    private List<MapPropertySource> getMergeSources(String stringPath) {
        if(StringUtils.isEmpty(stringPath)) {
            return new ArrayList<>();
        }

        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(stringPath);
        return mapToSource(resource);
    }
}
