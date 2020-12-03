package com.mibrahimuadev.spending.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.mibrahimuadev.spending.databinding.FragmentDetailTransactionBinding
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.Formatter

class DetailTransactionFragment : Fragment() {
    private val TAG = "DetailTransactionFragment"
    private var _binding: FragmentDetailTransactionBinding? = null
    private val binding get() = _binding!!
    private val args: DetailTransactionFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        _binding = FragmentDetailTransactionBinding.inflate(layoutInflater)
        val viewModelFactory = TransactionViewModelFactory(application)
        val transactionViewModel =
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

                )
        }

        setHasOptionsMenu(true)
        return binding.root
    }

}