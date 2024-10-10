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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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

    public static boolean isComment(String trimmedLine) {
        // Helm template comment can be: "{{/*", "{{ /*", or "{{- /*"
        return trimmedLine.startsWith("#") || trimmedLine.matches("\\{\\{-? */\\*.*");
    }

    public static boolean isHelmFunction(String trimmedLine) {
        return trimmedLine.startsWith("{{");
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static class YamlObject {
        private final Map<String, Object> contents;

        public YamlObject(Map<String, Object> contents) {
            this.contents = contents;
        }

        public boolean hasString(String... path) {
            return hasValue(String.class, path);
        }

        public String getString(String... path) {
            return getValue(path);
        }

        public boolean hasInt(String... path) {
            return hasValue(int.class, path);
        }

        public int getInt(String... path) {
            return getValue(path);
        }

        public boolean hasDouble(String... path) {
            return hasValue(double.class, path);
        }

        public double getDouble(String... path) {
            return getValue(path);
        }

        public boolean hasBoolean(String... path) {
            return hasValue(boolean.class, path);
        }

        public boolean getBoolean(String... path) {
            return getValue(path);
        }

        public boolean hasObject(String... path) {
            YamlObject obj = this;
            for (String key : path) {
                if (!obj.containsKey(key) || !(obj.get(key) instanceof Map)) {
                    return false;
                }
                obj = obj.getObject(key);
            }
            return true;
        }

        public YamlObject getObject(String... path) {
            YamlObject obj = this;
            for (String key : path) {
                Object value = obj.get(key);
                if (!(value instanceof Map)) {
                    throw new IllegalArgumentException(String.format("Value for [%s] is not of type Map: %s", key, value));
                }
                obj = new YamlObject((Map<String, Object>) value);
            }
            return obj;
        }

        public boolean hasList(String... path) {
            return hasValue(List.class, path);
        }

        public List<?> getList(String... path) {
            return getValue(path);
        }

        public List<YamlObject> getListOfObjects(String... path) {
            List<?> list = getList(path);
            if (list.stream().anyMatch(o -> !(o instanceof Map))) {
                throw new IllegalArgumentException("List contains non-map elements: " + list);
            }
            return list.stream()
                    .map(o -> (Map<String, Object>) o)
                    .map(YamlObject::new)
                    .collect(Collectors.toList());
        }

        public List<String> getListOfStrings(String... path) {
            List<?> list = getList(path);
            if (list.stream().anyMatch(o -> !(o instanceof String))) {
                throw new IllegalArgumentException("List contains non-string elements: " + list);
            }
            return (List<String>) list;
        }

        @Nonnull
        public Object get(String key) {
            if (!containsKey(key)) {
                throw new NoSuchElementException("Key not found: " + key);
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

        private <T> boolean hasValue(Class<T> type, String... path) {
            YamlObject parent = getParentIfExists(path);
            if (parent == null) {
                return false;
            }
            String key = path[path.length - 1];
            return parent.containsKey(key) && type.isInstance(parent.get(key));
        }

        private <T> T getValue(String... path) {
            YamlObject parent = getParentIfExists(path);
            if (parent == null) {
                throw new NoSuchElementException("Parent object does not exist at " + Arrays.toString(path));
            }
            String key = path[path.length - 1];
            return (T) parent.get(key);
        }

        private YamlObject getParentIfExists(String[] path) {
            if (path.length == 0) {
                throw new IllegalArgumentException("Root path is always an object");
            }
            String[] parentPath = Arrays.copyOf(path, path.length - 1);
            YamlObject parent = null;
            if (hasObject(parentPath)) {
                parent = getObject(parentPath);
            }
            return parent;
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
            if (!StringUtils.isBlank(line) && !isComment(line.trim()) && !isHelmFunction(line.trim())) {
                indentSpaces = line.indexOf(line.trim());
            }
            index++;
        }
        return indentSpaces;
    }
}
