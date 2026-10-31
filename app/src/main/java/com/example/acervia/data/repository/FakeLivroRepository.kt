package com.example.acervia.data.repository

import android.content.Context
import android.content.ContextWrapper
import com.example.acervia.data.DemoConfig
import com.example.acervia.domain.model.HomeDados
import com.example.acervia.domain.model.Livro
import com.example.acervia.domain.model.LivroResumo
import com.example.acervia.domain.model.Pagina
import com.example.acervia.domain.model.Reserva
import com.example.acervia.domain.model.StatusReserva
import com.example.acervia.domain.repository.LivroRepository
import com.example.acervia.domain.repository.Ordem
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeLivroRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val demoConfig: DemoConfig,
) : LivroRepository {

    constructor(demoConfig: DemoConfig, testMode: Boolean = true) : this(
        context = ContextWrapper(null),
        demoConfig = demoConfig,
    )

    private val acervoCompleto: List<Livro> by lazy {
        val doJson = carregarLivrosDoAsset()
        doJson.ifEmpty { acervoFallback }
    }

    private fun carregarLivrosDoAsset(): List<Livro> {
        return try {
            val jsonString = context.assets.open("livros.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val lista = mutableListOf<Livro>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val volumeId = obj.optString("volume_id", "vol_${i + 1}")
                val isbn13 = obj.optString("isbn13").takeIf { it.isNotBlank() }
                val titulo = obj.optString("titulo", "Sem título")

                val autoresArray = obj.optJSONArray("autores")
                val autoresList = mutableListOf<String>()
                if (autoresArray != null) {
                    for (j in 0 until autoresArray.length()) {
                        autoresList.add(autoresArray.getString(j))
                    }
                }
                if (autoresList.isEmpty()) {
                    autoresList.add("Autor desconhecido")
                }

                val editora = obj.optString("editora").takeIf { it.isNotBlank() }
                val paginas = if (obj.has("paginas") && !obj.isNull("paginas")) obj.optInt("paginas") else null
                val categoria = obj.optString("categoria").takeIf { it.isNotBlank() }
                val descricao = obj.optString("descricao").takeIf { it.isNotBlank() }
                val capaUrl = obj.optString("capa_url").takeIf { it.isNotBlank() }
                val exemplaresTotal = obj.optInt("exemplares", 3)

                lista.add(
                    Livro(
                        volumeId = volumeId,
                        isbn13 = isbn13,
                        titulo = titulo,
                        autores = autoresList,
                        descricao = descricao,
                        categoria = categoria,
                        paginas = paginas,
                        editora = editora,
                        capaUrl = capaUrl,
                        exemplaresTotal = exemplaresTotal,
                        exemplaresDisponiveis = exemplaresTotal,
                    )
                )
            }
            lista
        } catch (ignored: Exception) {
            emptyList()
        }
    }

    private val acervoFallback: List<Livro> = buildList {
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
                capaUrl = null,
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
                exemplaresDisponiveis = 1,
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
                exemplaresDisponiveis = 0,
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
                capaUrl = null,
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
                descricao = null,
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
                paginas = null,
                editora = "Companhia das Letras",
                capaUrl = null,
                exemplaresTotal = 2,
                exemplaresDisponiveis = 1,
            )
        )

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
        volumeId = "qmE0EQAAQBAJ",
        livroTitulo = "Dom Casmurro",
        livroAutor = "Machado de Assis",
        capaUrl = "https://books.google.com/books/publisher/content?id=qmE0EQAAQBAJ&printsec=frontcover&img=1&zoom=1&imgtk=AFLRE71rCNDl_txW2Yiw5X3tuvA_seFzem3sCBAiAeUYi3MhiPIEuV6KeYnXq0RgTaq048_O9ZL67JYMQFZFlxEnK76TEkAQUoTS2DRsTxZNKVcjSTB1U2xzpX24hoXAa-7T1nN0hTbQ&source=gbs_api",
        codigoRetirada = "482716",
        status = StatusReserva.ATIVA,
        reservadoEm = System.currentTimeMillis() - (3600 * 1000 * 5),
        expiraEm = System.currentTimeMillis() + (3600 * 1000 * 20),
        localBiblioteca = "Biblioteca Central",
        statusBiblioteca = "Aberta agora, fecha às 18h",
    )

    override suspend fun home(): HomeDados {
        demoConfig.simularLatenciaEFalhas()
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
        demoConfig.simularLatenciaEFalhas()
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
        demoConfig.simularLatenciaEFalhas()
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
