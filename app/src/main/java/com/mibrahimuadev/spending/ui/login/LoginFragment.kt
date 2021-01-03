package com.mibrahimuadev.spending.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationView: NavigationView
    private lateinit var labelEmail: TextView
    private lateinit var labelUsername: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(layoutInflater)
        navigationView = requireActivity().findViewById(R.id.navigationView) as NavigationView

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1)
        }

        binding.signOutButton.setOnClickListener {
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity()) {
                    Toast.makeText(requireContext(), "Log Out Successfully", Toast.LENGTH_SHORT)
                        .show()
                    updateUi(null)
                    Navigation.findNavController(requireView())
                        .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                }

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        updateUi(account)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUi(account)
            Toast.makeText(requireContext(), "Welcome ${account?.email}", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(requireView())
                .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUi(null)
        }
    }

    private fun updateUi(account: GoogleSignInAccount?) {
        val headerView = navigationView.getHeaderView(0)
        val labelEmail = headerView.findViewById<View>(R.id.labelEmail) as TextView
        val labelUsername = headerView.findViewById<View>(R.id.labelUsername) as TextView
        if (account != null) {
            navigationView.menu.findItem(R.id.loginFragment).title = "Log Out"
            labelUsername.text = account.displayName
            labelEmail.text = account.email
//            navigationView?.getMenu()?.findItem(R.id.loginFragment)?.setVisible(false);
//            navigationView?.getMenu()?.findItem(R.id.logout)?.setVisible(true);
        } else {
            navigationView.menu.findItem(R.id.loginFragment).title = "Log In"
            labelUsername.text = ""
            labelEmail.text = ""
        }

    }

}