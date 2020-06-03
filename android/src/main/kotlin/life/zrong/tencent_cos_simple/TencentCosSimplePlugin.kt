package life.zrong.tencent_cos_simple

import android.util.Log
import androidx.annotation.NonNull
import com.tencent.cos.xml.CosXmlServiceConfig
import com.tencent.cos.xml.CosXmlSimpleService
import com.tencent.cos.xml.transfer.COSXMLUploadTask
import com.tencent.cos.xml.transfer.TransferConfig
import com.tencent.cos.xml.transfer.TransferManager
import com.tencent.qcloud.core.auth.QCloudCredentialProvider
import com.tencent.qcloud.core.auth.SessionCredentialProvider
import com.tencent.qcloud.core.http.HttpRequest
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.net.MalformedURLException
import java.net.URL
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.tencent.cos.xml.listener.CosXmlResultListener




/**
 * TencentCosSimplePlugin
 * 腾讯云对象存储插件
 * @author zrong
 * https://www.zrong.life
 */
public class TencentCosSimplePlugin : FlutterPlugin, MethodCallHandler {
    private var TAG = "| TencentCosSimple | Flutter | Android | "

    lateinit var cosXmlService: CosXmlSimpleService

    private var initServiceStatus: Boolean = false

    constructor() {
        instance = this
    }

    constructor(binding: FlutterPlugin.FlutterPluginBinding, methodChannel: MethodChannel) {
        mPluginBinding = binding
        channel = methodChannel
        instance = this
    }

    constructor(mRegistrar: Registrar, mChannel: MethodChannel) {
        channel = mChannel
        registrar = mRegistrar
        instance = this
    }

    companion object {
        lateinit var instance: TencentCosSimplePlugin
        lateinit var mPluginBinding: FlutterPlugin.FlutterPluginBinding
        lateinit var channel: MethodChannel
        lateinit var registrar: Registrar
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "tencent_cos_simple")
            channel.setMethodCallHandler(TencentCosSimplePlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            Extras.FOR_FLUTTER_METHOD_GET_PLATFORM_VERSION -> getPlatformVersion(call, result)
            Extras.FOR_FLUTTER_METHOD_INIT -> initCos(call, result)
            Extras.FOR_FLUTTER_METHOD_UPLOAD_FILE -> uploadFile(call, result)
        }
    }

    private fun getPlatformVersion(call: MethodCall, result: MethodChannel.Result) {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }

    /**
     * 初始化腾讯CosService
     */
    private fun initCos (call: MethodCall, result: MethodChannel.Result) {
        Log.i(TAG, "调用${call.method}方法")
        val map = call.arguments<HashMap<String, Any>>()
        val serviceConfig: CosXmlServiceConfig = CosXmlServiceConfig.Builder()
                .setRegion(map["region"] as String)
                .isHttps(map["isHttps"] as Boolean)
                .builder()

        // auth server的URL
        val url: URL

        try {
            url = URL(map["authUrl"] as String)
        } catch (e: MalformedURLException) {
            Log.i(TAG, "出现错误${e.message}")
            result.error("[error]", "error info:", "${e.message}")
            return
        }

        val credentialProvider: QCloudCredentialProvider = SessionCredentialProvider(HttpRequest.Builder<String>()
                .url(url)
                .method("GET")
                .build())

        val context = if (mPluginBinding == null) registrar.context() else mPluginBinding.applicationContext
        val cosXmlServiceState: CosXmlSimpleService = CosXmlSimpleService(context, serviceConfig, credentialProvider)
        cosXmlService = cosXmlServiceState
        initServiceStatus = true
        Log.i(TAG, "初始化成功")
    }

    /**
     * 上传文件
     */
    private fun uploadFile(call: MethodCall, result: MethodChannel.Result) {
        Log.i(TAG, "调用${call.method}方法")
        if (!initServiceStatus) {
            result.error("3", "service not init", "should init service as first")
        }
        val map = call.arguments<HashMap<String, Any>>()
        val transferConfig: TransferConfig = TransferConfig.Builder()
                .setDividsionForCopy((5 * 1024 * 1024).toLong())        // 是否启用分块复制的最小对象大小
                .setSliceSizeForCopy((5 * 1024 * 1024).toLong())        // 分块复制时的分块大小
                .setDivisionForUpload((2 * 1024 * 1024).toLong())       // 是否启用分块上传的最小对象大小
                .setSliceSizeForUpload((1024 * 1024).toLong())          // 分块上传时的分块大小
                .build()
        val transferManager: TransferManager = TransferManager(cosXmlService, transferConfig)
        val bucket: String = map["bucket"] as String
        val cosPath: String = map["cosPath"] as String
        val filePath: String = map["filePath"] as String
        val uploadId: String = ""
        val cosxmlUploadTask: COSXMLUploadTask = transferManager.upload(bucket, cosPath, filePath, uploadId)

        cosxmlUploadTask.setCosXmlResultListener(object : CosXmlResultListener {
            override fun onSuccess(request: CosXmlRequest, results: CosXmlResult) {
                val cOSXMLUploadTaskResult = results as COSXMLUploadTask.COSXMLUploadTaskResult
                result.success(true)
                Log.i(TAG, "上传成功${cOSXMLUploadTaskResult}")
            }

            override fun onFail(request: CosXmlRequest, exception: CosXmlClientException, serviceException: CosXmlServiceException) {
                Log.i(TAG, exception.message)
                result.success(false)
            }
        })

        // 设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener { complete, target ->
            // todo Do something to update progress...
        }

    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val channel1 = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "tencent_cos_simple")
        channel1.setMethodCallHandler(TencentCosSimplePlugin(flutterPluginBinding, channel1))
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }
}
