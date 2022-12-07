package com.example.tanamin.Home.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tanamin.Home.product.Product
import com.google.firebase.firestore.FirebaseFirestore

class DetailFragmentViewModel : ViewModel() {

    val TAG = "FIRESTORE_VIEW_MODEL"
    var firestoreDB = FirebaseFirestore.getInstance()
    var nama_product : String = ""
    var productData: MutableLiveData<Product> = MutableLiveData()

    // save address to firebase
//    fun saveAddressToFirebase(addressItem: Product){
//        firebaseRepository.saveAddressItem(addressItem).addOnFailureListener {
//            Log.e(TAG,"Failed to save Address!")
//        }
//    }


    // get realtime updates from firebase regarding saved addresses
    fun getProductData(pid: String): MutableLiveData<Product> {
        firestoreDB.collection("product")
            .document(pid).get()
            .addOnSuccessListener { document ->
                productData.value = document.toObject(Product::class.java)!!
                nama_product = productData.value!!.nama_product
                Log.d("DetailFragmentViewModel", "data : ${productData.value!!.nama_product}")

            }
        return productData
    }

    // delete an address from firebase
//    fun deleteAddress(addressItem: AddressItem){
//        firebaseRepository.deleteAddress(addressItem).addOnFailureListener {
//            Log.e(TAG,"Failed to delete Address")
//        }
//    }

}