# Acervia — Aplicativo de Reserva de Livros

O **Acervia** é um aplicativo Android nativo para reservas de livros de biblioteca, desenvolvido em **Kotlin** com **Jetpack Compose**, **Material 3**, **Jetpack Navigation** e **Hilt DI**, seguindo rigorosamente as diretrizes do **Acervia Design System v0.1**.

---

## 📖 Sobre o Projeto

O Acervia simplifica o processo de descoberta e reserva de livros em bibliotecas. O usuário pode consultar o acervo, realizar reservas e visualizar seu código de retirada de 6 dígitos de forma rápida e offline no balcão de atendimento.

### Fluxo de Entrada e Navegação
- **Splash Screen:** Transição suave verificando sessão ativa ($\le 1,5\text{s}$).
- **Welcome:** Apresentação exibida no primeiro acesso.
- **Login:** Entrada única por Google Sign-In ou E-mail com código de acesso (OTP de 6 dígitos).
- **Falta Pouco:** Cadastro e confirmação de perfil para contas novas (Nome, Sobrenome, Data de Nascimento e validação de idade mínima $\ge 14$ anos).
- **Home:** Painel principal com saudação personalizada ("Olá, Marina..."), atalho de busca, cartão de reserva ativa com código formatado ($3+3$) e carrossel de destaques.
- **Catálogo, Detalhes e Reservas:** Busca com debounce de 400ms, rolagem infinita (20 em 20 itens), detalhes do exemplar com prazo de 3 dias úteis e aba de Reservas unificada (Ativas e Histórico).
- **Ajustes:** Gerenciamento do perfil, seleção de tema (Claro / Escuro / Seguir o sistema), chaves do modo de demonstração e Logout com confirmação modal.

---

## 🔑 Credenciais e Modo de Demonstração

O aplicativo roda com dados locais e simulação de latência (300 a 800ms) para funcionamento 100% offline.

- **E-mail de Teste:** `marina@exemplo.com` (Conta Existente) ou qualquer e-mail novo.
- **Código de Acesso OTP:** `123456`
- **Chaves de Teste (em Ajustes):**
  - **Simular erro de servidor:** Teste de tratamento de exceções na UI.
  - **Simular sem conexão:** Teste de banners offline e dados salvos no aparelho.

---

## 🏛️ Arquitetura e Diagrama de Dados

```
+-----------------------------------------------------------------+
|                       UI Layer (Compose)                        |
|  HomeScreen | CatalogoScreen | ReservasScreen | AjustesScreen... |
+-----------------------------------------------------------------+
                                |
                                v
+-----------------------------------------------------------------+
|                    ViewModel Layer (StateFlow)                  |
|   HomeViewModel | CatalogoViewModel | ReservasViewModel...      |
+-----------------------------------------------------------------+
                                |
                                v
+-----------------------------------------------------------------+
|               Domain Layer (Clean Architecture)                 |
|  LivroRepository | ReservaRepository | AuthRepository           |
|  CalcularPrazoReserva | CalcularDisponibilidade                 |
+-----------------------------------------------------------------+
                                |
                                v
+-----------------------------------------------------------------+
|                 Data Layer (Hilt @Singleton Demo)               |
|  FakeLivroRepository | FakeReservaRepository | FakeAuthRepo    |
|  livros.json (Acervo Local com 45+ Obras & Casos Extremos)      |
+-----------------------------------------------------------------+
```

---

## 🎨 Design System v0.1

O aplicativo utiliza o **Acervia Design System v0.1**, provido via `ui.theme`:

- **Cores & Tokens (`Color.kt`):** Azul-marinho de marca (`primary`: `#1E3A5F` no claro / `#9DB8E6` no escuro), neutros e acento para atenção (`accent`).
- **Tokens Extras (`ExtraColors`):** `success`, `accent`, `focus`, `disabledContainer` e `disabledContent`.
- **Tipografia (`Type.kt`):** Hierarquia completa com fonte **Inter** (Display, Title, Subtitle, Body, Label, Button, OTP e Captions).
- **Formas e Espaçamento (`Shape.kt` e `Space.kt`):** Cantos arredondados de 8dp, 12dp e 24dp; grid de múltiplos de 4dp (`s1` a `s8`).

---

## 🔄 Integração Contínua (CI)

O projeto conta com Integração Contínua configurada via **GitHub Actions** (`.github/workflows/android.yml`), executando automaticamente a cada `push` e `pull_request`:
- **Ambiente:** Ubuntu Latest com JDK 21
- **Testes Unitários:** `./gradlew testDebugUnitTest` (17 testes executados)
- **Compilação do App:** `./gradlew assembleDebug`

---

## 🚀 Como Executar o Projeto

1. Abra o projeto no **Android Studio**.
2. Sincronize o projeto com os arquivos do Gradle.
3. Execute no emulador ou dispositivo físico (Android 7.0+ / API 24+).

Para compilar e testar via linha de comando:
```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

---

## 📄 Licença

Projeto acadêmico e de desenvolvimento didático.
