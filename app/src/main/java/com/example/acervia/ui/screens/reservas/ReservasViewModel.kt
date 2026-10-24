package com.example.acervia.ui.screens.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.domain.repository.ReservaRepository
import com.example.acervia.domain.usecase.CalcularPrazoReserva
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservasUiState(
    val isLoading: Boolean = true,
    val erro: String? = null,
    val reservasAtivas: List<Reserva> = emptyList(),
    val historicoReservas: List<Reserva> = emptyList(),
    val historicoExibido: List<Reserva> = emptyList(),
    val quantidadeHistoricoExibida: Int = 10,
    val temMaisHistorico: Boolean = false,
)

@HiltViewModel
class ReservasViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservasUiState())
    val uiState: StateFlow<ReservasUiState> = _uiState.asStateFlow()

    private val calcularPrazoReserva = CalcularPrazoReserva()

    init {
        carregarReservas()
    }

    fun carregarReservas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            try {
                val listaCompleta = reservaRepository.listar()
                val agora = System.currentTimeMillis()

                val ativas = listaCompleta
                    .filter { it.status == StatusReserva.ATIVA && it.expiraEm > agora }
                    .sortedBy { it.expiraEm }

                val historico = listaCompleta
                    .filter { !(it.status == StatusReserva.ATIVA && it.expiraEm > agora) }
                    .sortedByDescending { it.reservadoEm }

                val qtdExibida = _uiState.value.quantidadeHistoricoExibida
                val exibidos = historico.take(qtdExibida)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        reservasAtivas = ativas,
                        historicoReservas = historico,
                        historicoExibido = exibidos,
                        temMaisHistorico = exibidos.size < historico.size,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = e.message ?: "Não conseguimos carregar suas reservas agora.",
                    )
                }
            }
        }
    }

    fun carregarMaisHistorico() {
        _uiState.update { estado ->
            val novaQtd = estado.quantidadeHistoricoExibida + 10
            val novosExibidos = estado.historicoReservas.take(novaQtd)
            estado.copy(
                quantidadeHistoricoExibida = novaQtd,
                historicoExibido = novosExibidos,
                temMaisHistorico = novosExibidos.size < estado.historicoReservas.size,
            )
        }
    }

    fun formatarPrazoTextual(reserva: Reserva): String {
        return calcularPrazoReserva.formatarPrazoTextual(reserva.expiraEm)
    }

    fun ehPrazoUrgente(reserva: Reserva): Boolean {
        return (reserva.expiraEm - System.currentTimeMillis()) <= (24 * 3600 * 1000L)
    }
}
