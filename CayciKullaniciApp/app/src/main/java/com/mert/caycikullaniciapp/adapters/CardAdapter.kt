package com.mert.caycikullaniciapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mert.caycikullaniciapp.R
import com.mert.caycikullaniciapp.classes.Urün
import kotlinx.android.synthetic.main.cards.view.*

class CardAdapter (private val urunler  : List<Urün>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    class CardViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cards,parent,false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.itemView.text1.text = urunler[position].isim
        //var sayi = 0
        //holder.itemView.text2.text = ürünler[position].toString()
        holder.itemView.resim.setImageResource(urunler[position].kapak.toInt())
        holder.itemView.azaltButton.setOnClickListener {
            if(urunler[position].adet > 0)
            {
                urunler[position].adet--
                holder.itemView.text3.text = urunler[position].adet.toString()
            }
        }

        holder.itemView.arttirButton.setOnClickListener {
            urunler[position].adet++
            holder.itemView.text3.text = urunler[position].adet.toString()
        }

    }

    override fun getItemCount(): Int = urunler.size


}