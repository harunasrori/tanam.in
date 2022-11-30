package com.example.tanamin.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailFragment : Fragment() {

    val args: DetailFragmentArgs by navArgs()

    private lateinit var rfbaseProduct: FirebaseFirestore

    private lateinit var binding: FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rfbaseProduct = FirebaseFirestore.getInstance()

        FirebaseFirestore.getInstance()
            .collection("destinasi")
            .document(args.pid).get()
            .addOnSuccessListener { document ->
                binding.tvNamaProduk.text = document.getString("nama_product")
                val harga = document.getString("harga")
                binding.tvHargaProduk.text = "Rp. $harga"
                binding.tvDeskripsi.text = document.getString("deskripsi")
                Glide.with(this)
                    .load(document.getString("photo"))
                    .apply(RequestOptions())
                    .into(binding.imgItemPhoto)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

//        binding.tvNamaProduk.text = args.pid

        return binding.root
    }

}