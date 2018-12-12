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

  private GitClient gitClient
  private PluginConfig pluginConfig

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
    def gitOutput = gitClient.describeDirtyMatch()

    if ("".equals(gitOutput)) {
      log.error("Compliance error - Unable to determine develop version as git describe did not return expected value")
      throw new NonComplianceException("Unable to determine develop version as git describe did not return expected value")
    }

    if (gitOutput.endsWith("-dirty")) {
      // Strip off the dirty indicator
      gitOutput = gitOutput.substring(0, gitOutput.length() - "-dirty".length())
    }

    def baseVersion = (gitOutput =~ /-[0-9]+-g[0-9a-f]+$/).replaceFirst("")

    // We get the commit count in the develop branch to figure out the pre-release number
    def gitSuffixMatcher = (gitOutput =~ /-([0-9]+)-g([0-9a-f]+)$/)
    def commits = Integer.parseInt(gitSuffixMatcher[0][1].toString())

    return Version.parse("${baseVersion}-${pluginConfig.alphaLabel}.${commits}")
        .incrementMinor(true)
  }
}
