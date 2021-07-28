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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

/** Convenience method when reading from (possibly) compressed files */
public class FileUtil {

  public static InputStream getInputStream(File file)
      throws FileNotFoundException, IOException {
    return file.getName().endsWith(".gz")
        ? new GZIPInputStream(new FileInputStream(file))
        : new FileInputStream(file);
  }

  /** Return a new Reader that is fed from a (probably gzip compressed)
   *  file. The method automatically selects the correct input stream.
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static Reader getReader(File filename)
      throws FileNotFoundException, IOException {
    return new InputStreamReader(getInputStream(filename));
  }

  /** Return a new Reader that is fed from a (probably gzip compressed)
   *  file. The method automatically selects the correct input stream.
   *  With the second argument, an encoding for the reader can be specified.
   *  to getReader
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static Reader getReader(File file, String encoding)
      throws FileNotFoundException, IOException {
    return new InputStreamReader(getInputStream(file), encoding);
  }

  /** remove everything including and beyond the last '.' from fileName*/
  public static String stripFileExtension(String fileName) {
    int dotPos = fileName.lastIndexOf('.');
    String diffName =
      (dotPos == -1) ? fileName : fileName.substring(0, dotPos);
    return diffName;
  }

  /** similar to basename in the unix shell: return everything after the last
   *  slash
   * @param fileName
   * @return
   */
  public static String baseName(String fileName) {
    return new File(fileName).getName();
  }

  /** Return an iterator that iterates over all files in the given list. If one
   *  of the strings points to a directory, the files in the directory are
   *  returned one after the other.
   *
   *  Currently, that does only recurse one level deep.
   *  TODO: include full recursion (optionally).
   */
  public static Iterable<File> findAllFiles(final List<String> names) {
    return new Iterable<File>() {
      public Iterator<File> iterator() {
        return new Iterator<File>() {
          private int dirIndex = 0;
          private File[] dirList = null;
          private File next = null;
          private Iterator<String> currentName = names.iterator();

          private File getNextFromDir() {
            File the_next;
            do {
              the_next = dirList[dirIndex++];
            } while (dirIndex < dirList.length && ! the_next.isFile());
            if (dirIndex >= dirList.length)
              dirList = null;
            return (the_next.isFile() ? the_next : null);
          }

          @Override
          public boolean hasNext() {
            next = (dirList != null) ? getNextFromDir() : null;
            while (next == null && currentName.hasNext()) {
              String name = currentName.next();
              next = new File(name);
              if (next.isDirectory()) {
                dirList = next.listFiles();
                dirIndex = 0;
                next = getNextFromDir();
              }
            }
            return (next != null);
          }

          @Override
          public File next() {
            File result = next;
            next = null;
            return result;
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
}
