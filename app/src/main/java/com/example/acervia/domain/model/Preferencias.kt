package com.example.acervia.domain.model

enum class Idioma(val tag: String, val nome: String) {
    PT_BR("pt-BR", "Português (Brasil)"),
}

object Idiomas {
    val disponiveis: List<Idioma> = Idioma.entries
    val seletorVisivel: Boolean get() = disponiveis.size > 1
}

sealed interface IdiomaEscolhido {
    data object Sistema : IdiomaEscolhido
    data class Fixo(val idioma: Idioma) : IdiomaEscolhido
}
