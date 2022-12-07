package com.example.tanamin.Home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.tanamin.Authentication.AuthenticationActivity
import com.example.tanamin.Home.carousel.Carousel
import com.example.tanamin.Home.carousel.CarouselAdapter
import com.example.tanamin.Home.carousel.carouselData
import com.example.tanamin.Home.favorite.Favorite
import com.example.tanamin.Home.favorite.FavoriteAdapter
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.util.*

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), View.OnClickListener, CarouselAdapter.IUKategori,
    FavoriteAdapter.IUProduct {

    private lateinit var container: SharedPreferences

    private lateinit var fAuth: FirebaseAuth

//    private lateinit var productViewModel: ProductViewModel

    private lateinit var binding: FragmentHomeBinding
    private lateinit var carousel_layout_manager: LinearLayoutManager

    //    private lateinit var carousel_layout_manager2: LoopingLayoutManager
    private lateinit var db: DocumentReference

    private var list_carousel: ArrayList<Carousel> = arrayListOf<Carousel>()
    private var list_favorite: ArrayList<Favorite> = arrayListOf<Favorite>()
    private lateinit var rvCarousel: RecyclerView
    private lateinit var listCarouselAdapter: CarouselAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter

    private var uid: String = ""
    private var user_name_google: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        container = requireActivity().getSharedPreferences("dataUser", Context.MODE_PRIVATE)
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        list_carousel.addAll(carouselData.listData)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fAuth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener(this)

        rvCarousel = binding.rvCarousel
        rvCarousel.setHasFixedSize(true)

        binding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToSearchFragment(p0.toString())
                findNavController().navigate(action)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        showRecyclerList_carousel()

        showRecylerview_favorite()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (container.contains("USERNAME")) {
            val sharedPreferences =
                (activity?.getSharedPreferences("dataUser", Context.MODE_PRIVATE) ?: null)!!
            var nama = sharedPreferences.getString("USERNAME", "USER")
            if (nama != null || nama != "Null") {
                loadDataLocal()
            } else {
                readUserData(uid)
            }
        } else {
            readUserData(uid)
        }
    }

    private fun readUserData(uid: String) {
        user_name_google = FirebaseAuth.getInstance().currentUser!!.displayName.toString()
        Log.d("reading google auth", "nama user dari firebase $user_name_google")
        if (user_name_google != "null") {
            user_name_google = user_name_google.split(" ")[0]

            binding.tvSalute.text = Html.fromHtml(
                "<font color=${Color.parseColor("#444941")}>Halo </font>" +
                        "<font color=${Color.parseColor("#5F7A61")}> ${
                            capitalize(
                                user_name_google
                            )
                        }</font>"
            )
            saveData(user_name_google)
        } else {
            readUserDataFromFirestore(uid)
        }
    }

    fun readUserDataFromFirestore(uid: String) {
        db = FirebaseFirestore.getInstance().collection("Users").document(uid)
        db.get().addOnSuccessListener {
            val name = it.data?.getValue("nama").toString().split(" ")[0]
            Log.d("reading firebase", "id user dari firebase $uid")
            if (name.equals("") || name.equals(null)) {
                binding.tvSalute.text = "User"
            } else {
                var nama_user = name
                binding.tvSalute.text = Html.fromHtml(
                    "<font color=${Color.parseColor("#444941")}>Halo </font>" +
                            "<font color=${Color.parseColor("#5F7A61")}> ${
                                capitalize(
                                    nama_user
                                )
                            }</font>"
                )
            }
            saveData(name)
        }

    }

    private fun saveData(nama: String = "User!") {
        Log.d("Saving data", "Nama adalah : $nama")
        val sharedPreferences =
            (activity?.getSharedPreferences("dataUser", Context.MODE_PRIVATE))
        val editor = sharedPreferences?.edit()
        editor?.apply {
            putString("USERNAME", nama)
        }?.apply()
    }

    private fun loadDataLocal() {
        val sharedPreferences =
            (activity?.getSharedPreferences("dataUser", Context.MODE_PRIVATE) ?: null)!!
        var nama = sharedPreferences.getString("USERNAME", "USER")
        Log.d("SharedPreference Load ", "nama yang ada $nama")
        binding.tvSalute.text = sharedPreferences.getString("USERNAME", "USER")
        binding.tvSalute.text = Html.fromHtml(
            "<font color=${Color.parseColor("#444941")}>Halo </font>" +
                    "<font color=${Color.parseColor("#5F7A61")}> ${capitalize(nama)}</font>"
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                fAuth.signOut()
                val SharedPreferences = activity?.getSharedPreferences("dataUser", Context.MODE_PRIVATE)
                SharedPreferences?.edit()?.clear()?.commit()
                startActivity(Intent(this.context, AuthenticationActivity::class.java))
            }
        }
    }

    private fun showRecyclerList_carousel() {
        carousel_layout_manager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        rvCarousel.layoutManager = carousel_layout_manager

        listCarouselAdapter = CarouselAdapter(list_carousel, this)
        rvCarousel.adapter = listCarouselAdapter

        listCarouselAdapter.setOnItemClickCallback(object : CarouselAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Carousel) {

            }
        })
    }

    private fun showRecylerview_favorite() {

        list_favorite.clear()
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(uid)
            .collection("favorites")
            .addSnapshotListener { data, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }
                for (dc: DocumentChange in data?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {
                        list_favorite.add(dc.document.toObject(Favorite::class.java))
                    }
                }
                favoriteAdapter.notifyDataSetChanged()
                println("Banyak data di array list : ${list_favorite.size}")

            }
        binding.rvFavoriteProduct.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        favoriteAdapter = FavoriteAdapter(list_favorite, this)
        binding.rvFavoriteProduct.adapter = favoriteAdapter
    }

    override fun toDetail(idProduct: String) {
        super.toDetail(idProduct)
        val navigateInto = HomeFragmentDirections.actionHomeFragmentToDetailFragment(idProduct)
        findNavController().navigate(navigateInto)
    }

    override fun ToSearch(idx_kategori: Int) {
        super.ToSearch(idx_kategori)
        val navigateInto = HomeFragmentDirections.actionHomeFragmentToSearchFragment("", idx_kategori)
        findNavController().navigate(navigateInto)
    }

    fun capitalize(str: String?): String? {
        return str?.capitalize() ?: str
    }


}

