package com.boozallen.aiops.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.boozallen.aiops.security.config.SecurityConfiguration;
import com.github.boozallen.aissemble.authz.attribute._1.AiopsAttributeExtension;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.ow2.authzforce.core.pdp.api.AttributeFqn;
import org.ow2.authzforce.core.pdp.api.BaseNamedAttributeProvider;
import org.ow2.authzforce.core.pdp.api.CloseableNamedAttributeProvider;
import org.ow2.authzforce.core.pdp.api.EnvironmentProperties;
import org.ow2.authzforce.core.pdp.api.EvaluationContext;
import org.ow2.authzforce.core.pdp.api.IndeterminateEvaluationException;
import org.ow2.authzforce.core.pdp.api.NamedAttributeProvider;
import org.ow2.authzforce.core.pdp.api.value.AttributeBag;
import org.ow2.authzforce.core.pdp.api.value.AttributeValue;
import org.ow2.authzforce.core.pdp.api.value.AttributeValueFactoryRegistry;
import org.ow2.authzforce.core.pdp.api.value.Bags;
import org.ow2.authzforce.core.pdp.api.value.Datatype;
import org.ow2.authzforce.core.pdp.api.value.SimpleValue;
import org.ow2.authzforce.xacml.identifiers.XacmlAttributeId;
import org.ow2.authzforce.xacml.identifiers.XacmlStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;

/**
 * Provides an Authzforce attribute provider that allows relatively easy configuration of various attributes for use in
 * the policy decision point.
 */
public class AiopsAttributeProvider extends BaseNamedAttributeProvider {

    private static final String XML_SCHEMA_DATE = "http://www.w3.org/2001/XMLSchema#date";

    private static final String XML_SCHEMA_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";

    private static final String XML_SCHEMA_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

    private static final String XML_SCHEMA_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";

    private static final String XML_SCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";

    private static final String XML_SCHEMA_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";

    private static final TypeReference<List<AiopsAttribute>> attributeListTypeReference = new TypeReference<List<AiopsAttribute>>() {};

    private static final Logger logger = LoggerFactory.getLogger(AiopsAttributeProvider.class);

    protected SecurityConfiguration config = KrauseningConfigFactory.create(SecurityConfiguration.class);
    protected Map<String, AttributeDesignatorType> supportedDesignatorTypes = new HashMap<>();
    protected Map<Class<AiopsAttributePoint>, AiopsAttributePoint> pointClassToInstanceMap = new HashMap<>();
    protected Map<String, AiopsAttributePoint> idToAttributePointMap = new HashMap<>();

    private Cache<String, Collection<com.boozallen.aiops.security.authorization.policy.AttributeValue<?>>> attributeCache;

    private AiopsAttributeProvider(AiopsAttributeExtension conf) {
        super(conf.getId());

        loadAttributeConfiguration();
    }

    /**
     * Allows an instance to be created outside the PDP.
     */
    public AiopsAttributeProvider() {
        super(RandomStringUtils.randomAlphabetic(10));

        loadAttributeConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // nothing to close
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AttributeDesignatorType> getProvidedAttributes() {
        return Collections.unmodifiableSet(new HashSet<>(supportedDesignatorTypes.values()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <AV extends AttributeValue> AttributeBag<AV> get(AttributeFqn attributeGUID, Datatype<AV> attributeDatatype,
                                                            EvaluationContext context, Optional<EvaluationContext> optionalEvaluationContext) throws IndeterminateEvaluationException {
        String id = attributeGUID.getId();
        String subject = findSubjectInEnvironmentContext(context);

        // lookup the correct attribute point to use:
        Collection<AV> attributeCollection = new ArrayList<>();
        Collection<com.boozallen.aiops.security.authorization.policy.AttributeValue<?>> retrievedValues = getAiopsAttributeByIdAndSubject(
                id, subject);
        if (retrievedValues != null) {
            for (com.boozallen.aiops.security.authorization.policy.AttributeValue<?> retrievedValue : retrievedValues) {
                SimpleValue<?> simpleValue = convertRetrievedValueToXacmlFormat(attributeDatatype, id, subject,
                        retrievedValue);

                if (simpleValue != null) {
                    attributeCollection.add((AV) simpleValue);
                }
            }
        }

        AttributeBag<AV> attrVals = Bags.newAttributeBag(attributeDatatype, attributeCollection);
        if (logger.isDebugEnabled()) {
            logger.debug("Retrieved attribute '{}' for subject '{}' with the values '{}'", id, subject,
                    attributeCollection);
        }

        if (attrVals.getElementDatatype().equals(attributeDatatype)) {
            return attrVals;
        }

        throw new IndeterminateEvaluationException("Requested datatype (" + attributeDatatype + ") != provided by "
                + this + " (" + attrVals.getElementDatatype() + ")", XacmlStatusCode.MISSING_ATTRIBUTE.value());
    }


    /**
     * Returns a request for a simple, AIOps attribute by attribute id and subject.
     *
     * @param id
     *            attribute id
     * @param subject
     *            subject name
     * @return collection of attributes or null if none found
     */
    public Collection<com.boozallen.aiops.security.authorization.policy.AttributeValue<?>> getAiopsAttributeByIdAndSubject(String id,
            String subject) {
        final String cacheKey = subject + ':' + id;

        Collection<com.boozallen.aiops.security.authorization.policy.AttributeValue<?>> retrievedValues;
        retrievedValues = attributeCache.getIfPresent(cacheKey);
        if (retrievedValues == null) {
            AiopsAttributePoint attributePoint = idToAttributePointMap.get(id);
            retrievedValues = attributePoint.getValueForAttribute(id, subject);
            attributeCache.put(cacheKey, retrievedValues);
        }

        return retrievedValues;
    }

    protected <AV extends AttributeValue> SimpleValue<?> convertRetrievedValueToXacmlFormat(
            Datatype<AV> attributeDatatype, String id, String subject,
            com.boozallen.aiops.security.authorization.policy.AttributeValue<?> retrievedValue) {
        SimpleValue<?> simpleValue = null;
        if (retrievedValue != null) {
            switch (attributeDatatype.toString()) {
            case XML_SCHEMA_INTEGER:
                simpleValue = retrievedValue.getAsIntegerValue();
                break;
            case XML_SCHEMA_STRING:
                simpleValue = retrievedValue.getAsStringValue();
                break;
            case XML_SCHEMA_BOOLEAN:
                simpleValue = retrievedValue.getAsBooleanValue();
                break;
            case XML_SCHEMA_ANY_URI:
                simpleValue = retrievedValue.getAsAnyUriValue();
                break;
            case XML_SCHEMA_DOUBLE:
                simpleValue = retrievedValue.getAsDoubleValue();
                break;
            case XML_SCHEMA_DATE:
                simpleValue = retrievedValue.getAsDateValue();
                break;
            default:
                simpleValue = retrievedValue.getAsStringValue();
                logger.warn("No type of '{}' was found for attribute '{}', converting to string!", attributeDatatype,
                        id);
            }
        }

        return simpleValue;
    }

    protected String findSubjectInEnvironmentContext(final EvaluationContext context) {
        String subject = null;
        Iterator<Entry<AttributeFqn, AttributeBag<?>>> contextAttributeIterator = context.getNamedAttributes();
        while (contextAttributeIterator.hasNext()) {
            Entry<AttributeFqn, AttributeBag<?>> contextAttribute = contextAttributeIterator.next();
            AttributeFqn attributeFullyQualifiedName = contextAttribute.getKey();
            if (XacmlAttributeId.XACML_1_0_SUBJECT_ID.value().equals(attributeFullyQualifiedName.getId())) {
                subject = contextAttribute.getValue().getSingleElement().toString();
                break;
            }
        }
        return subject;
    }

    /**
     * {@link AiopsAttributeExtension} factory
     *
     */
    public static class Factory extends CloseableNamedAttributeProvider.FactoryBuilder<AiopsAttributeExtension> {

        @Override
        public Class<AiopsAttributeExtension> getJaxbClass() {
            return AiopsAttributeExtension.class;
        }

        @Override
        public DependencyAwareFactory getInstance(AiopsAttributeExtension conf, EnvironmentProperties envProperties) {
            return new DependencyAwareFactory() {
                @Override
                public Set<AttributeDesignatorType> getDependencies() {
                    return null;
                }

                @Override
                public CloseableNamedAttributeProvider getInstance(AttributeValueFactoryRegistry var1, NamedAttributeProvider var2) {
                    return new AiopsAttributeProvider(conf);
                }
            };
        }
    }

    protected void loadAttributeConfiguration() {
        String attributeLocation = config.getAttributeDefinitionLocation();
        ObjectMapper mapper = new ObjectMapper();
        File root = new File(attributeLocation);
        if (root.exists()) {
            String[] extensions = { "json" };
            Collection<File> files = FileUtils.listFiles(root, extensions, true);
            if (files.isEmpty()) {
                logger.warn(
                        "Your PDP is configured to use custom attributes, but the location that defines "
                                + "these attributes does not contain any attribute definitions! - {}",
                        root.getAbsolutePath());
            }

            for (File attributeDefintionFile : files) {
                List<AiopsAttribute> attributes;
                try {
                    attributes = mapper.readValue(attributeDefintionFile, attributeListTypeReference);
                    for (AiopsAttribute attribute : attributes) {
                        addAttributeDefinition(attribute);
                    }
                } catch (IOException e) {
                    logger.error("Problem loading attributes in file {}!", attributeDefintionFile.getAbsolutePath(), e);
                    attributes = Collections.emptyList();
                }
                logger.info("Found {} attribute definitions in file {}", attributes.size(),
                        attributeDefintionFile.getAbsolutePath());
            }

        } else {
            logger.warn("\n");
            logger.warn("*********************************************************************************");
            logger.warn("Your PDP is configured to use custom attributes, but the location that defines these "
                    + "attributes does not exist! - {}", root.getAbsolutePath());
            logger.warn("Update the your authorization.properties via Krausening to point to a valid location "
                    + "for attribute json files!");
            logger.warn("*********************************************************************************");
            logger.warn("\n");
        }

        attributeCache = Caffeine.newBuilder()
                .expireAfterWrite(config.getAttributeCacheExpirationInMinutes(), TimeUnit.MINUTES).build();
    }

    protected void addAttributeDefinition(AiopsAttribute attribute) {
        String id = attribute.getId();

        AttributeDesignatorType designatorType = AiopsAttributeUtils.translateAttributeToXacmlFormat(attribute);
        AttributeDesignatorType existingDesignatorType = supportedDesignatorTypes.put(id, designatorType);

        AiopsAttributePoint attributePoint = findAttributePointImplementation(attribute);
        idToAttributePointMap.put(id, attributePoint);

        logger.info("Translated AIOps attribute definition '{}' into the fully qualified \n\t{}", id, designatorType);

        if (existingDesignatorType != null) {
            logger.warn("Multiple attributes named '{}' exist!  The last one in read will be used {}", id,
                    designatorType);
        }
    }

    protected AiopsAttributePoint findAttributePointImplementation(AiopsAttribute attribute) {
        AiopsAttributePoint attributePoint = null;

        String attributePointClassName = null;
        try {
            attributePointClassName = attribute.getAttributePointClass();
            if (StringUtils.isBlank(attributePointClassName)) {
                logger.error("No attribute point specified for attribute '{}'!", attribute.getId());

            } else {
                Class<AiopsAttributePoint> attributePointClass = (Class<AiopsAttributePoint>) Class
                        .forName(attributePointClassName, false, Thread.currentThread().getContextClassLoader());

                // reuse an existing instance if we have already encountered this class:
                attributePoint = pointClassToInstanceMap.get(attributePointClass);

                if (attributePoint == null) {
                    // add a new reusable instance if we have not already encountered this class:
                    attributePoint = attributePointClass.newInstance();
                    pointClassToInstanceMap.put(attributePointClass, attributePoint);
                    logger.debug("Instantiated AttributePoint '{}'", attributePointClass.getName());
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("Could not find attribute point '{}' in classpath!", attributePointClassName, e);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Could not instantiate attribute point '{}'!", attributePointClassName, e);
        }

        return attributePoint;
    }
}
