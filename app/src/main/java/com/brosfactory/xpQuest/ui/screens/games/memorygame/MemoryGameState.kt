package com.brosfactory.xpQuest.ui.screens.games.memorygame


data class MemoryCard(
    val id: Int,
    val imageResId: Int,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

data class MemoryGameState(
    val cards: List<MemoryCard> = emptyList(),
    val lives: Int = 20,
    val matchesFound: Int = 0,
    val isGameOver: Boolean = false,
    val isGameWon: Boolean = false,
    val isFlipping: Boolean = false // Prevents clicking other cards while 2 wrong cards are showing
)