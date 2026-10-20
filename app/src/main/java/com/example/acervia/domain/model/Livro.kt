package com.example.acervia.domain.model

data class LivroResumo(
    val volumeId: String,
    val isbn13: String?,
    val titulo: String,
    val autores: List<String>,
    val capaUrl: String?,
    val exemplaresTotal: Int,
    val exemplaresDisponiveis: Int,
) {
    val disponivel: Boolean get() = exemplaresDisponiveis > 0
}

data class Livro(
    val volumeId: String,
    val isbn13: String?,
    val titulo: String,
    val autores: List<String>,
    val descricao: String?,
    val categoria: String?,
    val paginas: Int?,
    val editora: String?,
    val capaUrl: String?,
    val exemplaresTotal: Int,
    val exemplaresDisponiveis: Int,
) {
    val disponivel: Boolean get() = exemplaresDisponiveis > 0
}

data class Pagina<T>(
    val itens: List<T>,
    val paginaAtual: Int,
    val temProximaPagina: Boolean,
)
