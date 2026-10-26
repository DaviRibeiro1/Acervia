package com.example.acervia.ui.screens.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.ResultadoLogin
import com.example.acervia.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CodigoAcessoUiState(
    val email: String = "",
    val codigo: String = "",
    val tempoReenvioRestante: Int = 30,
    val podeReenviar: Boolean = false,
    val isLoading: Boolean = false,
    val erro: String? = null,
)

@HiltViewModel
class CodigoAcessoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val emailParam: String = savedStateHandle["email"] ?: ""

    private val _uiState = MutableStateFlow(CodigoAcessoUiState(email = emailParam))
    val uiState: StateFlow<CodigoAcessoUiState> = _uiState.asStateFlow()

    init {
        iniciarContadorReenvio()
    }

    private fun iniciarContadorReenvio() {
        viewModelScope.launch {
            _uiState.update { it.copy(tempoReenvioRestante = 30, podeReenviar = false) }
            for (i in 30 downTo 1) {
                _uiState.update { it.copy(tempoReenvioRestante = i) }
                delay(1000)
            }
            _uiState.update { it.copy(podeReenviar = true) }
        }
    }

    fun onCodigoChange(novoCodigo: String, onSucesso: (Boolean) -> Unit) {
        val apenasNumeros = novoCodigo.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(codigo = apenasNumeros, erro = null) }

        if (apenasNumeros.length == 6) {
            entrarComCodigo(apenasNumeros, onSucesso)
        }
    }

    fun onEntrarClick(onSucesso: (Boolean) -> Unit) {
        val codigoAtual = _uiState.value.codigo
        if (codigoAtual.length < 6) {
            _uiState.update { it.copy(erro = "Digite os 6 dígitos do código.") }
            return
        }
        entrarComCodigo(codigoAtual, onSucesso)
    }

    fun onReenviarCodigoClick() {
        if (!_uiState.value.podeReenviar) return
        viewModelScope.launch {
            try {
                authRepository.enviarCodigo(_uiState.value.email)
                iniciarContadorReenvio()
            } catch (e: Exception) {
                _uiState.update { it.copy(erro = "Não foi possível reenviar o código. Tente de novo.") }
            }
        }
    }

    private fun entrarComCodigo(codigo: String, onSucesso: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            when (val resultado = authRepository.entrarComCodigo(_uiState.value.email, codigo)) {
                is ResultadoLogin.Sucesso -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSucesso(resultado.contaNova)
                }
                is ResultadoLogin.Erro -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            erro = resultado.mensagem,
                        )
                    }
                }
            }
        }
    }
}
