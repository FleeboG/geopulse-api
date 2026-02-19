package com.geopulse.geopulse_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = GeopulseApiApplication.class)
class GeopulseApiApplicationTests {

    @Test
    void contextLoads() {}
}
