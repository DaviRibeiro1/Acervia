package com.example.acervia.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.LivroResumo
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.domain.repository.AuthRepository
import com.example.acervia.domain.repository.LivroRepository
import com.example.acervia.domain.repository.Ordem
import com.example.acervia.domain.repository.ReservaRepository
import com.example.acervia.domain.usecase.CalcularPrazoReserva
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val nomeUsuario: String = "",
    val reservaAtiva: Reserva? = null,
    val prazoTextoReserva: String = "",
    val prazoUrgenteReserva: Boolean = false,
    val livrosDestaque: List<LivroResumo> = emptyList(),
    val erro: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reservaRepository: ReservaRepository,
    private val livroRepository: LivroRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val calcularPrazoReserva = CalcularPrazoReserva()

    init {
        carregarHome()
    }

    fun carregarHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            executarCarregamento()
        }
    }

    fun atualizarHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, erro = null) }
            executarCarregamento()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun executarCarregamento() {
        try {
            val sessao = authRepository.sessao.firstOrNull()
            val nome = sessao?.perfil?.nome.orEmpty()

            val reservas = reservaRepository.listar()
            val agora = System.currentTimeMillis()
            val reservaAtiva = reservas
                .filter { it.status == StatusReserva.ATIVA && it.expiraEm > agora }
                .minByOrNull { it.expiraEm }

            var prazoTexto = ""
            var urgente = false
            if (reservaAtiva != null) {
                prazoTexto = calcularPrazoReserva.formatarPrazoTextual(reservaAtiva.expiraEm, agora)
                urgente = (reservaAtiva.expiraEm - agora) <= (24 * 3600 * 1000L)
            }

            val paginaDestaque = livroRepository.buscar(
                q = "",
                soDisponiveis = false,
                ordem = Ordem.MAIS_RESERVADOS,
                pagina = 1,
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    nomeUsuario = nome,
                    reservaAtiva = reservaAtiva,
                    prazoTextoReserva = prazoTexto,
                    prazoUrgenteReserva = urgente,
                    livrosDestaque = paginaDestaque.itens,
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    erro = e.message ?: "Não conseguimos carregar o início agora.",
                )
            }
        }
    }
}
