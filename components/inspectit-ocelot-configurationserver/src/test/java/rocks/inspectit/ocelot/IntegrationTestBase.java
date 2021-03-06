package rocks.inspectit.ocelot;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rocks.inspectit.ocelot.config.model.InspectitServerSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:userdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create",
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = IntegrationTestBase.Initializer.class)
public class IntegrationTestBase {

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            try {
                Path tempDirectory = Files.createTempDirectory("ocelot");
                FileUtils.forceDeleteOnExit(tempDirectory.toFile());

                TestPropertyValues.of("inspectit-config-server.working-directory=" + tempDirectory.toAbsolutePath().toString())
                        .applyTo(context.getEnvironment());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Autowired
    protected TestRestTemplate rest;

    @Autowired
    protected InspectitServerSettings settings;

    /**
     * Authenticated restTemplate;
     */
    protected TestRestTemplate authRest;

    @BeforeEach
    void setupAuthentication() {
        authRest = rest.withBasicAuth(settings.getDefaultUser().getName(), settings.getDefaultUser().getPassword());
    }

    @AfterEach
    void afterEach() throws IOException {
        FileUtils.cleanDirectory(new File(settings.getWorkingDirectory()));
    }
}
