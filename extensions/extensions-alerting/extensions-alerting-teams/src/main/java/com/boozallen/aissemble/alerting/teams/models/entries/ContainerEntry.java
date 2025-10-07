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

import java.util.List;
import java.util.Objects;

public class ContainerEntry extends CardBodyEntry {

    private String type;
    private List<CardBodyEntry> items;

    // Needed for JSON serialization but should never change
    @Override
    public String getType() {
        return "Container";
    }

    // Needed for JSON serialization but should never change
    @Override
    public void setType(String type) {
        this.type = "Container";
    }

    public List<CardBodyEntry> getItems() {
        return items;
    }

    public void setItems(List<CardBodyEntry> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ContainerEntry that = (ContainerEntry) o;
        return Objects.equals(getType(), that.getType()) && Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getItems());
    }
}
