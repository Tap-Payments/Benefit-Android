package company.tap.tapcardformkit.open.web_wrapper

import Headers
import android.content.Context
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import company.tap.tapcardformkit.R
import company.tap.tapcardformkit.open.AppLifecycleObserver
import company.tap.tapcardformkit.open.DataConfiguration
import company.tap.tapcardformkit.open.DataConfiguration.configurationsAsHashMap
import company.tap.tapcardformkit.open.BenefitStatusDelegate
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.utils.CryptoUtil


class TapBenefitConfiguration {

    companion object {


        fun configureWithBenefitDictionary(
            context: Context,
            tapCardInputViewWeb: TapBenefit?,
            tapMapConfiguration: java.util.HashMap<String, Any>,
            benefitStatusDelegate: BenefitStatusDelegate? = null
        ) {
            with(tapMapConfiguration) {
                Log.e("map", tapMapConfiguration.toString())
                configurationsAsHashMap = tapMapConfiguration
                val operator = configurationsAsHashMap?.get(operatorKey) as HashMap<*, *>
                val publickKey = operator.get(publicKeyToGet)

                val appLifecycleObserver = AppLifecycleObserver()
                ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

                addOperatorHeaderField(
                    tapCardInputViewWeb,
                    context,
                    KnetConfiguration.MapConfigruation,
                    publickKey.toString()
                )

                DataConfiguration.addTapBenefitPayStatusDelegate(benefitStatusDelegate)
                tapCardInputViewWeb?.init(KnetConfiguration.MapConfigruation)

            }
        }

        fun addOperatorHeaderField(
            tapCardInputViewWeb: TapBenefit?,
            context: Context,
            modelConfiguration: KnetConfiguration,
            publicKey: String?
        ) {
         val encodedeky = when(publicKey.toString().contains("test")){
                true->{
                    tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyTest)
                }
                false->{
                    tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyProduction)

                }
            }

            Log.e("packagedname",context.packageName.toString())
            NetworkApp.initNetwork(
                tapCardInputViewWeb?.context ,
                publicKey ?: "",
                context.packageName,
                ApiService.BASE_URL,
                "android-knet",
                true,
                encodedeky,
                null
            )
            val headers = Headers(
                application = NetworkApp.getApplicationInfo(),
                mdn = CryptoUtil.encryptJsonString(
                    context.packageName.toString(),
                    encodedeky,
                )
            )

            when (modelConfiguration) {
                KnetConfiguration.MapConfigruation -> {
                    val hashMapHeader = HashMap<String, Any>()
                    hashMapHeader[HeadersMdn] = headers.mdn.toString()
                    hashMapHeader[HeadersApplication] = headers.application.toString()
                    configurationsAsHashMap?.put(headersKey, hashMapHeader)

                }
                else -> {}
            }


        }
    }
}


