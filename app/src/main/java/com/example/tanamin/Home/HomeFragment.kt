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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.tanamin.Authentication.AuthenticationActivity
import com.example.tanamin.Home.carousel.Carousel
import com.example.tanamin.Home.carousel.CarouselAdapter
import com.example.tanamin.Home.carousel.carouselData
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), View.OnClickListener, CarouselAdapter.IUKategori {

    private lateinit var container: SharedPreferences

    private lateinit var fAuth: FirebaseAuth

//    private lateinit var productViewModel: ProductViewModel

    private lateinit var binding: FragmentHomeBinding
    private lateinit var carousel_layout_manager: LinearLayoutManager

    //    private lateinit var carousel_layout_manager2: LoopingLayoutManager
    private lateinit var db: DocumentReference

    private var list_carousel: ArrayList<Carousel> = arrayListOf<Carousel>()
    private lateinit var rvCarousel: RecyclerView
    private lateinit var listCarouselAdapter: CarouselAdapter

    private var uid: String = ""
    private var user_name_google: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)


//        productViewModel.getAllProductData(requireContext())!!.observe(this, object : Observer<List<Product>?>(),
//            androidx.lifecycle.Observer<List<Product>> {
//
//            override fun onChanged(t: List<Product>?) {
//                TODO("Not yet implemented")
//            }
//        })


        //        context?.let {
        //            productViewModel.getAllProductData(it)?.observe(this, Observer {
        //                noteAdapter.setData(it as ArrayList<Product>)
        //                noteList = it
        //            })
        //        }

        container = requireActivity().getSharedPreferences("dataUser", Context.MODE_PRIVATE)

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

        binding.btnTesRv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
//            Toast.makeText(
//                context,
//                "Item sekarang adalah ke - ${carousel_layout_manager.findFirstVisibleItemPosition()}",
//                Toast.LENGTH_SHORT
//            ).show()
        }

        binding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(p0.toString())
                findNavController().navigate(action)
//                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
//                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                return false
            }

        })

        showRecyclerList()

        scrollTask()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = FirebaseAuth.getInstance().currentUser!!.uid
        if (container.contains("USERNAME")) {
            loadDataLocal()
        } else {
            readUserData(uid)
        }
    }

    private fun readUserData(uid: String) {
        user_name_google = FirebaseAuth.getInstance().currentUser!!.displayName.toString()
        user_name_google =
            if (user_name_google.length == 1) user_name_google else user_name_google.split(" ")[0]
        Log.d("reading firebase", "nama user dari firebase $user_name_google")
        if (user_name_google != "") {

            binding.tvSalute.setText(
                Html.fromHtml(
                    "<font color=${Color.parseColor("#444941")}>Halo </font>" +
                            "<font color=${Color.parseColor("#5F7A61")}> ${
                                capitalize(
                                    user_name_google
                                )
                            }</font>"
                )
            )
        } else {
            db = FirebaseFirestore.getInstance().collection("Users").document(uid)
            db.get().addOnSuccessListener {
                val name = it.data?.getValue("nama").toString()

                if (name.equals("") || name.equals(null)) {
                    binding.tvSalute.setText("User")
                } else {
                    var nama_user = it.data?.getValue("nama").toString()
                    binding.tvSalute.setText(
                        Html.fromHtml(
                            "<font color=${Color.parseColor("#444941")}>Halo </font>" +
                                    "<font color=${Color.parseColor("#5F7A61")}> ${
                                        capitalize(
                                            nama_user
                                        )
                                    }</font>"
                        )
                    )
                }

            }
        }
        saveData()
    }

    private fun saveData() {
        val nama = binding.tvSalute.text.toString().split(" ")[1]
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
        binding.tvSalute.text = sharedPreferences.getString("USERNAME", "USER")
        binding.tvSalute.setText(
            Html.fromHtml(
                "<font color=${Color.parseColor("#444941")}>Halo </font>" +
                        "<font color=${Color.parseColor("#5F7A61")}> ${capitalize(nama)}</font>"
            )
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_logout -> {
                fAuth.signOut()
                startActivity(Intent(this.context, AuthenticationActivity::class.java))
            }
        }
    }

    private fun showRecyclerList() {
        carousel_layout_manager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        rvCarousel.layoutManager = carousel_layout_manager

        // Ini buat looping layout manager sehingga item akan infinite scrolling, tapi kalo cek visible child ga bisa
        //        carousel_layout_manager2 =
//            context?.let {
//                LoopingLayoutManager(
//                    it, LoopingLayoutManager.HORIZONTAL,  false                           // Pass whether the views are laid out in reverse.
//                    // False by default.
//                )
//            }!!
//        rvCarousel.layoutManager = carousel_layout_manager2


        listCarouselAdapter = CarouselAdapter(list_carousel, this)
        rvCarousel.adapter = listCarouselAdapter

        listCarouselAdapter.setOnItemClickCallback(object : CarouselAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Carousel) {
//                showSelectedHero(data)
            }
        })
    }

    private val llm: LinearLayoutManager =
        object : LinearLayoutManager(context, HORIZONTAL, false) {
            override fun smoothScrollToPosition(
                recyclerView: RecyclerView,
                state: RecyclerView.State,
                position: Int
            ) {
                val scroller: LinearSmoothScroller =
                    object : LinearSmoothScroller(context) {
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return 2000f
                        }
                    }
                scroller.targetPosition = position
                startSmoothScroll(scroller)
            }
        }

    override fun ToSearch(kategori: String) {
        super.ToSearch(kategori)
        // TODO TAMBAHKAN INTENT KE SEARCH FRAGMENT ??
//        val intent = Intent(context, DetailActivity::class.java)
//        intent.putExtra(DetailActivity.idDestinasi, idDestinasi)
//        startActivity(intent)
    }

    fun capitalize(str: String?): String? {
        return str?.capitalize() ?: str
    }

    fun scrollTask() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                rvCarousel.post(Runnable() {
                    fun run() {

                        var nextView =
                            ((carousel_layout_manager.findFirstVisibleItemPosition()) + 1) % listCarouselAdapter.itemCount
                        Toast.makeText(context, "ni delay", Toast.LENGTH_SHORT).show()
                        rvCarousel.smoothScrollToPosition(nextView);
                    }
                });
            }
        }, 2000)
    }

}

