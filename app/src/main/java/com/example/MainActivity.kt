package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.MainScaffold
import com.example.ui.theme.SiamNexaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiamNexaTheme {
                val viewModel: MainViewModel = viewModel()
                val currentUser by viewModel.currentUser.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (currentUser == null) {
                        AuthScreen(viewModel = viewModel)
                    } else {
                        MainScaffold(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
