package com.example.ahernandez.mangatranslator

import android.content.res.Configuration
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ahernandez.mangatranslator.adapters.MyRecyclerAdapter
import com.example.ahernandez.mangatranslator.model.Translator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

var imageCutSize = 6

class ComicActivity : AppCompatActivity() {

    /**************
     * VARIABLES  *
     **************/

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var myDataset = ArrayList<Bitmap>()
    private var origPanelArray = ArrayList<Bitmap>()

    private var progressIncrement = 0
    private val MAX_PROGRESS = 1000
    private val BASE_INCR = 20
    private val INIT_PROG_JUMP = 100

    private val utils = Utils()
    lateinit var translator:List<Translator>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comic)

        // Hide the status bar only
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        // Define ProgressBar
        var progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.max = MAX_PROGRESS

        // Run following on thread to update progress bar
        CoroutineScope(Dispatchers.Default).launch {

            try {

                // Get path to chapter images
                var pathStr: String? = getIntent().getStringExtra("strPath")

                // To reach Assets folder
                val assetManager = assets
                // Get all name of all image files for specific path
                val images = pathStr?.let { assetManager.list(it) }

                // Iterate and add each as a Bitmap for the OCR
                for (i in images!!.indices) {

                    origPanelArray.add(BitmapFactory.decodeStream(assets.open(pathStr + images[i])))
                }
            } catch (e: IOException) {
                // you can print error or log.
            }

            //progressIncrement = origPanelArray.size * imageCutSize
            progressBar.progress = INIT_PROG_JUMP
            var barIncr = BASE_INCR

            var bitmapArray: ArrayList<Bitmap>

            // Split panels into manageable pieces for OCR
            bitmapArray = splitBitmapPanels(origPanelArray, progressBar, barIncr)
            // Run OCR on current pieces
            processImage(bitmapArray, progressBar)

        } // END CoroutineScope()

    } // END OnCreate()


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {

            // Hide the status bar
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {

            // Hide the status bar
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }

    } // END onConfigurationChanged()



    override fun onResume() {
        super.onResume()

        // Hide the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

    } // END onResume()



    override fun onBackPressed() {
        finish()

    } // END onBackPressed()



    fun processImage(bitmapArray: ArrayList<Bitmap>, progressBar: ProgressBar) {

        // Retrieve JSON language file
        val jsonFileString = utils.getJsonFromDataFolder(this, "language.json")
        // TypeToken to get the type of the object
        val listTranslatorType = object: TypeToken<List<Translator>>() {}.type

        // Get current target language set by user to use for translation
        translator = Gson().fromJson(jsonFileString, listTranslatorType)
        var targetLang: String = translator[0].target.toString()

        if (targetLang == "spanish")
        {
            targetLang = "es"
        }
        else if (targetLang == "french") {
            targetLang = "fr"
        }

        // Pass in target language, using English as base
        val options = TranslateLanguage.fromLanguageTag(targetLang)?.let {
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(it)
                .build()
        }

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        // Initialize translator
        val englishToTargetTranslator = options?.let { Translation.getClient(it) }

        if (englishToTargetTranslator != null) {
            getLifecycle().addObserver(englishToTargetTranslator)
        }

        // Re-adjust progress bar increment
        progressIncrement = (1000 - progressBar.progress) / bitmapArray.size

        // Process each mini-image in array
        for(miniImage in bitmapArray) {

            var newBitmap: Bitmap? = null

            val image = InputImage.fromBitmap(miniImage, 0)
            val recognizer = TextRecognition.getClient()

            // Listener having OCR recognize text in image
            recognizer.process(image).addOnSuccessListener { visionText ->

                // Create new Bitmap holding same dimensions as mini-image
                newBitmap = Bitmap.createBitmap(miniImage.width, miniImage.height, Bitmap.Config.ARGB_8888)

                // Apply it to canvas
                val newCanvas = newBitmap?.let { Canvas(it) }
                newCanvas?.drawBitmap(miniImage, 0f, 0f, null)

                progressBar.progress += progressIncrement

                // Iterate through each block of text found in image
                for (block in visionText.textBlocks) {

                    for ( y in 0 until block.lines.size) {

                        var line = block.lines[y]
                        val lineText = line.text
                        val lineFrame = line.boundingBox

                        // Set Paint characteristics
                        val paintText = Paint().apply {
                            isAntiAlias = true
                            style = Paint.Style.FILL
                            color = Color.RED
                            strokeWidth = 20f
                            textSize = 35f
                        }

                        // Listener to translate each line of text found by OCR ML Kit
                        if (englishToTargetTranslator != null) {
                            englishToTargetTranslator.translate(lineText)
                                .addOnSuccessListener { translatedText ->

                                    // Translation successful
                                    if (newCanvas != null) {

                                        if (lineFrame != null) {

                                            var mpaint = Paint()
                                            mpaint.setColor(Color.WHITE)
                                            mpaint.setStyle(Paint.Style.FILL)

                                            // Draw white rectange background to overwrite existing text
                                            newCanvas.drawRect(lineFrame.left.toFloat(), lineFrame.top.toFloat(), lineFrame.right.toFloat(), lineFrame.bottom.toFloat(), mpaint);

                                            // Draw translated text over it
                                            newCanvas.drawText(translatedText, lineFrame.left.toFloat(), lineFrame.bottom.toFloat(), paintText)

                                            // Apply canvas and add to Bitmap only if all text
                                            // found in picture has been added
                                            if(line == block.lines.last() && block == visionText.textBlocks.last()){

                                                newBitmap?.let { Canvas(it) }?.apply { newCanvas }
                                                progressBar.progress = 1000

                                                // Add processed image to ArrayList
                                                newBitmap?.let { myDataset.add(it) }

                                                setRecyclerView(myDataset, progressBar)

                                            }

                                        } // END Inner IF

                                    } // END Outer IF

                                }
                                .addOnFailureListener { exception ->
                                    // Error.
                                    println(exception)
                                }

                        } // END OUTER IF

                    } // END INNER FOR

                } // END OUTER FOR

            }.addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(this, "OCR Failed!", Toast.LENGTH_LONG).show()
            }

        } // END for(imgTxt in bitmapArray)

    } // END processImage()



    fun setRecyclerView(myDataset: ArrayList<Bitmap>, progressBar: ProgressBar) {

        // RecyclerView code
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyRecyclerAdapter(myDataset)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {


            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        if(this::recyclerView.isInitialized){

            // Disable visibility on progress bar and textview
            progressBar.visibility = View.GONE

            var statusTV: TextView = findViewById(R.id.statusTextView)
            statusTV.visibility = View.GONE
        }

    } // END setRecyclerView()



    fun splitBitmapPanels(origPanelArray: ArrayList<Bitmap>, progressBar: ProgressBar, barIncr: Int): ArrayList<Bitmap> {

        var bitmapArray = ArrayList<Bitmap>()

        // Split panels into several pieces
        for (y in 0 until origPanelArray.size){

            // Set image cuts depending on image height
            if(origPanelArray[y].height < 7500) {
                imageCutSize = 4
            }

            if(origPanelArray[y].height < 5000) {
                imageCutSize = 2
            }

            if (origPanelArray[y].height < 3000) {
                imageCutSize = 1
            }

            if(origPanelArray[y].height > 10000) {
                imageCutSize = 8
            }


            val pixelByRow = origPanelArray[y].height / imageCutSize

            // IF image is less than 3000, do not cut
            if (imageCutSize != 1) {
                for (x in 0 until imageCutSize) {

                    val testImg: Bitmap = Bitmap.createBitmap(origPanelArray[y], 0, (origPanelArray[y].height / imageCutSize) * x, origPanelArray[y].width, pixelByRow)
                    bitmapArray.add(testImg)

                    progressBar.progress += barIncr / 2
                }
            }
            else {
                bitmapArray.add(origPanelArray[y])

                progressBar.progress += barIncr / 2
            }

            // Set cut default back to original
            imageCutSize = 6

        } // END FOR loop

        // Clear initial array contents
        origPanelArray.clear()

        return bitmapArray

    } // END splitBitmapPanels()



} // END ComicActivity class