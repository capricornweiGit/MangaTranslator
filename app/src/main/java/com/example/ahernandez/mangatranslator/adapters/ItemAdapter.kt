package com.example.ahernandez.mangatranslator.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ahernandez.mangatranslator.ComicActivity
import com.example.ahernandez.mangatranslator.R


class ItemAdapter(val context: Context, val items: ArrayList<String>):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_custom_row, parent, false
            )
        )

    } // END onCreateViewHolder()



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items.get(position)

        // Format string before setting it as chapter name
        holder.tvItem.text = item.substring(item.indexOf("C"))

        // Update background color according to the odd/even positions in list
        if (position % 2 == 0) {
            holder.cardViewItem.setBackgroundColor(
                ContextCompat.getColor(
                    context, R.color.SlateGrey
                )
            )
        } else {
            holder.cardViewItem.setBackgroundColor(
                ContextCompat.getColor(
                    context, R.color.SlateGrey
                )
            )
        }


        // OnClick not working directly in ChapterActivity.
        // Listener overriding onClick required inside the adapter class
        holder.cardViewItem.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                // Get current item clicked
                val item = items.get(position)

                // Format string into proper path
                var strArr: List<String> = item.split(" ")
                var pathStr: String = strArr[0].capitalizeWords() + "/" + strArr[2] + "/"

                // Launch ComicActivity
                val intent = Intent(context, ComicActivity::class.java)
                intent.putExtra("strPath", pathStr)
                context.startActivity(intent)

            }
        })

    } // END setOnClickListener



    override fun getItemCount(): Int {
        return items.size

    } // END getItemCount()



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextViews that each item will be added to
        val tvItem: TextView = view.findViewById(R.id.tv_item_name)
        val cardViewItem: CardView = view.findViewById(R.id.card_view_item)

    } // END ViewHolder class



    // Helper function to capitalize first character of each word
    fun String.capitalizeWords(): String = split("_").map { it.capitalize() }.joinToString(" ")



} // END ItemAdapter class