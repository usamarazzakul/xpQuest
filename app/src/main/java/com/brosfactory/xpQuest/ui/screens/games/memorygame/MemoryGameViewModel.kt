package com.brosfactory.xpQuest.features.games.memorygame


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brosfactory.xpQuest.R // Replace with your actual R package
import com.brosfactory.xpQuest.ui.screens.games.memorygame.MemoryCard
import com.brosfactory.xpQuest.ui.screens.games.memorygame.MemoryGameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MemoryGameViewModel : ViewModel() {

    private val _state = MutableStateFlow(MemoryGameState())
    val state = _state.asStateFlow()

    private var firstSelectedCardIndex: Int? = null

    init {
        initializeGame()
    }

    fun initializeGame() {
        // You need 10 images in your drawable folder for this to work!
        val images = listOf(
            R.drawable.card_cat, R.drawable.card_bear, R.drawable.card_duck, R.drawable.card_panda, R.drawable.card_dolphin,
            R.drawable.card_elephant, R.drawable.card_giraffe, R.drawable.card_monkey, R.drawable.card_sheep, R.drawable.card_tortoise
        )

        // Duplicate the images to create pairs, then shuffle them
        val pairs = (images + images).shuffled()
        val cards = pairs.mapIndexed { index, imageRes ->
            MemoryCard(id = index, imageResId = imageRes)
        }

        _state.value = MemoryGameState(cards = cards, lives = 20)
        firstSelectedCardIndex = null
    }

    fun onCardClicked(index: Int) {
        val currentState = _state.value
        val clickedCard = currentState.cards[index]

        // Ignore clicks if game is over, cards are flipping, or card is already revealed
        if (currentState.isGameOver || currentState.isGameWon || currentState.isFlipping || clickedCard.isFaceUp || clickedCard.isMatched) {
            return
        }

        // Flip the clicked card face up
        val updatedCards = currentState.cards.toMutableList()
        updatedCards[index] = clickedCard.copy(isFaceUp = true)
        _state.update { it.copy(cards = updatedCards) }

        if (firstSelectedCardIndex == null) {
            // This is the first card clicked
            firstSelectedCardIndex = index
        } else {
            // This is the second card clicked. Check for a match!
            checkMatch(firstSelectedCardIndex!!, index)
            firstSelectedCardIndex = null
        }
    }

    private fun checkMatch(index1: Int, index2: Int) {
        viewModelScope.launch {
            val currentState = _state.value
            val card1 = currentState.cards[index1]
            val card2 = currentState.cards[index2]

            if (card1.imageResId == card2.imageResId) {
                // MATCH FOUND!
                val updatedCards = currentState.cards.toMutableList()
                updatedCards[index1] = card1.copy(isMatched = true)
                updatedCards[index2] = card2.copy(isMatched = true)

                val matches = currentState.matchesFound + 1
                val won = matches == 10 // 10 pairs total

                _state.update {
                    it.copy(cards = updatedCards, matchesFound = matches, isGameWon = won)
                }
            } else {
                // WRONG ANSWER! Lock the board briefly, then flip back and deduct life.
                _state.update { it.copy(isFlipping = true) }

                delay(1000) // Wait 1 second so the user can see the wrong cards

                val freshState = _state.value
                val updatedCards = freshState.cards.toMutableList()
                updatedCards[index1] = updatedCards[index1].copy(isFaceUp = false)
                updatedCards[index2] = updatedCards[index2].copy(isFaceUp = false)

                val remainingLives = freshState.lives - 1
                val lost = remainingLives <= 0

                _state.update {
                    it.copy(
                        cards = updatedCards,
                        lives = remainingLives,
                        isGameOver = lost,
                        isFlipping = false
                    )
                }
            }
        }
    }
}