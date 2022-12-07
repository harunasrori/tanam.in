package com.example.tanamin.Home.carousel

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tanamin.R
import java.util.ArrayList

class CarouselAdapter internal constructor(
    private val listCarousel: ArrayList<Carousel>,
    mListener: IUKategori
) :
    RecyclerView.Adapter<CarouselAdapter.CardViewViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    private var mListener = mListener

    val noOfItems = 3

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_carousel, viewGroup, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {

        val carousel = listCarousel[position]
        holder.itemView.apply {

        Glide.with(holder.itemView.context)
            .load(carousel.photo)
            .apply(RequestOptions().override(1065, 492))
            .into(holder.imgPhoto)
        }.setOnClickListener { mView ->
            Log.d("onClick ProductAdapter", "toDetail di panggil")
            mListener.ToSearch(position)
            Toast.makeText(
                holder.itemView.context,
                "Kamu memilih " + listCarousel[holder.adapterPosition].kategori,
                Toast.LENGTH_SHORT
            ).show()

        }
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun getItemCount(): Int {
//        return Integer.MAX_VALUE;
        return listCarousel.size
    }


    inner class CardViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Carousel)
    }

    interface IUKategori {
        fun ToSearch(idx_kategori: Int) {}
    }
}