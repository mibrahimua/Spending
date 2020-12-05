package com.mibrahimuadev.spending.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.TransactionListAdapter
import com.mibrahimuadev.spending.data.Result
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.databinding.FragmentHomeBinding
import com.mibrahimuadev.spending.utils.Formatter


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

        val recycleView = binding.recycleviewTransaksi
        val adapter = TransactionListAdapter(application)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(application)

        val viewModelFactory = HomeViewModelFactory(application)

        val homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        homeViewModel.getSummaryTransaction("2020-09-20 00:00:00", "2020-10-20 23:59:59")
        homeViewModel.allTransaction.observe(viewLifecycleOwner, { transaksi ->
            if (transaksi is Result.Success) {
                adapter.setTransaksi(transaksi.data)
            }

        })
        homeViewModel.expenseNominal.observe(viewLifecycleOwner) {
            binding.tvExpense.text = Formatter.addThousandsDelimiter(it.toString())
        }
        homeViewModel.incomeNominal.observe(viewLifecycleOwner) {
            binding.tvIncome.text = Formatter.addThousandsDelimiter(it.toString())
        }
        homeViewModel.balanceNominal.observe(viewLifecycleOwner) {
            binding.tvBalancce.text = Formatter.addThousandsDelimiter(it.toString())
        }

        fabTrans = binding.floatingActionButtonTrans
        fabExpense = binding.floatingActionButtonExpense
        fabIncome = binding.floatingActionButtonIncome

        fab_open = AnimationUtils.loadAnimation(application, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(application, R.anim.fab_close)
        rotate_forward = AnimationUtils.loadAnimation(application, R.anim.rotate_forward)
        rotate_backward = AnimationUtils.loadAnimation(application, R.anim.rotate_backward)

        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 || dy < 0 && getStatusFab()) {
                    fabTrans.hide()
                    if(isFabOpen) {
                        fabExpense.startAnimation(fab_close)
                        fabIncome.startAnimation(fab_close)
                        fabExpense.isClickable = false
                        fabIncome.isClickable = false
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabTrans.show()
                    if(isFabOpen) {
                        fabExpense.startAnimation(fab_open)
                        fabIncome.startAnimation(fab_open)
                        fabExpense.isClickable = true
                        fabIncome.isClickable = true
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        fabTrans.setOnClickListener {
            animateFab()
        }

        fabExpense.setOnClickListener {
            isFabOpen = false
            Navigation.findNavController(requireView())
                .navigate(
                    HomeFragmentDirections.actionHomeFragmentToAddTransaksiFragment()
                        .setTransactionType(TransactionType.EXPENSE.name)
                )
        }

        fabIncome.setOnClickListener {
            isFabOpen = false
            Navigation.findNavController(requireView())
                .navigate(
                    HomeFragmentDirections.actionHomeFragmentToAddTransaksiFragment()
                        .setTransactionType(TransactionType.INCOME.name)
                )
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

    fun getStatusFab(): Boolean {
        return fabTrans.isShown || fabIncome.isShown && fabExpense.isShown
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}