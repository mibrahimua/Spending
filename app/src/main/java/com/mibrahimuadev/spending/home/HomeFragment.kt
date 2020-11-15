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
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        Log.i("HomeFragment", "Called ViewModelProvider.get")

        val recycleView = binding.recycleviewTransaksi
        val adapter = TransaksiListAdapter(application)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(application)

        val viewModelFactory = HomeViewModelFactory(application)

        val homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        homeViewModel.allTransaksi.observe(viewLifecycleOwner, { transaksi ->
            if(transaksi is Result.Success) {
//                transaksi.let {
                    adapter.setTransaksi(transaksi.data)

//                }
            }

        })


        binding.floatingActionButton.setOnClickListener {

            Navigation.findNavController(requireView())
                .navigate(HomeFragmentDirections.actionHomeFragmentToPemasukanFragment())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}