package com.example.ahernandez.mangatranslator

import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import com.example.ahernandez.mangatranslator.adapters.PosterAdapter
import com.example.ahernandez.mangatranslator.model.PosterItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException

class LibraryActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var posterList: ArrayList<PosterItem>? = null
    private var gridView: GridView? = null
    private var posterAdapter: PosterAdapter? = null

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        // Initialize variables
        gridView = findViewById(R.id.gridViewList)
        posterList = getPostersList()

        posterAdapter = PosterAdapter(applicationContext, posterList!!)
        gridView?.adapter = posterAdapter
        gridView?.onItemClickListener = this

        // Set Library as currently selected navigation item
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setSelectedItemId(R.id.navigationLibrary)

        // Perform ItemSelectedListener
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

        }

    } // END onCreate()



    override fun onBackPressed() {
        finish()
        bottomNavigationView.setSelectedItemId(R.id.navigationSearch)

    } // END onBackPressed()



    private fun getPostersList(): ArrayList<PosterItem> {

        var arrayList: ArrayList<PosterItem> = ArrayList()

        // Retrieved from Drawables folder
        arrayList.add(PosterItem(R.drawable.the_ghostly_doctor, "The Ghostly Doctor"))
        arrayList.add(PosterItem(R.drawable.martial_master, "Martial Master"))
        arrayList.add(PosterItem(R.drawable.peerless_alchemist, "Peerless Alchemist"))
        arrayList.add(PosterItem(R.drawable.martial_peak, "Martial Peak"))
        arrayList.add(PosterItem(R.drawable.the_peerless_doctor, "The Peerless Doctor"))
        arrayList.add(PosterItem(R.drawable.peerless_battle_spirit, "Peerless Battle Spirit"))

        return arrayList

    } // END getPostersList()



    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        // Get current item clicked
        var items: PosterItem = posterList!!.get(position)

        // Get all chapters available in manga series
        var mngr: AssetManager = applicationContext.assets

        var chaptersInSeriesList: ArrayList<String> = displayFiles(mngr, items.name.toString())

        // Format comic name
        var mangaName: String? = items.name

        if (mangaName != null) {
            mangaName = mangaName.replace("\\s".toRegex(), "_").toLowerCase()
        }

        // Navigate to Chapters Activity
        val intent = Intent(this, ChaptersActivity::class.java)
        intent.putExtra("chaptersInSeriesList", chaptersInSeriesList)
        intent.putExtra("comicName", mangaName)

        startActivity(intent)

    } // END onItemClick()



    private fun displayFiles(mgr: AssetManager, path: String): ArrayList<String> {

        var comicArr: ArrayList<String> = ArrayList()

        try {
            val list = mgr.list(path)

            if (list != null) for (i in list.indices) {

             comicArr.add(list[i].toString())

                displayFiles(mgr, path + "/" + list[i])
            }

            return comicArr

        } catch (e: IOException) {
            println(e)
            return comicArr
        }

    } // END displayFiles()



} // END LibraryActivity class()