package com.mibrahimuadev.spending.ui.transaction

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentDetailTransactionBinding
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.EventObserver
import com.mibrahimuadev.spending.utils.Formatter

class DetailTransactionFragment : Fragment() {
    private val TAG = "DetailTransactionFragment"
    private var _binding: FragmentDetailTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionViewModel: TransactionViewModel
    private val args: DetailTransactionFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        _binding = FragmentDetailTransactionBinding.inflate(layoutInflater)
        val viewModelFactory = TransactionViewModelFactory(application)
        transactionViewModel =
            ViewModelProvider(this, viewModelFactory).get(TransactionViewModel::class.java)

        transactionViewModel.getDetailTransaction(args.transactionId)

        transactionViewModel.categoryName.observe(viewLifecycleOwner) {
            binding.tvTranscCategory.text = it
        }

        transactionViewModel.transactionType.observe(viewLifecycleOwner) {
            binding.tvTrancsType.text = it.name
        }

        transactionViewModel.transactionNominal.observe(viewLifecycleOwner) {
            binding.tvTranscNominal.text = Formatter.addThousandsDelimiter(it)
        }

        transactionViewModel.dateTransaction.observe(viewLifecycleOwner) {
            binding.tvTranscDate.text = CurrentDate.getDateString(it)
        }

        transactionViewModel.noteTransaction.observe(viewLifecycleOwner) {
            binding.tvTranscNote.text = it
        }

        binding.btnEdit.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(
                    DetailTransactionFragmentDirections.actionDetailTransactionFragment2ToNavTransc(

                    ).setTransactionId(args.transactionId)
                        .setTransactionType(transactionViewModel.transactionType.value?.name!!)
                        .setActionType("UPDATE")

                )
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_action) {
            AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Are you sure want to delete this transaction ?")
                .setNegativeButton("No") { _, _ ->
                }
                .setPositiveButton("Yes") { _, _ ->
                    transactionViewModel.deleteTransaction(args.transactionId)
                    transactionViewModel.navigateToHome.observe(viewLifecycleOwner, EventObserver{
                      val action = DetailTransactionFragmentDirections.actionDetailTransactionFragment2ToHomeFragment()
                      findNavController().navigate(action)
                    })
                }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModelStore.clear()
        _binding = null
    }
}