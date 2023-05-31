package com.cda.api;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;

@Getter
public class TenantRegistryModel implements TenantRegistry {
  private String name;
  private String password;
  private String packageName;
  private String file;

  private static final Gson gson = new Gson();

  public TenantRegistryModel(String name, String password, String packageName) {
    this.name = name;
    this.password = password;
    this.packageName = packageName;
    createAndSerializeJarFileFromPackage(packageName);
  }

  private void readAndSerializeJarFile(String jarPath) throws IOException {
    byte[] jarFileBytes = FileUtils.readFileToByteArray(new File(jarPath));
    String base64JarFile = Base64.getEncoder().encodeToString(jarFileBytes);
    this.file = base64JarFile;
  }


  private void createAndSerializeJarFileFromPackage(String packageName) {
    try {
      Path jarPath = Files.createTempFile(UUID.randomUUID().toString(), ".jar");
      String tempFilePath = jarPath.toAbsolutePath().toString();

      Reflections reflections = new Reflections(packageName);
      Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

      try (JarOutputStream jarOutputStream =
          new JarOutputStream(new FileOutputStream(tempFilePath))) {
        for (Class<?> clazz : classes) {
          String className = clazz.getName().replace('.', '/') + ".class";
          JarEntry entry = new JarEntry(className);
          jarOutputStream.putNextEntry(entry);
          jarOutputStream.write(clazz.getResourceAsStream("/" + className).readAllBytes());
          jarOutputStream.closeEntry();
        }
      }
      readAndSerializeJarFile(tempFilePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toJson() {
    return gson.toJson(this);
  }
}
