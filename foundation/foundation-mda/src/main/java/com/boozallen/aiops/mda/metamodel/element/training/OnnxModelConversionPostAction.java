package com.boozallen.aiops.mda.metamodel.element.training;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import org.technologybrewery.fermenter.mda.generator.GenerationException;

import com.boozallen.aiops.mda.metamodel.element.PostAction;

/**
 * Decorates {@link PostAction} with model-conversion functionality specific to
 * ONNX model conversion.
 */
public class OnnxModelConversionPostAction extends ModelConversionPostAction {

    // supported onnx conversions
    public static final String SKLEARN = "sklearn";
    public static final String KERAS = "keras";

    // onnx conversion parameters
    private static final String NAME_PARAM = "name";
    private static final String INITIAL_TYPES_PARAM = "initial_types";
    private static final String DOC_STRING_PARAM = "doc_string";
    private static final String TARGET_OPSET_PARAM = "target_opset";
    private static final String CUSTOM_CONVERSION_FUNCTIONS_PARAM = "custom_conversion_functions";
    private static final String CUSTOM_SHAPE_CALCULATORS_PARAM = "custom_shape_calculators";
    private static final String CHANNEL_FIRST_INPUTS_PARAM = "channel_first_inputs";
    private static final String DEFAULT_BATCH_SIZE_PARAM = "default_batch_size";

    // common default values
    private static final String NONE = "None";

    private static final Map<String, String> defaultParameterValues;
    static {
        defaultParameterValues = new HashMap<>();
        defaultParameterValues.put(NAME_PARAM, NONE);
        defaultParameterValues.put(INITIAL_TYPES_PARAM, NONE);
        defaultParameterValues.put(DOC_STRING_PARAM, "''");
        defaultParameterValues.put(TARGET_OPSET_PARAM, NONE);
        defaultParameterValues.put(CUSTOM_CONVERSION_FUNCTIONS_PARAM, NONE);
        defaultParameterValues.put(CUSTOM_SHAPE_CALCULATORS_PARAM, NONE);
        defaultParameterValues.put(CHANNEL_FIRST_INPUTS_PARAM, NONE);
        defaultParameterValues.put(DEFAULT_BATCH_SIZE_PARAM, "1");
    }

    /**
     * {@inheritDoc}
     */
    public OnnxModelConversionPostAction(PostAction postActionToDecorate) {
        super(postActionToDecorate);
    }

    /**
     * Returns the onnx conversion parameters for converting the source model,
     * and whether each parameter is required or not.
     * 
     * @return onnx conversion parameters
     */
    public Map<String, Boolean> getOnnxConversionParameters() {
        Map<String, Boolean> parameters;

        String modelSource = getModelSource();
        if (SKLEARN.equals(modelSource)) {
            parameters = getSklearnParameters();
        } else if (KERAS.equals(modelSource)) {
            parameters = getKerasParameters();
        } else {
            throw new GenerationException(
                    "ONNX conversion for modelSource '" + modelSource + "' is currently not supported!");
        }

        return parameters;
    }

    /**
     * Returns the default value for the given parameter.
     * 
     * @param parameter
     * @return default parameter value
     */
    public String getDefaultParameterValue(String parameter) {
        return defaultParameterValues.get(parameter);
    }

    /**
     * Returns the onnx conversion method.
     * 
     * @return onnx conversion method
     */
    public String getOnnxConversionMethod() {
        return "convert_" + getModelSource();
    }

    private Map<String, Boolean> getSklearnParameters() {
        Map<String, Boolean> sklearnParameters = new HashMap<>();

        sklearnParameters.put(INITIAL_TYPES_PARAM, true);
        sklearnParameters.put(NAME_PARAM, false);
        sklearnParameters.put(DOC_STRING_PARAM, false);
        sklearnParameters.put(TARGET_OPSET_PARAM, false);
        sklearnParameters.put(CUSTOM_CONVERSION_FUNCTIONS_PARAM, false);
        sklearnParameters.put(CUSTOM_SHAPE_CALCULATORS_PARAM, false);

        return sklearnParameters;
    }

    private Map<String, Boolean> getKerasParameters() {
        Map<String, Boolean> kerasParameters = new HashMap<>();

        kerasParameters.put(INITIAL_TYPES_PARAM, false);
        kerasParameters.put(NAME_PARAM, false);
        kerasParameters.put(DOC_STRING_PARAM, false);
        kerasParameters.put(TARGET_OPSET_PARAM, false);
        kerasParameters.put(CUSTOM_CONVERSION_FUNCTIONS_PARAM, false);
        kerasParameters.put(CUSTOM_SHAPE_CALCULATORS_PARAM, false);
        kerasParameters.put(CHANNEL_FIRST_INPUTS_PARAM, false);
        kerasParameters.put(DEFAULT_BATCH_SIZE_PARAM, false);

        return kerasParameters;
    }

}
