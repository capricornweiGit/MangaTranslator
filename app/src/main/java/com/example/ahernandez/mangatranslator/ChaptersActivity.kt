package com.example.ahernandez.mangatranslator

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ahernandez.mangatranslator.adapters.ItemAdapter

class ChaptersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)

        // Hide the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        // Get poster image name and set ImageView to it
        var posterName: String? = getIntent().getStringExtra("comicName")

        var imageviewHeader: ImageView = findViewById(R.id.imageViewHeader)
        imageviewHeader.setScaleType(ImageView.ScaleType.MATRIX);

        val res: Resources = resources
        val resID: Int = res.getIdentifier(posterName, "drawable", packageName)
        imageviewHeader.setImageResource(resID)

        // Set the LayoutManager used by the RecyclerView
        var recycler_view_items: RecyclerView = findViewById(R.id.recycler_view_items)
        recycler_view_items.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter class and pass in content list
        val itemAdapter = ItemAdapter(this, getItemsList())

        // Inflate items
        recycler_view_items.adapter = itemAdapter

    } // END onCreate()



    override fun onResume() {
        super.onResume()

        // Hide the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

    } // END onResume()



    override fun onBackPressed() {
        finish()

    } // END onBackPressed()



    private fun getItemsList(): ArrayList<String> {

        val list = ArrayList<String>()
        var mangaName: String? = getIntent().getStringExtra("comicName")
        var chaptersArray: ArrayList<String>? = getIntent().getSerializableExtra("chaptersInSeriesList") as ArrayList<String>?

        if (chaptersArray != null) {

            // Load availble chapter names
            if(chaptersArray.size > 0) {

                for(i in 0 until chaptersArray.size) {

                    list.add("$mangaName Chapter " + chaptersArray[i])
                }

            }
            else {

                // Load 30 fake chapters
                for(i in 1..30) {

                    list.add("Chapter $i")
                }
            }

        } // END outer IF

        return list

    } // END getItemsList()



} // END ChaptersActivity class