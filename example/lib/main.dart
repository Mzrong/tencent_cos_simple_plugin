import 'dart:convert';

import 'package:flutter/material.dart';

import 'package:image_picker/image_picker.dart';
import 'package:tencent_cos_simple/tencent_cos_simple.dart';
import 'package:crypto/crypto.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
  }

  initCos () async {
    await TencentCosSimple.initCos(authUrl: 'http://console.zrong.life/api/auth/getAuthorization', region: 'ap-guangzhou');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: FlatButton(
            child: Text('选择图片'),
            onPressed: () async {
              var image = await ImagePicker().getImage(source: ImageSource.gallery);
              if (image == null) {return;}
              var tmpList = image.path.split('.');
              var imageType = tmpList[tmpList.length - 1];
              var bytes = utf8.encode(image.path);
              var digest = sha1.convert(bytes);

              var result = await TencentCosSimple.uploadFile(bucket: "cdn-1301620727", cosPath: 'app/$digest.$imageType', filePath: image.path);
              print("上传结果$result");
            },
          ),
        ),
        floatingActionButton: FloatingActionButton(
          child: Icon(Icons.add),
          onPressed: () async {
            initCos();
          },
        ),
      ),
    );
  }
}
