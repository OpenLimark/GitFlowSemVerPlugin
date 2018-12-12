package com.limark.open.gradle.plugins.gitflowsemver.core.strategies.impl

import com.limark.open.gradle.plugins.gitflowsemver.config.PluginConfig
import com.limark.open.gradle.plugins.gitflowsemver.core.GitClient
import com.limark.open.gradle.plugins.gitflowsemver.core.model.Version
import com.limark.open.gradle.plugins.gitflowsemver.core.strategies.VersioningStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ReleaseBranchVersioningStrategy implements VersioningStrategy {

  private static final Logger log = LoggerFactory.getLogger(this.getClass())

  private GitClient gitClient
  private PluginConfig pluginConfig

  ReleaseBranchVersioningStrategy(GitClient gitClient, PluginConfig pluginConfig) {
    this.gitClient = gitClient
    this.pluginConfig = pluginConfig
  }


  @Override
  boolean supports(String branch) {
    return branch.startsWith("release/")
  }

  @Override
  Version resolve() {
    def releaseBaseVersion = gitClient.getBranchName().tokenize('/')[1]
    def commits = gitClient.getCommitsSince("develop") + 1
    return Version.parse("${releaseBaseVersion}-${pluginConfig.rcLabel}.${commits}")
  }
}
