package com.mert.caycikullaniciapp.adapters

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mert.caycikullaniciapp.R
import com.mert.caycikullaniciapp.activities.SahipArayuzActivity
import com.mert.caycikullaniciapp.classes.Kullanici
import com.mert.caycikullaniciapp.classes.Urün
import kotlinx.android.synthetic.main.activity_arayuz_recycler_adapter.view.*
import kotlinx.android.synthetic.main.activity_sahip_arayuz.view.*
import kotlin.contracts.contract


class ArayuzRecyclerAdapter(
    val siparisVerenListesi: ArrayList<Kullanici>,
    context: SahipArayuzActivity
) : RecyclerView.Adapter<ArayuzRecyclerAdapter.OrderHolder>() {
    //Recycler ile bağlantı kurabilmek için bir adapter sınıfı oluşturduk

    private lateinit var database: FirebaseFirestore

    val context = context

    val orders = ArrayList<String>()
    var hold = 0


    class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //tutucu kullandık

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val inflater = LayoutInflater.from(parent.context)
        //Recycler row u buraya bağladık

        val view = inflater.inflate(R.layout.activity_arayuz_recycler_adapter, parent, false)
        //Görünümü oluşturduk

        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        println(R.drawable.soda)
        try {

            holder.itemView.LinearLayout.visibility = View.VISIBLE

            database = FirebaseFirestore.getInstance()
            //burada da yapılacakları söylüyoruz
            holder.itemView.recycler_row_user_email.text = siparisVerenListesi[position].userEmail

            holder.itemView.recycler_row_order_date.text = siparisVerenListesi[position].orderDate

            orders.clear()
            hold = 0

            var tutucu : String = ""

            //holder.itemView.textttttt.setText(siparisVerenListesi[position].userOrder[hold])

            while (hold < siparisVerenListesi[position].userOrder.size) {

                tutucu += siparisVerenListesi[position].userOrder[hold]
                tutucu += "\n"
                //println("girdim" + hold)
                //orders.add(siparisVerenListesi[position].userOrder[hold])
                hold++
            }


            //val adapter = ArrayAdapter(context,android.R.layout.simple_list_item_1,orders)
            //holder.itemView.listView.adapter = adapter

            holder.itemView.recycler_row_order_list.setText(tutucu)

            //holder.itemView.recycler_row_order_list.text = siparisVerenListesi[position].userOrder.toString()

            var id = siparisVerenListesi[position].id
            holder.itemView.gonderildiButton.setOnClickListener {
                holder.itemView.LinearLayout.visibility = View.INVISIBLE
                database.collection("KullaniciSiparisi").document(id).delete()
            }


            /*var number = siparisVerenListesi[position].userOrder.size
            while (number>0)
            {
                holder.itemView.recycler_row_order_list.text = siparisVerenListesi[position].userOrder[number]
                number--
            }

             */

        } catch (e: Exception) {
            Toast.makeText(context,e.localizedMessage,Toast.LENGTH_LONG).show()
            println("Adapterdeyim")
        }

    }

    override fun getItemCount(): Int {
        //println(postList.size)
        return siparisVerenListesi.size
        //Primary constructor da listeyi istedik ve aldığımız listenin uzunluğunu burada döndürdük
    }

}