package com.boozallen.aissemble.core.cdi;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
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

import jakarta.enterprise.inject.spi.Extension;
import java.util.List;

/**
 * {@link CdiContext} interface provides a way to compose a cdi context with
 * several different classes and extension depending on what's needed.
 */
public interface CdiContext {

	/**
	 * The classes that could be added to CDI.
	 * 
	 * @return list of CDI classes
	 */
	List<Class<?>> getCdiClasses();

	/**
	 * Extensions that should be added to CDI.
	 * 
	 * @return list of CDI extensions
	 */
	List<Extension> getExtensions();

}
