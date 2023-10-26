package company.tap.tapcardformkit.open

import Customer
import TapAuthentication
import TapCardConfigurations
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import company.tap.tapcardformkit.R
import company.tap.tapcardformkit.open.web_wrapper.TapBenefitConfiguration
import company.tap.tapcardformkit.open.web_wrapper.TapBenefit
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@SuppressLint("StaticFieldLeak")
object DataConfiguration {

    private var benefitStatusDelegate: BenefitStatusDelegate? = null
    private var applicationLifecycle: ApplicationLifecycle? = null

    var configurations: TapCardConfigurations? = null
        get() = field
        set(value) {
            field = value
        }

    var customerExample: Customer? = null
        get() = field
        set(value) {
            field = value
        }

    var authenticationExample: TapAuthentication? = null
        get() = field
        set(value) {
            field = value
        }
    var configurationsAsJson: String? = null
        get() = field
        set(value) {
            field = value
        }

    var configurationsAsHashMap: HashMap<String,Any>? = null
        get() = field
        set(value) {
            field = value
        }

    var lanuage: String? = null
        get() = field
        set(value) {
            field = value
        }









    fun setTheme(
        context: Context?,
        resources: Resources?,
        urlString: String?,
        urlPathLocal: Int?,
        fileName: String?
    ) {
        if (resources != null && urlPathLocal != null) {
            if (fileName != null && fileName.contains("dark")) {
                if (urlPathLocal != null) {
                    ThemeManager.loadTapTheme(resources, urlPathLocal, "darktheme")
                }
            } else {
                if (urlPathLocal != null) {
                    ThemeManager.loadTapTheme(resources, urlPathLocal, "lighttheme")
                }
            }
        } else if (urlString != null) {
            if (context != null) {
                println("urlString>>>" + urlString)
                ThemeManager.loadTapTheme(context, urlString, "lighttheme")
            }
        }

    }

    fun setLocale(
        context: Context,
        languageString: String,
        urlString: String?,
        resources: Resources?,
        urlPathLocal: Int?
    ) {
        LocalizationManager.setLocale(context, Locale(languageString))
        lanuage = languageString
        if (resources != null && urlPathLocal != null) {
            LocalizationManager.loadTapLocale(resources, R.raw.lang)
        } else if (urlString != null) {
            if (context != null) {
                LocalizationManager.loadTapLocale(context, urlString)
                Log.e("local", urlString.toString())

            }
        }

    }

    fun setCustomer(customer: Customer) {
        customerExample = customer
    }


    fun setTapAuthentication(tapAuthentication: TapAuthentication) {
        authenticationExample = tapAuthentication
    }

    fun addTapBenefitPayStatusDelegate(_tapCardStatuDelegate: BenefitStatusDelegate?) {
        this.benefitStatusDelegate = _tapCardStatuDelegate

    }
    fun addAppLifeCycle(appLifeCycle: ApplicationLifecycle?) {
        this.applicationLifecycle = appLifeCycle
    }

    fun getAppLifeCycle(): ApplicationLifecycle? {
        return this.applicationLifecycle
    }
    fun getTapKnetListener(): BenefitStatusDelegate? {
        return benefitStatusDelegate
    }

    fun initializeSDK(activity: Activity, configurations: HashMap<String,Any>, tapBenefit: TapBenefit){
        TapBenefitConfiguration.configureWithBenefitDictionary(activity,tapBenefit,configurations)
    }






}

interface BenefitStatusDelegate {
    fun onSuccess(data: String)
    fun onReady(){}
    fun onClick(){}
    fun onOrderCreated(data: String){}
    fun onChargeCreated(data:String){}
    fun onError(error: String)
    fun cancel(){}
}

interface ApplicationLifecycle {

     fun onEnterForeground()
     fun onEnterBackground()


}
