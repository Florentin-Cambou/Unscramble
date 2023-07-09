// Classe modèle pour l'UI d'état
// L'ui State est un rassemblement des entrées utilisateurs
//en gros c'est le point d'entrée entre l'app et la communication utilisateurs
package com.example.unscramble.ui

data class GameUiState(
    val currentScrambleWord: String = "",
    val isGuessedWordWrong: Boolean = false,
    val score: Int = 0,
    val currentWordCount: Int = 1
)