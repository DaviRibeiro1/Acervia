# Acervia — Aplicativo de Reserva de Livros

O **Acervia** é um aplicativo Android nativo para reservas de livros de biblioteca, desenvolvido em **Kotlin** com **Jetpack Compose**, **Material 3** e **Jetpack Navigation**, seguindo rigorosamente as diretrizes do **Acervia Design System v0.1**.

## 📖 Sobre o Projeto

O Acervia simplifica o processo de descoberta e reserva de livros em bibliotecas. O usuário pode consultar o acervo, realizar reservas e visualizar seu código de retirada de forma rápida e offline no balcão.

### Fluxo de Entrada e Telas
- **Splash Screen:** Transição suave verificando sessão ativa.
- **Welcome:** Apresentação exibida no primeiro acesso.
- **Login:** Entrada única por Google Sign-In ou E-mail com código de acesso (OTP de 6 dígitos).
- **Falta Pouco:** Cadastro e confirmação de perfil para contas novas (Nome, Sobrenome, Data de Nascimento e Termos).
- **Home:** Painel principal com reservas ativas, código de retirada e destaques do acervo.
- **Catálogo, Detalhes e Reservas:** Busca, navegação por livros, detalhes de exemplar e gerenciamento de reservas.
- **Ajustes:** Idioma, aparência (Claro/Escuro/Sistema) e informações da conta.

---

## 🎨 Design System v0.1

O aplicativo utiliza o **Acervia Design System v0.1**, provido via `ui.theme`:

- **Cores & Tokens (`Color.kt`):** Azul-marinho de marca (`primary`: `#1E3A5F` no claro / `#9DB8E6` no escuro), neutros e acento para atenção (`accent`).
- **Tokens Extras (`ExtraColors`):** `success`, `accent`, `focus`, `disabledContainer` e `disabledContent`.
- **Tipografia (`Type.kt`):** Hierarquia completa com fonte **Inter** (Display, Title, Subtitle, Body, Label, Button, OTP e Captions).
- **Formas e Espaçamento (`Shape.kt` e `Space.kt`):** Cantos arredondados de 8dp, 12dp e 24dp; grid de múltiplos de 4dp (`s1` a `s8`).

---

## 🛠️ Arquitetura e Tecnologias

- **Linguagem:** Kotlin
- **UI Toolkit:** Jetpack Compose com Material 3
- **Navegação:** Jetpack Navigation Compose
- **Imagens:** Coil Compose
- **Injeção e Dados:** Inversão de dependências com Repositórios Fake/Locais para desenvolvimento e prototipagem offline.

---

## 🔄 Integração Contínua (CI)

O projeto conta com Integração Contínua configurada via **GitHub Actions** (`.github/workflows/android.yml`), executando automaticamente a cada `push` e `pull_request`:
- **Ambiente:** Ubuntu Latest com JDK 21
- **Testes Unitários:** `./gradlew testDebugUnitTest`
- **Compilação do App:** `./gradlew assembleDebug`
- **Relatórios:** Upload automático de relatórios dos testes executados.

---

## 🚀 Como Executar o Projeto

1. Abra o projeto no **Android Studio**.
2. Sincronize o projeto com os arquivos do Gradle.
3. Execute no emulador ou dispositivo físico (Android 7.0+ / API 24+).

Para compilar via linha de comando:
```bash
./gradlew assembleDebug
```

---

## 📄 Licença

Projeto acadêmico e de desenvolvimento didático.
