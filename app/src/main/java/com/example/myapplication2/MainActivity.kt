package com.example.myapplication2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication2.ui.theme.MyApplication2Theme



class MainActivity : ComponentActivity() {

    private lateinit var qrScanner: QR_Scanner
    private  lateinit var printQr: print_Qr 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        qrScanner = QR_Scanner(this, this)

        setContent {
            MyApplication2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current
                    var code by remember { mutableStateOf("") }

                    Baslat(
                        qrScanner = qrScanner,
                        code = code,
                        context = context,
                        onEncodeChange = { newEncode -> code = newEncode} ,
                        printQr= print_Qr()

                    )
                }

                }
            }
        }
    }


    @Composable
    fun Baslat(
        qrScanner: QR_Scanner,
        code: String,
        context: Context,
        onEncodeChange: (String) -> Unit,
        printQr: print_Qr
    ) {
        var bitmapState by remember { mutableStateOf<Bitmap?>(null) }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
        , horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = code,
                onValueChange = onEncodeChange,
                singleLine = true,

            )
            Spacer(modifier = Modifier.height(15.dp))
            Column(modifier = Modifier.fillMaxWidth(),Arrangement.SpaceEvenly) {
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        qrScanner.checkCameraPermission()
                    },
                ) {
                    Text("QR Scanner")
                }


                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (code.isEmpty()) {
                            Toast.makeText(context, "Boş alan bırakmayınız", Toast.LENGTH_LONG).show()
                        } else {
                            val qr=Qr_generate()
                            val bitmap2=qr.generateQRCode(code)
                            bitmap2.let {
                                bitmapState=bitmap2


                            }
                            
                        }
                    },

                ) {
                    Text("QR Generate")
                }

                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                    try{
                        bitmapState?.let { qr_kayit().saveBitmapToGallery(context,it,"qr") }
                    }
                catch (e:Exception){
                    Toast.makeText(context,"${e.message}",Toast.LENGTH_LONG).show()

                }
                })
                
                {
                    Text(text = "Save")
                    
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                    bitmapState?.let { printQr.printBitmap(context, it )}?: Toast.makeText(context, "No QR code to print", Toast.LENGTH_LONG).show()})
                {
                    Text(text = "yazdır")
                    
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (qrScanner.getcode().isNotEmpty()) {
                            // URL'yi aç
                            val uri = Uri.parse(qrScanner.getcode())
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "QR kodu boş olmamalıdır", Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    Text("Web'de Aç")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))



            Text(text ="Okunan QR:  ${qrScanner.getcode()}" )


            Spacer(modifier = Modifier.height(8.dp))


            bitmapState?.let {
                Text(text = "uretilen qr")
                

                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "QR Code"

                )
            }
        }


    }











