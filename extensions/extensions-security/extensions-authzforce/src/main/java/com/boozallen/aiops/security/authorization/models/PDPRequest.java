package com.boozallen.aiops.security.authorization.models;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

public class PDPRequest {
    private String jwt;
    private String resource;
    private String action;

    public PDPRequest() {
    }

    public PDPRequest(String jwt, String resource, String action) {
        this.jwt = jwt;
        this.resource = resource;
        this.action = action;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
}
