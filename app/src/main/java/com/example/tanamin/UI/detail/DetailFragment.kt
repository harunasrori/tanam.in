package com.example.tanamin.UI.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tanamin.model.Product
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DetailFragment : Fragment(), View.OnClickListener {

    val args: DetailFragmentArgs by navArgs()

    private var fStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var uid: String

    private lateinit var rfbaseProduct: FirebaseFirestore

    private var isFavorite = false

    private lateinit var binding: FragmentDetailBinding

    private lateinit var tokopedia_url: String

    private var product_data: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rfbaseProduct = FirebaseFirestore.getInstance()

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore.getInstance()
            .collection("product")
            .document(args.pid).get()
            .addOnSuccessListener { productData ->
                binding.tvNamaProduk.text = productData.getString("nama_product")
                val harga = productData.getString("harga")
                binding.tvHargaProduk.text = "Rp. $harga"
                binding.tvDeskripsi.text = productData.getString("deskripsi")
                    ?.replace("\\n", "\n")
                    ?.replace("\t", "\t\t\t")
                Glide.with(this)
                    .load(productData.getString("photo"))
                    .apply(RequestOptions())
                    .into(binding.imgItemPhoto)
                tokopedia_url = productData.getString("link_tokopedia").toString()

            }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        checkFavorites()
        binding.btnBack.setOnClickListener(this)
        binding.btnFavorite.setOnClickListener(this)
        binding.btnToTokopedia.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                findNavController().navigate(R.id.action_detailFragment_to_searchFragment)
            }
            R.id.btn_to_tokopedia -> {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(tokopedia_url)
                startActivity(openURL)
            }
            R.id.btn_favorite -> {
                if (isFavorite) {
                    deleteFavorite()
                } else {
                    addFavorite()
                }

            }
        }
    }

    fun deleteFavorite() {
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(uid).collection("favorites")
            .document(args.pid).delete()
        isFavorite = false
        binding.btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        Toast.makeText(context, "Membatalkan Favorite", Toast.LENGTH_SHORT).show()
    }

    fun addFavorite() {
        Toast.makeText(context, "Menambahkan ke Favorite", Toast.LENGTH_SHORT).show()
        val product = HashMap<String, Any>()
        val query = FirebaseFirestore.getInstance().collection("product").document(args.pid)
        query.get().addOnSuccessListener {
            val sdf = SimpleDateFormat("dd MMMM yyyy")
            val currentDate = sdf.format(Date())
            product.put("nama_product", it.getString("nama_product").toString())
            product.put("deskripsi", it.getString("deskripsi").toString())
            product.put("harga", it.getString("harga").toString())
            product.put("kategori", it.getString("kategori").toString())
            product.put("photo", it.getString("photo").toString())
            product.put("pid", it.getString("pid").toString())
            product.put("date", currentDate)

            fStore.collection("Users").document(uid).collection("favorites")
                .document(args.pid).set(product)
            binding.btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            isFavorite = true
        }
    }

    fun checkFavorites() {
        isFavorite = false
        binding.btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(uid).collection("favorites")
            .document(args.pid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d("TAG", "Document already exists.")
                            isFavorite = true
                            binding.btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                        } else {
                            Log.d("TAG", "Document doesn't exist.")
                        }
                    }
                } else {
                    Log.d("TAG", "Error: ", task.exception)
                }
            }

    }

}