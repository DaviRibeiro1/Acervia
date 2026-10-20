package com.example.acervia.data.repository

import com.example.acervia.domain.model.HomeDados
import com.example.acervia.domain.model.Livro
import com.example.acervia.domain.model.LivroResumo
import com.example.acervia.domain.model.Pagina
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.domain.repository.LivroRepository
import com.example.acervia.domain.repository.Ordem
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeLivroRepository @Inject constructor() : LivroRepository {

    private val mockLivros = listOf(
        LivroResumo(
            volumeId = "vol_1",
            isbn13 = "9786555600292",
            titulo = "A Biblioteca da Meia-Noite",
            autores = listOf("Matt Haig"),
            capaUrl = null,
            exemplaresTotal = 3,
            exemplaresDisponiveis = 3,
        ),
        LivroResumo(
            volumeId = "vol_2",
            isbn13 = "9788535914849",
            titulo = "1984",
            autores = listOf("George Orwell"),
            capaUrl = null,
            exemplaresTotal = 5,
            exemplaresDisponiveis = 2,
        ),
        LivroResumo(
            volumeId = "vol_3",
            isbn13 = "9788576573005",
            titulo = "Duna",
            autores = listOf("Frank Herbert"),
            capaUrl = null,
            exemplaresTotal = 4,
            exemplaresDisponiveis = 1,
        ),
        LivroResumo(
            volumeId = "vol_4",
            isbn13 = "9788501115829",
            titulo = "O Sol É Para Todos",
            autores = listOf("Harper Lee"),
            capaUrl = null,
            exemplaresTotal = 2,
            exemplaresDisponiveis = 0,
        ),
        LivroResumo(
            volumeId = "vol_5",
            isbn13 = "9788535902778",
            titulo = "Dom Casmurro",
            autores = listOf("Machado de Assis"),
            capaUrl = null,
            exemplaresTotal = 6,
            exemplaresDisponiveis = 4,
        ),
        LivroResumo(
            volumeId = "vol_6",
            isbn13 = "9788575421130",
            titulo = "O Hobbit",
            autores = listOf("J.R.R. Tolkien"),
            capaUrl = null,
            exemplaresTotal = 3,
            exemplaresDisponiveis = 2,
        ),
    )

    private val mockReservaAtiva = Reserva(
        id = "res_101",
        usuarioId = "usr_1",
        volumeId = "vol_1",
        livroTitulo = "A Biblioteca da Meia-Noite",
        livroAutor = "Matt Haig",
        capaUrl = null,
        codigoRetirada = "482716",
        status = StatusReserva.ATIVA,
        reservadoEm = System.currentTimeMillis() - (3600 * 1000 * 5),
        expiraEm = System.currentTimeMillis() + (3600 * 1000 * 20),
        localBiblioteca = "Biblioteca Central",
        statusBiblioteca = "Aberta agora, fecha às 18h",
    )

    override suspend fun home(): HomeDados {
        delay(400)
        return HomeDados(
            nomeUsuario = "Marina",
            reservaAtivaMaisProxima = mockReservaAtiva,
            quantidadeReservasAtivas = 1,
            destaques = mockLivros,
        )
    }

    override suspend fun buscar(
        q: String?,
        soDisponiveis: Boolean,
        ordem: Ordem,
        pagina: Int,
    ): Pagina<LivroResumo> {
        delay(500)
        var resultado = mockLivros

        if (!q.isNullOrBlank()) {
            val query = q.trim().lowercase()
            resultado = resultado.filter {
                it.titulo.lowercase().contains(query) ||
                        it.autores.any { autor -> autor.lowercase().contains(query) } ||
                        (it.isbn13 != null && it.isbn13.contains(query))
            }
        }

        if (soDisponiveis) {
            resultado = resultado.filter { it.disponivel }
        }

        resultado = when (ordem) {
            Ordem.TITULO -> resultado.sortedBy { it.titulo }
            Ordem.MAIS_RESERVADOS -> resultado
            Ordem.MAIS_RECENTES -> resultado.reversed()
            Ordem.RELEVANCIA -> resultado
        }

        return Pagina(
            itens = resultado,
            paginaAtual = pagina,
            temProximaPagina = false,
        )
    }

    override suspend fun livro(id: String): Livro {
        delay(300)
        val resumo = mockLivros.find { it.volumeId == id } ?: mockLivros.first()
        return Livro(
            volumeId = resumo.volumeId,
            isbn13 = resumo.isbn13,
            titulo = resumo.titulo,
            autores = resumo.autores,
            descricao = "Nora Seed é uma mulher de 35 anos que está cheia de arrependimentos sobre noites não vividas e caminhos não seguidos. Entre a vida e a morte, ela chega à Biblioteca da Meia-Noite, onde cada livro oferece a chance de vivenciar outra versão de sua vida.",
            categoria = "Ficção",
            paginas = 308,
            editora = "Bertrand Brasil",
            capaUrl = resumo.capaUrl,
            exemplaresTotal = resumo.exemplaresTotal,
            exemplaresDisponiveis = resumo.exemplaresDisponiveis,
        )
    }
}
