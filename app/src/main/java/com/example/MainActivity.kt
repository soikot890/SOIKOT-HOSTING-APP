package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DashboardScreen
import com.example.ui.DashboardViewModel
import com.example.ui.LoginScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: DashboardViewModel = viewModel()
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()

        if (isLoggedIn) {
          DashboardScreen(
            viewModel = viewModel,
            onLogout = { viewModel.logout() }
          )
        } else {
          LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { email ->
              viewModel.login(email)
            }
          )
        }
      }
    }
  }
}
