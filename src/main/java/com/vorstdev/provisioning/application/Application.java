package com.vorstdev.provisioning.application;

import com.google.common.collect.Sets;
import com.vorstdev.provisioning.application.configs.LdapSettings;
import com.vorstdev.provisioning.connector.ConnectorLoader;
import com.vorstdev.provisioning.connector.domain.ConnectorDefinition;
import org.apache.commons.lang3.RandomStringUtils;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

/**
 * Created by ernstvorsteveld on 17/06/16.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    private ConnectorLoader connectorLoader = new ConnectorLoader();

    @Autowired
    private LdapSettings ldapSettings;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && args[0].equals("exit code")) {
            throw new RuntimeException("could not start", null);
        }
        ConnectorDefinition connectorDefinition = expectConnectorBundleVersionDefinition(args);
        ConnectorFacade connectorFacade = connectorLoader.load(connectorDefinition);
        ObjectClass objectClass = ObjectClass.ACCOUNT;
        Set<Attribute> attributes = expectAttributeSet(ldapSettings);
        OperationOptions operationOptions = new OperationOptions(null);
        connectorFacade.create(objectClass, attributes, operationOptions);

    }

    private Set<Attribute> expectAttributeSet(LdapSettings ldapSettings) {
        Set<Attribute> attributes = Sets.newHashSet();
        for(String name : ldapSettings.getAttributeNames()) {
            Attribute attribute = AttributeBuilder.build(name, RandomStringUtils.randomAlphanumeric(10));
            attributes.add(attribute);
        }
        return attributes;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    private ConnectorDefinition expectConnectorBundleVersionDefinition(String ...args) {
        ConnectorDefinition definition = expectConnectorBundleNameDefinition();
        definition.setVersion(args[2]);
        return definition;
    }

    private ConnectorDefinition expectConnectorBundleNameDefinition(String ...args) {
        ConnectorDefinition definition = expectConnectorUriDefinition();
        definition.setBundleName(args[1]);
        return definition;
    }

    private ConnectorDefinition expectConnectorUriDefinition(String ...args) {
        ConnectorDefinition definition = new ConnectorDefinition();
        definition.setLocation(args[0]);
        return definition;
    }
}
