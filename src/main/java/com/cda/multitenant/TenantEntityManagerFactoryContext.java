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
  private final Map<String, String> tokenByTenantId;

  public TenantEntityManagerFactoryContext() {
    factoriesRepository = new ConcurrentHashMap<>();
    tokenByTenantId = new ConcurrentHashMap<>();
  }

  public void register(String tenantId, String token, EntityManagerFactory factory) {
    if (tenantId == null || factory == null || token == null || !factory.isOpen())
      throw new TenantContextException("There are invalid values on tenant factory registry");
    factoriesRepository.put(token, factory);
    tokenByTenantId.put(tenantId, token);
  }

  public void unregister(String tenantId) {
    if (!tokenByTenantId.containsKey(tenantId)) return;

    String token = tokenByTenantId.remove(tenantId);
    if (!factoriesRepository.containsKey(token)) return;

    EntityManagerFactory factory = factoriesRepository.remove(token);
    if (factory != null && factory.isOpen()) factory.close();
  }

  public EntityManager createEntityManager(String token) {
    return getFactory(token).createEntityManager();
  }

  public Class<?> getClass(String token, String className) {
    EntityManagerFactory factory = getFactory(token);
    Optional<EntityType<?>> findFirst =
        factory.getMetamodel().getEntities().stream()
            .filter(x -> x.getJavaType().getName().equals(className))
            .findFirst();
    if (!findFirst.isPresent())
      throw new TenantContextException(
          "The class \"" + className + "\" was not present in entities context");
    return findFirst.get().getJavaType();
  }

  private EntityManagerFactory getFactory(String token) {
    if (token == null)
      throw new TenantContextException("There are invalid values on tenant factory registry");

    if (!factoriesRepository.containsKey(token))
      throw new TenantContextException("There is no factory registered for this token");
    return factoriesRepository.get(token);
  }
}
