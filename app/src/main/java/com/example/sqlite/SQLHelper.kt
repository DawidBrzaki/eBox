package com.example.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception
import kotlin.collections.ArrayList as ArrayList

class SQLHelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "scaner.db"
        private const val TBL_BOX1: String = "tbl_box"
        private const val ID1 = "id"
        private const val NAME = "boxName"
        private const val QRCode = "qrcode"
        private const val TBL_BOX2 = "Rzeczy"
        private const val ID2 = "id"
        private const val IDBOX = "idBoxa"
        private const val NAME2 = "nazwaRzeczy"

    }

    override fun onCreate(db: SQLiteDatabase) {


        val createTblBox = ("CREATE TABLE " + TBL_BOX1 + " ("
                + ID1 + " INTEGER PRIMARY KEY," + NAME + " TEXT," + QRCode + " TEXT" + ")")
    //      val createTbl = ("CREATE TABLE " + TBL_BOX2 + " ("
      //          + ID2 + " INTEGER PRIMARY KEY," + IDBOX + " INTEGER," + NAME2 + " TEXT," +
      //          " FOREIGN KEY ($IDBOX) REFERENCES  $TBL_BOX1($ID1))")

      //  db.execSQL(createTbl)

        db.execSQL(createTblBox);
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_BOX1")
        onCreate(db)
    }

    fun insertBox(std: BoxModel): Long {
        val db = this.writableDatabase  
        val contentValues = ContentValues()
        contentValues.put(ID1, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(QRCode, std.qr)

        val success = db.insert(TBL_BOX1, null, contentValues)
        db.close()
        return success
    }
    @SuppressLint("Range")
    fun getAllBoxes():ArrayList<BoxModel> {
        val stdList: ArrayList<BoxModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_BOX1"
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()

        }
        var id: Int
        var boxName: String
        var qr: String

      /*  var id: Int
        var idBoxa: Int
        var nazwaRzeczy: String

       */
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                boxName = cursor.getString(cursor.getColumnIndex("boxName"))
                qr = cursor.getString(cursor.getColumnIndex("qrcode"))
                val std = BoxModel(id = id, name = boxName, qr = qr)
                stdList.add(std)

              /*  id = cursor.getInt(cursor.getColumnIndex("idRzeczy"))
                idBoxa = cursor.getInt(cursor.getColumnIndex("idBoxa"))
                nazwaRzeczy = cursor.getString(cursor.getColumnIndex("nazwaRzeczy"))
                val std = BoxModel2(rzecz = nazwaRzeczy,idBoxa = idBoxa, idRzeczy = id)

               */


            } while (cursor.moveToNext())
        }
        return stdList
    }
    fun updateBox(std: BoxModel):Int{
        val db=this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID1,std.id)
        contentValues.put(NAME,std.name)
        contentValues.put(QRCode,std.qr)
        val success = db.update(TBL_BOX1,contentValues,"id="+std.id,null)
        if (success > -1) {

            Log.d("MyApp", "git")
        } else {

            Log.d("MyApp", "nie git")
        }

        db.close()
        return success
    }
        fun delBox(std:BoxModel): Int
        {
        val db=this.writableDatabase
        val success=db.delete(TBL_BOX1,"id="+std.id,null)
        db.close()
        return success
    }
}






