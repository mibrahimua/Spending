package com.mibrahimuadev.spending.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.mibrahimuadev.spending.adapter.TransaksiListAdapter
import com.mibrahimuadev.spending.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        Log.i("HomeFragment", "Called ViewModelProvider.get")

        val recycleView = binding.recycleviewTransaksi
        val adapter = TransaksiListAdapter(application)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(application)

        val viewModelFactory = HomeViewModelFactory(application)

        val homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        homeViewModel.allTransaksi.observe(viewLifecycleOwner, { transaksi ->
            transaksi.let {
                adapter.setTransaksi(transaksi)
            }
        })


        binding.floatingActionButton.setOnClickListener {

            Navigation.findNavController(requireView())
                .navigate(HomeFragmentDirections.actionHomeFragmentToPemasukanFragment())
        }

        return binding.root
    }

}