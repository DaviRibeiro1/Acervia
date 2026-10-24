package com.example.acervia.data

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DemoConfig @Inject constructor() {
    var simularErro: Boolean = false
    var simularSemConexao: Boolean = false
    var latenciaMinMs: Long = 300L
    var latenciaMaxMs: Long = 800L

    suspend fun simularLatenciaEFalhas() {
        val ms = if (latenciaMaxMs > latenciaMinMs) {
            Random.nextLong(latenciaMinMs, latenciaMaxMs)
        } else {
            latenciaMinMs
        }
        delay(ms)

        if (simularSemConexao) {
            throw java.io.IOException("Sem conexão com a internet. Verifique e tente de novo.")
        }
        if (simularErro) {
            throw IllegalStateException("Não foi possível carregar os dados agora. Tente de novo.")
        }
    }

    fun resetar() {
        simularErro = false
        simularSemConexao = false
        latenciaMinMs = 300L
        latenciaMaxMs = 800L
    }
}
