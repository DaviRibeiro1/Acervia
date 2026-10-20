package com.example.acervia.domain.repository

import com.example.acervia.domain.model.HomeDados
import com.example.acervia.domain.model.Livro
import com.example.acervia.domain.model.LivroResumo
import com.example.acervia.domain.model.Pagina

enum class Ordem {
    TITULO,
    MAIS_RESERVADOS,
    MAIS_RECENTES,
    RELEVANCIA,
}

interface LivroRepository {
    suspend fun home(): HomeDados
    suspend fun buscar(q: String?, soDisponiveis: Boolean, ordem: Ordem, pagina: Int): Pagina<LivroResumo>
    suspend fun livro(id: String): Livro
}
