package com.mibrahimuadev.spending.ui.backup

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.entity.AccountEntity
import com.mibrahimuadev.spending.data.model.BackupSchedule
import com.mibrahimuadev.spending.data.network.google.GoogleAuthService
import com.mibrahimuadev.spending.databinding.FragmentBackupBinding
import com.mibrahimuadev.spending.ui.nav.NavDrawer
import com.mibrahimuadev.spending.utils.PermissionsApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class BackupFragment : Fragment() {
    private val TAG = "BackupFragment"
    private var _binding: FragmentBackupBinding? = null
    private val binding get() = _binding!!
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var backupViewModel: BackupViewModel
    private lateinit var navigationView: NavigationView
    private lateinit var navDrawer: NavDrawer
    private val REQUEST_CODE_SIGN_IN = 1
    private val REQUEST_CODE_OPEN_DOCUMENT = 2
    private var isUserExist = false

    private lateinit var dialogBackupSchedule: Dialog
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBackupBinding.inflate(layoutInflater)
        navigationView = requireActivity().findViewById(R.id.navigationView) as NavigationView
        navDrawer = NavDrawer(navigationView)

        val application = requireActivity().application
        val viewModelFactory = BackupViewModelFactory(application = application)

        backupViewModel =
            ViewModelProvider(this, viewModelFactory).get(BackupViewModel::class.java)

        mGoogleSignInClient = backupViewModel.mGoogleSignInClient

        backupViewModel.backupSchedule.observe(viewLifecycleOwner) {
            binding.labelBackupSchedule.text = it.name
            /**
             * Reset periodic work request if backupSchedule has changed
             * tapi fungsi ini akan terus dijalankan tiap kali buka halaman backup, hmmmm
             */
//            backupViewModel.doBackupPeriodic()
        }

        binding.layoutBackupSchedule.setOnClickListener {
            dialogBackupSchedule.show()
        }

        backupViewModel.backupDateFlow.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.localBackup.text = it.localBackup ?: "-"
                binding.googleBackup.text = it.googleBackup ?: "-"
            }
        }

        backupViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
            binding.authButton.isVisible = !it
        }

        backupViewModel.loggedUserFlow.observe(viewLifecycleOwner) {
            updateUi(it ?: null)
        }
        binding.authButton.setOnClickListener {
            if (isUserExist) {
                loggingOut()
            } else {
                loggingIn()
            }
        }

        PermissionsApp(requireActivity()).also {
            it.checkPermissions()
        }

        binding.buttonBackup.setOnClickListener {
            backupViewModel.doBackupOneTime()
        }

        return binding.root
    }

    private fun createDialogBackupSchedule() {
        dialogBackupSchedule = Dialog(requireContext())
        dialogBackupSchedule.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBackupSchedule.setContentView(R.layout.backup_schedule_dialog)

        radioGroup = dialogBackupSchedule.findViewById<View>(R.id.radioGroupBackup) as RadioGroup
        val buttonNever =
            dialogBackupSchedule.findViewById<View>(R.id.radioButtonNever) as RadioButton
        val buttonDaily =
            dialogBackupSchedule.findViewById<View>(R.id.radioButtonDaily) as RadioButton
        val buttonWeekly =
            dialogBackupSchedule.findViewById<View>(R.id.radioButtonWeekly) as RadioButton
        val buttonMonthly =
            dialogBackupSchedule.findViewById<View>(R.id.radioButtonMonthly) as RadioButton

        backupViewModel.backupSchedule.observe(viewLifecycleOwner) {
            if (it.name == "NEVER") {
                buttonNever.isChecked = true
            }
            if (it.name == "DAILY") {
                buttonDaily.isChecked = true
            }
            if (it.name == "WEEKLY") {
                buttonWeekly.isChecked = true
            }
            if (it.name == "MONTHLY") {
                buttonMonthly.isChecked = true
            }
        }
        radioGroup.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            radioButton =
                dialogBackupSchedule.findViewById<View>(selectedRadioButtonId) as RadioButton

            backupViewModel.updateBackupSchedule(
                BackupSchedule.valueOf(
                    radioButton.text.toString()
                        .toUpperCase(Locale.getDefault())
                )
            )

            dialogBackupSchedule.dismiss()
        }

        val closeDialogBackup =
            dialogBackupSchedule.findViewById<View>(R.id.closeDialogBackup) as TextView
        closeDialogBackup.setOnClickListener {
            dialogBackupSchedule.dismiss()
        }

        val window: Window = dialogBackupSchedule.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun updateUi(accountEntity: AccountEntity?) {

        if (accountEntity != null) {
            binding.authButton.text = "Log Out"
            binding.authButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )

            binding.emailBackup.text = accountEntity.userEmail
            navDrawer.updateNavigationDrawer(accountEntity)

            binding.groupDriveSetting.isVisible = true
            binding.labelBackupToDrive.isVisible = true
            binding.labelBackupSchedule.isVisible = true
            createDialogBackupSchedule()
            isUserExist = true
        } else {
            binding.authButton.text = "Log In"
            binding.authButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            binding.groupDriveSetting.isVisible = false
            binding.labelBackupToDrive.isVisible = false
            binding.labelBackupSchedule.isVisible = false
            navDrawer.updateNavigationDrawer(null)
            isUserExist = false
        }

    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        Log.i(TAG, "check if user if exist : ${isUserExist}")

        if (isUserExist) {
            runBlocking {
                val job = lifecycleScope.launch {
                    try {
                        val checkPermission = GoogleAuthService(requireActivity().application)
                        checkPermission.checkDriveAuth()
                        Log.i(TAG, "ada permission")
                    } catch (e: UserRecoverableAuthIOException) {
                        Log.i(TAG, "tidak ada permission, request ulang")
                        startActivityForResult(e.getIntent(), 0)
                    }
                }
                job.join()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == Activity.RESULT_OK && data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to sign in, please check your internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
            REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri: Uri? = data.data
                if (uri != null) {
//                    backupViewModel.createFolderDrive()
//                    openFileFromFilePicker(uri)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            if (account != null) {
                /**
                 * post value isUserLoggedIn mungkin bisa diganti?
                 */
                backupViewModel.isUserLoggedIn.postValue(true)
                backupViewModel.insertOrUpdateLoggedUser(
                    AccountEntity(
                        userId = account.id!!,
                        userName = account.displayName!!,
                        userEmail = account.email!!
                    )
                )
            }
            Toast.makeText(requireContext(), "Welcome ${account?.email}", Toast.LENGTH_SHORT)
                .show()

            Navigation.findNavController(requireView())
                .navigate(BackupFragmentDirections.actionBackupFragmentToHomeFragment())
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("BackupFragment", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(
                requireContext(),
                "Failed to sign in : ${e.statusCode}",
                Toast.LENGTH_SHORT
            ).show()

            updateUi(null)
        }
    }

    private fun loggingIn() {
        startActivityForResult(mGoogleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun loggingOut() {
        backupViewModel.deleteLoggedUser()
        backupViewModel.deleteBackupDate()
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()) {
                Toast.makeText(requireContext(), "Log Out Successfully", Toast.LENGTH_SHORT)
                    .show()
                updateUi(null)
                Navigation.findNavController(requireView())
                    .navigate(BackupFragmentDirections.actionBackupFragmentToHomeFragment())
            }
    }
}