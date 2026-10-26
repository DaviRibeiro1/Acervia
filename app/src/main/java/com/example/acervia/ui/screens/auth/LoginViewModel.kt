package com.example.acervia.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.ResultadoLogin
import com.example.acervia.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val isLoadingGoogle: Boolean = false,
    val isLoadingEmail: Boolean = false,
    val erroEmail: String? = null,
    val erroGeral: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(novoEmail: String) {
        _uiState.update { it.copy(email = novoEmail, erroEmail = null, erroGeral = null) }
    }

    fun onEnviarCodigoClick(onSucesso: (String) -> Unit) {
        val emailDigitado = _uiState.value.email.trim()

        if (emailDigitado.isBlank()) {
            _uiState.update { it.copy(erroEmail = "Digite seu e-mail para receber o código.") }
            return
        }

        if (!emailDigitado.contains("@") || !emailDigitado.contains(".")) {
            _uiState.update { it.copy(erroEmail = "Confira o e-mail: parece faltar algo, como o @.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEmail = true, erroEmail = null) }
            try {
                authRepository.enviarCodigo(emailDigitado)
                _uiState.update { it.copy(isLoadingEmail = false) }
                onSucesso(emailDigitado)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingEmail = false,
                        erroGeral = e.message ?: "Não conseguimos enviar o código agora. Tente de novo.",
                    )
                }
            }
        }
    }

    fun onEntrarComGoogleClick(onSucesso: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGoogle = true, erroGeral = null) }
            when (val resultado = authRepository.entrarComGoogle()) {
                is ResultadoLogin.Sucesso -> {
                    _uiState.update { it.copy(isLoadingGoogle = false) }
                    onSucesso(resultado.contaNova)
                }
                is ResultadoLogin.Erro -> {
                    _uiState.update {
                        it.copy(
                            isLoadingGoogle = false,
                            erroGeral = resultado.mensagem,
                        )
                    }
                }
            }
        }
    }
}
