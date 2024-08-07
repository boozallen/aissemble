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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TiltfileUtil {

    /**
     * Specifies the latest version of VERSION_AISSEMBLE to be used by the tilt file.
     */
    public static final String COLON = ":";
    public static final String EQUALS = "=";

    // This should be the old spelling so we detect when we have to upgrade
    public static final String VERSION_AISSEMBLE_PRE_1_5_0 = "'VERSION_AISSEMBLE'";
    /**
     * This regex is supposed to match all old aissemble version notations from
     * 'VERSION_AISSEMBLE': '1.4.0' to 'VERSION_AISSEMBLE': = '1.5.0-SNAPSHOT'
     */
    public static final String VERSION_AISSEMBLE_PRE_1_5_0_REGEX = "(.*['\"]VERSION_AISSEMBLE['\"]: *)(['\"]\\d+.\\d+.\\d+[-\\da-zA-Z]*['\"])(.*)";

    // Data pipeline commands in Tiltfiles will be helm templates
    public static final String VERSION_AISSEMBLE_PRE_PIPELINE = "helm template";

    /**
     * This is regex will match the version flag in helm template commands for data pipelines.
     * Ex: --version 1.4.3
     */
    public static final String VERSION_AISSEMBLE_PRE_PIPELINE_REGEX = "(.*--version )(\\d+.\\d+.\\d+[-\\da-zA-Z]*)(.*)(\\))";

    // The post-1.5.0 notation of aissemble_version
    public static final String VERSION_AISSEMBLE_POST_1_5_0 = "aissemble_version";

    /**
     * This regex is supposed to match all new aissemble version notations from
     * aissemble_version = '1.4.0' to aissemble_version = '1.5.0-SNAPSHOT'
     */
    public static final String VERSION_AISSEMBLE_POST_1_5_0_REGEX = "(aissemble_version *= *['\"])(\\d+.\\d+.\\d+[-\\da-zA-Z]*)(['\"])";

    /**
     *
     * @param line that we are checking contents of
     * @return String representation of aiSSEMBLE version, or null if not found
     */
    public static String extractAissembleVersion(String line) {
        String tiltfileVersionvalue;

        if (line.trim().matches(VERSION_AISSEMBLE_POST_1_5_0_REGEX)) {
            int index = line.indexOf(VERSION_AISSEMBLE_POST_1_5_0);
            String result = (index != -1) ? line.substring(index) : line;
            String[] parts = result.split(EQUALS);

            if (parts.length != 2 || !result.contains(EQUALS)) {
                // because the variable name can appear in other lines
                return null;
            }

            tiltfileVersionvalue = parts[1].replaceAll("['|\"]","").trim();

            return tiltfileVersionvalue;
        }
        else if (line.trim().matches(VERSION_AISSEMBLE_PRE_1_5_0_REGEX)) {
            Pattern pattern = Pattern.compile(TiltfileUtil.VERSION_AISSEMBLE_PRE_1_5_0_REGEX, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            tiltfileVersionvalue = matcher.group(2).replaceAll("['|\"]", "");
            return tiltfileVersionvalue;
        }
        else {
            return null;
        }
    }

}

