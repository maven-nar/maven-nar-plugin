/*
 * #%L
 * Native ARchive plugin for Maven
 * %%
 * Copyright (C) 2002 - 2014 NAR Maven Plugin developers.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.maven_nar.cpptasks.msvc;

import java.util.Vector;

/**
 * A add-in class for Microsoft Developer Studio processors
 *
 * 
 */
public class MsvcProcessor {
  public static void addWarningSwitch(final Vector<String> args, final int level) {
    switch (level) {
      case 0:
        args.addElement("/W0");
        break;
      case 1:
        args.addElement("/W1");
        break;
      case 2:
        break;
      case 3:
        args.addElement("/W3");
        break;
      case 4:
        args.addElement("/W4");
        break;
      case 5:
        args.addElement("/WX");
        break;
      default:
      	break;
    }
  }

  public static String getCommandFileSwitch(final String cmdFile) {
    final StringBuffer buf = new StringBuffer("@");
    if (cmdFile.indexOf(' ') >= 0) {
      buf.append('\"');
      buf.append(cmdFile.replace('/', '\\'));
      buf.append('\"');
    } else {
      buf.append(cmdFile);
    }
    return buf.toString();
  }

  public static void getDefineSwitch(final StringBuffer buffer, final String define, final String value) {
    buffer.append("/D");
    buffer.append(define);
    if (value != null && value.length() > 0) {
      buffer.append('=');
      buffer.append(value);
    }
  }

  public static String getIncludeDirSwitch(final String includeDir) {
    return "/I" + includeDir.replace('/', '\\');
  }

  public static String[] getOutputFileSwitch(final String outPath) {
    final StringBuffer buf = new StringBuffer("/Fo");
    if (outPath.indexOf(' ') >= 0) {
      buf.append('\"');
      buf.append(outPath);
      buf.append('\"');
    } else {
      buf.append(outPath);
    }
    final String[] retval = new String[] {
      buf.toString()
    };
    return retval;
  }

  public static void getUndefineSwitch(final StringBuffer buffer, final String define) {
    buffer.append("/U");
    buffer.append(define);
  }

  public static boolean isCaseSensitive() {
    return false;
  }

  private MsvcProcessor() {
  }
}
