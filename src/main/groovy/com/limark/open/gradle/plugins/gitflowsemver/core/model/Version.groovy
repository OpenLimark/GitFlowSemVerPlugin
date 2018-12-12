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

package com.limark.open.gradle.plugins.gitflowsemver.core.model

class Version {
  int major
  int minor
  int patch
  String preReleasePrefix
  int preRelease
  String buildMetadata

  static Version parse(String originalString) {

    // TODO Use Regex to parse (ex.) // (\d+)\.(\d+)\.(\d+)(-(.*)\.(\d+))?(\+(.*))?

    Version version = new Version()

    def carryOver = originalString

    if (carryOver.contains('+')) {
      def tokens = carryOver.tokenize('+')
      carryOver = tokens[0]
      version.buildMetadata = tokens[1]
    }

    if (carryOver.contains('-')) {
      def tokens = carryOver.tokenize('-')
      carryOver = tokens[0]

      def preTokens = tokens[1].tokenize(".")

      version.preReleasePrefix = preTokens[0]

      if (preTokens.size() > 0 && preTokens[1] != null) {
        version.preRelease = Integer.parseInt(preTokens[1])
      }
    }

    // At this point, carry over contains the base version (x.y.z)
    def baseTokens = carryOver.tokenize('.').collect {
      Integer.parseInt(it)
    }

    version.major = baseTokens[0]
    version.minor = baseTokens[1]
    version.patch = baseTokens[2]

    return version
  }

  Version incrementMajor(boolean resetOthers = true) {
    this.major++
    if (resetOthers) {
      this.minor = 0
      this.patch = 0
    }
    return this
  }

  Version incrementMinor(boolean resetOthers = true) {
    this.minor++
    if (resetOthers) {
      this.patch = 0
    }
    return this
  }

  Version incrementPatch() {
    this.patch++
    return this
  }

  Version incrementPreRelease() {
    this.preRelease++
    return this
  }

  Version dropPreRelease() {
    this.preReleasePrefix = ""
    this.preRelease = 0
    return this
  }

  @Override
  String toString() {

    // Build the base version
    StringBuilder sb = new StringBuilder("${major}.${minor}.${patch}")

    // Add any pre-release data
    if (isNotEmpty(preReleasePrefix) || this.preRelease > 0) {
      sb.append("-")

      if (isNotEmpty(preReleasePrefix)) {
        sb.append("${preReleasePrefix}")
      }

      if (this.preRelease > 0) {
        if (isNotEmpty(preReleasePrefix)) {
          sb.append(".")
        }
        sb.append(preRelease)
      }
    }

    // Add any build metadata
    if (isNotEmpty(buildMetadata)) {
      sb.append("+${buildMetadata}")
    }

    return sb.toString()
  }


  private boolean isNotEmpty(String str) {
    return str != null && str.trim().length() > 0
  }

}
