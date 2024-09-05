package com.boozallen.aissemble.upgrade.util;
/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.google.common.base.CaseFormat;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.boozallen.aissemble.upgrade.pojo.AbstractYamlObject;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

public class YamlUtils {
    private static final String SPACE = " ";
    private static final int TAB = 2;
    private static final String VALUE_FOR_KEY_IS_NOT = "Value for [%s] is not %s: %s";
    public static YamlObject loadYaml(File file) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream fileStream = Files.asByteSource(file).openStream()) {
            Map<String, Object> contents = yaml.load(fileStream);
            //If the file is empty or only contains comments, contents will be null
            if (contents == null) {
                contents = Map.of();
            }
            return new YamlObject(contents);
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static class YamlObject {
        private final Map<String, Object> contents;

        public YamlObject(Map<String, Object> contents) {
            this.contents = contents;
        }

        public boolean hasString(String key) {
            return containsKey(key) && get(key) instanceof String;
        }

        public String getString(String key) {
            Object value = get(key);
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(String.format(VALUE_FOR_KEY_IS_NOT, key, "a string", value));
            }
            return (String) value;
        }

        public boolean hasInt(String key) {
            return containsKey(key) && get(key) instanceof Integer;
        }
        
        public int getInt(String key) {
            Object value = get(key);
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException(String.format(VALUE_FOR_KEY_IS_NOT, key, "an integer", value));
            }
            return (int) value;
        }

        public boolean hasDouble(String key) {
            return containsKey(key) && get(key) instanceof Double;
        }

        public double getDouble(String key) {
            Object value = get(key);
            if (!(value instanceof Double)) {
                throw new IllegalArgumentException(String.format(VALUE_FOR_KEY_IS_NOT, key, "a double", value));
            }
            return (double) value;
        }

        public boolean hasBoolean(String key) {
            return containsKey(key) && get(key) instanceof Boolean;
        }

        public boolean getBoolean(String key) {
            Object value = get(key);
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException(String.format(VALUE_FOR_KEY_IS_NOT, key, "a boolean", value));
            }
            return (boolean) value;
        }

        public boolean hasObject(String key) {
            return containsKey(key) && get(key) instanceof Map;
        }

        public YamlObject getObject(String key) {
            Object value = get(key);
            if (!(value instanceof Map)) {
                throw new IllegalArgumentException(String.format(VALUE_FOR_KEY_IS_NOT, key, "a map", value));
            }
            return new YamlObject((Map<String, Object>) value);
        }

        public boolean hasList(String key) {
            return containsKey(key) && get(key) instanceof List;
        }

        public List<?> getList(String key) {
            Object value = get(key);
            if (!(value instanceof List)) {
                throw new IllegalArgumentException(String.format(VALUE_FOR_KEY_IS_NOT, key, "a list", value));
            }
            return (List<Object>) value;
        }

        public List<YamlObject> getListOfObjects(String key) {
            List<?> list = getList(key);
            if (list.stream().anyMatch(o -> !(o instanceof Map))) {
                throw new IllegalArgumentException("List contains non-map elements: " + list);
            }
            return list.stream()
                    .map(o -> (Map<String, Object>) o)
                    .map(YamlObject::new)
                    .collect(Collectors.toList());
        }

        public List<String> getListOfStrings(String key) {
            List<?> list = getList(key);
            if (list.stream().anyMatch(o -> !(o instanceof String))) {
                throw new IllegalArgumentException("List contains non-string elements: " + list);
            }
            return (List<String>) list;
        }

        @Nonnull
        public Object get(String key) {
            if(!containsKey(key)) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            return contents.get(key);
        }

        public boolean containsKey(String key) {
            return contents.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return contents.containsValue(value);
        }

        public boolean isEmpty() {
            return contents.isEmpty();
        }

        public int size() {
            return contents.size();
        }

        public Set<String> keySet() {
            return contents.keySet();
        }

        public Collection<Object> values() {
            return contents.values();
        }

        public Set<Map.Entry<String, Object>> entrySet() {
            return contents.entrySet();
        }
    }

    /**
     * Reads in a yaml file and returns the implClass instance of it
     * @param <T> implementation of {@link AbstractYamlObject}
     * @param file yaml file to read
     * @param clazz Java object to read the yaml into
     * @param logger
     * @return implClass instance of file
     */
    public static <T extends AbstractYamlObject> T createYamlObjectFromFile(File file, Class<T> clazz, Logger logger) {
        // Add custom functionality to our constructor to convert dashed attributes (i.e. aissemble-mlflow) 
        // to camel case (i.e. aissembleMlflow) for java serialization
        Representer representer = new Representer(new DumperOptions());
        representer.getPropertyUtils().setSkipMissingProperties(true);
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(clazz, loaderOptions);
        constructor.setPropertyUtils(new PropertyUtils() {
            @Override
            public Property getProperty(Class<? extends Object> type, String name) {
                if (name.contains("-")) {
                    name = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, name);
                }
                return super.getProperty(type, name);
            }
            });
        constructor.getPropertyUtils().setSkipMissingProperties(true);
        Yaml helmYaml  = new Yaml(constructor, representer);

        // Read in the yaml file and convert to its respective Java object
        T helmYamlObject = null;
        try {
            InputStream fileStream = Files.asByteSource(file).openStream();
            helmYamlObject = helmYaml.load(fileStream);
        } catch (IOException e) {
            logger.error("Unable to load file into yaml class due to exception:", e);
        }
        return helmYamlObject;
    }

    /**
     * Provide indent with given indent spaces and level of indention
     * @param level the level of indent
     * @return the indent
     */
    public static String indent(int level, int tab) {
        return StringUtils.repeat(SPACE, level*tab);
    }

    /**
     * Provide indent with default indent spaces (2) and level of indention
     * @param level the level of indent
     * @return the indent
     */
    public static String indent(int level) {
        return StringUtils.repeat(SPACE, level*TAB);
    }


    /**
     * get the number of indent spaces with given file yaml file content in List format and a start index to search the indent
     * @param yamlFileContent yaml file content in list format
     * @param startIndex start index to search the indent
     * @return number of indent spaces
     */
    public static int getIndentSpaces(List<String> yamlFileContent, int startIndex) {
        int indentSpaces = 0;
        int index = startIndex;
        while (indentSpaces == 0) {
            String line = yamlFileContent.get(index).stripTrailing();
            if (!StringUtils.isBlank(line)) {
                indentSpaces = line.indexOf(line.trim());
            }
            index++;
        }
        return indentSpaces;
    }
}
