package com.example.acervia.ui.screens.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acervia.domain.model.LivroResumo
import com.example.acervia.domain.repository.LivroRepository
import com.example.acervia.domain.repository.Ordem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class CatalogoUiState(
    val query: String = "",
    val soDisponiveis: Boolean = false,
    val ordem: Ordem = Ordem.TITULO,
    val isLoading: Boolean = true,
    val isLoadingMais: Boolean = false,
    val erro: String? = null,
    val erroCarregarMais: String? = null,
    val isSemConexao: Boolean = false,
    val livros: List<LivroResumo> = emptyList(),
    val totalItens: Int = 0,
    val paginaAtual: Int = 1,
    val temProximaPagina: Boolean = false,
    val mostrarBottomSheetOrdenacao: Boolean = false,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class CatalogoViewModel @Inject constructor(
    private val livroRepository: LivroRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    private val _queryInput = MutableStateFlow("")

    init {
        carregarLivros()

        viewModelScope.launch {
            _queryInput
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(query = query) }
                    carregarLivros()
                }
        }
    }

    fun onQueryChange(novoTexto: String) {
        _queryInput.value = novoTexto
        _uiState.update { it.copy(query = novoTexto) }
    }

    fun onLimparQuery() {
        onQueryChange("")
    }

    fun onSoDisponiveisToggle(ativo: Boolean) {
        _uiState.update { it.copy(soDisponiveis = ativo) }
        carregarLivros()
    }

    fun onOrdemSelecionada(novaOrdem: Ordem) {
        _uiState.update { it.copy(ordem = novaOrdem, mostrarBottomSheetOrdenacao = false) }
        carregarLivros()
    }

    fun onMostrarBottomSheetOrdenacao(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarBottomSheetOrdenacao = mostrar) }
    }

    fun onLimparFiltros() {
        _queryInput.value = ""
        _uiState.update {
            it.copy(
                query = "",
                soDisponiveis = false,
                ordem = Ordem.TITULO,
            )
        }
        carregarLivros()
    }

    fun carregarLivros() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    erro = null,
                    isSemConexao = false,
                    paginaAtual = 1,
                )
            }
            try {
                val estado = _uiState.value
                val pagina = livroRepository.buscar(
                    q = estado.query,
                    soDisponiveis = estado.soDisponiveis,
                    ordem = estado.ordem,
                    pagina = 1,
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        livros = pagina.itens,
                        totalItens = pagina.itens.size,
                        paginaAtual = 1,
                        temProximaPagina = pagina.temProximaPagina,
                    )
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSemConexao = true,
                        erro = "Sem conexão. Mostrando os dados salvos.",
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = e.message ?: "Não conseguimos carregar o catálogo agora.",
                    )
                }
            }
        }
    }

    fun carregarMaisLivros() {
        val estado = _uiState.value
        if (estado.isLoading || estado.isLoadingMais || !estado.temProximaPagina) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMais = true, erroCarregarMais = null) }
            try {
                val proximaPaginaNum = estado.paginaAtual + 1
                val pagina = livroRepository.buscar(
                    q = estado.query,
                    soDisponiveis = estado.soDisponiveis,
                    ordem = estado.ordem,
                    pagina = proximaPaginaNum,
                )
                _uiState.update {
                    it.copy(
                        isLoadingMais = false,
                        livros = it.livros + pagina.itens,
                        paginaAtual = proximaPaginaNum,
                        temProximaPagina = pagina.temProximaPagina,
                        totalItens = it.totalItens + pagina.itens.size,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMais = false,
                        erroCarregarMais = "Não foi possível carregar mais.",
                    )
                }
            }
        }
    }
}
