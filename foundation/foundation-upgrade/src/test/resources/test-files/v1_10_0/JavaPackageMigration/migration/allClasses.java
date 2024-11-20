package test;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.data.DataSource;
import javax.decorator.Decorator;
import javax.ejb.EJB;
import javax.el.ExpressionFactory;
import javax.enterprise.inject.spi.CDI;
import javax.faces.application.Application;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.jms.ConnectionFactory;
import javax.json.Json;
import javax.mail.Session;
import javax.persistence.EntityManager;
import javax.resource.ResourceException;
import javax.security.auth.login.LoginContext;
import javax.servlet.Servlet;
import javax.transaction.TransactionManager;
import javax.validation.Validation;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.GET;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector; 
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink; 

public class allClasses {
    public static void main(String[] args) {
        javax.activation.DataHandler dataHandler = new javax.activation.DataHandler();
        javax.annotation.PostConstruct postConstruct = new javax.annotation.PostConstruct() {};
        javax.batch.operations.JobOperator jobOperator = javax.batch.operations.JobOperator.instance();
        javax.data.DataSource dataSource = new javax.data.DataSource() {};
        javax.decorator.Decorator decorator = new javax.decorator.Decorator() {};
        javax.ejb.EJB ejb = new javax.ejb.EJB() {};
        javax.el.ExpressionFactory expressionFactory = javax.el.ExpressionFactory.newInstance();
        javax.enterprise.inject.spi.CDI<Object> cdi = javax.enterprise.inject.spi.CDI.current();
        javax.faces.application.Application application = new javax.faces.application.Application() {};
        javax.inject.Inject inject = new javax.inject.Inject() {};
        javax.interceptor.Interceptor interceptor = new javax.interceptor.Interceptor() {};
        javax.jms.ConnectionFactory connectionFactory = new javax.jms.ConnectionFactory() {};
        javax.json.Json json = javax.json.Json.createObjectBuilder().build();
        javax.mail.Session mailSession = javax.mail.Session.getInstance(System.getProperties());
        javax.persistence.EntityManager entityManager = new javax.persistence.EntityManager() {};
        javax.resource.ResourceException resourceException = new javax.resource.ResourceException();
        javax.security.auth.login.LoginContext loginContext = new javax.security.auth.login.LoginContext("someLoginContext");
        javax.servlet.Servlet servlet = new javax.servlet.Servlet() {};
        javax.transaction.TransactionManager transactionManager = new javax.transaction.TransactionManager() {};
        javax.validation.Validation validation = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();
        javax.websocket.server.ServerEndpoint serverEndpoint = new javax.websocket.server.ServerEndpoint() {};
        javax.ws.rs.GET get = new javax.ws.rs.GET() {};
        io.smallrye.reactive.messaging.providers.connectors.InMemorySink inMemorySink = new io.smallrye.reactive.messaging.providers.connectors.InMemorySink();
        io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector inMemoryConnector = new io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector();
    }
}
