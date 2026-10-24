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
import kotlin.random.Random

@Singleton
class FakeLivroRepository @Inject constructor() : LivroRepository {

    var simularErro: Boolean = false
    var simularSemConexao: Boolean = false

    private suspend fun simularLatencia() {
        val ms = Random.nextLong(300, 800)
        delay(ms)
        if (simularSemConexao) {
            throw java.io.IOException("Sem conexão com a internet.")
        }
        if (simularErro) {
            throw IllegalStateException("Erro ao carregar dados do servidor.")
        }
    }

    private val acervoCompleto: List<Livro> = buildList {
        add(
            Livro(
                volumeId = "vol_1",
                isbn13 = "9786555600292",
                titulo = "A Biblioteca da Meia-Noite",
                autores = listOf("Matt Haig"),
                descricao = "Nora Seed é uma mulher de 35 anos cheia de arrepimentos. Entre a vida e a morte, ela chega à Biblioteca da Meia-Noite, onde cada livro oferece a chance de vivenciar outra versão de sua vida.",
                categoria = "Ficção",
                paginas = 308,
                editora = "Bertrand Brasil",
                capaUrl = "https://images.books.google.com/books/content?id=vol_1",
                exemplaresTotal = 3,
                exemplaresDisponiveis = 3,
            )
        )
        add(
            Livro(
                volumeId = "vol_2",
                isbn13 = "9788535914849",
                titulo = "1984",
                autores = listOf("George Orwell"),
                descricao = "Winston Smith vive em um regime totalitário sob a vigilância constante do Grande Irmão.",
                categoria = "Ficção Científica",
                paginas = 416,
                editora = "Companhia das Letras",
                capaUrl = null, // Edge case: Sem capa
                exemplaresTotal = 5,
                exemplaresDisponiveis = 2,
            )
        )
        add(
            Livro(
                volumeId = "vol_3",
                isbn13 = "9788576573005",
                titulo = "Duna",
                autores = listOf("Frank Herbert"),
                descricao = "Paul Atreides é um jovem herdeiro enviado para o desértico e perigoso planeta Arrakis.",
                categoria = "Ficção Científica",
                paginas = 680,
                editora = "Editora Aleph",
                capaUrl = null,
                exemplaresTotal = 4,
                exemplaresDisponiveis = 1, // Edge case: 1 exemplar disponível
            )
        )
        add(
            Livro(
                volumeId = "vol_4",
                isbn13 = "9788501115829",
                titulo = "O Sol É Para Todos",
                autores = listOf("Harper Lee"),
                descricao = "Um clássico da literatura moderna sobre injustiça racial e perda da inocência no sul dos EUA.",
                categoria = "Romance",
                paginas = 349,
                editora = "José Olympio",
                capaUrl = null,
                exemplaresTotal = 2,
                exemplaresDisponiveis = 0, // Edge case: Indisponível (0 exemplares)
            )
        )
        add(
            Livro(
                volumeId = "vol_5",
                isbn13 = "9788535902778",
                titulo = "Dom Casmurro",
                autores = listOf("Machado de Assis"),
                descricao = "Bento Santiago narra sua história de amor e ciúme por Capitu, levantando a eterna dúvida.",
                categoria = "Literatura Brasileira",
                paginas = 256,
                editora = "Companhia das Letras",
                capaUrl = null,
                exemplaresTotal = 6,
                exemplaresDisponiveis = 4,
            )
        )
        add(
            Livro(
                volumeId = "vol_6",
                isbn13 = "9788595084742",
                titulo = "O Senhor dos Anéis: A Sociedade do Anel e a Longa Jornada Através da Terra Média de Tolkien",
                autores = listOf("J.R.R. Tolkien"),
                descricao = "Frodo Bolseiro recebe a missão épica de destruir o Um Anel nas montanhas de Mordor.",
                categoria = "Fantasia",
                paginas = 576,
                editora = "HarperCollins",
                capaUrl = null, // Edge case: Título muito longo
                exemplaresTotal = 4,
                exemplaresDisponiveis = 2,
            )
        )
        add(
            Livro(
                volumeId = "vol_7",
                isbn13 = "9788535909555",
                titulo = "A Revolução dos Bichos",
                autores = listOf("George Orwell"),
                descricao = null, // Edge case: Sem descrição
                categoria = "Sátira Política",
                paginas = 152,
                editora = "Companhia das Letras",
                capaUrl = null,
                exemplaresTotal = 3,
                exemplaresDisponiveis = 3,
            )
        )
        add(
            Livro(
                volumeId = "vol_8",
                isbn13 = "9788535911688",
                titulo = "Grande Sertão: Veredas",
                autores = listOf("Guimarães Rosa"),
                descricao = "O ex-jagunço Riobaldo conta sua trajetória pelo sertão brasileiro e suas reflexões existenciais.",
                categoria = "Literatura Brasileira",
                paginas = null, // Edge case: Sem número de páginas
                editora = "Companhia das Letras",
                capaUrl = null,
                exemplaresTotal = 2,
                exemplaresDisponiveis = 1,
            )
        )

        // Gerar livros adicionais para totalizar 50 exemplares (para testar paginação infinita com páginas de 20 itens)
        val generos = listOf("Ficção", "História", "Filosofia", "Biografia", "Ciência")
        for (i in 9..50) {
            add(
                Livro(
                    volumeId = "vol_$i",
                    isbn13 = "978850000${1000 + i}",
                    titulo = "Livro de Demonstração Acervia Vol. $i",
                    autores = listOf("Autor Exemplo $i"),
                    descricao = "Esta é uma descrição gerada para o livro de demonstração volume $i do acervo da biblioteca.",
                    categoria = generos[i % generos.size],
                    paginas = 150 + (i * 5),
                    editora = "Editora Acervia",
                    capaUrl = null,
                    exemplaresTotal = (i % 5) + 1,
                    exemplaresDisponiveis = i % 4,
                )
            )
        }
    }

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
        simularLatencia()
        val destaquesResumo = acervoCompleto.take(8).map { it.toResumo() }
        return HomeDados(
            nomeUsuario = "Marina",
            reservaAtivaMaisProxima = mockReservaAtiva,
            quantidadeReservasAtivas = 1,
            destaques = destaquesResumo,
        )
    }

    override suspend fun buscar(
        q: String?,
        soDisponiveis: Boolean,
        ordem: Ordem,
        pagina: Int,
    ): Pagina<LivroResumo> {
        simularLatencia()
        var lista = acervoCompleto.map { it.toResumo() }

        if (!q.isNullOrBlank()) {
            val query = q.trim().lowercase()
            lista = lista.filter {
                it.titulo.lowercase().contains(query) ||
                        it.autores.any { autor -> autor.lowercase().contains(query) } ||
                        (it.isbn13 != null && it.isbn13.contains(query))
            }
        }

        if (soDisponiveis) {
            lista = lista.filter { it.disponivel }
        }

        lista = when (ordem) {
            Ordem.TITULO -> lista.sortedBy { it.titulo }
            Ordem.MAIS_RESERVADOS -> lista.sortedByDescending { it.exemplaresTotal - it.exemplaresDisponiveis }
            Ordem.MAIS_RECENTES -> lista.reversed()
            Ordem.RELEVANCIA -> lista
        }

        val tamanhoPagina = 20
        val inicio = (pagina - 1) * tamanhoPagina
        val fim = minOf(inicio + tamanhoPagina, lista.size)

        val itensPagina = if (inicio < lista.size) {
            lista.subList(inicio, fim)
        } else {
            emptyList()
        }

        return Pagina(
            itens = itensPagina,
            paginaAtual = pagina,
            temProximaPagina = fim < lista.size,
        )
    }

    override suspend fun livro(id: String): Livro {
        simularLatencia()
        return acervoCompleto.find { it.volumeId == id }
            ?: throw IllegalArgumentException("Livro não encontrado no acervo: $id")
    }

    private fun Livro.toResumo() = LivroResumo(
        volumeId = volumeId,
        isbn13 = isbn13,
        titulo = titulo,
        autores = autores,
        capaUrl = capaUrl,
        exemplaresTotal = exemplaresTotal,
        exemplaresDisponiveis = exemplaresDisponiveis,
    )
}
