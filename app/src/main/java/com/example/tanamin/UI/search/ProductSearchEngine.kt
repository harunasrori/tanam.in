package com.example.tanamin.UI.search

import android.content.Context
import android.util.Log
import com.example.tanamin.model.Product
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class ProductSearchEngine(private val context: Context) {

    private var rfbaseProduct: FirebaseFirestore

    private var ProductArrayList: ArrayList<Product> = arrayListOf<Product>()


    init {
        rfbaseProduct = FirebaseFirestore.getInstance()
    }

    fun search(query: String): ArrayList<Product> {
        Thread.sleep(2000)
        Log.d("Searching", "Searching for $query")
        rfbaseProduct.collection("product").orderBy("nama_product")
            .startAt(query)
            .endAt(query + "\uf8ff").addSnapshotListener { data, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }

                for (dc: DocumentChange in data?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {

                        ProductArrayList.add(dc.document.toObject(Product::class.java))
                    }
                }
            }

        Log.d("Searching", "Resulting with for $query")
        return ProductArrayList
    }



}