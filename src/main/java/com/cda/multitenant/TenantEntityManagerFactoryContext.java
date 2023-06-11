package com.cda.multitenant;

import com.cda.exceptions.TenantContextException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class TenantEntityManagerFactoryContext {
  private final Map<String, EntityManagerFactory> factoriesRepository;

  public TenantEntityManagerFactoryContext() {
    factoriesRepository = new ConcurrentHashMap<>();
  }

  public void register(String tenantId, EntityManagerFactory factory) {
    if (tenantId == null || factory == null || !factory.isOpen())
      throw new TenantContextException("There are invalid values on tenant factory registry");
    factoriesRepository.put(tenantId, factory);
  }

  public void unregister(String tenantId) {
    if (!factoriesRepository.containsKey(tenantId)) return;

    EntityManagerFactory factory = factoriesRepository.remove(tenantId);
    if (factory != null && factory.isOpen()) factory.close();
  }

  public EntityManager createEntityManager(String tenantId) {
    return getFactory(tenantId).createEntityManager();
  }

  public Class<?> getClass(String tenantId, String className) {
    EntityManagerFactory factory = getFactory(tenantId);
    Optional<EntityType<?>> findFirst =
        factory.getMetamodel().getEntities().stream()
            .filter(x -> x.getClass().getName().equals(className))
            .findFirst();
    if (!findFirst.isPresent())
      throw new TenantContextException(
          "The class \"" + className + "\" was not present in entities context");
    return findFirst.get().getJavaType();
  }

  private EntityManagerFactory getFactory(String tenantId) {
    if (tenantId == null)
      throw new TenantContextException("There are invalid values on tenant factory registry");

    if (!factoriesRepository.containsKey(tenantId))
      throw new TenantContextException("There is no factory registered for this token");
    return factoriesRepository.get(tenantId);
  }
}
