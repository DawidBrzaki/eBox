package com.example.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception
import kotlin.collections.ArrayList as ArrayList

class SQLHelper2(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "scanner.db"
        private const val TBL_BOX1 = "tbl_box"
        private const val ID1 = "id"
        private const val NAME = "boxName"
        private const val QRCode = "qrcode"
        private const val TBL_BOX2 = "Rzeczy"
        private const val ID2 = "id"
        private const val IDBOX = "idBoxa"
        private const val NAME2 = "nazwaRzeczy"

    }

    override fun onCreate(db: SQLiteDatabase) {
      //  val createTblBox = ("CREATE TABLE " +TBL_BOX1 + " ("
          //      + ID1 + " INTEGER PRIMARY KEY," + NAME + " TEXT," + QRCode + " TEXT" + ")")
        val createTbl = ("CREATE TABLE " + TBL_BOX2 + " ("
                + ID2 + " INTEGER PRIMARY KEY," + IDBOX + " INTEGER," +NAME2 + " TEXT," +
                " FOREIGN KEY ($IDBOX) REFERENCES $TBL_BOX1($ID1))")

        db.execSQL(createTbl)
     //   db.execSQL(createTblBox);
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_BOX1")
        onCreate(db)
    }

    fun insertBox(std: BoxModel2): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID2, std.idBox)
        contentValues.put(IDBOX, xdid.toInt())
        contentValues.put(NAME2, std.name)

        val success = db.insert(TBL_BOX2, null, contentValues)
        db.close()
        return success
    }
    @SuppressLint("Range")
    fun getAllBoxes():ArrayList<BoxModel2> {
        val stdList: ArrayList<BoxModel2> = ArrayList()
        // val selectQuery = "SELECT $NAME, $NAME2 FROM $Rzeczy INNER JOIN $TBL_BOX1 ON $TBL_BOX1.$ID1 = $Rzeczy.$IDBOX"
        val selectQuery = "SELECT * FROM $TBL_BOX2 WHERE idBoxa = $xdid"
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
         var idBoxa: Int
         var nazwaRzeczy: String
        if (cursor.moveToFirst()) {
            do {
                  id = cursor.getInt(cursor.getColumnIndex("id"))
                  idBoxa = cursor.getInt(cursor.getColumnIndex("idBoxa"))
                  nazwaRzeczy = cursor.getString(cursor.getColumnIndex("nazwaRzeczy"))
                  val std = BoxModel2(id = id,idBox = idBoxa, name = nazwaRzeczy)
                  stdList.add(std)
            } while (cursor.moveToNext())
        }
        return stdList
    }
    fun updateBox(std: BoxModel2):Int{
        val db=this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID2,std.id)
        contentValues.put(NAME2,std.name)
        val success = db.update(TBL_BOX2,contentValues,"id="+std.id,null)
        db.close()
        return success
    }
    fun delBox(std: BoxModel2): Int
    {
        val db=this.writableDatabase
        val success=db.delete(TBL_BOX2,"id="+std.id,null)
        db.close()
        return success
    }
}






