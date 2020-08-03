package com.datastax.examples;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.*;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DataCenterReplication;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;

import com.datastax.examples.SpringDataCassandraApplication;

@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
public class SpringDataCassandraConfiguration extends AbstractCassandraConfiguration {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Value("${astra.secure-connect-bundle:none}")
    private String astraSecureConnectBundle;

    @Value("${DB_USERNAME:cassandra}")
    private String username;

    @Value("${DB_PASSWORD:cassandra}")
    private String password;

    @Override
    protected String getKeyspaceName() {
        return cassandraProperties.getKeyspaceName();
    }

    @Override
    protected String getLocalDataCenter() {
        return cassandraProperties.getLocalDatacenter();
    }

    @Override
    protected int getPort() {
        return cassandraProperties.getPort();
    }

    @Override
    protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
        return new SessionBuilderConfigurer() {
            @Override
            public CqlSessionBuilder configure(CqlSessionBuilder cqlSessionBuilder) {
                if (!astraSecureConnectBundle.equals("none")) {
                    return cqlSessionBuilder
                            .withCloudSecureConnectBundle(Paths.get(astraSecureConnectBundle))
                            .withAuthCredentials(username, password);
                }
                else{
                    return cqlSessionBuilder
                            .addContactPoint(new InetSocketAddress(
                                    cassandraProperties.getContactPoints().get(0),
                                    cassandraProperties.getPort()))
                            .withAuthCredentials(username, password);
                }
            }
        };
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        if (astraSecureConnectBundle.equals("none")) {
            return Arrays.asList(CreateKeyspaceSpecification
                    .createKeyspace(getKeyspaceName())
                    .ifNotExists(true)
                    .withNetworkReplication(DataCenterReplication.of(getLocalDataCenter(), 1))
                    .with(KeyspaceOption.DURABLE_WRITES));
        }
        return Arrays.asList();
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected KeyspacePopulator keyspacePopulator() {
        ResourceKeyspacePopulator keyspacePopulate = new ResourceKeyspacePopulator();
        keyspacePopulate.setSeparator(";");
        keyspacePopulate.setScripts(new ClassPathResource("orders-schema.cql"));
        return keyspacePopulate;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{ SpringDataCassandraApplication.class.getPackageName() + ".order" };
    }

}
