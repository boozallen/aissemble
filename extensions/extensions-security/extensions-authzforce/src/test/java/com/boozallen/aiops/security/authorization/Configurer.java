package com.boozallen.aiops.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.security.authorization.policy.PolicyRequest;
import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableEntryTransformer;

import java.util.Locale;
import java.util.Map;

public class Configurer implements TypeRegistryConfigurer {

    @Override
    public void configureTypeRegistry(TypeRegistry registry) {

        registry.defineDataTableType(new DataTableType(PolicyRequest.class, new TableEntryTransformer<PolicyRequest>() {
            @Override
            public PolicyRequest transform(Map<String, String> entry) {
                return new PolicyRequest(entry.get("name"),entry.get("resource"),entry.get("action"));
            }
        }));
        registry.defineDataTableType(new DataTableType(TokenDataInput.class, new TableEntryTransformer<TokenDataInput>() {
            @Override
            public TokenDataInput transform(Map<String, String> entry) {
                return new TokenDataInput(entry.get("name"),entry.get("resource"),entry.get("action"),entry.get("result"));
            }
        }));
    }

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

}
