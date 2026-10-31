package com.example.acervia.ui.screens.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.data.DemoConfig
import com.example.acervia.domain.repository.AuthRepository
import com.example.acervia.domain.repository.PreferenciasRepository
import com.example.acervia.ui.theme.TemaOpcao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AjustesUiState(
    val nomeUsuario: String = "Marina Silva",
    val emailUsuario: String = "marina@exemplo.com",
    val iniciais: String = "MS",
    val simularErro: Boolean = false,
    val simularSemConexao: Boolean = false,
    val temaAtual: String = "Sistema",
    val mostrarBottomSheetAparencia: Boolean = false,
    val mostrarBottomSheetSair: Boolean = false,
    val isSaindo: Boolean = false,
)

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferenciasRepository: PreferenciasRepository,
    private val demoConfig: DemoConfig,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesUiState())
    val uiState: StateFlow<AjustesUiState> = _uiState.asStateFlow()

    init {
        carregarPerfil()
        observarTema()
    }

    private fun observarTema() {
        viewModelScope.launch {
            preferenciasRepository.temaEscolhido.collect { temaOpcao ->
                val rotulo = when (temaOpcao) {
                    TemaOpcao.CLARO -> "Claro"
                    TemaOpcao.ESCURO -> "Escuro"
                    TemaOpcao.SISTEMA -> "Sistema"
                }
                _uiState.update { it.copy(temaAtual = rotulo) }
            }
        }
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            authRepository.sessao.collect { sessao ->
                if (sessao != null) {
                    val nome = sessao.perfil?.nome ?: "Marina"
                    val sobrenome = sessao.perfil?.sobrenome ?: "Silva"
                    val nomeCompleto = "$nome $sobrenome".trim()
                    val inits = "${nome.firstOrNull() ?: 'M'}${sobrenome.firstOrNull() ?: 'S'}".uppercase()

                    _uiState.update {
                        it.copy(
                            nomeUsuario = if (nomeCompleto.isBlank()) "Marina Silva" else nomeCompleto,
                            emailUsuario = sessao.email,
                            iniciais = inits,
                            simularErro = demoConfig.simularErro,
                            simularSemConexao = demoConfig.simularSemConexao,
                        )
                    }
                }
            }
        }
    }

    fun onSimularErroToggle(ativo: Boolean) {
        demoConfig.simularErro = ativo
        _uiState.update { it.copy(simularErro = ativo) }
    }

    fun onSimularSemConexaoToggle(ativo: Boolean) {
        demoConfig.simularSemConexao = ativo
        _uiState.update { it.copy(simularSemConexao = ativo) }
    }

    fun onMostrarBottomSheetAparencia(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarBottomSheetAparencia = mostrar) }
    }

    fun onTemaSelecionado(novoTema: String) {
        val opcao = when (novoTema) {
            "Claro" -> TemaOpcao.CLARO
            "Escuro" -> TemaOpcao.ESCURO
            else -> TemaOpcao.SISTEMA
        }
        viewModelScope.launch {
            preferenciasRepository.salvarTema(opcao)
            _uiState.update { it.copy(temaAtual = novoTema, mostrarBottomSheetAparencia = false) }
        }
    }

    fun onMostrarBottomSheetSair(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarBottomSheetSair = mostrar) }
    }

    fun onConfirmarSairClick(onLogoutConcluido: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaindo = true) }
            authRepository.sair()
            _uiState.update { it.copy(isSaindo = false, mostrarBottomSheetSair = false) }
            onLogoutConcluido()
        }
    }
}
