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

pipeline {

  agent none

  environment {
    GRADLE_CREDS = credentials('gradle-credentials')
  }

  stages {

    stage('Build') {
      steps {
        sh "./gradlew --no-daemon clean build"
      }
    }

    stage('Confirm-Publish') {
      steps {
        timeout(time: 5, unit: 'DAYS') {
          input message: 'Publish plugin?'
        }
      }
    }

    stage('Publish') {
      when {
        anyOf {
          // Publish only from master
          branch 'master'
        }
      }
      steps {
        milestone()
        sh "./gradlew --no-daemon publishPlugins -Pgradle.publish.key=$GRADLE_CREDS_USR -Pgradle.publish.secret=$GRADLE_CREDS_PSW"
      }
    }
  }
}