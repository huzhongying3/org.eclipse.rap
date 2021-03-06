#!/bin/bash
#
# This script is used to trigger the runtime build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized
#

# Cleanup workspace dir
rm -rf "$WORKSPACE"/*

runtimeDir=/home/build/rap/build-runtime/eclipse-3.4.1
scriptsDir=`dirname $0`

$scriptsDir/build-common.sh \
  --cvs-tag "$CVS_TAG" \
  --build-type "$BUILD_TYPE" \
  --work "$WORKSPACE" \
  --runtime "$runtimeDir" \
  --base-platform "$PLATFORM_DIR" \
  --builder "org.eclipse.rap/releng/org.eclipse.rap.releng/runtime" \
  --output "$WORKSPACE"
