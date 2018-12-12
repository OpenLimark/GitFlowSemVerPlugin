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

package com.limark.open.gradle.plugins.semver.config

class PluginConfig {

  String alphaLabel = "alpha"
  String rcLabel = "rc"
  String gitDescribeMatchRule = "*[0-9].[0-9]*.[0-9]*"
  Object propertiesFile

}
