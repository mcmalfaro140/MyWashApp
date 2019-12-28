package com.example.myfinalproject.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.myfinalproject.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.user_order_items_row_view.view.*

class UsersOrdersAdapter(private val context: Context,
                         private var dataSource: ArrayList<String>
) : BaseAdapter() {



    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var  db = FirebaseFirestore.getInstance()




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


        //returns the row view of the customize list
        val rowView = inflater.inflate(R.layout.user_order_items_row_view, parent, false)
        val cancelbtn: Button = rowView.findViewById(R.id.btnCancel)
        val title: TextView = rowView.findViewById(R.id.textHeaderPro)



        var temp = getItem(position).split("-")




        // Get provider element
        val name :TextView = rowView.nametextView
        // name.text = getItem(position)
        name.text = temp[0]


        //phone
       // val phone :TextView = rowView.phonetextView
       // phone.text =  temp[1]


        //address
        val location: TextView = rowView.addresstextView
        location.text = temp[2]


        //date
        val date: TextView = rowView.datetextView
        date.text = temp[3]

        //washtype
        val washType :TextView = rowView.washTypetextView
        // name.text = getItem(position)
        washType.text = temp[4]


        //order status
        val orderStatus :TextView = rowView.orderStatustextView

        orderStatus.text = temp[5]

        if(temp[5].equals("Canceled") or temp[5].equals("Completed") or temp[5].equals("Decline")){
            title.text = "Pass Order"
            cancelbtn.setVisibility(View.GONE)
        }


        cancelbtn.setOnClickListener {

            cancel(temp[6])
            cancelbtn.setVisibility(View.GONE)
            orderStatus.text = "Canceled"
            title.text = "Pass Order"

        }



        return  rowView


    }

    private fun cancel(id:String){
        val docRef = db.collection("orders").document(id)
        db.runTransaction { transaction ->
            transaction.update(docRef, "OrderStatus", "Canceled")
        }
    }



}
