package com.mibrahimuadev.spending.ui.transaction

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentAddIncomTransBinding

class AddIncomTransFragment : Fragment() {

    private var _binding: FragmentAddIncomTransBinding? = null
    private val binding get() = _binding!!
    lateinit var context: AppCompatActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddIncomTransBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        Log.i("AddIncomTransFragment", "Called ViewModelProvider.get")


        // Create instance to ViewModel Factory
        val viewModelFactory = AddTransaksiViewModelFactory(application)

        // Get a reference to the ViewModel associated with this fragment.
        val addTransaksiViewModel =
            ViewModelProvider(this, viewModelFactory).get(AddTransaksiViewModel::class.java)

        addTransaksiViewModel.dataCalculator.observe(viewLifecycleOwner, Observer {
            binding.nominalPemasukan.setText(it.toString())
        })

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

        val fm = context.supportFragmentManager
        val fragmentTransaction: FragmentTransaction
        val fragment = CalculatorFragment()
        fragmentTransaction = fm.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
        fragmentTransaction.commit()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}