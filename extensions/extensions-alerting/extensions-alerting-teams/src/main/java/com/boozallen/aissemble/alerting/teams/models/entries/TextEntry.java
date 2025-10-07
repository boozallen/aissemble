package com.boozallen.aissemble.alerting.teams.models.entries;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
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

import com.boozallen.aissemble.alerting.teams.models.CardBodyEntry;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TextEntry textEntry = (TextEntry) o;
        return isWrap() == textEntry.isWrap() && Objects.equals(getType(), textEntry.getType()) && Objects.equals(getText(), textEntry.getText()) && Objects.equals(getWeight(), textEntry.getWeight()) && Objects.equals(getSize(), textEntry.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getText(), getWeight(), getSize(), isWrap());
    }
}
