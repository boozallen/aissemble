package com.boozallen.aissemble.upgrade.migration.extensions;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.TiltfileMigration;

public class TiltfileMigrationTest extends TiltfileMigration {
    @Override
    public String getAissembleVersion() {
        return "1.4.0";
    }
}
