package com.boozallen.aissemble.alerting.teams.models;

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

import java.util.List;
import java.util.Objects;

public class CardMessage {
    private String type;
    private List<Card> attachments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Card> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Card> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CardMessage that = (CardMessage) o;
        return Objects.equals(getType(), that.getType()) && Objects.equals(getAttachments(), that.getAttachments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getAttachments());
    }
}
