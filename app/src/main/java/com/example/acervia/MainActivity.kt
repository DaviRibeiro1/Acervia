package com.example.acervia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.acervia.domain.repository.PreferenciasRepository
import com.example.acervia.navigation.AcerviaNavHost
import com.example.acervia.ui.theme.AcerviaTheme
import com.example.acervia.ui.theme.TemaOpcao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenciasRepository: PreferenciasRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val temaOpcao by preferenciasRepository.temaEscolhido.collectAsState(initial = TemaOpcao.SISTEMA)
            AcerviaTheme(opcaoTema = temaOpcao) {
                AcerviaNavHost()
            }
        }
    }
}
