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
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.maven_nar.cpptasks.CUtil;
import com.github.maven_nar.cpptasks.CompilerDef;
import com.github.maven_nar.cpptasks.CompilerEnum;
import com.github.maven_nar.cpptasks.OptimizationEnum;
import com.github.maven_nar.cpptasks.types.CompilerArgument;
import com.github.maven_nar.cpptasks.types.ConditionalFileSet;
import com.github.maven_nar.cpptasks.types.DefineArgument;
import com.github.maven_nar.cpptasks.types.DefineSet;
import com.github.maven_nar.IncludePath;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract Compiler class
 * 
 * @author Mark Donszelmann
 */
public abstract class Compiler
{

    /**
     * The name of the compiler. Some choices are: "msvc", "g++", "gcc", "CC", "cc", "icc", "icpc", ... Default is
     * Architecture-OS-Linker specific: FIXME: table missing

     */
    @Parameter
    private String name;

    /**
     * Path location of the compile tool
     */
    @Parameter
    private String toolPath;

    /**
     * Source directory for native files
     */
    @Parameter(defaultValue = "${basedir}/src/main", required = true)
    private File sourceDirectory;

    /**
     * Source directory for native test files
     */
    @Parameter(defaultValue = "${basedir}/src/test", required = true)
    private File testSourceDirectory;

    /**
     * Include patterns for sources
     */
    @Parameter(required = true)
    private Set<String> includes = new HashSet<String>();

    /**
     * Exclude patterns for sources
     */
    @Parameter(required = true)
    private Set<String> excludes = new HashSet<String>();

    /**
     * Include patterns for test sources
     */
    @Parameter(required = true)
    private Set<String> testIncludes = new HashSet<String>();

    /**
     * Exclude patterns for test sources
     */
    @Parameter(required = true)
    private Set<String> testExcludes = new HashSet<String>();

    @Parameter(defaultValue = "false", required = false)
    private boolean ccache = false;
    
    /**
     * Compile with debug information.
     */
    @Parameter(required = true)
    private boolean debug = false;

    /**
     * Enables generation of exception handling code.
     */
    @Parameter(defaultValue = "true", required = true)
    private boolean exceptions = true;

    /**
     * Enables run-time type information.
     */
    @Parameter(defaultValue = "true", required = true)
    private boolean rtti = true;

    /**
     * Sets optimization. Possible choices are: "none", "size", "minimal", "speed", "full", "aggressive", "extreme",
     * "unsafe".
     */
    @Parameter(defaultValue = "none", required = true)
    private String optimize = "none";

    /**
     * Enables or disables generation of multi-threaded code. Default value: false, except on Windows.
     */
    @Parameter(required = true)
    private boolean multiThreaded = false;

    /**
     * Defines
     */
    @Parameter
    private List<String> defines;

    /**
     * Defines for the compiler as a comma separated list of name[=value] pairs, where the value is optional. Will work
     * in combination with &lt;defines&gt;.
     */
    @Parameter
    private String defineSet;

    /**
     * Clears default defines
     */
    @Parameter(required = true)
    private boolean clearDefaultDefines;

    /**
     * Undefines
     */
    @Parameter
    private List<String> undefines;

    /**
     * Undefines for the compiler as a comma separated list of name[=value] pairs where the value is optional. Will work
     * in combination with &lt;undefines&gt;.
     */
    @Parameter
    private String undefineSet;

    /**
     * Clears default undefines
     */
    @Parameter
    private boolean clearDefaultUndefines;

    /**
     * Include Paths. Defaults to "${sourceDirectory}/include"
     */
    @Parameter
    private List<IncludePath> includePaths;

    /**
     * Test Include Paths. Defaults to "${testSourceDirectory}/include"
     */
    @Parameter
    private List<IncludePath> testIncludePaths;

    /**
     * System Include Paths, which are added at the end of all include paths
     */
    @Parameter
    private List<String> systemIncludePaths;

    /**
     * Additional options for the C++ compiler Defaults to Architecture-OS-Linker specific values. FIXME table missing
     */
    @Parameter
    private List<String> options;

    /**
     * Additional options for the compiler when running in the nar-testCompile phase.
     */
    @Parameter
    private List<String> testOptions;

    /**
     * Options for the compiler as a whitespace separated list. Will work in combination with &lt;options&gt;.
     */
    @Parameter
    private String optionSet;

    /**
     * Clears default options
     */
    @Parameter(required = true)
    private boolean clearDefaultOptions;

    /**
     * Comma separated list of filenames to compile in order
     */
    @Parameter
    private String compileOrder;

    private AbstractCompileMojo mojo;

    public static final String MAIN = "main";
    public static final String TEST = "test";

    protected Compiler()
    {
    }

    public String getName()
        throws MojoFailureException, MojoExecutionException
    {
        // adjust default values
        if ( name == null )
        {
            name = NarProperties.getInstance(mojo.getMavenProject()).getProperty( getPrefix() + "compiler" );
        }
        return name;
    }

    public final void setAbstractCompileMojo( AbstractCompileMojo mojo )
    {
        this.mojo = mojo;
    }

    public final List<File> getSourceDirectories()
    {
        return getSourceDirectories( "dummy" );
    }

    private List<File> getSourceDirectories( String type )
    {
        List<File> sourceDirectories = new ArrayList<File>();
        File baseDir = mojo.getMavenProject().getBasedir();

        if ( type.equals( TEST ) )
        {
            if ( testSourceDirectory == null )
            {
                testSourceDirectory = new File( baseDir, "/src/test" );
            }
            if ( testSourceDirectory.exists() )
            {
                sourceDirectories.add( testSourceDirectory );
            }

            for ( Iterator i = mojo.getMavenProject().getTestCompileSourceRoots().iterator(); i.hasNext(); )
            {
                File extraTestSourceDirectory = new File( (String) i.next() );
                if ( extraTestSourceDirectory.exists() )
                {
                    sourceDirectories.add( extraTestSourceDirectory );
                }
            }
        }
        else
        {
            if ( sourceDirectory == null )
            {
                sourceDirectory = new File( baseDir, "src/main" );
            }
            if ( sourceDirectory.exists() )
            {
                sourceDirectories.add( sourceDirectory );
            }

            for ( Iterator i = mojo.getMavenProject().getCompileSourceRoots().iterator(); i.hasNext(); )
            {
                File extraSourceDirectory = new File( (String) i.next() );
                if ( extraSourceDirectory.exists() )
                {
                    sourceDirectories.add( extraSourceDirectory );
                }
            }
        }

        if ( mojo.getLog().isDebugEnabled() )
        {
            for ( Iterator<File> i = sourceDirectories.iterator(); i.hasNext(); )
            {
                mojo.getLog().debug( "Added to sourceDirectory: " + i.next().getPath() );
            }
        }
        return sourceDirectories;
    }

    protected final List<IncludePath> getIncludePaths( String type )
    {
        List<IncludePath> includeList = type.equals( TEST ) ? testIncludePaths : includePaths;

        if ( includeList != null && includeList.size() != 0 )
            return includeList;

        includeList = new ArrayList<IncludePath>();
        for ( Iterator<File> i = getSourceDirectories( type ).iterator(); i.hasNext(); )
        {
            //VR 20100318 only add include directories that exist - we now fail the build fast if an include directory does not exist
            File file = new File( (File) i.next(), "include" );
            if ( file.isDirectory() ) {
                IncludePath includePath = new IncludePath();
                includePath.setPath( file.getPath() );
                includeList.add( includePath );
            }
        }
        return includeList;
    }

    public final Set<String> getIncludes()
        throws MojoFailureException, MojoExecutionException
    {
        return getIncludes( "main" );
    }

    protected final Set<String> getIncludes( String type )
        throws MojoFailureException, MojoExecutionException
    {
        Set<String> result = new HashSet<String>();
        if ( !type.equals( TEST ) && !includes.isEmpty() )
        {
            result.addAll( includes );
        }
        else if ( type.equals( TEST ) && !testIncludes.isEmpty() )
        {
            result.addAll( testIncludes );
        }
        else
        {
            String defaultIncludes = NarProperties.getInstance(mojo.getMavenProject()).getProperty( getPrefix() + "includes" );
            if ( defaultIncludes != null )
            {
                String[] include = defaultIncludes.split( " " );
                for ( int i = 0; i < include.length; i++ )
                {
                    result.add( include[i].trim() );
                }
            }
        }
        return result;
    }

    public final Set<String> getExcludes()
        throws MojoFailureException, MojoExecutionException
    {
        return getExcludes( "main" );
    }

    protected final Set<String> getExcludes( String type )
        throws MojoFailureException, MojoExecutionException
    {
        Set<String> result = new HashSet<String>();
        if ( type.equals( TEST ) && !testExcludes.isEmpty() )
        {
            result.addAll( testExcludes );
        }
        else if ( !excludes.isEmpty() )
        {
            result.addAll( excludes );
        }
        else
        {
            String defaultExcludes = NarProperties.getInstance(mojo.getMavenProject()).getProperty( getPrefix() + "excludes" );
            if ( defaultExcludes != null )
            {
                String[] exclude = defaultExcludes.split( " " );
                for ( int i = 0; i < exclude.length; i++ )
                {
                    result.add( exclude[i].trim() );
                }
            }
        }

        return result;
    }

    protected final String getPrefix()
        throws MojoFailureException, MojoExecutionException
    {
        return mojo.getAOL().getKey() + "." + getLanguage() + ".";
    }

    /**
     * @return The standard Compiler configuration with 'testOptions' added to the argument list.
     */
    public final CompilerDef getTestCompiler( String type, String output )
        throws MojoFailureException, MojoExecutionException
    {
        CompilerDef compiler = getCompiler(type, output);
        if ( testOptions != null )
        {
            for ( Iterator<String> i = testOptions.iterator(); i.hasNext(); )
            {
                CompilerArgument arg = new CompilerArgument();
                arg.setValue( i.next() );
                compiler.addConfiguredCompilerArg( arg );
            }
        }
        return compiler;
    }

    /**
     * Generates a new {@link CompilerDef} and populates it give the parameters provided.
     * 
     * @param type  - main or test library - used to determine include and exclude paths.
     * @param output - TODO Not sure..
     * @return {@link CompilerDef} which contains the configuration for this compiler given the type and output.
     * @throws MojoFailureException TODO
     * @throws MojoExecutionException TODO
     */
    public final CompilerDef getCompiler( String type, String output )
        throws MojoFailureException, MojoExecutionException
    {
        String name = getName();
        if (name == null) return null;
        
        CompilerDef compilerDef = new CompilerDef();
        compilerDef.setProject( mojo.getAntProject() );
        CompilerEnum compilerName = new CompilerEnum();
        compilerName.setValue( name );
        compilerDef.setName( compilerName );

        // tool path
        if ( toolPath != null )
        {
            compilerDef.setToolPath( toolPath );
        }

        // debug, exceptions, rtti, multiThreaded
        compilerDef.setCcache( ccache );
        compilerDef.setDebug( debug );
        compilerDef.setExceptions( exceptions );
        compilerDef.setRtti( rtti );
        compilerDef.setMultithreaded( mojo.getOS().equals( "Windows" ) ? true : multiThreaded );

        // optimize
        OptimizationEnum optimization = new OptimizationEnum();
        optimization.setValue( optimize );
        compilerDef.setOptimize( optimization );

        // add options
        if ( options != null )
        {
            for ( Iterator<String> i = options.iterator(); i.hasNext(); )
            {
                CompilerArgument arg = new CompilerArgument();
                arg.setValue( (String) i.next() );
                compilerDef.addConfiguredCompilerArg( arg );
            }
        }

        if ( optionSet != null )
        {

            String[] opts = optionSet.split( "\\s" );

            for ( int i = 0; i < opts.length; i++ )
            {

                CompilerArgument arg = new CompilerArgument();

                arg.setValue( opts[i] );
                compilerDef.addConfiguredCompilerArg( arg );
            }
        }

        compilerDef.setClearDefaultOptions(clearDefaultOptions);
        if ( !clearDefaultOptions )
        {
            String optionsProperty = NarProperties.getInstance(mojo.getMavenProject()).getProperty( getPrefix() + "options" );
            if ( optionsProperty != null )
            {
                String[] option = optionsProperty.split( " " );
                for ( int i = 0; i < option.length; i++ )
                {
                    CompilerArgument arg = new CompilerArgument();
                    arg.setValue( option[i] );
                    compilerDef.addConfiguredCompilerArg( arg );
                }
            }
        }

        // add defines
        if ( defines != null )
        {
            DefineSet ds = new DefineSet();
            for ( Iterator<String> i = defines.iterator(); i.hasNext(); )
            {
                DefineArgument define = new DefineArgument();
                String[] pair = i.next().split( "=", 2 );
                define.setName( pair[0] );
                define.setValue( pair.length > 1 ? pair[1] : null );
                ds.addDefine( define );
            }
            compilerDef.addConfiguredDefineset( ds );
        }

        if ( defineSet != null )
        {

            String[] defList = defineSet.split( "," );
            DefineSet defSet = new DefineSet();

            for ( int i = 0; i < defList.length; i++ )
            {

                String[] pair = defList[i].trim().split( "=", 2 );
                DefineArgument def = new DefineArgument();

                def.setName( pair[0] );
                def.setValue( pair.length > 1 ? pair[1] : null );

                defSet.addDefine( def );
            }

            compilerDef.addConfiguredDefineset( defSet );
        }

        if ( !clearDefaultDefines )
        {
            DefineSet ds = new DefineSet();
            String defaultDefines = NarProperties.getInstance(mojo.getMavenProject()).getProperty( getPrefix() + "defines" );
            if ( defaultDefines != null )
            {
                ds.setDefine( new CUtil.StringArrayBuilder( defaultDefines ) );
            }
            compilerDef.addConfiguredDefineset( ds );
        }

        // add undefines
        if ( undefines != null )
        {
            DefineSet us = new DefineSet();
            for ( Iterator<String> i = undefines.iterator(); i.hasNext(); )
            {
                DefineArgument undefine = new DefineArgument();
                String[] pair = i.next().split( "=", 2 );
                undefine.setName( pair[0] );
                undefine.setValue( pair.length > 1 ? pair[1] : null );
                us.addUndefine( undefine );
            }
            compilerDef.addConfiguredDefineset( us );
        }

        if ( undefineSet != null )
        {

            String[] undefList = undefineSet.split( "," );
            DefineSet undefSet = new DefineSet();

            for ( int i = 0; i < undefList.length; i++ )
            {

                String[] pair = undefList[i].trim().split( "=", 2 );
                DefineArgument undef = new DefineArgument();

                undef.setName( pair[0] );
                undef.setValue( pair.length > 1 ? pair[1] : null );

                undefSet.addUndefine( undef );
            }

            compilerDef.addConfiguredDefineset( undefSet );
        }

        if ( !clearDefaultUndefines )
        {
            DefineSet us = new DefineSet();
            String defaultUndefines = NarProperties.getInstance(mojo.getMavenProject()).getProperty( getPrefix() + "undefines" );
            if ( defaultUndefines != null )
            {
                us.setUndefine( new CUtil.StringArrayBuilder( defaultUndefines ) );
            }
            compilerDef.addConfiguredDefineset( us );
        }

        // add include path
        for ( Iterator<IncludePath> i = getIncludePaths( type ).iterator(); i.hasNext(); )
        {
            IncludePath includePath = i.next();
            // Darren Sargent, 30Jan2008 - fail build if invalid include path(s) specified.
                        if ( ! includePath.exists() ) {
                                throw new MojoFailureException("NAR: Include path not found: " + includePath);
                        }
            compilerDef.createIncludePath().setPath( includePath.getPath() );
        }

        // add system include path (at the end)
        if ( systemIncludePaths != null )
        {
            for ( Iterator<String> i = systemIncludePaths.iterator(); i.hasNext(); )
            {
                String path = i.next();
                compilerDef.createSysIncludePath().setPath( path );
            }
        }

        // Add default fileset (if exists)
        List<File> srcDirs = getSourceDirectories( type );
        Set<String> includeSet = getIncludes( type );
        Set<String> excludeSet = getExcludes( type );

        // now add all but the current test to the excludes
        for ( Iterator i = mojo.getTests().iterator(); i.hasNext(); )
        {
            Test test = (Test) i.next();
            if ( !test.getName().equals( output ) )
            {
                excludeSet.add( "**/" + test.getName() + ".*" );
            }
        }

        for ( Iterator<File> i = srcDirs.iterator(); i.hasNext(); )
        {
            File srcDir = i.next();
            mojo.getLog().debug( "Checking for existence of " + getLanguage() + " source directory: " + srcDir );
            if ( srcDir.exists() )
            {
                if ( compileOrder != null )
                {
                    compilerDef.setOrder( Arrays.asList( StringUtils.split( compileOrder, ", " ) ) );
                }

                ConditionalFileSet fileSet = new ConditionalFileSet();
                fileSet.setProject( mojo.getAntProject() );
                fileSet.setIncludes( StringUtils.join( includeSet.iterator(), "," ) );
                fileSet.setExcludes( StringUtils.join( excludeSet.iterator(), "," ) );
                fileSet.setDir( srcDir );
                compilerDef.addFileset( fileSet );
            }
        }

        return compilerDef;
    }

    protected abstract String getLanguage();

    public final void copyIncludeFiles( MavenProject mavenProject, File targetDirectory )
        throws IOException
    {
        for ( Iterator<IncludePath> i = getIncludePaths( "dummy" ).iterator(); i.hasNext(); )
        {
                IncludePath includePath = i.next();
            if ( includePath.exists() )
            {
                NarUtil.copyDirectoryStructure( includePath.getFile(), targetDirectory, includePath.getIncludes(), NarUtil.DEFAULT_EXCLUDES );
            }
        }
    }
}
