package com.boozallen.aissemble.upgrade.pojo;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

import lombok.Data;

/**
 * Java object to represent a Chart.yaml file with dependencies
 */
@Data
public class Chart implements AbstractYamlObject {
    private List<Dependency> dependencies;

    @Data
    public static class Dependency {
        private String name;
        private String version;
    }
}
