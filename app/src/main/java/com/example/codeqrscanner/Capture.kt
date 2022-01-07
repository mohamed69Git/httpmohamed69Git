package com.example.codeqrscanner

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.example.tryapp.service.RetrofitFactory
import kotlinx.android.synthetic.main.activity_capture.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class Capture : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)
        val intent = intent

        student_name.text = intent.getStringExtra("name")
        student_email.text = intent.getStringExtra("email")
        nombreTicketCent.text = intent.getIntExtra("nbrticketcent", 0).toString()
        nombreTicket.text = intent.getIntExtra("nbrticketfifty", 0).toString()
        val status = intent.getIntExtra("status", -1)
        if (status == 1) {
            student_name.text = intent.getStringExtra("name")
            student_email.text = intent.getStringExtra("email")
            nombreTicketCent.text = intent.getIntExtra("nbrticketcent", 0).toString()
            nombreTicket.text = intent.getIntExtra("nbrticketfifty", 0).toString()
            infouseraccount.visibility = VISIBLE
            val contextRepas = intent.getStringExtra("context")
            println("CONTESSSS $contextRepas")
            if (contextRepas == "repas"  || contextRepas == "diner")
                coupeticketcent.visibility = VISIBLE
            if (contextRepas == "petitDejeuner")
                coupetikettfifty.visibility = VISIBLE


            if (contextRepas == "null") {
                infouseraccount.visibility = VISIBLE

            }
            coupetikettfifty.setOnClickListener {
                apply(User(intent.getIntExtra("id", -1)))
//                        coupetikettfifty.isEnabled = false

            }
            coupeticketcent.setOnClickListener {
                if (contextRepas == "repas")
                    apply(User(intent.getIntExtra("id", -1)))
                if (contextRepas == "diner")
                    apply(User(intent.getIntExtra("id", -1)))


            }
            println("HEURESS      ${java.util.Date().hours}")

        }

    }

    fun apply(user: User) {
        println("DAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPppp")
        val service = RetrofitFactory.makeRetrofitService()
        val call = service.apply(user)
        call.enqueue(object : Callback<ResponsePass> {
            override fun onResponse(call: Call<ResponsePass>, response: Response<ResponsePass>) {
                println("REPONSEREPONSEREPONSEREPONSE ${response.body()?.message}")
                coupeticketcent.isEnabled = false
                coupetikettfifty.isEnabled = false
                if (response.body()!!.message == "OK") {
                    Toast.makeText(this@Capture, "Le ticket a été retiré", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@Capture, "Nombre de tickets insuffisant", Toast.LENGTH_LONG)
                        .show()
                }
                println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOooo")
                println("APPLYYYYYYYYYYY: ${response.body()?.message}")
            }

            override fun onFailure(call: Call<ResponsePass>, t: Throwable) {
                t.printStackTrace()
            }

        })
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = service.apply(user)
//            withContext(Dispatchers.Main) {
//                try {
//
//                    if (response.isSuccessful) {
//                        println("REPONSEREPONSEREPONSEREPONSE ${response.body()!!.message}")
//                        coupeticketcent.isEnabled = false
//                        coupetikettfifty.isEnabled = false
//                        if(response.body()!!.message == "OK"){
//                            Toast.makeText(this@Capture,"Le ticket a été retiré", Toast.LENGTH_LONG).show()
//                        }
//                        else{
//                            Toast.makeText(this@Capture,"Nombre de tickets insuffisant", Toast.LENGTH_LONG).show()
//                        }
//                        println("oOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOooo")
//                        println("APPLYYYYYYYYYYY: ${response.body()}")
//                    } else {
//                        println("DAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaaPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPppp")
//                        print("THE RESPONSE FAILED:   $response")
//                    }
//                } catch (e: HttpException) {
//                    println("Exception ${e.message}")
//                } catch (e: Throwable) {
//                    println("Oops: Something else went wrong")
//                }
//            }
//        }
    }

    fun restart(view: android.view.View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}