package com.limark.open.gradle.plugins.semver.core.strategies.impl

import com.limark.open.gradle.plugins.semver.config.PluginConfig
import com.limark.open.gradle.plugins.semver.core.GitClient
import com.limark.open.gradle.plugins.semver.core.exceptions.NonComplianceException
import com.limark.open.gradle.plugins.semver.core.model.Version
import com.limark.open.gradle.plugins.semver.core.strategies.VersioningStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HotfixBranchVersioningStrategy implements VersioningStrategy {

  private static final Logger log = LoggerFactory.getLogger(this.getClass())

  private GitClient gitClient
  private PluginConfig pluginConfig

  HotfixBranchVersioningStrategy(GitClient gitClient, PluginConfig pluginConfig) {
    this.gitClient = gitClient
    this.pluginConfig = pluginConfig
  }


  @Override
  boolean supports(String branch) {
    return branch.startsWith("hotfix/")
  }

  @Override
  Version resolve() {
    def hotfixBaseVersion = gitClient.getBranchName().tokenize('/')[1]
    def commits = gitClient.getCommitsSince("master") + 1

    return Version.parse("${hotfixBaseVersion}-${pluginConfig.rcLabel}.${commits}")
  }
}
