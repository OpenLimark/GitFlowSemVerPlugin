/**********************************************************************************************************************
 * Copyright (c) 2018 Limark Technologies (Pvt) Ltd.                                                                  *
 *                                                                                                                    *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated      *
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation   *
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and  *
 *  to permit persons to whom the Software is furnished to do so, subject to the following conditions:                *
 *                                                                                                                    *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of   *
 * the Software.                                                                                                      *
 *                                                                                                                    *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO   *
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL         *
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF      *
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE                               *
 **********************************************************************************************************************/

package com.limark.open.gradle.plugins.gitflowsemver

import com.limark.open.gradle.plugins.gitflowsemver.config.PluginConfig
import com.limark.open.gradle.plugins.gitflowsemver.core.GitClient
import com.limark.open.gradle.plugins.gitflowsemver.core.Versioner
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.WriteProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * GitFlow SemVer Plugin.
 */
class GitFlowSemVerPlugin implements Plugin<Project> {

  final Logger log = LoggerFactory.getLogger(this.getClass())

  @Override
  void apply(Project project) {

    def config = initConfig(project)
    def gitClient

    def version

    project.afterEvaluate {
      gitClient = new GitClient(project.projectDir, config)
      version = new Versioner(gitClient, config).resolve()

      log.info("Resolved version: $version")

      // Update Project Version
      project.version = version.toString()
    }

    // Add Tasks
    project.tasks.create('printVersion') {
      doLast {
        println project.version
      }
    }

    project.tasks.create('writeVersion', WriteProperties) {
      properties = buildVersionProps(version.toString())
      outputFile = config.propertiesFile
    }
  }

  private PluginConfig initConfig(Project project) {
    def config = project.extensions.create('versionConfig', PluginConfig)
    if (config.propertiesFile == null) {
      config.propertiesFile = "${project.buildDir}/version.properties"
    }
    return config
  }

  private Properties buildVersionProps(String version) {
    def props = new Properties()
    props.put("version", version)
    return props
  }
}
