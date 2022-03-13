package ru.vilture.fastsongsterr.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.widget.Toast
import fastsongsterr.R
import ru.vilture.fastsongsterr.Model.Artist
import ru.vilture.fastsongsterr.Model.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.sql.SQLException


class ConnectDB(private var context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME, null,
        DATABASE_VERS
    ) {
    override fun onCreate(db: SQLiteDatabase?) {
        try {
            val sqlFavorites =
                "CREATE TABLE Favorites (id TEXT NOT NULL,artist TEXT,song TEXT ,PRIMARY KEY(id))"

            db?.execSQL(sqlFavorites)

        } catch (e: SQLException) {
            Toast.makeText(context, context.getString(R.string.errcre_db), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            when (oldVersion) {
                1 -> {
                }

            }
        } catch (e: SQLException) {
            Toast.makeText(context, context.getString(R.string.errupd_db), Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        const val DATABASE_NAME = "fastsongsterr.db"
        const val DATABASE_VERS = 1
        const val TAB_NAME = "Favorites"
    }

    fun existId(id: String):Boolean{
        val rdb = this.readableDatabase

        val result =
            rdb.query(
                TAB_NAME, null, "id=?", arrayOf(id), null, null, null
            )

        val exist = result.count > 0
        result.close()

        return exist
    }

    @SuppressLint("SimpleDateFormat", "Range")
    fun readData(): List<Response> {
        val list = ArrayList<Response>()
        val rdb = this.readableDatabase

        val result =
            rdb.query(
                TAB_NAME, null, null, null, null, null, null
            )

        if (result.moveToFirst() && result != null) {
            do {
                val favo = Response()
                val artist = Artist()
                favo.id = result.getString(result.getColumnIndex("id")).toInt()
                artist.name = result.getString(result.getColumnIndex("artist"))
                favo.artist = artist
                favo.title = result.getString(result.getColumnIndex("song"))

                list.add(favo)
            } while (result.moveToNext())
        }

        result.close()
        rdb.close()
        return list
    }

    fun addData(values: HashMap<String, String>): DbHandlerResult {
        val res = DbHandlerResult()
        val cv = ContentValues()
        val rdb = this.readableDatabase
        val wdb = this.writableDatabase

        try {
            for (p in values.entries) {
                when (p.key) {
                    "id" -> cv.put("id", p.value)
                    "artist" -> cv.put("artist", p.value)
                    "song" -> cv.put("song", p.value)
                }
            }

            val curr = rdb.query(
                TAB_NAME,
                null,
                "id=?",
                arrayOf(cv["id"].toString()),
                null,
                null,
                null,
                null
            )
            val exists = curr.count > 0
            curr.close()
            if (exists) {
                wdb.delete(TAB_NAME, "id=?", arrayOf(cv["id"].toString()))
            }

            if (wdb.insert(TAB_NAME, null, cv) == -1L) {
                throw Exception(context.getString(R.string.errwrite_db))
            }

            res.isOk = true
        } catch (ex: Exception) {
            ex.printStackTrace()
            res.message = ex.message.toString()
            res.isOk = false
        } finally {
            wdb.close()
            rdb.close()
        }

        return res
    }

    fun delData(id: String): DbHandlerResult {
        val res = DbHandlerResult()
        val wdb = this.writableDatabase
        try {
            wdb.delete(TAB_NAME, "id=?", arrayOf(id))
            res.isOk = true
        } catch (ex: Exception) {
            ex.printStackTrace()
            res.message = ex.message.toString()
            res.isOk = false
        } finally {
            wdb.close()
        }

        return res
    }

    fun exportDatabase(context:Context) = try {
        val dbPath = File("/data/data/ru.vilture.fastsongsterr/databases/fastsongsterr.db")

        //COPY DB PATH
        val dbNew = File("/sdcard/FastSongsterr/databases/")
        if (!dbNew.exists()) {
            dbNew.mkdirs()
        }
        val dbBackup = File(dbNew, "fastsongsterr.db")

        File(dbBackup.parent!!).mkdirs()
        val srcChannel = FileInputStream(dbPath).channel

        val dstChannel = FileOutputStream(dbBackup).channel
        dstChannel.transferFrom(srcChannel,0,srcChannel.size())
        srcChannel.close()
        dstChannel.close()

        Toast.makeText(context,"Сохранили тут $dbBackup",Toast.LENGTH_LONG).show()
    } catch (ex: Exception) {
        Toast.makeText(context,"Ошибка экспорта' избраного ${ex.message}",Toast.LENGTH_LONG).show()
        ex.printStackTrace()
    }

    @Suppress("DEPRECATION")
    fun importDatabase(context:Context) {
        val dir = Environment.getExternalStorageDirectory().absolutePath
        val sd = File(dir)
        val data = Environment.getDataDirectory()

        val backupDBPath = "/data/data/ru.vilture.fastsongsterr/databases/fastsongsterr.db"
        val currentDBPath = "fastsongsterr.db"
        val currentDB = File(sd, currentDBPath)
        val backupDB = File(data, backupDBPath)
        try {
           val source = FileInputStream(currentDB).channel
           val destination = FileOutputStream(backupDB).channel
            destination.transferFrom(source, 0, source.size())
            source.close()
            destination.close()
            Toast.makeText(context, "Импорт завершен", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Toast.makeText(context,"Ошибка импорта избраного ${ex.message}",Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        }
    }

}

class Favorites {
    var id: String = ""
    var artist: String = ""
    var song: String = ""
}

class DbHandlerResult {
    var isOk: Boolean = false
    var message: String = ""
}