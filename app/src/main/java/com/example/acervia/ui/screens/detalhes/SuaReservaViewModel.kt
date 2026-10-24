package com.example.acervia.ui.screens.detalhes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.repository.ReservaRepository
import com.example.acervia.domain.usecase.CalcularPrazoReserva
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SuaReservaUiState(
    val isLoading: Boolean = true,
    val reserva: Reserva? = null,
    val prazoTexto: String = "",
    val prazoUrgente: Boolean = false,
    val isConfirmacao: Boolean = false,
    val mostrarDialogoCancelamento: Boolean = false,
    val isCancelando: Boolean = false,
    val erro: String? = null,
)

@HiltViewModel
class SuaReservaViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val reservaId: String = savedStateHandle["reservaId"] ?: ""
    private val isConfirmacaoParam: Boolean = savedStateHandle.get<Boolean>("confirmacao") ?: false
    private val calcularPrazoReserva = CalcularPrazoReserva()

    private val _uiState = MutableStateFlow(SuaReservaUiState(isConfirmacao = isConfirmacaoParam))
    val uiState: StateFlow<SuaReservaUiState> = _uiState.asStateFlow()

    init {
        carregarReserva()
    }

    fun carregarReserva() {
        if (reservaId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, erro = "Identificador da reserva não informado.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            try {
                val reservaObj = reservaRepository.reserva(reservaId)
                if (reservaObj != null) {
                    val agora = System.currentTimeMillis()
                    val prazoTexto = calcularPrazoReserva.formatarPrazoTextual(reservaObj.expiraEm, agora)
                    val urgente = (reservaObj.expiraEm - agora) <= (24 * 3600 * 1000L)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reserva = reservaObj,
                            prazoTexto = prazoTexto,
                            prazoUrgente = urgente,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, erro = "Reserva não encontrada.") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, erro = e.message ?: "Não conseguimos carregar a reserva agora.")
                }
            }
        }
    }

    fun onMostrarDialogoCancelamento(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarDialogoCancelamento = mostrar) }
    }

    fun onConfirmarCancelamento(onSucessoCancelamento: () -> Unit) {
        val res = _uiState.value.reserva ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isCancelando = true) }
            val ok = reservaRepository.cancelar(res.id)
            if (ok) {
                _uiState.update { it.copy(isCancelando = false, mostrarDialogoCancelamento = false) }
                onSucessoCancelamento()
            } else {
                _uiState.update {
                    it.copy(
                        isCancelando = false,
                        erro = "Não conseguimos cancelar agora. Tente de novo.",
                    )
                }
            }
        }
    }
}
