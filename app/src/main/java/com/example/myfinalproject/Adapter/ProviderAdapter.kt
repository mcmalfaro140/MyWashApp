package com.example.myfinalproject.Adapter

import android.app.PendingIntent.getActivity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.myfinalproject.ApptFragment
import com.example.myfinalproject.Communicator
import com.example.myfinalproject.MapFragment
import com.example.myfinalproject.R
import kotlinx.android.synthetic.main.bookwash_item_row_layout.view.*


class ProviderAdapter(private val context: Context,
                      private var dataSource: ArrayList<String>
) : BaseAdapter() {


     val model: Communicator? = null
    //lateinit var  model: Communicator


    private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater




    override fun getItem(position: Int): String{
      return dataSource[position]
    }



    override fun getItemId(position: Int): Long {
         return position.toLong()
    }



    override fun getCount(): Int {
        return  dataSource.size//returns the number of items in the database
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

       val rowView = inflater.inflate(R.layout.bookwash_item_row_layout, parent, false)
        var btn:Button = rowView.findViewById(R.id.bookwash_button)
        var nameProvApp : TextView = rowView.findViewById(R.id.provider_for_bookwash)
        var priceWahs : TextView = rowView.findViewById(R.id.price_text)



        var temp = getItem(position).split("-")
       // Get name element
        val name :TextView = rowView.provider_for_bookwash
       // val name1 :TextView = rowView.provider_for_bookwash
        name.text = temp[0]

       //Get the price element
       val price :TextView = rowView.price_text
        price.text = "Starting price: $ ${temp[1]}"

        val provEmail = temp[2]



        btn.setOnClickListener {

            var bundle = bundleOf("name" to temp[0], "price" to temp[1], "Email" to temp[2])//saves data into the bundle
            rowView.findNavController().navigate(R.id.action_mapFragment_to_apptFragment, bundle)

        }

        return  rowView
    }

}