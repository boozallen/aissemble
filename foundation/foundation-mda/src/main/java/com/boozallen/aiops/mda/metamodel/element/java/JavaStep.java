package com.boozallen.aiops.mda.metamodel.element.java;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.*;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.util.JavaElementUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.TypeManager;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Step decorator to ease generation of Java files.
 */
public class JavaStep extends BaseStepDecorator {

    private static final String VOID = "void";
    private static final String MESSAGE = "message";
    private static final String MULTI_MESSAGE = "multimessage";
    private static final String DATASET = "dataset";
    private static final String ROW = "row";
    private static final String STRING = "string";

    /**
     * Uni is hard wired into our templates for a special messaging case, so it is not abstracted via the
     * {@link TypeManager}.
     */
    private static final String IO_SMALLRYE_MUTINY_UNI = "io.smallrye.mutiny.Uni";
    private Set<String> imports = new TreeSet<>();
    private AissembleModelInstanceRepository metamodelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AissembleModelInstanceRepository.class);

    /**
     * {@inheritDoc}
     */
    public JavaStep(Step stepToDecorate) {
        super(stepToDecorate);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Persist getPersist() {
        return super.getPersist() != null ? new JavaPersist(super.getPersist()) : null;
    }

    @Override
    public JavaStepDataBinding getInbound() {
        return super.getInbound() != null? new JavaStepDataBinding(super.getInbound()) : null;
    }

    @Override
    public JavaStepDataBinding getOutbound() {
        return super.getOutbound() != null? new JavaStepDataBinding(super.getOutbound()) : null;
    }

    /**
     * Calculates the plain Java signature for this operation. It's labor intensive and messy in velocity, so much more
     * clean to do it in the Java decorator.
     *
     * @return signature
     */
    public String getBaseSignature() {
        String inputType = getBaseInboundType();
        String outboundType = getBaseOutboundType();

        return createSignature("executeStep", "public", inputType, outboundType, false);
    }

    /**
     * Calculates the plain Java signature for this operation. It's labor intensive and messy in velocity, so much more
     * clean to do it in the Java decorator.
     *
     * @return signature
     */
    public String getAbstractImplSignature() {
        return getImplSignature(true);
    }

    /**
     * Calculates the plain Java signature for this operation. It's labor intensive and messy in velocity, so much more
     * clean to do it in the Java decorator.
     *
     * @return signature
     */
    public String getConcreteImplSignature() {
        return getImplSignature(false);
    }

    /**
     * Calculates the signature for the encryption.
     *
     * @return encryption method signature
     */
    public String getEncryptionSignature() {
        String inputType = getBaseInboundType();
        return createSignature("checkAndApplyEncryptionPolicy", "protected", inputType, inputType, false);
    }

    private String getImplSignature(boolean isAbstract) {
        String inputType;
        String outboundType;
        if ("asynchronous".equalsIgnoreCase(getType())) {
            inputType = getBaseInboundType();
            outboundType = getBaseOutboundType();
        } else {
            inputType = getImplInboundType();
            outboundType = getImplOutboundType();
        }

        return createSignature("executeStepImpl", "protected", inputType, outboundType, isAbstract);
    }

    private String createSignature(String methodName, String visibility, String inputType, String outputType,
                                   boolean isAbstract) {
        StringBuilder builder = new StringBuilder();
        builder.append(visibility).append(" ");

        if (isAbstract) {
            builder.append("abstract ");
        }

        builder.append(outputType).append(" ");
        builder.append(methodName).append("(");

        if (StringUtils.isNotBlank(inputType)) {
            builder.append(inputType);
            builder.append(" inbound");
        }

        builder.append(")");

        return builder.toString();
    }

    public boolean hasVoidInbound() {
        return isVoid(getInbound());
    }

    public boolean hasVoidOutbound() {
        return isVoid(getOutbound());
    }

    public boolean useEmitter() {
        return hasNativeInbound() || hasVoidInbound();
    }

    public Set<String> getBaseImports() {
        getBaseSignature();
        addPersistImports();

        return imports;
    }

    public Set<String> getImplImports() {
        getImplSignature(false);

        return imports;
    }

    @Override
    public boolean isMessaging(StepDataBinding stepDataBinding) {
        return stepDataBinding != null
                && ("messaging".equalsIgnoreCase(stepDataBinding.getType()));
    }

    public boolean isMultiMessaging(StepDataBinding stepDataBinding) {
        return stepDataBinding != null && "multimessaging".equalsIgnoreCase(stepDataBinding.getType());
    }

    /**
     * If the outbound type of this step is messaging, this method will return the Reactive Messaging outgoing channel
     * name.  Note this is not the same as {@link StepDataBinding#getChannelName()} and is simply an externality of our
     * utilization of Reactive Messaging to proxy the backing messaging provider (i.e. Kafka).
     *
     * @return the outgoing channel name for Reactive Messaging
     * @throws GenerationException if this step does not have a messaging outbound
     */
    public String getOutgoingChannel() {
        if(!hasMessagingOutbound()) {
            throw new GenerationException("Cannot retrieve outgoing channel name for non-messaging outbound: " + getName());
        }
        return getHyphenatedName() + "-out" ;
    }

    /**
     * If the inbound type of this step is messaging, this method will return the Reactive Messaging incoming channel
     * name.  Note this is not the same as {@link StepDataBinding#getChannelName()} and is simply an externality of our
     * utilization of Reactive Messaging to proxy the backing messaging provider (i.e. Kafka).
     *
     * @return the incoming channel name for Reactive Messaging
     * @throws GenerationException if this step does not have a messaging inbound
     */
    public String getIncomingChannel() {
        if(!hasMessagingInbound()) {
            throw new GenerationException("Cannot retrieve incoming channel name for non-messaging inbound: " + getName());
        }
        return getHyphenatedName() + "-in" ;
    }

    /**
     * Converts the camel-case step name to a lowercase hyphenated version. E.g. `IngestAWSInfo` => `ingest-aws-info`
     *
     * @return hyphenated version of the step name
     */
    private String getHyphenatedName() {
        String[] nameParts = StringUtils.splitByCharacterTypeCamelCase(getName());
        return StringUtils.join(nameParts, '-').toLowerCase();
    }

    public boolean isVoid(StepDataBinding stepDataBinding) {
        return stepDataBinding == null || VOID.equalsIgnoreCase(stepDataBinding.getType());
    }

    public String getBaseInboundType() {
        String inboundType = null;
        String implInboundType = getImplInboundType();
        if (hasMessagingInbound()) {
            String messageType = TypeManager.getShortType(MESSAGE);
            inboundType = messageType + "<" + implInboundType + ">";

            if (isMultiMessaging(getInbound())) {
                inboundType = TypeManager.getShortType(MULTI_MESSAGE) + "<" +  inboundType + ">";
            }

            addImport(TypeManager.getFullyQualifiedType("incoming"));
            addImport(TypeManager.getFullyQualifiedType(MESSAGE));

        } else if (!hasVoidInbound()) {
            inboundType = implInboundType;
        }

        return inboundType;
    }

    public String getBaseOutboundType() {
        if ("asynchronous".equalsIgnoreCase(getType())) {
            return getAsyncBaseOutboundType();
        } else {
            return getSyncBaseOutboundType();
        }
    }

    public String getAsyncBaseOutboundType() {
        String outboundType = "CompletionStage";
        StepDataBinding outbound = getOutbound();
        String implOutboundType = getImplOutboundType();
        if (outbound == null) {
            outboundType += "<Void>";
        } else {
            List<String> types = new ArrayList<>();
            if (isMessaging(outbound)) {
                types.add("<Message");
            }
            if (isMultiMessaging(getOutbound())) {
                types.add("<Message");
                outboundType = "Publisher";
            }

            if (implOutboundType != null) {
                types.add("<" + implOutboundType);
            }

            StringBuilder builder = new StringBuilder();
            types.forEach(builder::append);
            types.forEach(type -> builder.append(">"));

            outboundType += builder.toString();
        }

        return outboundType;
    }

    public String getSyncBaseOutboundType() {
        String outboundType = "";
        String implOutboundType = getImplOutboundType();

        boolean hasMessagingInbound = hasMessagingInbound();
        StepDataBinding outbound = getOutbound();
        if (outbound == null && hasMessagingInbound) {
            outboundType = "Uni<Void>";
            addImport(IO_SMALLRYE_MUTINY_UNI);

        } else if (hasVoidOutbound() || (hasMessagingOutbound() && hasNativeInbound())) {
            outboundType = VOID;

        } else if (hasMessagingOutbound()) {
            if (hasMultiMessagingOutbound()) {
                outboundType = TypeManager.getShortType(MULTI_MESSAGE) + "<" +  TypeManager.getShortType(MESSAGE) + ">";
                addImport(TypeManager.getFullyQualifiedType(MULTI_MESSAGE));
            } else {
                String messageType = TypeManager.getShortType(MESSAGE);
                outboundType = messageType + "<" + implOutboundType + ">";
            }

            addImport(TypeManager.getFullyQualifiedType("outgoing"));
            addImport(TypeManager.getFullyQualifiedType(MESSAGE));

        } else {
            if (hasMessagingInbound) {
                outboundType = "Uni<" + implOutboundType + ">";
                addImport(IO_SMALLRYE_MUTINY_UNI);
            } else {
                outboundType = implOutboundType;
            }
        }

        return outboundType;
    }

    public String getImplInboundType() {
        StepDataBinding inbound = getInbound();
        return deriveImplType(inbound);

    }

    public String getImplOutboundType() {
        String outboundType;

        StepDataBinding outbound = getOutbound();
        if (hasVoidOutbound()) {
            outboundType = VOID;

        } else {
            outboundType = deriveImplType(outbound);
        }

        return outboundType;
    }

    private String deriveImplType(StepDataBinding dataBinding) {
        String implementationType = null;
        String javaNativeCollectionTypeName = null;
        String javaNativeCollectionImportName = null;
        String javaRecordTypeName = null;
        String javaRecordImportName = null;

        if (hasRecordType(dataBinding)) {
            JavaStepDataRecordType javaRecordType = new JavaStepDataRecordType(dataBinding.getRecordType());
            javaRecordTypeName = javaRecordType.getName();
            javaRecordImportName = javaRecordType.getFullyQualifiedType();
        } else {
            // default record types
            if (isMessaging(dataBinding) || isMultiMessaging(dataBinding)) {
                javaRecordTypeName = TypeManager.getShortType(STRING);
                javaRecordImportName = TypeManager.getFullyQualifiedType(STRING);
            } else if (isNative(dataBinding)) {
                javaRecordTypeName = TypeManager.getShortType(ROW);
                javaRecordImportName = TypeManager.getFullyQualifiedType(ROW);
            }
        }

        if (isNative(dataBinding)) {
            if (dataBinding.getNativeCollectionType() != null) {
                JavaStepDataCollectionType javaNativeCollectionType = new JavaStepDataCollectionType(
                        dataBinding.getNativeCollectionType());
                javaNativeCollectionTypeName = javaNativeCollectionType.getShortType();
                javaNativeCollectionImportName = javaNativeCollectionType.getFullyQualifiedType();
            } else {
                // default native collection type
                javaNativeCollectionTypeName = TypeManager.getShortType(DATASET);
                javaNativeCollectionImportName = TypeManager.getFullyQualifiedType(DATASET);
            }

            addImport(javaNativeCollectionImportName);
        }

        if (StringUtils.isNotBlank(javaNativeCollectionTypeName)) {
            implementationType = javaNativeCollectionTypeName + "<" + javaRecordTypeName + ">";
            addRecordImports(javaRecordImportName);
        } else if (!(isVoid(dataBinding))) {
            implementationType = javaRecordTypeName;
            addRecordImports(javaRecordImportName);
        }

        return implementationType;
    }

    /**
     * Adds imports, but pulls out items that don't need to be specified for a more clean result.
     */
    private void addImport(String importValue) {
        if (JavaElementUtils.isImportNeeded(importValue)) {
            imports.add(importValue);
        }

    }

    private void addPersistImports() {
        JavaPersist javaPersist = (JavaPersist) getPersist();
        if (javaPersist != null) {
            for (String importValue : javaPersist.getImports()) {
                addImport(importValue);
            }
        }
    }

    @Override
    public void validate() {
        super.validate();

        if (hasMessagingInbound() && hasNativeOutbound()) {
            throw new GenerationException("Step '" + getName() + "' uses messaging inbound and native outbound. "
                    + "This combination cannot be used together as it is not possible for a synchronous consumer to be "
                    + "listening for an asynchonrous event without also messaging being used as the outbound type.");
        }
    }

    /**
     * Returns the instantiation type of a step's native collection type.
     * 
     * @return native collection instantiation type
     */
    public String getNativeCollectionInstantiationType() {
        String instantationType = null;
        if (getInbound() != null && getInbound().getNativeCollectionType() != null) {
            DictionaryType dictionaryType = getInbound().getNativeCollectionType().getDictionaryType();
            String simpleType = dictionaryType.getSimpleType();
            String fullyQualifiedType = TypeManager.getFullyQualifiedType(simpleType);
            if (JavaElementUtils.LIST_IMPORT.equals(fullyQualifiedType)) {
                instantationType = "ArrayList";
            } else if (JavaElementUtils.SET_IMPORT.equals(fullyQualifiedType)) {
                instantationType = "HashSet";
            } else {
                instantationType = TypeManager.getShortType(simpleType);
            }
        }

        return instantationType;
    }

    /**
     * Returns the record name in lower camel case format.
     * 
     * @return lower camel case name
     */
    public String getLowerCamelCaseName() {
        return PipelineUtils.deriveLowerCamelNameFromUpperCamelName(getName());
    }

    private void addRecordImports(String importName) {
        String packageName = importName.substring(0, importName.lastIndexOf('.'));
        String className = importName.substring(importName.lastIndexOf('.')+1);
        Record record = metamodelRepository.getRecord(packageName, className);
        imports.add(importName);

        if(record != null) {
            imports.add(record.getPackage() + "." + record.getName() + "Schema");
        }
    }
}
