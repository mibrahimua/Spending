package com.mibrahimuadev.spending.transaksi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.adapter.TransaksiListAdapter
import com.mibrahimuadev.spending.databinding.FragmentTransaksiBinding

class TransaksiFragment : Fragment() {

    private lateinit var binding: FragmentTransaksiBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransaksiBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        Log.i("PemasukanFragment", "Called ViewModelProvider.get")


        // Create instance to ViewModel Factory
        val viewModelFactory = TransaksiViewModelFactory(application)

        // Get a reference to the ViewModel associated with this fragment.
        val pemasukanViewModel =
            ViewModelProvider(this, viewModelFactory).get(TransaksiViewModel::class.java)

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