package com.example.tanamin.Home.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView

import androidx.navigation.fragment.findNavController
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanamin.Home.product.Product
import com.example.tanamin.Home.product.ProductAdapter
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentSearchBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment(), ProductAdapter.IUProduct {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var rfbaseProduct: FirebaseFirestore
    private lateinit var ProductAdapter: ProductAdapter

    private lateinit var rvProduct: RecyclerView

    private var ProductArrayList: ArrayList<Product> = arrayListOf<Product>()

    private lateinit var db: DocumentReference
    private var uid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        rvProduct = binding.rvProduct
        rvProduct.setHasFixedSize(true)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProduct()
    }

    fun loadProduct() {
        ProductAdapter = ProductAdapter(ProductArrayList, this)
        rfbaseProduct = FirebaseFirestore.getInstance()
        rfbaseProduct.collection("product").addSnapshotListener { data, error ->
            if (error != null) {
                Log.e("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }

            for (dc: DocumentChange in data?.documentChanges!!) {

                if (dc.type == DocumentChange.Type.ADDED) {

                    ProductArrayList.add(dc.document.toObject(Product::class.java))
                }
            }
            ProductAdapter.notifyDataSetChanged()
        }

        rvProduct.layoutManager = GridLayoutManager(context, 2)
        rvProduct.adapter = ProductAdapter

        ProductAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showSelectedProduct(data)
            }
        })

    }

    private fun showSelectedProduct(product: Product) {
        Toast.makeText(context, "Kamu memilih " + product.nama_product, Toast.LENGTH_SHORT).show()
    }

    override fun toDetail(idDestinasi: String) {
        super.toDetail(idDestinasi)
//        val intent = Intent(context, DetailActivity::class.java)
//        intent.putExtra(DetailActivity.idDestinasi, idDestinasi)
//        startActivity(intent)
    }


}

