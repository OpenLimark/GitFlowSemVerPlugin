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