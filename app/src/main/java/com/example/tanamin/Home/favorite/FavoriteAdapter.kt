package com.example.tanamin.Home.favorite
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tanamin.R

class FavoriteAdapter(private val listFavorite: ArrayList<Favorite>, mListener : IUProduct) :
    RecyclerView.Adapter<FavoriteAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    private var mListener = mListener

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View, mListener : IUProduct) : RecyclerView.ViewHolder(itemView) {

        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvNamaProduk: TextView = itemView.findViewById(R.id.tv_nama_produk)
        var tvHargaProduk: TextView = itemView.findViewById(R.id.tv_harga_produk)
        var tvDeskripsiProduk : TextView = itemView.findViewById(R.id.tv_deskripsi)
        var tvKategoriProduk : TextView = itemView.findViewById(R.id.tv_kategori_produk)

        var mListener = mListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_favorites_list, viewGroup, false)
        return ListViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentfavorite = listFavorite[position]
        if (currentfavorite.equals(null)) {
            println("KOSONG")
        }

        holder.itemView.apply {
            holder.tvNamaProduk.text = currentfavorite.nama_product
            holder.tvHargaProduk.text = currentfavorite.harga
            holder.tvDeskripsiProduk.text = currentfavorite.deskripsi
            holder.tvKategoriProduk.text = currentfavorite.kategori

            Glide.with(holder.itemView.context)
                .load(currentfavorite.photo)
                .apply(RequestOptions())
                .into(holder.imgPhoto)
        }.setOnClickListener{ mView ->

            mListener.toDetail(currentfavorite.pid.toString())
//            val toDetail = HomeFragmentDirections.actionHomeFragmentToDetailFragment2(currentdestinasi.id)
//            mView.findNavController().navigate(toDetail)

        }
    }

    override fun getItemCount(): Int {
        return listFavorite.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Favorite)

    }

    interface IUProduct {
        fun toDetail(idProduct : String){}
    }


}