package com.mibrahimuadev.spending.pengeluaran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mibrahimuadev.spending.databinding.FragmentPengeluaranBinding

class PengeluaranFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPengeluaranBinding.inflate(layoutInflater)

        return binding.root
    }
}