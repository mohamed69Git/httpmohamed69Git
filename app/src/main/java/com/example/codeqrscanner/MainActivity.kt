package com.example.codeqrscanner

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.tryapp.service.RetrofitFactory
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var codescanner: CodeScanner
    private lateinit var mQrResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var textQr: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                123
            )

        } else {
            startScanning();
        }

        // Alternative to "onActivityResult", because that is "deprecated"
//        mQrResultLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == Activity.RESULT_OK) {
//                    val result = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
//
//                    if (result.contents != null) {
//                        val intent = (Intent(this, Capture::class.java))
//                        val resultat: String? = result.contents
//                        intent.putExtra("name", "Mor Diaw")
//                        intent.putExtra("email", resultat.toString())
//                        textQr.text = result.contents
////                    Toast.makeText(this, "${result.contents}", Toast.LENGTH_SHORT).show()
//                        // Do something with the contents (this is usually a URL)
//                        println(" Goooooooooooooooooooooooooooooo ${result.contents}")
//                        apply(result.contents)
//                        startActivity(intent)
//                        finish()
//                    }
//
//
//                } else {
//                    textQr.text = "ECHEC"
//                }
//            }

        // Starts scanner on Create of Overlay (you can also call this function using a button click)
//        startScanner()
    }

    // Start the QR Scanner
    private fun startScanner() {
        val scanner = IntentIntegrator(this)
        // QR Code Format
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        // Set Text Prompt at Bottom of QR code Scanner Activity
        scanner.setPrompt("QR Code Scanner Prompt Text")
        scanner.setBeepEnabled(true)
        scanner.setTimeout(10000)

        // Start Scanner (don't use initiateScan() unless if you want to use OnActivityResult)
        mQrResultLauncher.launch(scanner.createScanIntent())
    }


    fun startScanning() {
        val scannerView: CodeScannerView = findViewById(R.id.scanner_view)
        codescanner = CodeScanner(this, scannerView)
        codescanner.camera = CodeScanner.CAMERA_BACK
        codescanner.formats = CodeScanner.ALL_FORMATS
        codescanner.autoFocusMode = AutoFocusMode.SAFE
        codescanner.scanMode = ScanMode.SINGLE
        codescanner.isAutoFocusEnabled = true
        codescanner.isFlashEnabled = true
        codescanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan Result: ${it.text}", Toast.LENGTH_SHORT).show()
                findUser(it.text)
            }
        }

        codescanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        scannerView.setOnClickListener {
            codescanner.startPreview()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (::codescanner.isInitialized) {
            codescanner?.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codescanner.isInitialized) {
            codescanner.releaseResources()
        }

    }

    fun findUser(email: String) {
        val service = RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.findUser(email)
            withContext(Dispatchers.Main) {
                try {

                    if (response.isSuccessful) {
                        println("GOOOAAAAAAAALAAAASOOOOOOOO \n ${response.body()}")
//                        if(response.body()?.status == 1){

                        val intent = (Intent(this@MainActivity, Capture::class.java))
                        intent.putExtra("name", response.body()!!.name)
                        intent.putExtra("email", response.body()!!.email)
                        intent.putExtra("nbrticketcent", response.body()!!.ticket_cent)
                        intent.putExtra("nbrticketfifty", response.body()!!.ticket_fifty)
                        intent.putExtra("status", response.body()!!.status)
                        intent.putExtra("context", response.body()!!.context)


//                        }
//                        if(response.body()?.status == 0){
//                        }
                        startActivity(intent)
                        finish()


                    } else {
                        print("THE RESPONSE FAILED:   $response")
                    }
                } catch (e: HttpException) {
                    println("Exception ${e.message}")
                } catch (e: Throwable) {
                    println("Oops: Something else went wrong")
                }
            }
        }
    }
}