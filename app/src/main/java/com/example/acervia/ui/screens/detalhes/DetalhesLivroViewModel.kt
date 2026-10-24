package com.example.acervia.ui.screens.detalhes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.Livro
import com.example.acervia.domain.model.ResultadoReserva
import com.example.acervia.domain.repository.LivroRepository
import com.example.acervia.domain.repository.ReservaRepository
import com.example.acervia.domain.usecase.CalcularPrazoReserva
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetalhesLivroUiState(
    val isLoading: Boolean = true,
    val livro: Livro? = null,
    val erro: String? = null,
    val descricaoExpandida: Boolean = false,
    val mostrarBottomSheetConfirmacao: Boolean = false,
    val isReservando: Boolean = false,
    val prazoTextualFormatted: String = "",
    val erroReserva: String? = null,
)

@HiltViewModel
class DetalhesLivroViewModel @Inject constructor(
    private val livroRepository: LivroRepository,
    private val reservaRepository: ReservaRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val volumeId: String = savedStateHandle["volumeId"] ?: ""
    private val calcularPrazoReserva = CalcularPrazoReserva()

    private val _uiState = MutableStateFlow(DetalhesLivroUiState())
    val uiState: StateFlow<DetalhesLivroUiState> = _uiState.asStateFlow()

    init {
        carregarDetalhes()
    }

    fun carregarDetalhes() {
        if (volumeId.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false, erro = "Identificador do livro não informado.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            try {
                val livroDetalhes = livroRepository.livro(volumeId)
                val expiraEm = calcularPrazoReserva()
                val prazoTexto = calcularPrazoReserva.formatarPrazoTextual(expiraEm)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        livro = livroDetalhes,
                        prazoTextualFormatted = prazoTexto,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = e.message ?: "Não conseguimos carregar este livro agora.",
                    )
                }
            }
        }
    }

    fun toggleDescricaoExpandida() {
        _uiState.update { it.copy(descricaoExpandida = !it.descricaoExpandida) }
    }

    fun onAbrirBottomSheetConfirmacao() {
        _uiState.update { it.copy(mostrarBottomSheetConfirmacao = true, erroReserva = null) }
    }

    fun onFecharBottomSheetConfirmacao() {
        _uiState.update { it.copy(mostrarBottomSheetConfirmacao = false, erroReserva = null) }
    }

    fun onConfirmarReserva(onSucesso: (String) -> Unit) {
        val livro = _uiState.value.livro ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isReservando = true, erroReserva = null) }
            when (val resultado = reservaRepository.reservar(livro.volumeId)) {
                is ResultadoReserva.Ok -> {
                    _uiState.update { it.copy(isReservando = false, mostrarBottomSheetConfirmacao = false) }
                    onSucesso(resultado.reserva.id)
                }
                is ResultadoReserva.JaReservado -> {
                    _uiState.update { it.copy(isReservando = false, mostrarBottomSheetConfirmacao = false) }
                    onSucesso(resultado.reserva.id)
                }
                is ResultadoReserva.Esgotado -> {
                    _uiState.update {
                        it.copy(
                            isReservando = false,
                            erroReserva = "Este livro acabou de ser reservado por outra pessoa.",
                        )
                    }
                }
            }
        }
    }
}
