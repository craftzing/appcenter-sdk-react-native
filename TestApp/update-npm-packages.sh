#!/bin/bash
set -e

echo 'Removing existing appcenter* packages...'
rm -rf node_modules/appcenter*

echo "Packing appcenter* packages..."
npm pack ../appcenter
npm pack ../appcenter-analytics
npm pack ../appcenter-crashes
npm pack ../appcenter-link-scripts

echo "Installing appcenter* packages..."
npm install appcenter*.tgz

echo "workaround for macs on arm64"
npm install react-native-fs --save --legacy-peer-deps

echo "Cleanup appcenter*.tgz"
rm appcenter*.tgz

echo "Installing other packages..."
npm install

# Issue: https://github.com/react-native-community/cli/issues/1403
# Fixed in RN 0.65: https://github.com/facebook/react-native/commit/8721ee0a6b10e5bc8a5a95809aaa7b25dd5a6043
echo "Applying patch to react-native package that fixes build on Xcode 12.5..."
npx patch-package

echo "Running jetify to resolve AndroidX compatibility issues..."
npx jetify

echo "Updating CocoaPods repos..."
pod repo update

echo "Install shared framework pods..."
(cd ../AppCenterReactNativeShared/ios && pod install)

echo "Running pod install and building shared framework..."
(cd ios && pod install)

# workaround for macs on arm64 (uncomment when running on arm64 mac)
# (cd ios && pod install)
