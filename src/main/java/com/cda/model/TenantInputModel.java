package com.cda.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class TenantInputModel {
  private String name;
  private String password;
  private String packageName;
  private MultipartFile file;

  public File convertToJarFile() {
    if (file == null || file.isEmpty()) return null;
    File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID());
    try (FileOutputStream fos = new FileOutputStream(convertedFile);
        InputStream is = file.getInputStream()) {
      StreamUtils.copy(is, fos);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return convertedFile;
  }
}
