package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.Validatable;
//import org.technologybrewery.fermenter.mda.metamodel.element.Field;
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

/**
 * Defines the contract for a record that has a child of another record.
 */
public interface Relation extends NamespacedMetamodel {

	/**
	 * Returns the type package of relation.
	 *
	 * @return relation type package
	 */
//	String getPackage();

	/**
	 * Returns the type of relation.
	 *
	 * @return relation type
	 */
//	String getName();

	/**
	 * Returns relation-level documentation.
	 *
	 * @return relation documentation
	 */
	String getDocumentation();

	/**
	 * Returns multiplicity of this relation (e.g., 1-M, 1-1, M-M).
	 *
	 * @return multiplicity
	 */
	Multiplicity getMultiplicity();

	/**
	 * Enumerated values representing multiplicity options.
	 */
	public enum Multiplicity {

		ONE_TO_MANY("1-M"), ONE_TO_ONE("1-1"), MANY_TO_MANY("M-M");

		private String value;

		private Multiplicity(String value) {
			this.value = value;
		}

		/**
		 * Returns the instance for the passed multiplicity value (ignoring case). If no
		 * known match is found, null is returned.
		 *
		 * @param value string representation
		 * @return instance
		 */
		public static Multiplicity fromString(String value) {
			Multiplicity matchedMultiplicity = null;

			if (StringUtils.isNotBlank(value)) {
				String lowerCasedValue = value.toLowerCase();
				if ((ONE_TO_MANY.toString().equalsIgnoreCase(lowerCasedValue))
						|| ("one-to-many".equals(lowerCasedValue)) || ("one-many".equals(lowerCasedValue))) {
					matchedMultiplicity = ONE_TO_MANY;

				} else if ((ONE_TO_ONE.toString().equalsIgnoreCase(lowerCasedValue))
						|| ("one-to-one".equals(lowerCasedValue)) || ("one-one".equals(lowerCasedValue))) {
					matchedMultiplicity = ONE_TO_ONE;

				} else if ((MANY_TO_MANY.toString().equalsIgnoreCase(lowerCasedValue))
						|| ("many-to-many".equals(lowerCasedValue)) || ("many-many".equals(lowerCasedValue))) {
					matchedMultiplicity = MANY_TO_MANY;

				}
			}

			return matchedMultiplicity;
		}

		/**
		 * {@inheritDoc}
		 */
		@JsonValue
		@Override
		public String toString() {
			return value;
		}

		/**
		 * A comma-separated list of valid options.
		 *
		 * @return valid options
		 */
		public static String options() {
			StringBuilder sb = new StringBuilder();
			boolean isFirst = true;
			for (Multiplicity mode : values()) {
				if (!isFirst) {
					sb.append(", ");
				}

				sb.append(mode.toString());

				isFirst = false;
			}

			return sb.toString();
		}

	}
}
