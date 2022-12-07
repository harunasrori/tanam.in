package com.example.tanamin.Home.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tanamin.Home.product.Product
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragmentViewModel() : ViewModel() {

    val TAG = "FIRESTORE_VIEW_MODEL"
    var products: MutableLiveData<List<Product>> = MutableLiveData()

    private lateinit var rfbaseProduct: FirebaseFirestore

    fun getProduct() : LiveData<List<Product>>{
        return products
    }

    fun loadAllProduct(): LiveData<List<Product>> {
        rfbaseProduct = FirebaseFirestore.getInstance()
        var productsList: MutableList<Product> = mutableListOf()
        rfbaseProduct.collection("product").addSnapshotListener { data, error ->
            if (error != null) {
                Log.e("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }

            for (dc: DocumentChange in data?.documentChanges!!) {

                if (dc.type == DocumentChange.Type.ADDED) {

                    productsList.add(dc.document.toObject(Product::class.java))
                }
                products.value = productsList
            }
        }
        return products
    }

    fun loadProductbyCategory(category: String): LiveData<List<Product>> {
        rfbaseProduct = FirebaseFirestore.getInstance()
        var productsList: MutableList<Product> = mutableListOf()
        rfbaseProduct.collection("product").whereEqualTo("kategori", category)
            .addSnapshotListener { data, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }

                for (dc: DocumentChange in data?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {

                        productsList.add(dc.document.toObject(Product::class.java))
                    }
                    products.value = productsList
                }
            }
        return products
    }


}