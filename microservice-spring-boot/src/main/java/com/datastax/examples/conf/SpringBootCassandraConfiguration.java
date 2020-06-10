package com.datastax.examples.conf;

import com.datastax.examples.dao.ProductDao;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.autoconfigure.cassandra.DriverConfigLoaderBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

@Configuration
public class SpringBootCassandraConfiguration {

    @Value("${astra.secure-connect-bundle:none}")
    private String astraSecureConnectBundle;

    @Value("${cassandra.keyspace:betterbotz}")
    private String keyspace;

    @Value("${DB_USERNAME:cassandra}")
    private String username;

    @Value("${DB_PASSWORD:cassandra}")
    private String password;

    @Value("${cassandra.contact-points}")
    private String contactPoints;

    @Value("${cassandra.port:9042}")
    private Integer port;

    @Value("${cassandra.local-datacenter}")
    private String localDataCenter;

    public String getKeyspace() {
        return this.keyspace;
    }

    public String getAstraSecureConnectBundle() {
        return this.astraSecureConnectBundle;
    }

    public String getLocalDataCenter() {
        return this.localDataCenter;
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer() {
        if (!astraSecureConnectBundle.equals("none")) {
            return builder -> builder
                    .withCloudSecureConnectBundle(Paths.get(this.astraSecureConnectBundle))
                    .withAuthCredentials(this.username, this.password);
        }
        else {
            return builder -> builder
                    .addContactPoint(new InetSocketAddress(this.contactPoints, this.port))
                    .withLocalDatacenter(this.localDataCenter)
                    .withAuthCredentials(this.username, this.password);
        }
    }

    @Bean
    public DriverConfigLoaderBuilderCustomizer driverConfigLoaderBuilderCustomizer() {
        if (!astraSecureConnectBundle.equals("none")) {
            return builder -> builder.without(DefaultDriverOption.CONTACT_POINTS);
        }
        return builder -> builder
                .withString(DefaultDriverOption.SESSION_KEYSPACE, this.keyspace);
    }

    @Bean
    public ProductDao productDao(CqlSession session) {
        return new ProductDao(session, keyspace, localDataCenter, astraSecureConnectBundle);
    }
}