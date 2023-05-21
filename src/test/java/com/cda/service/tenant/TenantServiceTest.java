package com.cda.service.tenant;

import com.cda.service.BaseServiceTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class TenantServiceTest extends BaseServiceTest {

  @Test
  public void shouldCreateEntityManagerFactoryForTenant() {
    String urlPrefix = "jdbc:mysql://localhost:3306/";
    TenantService tenantService =
        new TenantService(
            buildMockRepositoryFacotry(),
            buildTransactionHandler(),
            buildMockEncryptionService(),
            null,
            null,
            null
            );
  }
}
