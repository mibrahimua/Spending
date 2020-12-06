package com.mibrahimuadev.spending.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mibrahimuadev.spending.databinding.FragmentCategorySettingBinding

class CategorySettingFragment : Fragment() {
    private var _binding: FragmentCategorySettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategorySettingBinding.inflate(layoutInflater)
        return binding.root
    }
}