package com.example.acervia.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.Perfil
import com.example.acervia.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class FaltaPoucoUiState(
    val email: String = "",
    val nome: String = "",
    val sobrenome: String = "",
    val dataNascimento: String = "",
    val termosAceitos: Boolean = false,
    val isLoading: Boolean = false,
    val erroNome: String? = null,
    val erroSobrenome: String? = null,
    val erroDataNascimento: String? = null,
    val erroTermos: String? = null,
    val erroGeral: String? = null,
)

@HiltViewModel
class FaltaPoucoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaltaPoucoUiState())
    val uiState: StateFlow<FaltaPoucoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessao.collect { sessao ->
                if (sessao != null) {
                    _uiState.update { it.copy(email = sessao.email) }
                }
            }
        }
    }

    fun onNomeChange(novo: String) {
        _uiState.update { it.copy(nome = novo, erroNome = null, erroGeral = null) }
    }

    fun onSobrenomeChange(novo: String) {
        _uiState.update { it.copy(sobrenome = novo, erroSobrenome = null, erroGeral = null) }
    }

    fun onDataNascimentoChange(novo: String) {
        _uiState.update { it.copy(dataNascimento = novo, erroDataNascimento = null, erroGeral = null) }
    }

    fun onTermosAceitosChange(aceito: Boolean) {
        _uiState.update { it.copy(termosAceitos = aceito, erroTermos = null, erroGeral = null) }
    }

    fun onConcluirClick(onSucesso: () -> Unit) {
        val estado = _uiState.value
        var valido = true

        if (estado.nome.trim().isBlank()) {
            _uiState.update { it.copy(erroNome = "Digite seu nome.") }
            valido = false
        }

        if (estado.sobrenome.trim().isBlank()) {
            _uiState.update { it.copy(erroSobrenome = "Digite seu sobrenome.") }
            valido = false
        }

        val dataNascStr = estado.dataNascimento.trim()
        val idade = calcularIdade(dataNascStr)

        if (dataNascStr.isBlank() || idade == null) {
            _uiState.update { it.copy(erroDataNascimento = "Digite uma data de nascimento válida.") }
            valido = false
        } else if (idade < 14) {
            // Regra legal: Bloqueio para menores de 14 anos
            _uiState.update { it.copy(erroDataNascimento = "Este app é para maiores de 14 anos.") }
            valido = false
        }

        if (!estado.termosAceitos) {
            _uiState.update { it.copy(erroTermos = "Aceite os termos para continuar.") }
            valido = false
        }

        if (!valido) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erroGeral = null) }
            try {
                val perfil = Perfil(
                    nome = estado.nome.trim(),
                    sobrenome = estado.sobrenome.trim(),
                    dataNascimento = estado.dataNascimento.trim(),
                    termosAceitos = true,
                )
                authRepository.concluirCadastro(perfil)
                _uiState.update { it.copy(isLoading = false) }
                onSucesso()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erroGeral = e.message ?: "Não foi possível concluir o cadastro. Tente de novo.",
                    )
                }
            }
        }
    }

    private fun calcularIdade(dataStr: String): Int? {
        val formatters = listOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
        )

        var date: Date? = null
        for (f in formatters) {
            try {
                f.isLenient = false
                date = f.parse(dataStr)
                if (date != null) break
            } catch (ignored: Exception) {
            }
        }

        if (date == null) return null

        val calNasc = Calendar.getInstance().apply { time = date }
        val calHoje = Calendar.getInstance()

        var idade = calHoje.get(Calendar.YEAR) - calNasc.get(Calendar.YEAR)
        if (calHoje.get(Calendar.DAY_OF_YEAR) < calNasc.get(Calendar.DAY_OF_YEAR)) {
            idade--
        }
        return idade
    }
}
