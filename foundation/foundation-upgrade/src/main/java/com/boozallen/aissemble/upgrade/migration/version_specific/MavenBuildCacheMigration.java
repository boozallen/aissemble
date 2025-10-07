package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Updates the `maxBuilds` property of the `maven-build-cache-config.xml` file to 1 with an explanatory comment in order
 * to avoid issues with stale Docker images when restoring builds from the build cache.x
 */
public class MavenBuildCacheMigration extends AbstractAissembleMigration {
    private static final Pattern MAX_BUILDS = Pattern.compile("( *)<maxBuildsCached>(.*?)</maxBuildsCached>");
    private static final int MAX_BUILD_COUNT_CAPTURE = 2;

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        if (file.getName().endsWith(".xml")) {
            try (Stream<String> lines = Files.lines(file.toPath())) {
                return lines.map(MAX_BUILDS::matcher)
                        .filter(Matcher::find)
                        .findFirst()
                        .map(matcher -> matcher.group(MAX_BUILD_COUNT_CAPTURE))
                        .map(count -> !count.equals("1")).orElse(false);
            } catch (IOException e) {
                throw new BatonException("Failed to read build cache config", e);
            }
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            return FileUtils.replaceInFile(file, MAX_BUILDS.pattern(), getMaxBuildsWithComment());
        } catch (IOException e) {
            throw new BatonException("Failed to update <maxBuilds> to 1 in: " + file.getPath(), e);
        }
    }

    private String getMaxBuildsWithComment() {
        // note: first capture group of MAX_BUILDS regex is the leading space
        return """
$1<!-- NB: saving more than 1 build can result in stale docker images, as build-cache restore does not restore images -->
$1<maxBuildsCached>1</maxBuildsCached>""";
    }
}
