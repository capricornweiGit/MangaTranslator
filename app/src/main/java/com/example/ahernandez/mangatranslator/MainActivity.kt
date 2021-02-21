package com.example.ahernandez.mangatranslator

/*
    Coder: Osiris Anthony Hernandez - 0121158
    Date: Dec 3rd, 2020
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ahernandez.mangatranslator.model.Translator
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        copyJsonFromAssetsToData()

        checkDownloadedLanguages()

        // Set Main as selected navigation item
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setSelectedItemId(R.id.navigationSearch)

        // Check which item in custom navigator is selected
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navigationSearch -> {
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigationLibrary -> {
                    val intent = Intent(this, LibraryActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    //finish()

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigationSettings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()

                    return@setOnNavigationItemSelectedListener true
                }
            }

            return@setOnNavigationItemSelectedListener false

        } // END setOnNavigationItemSelectedListener


    } // END onCreate()


    override fun onResume() {
        super.onResume()
        bottomNavigationView.setSelectedItemId(R.id.navigationSearch)

    } // END onResume()



    override fun onBackPressed() {
        moveTaskToBack(true)
        finish()

    } // END onBackPressed()



    fun copyJsonFromAssetsToData() {

        val utils = Utils()
        val jsonString = utils.loadJsonFromAssets(this, "language.json")

        val transType = object: TypeToken<List<Translator>>() {}.type
        var translator:List<Translator> = Gson().fromJson(jsonString, transType)

        // Saving initial Assets file to Data/Data folder
        utils.saveToDataFolder(this, "language.json", translator)

    } // END copyJsonFromAssetsToData()



    fun checkDownloadedLanguages() {

        val modelManager = RemoteModelManager.getInstance()

        // Get all loaded language models and downloaded required ones only
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java).addOnSuccessListener { remoteModels ->

            var arr = remoteModels.sortedBy { it.language }.map { it.language }

            if (!arr.contains("es")) {
                downloadSpanishModel()
            }

            if (!arr.contains("fr")) {
                downloadFrenchModel()
            }

        } // END listener


    } // END checkDownloadedLanguages()




    fun downloadSpanishModel() {
        val modelManager = RemoteModelManager.getInstance()
        val spanishModel = TranslateRemoteModel.Builder(TranslateLanguage.SPANISH).build()

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        // Attempt to downloaded language model
        modelManager.download(spanishModel, conditions)
            .addOnSuccessListener {
                // Model downloaded.
                Toast.makeText(this, "Spanish Language Loaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Spanish Language Failed", Toast.LENGTH_SHORT).show()
            }

    } // END downloadSpanishModel()



    fun downloadFrenchModel() {
        val modelManager = RemoteModelManager.getInstance()
        val frenchModel = TranslateRemoteModel.Builder(TranslateLanguage.FRENCH).build()

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        // Attempt to downloaded language model
        modelManager.download(frenchModel, conditions)
            .addOnSuccessListener {
                // Model downloaded.
                Toast.makeText(this, "French Language Loaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "French Language Failed", Toast.LENGTH_SHORT).show()
            }

    } // END downloadFrenchModel()



} // END MainActivity class