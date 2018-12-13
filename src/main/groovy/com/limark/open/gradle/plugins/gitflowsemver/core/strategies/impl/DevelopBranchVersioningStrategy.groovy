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

package com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl

import com.limark.open.gradle.plugins.gitflowsemver.config.PluginConfig
import com.limark.open.gradle.plugins.gitflowsemver.core.GitClient
import com.limark.open.gradle.plugins.gitflowsemver.core.exceptions.NonComplianceException
import com.limark.open.gradle.plugins.gitflowsemver.core.model.Version
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.VersioningStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DevelopBranchVersioningStrategy implements VersioningStrategy {

  private static final Logger log = LoggerFactory.getLogger(this.getClass())

  GitClient gitClient
  PluginConfig pluginConfig

  DevelopBranchVersioningStrategy(GitClient gitClient, PluginConfig pluginConfig) {
    this.gitClient = gitClient
    this.pluginConfig = pluginConfig
  }


  @Override
  boolean supports(String branch) {
    return branch == "develop"
  }

  @Override
  Version resolve() {
    def gitOutput = sanitizeGitOutput(gitClient.describeDirtyMatch())


    def baseVersion = getBaseVersion(gitOutput)
    def commits = getCommits(gitOutput)

    return Version.parse("${baseVersion}-${pluginConfig.alphaLabel}.${commits}").incrementMinor(true)
  }

  protected String sanitizeGitOutput(String gitOutput) {
    if ("".equals(gitOutput)) {
      log.error("Compliance error - Unable to determine develop version as git describe did not return expected value")
      throw new NonComplianceException("Unable to determine develop version as git describe did not return expected value")
    }

    if (gitOutput.endsWith("-dirty")) {
      // Strip off the dirty indicator
      gitOutput = gitOutput.substring(0, gitOutput.length() - "-dirty".length())
    }

    return gitOutput
  }

  protected String getBaseVersion(gitOutput) {
    return (gitOutput =~ /-[0-9]+-g[0-9a-f]+$/).replaceFirst("")
  }

  protected String getCommits(gitOutput) {
    def gitSuffixMatcher = (gitOutput =~ /-([0-9]+)-g([0-9a-f]+)$/)
    return Integer.parseInt(gitSuffixMatcher[0][1].toString())
  }
}
