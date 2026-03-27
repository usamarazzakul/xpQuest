package com.brosfactory.xpQuest.features.games.memorygame

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.brosfactory.xpQuest.ui.screens.games.memorygame.MemoryCard

// ================= COLORS =================

val MintGreen = Color(0xFF00E676)
val DarkPurple = Color(0xFF4A148C)
val PurpleAccent = Color(0xFF7C4DFF)
val DangerRed = Color(0xFFFF1744)

val CardGradient = Brush.linearGradient(listOf(PurpleAccent, DarkPurple))
val BackgroundGradient =
    Brush.verticalGradient(listOf(Color(0xFFF4F6F8), Color(0xFFE2E8F0)))

// ================= SCREEN =================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryGameScreen(
    viewModel: MemoryGameViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Memory Quest",
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkPurple
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkPurple
                        )
                    }
                },
                actions = {
                    LivesChip(state.lives)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(12.dp))

                AnimatedScore(state.matchesFound)

                Spacer(Modifier.height(24.dp))

                AnimatedContent(
                    targetState = state.isGameWon || state.isGameOver,
                    transitionSpec = {
                        fadeIn() + scaleIn() togetherWith fadeOut()
                    },
                    label = "GameStateAnimation"
                ) { isGameEnded ->
                    if (isGameEnded) {
                        GameEndState(
                            isWon = state.isGameWon
                        ) { viewModel.initializeGame() }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(state.cards) { index, card ->
                                MemoryCardItem(
                                    card = card,
                                    onClick = { viewModel.onCardClicked(index) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= LIVES CHIP =================

@Composable
private fun LivesChip(lives: Int) {
    val animatedLives by animateIntAsState(lives, label = "LivesAnimation")

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = "Lives remaining",
                tint = DangerRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "$animatedLives",
                fontWeight = FontWeight.Bold,
                color = DarkPurple
            )
        }
    }
}

// ================= SCORE =================

@Composable
private fun AnimatedScore(matches: Int) {
    val animatedMatches by animateIntAsState(matches, label = "ScoreAnimation")

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Text(
            text = "Matches: $animatedMatches / 10",
            fontWeight = FontWeight.SemiBold,
            color = DarkPurple,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

// ================= GAME END =================

@Composable
fun GameEndState(isWon: Boolean, onRestart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = if (isWon) Icons.Default.Star else Icons.Default.Favorite,
                contentDescription = null,
                tint = if (isWon) Color(0xFFFFD700) else DangerRed,
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isWon) "Victory!" else "Game Over",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isWon) MintGreen else DangerRed
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (isWon) "Elite memory detected." else "Reset. Refocus. Retry.",
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkPurple)
            ) {
                Text("Play Again", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ================= CARD =================

@Composable
fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = tween(120),
        label = "CardPressScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        animationSpec = tween(450, easing = FastOutSlowInEasing),
        label = "CardFlipRotation"
    )

    val infinite = rememberInfiniteTransition(label = "FloatingAnimation")
    val floatOffset by infinite.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatingOffset"
    )

    val density = LocalDensity.current.density

    Card(
        modifier = Modifier
            .aspectRatio(0.75f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationY = rotation
                cameraDistance = 16 * density
                translationY = if (!card.isFaceUp && !card.isMatched) floatOffset else 0f
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !card.isMatched && !card.isFaceUp
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (card.isFaceUp) 2.dp else 8.dp
        ),
        border = if (card.isMatched)
            BorderStroke(3.dp, MintGreen)
        else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation > 90f) {
                // Front side (image)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                ) {
                    Image(
                        painter = painterResource(card.imageResId),
                        contentDescription = "Memory Card Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (card.isMatched) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MintGreen.copy(alpha = 0.25f))
                        )
                    }
                }
            } else {
                // Back side (gradient with ?)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CardGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "?",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}