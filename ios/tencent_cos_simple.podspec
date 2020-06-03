#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint tencent_cos_simple.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'tencent_cos_simple'
  s.version          = '0.0.1'
  s.summary          = 'tencent cos simple plugin'
  s.description      = <<-DESC
tencent cos simple plugin.
                       DESC
  s.homepage         = 'https://www.zrong.life'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'zrong' => '1174064549@qq.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '8.0'
  
  s.dependency 'QCloudCOSXML/Transfer'
  
  s.static_framework = true

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
  s.swift_version = '5.0'
end
