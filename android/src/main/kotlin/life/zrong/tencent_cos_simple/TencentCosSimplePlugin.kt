package life.zrong.tencent_cos_simple

import android.util.Log
import androidx.annotation.NonNull
import com.tencent.cos.xml.CosXmlServiceConfig
import com.tencent.cos.xml.CosXmlSimpleService
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

/**
 * TencentCosSimplePlugin
 * 腾讯云对象存储插件
 * @author zrong
 * https://www.zrong.life
 */
public class TencentCosSimplePlugin : FlutterPlugin, MethodCallHandler {
    private var TAG = "| TencentCosSimple | Flutter | Android | "

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
            Extras.FOR_FLUTTER_METHOD_INIT -> init(call, result)
        }
    }

    private fun getPlatformVersion(call: MethodCall, result: MethodChannel.Result) {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }

    /**
     * 初始化腾讯CosService
     */
    private fun init (call: MethodCall, result: MethodChannel.Result) {
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
        var cosXmlService: CosXmlSimpleService = CosXmlSimpleService(context, serviceConfig, credentialProvider)
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "tencent_cos_simple")
        channel.setMethodCallHandler(TencentCosSimplePlugin());
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }
}
