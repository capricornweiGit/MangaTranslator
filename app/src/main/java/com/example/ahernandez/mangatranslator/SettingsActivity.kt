package com.example.ahernandez.mangatranslator

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.ahernandez.mangatranslator.model.Translator


class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var radioGroup: RadioGroup
    lateinit var spaRadBtn: RadioButton
    lateinit var frRadBtn: RadioButton
    lateinit var translator:List<Translator>

    private val utils = Utils()
    var defaultColor: Int = Color.parseColor("#B00020")
    var successColor: Int = Color.parseColor("#228B22")


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // initialize lateinit variables
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        radioGroup = findViewById(R.id.langRadioGroup)
        spaRadBtn = findViewById(R.id.spanishRadioBtn)
        frRadBtn = findViewById(R.id.frenchRadioBtn)

        getCurrentTargetLanguage()

        // Set Settings as current navigation item selected
        bottomNavigationView.setSelectedItemId(R.id.navigationSettings)

        // Check which item in custom navigator is selected
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navigationSearch -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigationLibrary -> {
                    val intent = Intent(this, LibraryActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigationSettings -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }

            return@setOnNavigationItemSelectedListener false

        }

    } // END onCreate()



    override fun onBackPressed() {
        finish()
        bottomNavigationView.setSelectedItemId(R.id.navigationSearch)

    } // END onBackPressed()





    fun getCurrentTargetLanguage() {

        // Retrieve language JSON file
        val jsonFileString = utils.getJsonFromDataFolder(this, "language.json")
        // TypeToken to get the type of the object
        val listTranslatorType = object: TypeToken<List<Translator>>() {}.type
        translator = Gson().fromJson(jsonFileString, listTranslatorType)

        // Find which language is currently set as Target
        var targetLang: String = translator[0].target.toString()

        // Check target language and change radio button color
        if (targetLang == "spanish")
        {
            spaRadBtn.setChecked(true)
            spaRadBtn.buttonTintList = (ColorStateList.valueOf(successColor))
        }
        else if (targetLang == "french")
        {
            frRadBtn.setChecked(true)
            frRadBtn.buttonTintList = (ColorStateList.valueOf(successColor))
        }

    } // END getCurrentTargetLanguage()



    fun onButtonClick(view: View) {

        // Retrieve language JSON file
        val jsonFileString = utils.getJsonFromDataFolder(this, "language.json")

        // TypeToken to get the type of the object
        val listTranslatorType = object: TypeToken<List<Translator>>() {}.type
        translator = Gson().fromJson(jsonFileString, listTranslatorType)

        // Switch statement used to determine which radio button was selected
        var checked = radioGroup.checkedRadioButtonId

        // Update JSON object w selection and change
        // radio button color to reflect success
        when (checked) {
            R.id.spanishRadioBtn -> {

                // Update value
                translator[0].target = "spanish"

                // Change radio button colors
                spaRadBtn.buttonTintList = (ColorStateList.valueOf(successColor))
                frRadBtn.buttonTintList = (ColorStateList.valueOf(defaultColor))
            }

            R.id.frenchRadioBtn -> {

                // Update value
                translator[0].target = "french"

                // Change radio button colors
                spaRadBtn.buttonTintList = (ColorStateList.valueOf(defaultColor))
                frRadBtn.buttonTintList = (ColorStateList.valueOf(successColor))
            }

        }

        // Update JSON file
        val isUpdated = utils.updateJsonInDataFolder(this, "language.json", translator)

        if (isUpdated) {
            Toast.makeText(this, "Target language set", Toast.LENGTH_SHORT).show()
        }

    } // END onButtonClick()



} // END SettingsActivity class()