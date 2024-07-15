package com.boozallen.aissemble.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.security.config.SecurityConfiguration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.boozallen.aissemble.authz.attribute._1.AissembleAttributeExtension;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
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

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Provides an Authzforce attribute provider that allows relatively easy configuration of various attributes for use in
 * the policy decision point.
 */
public class AissembleAttributeProvider extends BaseNamedAttributeProvider {

    private static final String XML_SCHEMA_DATE = "http://www.w3.org/2001/XMLSchema#date";

    private static final String XML_SCHEMA_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";

    private static final String XML_SCHEMA_ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

    private static final String XML_SCHEMA_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";

    private static final String XML_SCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";

    private static final String XML_SCHEMA_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";

    private static final TypeReference<List<AissembleAttribute>> attributeListTypeReference = new TypeReference<List<AissembleAttribute>>() {};

    private static final Logger logger = LoggerFactory.getLogger(AissembleAttributeProvider.class);

    protected SecurityConfiguration config = KrauseningConfigFactory.create(SecurityConfiguration.class);
    protected Map<String, AttributeDesignatorType> supportedDesignatorTypes = new HashMap<>();
    protected Map<Class<AissembleAttributePoint>, AissembleAttributePoint> pointClassToInstanceMap = new HashMap<>();
    protected Map<String, AissembleAttributePoint> idToAttributePointMap = new HashMap<>();

    private Cache<String, Collection<com.boozallen.aissemble.security.authorization.policy.AttributeValue<?>>> attributeCache;

    private AissembleAttributeProvider(AissembleAttributeExtension conf) {
        super(conf.getId());

        loadAttributeConfiguration();
    }

    /**
     * Allows an instance to be created outside the PDP.
     */
    public AissembleAttributeProvider() {
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
        Collection<com.boozallen.aissemble.security.authorization.policy.AttributeValue<?>> retrievedValues = getAissembleAttributeByIdAndSubject(
                id, subject);
        if (retrievedValues != null) {
            for (com.boozallen.aissemble.security.authorization.policy.AttributeValue<?> retrievedValue : retrievedValues) {
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
     * Returns a request for a simple, aissemble attribute by attribute id and subject.
     *
     * @param id
     *            attribute id
     * @param subject
     *            subject name
     * @return collection of attributes or null if none found
     */
    public Collection<com.boozallen.aissemble.security.authorization.policy.AttributeValue<?>> getAissembleAttributeByIdAndSubject(String id,
                                                                                                                               String subject) {
        final String cacheKey = subject + ':' + id;

        Collection<com.boozallen.aissemble.security.authorization.policy.AttributeValue<?>> retrievedValues;
        retrievedValues = attributeCache.getIfPresent(cacheKey);
        if (retrievedValues == null) {
            AissembleAttributePoint attributePoint = idToAttributePointMap.get(id);
            retrievedValues = attributePoint.getValueForAttribute(id, subject);
            attributeCache.put(cacheKey, retrievedValues);
        }

        return retrievedValues;
    }

    protected <AV extends AttributeValue> SimpleValue<?> convertRetrievedValueToXacmlFormat(
            Datatype<AV> attributeDatatype, String id, String subject,
            com.boozallen.aissemble.security.authorization.policy.AttributeValue<?> retrievedValue) {
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
     * {@link AissembleAttributeExtension} factory
     *
     */
    public static class Factory extends CloseableNamedAttributeProvider.FactoryBuilder<AissembleAttributeExtension> {

        @Override
        public Class<AissembleAttributeExtension> getJaxbClass() {
            return AissembleAttributeExtension.class;
        }

        @Override
        public DependencyAwareFactory getInstance(AissembleAttributeExtension conf, EnvironmentProperties envProperties) {
            return new DependencyAwareFactory() {
                @Override
                public Set<AttributeDesignatorType> getDependencies() {
                    return null;
                }

                @Override
                public CloseableNamedAttributeProvider getInstance(AttributeValueFactoryRegistry var1, NamedAttributeProvider var2) {
                    return new AissembleAttributeProvider(conf);
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
                List<AissembleAttribute> attributes;
                try {
                    attributes = mapper.readValue(attributeDefintionFile, attributeListTypeReference);
                    for (AissembleAttribute attribute : attributes) {
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

    protected void addAttributeDefinition(AissembleAttribute attribute) {
        String id = attribute.getId();

        AttributeDesignatorType designatorType = AissembleAttributeUtils.translateAttributeToXacmlFormat(attribute);
        AttributeDesignatorType existingDesignatorType = supportedDesignatorTypes.put(id, designatorType);

        AissembleAttributePoint attributePoint = findAttributePointImplementation(attribute);
        idToAttributePointMap.put(id, attributePoint);

        logger.info("Translated aissemble attribute definition '{}' into the fully qualified \n\t{}", id, designatorType);

        if (existingDesignatorType != null) {
            logger.warn("Multiple attributes named '{}' exist!  The last one in read will be used {}", id,
                    designatorType);
        }
    }

    protected AissembleAttributePoint findAttributePointImplementation(AissembleAttribute attribute) {
        AissembleAttributePoint attributePoint = null;

        String attributePointClassName = null;
        try {
            attributePointClassName = attribute.getAttributePointClass();
            if (StringUtils.isBlank(attributePointClassName)) {
                logger.error("No attribute point specified for attribute '{}'!", attribute.getId());

            } else {
                Class<AissembleAttributePoint> attributePointClass = (Class<AissembleAttributePoint>) Class
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
            logger.error("Could not find attribute point '{}' in classpath!", attributePointClassName);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Could not instantiate attribute point '" + attributePointClassName + "'!", e);
        }

        return attributePoint;
    }
}
