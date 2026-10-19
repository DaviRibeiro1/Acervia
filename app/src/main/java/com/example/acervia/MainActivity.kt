package com.example.acervia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.acervia.navigation.AcerviaNavHost
import com.example.acervia.ui.theme.AcerviaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcerviaTheme {
                AcerviaNavHost()
            }
        }
    }
}
