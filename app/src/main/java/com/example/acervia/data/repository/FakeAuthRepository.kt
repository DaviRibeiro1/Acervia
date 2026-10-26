package com.example.acervia.data.repository

import com.example.acervia.data.DemoConfig
import com.example.acervia.domain.model.Perfil
import com.example.acervia.domain.model.ResultadoLogin
import com.example.acervia.domain.model.Sessao
import com.example.acervia.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAuthRepository @Inject constructor(
    private val demoConfig: DemoConfig,
) : AuthRepository {

    private val sessaoDemo = Sessao(
        usuarioId = "usr_1",
        email = "marina@exemplo.com",
        perfil = Perfil(
            nome = "Marina",
            sobrenome = "Silva",
            dataNascimento = "1998-05-12",
            termosAceitos = true,
        ),
    )

    private val _sessao = MutableStateFlow<Sessao?>(sessaoDemo)
    override val sessao: Flow<Sessao?> = _sessao.asStateFlow()

    private val _primeiroAcesso = MutableStateFlow(false)
    val primeiroAcessoFlow: Flow<Boolean> = _primeiroAcesso.asStateFlow()

    fun marcarPrimeiroAcessoConcluido() {
        _primeiroAcesso.value = false
    }

    override suspend fun enviarCodigo(email: String) {
        demoConfig.simularLatenciaEFalhas()
    }

    override suspend fun entrarComCodigo(email: String, codigo: String): ResultadoLogin {
        demoConfig.simularLatenciaEFalhas()
        return if (codigo == "123456" || codigo.length == 6) {
            val novaSessao = Sessao(
                usuarioId = "usr_${System.currentTimeMillis()}",
                email = email,
                perfil = if (email == "marina@exemplo.com") sessaoDemo.perfil else null,
            )
            _sessao.value = novaSessao
            ResultadoLogin.Sucesso(
                sessao = novaSessao,
                contaNova = novaSessao.perfil == null,
            )
        } else {
            ResultadoLogin.Erro("Código incorreto. Confira e tente de novo.")
        }
    }

    override suspend fun entrarComGoogle(): ResultadoLogin {
        demoConfig.simularLatenciaEFalhas()
        _sessao.value = sessaoDemo
        return ResultadoLogin.Sucesso(sessao = sessaoDemo, contaNova = false)
    }

    override suspend fun concluirCadastro(perfil: Perfil) {
        demoConfig.simularLatenciaEFalhas()
        val atual = _sessao.value
        if (atual != null) {
            _sessao.value = atual.copy(perfil = perfil)
        }
    }

    override suspend fun sair() {
        demoConfig.simularLatenciaEFalhas()
        _sessao.value = null
    }
}
