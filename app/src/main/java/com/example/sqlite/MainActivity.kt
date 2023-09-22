package com.example.sqlite

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity() {

    @SuppressLint("MissingInflatedId")
    private lateinit var edName: EditText
    private lateinit var btnAdd: Button
    private lateinit var sqliteHelper: SQLHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: BoxAdapter? = null
    private var std: BoxModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initView()
        initRecyclerView()
        sqliteHelper = SQLHelper(this)
        btnAdd.setOnClickListener { addBox() }

        adapter?.setOnClickItem {
            edName.setText(it.name)
            std = it
            insideBox()


        }
        edName.setOnClickListener { edName.text.clear() }

        val plus_button = findViewById<ImageButton>(R.id.BTNplus)
        plus_button.setOnClickListener {

            plus_button.animate().apply {
                duration = 50
                scaleX(1.2f)
                scaleY(1.2f)
                withEndAction {
                    scaleX(1.0f)
                    scaleY(1.0f)
                    val popupMenu = PopupMenu(this@MainActivity, plus_button)
                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.gen_QR -> {
                                val intent = Intent(this@MainActivity, Generowanie_QR::class.java)
                                startActivity(intent)

                                true
                            }


                            R.id.scan_box -> {

                                barcodeLauncher!!.launch(ScanOptions())
                                val scanner = IntentIntegrator(this@MainActivity)
                                scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                                scanner.setBeepEnabled(false)
                                scanner.initiateScan()


                                true
                            }

                            else -> false
                        }
                    }
                    popupMenu.inflate(R.menu.popup_menu)
                    try {
                        val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                        fieldMPopup.isAccessible = true
                        val mPopup = fieldMPopup.get(popupMenu)
                        mPopup.javaClass
                            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                            .invoke(mPopup, true)

                    } catch (e: Exception) {
                        Log.e("Main", "eror", e)
                    } finally {
                        popupMenu.show()
                    }
                }
            }
        }
        adapter?.setOnLongClickItem {
            edName.setText(it.name)
            std = it

            val popupMenu_box = PopupMenu(this@MainActivity, edName)
            popupMenu_box.menuInflater.inflate(R.menu.popup_menu_box, popupMenu_box.menu)
            popupMenu_box.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.Edit_text -> {

                        zmiana_dialogu()

                        true
                    }

                    R.id.Delete_box -> {
                        val activityName = " tego boxa"
                        DialogHelper.delete_confirm(this, activityName) {

                            deleteBox(this)
                            clearEditText()
                        }
                        true
                    }

                    else -> false
                }

            }
            popupMenu_box.show()
        }

        val edSearch = findViewById<EditText>(R.id.edName)
        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val searchText = s.toString().toLowerCase()
                adapter?.filter?.filter(searchText)

            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun zmiana_dialogu() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Zmień nazwę boxa")

        val input = EditText(this)
        input.setText(edName.text.toString())


        builder.setView(input)

        builder.setPositiveButton("Zapisz") { dialog, which ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {

                updateBox(newName)
            }
            else
            {
                Toast.makeText(this, "Nazwa nie może być pusta", Toast.LENGTH_SHORT).show()
                zmiana_dialogu()
            }
        }

        builder.setNegativeButton("Anuluj") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }


    private fun addBox(){
        val name=edName.text.toString()
        if(name.isEmpty())
        {
            Toast.makeText(this,"Wprowadź dane",Toast.LENGTH_SHORT).show()
        }else{
            val std = BoxModel(name=name)
            val status =sqliteHelper.insertBox(std)

            if(status>-1)
            {
                Toast.makeText(this,"Box dodany",Toast.LENGTH_SHORT).show()
                clearEditText()
                getBox()
            }

        }
    }
    private fun getBox()
    {
        val stdList = sqliteHelper.getAllBoxes()
        adapter!!.addItems(stdList)
        adapter?.filter?.filter(edName.text)

    }
    fun deleteBox(context: Context){

        std?.let { sqliteHelper.delBox(it) }
        getBox()
    }
    private fun updateBox(name : String )
    {
        Log.d("MyApp", "Stara nazwa boxa: ${std?.name}, Nowa nazwa boxa: $name")

        if (name == std?.name) {
            Toast.makeText(this,"Nazwa nie została zmieniona",Toast.LENGTH_SHORT).show()
            return
        }

        if (std == null) return

        val updatedBox = BoxModel(std!!.id, name, std!!.qr)

        val status = sqliteHelper.updateBox(updatedBox)

        if (status > -1) {
            clearEditText()
            getBox()
        } else {
            Toast.makeText(this,"Błąd",Toast.LENGTH_SHORT).show()
        }
    }

    private fun insideBox() {
        val name=edName.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this, "Wprowadź dane", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, MainActivity2::class.java)
            val message = std?.name
            val message2= std?.id.toString()
            intent.putExtra("NAME", message)
            intent.putExtra("ID", message2)

            startActivity(intent)
            clearEditText()

        }
    }
    private fun insideBox_scan(nazwa : String) {
        Log.d("MyApp", "Warunek if spełniony: NAZWA_PRZESŁANA=$nazwa")
        edName.setText(nazwa).toString()
        if (edName.text.isEmpty()) {
            Toast.makeText(this, "Wprowadź dane", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, MainActivity2::class.java)

            val FilteredItem: BoxModel? = adapter?.getFilteredItemByName(nazwa)

            if (FilteredItem!= null) {

                val id = FilteredItem?.id.toString()
                val name = FilteredItem?.name

                Log.d("MyApp", ": ID=$id")
                Log.d("MyApp", ": nazwa=$name")

                intent.putExtra("NAME", name)
                intent.putExtra("ID", id)
                Toast.makeText(this, "Zeskanowano: " + nazwa, Toast.LENGTH_LONG).show()
                startActivity(intent)
                clearEditText()
            }
            else
            {
                Toast.makeText(this,"Brak kodu w bazie danych",Toast.LENGTH_SHORT).show()
                clearEditText()
            }
        }
    }

    private  fun clearEditText()
    {
        edName.setText("")
        edName.requestFocus()

    }
    private fun initRecyclerView(){

        recyclerView.layoutManager=LinearLayoutManager(this)
        adapter=BoxAdapter()
        recyclerView.adapter=adapter


        var handler = Handler()

        handler.postDelayed({getBox()},100)

    }
    private fun handleScanResult(result: ScanIntentResult) {
        if (result.contents == null) {
            Toast.makeText(this, "Anulowano", Toast.LENGTH_LONG).show()
        } else {
            val scanned = result.contents
            insideBox_scan(scanned)

        }
    }
    private val barcodeLauncher: ActivityResultLauncher<ScanOptions?>? =
        registerForActivityResult<ScanOptions, ScanIntentResult>(ScanContract()) { result: ScanIntentResult ->
            handleScanResult(result)
        }
    private fun initView(){
        edName=findViewById(R.id.edName)
        btnAdd=findViewById(R.id.btnAdd)
        recyclerView=findViewById(R.id.recyclerView)
    }

}
class DialogHelper {
    companion object {
        fun delete_confirm(context: Context,activityName:String , onDeleteConfirmed: () -> Unit) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Czy na pewno chcesz usunąć $activityName?")

            builder.setPositiveButton("Tak") { dialogInterface: DialogInterface, i: Int ->
                onDeleteConfirmed()
            }

            builder.setNegativeButton("Nie") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }
}
