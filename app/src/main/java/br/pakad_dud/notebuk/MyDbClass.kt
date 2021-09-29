package br.pakad_dud.notebuk

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MyDBaseObject : BaseColumns {
    // Описание таблицы
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_IMAGE_URI = "uri"
    const val COLUMN_NAME_TIME = "time"

    // Описание Базы Данных
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "MyDb.db"

    // Создание таблицы
    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_TITLE TEXT," +
            "$COLUMN_NAME_CONTENT TEXT," +
            "$COLUMN_NAME_IMAGE_URI TEXT," +
            "$COLUMN_NAME_TIME TEXT)"

    const val DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME}"
}

// Создание , удаление таблицы
class MyDbHelper(contex: Context) : SQLiteOpenHelper(contex, MyDBaseObject.DATABASE_NAME,
        null, MyDBaseObject.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MyDBaseObject.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(MyDBaseObject.DELETE_TABLE)
        onCreate(db)
    }
}

// Открытие , закрытие , чтение, запись  базы данных
class MyDbManadger(contex: Context) {
    val myDbHelper = MyDbHelper(contex)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    suspend fun insertDb(title: String, content: String, uri: String, time: String)
                                                = withContext(Dispatchers.IO){
        val values = ContentValues().apply {
            put(MyDBaseObject.COLUMN_NAME_TITLE, title)
            put(MyDBaseObject.COLUMN_NAME_CONTENT, content)
            put(MyDBaseObject.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDBaseObject.COLUMN_NAME_TIME, time)
        }
        db?.insert(MyDBaseObject.TABLE_NAME, null, values)
    }

    suspend fun updateDb(id: Int, title: String, content: String, uri: String, time:String)
                                             = withContext(Dispatchers.IO){
        val select = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDBaseObject.COLUMN_NAME_TITLE, title)
            put(MyDBaseObject.COLUMN_NAME_CONTENT, content)
            put(MyDBaseObject.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDBaseObject.COLUMN_NAME_TIME, time)
        }
        db?.update(MyDBaseObject.TABLE_NAME, values, select, null)
    }

    fun removeItemDb(id: String) {                                       // удаление записи из БД
        val select = BaseColumns._ID + "=$id"
        db?.delete(MyDBaseObject.TABLE_NAME, select, null)
    }

    suspend fun readDb(searchText: String): ArrayList<ListBuk> = withContext(Dispatchers.IO){
        val dataList = ArrayList<ListBuk>()
        val select = "${MyDBaseObject.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(MyDBaseObject.TABLE_NAME, null, select, arrayOf("%$searchText%"), null,
                null, null, null)
        while (cursor?.moveToNext()!!) {
            val tempData = ListBuk()
            tempData.id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            tempData.title = cursor.getString(cursor.getColumnIndex(MyDBaseObject.COLUMN_NAME_TITLE))
            tempData.content = cursor.getString(cursor.getColumnIndex(MyDBaseObject.COLUMN_NAME_CONTENT))
            tempData.uri = cursor.getString(cursor.getColumnIndex(MyDBaseObject.COLUMN_NAME_IMAGE_URI))
            tempData.time = cursor.getString(cursor.getColumnIndex(MyDBaseObject.COLUMN_NAME_TIME))

            dataList.add(tempData)
            // Log.d("bbb:", "dataList" + dataList)
        }
        cursor.close()
        return@withContext dataList
    }

    fun deleteDb(rov: String) {
        val selection = "title LIKE ?"
        val selectionArgs = arrayOf(rov)
        db?.delete(MyDBaseObject.TABLE_NAME, selection, selectionArgs)
    }

    fun closeDb() {
        db?.close()
    }
}
