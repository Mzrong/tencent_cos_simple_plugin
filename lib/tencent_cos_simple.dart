import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class TencentCosSimple {
  static const MethodChannel _channel =
      const MethodChannel('tencent_cos_simple');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  // 初始化SDK
  static Future<void> initCos({
    @required String authUrl,
    @required String region,
    bool isHttps = false
  }) async {
    await _channel.invokeMethod("initCos", {
      "authUrl": authUrl,
      "isHttps": isHttps,
      "region": region
    });
  }

  static Future<bool> uploadFile({
    @required String bucket,
    @required String cosPath,
    @required String filePath,
  }) async {
    bool result = await _channel.invokeMethod("uploadFile", {
      "bucket": bucket,
      "cosPath": cosPath,
      "filePath": filePath
    });

    return result;
  }
}
