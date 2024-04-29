package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.BaseConfigurationItemDecorator;
import org.technologybrewery.fermenter.mda.metamodel.element.ConfigurationItem;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

import com.boozallen.aiops.mda.generator.common.PersistType;

/**
 * Provides baseline decorator functionality for {@link Step}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various
 * implementations of generators (e.g., Java, python, Docker) so that each
 * concrete decorator only has to decorate those aspects of the class that are
 * needed, not all the pass-through methods that each decorator would otherwise
 * need to implement (that add no real value).
 */
public class BaseStepDecorator implements Step {

	protected Step wrapped;

	/**
	 * New decorator for {@link Step}.
	 * 
	 * @param stepToDecorate instance to decorate
	 */
	public BaseStepDecorator(Step stepToDecorate) {
		MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), stepToDecorate);
		wrapped = stepToDecorate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileName() {
		return wrapped.getFileName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return wrapped.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return wrapped.getType();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Alerting getAlerting() {
        return wrapped.getAlerting();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataProfiling getDataProfiling() {
        return wrapped.getDataProfiling();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDataProfilingEnabled() {
		return wrapped.isDataProfilingEnabled();
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    public ModelLineage getModelLineage() {
        return wrapped.getModelLineage();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StepDataBinding getInbound() {
		StepDataBinding wrappedInbound = null;
		if (wrapped.getInbound() != null) {
			wrappedInbound = new BaseStepDataBindingDecorator(wrapped.getInbound());
		}
		return wrappedInbound;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StepDataBinding getOutbound() {
		StepDataBinding wrappedOutbound = null;
		if (wrapped.getOutbound() != null) {
			wrappedOutbound = new BaseStepDataBindingDecorator(wrapped.getOutbound());
		}
		return wrappedOutbound;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persist getPersist() {
		Persist wrappedPersist = null;
		if (wrapped.getPersist() != null) {
			wrappedPersist = new BasePersistDecorator(wrapped.getPersist());
		}
		return wrappedPersist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Provenance getProvenance() {
		Provenance wrappedProvenance = null;
		if (wrapped.getProvenance() != null) {
		    wrappedProvenance = new BaseProvenanceDecorator(wrapped.getProvenance());
		}
		return wrappedProvenance;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PostAction> getPostActions() {
        List<PostAction> wrappedPostActions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(wrapped.getPostActions())) {
            for (PostAction postAction : wrapped.getPostActions()) {
                PostAction wrappedPostAction = new BasePostActionDecorator(postAction);
                wrappedPostActions.add(wrappedPostAction);
            }
        }

        return wrappedPostActions;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ConfigurationItem> getConfiguration() {
		List<ConfigurationItem> wrappedConfigurationItems = new ArrayList<>();
		for (ConfigurationItem configurationItem : wrapped.getConfiguration()) {
			ConfigurationItem wrappedConfigurationItem = new BaseConfigurationItemDecorator(configurationItem);
			wrappedConfigurationItems.add(wrappedConfigurationItem);
		}

		return wrappedConfigurationItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getFileStores() {
		return wrapped.getFileStores();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
		wrapped.validate();
	}

	/**
	 * Returns the step name, capitalized.
	 * 
	 * @return capitalized name
	 */
	public String getCapitalizedName() {
		return StringUtils.capitalize(getName());
	}

    /**
     * Whether or not the step has a persist type of postgres.
     * 
     * @return true if the step has a persist type of postgres.
     */
    public boolean isPersistTypePostgres() {
        return PersistType.hasPersistType(getPersist(), PersistType.POSTGRES);
    }

    /**
     * Whether or not the step has a persist type of rdbms.
     * 
     * @return true if the step has a persist type of rdbms.
     */
    public boolean isPersistTypeRdbms() {
        return PersistType.hasPersistType(getPersist(), PersistType.RDBMS);
    }


    /**
     * Whether or not the step has a persist type of delta-lake.
     * 
     * @return true if the step has a persist type of delta-lake.
     */
    public boolean isPersistTypeDeltaLake() {
        return PersistType.hasPersistType(getPersist(), PersistType.DELTA_LAKE);
    }

    /**
     * Whether or not the step has a persist type of hive.
     * 
     * @return true if the step has a persist type of hive.
     */
    public boolean isPersistTypeHive() {
        return PersistType.hasPersistType(getPersist(), PersistType.HIVE);
    }

    /**
     * Whether or not the step has a persist type of elasticsearch.
     * 
     * @return true if the step has a persist type of elasticsearch.
     */
    public boolean isPersistTypeElasticsearch() {
        return PersistType.hasPersistType(getPersist(), PersistType.ELASTICSEARCH);
    }

    /**
     * Whether or not the step has a persist type of neo4j.
     * 
     * @return true if the step has a persist type of neo4j.
     */
    public boolean isPersistTypeNeo4j() {
        return PersistType.hasPersistType(getPersist(), PersistType.NEO4J);
    }

    /**
     * Whether or not the step has inbound type 'native'.
     * 
     * @return true if the step has inbound type 'native'
     */
    public boolean hasNativeInbound() {
        return isNative(getInbound());
    }

    /**
     * Whether or not the step has outbound type 'native'.
     * 
     * @return true if the step has outbound type 'native'
     */
    public boolean hasNativeOutbound() {
        return isNative(getOutbound());
    }

    /**
     * Whether or not the given step data binding has type 'native'.
     * 
     * @param stepDataBinding
     *            the step data binding to check
     * @return true if the given step data binding has type 'native'
     */
    public boolean isNative(StepDataBinding stepDataBinding) {
        return stepDataBinding != null && "native".equalsIgnoreCase(stepDataBinding.getType());
    }

    /**
     * Whether or not the given step data binding has type 'messaging'.
     * 
     * @param stepDataBinding
     *            the step data binding to check
     * @return true if the given step data binding has type 'messaging'
     */
	public boolean isMessaging(StepDataBinding stepDataBinding) {
		return stepDataBinding != null && ("messaging".equalsIgnoreCase(stepDataBinding.getType()));
	}

	/**
	 * Whether or not the given step data binding has type 'multimessaging'.
	 * @param stepDataBinding the step data binding to check
	 * @return true if the given step data binding has type 'multimessaging'.
	 */
	public boolean isMultiMessaging(StepDataBinding stepDataBinding) {
		return stepDataBinding != null && ("multimessaging".equalsIgnoreCase(stepDataBinding.getType()));
	}

    /**
     * Whether or not the step has inbound type 'messaging'.
     * 
     * @return true if the step has inbound type 'messaging'
     */
	public boolean hasMessagingInbound() {
		return isMessaging(getInbound());
	}

    /**
     * Whether or not the step has outbound type 'messaging'.
     * 
     * @return true if the step has outbound type 'messaging'
     */
	public boolean hasMessagingOutbound() {
		return isMessaging(getOutbound()) || isMultiMessaging(getOutbound());
	}

	/**
	 * Whether or not the step has outbound type 'multimessaging'.
	 *
	 * @return true if the step has outbound type 'multimessaging'.
	 */
	public boolean hasMultiMessagingOutbound() {
		return isMultiMessaging((getOutbound()));
	}

    /**
     * Whether or not the step has a inbound record type defined.
     * 
     * @return true if the step has an inbound record type defined
     */
    public boolean hasInboundRecordType() {
        return hasRecordType(getInbound());
    }

    /**
     * Whether or not the step has a outbound record type defined.
     * 
     * @return true if the step has an outbound record type defined
     */
    public boolean hasOutboundRecordType() {
        return hasRecordType(getOutbound());
    }

    /**
     * Whether or not the given step data binding has a record type defined.
     * 
     * @param stepDataBinding
     *            the step data binding to check
     * @return true if the given step data binding has a record type defined
     */
    public boolean hasRecordType(StepDataBinding stepDataBinding) {
        return stepDataBinding != null && stepDataBinding.getRecordType() != null;
    }

    /**
     * Whether or not the step has an inbound native collection type defined.
     * 
     * @return true if the step has an inbound native collection type defined.
     */
    public boolean hasInboundNativeCollectionType() {
        return getInbound() != null && getInbound().getNativeCollectionType() != null;
    }

	/**
	 * Whether this step produces metadata.
	 *
	 * @return true if metadata is produced
	 */
	public boolean isMetadataEnabled() {
		return isProvenanceEnabled();
	}

	/**
	 * Whether provenance metadata is enabled for this step.
	 *
	 * @return true if provenance is enabled
	 */
	public boolean isProvenanceEnabled() {
		return getProvenance() == null || getProvenance().isEnabled();
	}

	/**
	 * Whether alerting is enabled for this step.
	 *
	 * @return true if alerting is enabled
	 */
	public boolean isAlertingEnabled() {
		return getAlerting() == null || getAlerting().isEnabled();
	}

	/**
	 * Whether model lineage is enabled for this step. Defaults to false.
	 *
	 * @return true if model lineage is enabled
	 */
	public boolean isModelLineageEnabled() {
		return getModelLineage() != null && getModelLineage().isEnabled();
	}

    /**
     * Whether this step has type `asynchronous`.
     * 
     * @return true if this step has type `asynchronous`
     */
    public boolean isAsynchronous() {
        return "asynchronous".equalsIgnoreCase(getType());
    }

	/**
	 * Creates decorated file stores for each file store name
	 *
	 * @return decorated object representation of file stores
	 */
	public List<FileStore> getDecoratedFileStores() {
		return getFileStores() != null ?
				getFileStores().stream()
						.map(FileStoreElement::new)
						.map(BaseFileStoreDecorator::new)
						.collect(Collectors.toList())
				: new ArrayList<>();
	}

	/**
	 * Whether this step has type `generic`.
	 *
	 * @return true if this step has type `generic`
	 */
	public boolean isGeneric() {
		return "generic".equalsIgnoreCase(getType());
	}
}
