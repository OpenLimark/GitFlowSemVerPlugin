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

package com.limark.open.gradle.plugins.gitflowsemver.core

import com.limark.open.gradle.plugins.gitflowsemver.config.PluginConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Utility Methods for interacting with Git.
 */
class GitClient {

  private static final Logger log = LoggerFactory.getLogger(this.getClass())

  File projectDir = null
  PluginConfig config = null

  GitClient(File projectDir, PluginConfig config) {
    this.projectDir = projectDir
    this.config = config
  }

  /**
   * Returns the name of the current branch, if one exists. Otherwise returns an empty String.
   * @param projectDir
   * @return
   */
  String getBranchName() {

    def gitBranch = execAndGet("git rev-parse --abbrev-ref HEAD")

    // Note: HEAD is returned when we are not in a branch
    if (!"HEAD".equals(gitBranch)) {
      return gitBranch
    } else {
      return ""
    }
  }

  String describeExactMatch() {
    return execAndGet("git describe --exact-match --match ${config.gitDescribeMatchRule}")
  }


  String describeDirtyMatch() {
    return execAndGet("git describe --dirty --abbrev=7 --match ${config.gitDescribeMatchRule}")
  }

  int getCommitsSince(String branch) {
    def countStr = execAndGet("git rev-list --count ${branch}..HEAD")
    if (countStr.length() > 0) {
      return Integer.parseInt(countStr)
    }
    return 0
  }

  private String execAndGet(String command, failOnError = false) {
    def process = (command).execute(null, projectDir)
    process.waitFor()
    if (process.exitValue() == 0) {
      return process.text.trim()
    } else {
      if (failOnError) {
        throw new IllegalStateException("Execution of command ${command} failed with a " +
            "non-zero exit value: ${process.exitValue()}")
      } else {
        log.debug("Process ${command} returned non-zero exit value ${process.exitValue()}. " +
            "Returning empty String as result since failOnError=false")
        return ""
      }
    }
  }
}