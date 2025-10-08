package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

/**
 * Defines the contract for a record that has a child of another record.
 */
public interface Relation extends NamespacedMetamodel {

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
	 * Returns whether the relation is required.
	 *
	 * @return required
	 */
	Boolean isRequired();

	/**
	 * Returns the column of the relation.
	 *
	 * @return column
	 */
	String getColumn();

	/**
	 * Enumerated values representing multiplicity options.
	 */
	enum Multiplicity {

		ONE_TO_MANY("1-M"), ONE_TO_ONE("1-1"), MANY_TO_ONE("M-1");

		private final String value;

		Multiplicity(String value) {
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

				} else if ((MANY_TO_ONE.toString().equalsIgnoreCase(lowerCasedValue))
						|| ("many-to-one".equals(lowerCasedValue)) || ("many-one".equals(lowerCasedValue))) {
					matchedMultiplicity = MANY_TO_ONE;

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
