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
package com.github.maven_nar.cpptasks.compiler;

import java.io.File;

import org.apache.tools.ant.types.Environment;

import com.github.maven_nar.cpptasks.parser.CobolParser;
import com.github.maven_nar.cpptasks.parser.Parser;

/**
 * An abstract Compiler implementation which uses an external program to
 * perform the compile.
 *
 * @author Adam Murdoch
 */
public abstract class CommandLineCobolCompiler extends CommandLineCompiler {
  protected CommandLineCobolCompiler(final String command, final String identifierArg, final String[] sourceExtensions,
      final String[] headerExtensions, final String outputSuffix, final boolean libtool,
      final CommandLineCobolCompiler libtoolCompiler, final boolean newEnvironment, final Environment env) {
    super(command, identifierArg, sourceExtensions, headerExtensions, outputSuffix, libtool, libtoolCompiler,
        newEnvironment, env);
  }

  @Override
  protected Parser createParser(final File source) {
    return new CobolParser();
  }
}
