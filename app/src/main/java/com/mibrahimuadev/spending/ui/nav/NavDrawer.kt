package com.mibrahimuadev.spending.ui.nav

import android.view.View
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import com.mibrahimuadev.spending.R
import com.mibrahimuadev.spending.data.model.GoogleAccount

class NavDrawer(navigationView: NavigationView) {
    val headerView = navigationView.getHeaderView(0)

    fun updateNavigationDrawer(googleAccount: GoogleAccount?) {
        val labelUsername = headerView.findViewById<View>(R.id.labelUsername) as TextView
        val labelEmail = headerView.findViewById<View>(R.id.labelEmail) as TextView

        labelUsername.text = googleAccount?.userDisplayName ?: ""
        labelEmail.text = googleAccount?.userEmail ?: ""
    }
}