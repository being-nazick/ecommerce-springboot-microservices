package com.billing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "spring.cloud.openfeign.client.enabled=false",
    "spring.cloud.discovery.enabled=false"
})
class BillingApplicationTests {

    @Test
    void contextLoads() {
        // This test will only verify that the basic Spring context can be loaded
        // without requiring external dependencies like Feign clients or Eureka
    }
} 