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

import jakarta.activation.DataHandler;
import jakarta.annotation.PostConstruct;
import jakarta.batch.operations.JobOperator;
import jakarta.data.DataSource;
import jakarta.decorator.Decorator;
import jakarta.ejb.EJB;
import jakarta.el.ExpressionFactory;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.Application;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.jms.ConnectionFactory;
import jakarta.json.Json;
import jakarta.mail.Session;
import jakarta.persistence.EntityManager;
import jakarta.resource.ResourceException;
import jakarta.security.auth.login.LoginContext;
import jakarta.servlet.Servlet;
import jakarta.transaction.TransactionManager;
import jakarta.validation.Validation;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.GET;
import io.smallrye.reactive.messaging.memory.InMemoryConnector; 
import io.smallrye.reactive.messaging.memory.InMemorySink; 

public class allClasses {
    public static void main(String[] args) {
        jakarta.activation.DataHandler dataHandler = new jakarta.activation.DataHandler();
        jakarta.annotation.PostConstruct postConstruct = new jakarta.annotation.PostConstruct() {};
        jakarta.batch.operations.JobOperator jobOperator = jakarta.batch.operations.JobOperator.instance();
        jakarta.data.DataSource dataSource = new jakarta.data.DataSource() {};
        jakarta.decorator.Decorator decorator = new jakarta.decorator.Decorator() {};
        jakarta.ejb.EJB ejb = new jakarta.ejb.EJB() {};
        jakarta.el.ExpressionFactory expressionFactory = jakarta.el.ExpressionFactory.newInstance();
        jakarta.enterprise.inject.spi.CDI<Object> cdi = jakarta.enterprise.inject.spi.CDI.current();
        jakarta.faces.application.Application application = new jakarta.faces.application.Application() {};
        jakarta.inject.Inject inject = new jakarta.inject.Inject() {};
        jakarta.interceptor.Interceptor interceptor = new jakarta.interceptor.Interceptor() {};
        jakarta.jms.ConnectionFactory connectionFactory = new jakarta.jms.ConnectionFactory() {};
        jakarta.json.Json json = jakarta.json.Json.createObjectBuilder().build();
        jakarta.mail.Session mailSession = jakarta.mail.Session.getInstance(System.getProperties());
        jakarta.persistence.EntityManager entityManager = new jakarta.persistence.EntityManager() {};
        jakarta.resource.ResourceException resourceException = new jakarta.resource.ResourceException();
        jakarta.security.auth.login.LoginContext loginContext = new jakarta.security.auth.login.LoginContext("someLoginContext");
        jakarta.servlet.Servlet servlet = new jakarta.servlet.Servlet() {};
        jakarta.transaction.TransactionManager transactionManager = new jakarta.transaction.TransactionManager() {};
        jakarta.validation.Validation validation = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
        jakarta.websocket.server.ServerEndpoint serverEndpoint = new jakarta.websocket.server.ServerEndpoint() {};
        jakarta.ws.rs.GET get = new jakarta.ws.rs.GET() {};
        io.smallrye.reactive.messaging.memory.InMemorySink inMemorySink = new io.smallrye.reactive.messaging.memory.InMemorySink();
        io.smallrye.reactive.messaging.memory.InMemoryConnector inMemoryConnector = new io.smallrye.reactive.messaging.memory.InMemoryConnector();
    }
}
