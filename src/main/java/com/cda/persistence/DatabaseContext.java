package com.cda.persistence;

import javax.persistence.EntityManager;

public interface DatabaseContext extends AutoCloseable {
    EntityManager getEntityManager();

}
