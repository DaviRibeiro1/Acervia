package com.example.acervia.data.repository

import com.example.acervia.domain.model.IdiomaEscolhido
import com.example.acervia.domain.repository.PreferenciasRepository
import com.example.acervia.ui.theme.TemaOpcao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePreferenciasRepository @Inject constructor() : PreferenciasRepository {

    private val _temaEscolhido = MutableStateFlow(TemaOpcao.SISTEMA)
    override val temaEscolhido: Flow<TemaOpcao> = _temaEscolhido.asStateFlow()

    private val _idiomaEscolhido = MutableStateFlow<IdiomaEscolhido>(IdiomaEscolhido.Sistema)
    override val idiomaEscolhido: Flow<IdiomaEscolhido> = _idiomaEscolhido.asStateFlow()

    override suspend fun salvarTema(tema: TemaOpcao) {
        _temaEscolhido.value = tema
    }

    override suspend fun salvarIdioma(idioma: IdiomaEscolhido) {
        _idiomaEscolhido.value = idioma
    }
}
