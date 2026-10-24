package com.example.acervia.ui.screens.detalhes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.Livro
import com.example.acervia.domain.repository.LivroRepository
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
)

@HiltViewModel
class DetalhesLivroViewModel @Inject constructor(
    private val livroRepository: LivroRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val volumeId: String = savedStateHandle["volumeId"] ?: ""

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
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        livro = livroDetalhes,
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
}
