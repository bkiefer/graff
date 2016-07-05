package de.dfki.lt.loot.util;

import java.io.File;
import java.util.Properties;

public class TestUtil {
  public static File getTestResourceDir() {
    String relativePath = "src/test/resources/";
    Properties sysProps = System.getProperties();
    String basedir = (String) sysProps.get("basedir");
    if (basedir != null) {
      return new File(basedir, relativePath);
    }
    return new File(relativePath);
  }
}
