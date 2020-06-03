import Flutter
import UIKit
import QCloudCOSXML

public class SwiftTencentCosSimplePlugin: NSObject, FlutterPlugin, QCloudSignatureProvider {
    private static var APP_ID: String?
    
    private static var COS_SECRETID: String?
    
    private static var COS_SECRETKEY: String?
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "tencent_cos_simple", binaryMessenger: registrar.messenger())
        let instance = SwiftTencentCosSimplePlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "getPlatformVersion":
            self.getPlatformVersion(call: call, result: result)
            break;
        case "initCos":
            self.initCos(call: call, result: result)
            break;
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    // 一个测试的方法
    public func getPlatformVersion(call: FlutterMethodCall, result: @escaping FlutterResult) {
        result("iOS " + UIDevice.current.systemVersion)
    }
    
    /**
        * 注册默认的COS服务
     */
    public func initCos(call: FlutterMethodCall, result: @escaping FlutterResult) {
        // appId
        if let appId = CommonUtils.getParam(call: call, result: result, param: "appId") as? String,
            // 存储地区
            let regionName = CommonUtils.getParam(call: call, result: result, param: "regionName") as? String,
            let isHttps = CommonUtils.getParam(call: call, result: result, param: "isHttps") as? Bool {
            SwiftTencentCosSimplePlugin.APP_ID = appId;
            let config = QCloudServiceConfiguration.init()
            config.signatureProvider = self
            config.appID = appId;
            let endpoint = QCloudCOSXMLEndPoint.init()
            endpoint.regionName = regionName
            endpoint.useHTTPS = isHttps
            config.endpoint = endpoint
            QCloudCOSXMLService.registerDefaultCOSXML(with: config)
            QCloudCOSTransferMangerService.registerDefaultCOSTransferManger(with: config)
        }
    }
    
    /**
        * 实现 QCloudSignatureProvider 协议
     */
    public func signature(with fileds: QCloudSignatureFields!, request: QCloudBizHTTPRequest!, urlRequest urlRequst: NSMutableURLRequest!, compelete continueBlock: QCloudHTTPAuthentationContinueBlock!) {
        let cre = QCloudCredential.init()
        cre.secretID = SwiftTencentCosSimplePlugin.COS_SECRETID
        cre.secretKey = SwiftTencentCosSimplePlugin.COS_SECRETKEY
        cre.token = "COS_TOKEN"
        /*强烈建议返回服务器时间作为签名的开始时间，用来避免由于用户手机本地时间偏差过大导致的签名不正确 */
        cre.startDate = DateFormatter().date(from: "startTime") // 单位是秒
        cre.experationDate = DateFormatter().date(from: "expiredTime")
        let auth = QCloudAuthentationV5Creator.init(credential: cre)
        let signature = auth?.signature(forData: urlRequst)
        continueBlock(signature, nil)
    }
}
