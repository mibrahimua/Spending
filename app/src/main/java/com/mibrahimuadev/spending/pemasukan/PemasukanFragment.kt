package com.mibrahimuadev.spending.pemasukan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.adapter.TransaksiListAdapter
import com.mibrahimuadev.spending.databinding.FragmentPemasukanBinding

class PemasukanFragment : Fragment() {

    private lateinit var binding: FragmentPemasukanBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPemasukanBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        Log.i("PemasukanFragment", "Called ViewModelProvider.get")

        val recyclerView = binding.recycleview
        val adapter = TransaksiListAdapter(application)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(application)

        // Create instance to ViewModel Factory
//        val dataSource = AppDatabase.getInstance(application).transaksiDao
        val viewModelFactory = PemasukanViewModelFactory(application)

        // Get a reference to the ViewModel associated with this fragment.
        val pemasukanViewModel =
            ViewModelProvider(this, viewModelFactory).get(PemasukanViewModel::class.java)

//        pemasukanViewModel.allTransaksi.observe(viewLifecycleOwner, Observer { transaksi ->
//            transaksi?.let { adapter.setTransaksi(transaksi) }
//        })

//        binding.namaTransaksi.text = "Halloo ini text dari view binding"
//        binding.simpanPemasukan.setOnClickListener {
//            pemasukanViewModel.insertTransaksi(Transaksi(1,"Pemasukan","Sarapan", 15000.00))
//        }
//        pemasukanViewModel.navigateFromHome.observe(viewLifecycleOwner, {
//            if (it == true) {
//
//            }
//        })


        return binding.root
    }

}