package com.example.autoservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.autoservice.ui.navigation.NavGraph
import com.example.autoservice.ui.theme.AutoServiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AutoServiceTheme {
                NavGraph()
            }
        }
    }
}