package com.cda.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

@AllArgsConstructor
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
  private String persistenceUnitName;
  private String packageName;
  private ClassLoader classLoader;

  @Override
  public String getPersistenceUnitName() {
    return persistenceUnitName;
  }

  @Override
  public String getPersistenceProviderClassName() {
    return "org.hibernate.jpa.HibernatePersistenceProvider";
  }

  @Override
  public PersistenceUnitTransactionType getTransactionType() {
    return PersistenceUnitTransactionType.RESOURCE_LOCAL;
  }

  @Override
  public DataSource getJtaDataSource() {
    return null;
  }

  @Override
  public DataSource getNonJtaDataSource() {
    return null;
  }

  @Override
  public List<String> getMappingFileNames() {
    return Collections.emptyList();
  }

  @Override
  public List<URL> getJarFileUrls() {
    try {
      return Collections.list(this.getClass().getClassLoader().getResources(""));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public URL getPersistenceUnitRootUrl() {
    return null;
  }

  @Override
  public List<String> getManagedClassNames() {
    return getClasses().stream().map(Class::getSimpleName).collect(Collectors.toList());
  }

  @Override
  public boolean excludeUnlistedClasses() {
    return false;
  }

  @Override
  public SharedCacheMode getSharedCacheMode() {
    return null;
  }

  @Override
  public ValidationMode getValidationMode() {
    return null;
  }

  @Override
  public Properties getProperties() {
    return new Properties();
  }

  @Override
  public String getPersistenceXMLSchemaVersion() {
    return null;
  }

  @Override
  public ClassLoader getClassLoader() {
    return classLoader;
  }

  @Override
  public void addTransformer(ClassTransformer classTransformer) {
    // TODO document why this method is empty
  }

  @Override
  public ClassLoader getNewTempClassLoader() {
    return null;
  }

  private Set<Class<?>> getClasses() {
    Reflections reflections =
        new Reflections(
            new ConfigurationBuilder()
                .forPackage(packageName)
                .setScanners(Scanners.values())
                .addClassLoaders(getClassLoader()));
    return reflections.getTypesAnnotatedWith(Entity.class);
  }

  @Override
  public String toString() {
    return "PersistenceUnitInfoImpl []";
  }
}
