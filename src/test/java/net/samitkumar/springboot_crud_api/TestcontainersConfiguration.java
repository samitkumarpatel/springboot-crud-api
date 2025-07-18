package net.samitkumar.springboot_crud_api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	MSSQLServerContainer<?> sqlServerContainer() {
		return new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/azure-sql-edge").asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server"))
				.withEnv("ACCEPT_EULA", "Y")
				//.withEnv("SA_PASSWORD", "YourStrong@Passw0rd") // Use a strong password
				.withExposedPorts(1433)
				.withInitScript("schema.sql");
	}

}
