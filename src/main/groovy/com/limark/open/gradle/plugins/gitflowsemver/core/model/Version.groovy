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
