package com.example.myfinalproject.Adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.myfinalproject.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.provider_orders_items_row.view.*


class ProvidersOrdersAdapter(private val context: Context,
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
        val rowView = inflater.inflate(R.layout.provider_orders_items_row, parent, false)
        val declineBtn :Button = rowView.findViewById(R.id.btnCancel)
        val acceptBtn :Button = rowView.findViewById(R.id.btnAccept)
        val title :TextView = rowView.findViewById(R.id.textHeaderPro)
        val layoutBtn:LinearLayout = rowView.findViewById(R.id.btnLayout)
        val donebtn : Button = rowView.findViewById(R.id.btnComplete)


        var temp = getItem(position).split("-")




        // Get provider element
        val client : TextView = rowView.clienttextView
        client.text = temp[0]


        //phone
        val phone :TextView = rowView.phoneProvtextView
        phone.text =  temp[1]


        //address
        val location: TextView = rowView.locationProvtextView
        location.text = temp[2]


        //date
        val date: TextView = rowView.dateProvtextView
        date.text = temp[3]

        //washtype
        val washType :TextView = rowView.washTypeProvtextView
        // name.text = getItem(position)
        washType.text = temp[4]


        //order status
        val orderStatus :TextView = rowView.orderStatusProvtextView
        orderStatus.text = temp[5]

        if(temp[5].equals("Canceled") or temp[5].equals("Completed") or temp[5].equals("Decline")){
            title.text = "Pass Order"
            layoutBtn.setVisibility(View.GONE)
            acceptBtn.setVisibility(View.GONE)
        }

        if(temp[5].equals("Accepted")){
            layoutBtn.setVisibility(View.GONE)
            donebtn.setVisibility(View.VISIBLE)
        }

        declineBtn.setOnClickListener {

            cancel(temp[6])
            layoutBtn.setVisibility(View.GONE)
            acceptBtn.setVisibility(View.GONE)
            orderStatus.text = "Decline"
            title.text = "Pass Order"

        }

        acceptBtn.setOnClickListener {

            accept(temp[6])
            layoutBtn.setVisibility(View.GONE)
            donebtn.setVisibility(View.VISIBLE)
            orderStatus.text = "Accepted"

        }

        donebtn.setOnClickListener {
            completed(temp[6])
            donebtn.setVisibility(View.GONE)
            title.text = "Pass Order"
        }






        return  rowView
    }

    private fun cancel(id:String){
        val docRef = db.collection("orders").document(id)
        db.runTransaction { transaction ->
            transaction.update(docRef, "OrderStatus", "Decline")
        }
    }

    private fun accept(id:String){
        val docRef = db.collection("orders").document(id)
        db.runTransaction { transaction ->
            transaction.update(docRef, "OrderStatus", "Accepted")
        }
    }

    private fun completed(id:String){
        val docRef = db.collection("orders").document(id)
        db.runTransaction { transaction ->
            transaction.update(docRef, "OrderStatus", "Completed")
        }
    }



}
