package com.cda.persistence;

import com.cda.utils.functional.ThrowableRunnable;
import com.cda.utils.functional.ThrowableSupplier;
import javax.persistence.EntityTransaction;
import org.hibernate.TransactionException;

public class TransactionHandler {
  private final DatabaseContext databaseContext;
  private EntityTransaction currentTransaction = null;

  public TransactionHandler(DatabaseContext databaseContext) {
    this.databaseContext = databaseContext;
  }

  public void encapsulateTransaction(ThrowableRunnable callback) throws Exception {
    EntityTransaction transaction = getTransaction();
    try {
      if (!transaction.isActive()) transaction.begin();
      callback.run();
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      throw e;
    } finally {
    }
  }

  public <T> T encapsulateTransaction(ThrowableSupplier<T> callback) throws Exception {
    EntityTransaction transaction = getTransaction();
    try {
      if (!transaction.isActive()) transaction.begin();
      T result = callback.get();
      transaction.commit();
      return result;
    } catch (Exception e) {
      transaction.rollback();
      throw e;
    } finally {
    }
  }

  public void flush() {
    if (currentTransaction == null || !currentTransaction.isActive())
      throw new TransactionException(
          "The transaction should be initiated before \"flush()\" method call");
    currentTransaction.commit();
    databaseContext.getEntityManager().flush();
  }

  private EntityTransaction getTransaction() {
    if (currentTransaction == null)
      currentTransaction = databaseContext.getEntityManager().getTransaction();
    return currentTransaction;
  }
}
