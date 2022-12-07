package com.example.tanamin.Home.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanamin.Home.detail.DetailFragmentArgs
import com.example.tanamin.Home.product.Product
import com.example.tanamin.Home.product.ProductAdapter
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentSearchBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SearchFragment : Fragment(), ProductAdapter.IUProduct {

    val args: SearchFragmentArgs by navArgs()

    private lateinit var binding: FragmentSearchBinding

    private lateinit var rfbaseProduct: FirebaseFirestore
    private lateinit var ProductAdapter: ProductAdapter

    private lateinit var rvProduct: RecyclerView

    private var ProductArrayList: ArrayList<Product> = arrayListOf<Product>()

    protected lateinit var productSearchEngine: ProductSearchEngine

    private lateinit var disposable: Disposable

    private lateinit var db: DocumentReference
    private var uid: String = ""

    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productSearchEngine = ProductSearchEngine(requireContext())


        ProductAdapter = ProductAdapter(ProductArrayList, this)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        tabLayout = binding.tabsFilter
        tabLayout.addTab(tabLayout.newTab().setText("semua"))
        tabLayout.addTab(tabLayout.newTab().setText("alat"))
        tabLayout.addTab(tabLayout.newTab().setText("bahan"))
        tabLayout.addTab(tabLayout.newTab().setText("hasil"))
        tabLayout.tabGravity = TabLayout.GRAVITY_CENTER
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                loadProduct(tab.text.toString().toLowerCase())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val searchTextObservable = createButtonClickObservable()
            .toFlowable(BackpressureStrategy.LATEST) // 1

        disposable = searchTextObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showProgress() }
            .observeOn(Schedulers.io())
            .map { productSearchEngine.search(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hideProgress()
                val imm = ContextCompat.getSystemService(
                    requireView().context,
                    InputMethodManager::class.java
                )
                imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
                binding.SearchView.clearFocus()
                showResult(it)

//                binding.SearchView.onActionViewCollapsed()
            }

        rvProduct = binding.rvProduct
        rvProduct.setHasFixedSize(true)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

        return binding.root
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(args.idxKategori){
            0 -> {
                loadProduct()
            }
//            1 ->{
//                loadProduct("alat")
//            }
//            2 ->{
//                loadProduct("bahan")
//            }
//            3 ->{
//                loadProduct("hasil")
//            }
//            else -> {
//                loadProduct()
//            }
        }
        val tab = tabLayout.getTabAt(args.idxKategori)
        tab!!.select()

    }

    @Override
    override fun onStop() {
        super.onStop()
        // 1
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    fun loadProduct(kategori: String = "semua") {
        ProductArrayList.clear()
        rfbaseProduct = FirebaseFirestore.getInstance()
        if (kategori == "semua") {
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
        } else {
            rfbaseProduct.collection("product").whereEqualTo("kategori", kategori)
                .addSnapshotListener { data, error ->
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
        }

        rvProduct.layoutManager = GridLayoutManager(context, 2)
        rvProduct.adapter = ProductAdapter

        ProductAdapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Product) {
                showSelectedProduct(data)
                toDetail(data.pid)
            }
        })
    }

    private fun showSelectedProduct(product: Product) {
        Toast.makeText(context, "Kamu memilih " + product.nama_product, Toast.LENGTH_SHORT).show()
    }

    override fun toDetail(idProduct: String) {
        super.toDetail(idProduct)
        val navigateInto = SearchFragmentDirections.actionSearchFragmentToDetailFragment(idProduct)
        findNavController().navigate(navigateInto)
    }

    private fun createButtonClickObservable(): Observable<String> {

        return Observable.create<String> { emitter ->

            binding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    emitter.onNext(query!!)
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }

            })

            emitter.setCancellable {
                binding.SearchView.setOnClickListener(null)
            }
        }
    }


    private fun showResult(result: ArrayList<Product>) {
        if (result.isEmpty()) {
            Toast.makeText(context, "Tidak Ditemukan Hasil", Toast.LENGTH_SHORT).show()
        }
        ProductAdapter.products = result
        ProductAdapter.notifyDataSetChanged()
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }


}

