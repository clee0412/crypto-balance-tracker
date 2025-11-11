package edu.itba.cryptotracker.integration;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Base class for integration tests.
 * Sets up mock web server for external API calls and provides common test configuration.
 */
@SpringBootTest(classes = edu.itba.cryptotracker.boot.Application.class, 
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    protected MockWebServer mockWebServer;

    @BeforeEach
    void setUpIntegrationTest() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start(65432);
    }

    @AfterEach
    void tearDownIntegrationTest() throws IOException {
        this.mockWebServer.shutdown();
    }
}