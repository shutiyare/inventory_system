package com.shutiye.inventory_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for Spring Boot application context loading.
 * Verifies that all beans are properly configured and the application can start.
 */
@SpringBootTest
@ActiveProfiles("test")
class InventorySystemApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the Spring application context loads successfully
		// without any configuration errors
	}

}
