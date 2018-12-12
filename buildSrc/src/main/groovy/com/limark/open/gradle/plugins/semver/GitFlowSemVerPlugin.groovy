/**********************************************************************************************************************
 * Copyright (C) 2018 Pixglo Inc - All Rights Reserved                                                                *
 *                                                                                                                    *
 * CONFIDENTIAL                                                                                                       *
 *                                                                                                                    *
 * All information contained herein is, and remains the property of Pixglo Inc and its partners,                      *
 * if any.  The intellectual and technical concepts contained herein are proprietary to Pixglo Inc  and its           *
 * partners and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or  *
 * copyright law. Dissemination of this information or reproduction of this material is strictly forbidden unless     *
 * prior written permission is obtained from Pixglo Inc.                                                              *
 *                                                                                                                    *
 **********************************************************************************************************************/

package com.limark.open.gradle.plugins.semver

import com.limark.open.gradle.plugins.semver.config.PluginConfig
import com.limark.open.gradle.plugins.semver.core.GitClient
import com.limark.open.gradle.plugins.semver.core.Versioner
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
    def gitClient = new GitClient(project.projectDir, config)

    def version = new Versioner(gitClient, config).resolve()

    log.info("Resolved version: $version")

    // Update Project Version
    project.version = version.toString()

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
