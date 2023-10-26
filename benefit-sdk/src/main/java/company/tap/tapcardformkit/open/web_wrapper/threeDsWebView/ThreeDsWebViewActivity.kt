package company.tap.tapcardformkit.open.web_wrapper.threeDsWebView

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.tap.tapcardformkit.R
import company.tap.tapcardformkit.doAfterSpecificTime
import company.tap.tapcardformkit.getDeviceSpecs
import company.tap.tapcardformkit.open.DataConfiguration
import company.tap.tapcardformkit.open.web_wrapper.*
import company.tap.taplocalizationkit.LocalizationManager
import java.util.*

const val chunkSize = 2048
const val keyValueForAuthPayer = "auth_payer"

class ThreeDsWebViewActivity : AppCompatActivity() {
    lateinit var threeDsBottomsheet: BottomSheetDialogFragment
    var loadedBottomSheet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three_ds_web_view)
        LocalizationManager.setLocale(this, Locale(DataConfiguration.lanuage.toString()))
        val webView  = WebView(this)
        webView.layoutParams = this.getDeviceSpecs().first.let {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                it
            )
        }

        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = true
        webView.requestFocus();

        webView.webViewClient = threeDsWebViewClient()
        webView.loadUrl(TapBenefit.threeDsResponse.url)
        threeDsBottomsheet = ThreeDsBottomSheetFragment(webView)

    }

    inner class threeDsWebViewClient : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldOverrideUrlLoading(
            webView: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            webView?.loadUrl(request?.url.toString())
            Log.e("3dsurl",request?.url.toString())
            val Redirect = DataConfiguration.configurationsAsHashMap?.get(redirectKey) as HashMap<*, *>
            val redirect = Redirect.get(urlKey)
            Log.e("redirect",redirect.toString())

            when (request?.url?.toString()?.contains(redirect.toString(),ignoreCase = true)) {
                true -> {
                    threeDsBottomsheet.dialog?.dismiss()
                    val splittiedString = request.url.toString().split(redirect.toString()+"?", ignoreCase = true)
                    Log.e("splitted",splittiedString.toString())
                    try {
                        TapBenefit.retrieve(splittiedString.get(1))
                    }catch (e:Exception){
                        DataConfiguration.getTapKnetListener()?.onError(e.message.toString())
                    }
                }
                false -> {}
                else -> {}
            }
            return true

        }

        override fun onPageFinished(view: WebView, url: String) {
            if (loadedBottomSheet){

                return
            }else{
                doAfterSpecificTime(time = 5000) {
                    threeDsBottomsheet.show(supportFragmentManager,"")
                }
            }
            loadedBottomSheet = true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
        }
    }





}
