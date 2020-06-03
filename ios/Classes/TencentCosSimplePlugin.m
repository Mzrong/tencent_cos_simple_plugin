#import "TencentCosSimplePlugin.h"
#if __has_include(<tencent_cos_simple/tencent_cos_simple-Swift.h>)
#import <tencent_cos_simple/tencent_cos_simple-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "tencent_cos_simple-Swift.h"
#endif

@implementation TencentCosSimplePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTencentCosSimplePlugin registerWithRegistrar:registrar];
}
@end
