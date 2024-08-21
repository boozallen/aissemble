package com.boozallen.aissemble.alerting.teams;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.teams.models.CardMessage;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@RegisterRestClient
public interface TeamsClient {

    @POST
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    String sendMessage(CardMessage cardMessage);
}
