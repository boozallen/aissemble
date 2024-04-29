package com.boozallen.aiops.mda.metamodel.element.python;

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
import com.boozallen.aiops.mda.metamodel.element.BaseStepDecorator;
import com.boozallen.aiops.mda.metamodel.element.Persist;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.StepDataBinding;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.TypeManager;
import org.technologybrewery.fermenter.mda.generator.GenerationException;

import java.util.Set;
import java.util.TreeSet;

/**
 * Step decorator to ease generation of Python files.
 */
public class PythonStep extends BaseStepDecorator {

    protected static final String DATAFRAME_TYPE = "pysparkDataFrame";
    private static final String NONE = "None";
    private static final String STRING = "str";

    private Set<String> imports = new TreeSet<>();

    /**
     * {@inheritDoc}
     */
    public PythonStep(Step stepToDecorate) {
        super(stepToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        if ((hasMessagingInbound() && hasInboundRecordType())
                || (hasMessagingOutbound() && hasOutboundRecordType())) {
            throw new GenerationException("Step '" + getName() + "' uses messaging with a record type. "
                    + "This combination cannot be used together as Python messaging only supports strings. "
                    + "Please remove the inbound and/or outbound record type from this step.");
        }

        if (hasMessagingInbound() && hasNativeOutbound()) {
            throw new GenerationException("Step '" + getName() + "' uses messaging inbound and native outbound. "
                    + "This combination cannot be used together as it is not possible for a synchronous consumer to be "
                    + "listening for an asynchronous event without also messaging being used as the outbound type.");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Persist getPersist() {
        return super.getPersist() != null ? new PythonPersist(super.getPersist()) : null;
    }

    /**
     * Returns the pipeline name formatted into lowercase with underscores
     * (Python naming convention).
     *
     * @return the pipeline name formatted into lowercase with underscores
     */
    public String getLowercaseSnakeCaseName() {
        return PythonElementUtils.getSnakeCaseValue(getName());
    }

    /**
     * Returns the pipeline name formatted into lowercase with hyphens.
     *
     * @return the pipeline name formatted into lowercase with hyphens
     */
    public String getKababCaseName() {
        return PipelineUtils.deriveArtifactIdFromCamelCase(getName());
    }

    /**
     * Returns the base import values needed for this Python step instance.
     *
     * @return imports
     */
    public Set<String> getBaseImports() {
        getBaseSignature();
        addPersistImports();
        return getImportsWithRelativePaths(false);
    }

    /**
     * Returns the implementation import values needed for this Python step
     * instance.
     *
     * @return imports
     */
    public Set<String> getImplImports() {
        getConcreteSignature();

        return getImportsWithRelativePaths(true);
    }

    /**
     * Helper method that replaces any {@link #imports} for this {@link PythonStep} that
     * reference metamodel-defined records and/or dictionary types with import statements
     * that reference the relative paths (instead of absolute paths) of these modules.
     * By using relative (instead of absolute) imports to generated record/dictionary classes,
     * we simplify generation and mitigate the need to generate the encapsulating package
     * path into every import statement in order to form valid absolute imports.
     * <p> For example, the import statement to a metamodel defined record becomes
     * {@code from ..record.custom_record import CustomRecord} instead of
     * {@code from the_package_name.record.custom_record import CustomRecord}.
     *
     * @param isImplModule {@code true} if {@link #imports} are being used in an implementation stub
     *                     (i.e. a developer modifiable class), {@code false} if {@link #imports} are
     *                     being used in a non-modifiable generated class.
     * @return {@link #imports} that have been appropriately modified to include the correct
     * relative import paths for any references to metamodel-define records or dictionary types.
     */
    private Set<String> getImportsWithRelativePaths(boolean isImplModule) {
        Set<String> importsWithRelativePaths = new TreeSet<>();
        for (String moduleImport : imports) {
            if (moduleImport.startsWith("from record.")
                    || moduleImport.startsWith("from dictionary.")) {
                importsWithRelativePaths.add(moduleImport.replace("from ",
                        isImplModule ? "from .." : "from ..."));
            } else {
                importsWithRelativePaths.add(moduleImport);
            }
        }
        return importsWithRelativePaths;
    }

    /**
     * Calculates the base Python method signature. It's labor intensive and
     * messy in velocity, so much more clean to do it in the Python decorator.
     *
     * @return signature
     */
    public String getBaseSignature() {
        String inputType = getInputType(false);
        String outputType = getOutputType(false);
        return createSignature("execute_step", inputType, outputType, isAsynchronous());
    }

    /**
     * Calculates the concrete Python method signature. It's labor intensive and
     * messy in velocity, so much more clean to do it in the Python decorator.
     *
     * @return signature
     */
    public String getConcreteSignature() {
        String inputType = getInputType(true);
        String outputType = getOutputType(true);
        return createSignature("execute_step_impl", inputType, outputType, isAsynchronous());
    }

    /**
     * Calculates the signature for the encryption.
     *
     * @return encryption method signature
     */
    public String getEncryptionSignature() {
        String inputType = getInputType(true);
        String outputType = getOutputType(true);
        return createSignature("check_and_apply_encryption_policy", inputType, outputType, false);
    }

    /**
     * Calculates the signature for the encryption.
     *
     * @return encryption method signature
     */
    public String getApplyEncryptionSignature() {
        String inputType = getInputType(true);
        String outputType = getOutputType(true);

        return createSignatureWithAdditionalParameter("apply_encryption_to_dataset", inputType, inputType,
                false, "fields_to_update: List[str], algorithm: str");
    }

    /**
     * Calculates the signature for get_fields_list.
     *
     * @return get_fields_list method signature
     */
    public String getFieldListSignature() {
        String inputType = getInputType(true);
        if (hasInboundNativeCollectionType() && !hasInboundRecordType()) {
            // The inbound type for this condition is actually Set<DataFrame>, but for
            // this method signature we are only interested in DataFrame
            inputType = "DataFrame";
        }
        String outputType = "List[str]";

        return createSignature("get_fields_list", inputType, outputType, false);
    }

    private String createSignature(String methodName, String inputType, String outputType, boolean asyncMethod) {
        // builds python method signature like so:
        // def method_name(self, inbound: inputType) -> outputType
        StringBuilder builder = new StringBuilder();

        if (asyncMethod) {
            builder.append("async ");
        }

        builder.append("def ");
        builder.append(methodName);
        builder.append("(self");

        if (StringUtils.isNotBlank(inputType)) {
            builder.append(", ");
            builder.append("inbound: ");
            builder.append(inputType);

        }

        builder.append(") ");

        if (StringUtils.isNotBlank(outputType)) {
            builder.append("-> ");
            builder.append(outputType);
        }

        return builder.toString();
    }

    private String createSignatureWithAdditionalParameter(String methodName, String inputType, String outputType, boolean asyncMethod, String additionalParameter) {
        // builds python method signature like so:
        // def method_name(self, inbound: inputType) -> outputType
        StringBuilder builder = new StringBuilder();

        if (asyncMethod) {
            builder.append("async ");
        }

        builder.append("def ");
        builder.append(methodName);
        builder.append("(self");

        if (StringUtils.isNotBlank(inputType)) {
            builder.append(", ");
            builder.append("inbound: ");
            builder.append(inputType);
            if (additionalParameter != null) {
                builder.append(", ");
                builder.append(additionalParameter);
            }
        }

        builder.append(") ");

        if (StringUtils.isNotBlank(outputType)) {
            builder.append("-> ");
            builder.append(outputType);
        }

        return builder.toString();
    }

    private String getInputType(boolean forImplMethod) {
        String inputType = null;

        if (hasNativeInbound()) {
            inputType = deriveNativeType(getInbound());
        } else if (hasMessagingInbound() && forImplMethod) {
            // for inbound messaging, the base method will get the inbound
            // message from kafka, so there is no need for an inputType in the
            // base method signature. the impl method will handle the inbound
            // message, thus the inputType of string here.
            inputType = STRING;
        }

        return inputType;
    }

    private String getOutputType(boolean forImplMethod) {
        String outputType;
        if (hasNativeOutbound()) {
            outputType = deriveNativeType(getOutbound());
        } else if (hasMessagingOutbound() && forImplMethod) {
            // for outbound messaging, the base method will send the outbound
            // message to kafka, so there is no need for an outputType in the
            // base method signature. the impl method will determine what the
            // output message is, thus the outputType of string here.
            outputType = STRING;
        } else {
            outputType = NONE;
        }

        return outputType;
    }

    private String deriveNativeType(StepDataBinding stepDataBinding) {
        String nativeTypeValue = null;

        String recordTypeName;
        String recordTypeImport;
        if (hasRecordType(stepDataBinding)) {
            PythonStepDataRecordType recordType = new PythonStepDataRecordType(stepDataBinding.getRecordType());
            recordTypeName = recordType.getName();
            recordTypeImport = recordType.getFullyQualifiedType();
        } else {
            // default type
            recordTypeName = TypeManager.getShortType(DATAFRAME_TYPE);
            recordTypeImport = TypeManager.getFullyQualifiedType(DATAFRAME_TYPE);
        }

        if (stepDataBinding.getNativeCollectionType() != null) {
            // builds the native type like so:
            // collectionTypeName[recordTypeName]
            PythonStepDataCollectionType collectionType = new PythonStepDataCollectionType(
                    stepDataBinding.getNativeCollectionType());
            String collectionTypeName = collectionType.getShortType();
            String collectionTypeImport = collectionType.getFullyQualifiedType();
            nativeTypeValue = collectionTypeName + "[" + recordTypeName + "]";
            addImport(collectionTypeImport);
        } else {
            nativeTypeValue = recordTypeName;
        }

        addImport(recordTypeImport);

        return nativeTypeValue;
    }

    private void addPersistImports() {
        PythonPersist pythonPersist = (PythonPersist) getPersist();
        if (pythonPersist != null) {
            addImport(pythonPersist.getFullyQualifiedCollectionType());
            addImport(pythonPersist.getFullyQualifiedRecordType());
        }
    }

    private void addImport(String fullyQualifiedType) {
        if (StringUtils.isNotBlank(fullyQualifiedType)) {
            String pythonImport = PythonElementUtils.derivePythonImport(fullyQualifiedType);
            if (StringUtils.isNotBlank(pythonImport)) {
                imports.add(pythonImport);
            }
        }
    }

}
