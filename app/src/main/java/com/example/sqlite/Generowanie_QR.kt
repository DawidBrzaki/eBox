package com.example.sqlite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.widget.ArrayAdapter
import android.widget.Spinner

class Generowanie_QR : AppCompatActivity() {

    private val STORAGE_PERMISSION_REQUEST_CODE = 456
    private lateinit var generateButton: Button
    private lateinit var saveButton: Button
    private lateinit var imageView: ImageView
    private lateinit var lista: Spinner
    private lateinit var sqliteHelper: SQLHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generowanie_qr)
        supportActionBar?.hide()
        generateButton = findViewById(R.id.generateButton)
        saveButton = findViewById(R.id.saveButton)
        imageView = findViewById(R.id.imageView)

        lista = findViewById(R.id.spinner)
        sqliteHelper = SQLHelper(this)

        val itemList = sqliteHelper.getAllBoxes()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        lista.adapter = adapter

        lista.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val selectedBox = itemList[position]
                val name_box =selectedBox.name
                generuj_qr(name_box)
                przeslij_nazwe(name_box)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })

    }
    fun przeslij_nazwe(pomocy : String)
    {
        saveButton.setOnClickListener {
            // Zapisz wygenerowany QR kod do pliku
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            } else {
                saveQRCodeToStorage(pomocy)
            }
        }
    }
    fun generuj_qr( qrCodeText: String) {
        generateButton.setOnClickListener {
            // Wygeneruj QR kod z podanego tekstu
            val qrCodeBitmap = generateQRCode(qrCodeText, 800, 800)
            if (qrCodeBitmap != null) {
                imageView.setImageBitmap(qrCodeBitmap)
                saveButton.visibility = View.VISIBLE
            }

        }
    }
    private fun generateQRCode(text: String, width: Int, height: Int): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height
            )
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }
        return bitmap
    }
    private fun saveQRCodeToStorage(nazwa_do_zapisu : String) {
        val qrCodeBitmap = (imageView.drawable as? BitmapDrawable)?.bitmap
        if (qrCodeBitmap != null) {
            val fileName = nazwa_do_zapisu + "_${System.currentTimeMillis()}.png"
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(filePath, fileName)
            try {
                val stream = FileOutputStream(file)
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
                stream.close()
                val message = "Kod QR zapisany: ${file.absolutePath}"
                Log.d("MainActivity", message)

                // Wyświetlenie komunikatu Toast
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("Generowanie_QR", "Błąd zapisu pliku: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}