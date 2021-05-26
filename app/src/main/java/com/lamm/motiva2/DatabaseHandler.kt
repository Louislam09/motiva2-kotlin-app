package com.lamm.motiva2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PhraseDatabase"
        private const val TABLE_PHRASES = "PhraseTable"

        private const val KEY_ID = "_id"
        private const val KEY_TEXT = "text"
        private const val KEY_IMAGE = "image"
        private const val KEY_ISLIKE = "islike"
        private const val KEY_AUTHOR = "author"

    }


    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_PHRASES_TABLE = ("CREATE TABLE " + TABLE_PHRASES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TEXT + " TEXT,"
                +  KEY_AUTHOR + " TEXT," + KEY_IMAGE + " BLOB,"
                + KEY_ISLIKE + " BOOLEAN CHECK ("+ KEY_ISLIKE + " IN (0, 1))" + ")")

        db?.execSQL(CREATE_PHRASES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_PHRASES)
        onCreate(db)
    }

    fun addFavPhrase(favPhrase: PhraseModal): Long {

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_TEXT,favPhrase.text)
        cv.put(KEY_AUTHOR,favPhrase.author)
        cv.put(KEY_ISLIKE,favPhrase.isLike)
        cv.put(KEY_IMAGE,favPhrase.imageResource)

        val success = db.insert(TABLE_PHRASES,null,cv)
        db.close()
        return success
    }

    fun viewPhrases(): ArrayList<PhraseModal> {
        val phraseList = ArrayList<PhraseModal>()

        val selectQuery = "SELECT * FROM $TABLE_PHRASES"

        val db = this.readableDatabase
        var cursor: Cursor?

        try{
            cursor = db.rawQuery(selectQuery,null)
            if(cursor!=null && cursor.count > 0){
                if(cursor.moveToFirst()){
                    do {
                        var id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                        var text = cursor.getString(cursor.getColumnIndex(KEY_TEXT))
                        var author = cursor.getString(cursor.getColumnIndex(KEY_AUTHOR))
                        var image = cursor.getString(cursor.getColumnIndex(KEY_IMAGE))
                        var islike = cursor.getInt(cursor.getColumnIndex(KEY_ISLIKE))

                        val flag2 = islike == 1
                        val favPhrase = PhraseModal(id = id,imageResource = image,text=text,isLike = flag2,author = author)

                        phraseList.add(favPhrase)
                    } while(cursor.moveToNext())
                }
            }
        }
        catch (e: SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        return phraseList
    }

    fun deleteFavPhrase(favPhraseId: Int): Int {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_ID,favPhraseId)

        val success = db.delete(TABLE_PHRASES, KEY_ID + "="  + favPhraseId,null)
        db.close()
        return success
    }
//
//    fun deleteAllFavPhrase() {
//        val db = this.writableDatabase
//        db!!.execSQL("DROP TABLE " + TABLE_PHRASES)
//        val CREATE_PHRASES_TABLE = ("CREATE TABLE " + TABLE_PHRASES + "("
//                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TEXT + " TEXT,"
//                +  KEY_AUTHOR + " TEXT," + KEY_IMAGE + " TEXT,"
//                + KEY_ISLIKE + " BOOLEAN CHECK ("+ KEY_ISLIKE + " IN (0, 1))" + ")")
//        db?.execSQL(CREATE_PHRASES_TABLE)
//        db.close()
//    }


}