package company.tap.tapcardformkit.open.web_wrapper

import TapLocal
import TapTheme
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.webkit.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.*
import cards.pay.paycardsrecognizer.sdk.Card
import com.google.gson.Gson
import company.tap.tapcardformkit.*
import company.tap.tapcardformkit.open.ApplicationLifecycle
import company.tap.tapcardformkit.open.DataConfiguration
import company.tap.tapcardformkit.open.web_wrapper.enums.BenefitStatusDelegate
import company.tap.tapcardformkit.open.web_wrapper.model.ThreeDsResponse
import company.tap.tapcardformkit.open.web_wrapper.threeDsWebView.ThreeDsWebViewActivity
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.*
import java.util.*


@SuppressLint("ViewConstructor")
class TapBenefit : LinearLayout,ApplicationLifecycle {
    lateinit var webViewFrame: FrameLayout
    companion object{
         lateinit var threeDsResponse: ThreeDsResponse

        lateinit var benefitWebView: WebView
        lateinit var benefitConfiguration: KnetConfiguration
        var card:Card?=null

        fun cancel() {
            benefitWebView.loadUrl("javascript:window.cancel()")
        }

        fun retrieve(value:String) {
            benefitWebView.loadUrl("javascript:window.retrieve('$value')")
        }



    }

    /**
     * Simple constructor to use when creating a TapPayCardSwitch from code.
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     **/
    constructor(context: Context) : super(context)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     *
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {
        LayoutInflater.from(context).inflate(R.layout.activity_card_web_wrapper, this)
        initWebView()

    }


     private fun initWebView() {
        benefitWebView = findViewById(R.id.webview)
        webViewFrame = findViewById(R.id.webViewFrame)

         with(benefitWebView.settings){
             javaScriptEnabled=true
             domStorageEnabled=true
             cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

         }
         benefitWebView.setBackgroundColor(Color.TRANSPARENT)
         benefitWebView.setLayerType(LAYER_TYPE_SOFTWARE, null)
         benefitWebView.webViewClient = MyWebViewClient()


     }
     fun init(configuraton: KnetConfiguration) {
         benefitConfiguration = configuraton
         DataConfiguration.addAppLifeCycle(this)
        applyTheme()
        when (configuraton) {
            KnetConfiguration.MapConfigruation -> {
                val url  = "${urlWebStarter}${encodeConfigurationMapToUrl(DataConfiguration.configurationsAsHashMap)}"
                benefitWebView.loadUrl(url)

            }
            else -> {}
        }
    }


    private fun applyTheme() {
        /**
         * need to be refactored : mulitple copies of same code
         */
        when(benefitConfiguration){
            KnetConfiguration.MapConfigruation ->{
                val tapInterface = DataConfiguration.configurationsAsHashMap?.get("interface") as? Map<*, *>
              setTapThemeAndLanguage(
                    this.context,
                    TapLocal.valueOf(tapInterface?.get("locale")?.toString() ?: TapLocal.en.name),
                  TapTheme.valueOf(tapInterface?.get("theme")?.toString() ?: TapTheme.light.name))
            }
            else -> {}
        }


    }

    private fun setTapThemeAndLanguage(context: Context, language: TapLocal?, themeMode: TapTheme?) {
        when (themeMode) {
            TapTheme.light -> {
                DataConfiguration.setTheme(
                    context, context.resources, null,
                    R.raw.defaultlighttheme, TapTheme.light.name
                )
                ThemeManager.currentThemeName = TapTheme.light.name
            }
            TapTheme.dark -> {
                DataConfiguration.setTheme(
                    context, context.resources, null,
                    R.raw.defaultdarktheme, TapTheme.dark.name
                )
                ThemeManager.currentThemeName = TapTheme.dark.name
            }
            else -> {}
        }
        DataConfiguration.setLocale(this.context, language?.name ?:"en", null, this@TapBenefit.context.resources, R.raw.lang)

    }




    inner class MyWebViewClient : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldOverrideUrlLoading(
            webView: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            /**
             * main checker if url start with "tapCardWebSDK://"
             */

            if (request?.url.toString().startsWith(benefitWebPrefix, ignoreCase = true)) {
                Log.e("url",request?.url.toString())
                /**
                 * listen for states of cardWebStatus of onReady , onValidInput .. etc
                 */
                if (request?.url.toString().contains(BenefitStatusDelegate.onReady.name)) {
                    DataConfiguration.getTapKnetListener()?.onReady()
                }

                if (request?.url.toString().contains(BenefitStatusDelegate.onSuccess.name)) {
                    DataConfiguration.getTapKnetListener()?.onSuccess(request?.url?.getQueryParameterFromUri(keyValueName).toString())
                }
                if (request?.url.toString().contains(BenefitStatusDelegate.onChargeCreated.name)) {
                    val data = request?.url?.getQueryParameterFromUri(keyValueName).toString()
                    val gson = Gson()
                    threeDsResponse = gson.fromJson(data, ThreeDsResponse::class.java)
                    navigateTo3dsActivity()
                    DataConfiguration.getTapKnetListener()?.onChargeCreated(request?.url?.getQueryParameterFromUri(keyValueName).toString())
                }

                if (request?.url.toString().contains(BenefitStatusDelegate.onOrderCreated.name)) {
                    DataConfiguration.getTapKnetListener()?.onOrderCreated(request?.url?.getQueryParameter(keyValueName).toString())
                }
                if (request?.url.toString().contains(BenefitStatusDelegate.onClick.name)) {
                    DataConfiguration.getTapKnetListener()?.onClick()

                }
                if (request?.url.toString().contains(BenefitStatusDelegate.cancel.name)) {
                    DataConfiguration.getTapKnetListener()?.cancel()
                }

                if (request?.url.toString().contains(BenefitStatusDelegate.onError.name)) {
                    DataConfiguration.getTapKnetListener()?.onError(request?.url?.getQueryParameterFromUri(keyValueName).toString())
                }

                return true

            }

            return false

        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

        }

        fun navigateTo3dsActivity() {
            // on below line we are creating a new bottom sheet dialog.
            /**
             * put buttomsheet in separate class
             */

            val intent = Intent(context, ThreeDsWebViewActivity::class.java)
            (context).startActivity(intent)


        }



        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Log.e("intercepted mainwebview",request?.url.toString())

            return super.shouldInterceptRequest(view, request)
        }





        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            Log.e("error",error.toString())
            Log.e("error",request.toString())

            super.onReceivedError(view, request, error)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed()
        }
    }

    override fun onEnterForeground() {

    }


    override fun onEnterBackground() {
        Log.e("applifeCycle","onEnterBackground")

    }


}





enum class KnetConfiguration() {
    MapConfigruation
}




