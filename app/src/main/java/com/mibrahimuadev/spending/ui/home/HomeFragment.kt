package com.mibrahimuadev.spending.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.TransactionListAdapter
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.databinding.FragmentHomeBinding
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.Formatter
import kotlinx.android.synthetic.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by navGraphViewModels(R.id.navigation) {
        HomeViewModelFactory(requireActivity().application)
    }
    private lateinit var toolbarTitle: TextView
    private var isFabOpen = false
    private lateinit var fabTrans: FloatingActionButton
    private lateinit var fabExpense: FloatingActionButton
    private lateinit var fabIncome: FloatingActionButton

    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var rotate_forward: Animation
    private lateinit var rotate_backward: Animation

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        Log.i("HomeFragment", "HomeFragment Created")

        val recycleView = binding.recycleviewTransaksi
        val adapter = TransactionListAdapter(application)
        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(application)
        val actionbar = (activity as AppCompatActivity).supportActionBar
        actionbar?.title = ""
        actionbar?.setDisplayShowCustomEnabled(true)
        actionbar?.setCustomView(R.layout.custom_toolbar_title)
        toolbarTitle = actionbar?.customView?.findViewById(R.id.toolbarTitle)!!

        toolbarTitle.setOnClickListener {
            CustomDatePickerDialog().show(parentFragmentManager, "MyCustomDialog")
        }

        homeViewModel.onFirstLoaded()
        homeViewModel.selectedMonth.observe(viewLifecycleOwner) { month ->
            homeViewModel.displayData()
            toolbarTitle.text = CurrentDate.monthName[month.toInt().minus(1)] + " " + homeViewModel.selectedYear.value
        }

        homeViewModel.allTransactions.observe(viewLifecycleOwner, { transaksi ->
            adapter.setTransaksi(transaksi)
        })
        homeViewModel.expenseNominal.observe(viewLifecycleOwner) {
            binding.tvExpense.text = Formatter.addThousandsDelimiter(it)
        }
        homeViewModel.incomeNominal.observe(viewLifecycleOwner) {
            binding.tvIncome.text = Formatter.addThousandsDelimiter(it)
        }
        homeViewModel.balanceNominal.observe(viewLifecycleOwner) {
            binding.tvBalancce.text = Formatter.addThousandsDelimiter(it)
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
                    if (isFabOpen) {
                        fabExpense.startAnimation(fab_close)
                        fabIncome.startAnimation(fab_close)
                        fabExpense.isClickable = false
                        fabIncome.isClickable = false
                        binding.labelExpense.startAnimation(fab_close)
                        binding.labelIncome.startAnimation(fab_close)
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabTrans.show()
                    if (isFabOpen) {
                        fabExpense.startAnimation(fab_open)
                        fabIncome.startAnimation(fab_open)
                        fabExpense.isClickable = true
                        fabIncome.isClickable = true
                        binding.labelExpense.startAnimation(fab_open)
                        binding.labelIncome.startAnimation(fab_open)
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

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    fun animateFab() {
        if (isFabOpen) {
            fabTrans.startAnimation(rotate_backward)
            fabExpense.startAnimation(fab_close)
            fabIncome.startAnimation(fab_close)
            fabExpense.isClickable = false
            fabIncome.isClickable = false
            binding.labelExpense.startAnimation(fab_close)
            binding.labelIncome.startAnimation(fab_close)
            isFabOpen = false
        } else {
            fabTrans.startAnimation(rotate_forward)
            fabExpense.startAnimation(fab_open)
            fabIncome.startAnimation(fab_open)
            fabExpense.isClickable = true
            fabIncome.isClickable = true
            binding.labelExpense.startAnimation(fab_open)
            binding.labelIncome.startAnimation(fab_open)
            isFabOpen = true
        }
    }

    fun getStatusFab(): Boolean {
        return fabTrans.isShown || fabIncome.isShown && fabExpense.isShown
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("HomeFragment", "HomeFragment destroyed")
        toolbarTitle.text = ""
        clearFindViewByIdCache()
        _binding = null
    }
}