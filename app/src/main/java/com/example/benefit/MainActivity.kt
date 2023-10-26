package com.example.benefit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.chillibits.simplesettings.tool.getPrefStringValue
import com.example.benefit_android.R
import company.tap.tapcardformkit.open.BenefitStatusDelegate
import company.tap.tapcardformkit.open.web_wrapper.TapBenefitConfiguration
import company.tap.tapcardformkit.open.web_wrapper.TapBenefit

class MainActivity : AppCompatActivity() ,BenefitStatusDelegate{
    lateinit var tapBenefit: TapBenefit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureSdk()

//        Bundle ID	tap.KnetExample
//        Prod - Public Key	pk_live_I8aWfZkiGtw9HYsRCcAgQzS6
//        Test - Public Key	pk_test_6jdl4Qo0FYOSXmrZTR1U5EHp
//        Prod - Secret Key	sk_live_462YewyxHlPsUGvSWOjckQAi
//        Test - Secret Key	sk_test_bNgRpokWMylX3CBJ6FOresTq
    }

    fun configureSdk(){


        /**
         * operator
         */
        val publicKey = intent.getStringExtra("publicKey")
        val hashStringKey = intent.getStringExtra("hashStringKey")
        val operator = HashMap<String,Any>()

        operator.put("publicKey","pk_test_6jdl4Qo0FYOSXmrZTR1U5EHp")
        operator.put("hashString",hashStringKey.toString())
        Log.e("orderData","pbulc" + publicKey.toString() + " \nhash" + hashStringKey.toString())

        /**
         * metadata
         */
        val metada = HashMap<String,Any>()
        metada.put("id","")

        /**
         * order
         */
        val ordrId =  intent.getStringExtra("orderIdKey")
        val orderDescription =  intent.getStringExtra("orderDescKey")
        val orderAmount =  intent.getStringExtra("amountKey")
        val orderRefrence =  intent.getStringExtra("orderTransactionRefrence")
        val selectedCurrency: String = intent.getStringExtra("selectedCurrencyKey").toString()

        val order = HashMap<String,Any>()
        order.put("id",ordrId.toString())
        order.put("amount",  if (orderAmount?.isEmpty() == true)"1" else orderAmount.toString() )
        order.put("currency",selectedCurrency)
        order.put("description",orderDescription ?: "")
        order.put("reference",orderRefrence ?: "")
        order.put("metadata",metada)
        Log.e("orderData","id" + ordrId.toString() + "  \n dest" + orderDescription.toString() +" \n orderamount " + orderAmount.toString() +"  \n orderRef" + orderRefrence.toString() + "  \n currency " + selectedCurrency.toString())

        /**
         * merchant
         */
        val merchant = HashMap<String,Any>()
        merchant.put("id", "")

        /**
         * invoice
         */
        val invoice = HashMap<String,Any>()
        invoice.put("id","")


        /**
         * phone
         */
        val phone = java.util.HashMap<String,Any>()
        phone.put("countryCode","+20")
        phone.put("number","011")


        /**
         * contact
         */
        val contact = java.util.HashMap<String,Any>()
        contact.put("email","test@gmail.com")
        contact.put("phone",phone)
        /**
         * name
         */
        val name = java.util.HashMap<String,Any>()
        name.put("lang","en")
        name.put("first", "first")
        name.put("middle", "middle")
        name.put("last","last")

        /**
         * customer
         */
        val customer = java.util.HashMap<String,Any>()
        customer.put("id", "")
        customer.put("contact",contact)
        customer.put("name", listOf(name))

        /**
         * interface
         */

        val selectedLanguage: String? =  intent.getStringExtra("selectedlangKey")
        val selectedTheme: String? = intent.getStringExtra("selectedthemeKey")
        val selectedCardEdge = intent.getStringExtra("selectedcardedgeKey")
        val selectedColorStylee = intent.getStringExtra("selectedcolorstyleKey")
        val loader = intent.getBooleanExtra("loaderKey",false)

        Log.e("interfaceData",selectedTheme.toString() + "language" + selectedLanguage.toString() + "cardedge " + selectedCardEdge.toString() +" loader" + loader.toString() + "selectedColorStylee " + selectedColorStylee.toString())
        val interfacee = HashMap<String,Any>()
        interfacee.put("locale",selectedLanguage ?: "en")
        interfacee.put("theme",selectedTheme ?: "light")
        interfacee.put("edges",selectedCardEdge ?: "curved")
        interfacee.put("colorStyle",selectedColorStylee ?:"colored")
        interfacee.put("loader",loader)

        val posturl =  intent.getStringExtra("posturlKey")
        val redirectUrl =  intent.getStringExtra("redirectUrlKey")


        val post = HashMap<String,Any>()
        post.put("url",posturl?: "")

        val redirect = HashMap<String,Any>()
        redirect.put("url","onTapBenefitRedirect://")
        val configuration = LinkedHashMap<String,Any>()




        /**
         * transaction && scope
         */


        intent.putExtra("scopeKey", getPrefStringValue("scopeKey","Token"))
        intent.putExtra("transactionRefrenceKey", getPrefStringValue("transactionRefrenceKey",""))
        intent.putExtra("transactionAuthroizeTypeKey", getPrefStringValue("transactionAuthroizeTypeKey",""))
        intent.putExtra("transactionAuthroizeTimeKey", getPrefStringValue("transactionAuthroizeTimeKey",""))

        val transaction = HashMap<String,Any>()
        val scopeKey = intent.getStringExtra("scopeKey")
        val transactionRefrenceKey = intent.getStringExtra("transactionRefrenceKey")
        val transactionAuthroizeTypeKey = intent.getStringExtra("transactionAuthroizeTypeKey")
        val transactionAuthroizeTimeKey = intent.getStringExtra("transactionAuthroizeTimeKey")

        Log.e("scope","scope is : " + scopeKey.toString() + " transactionRefrenceKey : " +  " " + transactionRefrenceKey.toString() +  " transactionAuthroizeTypeKey : " + transactionAuthroizeTypeKey.toString() + " transactionAuthroizeTimeKey : " + transactionAuthroizeTimeKey.toString())
        val authorize = HashMap<String,Any>()
        authorize.put("type",transactionAuthroizeTypeKey ?:"")
        authorize.put("time",transactionAuthroizeTimeKey ?:"")

        transaction.put("reference",transactionRefrenceKey?: "")
        transaction.put("authorize",authorize?: "")


        /**
         * configuration
         */

        configuration.put("operator",operator)
        configuration.put("order",order)
        configuration.put("customer",customer)
        configuration.put("merchant",merchant)
        configuration.put("invoice",invoice)
        configuration.put("interface",interfacee)
        configuration.put("post",post)
        configuration.put("redirect",redirect)
        configuration.put("scope",scopeKey.toString())
        configuration.put("transaction",transaction)




        TapBenefitConfiguration.configureWithBenefitDictionary(
            this,
            findViewById(R.id.knet_pay),
            configuration,
           this)


    }

    override fun onReady() {
        Toast.makeText(this, "onReady", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(data: String) {
        Log.i("onSuccess",data)
        Toast.makeText(this, "onSuccess $data", Toast.LENGTH_SHORT).show()

    }

    override fun onClick() {
        Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show()
    }

    override fun onChargeCreated(data: String) {
        Log.e("data",data.toString())
        Toast.makeText(this, "onChargeCreated $data", Toast.LENGTH_SHORT).show()

    }

    override fun onOrderCreated(data: String) {
        Toast.makeText(this, "onOrderCreated $data", Toast.LENGTH_SHORT).show()
    }

    override fun cancel() {
        Toast.makeText(this, "Cancel ", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        Log.e("error",error.toString())
        Toast.makeText(this, "onError $error ", Toast.LENGTH_SHORT).show()

    }

}