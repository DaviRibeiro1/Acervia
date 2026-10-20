package com.example.acervia.domain.repository

import com.example.acervia.domain.model.IdiomaEscolhido
import com.example.acervia.ui.theme.TemaOpcao
import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {
    val temaEscolhido: Flow<TemaOpcao>
    val idiomaEscolhido: Flow<IdiomaEscolhido>
    suspend fun salvarTema(tema: TemaOpcao)
    suspend fun salvarIdioma(idioma: IdiomaEscolhido)
}
