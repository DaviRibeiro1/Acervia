package com.example.acervia.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.data.repository.FakeAuthRepository
import com.example.acervia.domain.repository.AuthRepository
import com.example.acervia.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashDestino {
    data object EmAndamento : SplashDestino
    data class Concluido(val rota: String) : SplashDestino
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _destino = MutableStateFlow<SplashDestino>(SplashDestino.EmAndamento)
    val destino: StateFlow<SplashDestino> = _destino.asStateFlow()

    init {
        verificarSessaoERotear()
    }

    fun verificarSessaoERotear() {
        viewModelScope.launch {
            val inicioMs = System.currentTimeMillis()
            val sessao = authRepository.sessao.firstOrNull()
            val isPrimeiroAcesso = (authRepository as? FakeAuthRepository)?.primeiroAcessoFlow?.firstOrNull() ?: false

            val decorrido = System.currentTimeMillis() - inicioMs
            val restanteMs = (1500 - decorrido).coerceAtLeast(600)
            delay(restanteMs)

            val rotaDestino = when {
                sessao != null && sessao.perfil != null -> "main_container?tab=${NavRoutes.HOME}"
                isPrimeiroAcesso -> NavRoutes.WELCOME
                else -> NavRoutes.LOGIN
            }

            _destino.value = SplashDestino.Concluido(rotaDestino)
        }
    }
}
