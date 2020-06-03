import 'dart:async';

import 'package:flutter/services.dart';

class TencentCosSimple {
  static const MethodChannel _channel =
      const MethodChannel('tencent_cos_simple');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
