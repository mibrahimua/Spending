package com.mibrahimuadev.spending.ui.backup

import android.app.Activity
import android.app.AlertDialog
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
import com.mibrahimuadev.spending.utils.CurrentDate.toString
import com.mibrahimuadev.spending.utils.PermissionsApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
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
        }

        binding.layoutBackupSchedule.setOnClickListener {
            dialogBackupSchedule.show()
        }

        backupViewModel.backupDateFlow.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.localBackup.text = it.localBackup?.toString("dd MMM yyyy HH:mm") ?: "-"
                binding.googleBackup.text = it.googleBackup?.toString("dd MMM yyyy HH:mm") ?: "-"
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

//            backupViewModel.testMappingDataBackup()

            /**
             * Disable line bellow for testing read json file from backup db dir
             */

//            backupViewModel.doRestoreOneTime()

            /**
             * Disable line bellow for testing download file from google drive
             */

            val doBackup: Boolean = backupViewModel.doBackupOneTime()
            if (!doBackup) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                alertDialog.setTitle("Warning")
                alertDialog.setMessage("Failed to backup database, \nyou can perform backups 12 hours after the last backup")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        }

        return binding.root
    }

    private fun createDialogBackupSchedule() {
        dialogBackupSchedule = Dialog(requireContext())
        dialogBackupSchedule.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBackupSchedule.setContentView(R.layout.backup_schedule_dialog)

        radioGroup = dialogBackupSchedule.findViewById<View>(R.id.radioGroupBackup) as RadioGroup

        backupViewModel.backupSchedule.observe(viewLifecycleOwner) {
            if (it.name == "NEVER") radioGroup.check(R.id.radioButtonNever)
            if (it.name == "DAILY") radioGroup.check(R.id.radioButtonDaily)
            if (it.name == "WEEKLY") radioGroup.check(R.id.radioButtonWeekly)
            if (it.name == "MONTHLY") radioGroup.check(R.id.radioButtonMonthly)
        }

        radioGroup.setOnCheckedChangeListener { radioGroup: RadioGroup, _ ->
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val currentButton =
                dialogBackupSchedule.findViewById(selectedRadioButtonId) as RadioButton

            currentButton.setOnClickListener {
                backupViewModel.updateBackupSchedule(
                    BackupSchedule.valueOf(
                        currentButton.text.toString()
                            .uppercase()
                    )
                )
                dialogBackupSchedule.dismiss()
            }
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
        Timber.i("check if user if exist : ${isUserExist}")

        if (isUserExist) {
            runBlocking {
                val job = lifecycleScope.launch {
                    try {
                        val checkPermission = GoogleAuthService(requireActivity().application)
                        checkPermission.checkDriveAuth()
                        Timber.i("ada permission")
                    } catch (e: UserRecoverableAuthIOException) {
                        Timber.i("tidak ada permission, request ulang")
                        /**
                         * alternate for startActivityForResult
                         * https://stackoverflow.com/a/63654043/16139045
                         */
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
                backupViewModel.doRestoreOneTime()

                backupViewModel.updateBackupSchedule(BackupSchedule.valueOf("NEVER"))
            }
            Toast.makeText(requireContext(), "Welcome ${account?.email}", Toast.LENGTH_SHORT)
                .show()

            Navigation.findNavController(requireView())
                .navigate(BackupFragmentDirections.actionBackupFragmentToHomeFragment())
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.w( "signInResult:failed code=${e.statusCode}")
            Toast.makeText(
                requireContext(),
                "Failed to sign in : ${e.statusCode}",
                Toast.LENGTH_SHORT
            ).show()

            updateUi(null)
        }
    }

    private fun loggingIn() {
        /**
         * alternate for startActivityForResult
         * https://stackoverflow.com/a/63654043/16139045
         */
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