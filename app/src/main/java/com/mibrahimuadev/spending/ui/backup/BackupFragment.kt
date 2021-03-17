package com.mibrahimuadev.spending.ui.backup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.drive.DriveScopes
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.entity.AccountEntity
import com.mibrahimuadev.spending.data.network.google.DriveServiceHelper
import com.mibrahimuadev.spending.data.network.google.GoogleAuthService
import com.mibrahimuadev.spending.databinding.FragmentBackupBinding
import com.mibrahimuadev.spending.ui.nav.NavDrawer
import com.mibrahimuadev.spending.utils.PermissionsApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class BackupFragment : Fragment() {
    private val TAG = "BackupFragment"
    private var _binding: FragmentBackupBinding? = null
    private val binding get() = _binding!!
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var backupViewModel: BackupViewModel
    private lateinit var navigationView: NavigationView
    private lateinit var navDrawer: NavDrawer
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private val REQUEST_CODE_SIGN_IN = 1
    private val REQUEST_CODE_OPEN_DOCUMENT = 2
    private var isUserExist = false

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
        backupViewModel.initRequiredData()

        backupViewModel.googleAccount.observe(viewLifecycleOwner) {
            if (it != null) {
                isUserExist = true
                updateUi(true)
            } else {
                isUserExist = false
                updateUi(false)
            }
        }

        backupViewModel.backupDate.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.localBackup.text = it.localBackup ?: "-"
                binding.googleBackup.text = it.googleBackup ?: "-"
            }
        }

        backupViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
            binding.authButton.isVisible = !it
        }

        /**
         * driveServiceHelper ditrigger dari button backup
         * DISABLE untuk test work manager
         */
//        backupViewModel.driveServiceHelper.observe(viewLifecycleOwner) {
//            if (it != null) {
//                backupViewModel.syncFileBackupDrive()
//            }
//        }

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
            backupViewModel.doBackup()
            /**
             * DISABLE untuk test work manager
             */
//            val localFileBackup = backupViewModel.createLocalFileBackup()
//            if (localFileBackup) {
//                Toast.makeText(
//                    requireContext(),
//                    "file backup on local is exist, ready to sync",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }

        return binding.root
    }

    private fun updateUi(isUserExist: Boolean) {

        if (isUserExist) {
            binding.authButton.text = "Log Out"
            binding.authButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
            backupViewModel.googleAccount.observe(viewLifecycleOwner) { dataUi ->
                binding.emailBackup.text = dataUi.userEmail
                navDrawer.updateNavigationDrawer(dataUi)
            }

        } else {
            binding.authButton.text = "Log In"
            binding.authButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
            navDrawer.updateNavigationDrawer(null)
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
                /**
                 * Yang jadi pertanyaan bagaimana proses auth google sign in di work manager?
                 * jika prosesnya memakai intent ?
                 */
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
                backupViewModel.isUserLoggedIn.postValue(true)
                backupViewModel.insertOrUpdateLoggedUser(
                    AccountEntity(
                        userId = account.id!!,
                        userName = account.displayName!!,
                        userEmail = account.email!!
                    )
                )
                /**
                 * Re initRequiredData
                 */
                backupViewModel.initRequiredData()
            }

            Toast.makeText(requireContext(), "Welcome ${account?.email}", Toast.LENGTH_SHORT)
                .show()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("BackupFragment", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(
                requireContext(),
                "Failed to sign in : ${e.statusCode}",
                Toast.LENGTH_SHORT
            ).show()

            updateUi(false)
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
                updateUi(false)
                Navigation.findNavController(requireView())
                    .navigate(BackupFragmentDirections.actionBackupFragmentToHomeFragment())
            }
    }
}