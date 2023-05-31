package com.cda.model;

import com.cda.api.TenantRegistry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class TenantInputModel implements TenantRegistry {
  private String name;
  private String password;
  private String packageName;
  private String file;

  public File convertToJarFile() {
    try {
      if (file == null || file.isEmpty()) return null;
      return fromBase64ToFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File fromBase64ToFile() throws IOException {
    try {
      byte[] jarBytes = Base64.getDecoder().decode(file);
      Path jarPath = Files.createTempFile(UUID.randomUUID().toString(), ".jar");
      String tempJarFilePath = jarPath.toAbsolutePath().toString();

      try (FileOutputStream fos = new FileOutputStream(tempJarFilePath)) {
        fos.write(jarBytes);
        log.info("Base64 string converted to JAR file successfully.\nFile: " + tempJarFilePath);
      }

      return new File(tempJarFilePath);
    } catch (IllegalArgumentException e) {
      log.error("Invalid Base64 string", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getName() {
    return name == null ? null : name.toLowerCase();
  }

  @Override
  public String toJson() {
    return "";
  }
}
