package com.mibrahimuadev.spending.data.network.google

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class GoogleAuthService(val appContext: Context) {
    companion object {
        val SCOPES: List<String> =
            Collections.singletonList(Scope(Scopes.DRIVE_FILE).toString())
    }

    /**
     * Proses Google Sign In :
     * 1. Set GoogleSignInOptions
     * 2. Set GoogleSignInClient
     * 3. Retrieve GoogleSignInAccount
     */

    fun getGoogleSignOption(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_FILE))
            .requestEmail()
            .build()
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return GoogleSignIn.getClient(appContext, getGoogleSignOption())
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(appContext)
    }

    fun getLastSignedInAccountFlow(): LiveData<GoogleSignInAccount?> {
        return liveData {
            GoogleSignIn.getLastSignedInAccount(appContext)
        }
    }

    fun checkDriveAuth(): Drive {
        val mAccount: GoogleSignInAccount? =
            GoogleSignIn.getLastSignedInAccount(appContext)

        val credential: GoogleAccountCredential =
            GoogleAccountCredential.usingOAuth2(appContext, SCOPES)

        credential.setSelectedAccount(mAccount?.account)
        val googleDriveService: Drive =
            Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                .setApplicationName("Spending")
                .build()
        return googleDriveService
    }

}