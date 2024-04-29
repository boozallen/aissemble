package com.boozallen.aissemble.alerting.teams.models.entries;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.teams.models.CardBodyEntry;

public class TextEntry extends CardBodyEntry {

    private String type;
    private String text;
    private String weight;
    private String size;
    private boolean wrap;

    // Needed for JSON serialization but should never change
    @Override
    public String getType() {
        return "TextBlock";
    }

    // Needed for JSON serialization but should never change
    @Override
    public void setType(String type) {
        this.type = "TextBlock";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }
}
