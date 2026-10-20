package com.example.acervia.domain.repository

import com.example.acervia.domain.model.Perfil
import com.example.acervia.domain.model.ResultadoLogin
import com.example.acervia.domain.model.Sessao
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessao: Flow<Sessao?>
    suspend fun enviarCodigo(email: String)
    suspend fun entrarComCodigo(email: String, codigo: String): ResultadoLogin
    suspend fun entrarComGoogle(): ResultadoLogin
    suspend fun concluirCadastro(perfil: Perfil)
    suspend fun sair()
}
