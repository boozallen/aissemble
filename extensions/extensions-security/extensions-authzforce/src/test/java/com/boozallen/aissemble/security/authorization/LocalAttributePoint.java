package com.boozallen.aissemble.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
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

import com.boozallen.aissemble.security.authorization.policy.AissembleAttributePoint;
import com.boozallen.aissemble.security.authorization.policy.AttributeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Test attribute point that just does some local logic to support test cases.
 */
public class LocalAttributePoint implements AissembleAttributePoint {

    @Override
    public Collection<AttributeValue<?>> getValueForAttribute(String attributeId, String subject) {
        Collection<AttributeValue<?>> values = null;
        if ("urn:aissemble:jerseyNumber".equals(attributeId)) {
            values = sourceJerseyNumberAttribute(attributeId, subject);
        } else if ("urn:aissemble:hallOfFameProfile".equals(attributeId)) {
            values = sourceHallOfFameProfileAttribute(attributeId, subject);
        } else if ("urn:aissemble:suspectedPedUser".equals(attributeId)) {
            values = sourceSuspectedPedUserAttribute(attributeId, subject);
        } else if ("urn:aissemble:battingAverage".equals(attributeId)) {
            values = sourceBattingAverageAttribute(attributeId, subject);
        } else if ("urn:aissemble:serviceEntryDate".equals(attributeId)) {
            values = sourceServiceEntryDateAttribute(attributeId, subject);
        } else if ("urn:aissemble:seasonsBattingOver350".equals(attributeId)) {
            values = sourceSeasonsBattingOver350Attribute(attributeId, subject);
        }

        return values;
    }

    protected Collection<AttributeValue<?>> sourceJerseyNumberAttribute(String attributeId, String subject) {
        AttributeValue<?> value = null;

        if ("reggieJackson".equals(subject)) {
            value = new AttributeValue<Integer>(attributeId, 44);
        } else if ("tonyGwynn".equals(subject)) {
            value = new AttributeValue<Integer>(attributeId, 19);
        } else if ("alexOvechkin".equals(subject)) {
            value = new AttributeValue<Integer>(attributeId, 8);
        } else if ("anthonyRizzo".equals(subject)) {
            value = new AttributeValue<Integer>(attributeId, 44);
        }

        return wrapSingleValueInCollection(value);
    }

    protected Collection<AttributeValue<?>> sourceHallOfFameProfileAttribute(String attributeId, String subject) {
        AttributeValue<?> value = null;

        if ("reggieJackson".equals(subject)) {
            value = new AttributeValue<String>(attributeId, "https://baseballhall.org/hall-of-famers/jackson-reggie");
        } else if ("tonyGwynn".equals(subject)) {
            value = new AttributeValue<String>(attributeId, "https://baseballhall.org/hall-of-famers/gwynn-tony");
        } else if ("marioMendoza".equals(subject)) {
            value = new AttributeValue<String>(attributeId, "https://baseballhall.mx/mexican-hall-of-famers/mendoza");
        } else if ("alexOvechkin".equals(subject)) {
            value = new AttributeValue<String>(attributeId, "https://www.nhl.com/news/alex-ovechkin-100-greatest");
        }

        return wrapSingleValueInCollection(value);
    }

    protected Collection<AttributeValue<?>> sourceSuspectedPedUserAttribute(String attributeId, String subject) {
        AttributeValue<?> value = null;

        if ("reggieJackson".equals(subject)) {
            value = new AttributeValue<Boolean>(attributeId, Boolean.FALSE);
        } else if ("tonyGwynn".equals(subject)) {
            value = new AttributeValue<Boolean>(attributeId, Boolean.FALSE);
        } else if ("kenCaminiti".equals(subject)) {
            value = new AttributeValue<Boolean>(attributeId, Boolean.TRUE);
        } else if ("wallyJoyner".equals(subject)) {
            value = new AttributeValue<Boolean>(attributeId, Boolean.TRUE);
        }

        return wrapSingleValueInCollection(value);
    }

    protected Collection<AttributeValue<?>> sourceBattingAverageAttribute(String attributeId, String subject) {
        AttributeValue<?> value = null;

        if ("reggieJackson".equals(subject)) {
            value = new AttributeValue<Double>(attributeId, Double.valueOf(.262));
        } else if ("tonyGwynn".equals(subject)) {
            value = new AttributeValue<Double>(attributeId, Double.valueOf(.338));
        } else if ("kenCaminiti".equals(subject)) {
            value = new AttributeValue<Double>(attributeId, Double.valueOf(.272));
        } else if ("marioMendoza".equals(subject)) {
            value = new AttributeValue<Double>(attributeId, Double.valueOf(.215));
        }

        return wrapSingleValueInCollection(value);
    }

    protected Collection<AttributeValue<?>> sourceServiceEntryDateAttribute(String attributeId, String subject) {
        AttributeValue<?> value = null;

        if ("reggieJackson".equals(subject)) {
            value = new AttributeValue<Date>(attributeId, new Date(-78364800000L));
        } else if ("tonyGwynn".equals(subject)) {
            value = new AttributeValue<Date>(attributeId, new Date(395971200000L));
        } else if ("kenCaminiti".equals(subject)) {
            value = new AttributeValue<Date>(attributeId, new Date(553392000000L));
        } else if ("marioMendoza".equals(subject)) {
            value = new AttributeValue<Date>(attributeId, new Date(136166400000L));
        }

        return wrapSingleValueInCollection(value);
    }

    protected Collection<AttributeValue<?>> sourceSeasonsBattingOver350Attribute(String attributeId, String subject) {
        Collection<AttributeValue<?>> values = new ArrayList<>();
        if ("reggieJackson".equals(subject)) {
            //none
        } else if ("tonyGwynn".equals(subject)) {
            values.add(new AttributeValue<Integer>(attributeId, 1984));
            values.add(new AttributeValue<Integer>(attributeId, 1987));
            values.add(new AttributeValue<Integer>(attributeId, 1993));
            values.add(new AttributeValue<Integer>(attributeId, 1994));
            values.add(new AttributeValue<Integer>(attributeId, 1995));
            values.add(new AttributeValue<Integer>(attributeId, 1996));
            values.add(new AttributeValue<Integer>(attributeId, 1997));

        } else if ("wadeBoggs".equals(subject)) {
            values.add(new AttributeValue<Integer>(attributeId, 1983));
            values.add(new AttributeValue<Integer>(attributeId, 1984));
            values.add(new AttributeValue<Integer>(attributeId, 1985));
            values.add(new AttributeValue<Integer>(attributeId, 1987));
            values.add(new AttributeValue<Integer>(attributeId, 1988));

        } else if ("kirbyPuckett".equals(subject)) {
            values.add(new AttributeValue<Integer>(attributeId, 1988));
        }

        return values;
    }

    private Collection<AttributeValue<?>> wrapSingleValueInCollection(AttributeValue<?> value) {
        Collection<AttributeValue<?>> values = new ArrayList<>();
        values.add(value);
        return values;
    }
}

