/*
 * Copyright 2019-2022 Jörg Steffen, Bernd Kiefer
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * ​https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
