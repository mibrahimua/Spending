package com.mibrahimuadev.spending.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.TransaksiListAdapter
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isFabOpen = false
    private lateinit var fabTrans: FloatingActionButton
    private lateinit var fabExpense: FloatingActionButton
    private lateinit var fabIncome: FloatingActionButton

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var rotate_forward: Animation
    private lateinit var rotate_backward: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
//        Log.i("HomeFragment", "Called ViewModelProvider.get")
        Log.i("HomeFragment", "isFabOpen are $isFabOpen")

        val recycleView = binding.recycleviewTransaksi
        val adapter = TransaksiListAdapter(application)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(application)

        val viewModelFactory = HomeViewModelFactory(application)

        val homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        homeViewModel.allTransaksi.observe(viewLifecycleOwner, { transaksi ->
            if (transaksi is Result.Success) {
                adapter.setTransaksi(transaksi.data)
            }

        })
        fabTrans = binding.floatingActionButtonTrans
        fabExpense = binding.floatingActionButtonExpense
        fabIncome = binding.floatingActionButtonIncome

        fab_open = AnimationUtils.loadAnimation(application, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(application, R.anim.fab_close)
        rotate_forward = AnimationUtils.loadAnimation(application, R.anim.rotate_forward)
        rotate_backward = AnimationUtils.loadAnimation(application, R.anim.rotate_backward)

        fabTrans.setOnClickListener {
            animateFab()
        }

        fabExpense.setOnClickListener {
            isFabOpen = false
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToAddTransaksiFragment())
        }

        fabIncome.setOnClickListener {
            isFabOpen = false
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToAddTransaksiFragment())
        }
        return binding.root
    }

    fun animateFab() {
        if (isFabOpen) {
            fabTrans.startAnimation(rotate_backward)
            fabExpense.startAnimation(fab_close)
            fabIncome.startAnimation(fab_close)
            fabExpense.isClickable = false
            fabIncome.isClickable = false
            isFabOpen = false
        } else {
            fabTrans.startAnimation(rotate_forward)
            fabExpense.startAnimation(fab_open)
            fabIncome.startAnimation(fab_open)
            fabExpense.isClickable = true
            fabIncome.isClickable = true
            isFabOpen = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}