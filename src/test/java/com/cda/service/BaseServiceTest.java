package com.cda.service;

import static org.mockito.Mockito.*;

import com.cda.persistence.TransactionHandler;
import com.cda.repository.RepositoryFactory;
import com.cda.repository.tenant.TenantRepository;
import com.cda.utils.EncryptionService;
import com.cda.utils.functional.ThrowableSupplier;

public abstract class BaseServiceTest {

  protected EncryptionService buildMockEncryptionService() {
    EncryptionService encryptionService = mock(EncryptionService.class);
    when(encryptionService.decrypt(anyString(), anyString(), anyString()))
        .thenAnswer(arg -> arg.getArgument(0, String.class));
    when(encryptionService.encrypt(anyString(), anyString(), anyString()))
        .thenAnswer(arg -> arg.getArgument(0, String.class));
    return encryptionService;
  }

  protected RepositoryFactory buildMockRepositoryFacotry() {
    RepositoryFactory repositoryFactory = mock(RepositoryFactory.class);
    when(repositoryFactory.buildTenantRepository()).thenReturn(mock(TenantRepository.class));
    return repositoryFactory;
  }

  protected TransactionHandler buildTransactionHandler() {
    TransactionHandler transactionHandler = mock(TransactionHandler.class);
    try {
      when(transactionHandler.encapsulateTransaction(any()))
          .thenAnswer(
              arg -> {
                try {
                  return arg.getArgument(0, ThrowableSupplier.class).get();
                } catch (Exception e) {
                  return null;
                }
              });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return transactionHandler;
  }
}
