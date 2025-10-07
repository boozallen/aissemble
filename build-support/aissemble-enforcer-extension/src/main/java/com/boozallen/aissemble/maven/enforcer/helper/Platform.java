package com.boozallen.aissemble.maven.enforcer.helper;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
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

import java.io.File;

/**
 * Largely pulled from the com.github.eirslett:frontend-maven-plugin and then
 * culled of any Node/front-end specific references. The visibility of this
 * class within the frontend-maven-plugin does not easily facilitate reuse in
 * other modules, so a pared-down version is maintained here.
 */
enum Architecture {
    x86, x64, ppc64le, s390x, arm64, armv7l;

    public static Architecture guess() {
        String arch = System.getProperty("os.arch");
        String version = System.getProperty("os.version");

        if (arch.equals("ppc64le")) {
            return ppc64le;
        } else if (arch.equals("aarch64")) {
            return arm64;
        } else if (arch.equals("s390x")) {
            return s390x;
        } else if (arch.equals("arm") && version.contains("v7")) {
            return armv7l;
        } else {
            return arch.contains("64") ? x64 : x86;
        }
    }
}

enum OS {
    Windows, Mac, Linux, SunOS;

    public static OS guess() {
        final String osName = System.getProperty("os.name");
        return osName.contains("Windows") ? OS.Windows
                : osName.contains("Mac") ? OS.Mac : osName.contains("SunOS") ? OS.SunOS : OS.Linux;
    }

    public String getArchiveExtension() {
        if (this == OS.Windows) {
            return "zip";
        } else {
            return "tar.gz";
        }
    }

    public String getCodename() {
        if (this == OS.Mac) {
            return "darwin";
        } else if (this == OS.Windows) {
            return "win";
        } else if (this == OS.SunOS) {
            return "sunos";
        } else {
            return "linux";
        }
    }
}

public class Platform {
    private final OS os;
    private final Architecture architecture;
    private final String classifier;

    public Platform(OS os, Architecture architecture) {
        this(os, architecture, null);
    }

    public Platform(OS os, Architecture architecture, String classifier) {
        this.os = os;
        this.architecture = architecture;
        this.classifier = classifier;
    }

    public static Platform guess() {
        OS os = OS.guess();
        Architecture architecture = Architecture.guess();
        // The default libc is glibc, but Alpine uses musl. When not default, the nodejs
        // download
        // (and path within it) needs a classifier in the suffix (ex. -musl).
        // We know Alpine is in use if the release file exists, and this is the simplest
        // check.
        if (os == OS.Linux && new File("/etc/alpine-release").exists()) {
            return new Platform(os, architecture, "musl");
        }
        return new Platform(os, architecture);
    }

    public String getArchiveExtension() {
        return os.getArchiveExtension();
    }

    public String getCodename() {
        return os.getCodename();
    }

    public boolean isWindows() {
        return os == OS.Windows;
    }

    public boolean isMac() {
        return os == OS.Mac;
    }

}
