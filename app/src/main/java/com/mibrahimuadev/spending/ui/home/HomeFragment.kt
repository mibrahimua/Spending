package com.mibrahimuadev.spending.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.adapter.TransactionListAdapter
import com.mibrahimuadev.spending.data.model.TransactionType
import com.mibrahimuadev.spending.data.network.google.GoogleAuthService
import com.mibrahimuadev.spending.databinding.FragmentHomeBinding
import com.mibrahimuadev.spending.ui.backup.BackupFragmentDirections
import com.mibrahimuadev.spending.ui.backup.BackupViewModel
import com.mibrahimuadev.spending.ui.backup.BackupViewModelFactory
import com.mibrahimuadev.spending.ui.nav.NavDrawer
import com.mibrahimuadev.spending.utils.CurrentDate
import com.mibrahimuadev.spending.utils.Formatter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by navGraphViewModels(R.id.navigation) {
        HomeViewModelFactory(requireActivity().application)
    }
    private lateinit var backupViewModel: BackupViewModel

    private lateinit var toolbarTitle: TextView
    private lateinit var navigationView: NavigationView
    private lateinit var navDrawer: NavDrawer

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
        val application = requireActivity().application
        val viewModelFactory = BackupViewModelFactory(application = application)
        backupViewModel =
            ViewModelProvider(this, viewModelFactory).get(BackupViewModel::class.java)
        /**
         * DISABLE SEMENTARA BUAT TEST COROUTINES
         */
//        backupViewModel.checkLoggedUser()
        backupViewModel.initRequiredData()

        navigationView = requireActivity().findViewById(R.id.navigationView) as NavigationView
        navDrawer = NavDrawer(navigationView)

        backupViewModel.googleAccount.observe(viewLifecycleOwner) {
            if (it != null) {
                navDrawer.updateNavigationDrawer(it)
            } else {
                navDrawer.updateNavigationDrawer(null)
            }
        }
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
            CustomDatePickerFragment().show(parentFragmentManager, "MyCustomDialog")
        }

        homeViewModel.onFirstLoaded()
        homeViewModel.selectedMonth.observe(viewLifecycleOwner) { month ->
            homeViewModel.displayData()
            toolbarTitle.text = CurrentDate.monthName[month.toInt()
                .minus(1)] + " " + homeViewModel.selectedYear.value
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
        homeViewModel.previousBalanceNominal.observe(viewLifecycleOwner) {
            binding.tvPreviousBalance.text = Formatter.addThousandsDelimiter(it)
            if (it.toInt() == 0) {
                binding.infoPreviousBalance.isVisible = false
                binding.tvPreviousBalance.isVisible = false
            } else {
                binding.infoPreviousBalance.isVisible = true
                binding.tvPreviousBalance.isVisible = true
            }
        }

        val infoDialog = AlertDialog.Builder(requireActivity())
        binding.infoPreviousBalance.setOnClickListener {
            infoDialog.setTitle("Info")
                .setMessage(
                    "This amount is an accumulation of the previous balance in the previous month in a year. " +
                            "\n You can disable this feature in settings"
                )
                .setCancelable(true)
                .setPositiveButton("Ok") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .show()
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
// DISABLE GOOGLE SIGN IN
//    override fun onStart() {
//        super.onStart()
//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
//        if (account != null) {
//            runBlocking {
//                val job = lifecycleScope.launch {
//                    try {
//                        val checkPermission = GoogleAuthService()
//                        checkPermission.checkDriveAuth(requireActivity().application)
//                        Log.i("HomeFragment", "ada permission")
//                    } catch (e: UserRecoverableAuthIOException) {
//                        Log.i("HomeFragment", "tidak ada permission, request ulang")
//                        startActivityForResult(e.getIntent(), 0)
//                    }
//                }
//                job.join()
//            }
//            updateUi(account)
//        } else {
//            updateUi(null)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
//        if (requestCode == 1) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            runBlocking {
//                val job = lifecycleScope.launch {
//                    try {
//                        val checkPermission = GoogleAuthService()
//                        checkPermission.checkDriveAuth(requireActivity().application)
//                        Log.i(tag, "ada permission")
//                    } catch (e: UserRecoverableAuthIOException) {
//                        Log.i(tag, "tidak ada permission, request ulang")
//                        startActivityForResult(e.getIntent(), 0)
//                    }
//                }
//                job.join()
//            }
//
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }
//
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//
//            // Signed in successfully, show authenticated UI.
//            updateUi(account)
//
//            Toast.makeText(requireContext(), "Welcome ${account?.email}", Toast.LENGTH_SHORT)
//                .show()
//            Navigation.findNavController(requireView())
//                .navigate(BackupFragmentDirections.actionBackupFragmentToHomeFragment())
//
//        } catch (e: ApiException) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(ContentValues.TAG, "signInResult:failed code=" + e.statusCode)
//            Toast.makeText(
//                requireContext(),
//                "Failed to sign in : ${e.statusCode}",
//                Toast.LENGTH_SHORT
//            )
//                .show()
//
//            updateUi(null)
//        }
//    }
//
//    private fun updateUi(account: GoogleSignInAccount?) {
//        val headerView = navigationView.getHeaderView(0)
//        val labelEmail = headerView.findViewById<View>(R.id.labelEmail) as TextView
//        val labelUsername = headerView.findViewById<View>(R.id.labelUsername) as TextView
//        if (account != null) {
////            navigationView.menu.findItem(R.id.backupFragment).title = "Log Out"
//            labelUsername.text = account.displayName
//            labelEmail.text = account.email
//        } else {
////            navigationView.menu.findItem(R.id.backupFragment).title = "Log In"
//            labelUsername.text = ""
//            labelEmail.text = ""
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("HomeFragment", "HomeFragment destroyed")
        toolbarTitle.text = ""
        _binding = null
    }
}