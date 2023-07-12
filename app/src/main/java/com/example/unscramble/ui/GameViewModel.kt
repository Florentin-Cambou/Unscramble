// class qui gère le view model ce qui est affiché en direct à l'utilisateur
package com.example.unscramble.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    // communication Game UI State

    var userGuess by mutableStateOf("")
    private set

    // permet de faire la modification de l'UI
    private val _uiState = MutableStateFlow(GameUiState())

    // propriété de support de _uiState qui permet de faire que de la lecture seul des informations UI et pas de
    // modification
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        if(usedWords.contains(currentWord)){
            return pickRandomWordAndShuffle()
        }else{
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

     fun checkUserGuess() {
        if(userGuess.equals(currentWord,ignoreCase = true)){
            val updateScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updateScore)
        }else{
            //erreur
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }
    fun updateGameState(updateScore: Int) {
        if(usedWords.size == MAX_NO_OF_WORDS) {
            val isGameOver = true
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    isGameOver = true
                )
            }
        }else{
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambleWord = pickRandomWordAndShuffle(),
                    score = updateScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
        Log.i("currentWord","{$usedWords")
    }
    fun skipWord() {
        _uiState.update { currentState ->
            currentState.copy(
                currentScrambleWord = pickRandomWordAndShuffle(),
            )
        }
    }
    fun resetGame() {
        usedWords.clear()

        _uiState.value = GameUiState(currentScrambleWord = pickRandomWordAndShuffle())
    }

    init {
        resetGame()
    }
}