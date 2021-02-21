package com.example.ahernandez.mangatranslator.adapters
/*
    Note: Original MyRecyclerAdapter provided by Lianne Wong.
          Modified for project purposes.
 */

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.ahernandez.mangatranslator.R

class MyRecyclerAdapter(private val myDataset: ArrayList<Bitmap>) :
    RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyRecyclerAdapter.MyViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_layout, parent, false) as View

        // set the view's size, margins, paddings and layout parameters
        //val lp = view.layoutParams
        //lp.height = parent.measuredHeight
        //view.layoutParams = lp

        return MyViewHolder(view)

    } // END onCreateViewHolder()



    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.findViewById<ImageView>(R.id.imageViewRecyclerItem).setImageBitmap(myDataset[position])

    } // END onBindViewHolder()



    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size



} // END MyRecyclerAdapter class