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
package com.github.maven_nar;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.Project;
import org.codehaus.plexus.util.FileUtils;

import com.github.maven_nar.cpptasks.CUtil;
import com.github.maven_nar.cpptasks.LinkerDef;
import com.github.maven_nar.cpptasks.LinkerEnum;
import com.github.maven_nar.cpptasks.types.LibrarySet;
import com.github.maven_nar.cpptasks.types.LibraryTypeEnum;
import com.github.maven_nar.cpptasks.types.LinkerArgument;
import com.github.maven_nar.cpptasks.types.SystemLibrarySet;

/**
 * Linker tag
 *
 * @author Mark Donszelmann
 */
public class Linker {

  /**
   * The Linker Some choices are: "msvc", "g++", "CC", "icpc", ... Default is
   * Architecture-OS-Linker specific: FIXME:
   * table missing
   */
  @Parameter
  private String name;

  /**
   * Path location of the linker tool
   */
  @Parameter
  private String toolPath;

  /**
   * Enables or disables incremental linking.
   */
  @Parameter(required = true)
  private final boolean incremental = false;

  /**
   * Enables or disables the production of a map file.
   */
  @Parameter(required = true)
  private final boolean map = false;

  /**
   * Options for the linker Defaults to Architecture-OS-Linker specific values.
   * FIXME table missing
   */
  @Parameter
  private List options;

  /**
   * Additional options for the linker when running in the nar-testCompile
   * phase.
   * 
   */
  @Parameter
  private List testOptions;

  /**
   * Options for the linker as a whitespace separated list. Defaults to
   * Architecture-OS-Linker specific values. Will
   * work in combination with &lt;options&gt;.
   */
  @Parameter
  private String optionSet;

  /**
   * Clears default options
   */
  @Parameter(required = true)
  private boolean clearDefaultOptions;

  /**
   * Adds libraries to the linker.
   */
  @Parameter
  private List/* <Lib> */libs;

  /**
   * Adds libraries to the linker. Will work in combination with &lt;libs&gt;.
   * The format is comma separated,
   * colon-delimited values (name:type:dir), like
   * "myLib:shared:/home/me/libs/, otherLib:static:/some/path".
   */
  @Parameter
  private String libSet;

  /**
   * Adds system libraries to the linker.
   */
  @Parameter
  private List/* <SysLib> */sysLibs;

  /**
   * Adds system libraries to the linker. Will work in combination with
   * &lt;sysLibs&gt;. The format is comma
   * separated, colon-delimited values (name:type), like
   * "dl:shared, pthread:shared".
   */
  @Parameter
  private String sysLibSet;

  /**
   * <p>
   * Specifies the link ordering of libraries that come from nar dependencies.
   * The format is a comma separated list of dependency names, given as
   * groupId:artifactId.
   * </p>
   * <p>
   * Example: &lt;narDependencyLibOrder&gt;someGroup:myProduct,
   * other.group:productB&lt;narDependencyLibOrder&gt;
   * </p>
   */
  @Parameter
  private String narDependencyLibOrder;

  private final Log log;

  public Linker() {
    // default constructor for use as TAG
    this(null);
  }

  public Linker(final Log log) {
    this.log = log;
  }

  /**
   * For use with specific named linker.
   * 
   * @param name
   */
  public Linker(final String name, final Log log) {
    this.name = name;
    this.log = log;
  }

  private void addLibraries(final String libraryList, final LinkerDef linker, final Project antProject,
      final boolean isSystem) {

    if (libraryList == null) {
      return;
    }

    final String[] lib = libraryList.split(",");

    for (final String element : lib) {

      final String[] libInfo = element.trim().split(":", 3);

      LibrarySet librarySet = new LibrarySet();

      if (isSystem) {
        librarySet = new SystemLibrarySet();
      }

      librarySet.setProject(antProject);
      librarySet.setLibs(new CUtil.StringArrayBuilder(libInfo[0]));

      if (libInfo.length > 1) {

        final LibraryTypeEnum libType = new LibraryTypeEnum();

        libType.setValue(libInfo[1]);
        librarySet.setType(libType);

        if (!isSystem && libInfo.length > 2) {
          librarySet.setDir(new File(libInfo[2]));
        }
      }

      if (!isSystem) {
        linker.addLibset(librarySet);
      } else {
        linker.addSyslibset((SystemLibrarySet) librarySet);
      }
    }
  }

  public final LinkerDef getLinker(final AbstractCompileMojo mojo, final Project antProject, final String os,
      final String prefix, final String type) throws MojoFailureException, MojoExecutionException {
    if (this.name == null) {
      throw new MojoFailureException("NAR: Please specify a <Name> as part of <Linker>");
    }

    final LinkerDef linker = new LinkerDef();
    linker.setProject(antProject);
    final LinkerEnum linkerEnum = new LinkerEnum();
    linkerEnum.setValue(this.name);
    linker.setName(linkerEnum);

    // tool path
    if (this.toolPath != null) {
      linker.setToolPath(this.toolPath);
    }

    // incremental, map
    linker.setIncremental(this.incremental);
    linker.setMap(this.map);

    // Add definitions (Window only)
    if (os.equals(OS.WINDOWS) && getName(null, null).equals("msvc")
        && (type.equals(Library.SHARED) || type.equals(Library.JNI))) {
      final Set defs = new HashSet();
      try {
        if (mojo.getC() != null) {
          final List cSrcDirs = mojo.getC().getSourceDirectories();
          for (final Iterator i = cSrcDirs.iterator(); i.hasNext();) {
            final File dir = (File) i.next();
            if (dir.exists()) {
              defs.addAll(FileUtils.getFiles(dir, "**/*.def", null));
            }
          }
        }
      } catch (final IOException e) {
      }
      try {
        if (mojo.getCpp() != null) {
          final List cppSrcDirs = mojo.getCpp().getSourceDirectories();
          for (final Iterator i = cppSrcDirs.iterator(); i.hasNext();) {
            final File dir = (File) i.next();
            if (dir.exists()) {
              defs.addAll(FileUtils.getFiles(dir, "**/*.def", null));
            }
          }
        }
      } catch (final IOException e) {
      }
      try {
        if (mojo.getFortran() != null) {
          final List fortranSrcDirs = mojo.getFortran().getSourceDirectories();
          for (final Iterator i = fortranSrcDirs.iterator(); i.hasNext();) {
            final File dir = (File) i.next();
            if (dir.exists()) {
              defs.addAll(FileUtils.getFiles(dir, "**/*.def", null));
            }
          }
        }
      } catch (final IOException e) {
      }

      for (final Iterator i = defs.iterator(); i.hasNext();) {
        final LinkerArgument arg = new LinkerArgument();
        arg.setValue("/def:" + i.next());
        linker.addConfiguredLinkerArg(arg);
      }
    }

    // FIXME, this should be done in CPPTasks at some point, and may not be
    // necessary, but was for VS 2010 beta 2
    if (os.equals(OS.WINDOWS) && getName(null, null).equals("msvc") && !getVersion().startsWith("6.")) {
      final LinkerArgument arg = new LinkerArgument();
      arg.setValue("/MANIFEST");
      linker.addConfiguredLinkerArg(arg);
    }

    // Add options to linker
    if (this.options != null) {
      for (final Iterator i = this.options.iterator(); i.hasNext();) {
        final LinkerArgument arg = new LinkerArgument();
        arg.setValue((String) i.next());
        linker.addConfiguredLinkerArg(arg);
      }
    }

    if (this.optionSet != null) {

      final String[] opts = this.optionSet.split("\\s");

      for (final String opt : opts) {

        final LinkerArgument arg = new LinkerArgument();

        arg.setValue(opt);
        linker.addConfiguredLinkerArg(arg);
      }
    }

    if (!this.clearDefaultOptions) {
      final String option = NarProperties.getInstance(mojo.getMavenProject()).getProperty(prefix + "options");
      if (option != null) {
        final String[] opt = option.split(" ");
        for (final String element : opt) {
          final LinkerArgument arg = new LinkerArgument();
          arg.setValue(element);
          linker.addConfiguredLinkerArg(arg);
        }
      }
    }

    // record the preference for nar dependency library link order
    if (this.narDependencyLibOrder != null) {

      final List libOrder = new LinkedList();

      final String[] lib = this.narDependencyLibOrder.split(",");

      for (final String element : lib) {
        libOrder.add(element.trim());
      }

      mojo.setDependencyLibOrder(libOrder);
    }

    // Add Libraries to linker
    if (this.libs != null || this.libSet != null) {

      if (this.libs != null) {

        for (final Iterator i = this.libs.iterator(); i.hasNext();) {

          final Lib lib = (Lib) i.next();
          lib.addLibSet(mojo, linker, antProject);
        }
      }

      if (this.libSet != null) {
        addLibraries(this.libSet, linker, antProject, false);
      }
    } else {

      final String libsList = NarProperties.getInstance(mojo.getMavenProject()).getProperty(prefix + "libs");

      addLibraries(libsList, linker, antProject, false);
    }

    // Add System Libraries to linker
    if (this.sysLibs != null || this.sysLibSet != null) {

      if (this.sysLibs != null) {

        for (final Iterator i = this.sysLibs.iterator(); i.hasNext();) {

          final SysLib sysLib = (SysLib) i.next();
          linker.addSyslibset(sysLib.getSysLibSet(antProject));
        }
      }

      if (this.sysLibSet != null) {
        addLibraries(this.sysLibSet, linker, antProject, true);
      }
    } else {

      final String sysLibsList = NarProperties.getInstance(mojo.getMavenProject()).getProperty(prefix + "sysLibs");

      addLibraries(sysLibsList, linker, antProject, true);
    }

    return linker;
  }

  public final String getName() {
    return this.name;
  }

  public final String getName(final NarProperties properties, final String prefix)
      throws MojoFailureException, MojoExecutionException {
    if (this.name == null && properties != null && prefix != null) {
      this.name = properties.getProperty(prefix + "linker");
    }
    if (this.name == null) {
      throw new MojoExecutionException("NAR: One of two things may be wrong here:\n\n"
          + "1. <Name> tag is missing inside the <Linker> tag of your NAR configuration\n\n"
          + "2. no linker is defined in the aol.properties file for '" + prefix + "linker'\n");
    }
    return this.name;
  }

  /**
   * @return The standard Linker configuration with 'testOptions' added to the
   *         argument list.
   */
  public final LinkerDef getTestLinker(final AbstractCompileMojo mojo, final Project antProject, final String os,
      final String prefix, final String type) throws MojoFailureException, MojoExecutionException {
    final LinkerDef linker = getLinker(mojo, antProject, os, prefix, type);
    if (this.testOptions != null) {
      for (final Iterator i = this.testOptions.iterator(); i.hasNext();) {
        final LinkerArgument arg = new LinkerArgument();
        arg.setValue((String) i.next());
        linker.addConfiguredLinkerArg(arg);
      }
    }
    return linker;
  }

  public final String getVersion() throws MojoFailureException, MojoExecutionException {
    if (this.name == null) {
      throw new MojoFailureException("Cannot deduce linker version if name is null");
    }

    String version = null;

    final TextStream out = new StringTextStream();
    final TextStream err = new StringTextStream();
    final TextStream dbg = new StringTextStream();

    if (this.name.equals("g++") || this.name.equals("gcc")) {
      NarUtil.runCommand("gcc", new String[] {
        "--version"
      }, null, null, out, err, dbg, this.log);
      final Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+");
      final Matcher m = p.matcher(out.toString());
      if (m.find()) {
        version = m.group(0);
      }
    } else if (this.name.equals("msvc")) {
      NarUtil.runCommand("link", new String[] {
        "/?"
      }, null, null, out, err, dbg, this.log, true);
      final Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+(\\.\\d+)?");
      final Matcher m = p.matcher(out.toString());
      if (m.find()) {
        version = m.group(0);
      }
    } else if (this.name.equals("icc") || this.name.equals("icpc")) {
      NarUtil.runCommand("icc", new String[] {
        "--version"
      }, null, null, out, err, dbg, this.log);
      final Pattern p = Pattern.compile("\\d+\\.\\d+");
      final Matcher m = p.matcher(out.toString());
      if (m.find()) {
        version = m.group(0);
      }
    } else if (this.name.equals("icl")) {
      NarUtil.runCommand("icl", new String[] {
        "/QV"
      }, null, null, out, err, dbg, this.log);
      final Pattern p = Pattern.compile("\\d+\\.\\d+");
      final Matcher m = p.matcher(err.toString());
      if (m.find()) {
        version = m.group(0);
      }
    } else if (this.name.equals("CC")) {
      NarUtil.runCommand("CC", new String[] {
        "-V"
      }, null, null, out, err, dbg, this.log);
      final Pattern p = Pattern.compile("\\d+\\.d+");
      final Matcher m = p.matcher(err.toString());
      if (m.find()) {
        version = m.group(0);
      }
    } else if (this.name.equals("xlC")) {
      NarUtil.runCommand("/usr/vacpp/bin/xlC", new String[] {
        "-qversion"
      }, null, null, out, err, dbg, this.log);
      final Pattern p = Pattern.compile("\\d+\\.\\d+");
      final Matcher m = p.matcher(out.toString());
      if (m.find()) {
        version = m.group(0);
      }
    } else {
      throw new MojoFailureException("Cannot find version number for linker '" + this.name + "'");
    }

    if (version == null) {
      throw new MojoFailureException("Cannot deduce version number from: " + out.toString());
    }
    return version;
  }
}
