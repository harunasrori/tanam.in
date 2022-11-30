package com.example.tanamin.Home.product

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tanamin.R

class ProductAdapter(var listProduct: ArrayList<Product>, mListener: IUProduct) :
    RecyclerView.Adapter<ProductAdapter.GridViewHolder>() {

    var products: ArrayList<Product> = ArrayList()
        set(value) {
            listProduct = value
            notifyDataSetChanged()
        }

    private lateinit var onItemClickCallback: OnItemClickCallback

    private var mListener = mListener

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GridViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_product, viewGroup, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val product = listProduct[position]

        holder.itemView.apply {
            Glide.with(holder.itemView.context)
                .load(listProduct[position].photo)
                .apply(RequestOptions())
                .into(holder.imgPhoto)
            holder.imgPhoto.setOnClickListener { onItemClickCallback.onItemClicked(listProduct[holder.adapterPosition]) }
            holder.tvNamaProduk.text = product.nama_product
            holder.tvHargaProduk.text = product.harga
        }.setOnClickListener { mView ->
            Log.d("onClick ProductAdapter", "toDetail di panggil")
            mListener.toDetail(listProduct[position].pid)

        }

    }

    override fun getItemCount(): Int {
        return listProduct.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvNamaProduk: TextView = itemView.findViewById(R.id.tv_nama_produk)
        var tvHargaProduk: TextView = itemView.findViewById(R.id.tv_harga_produk)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

    interface IUProduct {
        fun toDetail(idProduct: String) {}
    }

}
