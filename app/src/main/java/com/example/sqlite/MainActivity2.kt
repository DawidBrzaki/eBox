package com.example.sqlite

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.DialogHelper

private lateinit var nam: TextView
var xdid : String = ""

class MainActivity2: ComponentActivity() {
     lateinit var edName:EditText
     lateinit var btnAdd: Button
     lateinit var btnDelete:Button
     lateinit var btnUpdate:Button
     lateinit var sqliteHelper2: SQLHelper2
     lateinit var recyclerView: RecyclerView
     lateinit var adapter: BoxAdapter2
     lateinit var std: BoxModel2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initView()
        initRecyclerView()
        nam = findViewById(R.id.t_name)
        val Intent = intent
        val name = Intent.getStringExtra("NAME")
        xdid = Intent.getStringExtra("ID").toString()
        if (xdid != null)
        {
            xdid.toInt()
        }
        nam.setText("Jesteś w boxie $name")
        sqliteHelper2 = SQLHelper2(this)
        btnAdd.setOnClickListener{addBox()}
        btnDelete.setOnClickListener {val activityName = " ten przedmiot"
            DialogHelper.delete_confirm(this, activityName){deleteBox(this)} }
        btnUpdate.setOnClickListener{updateBox()}
        adapter.setOnClickItem { edName.setText(it.name)
            std=it
        }

    }


private fun addBox(){
    val name=edName.text.toString()
    if(name.isEmpty())
    {
        Toast.makeText(this,"Wprowadź dane",Toast.LENGTH_SHORT).show()
    }else{
        val std = BoxModel2(name=name, id = xdid.toInt())
        val status =sqliteHelper2.insertBox(std)

        if(status>-1)
        {
            Toast.makeText(this,"Dodano przedmiot",Toast.LENGTH_SHORT).show()
            clearEditText()
            getBox()
        }

    }
}
private fun getBox()
{
    val stdList = sqliteHelper2.getAllBoxes()
    adapter.addItems(stdList)


}
    fun deleteBox(context: Context) {
        val name = edName.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Nie zaznaczono przedmiotu", Toast.LENGTH_SHORT).show()
        } else {
            std.let {
                sqliteHelper2.delBox(it)
                getBox()
            }
            clearEditText()
        }
    }
private fun updateBox() {
    val name = edName.text.toString()
    if (name.isEmpty()) {
        Toast.makeText(this, "Nie zaznaczono przedmiotu", Toast.LENGTH_SHORT).show()
    } else {
        if (name == std.name) {
            Toast.makeText(this, "Nazwa nie została zmieniona", Toast.LENGTH_SHORT).show()
            return
        }
        if (std == null) return

        val std = BoxModel2(id = std.id, name = name)
        val status = sqliteHelper2.updateBox(std)
        if (status > -1) {
            clearEditText()
            getBox()
        } else {
            Toast.makeText(this, "Niepowodzenie", Toast.LENGTH_SHORT).show()
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
    adapter=BoxAdapter2()
    recyclerView.adapter=adapter

    var handler = Handler()

    handler.postDelayed({getBox()},100)

}

private fun initView(){
    edName=findViewById(R.id.edName)
    btnAdd=findViewById(R.id.btnAdd)
    btnDelete=findViewById(R.id.btnDelete)
    btnUpdate=findViewById(R.id.btnUpdate)
    recyclerView=findViewById(R.id.recyclerView)
}
}