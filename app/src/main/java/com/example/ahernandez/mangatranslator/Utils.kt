package com.example.ahernandez.mangatranslator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.ahernandez.mangatranslator.model.Translator
import com.google.gson.Gson
import java.io.*

class Utils {


    fun loadJsonFromAssets(context: Context, filename: String): String? {
        var input: InputStream? = null
        var jsonString: String

        try {

            // Create InputStream
            input = context.assets.open(filename)

            val size = input.available()

            // Create a buffer w the size
            val buffer = ByteArray(size)

            // Read data from InputStream into the Buffer
            input.read(buffer)

            // Create a json String
            jsonString = String(buffer)


            return jsonString

        } catch (ex: Exception)
        {
            ex.printStackTrace()
        }
        finally {
            // Must close the stream
            input?.close()
        }

        return null

    }




    fun saveToDataFolder(context: Context, filename: String, translator: List<Translator>) {

        try {
            val ofile:FileOutputStream = context.getApplicationContext().openFileOutput(filename, AppCompatActivity.MODE_PRIVATE)
            val osw = OutputStreamWriter(ofile)
            var jsonList = Gson().toJson(translator)

            for(language in jsonList)
            {
                osw.write(language.toString())
            }
            osw.flush()
            osw.close()
        }
        catch (ioe: IOException)
        {
            ioe.printStackTrace()
        }

    }



    fun getJsonFromDataFolder(context: Context, filename:String):String?
    {
        val jsonString:String

        try
        {
            // Use bufferedReader
            val isr = InputStreamReader(context.getApplicationContext().openFileInput(filename))
            jsonString = isr.buffered().use { it.readText() }
        }
        catch (ioException: IOException)
        {
            ioException.printStackTrace()
            return null
        }

        return jsonString

    } // END getJsonFromDataFolder()



    fun updateJsonInDataFolder(context: Context, filename: String, translator: List<Translator>): Boolean {

        try {
            val ofile:FileOutputStream = context.getApplicationContext().openFileOutput(filename, AppCompatActivity.MODE_PRIVATE)
            val osw = OutputStreamWriter(ofile)
            var jsonList = Gson().toJson(translator)

            for(language in jsonList)
            {
                osw.write(language.toString())
            }
            osw.flush()
            osw.close()

            return true
        }
        catch (ioe: IOException)
        {
            ioe.printStackTrace()
            return false
        }

    } // END updateJsonInData()






} // END Utils class