package com.github.maven_nar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.ScopeFilter;

/**
 * Generates files .project and .cproject for Eclipse CDT
 * 
 * @execute goal="makedep"
 * @goal nar-make-eclipse-gen
 * @requiresProject
 * @requiresDependencyResolution
 * @author Jeremy Nguyen-Xuan (CERN)
 */
public class NarMakeEclipseFilesGenerationMojo extends AbstractDownloadMojo {
	public void narExecute() {
		
		String artifactId = getMavenProject().getArtifactId();
		
		// .project
		FileWriter fstream;
		try {
			fstream = new FileWriter(getMavenProject().getBasedir().toString() + "/.project");
			BufferedWriter out = new BufferedWriter(fstream);
			StringBuffer projectEclipse = new StringBuffer().append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
					.append("<projectDescription>").append("<name>" + artifactId + "</name>")
					.append("<comment></comment>").append("<projects>").append("</projects>").append("<buildSpec>")
					.append("<buildCommand>").append("<name>org.eclipse.cdt.managedbuilder.core.genmakebuilder</name>")
					.append("<triggers>clean,full,incremental,</triggers>").append("<arguments>")
					.append("<dictionary>").append("<key>?name?</key>").append("<value></value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.append_environment</key>").append("<value>true</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.autoBuildTarget</key>").append("<value>all</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.buildArguments</key>").append("<value>MAVEN_BUILD=true</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.buildCommand</key>").append("<value>make</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.buildLocation</key>")
					.append("<value>${workspace_loc:/" + artifactId + "/}</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.cleanBuildTarget</key>").append("<value>clean</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.contents</key>")
					.append("<value>org.eclipse.cdt.make.core.activeConfigSettings</value>").append("</dictionary>")
					.append("<dictionary>").append("<key>org.eclipse.cdt.make.core.enableAutoBuild</key>")
					.append("<value>false</value>").append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.enableCleanBuild</key>").append("<value>true</value>")
					.append(" </dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.enableFullBuild</key>").append("<value>true</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.fullBuildTarget</key>").append("<value>all</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.stopOnError</key>").append("<value>true</value>")
					.append("</dictionary>").append("<dictionary>")
					.append("<key>org.eclipse.cdt.make.core.useDefaultBuildCmd</key>").append("<value>false</value>")
					.append("</dictionary>").append("</arguments>").append("</buildCommand>").append(" <buildCommand>")
					.append("<name>org.eclipse.cdt.managedbuilder.core.ScannerConfigBuilder</name>")
					.append("<arguments>").append("</arguments>").append("</buildCommand>").append("</buildSpec>")
					.append("<natures>").append("<nature>org.eclipse.cdt.core.ccnature</nature>")
					.append("<nature>org.eclipse.cdt.managedbuilder.core.ScannerConfigNature</nature>")
					.append("<nature>org.eclipse.cdt.managedbuilder.core.managedBuildNature</nature>")
					.append("<nature>org.eclipse.cdt.core.cnature</nature>").append("</natures>")
					.append("</projectDescription>");
			out.write(projectEclipse.toString());
			// Close the output stream
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// .cproject
		try {
			fstream = new FileWriter(getMavenProject().getBasedir().toString() + "/.cproject");
			BufferedWriter out = new BufferedWriter(fstream);
			StringBuffer cprojectEclipse = new StringBuffer()
					.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
					.append("<?fileVersion 4.0.0?>")
					.append("<cproject>")
					.append("<storageModule moduleId=\"org.eclipse.cdt.core.settings\">")
					.append("<cconfiguration id=\"cdt.managedbuild.config.gnu.exe.debug.1398353481\">")
					.append("<storageModule buildSystemId=\"org.eclipse.cdt.managedbuilder.core.configurationDataProvider\" id=\"cdt.managedbuild.config.gnu.exe.debug.1398353481\" moduleId=\"org.eclipse.cdt.core.settings\" name=\"Debug\">")
					.append("<externalSettings/>")
					.append("<extensions>")
					.append("<extension id=\"org.eclipse.cdt.core.ELF\" point=\"org.eclipse.cdt.core.BinaryParser\"/>")
					.append("<extension id=\"org.eclipse.cdt.core.MakeErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>")
					.append("<extension id=\"org.eclipse.cdt.core.GCCErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>")
					.append("<extension id=\"org.eclipse.cdt.core.GASErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>")
					.append("<extension id=\"org.eclipse.cdt.core.GLDErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>")
					.append("</extensions>")
					.append("</storageModule>")
					.append("<storageModule moduleId=\"cdtBuildSystem\" version=\"4.0.0\">")
					.append("<configuration artifactName=\""
							+ artifactId
							+ "\" buildArtefactType=\"org.eclipse.cdt.build.core.buildArtefactType.exe\" buildProperties=\"org.eclipse.cdt.build.core.buildType=org.eclipse.cdt.build.core.buildType.debug,org.eclipse.cdt.build.core.buildArtefactType=org.eclipse.cdt.build.core.buildArtefactType.exe\" cleanCommand=\"rm -rf\" description=\"\" id=\"cdt.managedbuild.config.gnu.exe.debug.1398353481\" name=\"Debug\" parent=\"cdt.managedbuild.config.gnu.exe.debug\">")
					.append("<folderInfo id=\"cdt.managedbuild.config.gnu.exe.debug.1398353481.\" name=\"/\" resourcePath=\"\">")
					.append("<toolChain id=\"cdt.managedbuild.toolchain.gnu.exe.debug.954076630\" name=\"Linux GCC\" superClass=\"cdt.managedbuild.toolchain.gnu.exe.debug\">")
					.append("<targetPlatform id=\"cdt.managedbuild.target.gnu.platform.exe.debug.767520166\" name=\"Debug Platform\" superClass=\"cdt.managedbuild.target.gnu.platform.exe.debug\"/>")
					.append("<builder arguments=\"MAVEN_BUILD=true\" buildPath=\"${workspace_loc:/"
							+ artifactId
							+ "/}\" cleanBuildTarget=\"clean\" command=\"make\" id=\"cdt.managedbuild.target.gnu.builder.exe.debug.1753280378\" incrementalBuildTarget=\"all\" keepEnvironmentInBuildfile=\"false\" managedBuildOn=\"false\" name=\"Gnu Make Builder\" superClass=\"cdt.managedbuild.target.gnu.builder.exe.debug\"/>")
					.append("<tool id=\"cdt.managedbuild.tool.gnu.archiver.base.1780900856\" name=\"GCC Archiver\" superClass=\"cdt.managedbuild.tool.gnu.archiver.base\"/>")
					.append("<tool id=\"cdt.managedbuild.tool.gnu.cpp.compiler.exe.debug.1159943425\" name=\"GCC C++ Compiler\" superClass=\"cdt.managedbuild.tool.gnu.cpp.compiler.exe.debug\">")
					.append("<option id=\"gnu.cpp.compiler.exe.debug.option.optimization.level.1061824025\" name=\"Optimization Level\" superClass=\"gnu.cpp.compiler.exe.debug.option.optimization.level\" value=\"gnu.cpp.compiler.optimization.level.none\" valueType=\"enumerated\"/>")
					.append("<option id=\"gnu.cpp.compiler.exe.debug.option.debugging.level.379328278\" name=\"Debug Level\" superClass=\"gnu.cpp.compiler.exe.debug.option.debugging.level\" value=\"gnu.cpp.compiler.debugging.level.max\" valueType=\"enumerated\"/>")
					.append("<inputType id=\"cdt.managedbuild.tool.gnu.cpp.compiler.input.1020489130\" superClass=\"cdt.managedbuild.tool.gnu.cpp.compiler.input\"/>")
					.append("</tool>")
					.append("<tool id=\"cdt.managedbuild.tool.gnu.c.compiler.exe.debug.1228789912\" name=\"GCC C Compiler\" superClass=\"cdt.managedbuild.tool.gnu.c.compiler.exe.debug\">")
					.append("<option defaultValue=\"gnu.c.optimization.level.none\" id=\"gnu.c.compiler.exe.debug.option.optimization.level.379952342\" name=\"Optimization Level\" superClass=\"gnu.c.compiler.exe.debug.option.optimization.level\" valueType=\"enumerated\"/>")
					.append("<option id=\"gnu.c.compiler.exe.debug.option.debugging.level.210240964\" name=\"Debug Level\" superClass=\"gnu.c.compiler.exe.debug.option.debugging.level\" value=\"gnu.c.debugging.level.max\" valueType=\"enumerated\"/>")
					.append("<inputType id=\"cdt.managedbuild.tool.gnu.c.compiler.input.1094491609\" superClass=\"cdt.managedbuild.tool.gnu.c.compiler.input\"/>")
					.append("</tool>")
					.append("<tool id=\"cdt.managedbuild.tool.gnu.c.linker.exe.debug.1159769567\" name=\"GCC C Linker\" superClass=\"cdt.managedbuild.tool.gnu.c.linker.exe.debug\"/>")
					.append("<tool id=\"cdt.managedbuild.tool.gnu.cpp.linker.exe.debug.829637338\" name=\"GCC C++ Linker\" superClass=\"cdt.managedbuild.tool.gnu.cpp.linker.exe.debug\">")
					.append("<inputType id=\"cdt.managedbuild.tool.gnu.cpp.linker.input.1880134942\" superClass=\"cdt.managedbuild.tool.gnu.cpp.linker.input\">")
					.append("<additionalInput kind=\"additionalinputdependency\" paths=\"$(USER_OBJS)\"/>")
					.append("<additionalInput kind=\"additionalinput\" paths=\"$(LIBS)\"/>")
					.append("</inputType>")
					.append("</tool>")
					.append("<tool id=\"cdt.managedbuild.tool.gnu.assembler.exe.debug.1183478371\" name=\"GCC Assembler\" superClass=\"cdt.managedbuild.tool.gnu.assembler.exe.debug\">")
					.append("<inputType id=\"cdt.managedbuild.tool.gnu.assembler.input.618933710\" superClass=\"cdt.managedbuild.tool.gnu.assembler.input\"/>")
					.append("</tool>")
					.append("</toolChain>")
					.append("</folderInfo>")
					.append("</configuration>")
					.append("</storageModule>")
					.append("<storageModule moduleId=\"scannerConfiguration\">")
					.append("<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"org.eclipse.cdt.make.core.GCCStandardMakePerProjectProfile\"/>")
					.append("<profile id=\"org.eclipse.cdt.make.core.GCCStandardMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.make.core.GCCStandardMakePerFileProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"makefileGenerator\">")
					.append("<runAction arguments=\"-f ${project_name}_scd.mk\" command=\"make\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileCPP\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.cpp\" command=\"g++\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileC\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.c\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfileCPP\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.cpp\" command=\"g++\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfileC\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.c\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.exe.debug.1398353481;cdt.managedbuild.config.gnu.exe.debug.1398353481.;cdt.managedbuild.tool.gnu.cpp.compiler.exe.debug.1159943425;cdt.managedbuild.tool.gnu.cpp.compiler.input.1020489130\">")
					.append("<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileCPP\"/>")
					.append("<profile id=\"org.eclipse.cdt.make.core.GCCStandardMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.make.core.GCCStandardMakePerFileProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"makefileGenerator\">")
					.append("<runAction arguments=\"-f ${project_name}_scd.mk\" command=\"make\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileCPP\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.cpp\" command=\"g++\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileC\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.c\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfileCPP\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.cpp\" command=\"g++\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfileC\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.c\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("</scannerConfigBuildInfo>")
					.append("<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.exe.debug.1398353481;cdt.managedbuild.config.gnu.exe.debug.1398353481.;cdt.managedbuild.tool.gnu.c.compiler.exe.debug.1228789912;cdt.managedbuild.tool.gnu.c.compiler.input.1094491609\">")
					.append("<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileC\"/>")
					.append("<profile id=\"org.eclipse.cdt.make.core.GCCStandardMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.make.core.GCCStandardMakePerFileProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"makefileGenerator\">")
					.append("<runAction arguments=\"-f ${project_name}_scd.mk\" command=\"make\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileCPP\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.cpp\" command=\"g++\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCManagedMakePerProjectProfileC\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.c\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfile\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/${specs_file}\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfileCPP\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.cpp\" command=\"g++\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("<profile id=\"org.eclipse.cdt.managedbuilder.core.GCCWinManagedMakePerProjectProfileC\">")
					.append("<buildOutputProvider>")
					.append("<openAction enabled=\"true\" filePath=\"\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</buildOutputProvider>")
					.append("<scannerInfoProvider id=\"specsFile\">")
					.append("<runAction arguments=\"-E -P -v -dD ${plugin_state_location}/specs.c\" command=\"gcc\" useDefault=\"true\"/>")
					.append("<parser enabled=\"true\"/>")
					.append("</scannerInfoProvider>")
					.append("</profile>")
					.append("</scannerConfigBuildInfo>")
					.append("</storageModule>")
					.append("<storageModule moduleId=\"org.eclipse.cdt.core.externalSettings\"/>")
					.append("<storageModule moduleId=\"org.eclipse.cdt.core.language.mapping\"/>")
					.append("<storageModule moduleId=\"org.eclipse.cdt.internal.ui.text.commentOwnerProjectMappings\"/>")
					.append("<storageModule moduleId=\"org.eclipse.cdt.make.core.buildtargets\">")
					.append("<buildTargets>")
					.append("<target name=\"compile\" path=\"\" targetID=\"org.eclipse.cdt.build.MakeTargetBuilder\">")
					.append("<buildCommand>make</buildCommand>")
					.append("<buildArguments>MAVEN_BUILD=true</buildArguments>")
					.append("<buildTarget>compile</buildTarget>")
					.append("<stopOnError>true</stopOnError>")
					.append("<useDefaultCommand>true</useDefaultCommand>")
					.append("<runAllBuilders>true</runAllBuilders>")
					.append("</target>")
					.append("<target name=\"clean\" path=\"\" targetID=\"org.eclipse.cdt.build.MakeTargetBuilder\">")
					.append("<buildCommand>make</buildCommand>")
					.append("<buildArguments>MAVEN_BUILD=true</buildArguments>")
					.append("<buildTarget>clean</buildTarget>")
					.append("<stopOnError>true</stopOnError>")
					.append("<useDefaultCommand>true</useDefaultCommand>")
					.append("<runAllBuilders>true</runAllBuilders>")
					.append("</target>")
					.append("<target name=\"doc\" path=\"\" targetID=\"org.eclipse.cdt.build.MakeTargetBuilder\">")
					.append("<buildCommand>make</buildCommand>")
					.append("<buildArguments>MAVEN_BUILD=true</buildArguments>")
					.append("<buildTarget>doc</buildTarget>")
					.append("<stopOnError>true</stopOnError>")
					.append("<useDefaultCommand>true</useDefaultCommand>")
					.append("<runAllBuilders>true</runAllBuilders>")
					.append("</target>")
					.append("<target name=\"dist\" path=\"\" targetID=\"org.eclipse.cdt.build.MakeTargetBuilder\">")
					.append("<buildCommand>make</buildCommand>")
					.append("<buildArguments>MAVEN_BUILD=true</buildArguments>")
					.append("<buildTarget>dist</buildTarget>")
					.append("<stopOnError>true</stopOnError>")
					.append("<useDefaultCommand>true</useDefaultCommand>")
					.append("<runAllBuilders>true</runAllBuilders>")
					.append("</target>")
					.append("<target name=\"test\" path=\"\" targetID=\"org.eclipse.cdt.build.MakeTargetBuilder\">")
					.append("<buildCommand>make</buildCommand>")
					.append("<buildArguments>MAVEN_BUILD=true</buildArguments>")
					.append("<buildTarget>test</buildTarget>")
					.append("<stopOnError>true</stopOnError>")
					.append("<useDefaultCommand>true</useDefaultCommand>")
					.append("<runAllBuilders>true</runAllBuilders>")
					.append("</target>")
					.append("<target name=\"run-test\" path=\"\" targetID=\"org.eclipse.cdt.build.MakeTargetBuilder\">")
					.append("<buildCommand>make</buildCommand>")
					.append("<buildArguments>MAVEN_BUILD=true</buildArguments>")
					.append("<buildTarget>run-test</buildTarget>")
					.append("<stopOnError>true</stopOnError>")
					.append("<useDefaultCommand>true</useDefaultCommand>")
					.append("<runAllBuilders>true</runAllBuilders>")
					.append("</target>")
					.append("</buildTargets>")
					.append("</storageModule>")
					.append("</cconfiguration>")
					.append("</storageModule>")
					.append("<storageModule moduleId=\"cdtBuildSystem\" version=\"4.0.0\">")
					.append("<project id=\""
							+ artifactId
							+ ".cdt.managedbuild.target.gnu.exe.1811391375\" name=\"Executable\" projectType=\"cdt.managedbuild.target.gnu.exe\"/>")
					.append("</storageModule>").append("</cproject>");
			out.write(cprojectEclipse.toString());
			// Close the output stream
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * List the dependencies needed for tests compilations, those dependencies are
	 * used to get the include paths needed
	 * for compilation and to get the libraries paths and names needed for
	 * linking.
	 */
	@Override
	protected ScopeFilter getArtifactScopeFilter() {
	  // Was Artifact.SCOPE_TEST  - runtime??
	  return new ScopeFilter( Artifact.SCOPE_TEST, null );
	}
}
