package com.boozallen.aiops.mda.metamodel.json;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.FileStore;
import com.boozallen.aiops.mda.metamodel.element.FileStoreElement;
import com.boozallen.aiops.mda.metamodel.element.Framework;
import com.boozallen.aiops.mda.metamodel.element.FrameworkElement;
import org.technologybrewery.fermenter.mda.metamodel.element.ConfigurationItem;
import org.technologybrewery.fermenter.mda.metamodel.element.ConfigurationItemElement;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import com.boozallen.aiops.mda.metamodel.element.Alerting;
import com.boozallen.aiops.mda.metamodel.element.AlertingElement;
import com.boozallen.aiops.mda.metamodel.element.Composite;
import com.boozallen.aiops.mda.metamodel.element.CompositeElement;
import com.boozallen.aiops.mda.metamodel.element.Dictionary;
import com.boozallen.aiops.mda.metamodel.element.DictionaryElement;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.DictionaryTypeElement;
import com.boozallen.aiops.mda.metamodel.element.CompositeField;
import com.boozallen.aiops.mda.metamodel.element.CompositeFieldElement;
import com.boozallen.aiops.mda.metamodel.element.DataAccess;
import com.boozallen.aiops.mda.metamodel.element.DataAccessElement;
import com.boozallen.aiops.mda.metamodel.element.DataProfiling;
import com.boozallen.aiops.mda.metamodel.element.DataProfilingElement;
import com.boozallen.aiops.mda.metamodel.element.ModelLineage;
import com.boozallen.aiops.mda.metamodel.element.ModelLineageElement;
import com.boozallen.aiops.mda.metamodel.element.Persist;
import com.boozallen.aiops.mda.metamodel.element.PersistElement;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.PipelineElement;
import com.boozallen.aiops.mda.metamodel.element.PipelineType;
import com.boozallen.aiops.mda.metamodel.element.PipelineTypeElement;
import com.boozallen.aiops.mda.metamodel.element.Platform;
import com.boozallen.aiops.mda.metamodel.element.PlatformElement;
import com.boozallen.aiops.mda.metamodel.element.PostAction;
import com.boozallen.aiops.mda.metamodel.element.PostActionElement;
import com.boozallen.aiops.mda.metamodel.element.Provenance;
import com.boozallen.aiops.mda.metamodel.element.ProvenanceElement;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordElement;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldElement;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldTypeElement;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.StepDataBinding;
import com.boozallen.aiops.mda.metamodel.element.StepDataBindingElement;
import com.boozallen.aiops.mda.metamodel.element.StepDataCollectionType;
import com.boozallen.aiops.mda.metamodel.element.StepDataCollectionTypeElement;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordTypeElement;
import com.boozallen.aiops.mda.metamodel.element.StepElement;
import com.boozallen.aiops.mda.metamodel.element.Validation;
import com.boozallen.aiops.mda.metamodel.element.ValidationElement;
import com.boozallen.aiops.mda.metamodel.element.Versioning;
import com.boozallen.aiops.mda.metamodel.element.VersioningElement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Configures Json marshalling and unmarshalling to leverage metemodel classes.
 */
public final class AiopsMdaJsonUtils {

    private AiopsMdaJsonUtils() {
        // prevent local instantiation of all static class
    }

    /**
     * Configures a new {@link ObjectMapper} to use metamodels.
     */
    public static void configureCustomObjectMappper() {
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Pipeline.class, PipelineElement.class);
        module.addAbstractTypeMapping(PipelineType.class, PipelineTypeElement.class);
        module.addAbstractTypeMapping(Versioning.class, VersioningElement.class);
        module.addAbstractTypeMapping(Step.class, StepElement.class);
        module.addAbstractTypeMapping(StepDataBinding.class, StepDataBindingElement.class);
        module.addAbstractTypeMapping(StepDataCollectionType.class, StepDataCollectionTypeElement.class);
        module.addAbstractTypeMapping(StepDataRecordType.class, StepDataRecordTypeElement.class);
        module.addAbstractTypeMapping(Persist.class, PersistElement.class);
        module.addAbstractTypeMapping(Provenance.class, ProvenanceElement.class);
        module.addAbstractTypeMapping(Alerting.class, AlertingElement.class);
        module.addAbstractTypeMapping(ConfigurationItem.class, ConfigurationItemElement.class);
        module.addAbstractTypeMapping(DataProfiling.class, DataProfilingElement.class);
        module.addAbstractTypeMapping(PostAction.class, PostActionElement.class);
        module.addAbstractTypeMapping(FileStore.class, FileStoreElement.class);
        module.addAbstractTypeMapping(Platform.class, PlatformElement.class);
        module.addAbstractTypeMapping(ModelLineage.class, ModelLineageElement.class);

        module.addAbstractTypeMapping(Dictionary.class, DictionaryElement.class);
        module.addAbstractTypeMapping(DictionaryType.class, DictionaryTypeElement.class);
        module.addAbstractTypeMapping(Validation.class, ValidationElement.class);

        module.addAbstractTypeMapping(Record.class, RecordElement.class);
        module.addAbstractTypeMapping(RecordField.class, RecordFieldElement.class);
        module.addAbstractTypeMapping(RecordFieldType.class, RecordFieldTypeElement.class);
        module.addAbstractTypeMapping(DataAccess.class, DataAccessElement.class);
        module.addAbstractTypeMapping(Framework.class, FrameworkElement.class);

        module.addAbstractTypeMapping(Composite.class, CompositeElement.class);
        module.addAbstractTypeMapping(CompositeField.class, CompositeFieldElement.class);

        ObjectMapper localMapper = new ObjectMapper();
        localMapper.registerModule(module);

        JsonUtils.setObjectMapper(localMapper);
    }

}
