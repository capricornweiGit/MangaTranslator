package com.example.ahernandez.mangatranslator.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.ahernandez.mangatranslator.R
import com.example.ahernandez.mangatranslator.model.PosterItem

class PosterAdapter(var context: Context, var arrayList: ArrayList<PosterItem>): BaseAdapter() {

    override fun getItem(position: Int): Any {
        return arrayList.get(position)

    } // END getItem()



    override fun getItemId(position: Int): Long {
        return position.toLong()

    } // END getItemId()



    override fun getCount(): Int {
        return arrayList.size

    } // END getCount()



    override fun getView(position: Int, converterView: View?, parent: ViewGroup?): View {

        var view: View = View.inflate(context, R.layout.card_view_item_grid, null)
        var posters: ImageView = view.findViewById(R.id.posters)
        var names: TextView = view.findViewById(R.id.name_text_view)

        var listItem: PosterItem = arrayList.get(position)

        posters.setImageResource(listItem.posters!!)
        posters.setTag(listItem.name)

        names.text = listItem.name

        return view

    } // END getView()



} // END PosterAdapter class