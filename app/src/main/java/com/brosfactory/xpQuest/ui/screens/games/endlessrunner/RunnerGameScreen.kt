package com.brosfactory.xpQuest.ui.screens.games.endlessrunner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brosfactory.xpQuest.R
import kotlin.random.Random

@Composable
fun RunnerGameScreen(
    onNavigateHome: () -> Unit // <-- Added navigation callback
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Unified Coordinate System
    val groundLevel = screenHeightPx * 0.70f // Ground line
    val gravity = 2.4f
    val jumpStrength = -40f
    val obstacleSpeed = 18f // Sped up slightly for better pacing

    val dinoSize = 150f
    val obstacleSize = 150f
    val dinoX = screenWidthPx * 0.15f
    val groundedDinoY = groundLevel - dinoSize
    val hitboxPadding = 26f // Forgiving padding

    // Sprites
    val dinoFrames = listOf(
        ImageBitmap.imageResource(id = R.drawable.kuro_run001),
        ImageBitmap.imageResource(id = R.drawable.kuro_run002),
        ImageBitmap.imageResource(id = R.drawable.kuro_run003),
        ImageBitmap.imageResource(id = R.drawable.kuro_run004),
        ImageBitmap.imageResource(id = R.drawable.kuro_run005),
        ImageBitmap.imageResource(id = R.drawable.kuro_run006)
    )
    val cactusImage = ImageBitmap.imageResource(id = R.drawable.cactus)

    // State
    var isPlaying by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    var dinoY by remember { mutableFloatStateOf(groundedDinoY) }
    var verticalVelocity by remember { mutableFloatStateOf(0f) }
    var obstacleX by remember { mutableFloatStateOf(screenWidthPx + 200f) }
    var currentFrameIndex by remember { mutableIntStateOf(0) }

    // Game Loop
    LaunchedEffect(isPlaying, isGameOver) {
        if (isPlaying && !isGameOver) {
            var animationTimer = 0f

            while (true) {
                withFrameNanos { _ ->
                    // Physics
                    verticalVelocity += gravity
                    dinoY += verticalVelocity

                    // Ground Collision
                    if (dinoY >= groundedDinoY) {
                        dinoY = groundedDinoY
                        verticalVelocity = 0f
                    }

                    // Obstacle Movement
                    obstacleX -= obstacleSpeed
                    if (obstacleX < -obstacleSize) {
                        obstacleX = screenWidthPx + Random.nextInt(100, 500).toFloat()// Randomize spawn slightly
                        score++
                    }

                    // Accurate AABB Collision Detection
                    val obstacleY = groundLevel - obstacleSize

                    val dinoRight = dinoX + dinoSize - hitboxPadding
                    val dinoLeft = dinoX + hitboxPadding
                    val dinoBottom = dinoY + dinoSize - hitboxPadding
                    val dinoTop = dinoY + hitboxPadding

                    val obsRight = obstacleX + obstacleSize - hitboxPadding
                    val obsLeft = obstacleX + hitboxPadding
                    val obsBottom = obstacleY + obstacleSize - hitboxPadding
                    val obsTop = obstacleY + hitboxPadding

                    val isColliding = dinoRight > obsLeft &&
                            dinoLeft < obsRight &&
                            dinoBottom > obsTop &&
                            dinoTop < obsBottom

                    if (isColliding) {
                        isGameOver = true
                        isPlaying = false
                    }

                    // Animation
                    if (dinoY == groundedDinoY) {
                        animationTimer += 16f
                        if (animationTimer > 60f) {
                            currentFrameIndex = (currentFrameIndex + 1) % dinoFrames.size
                            animationTimer = 0f
                        }
                    }
                }
            }
        }
    }

    // Input & UI Layer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isGameOver) {
                    isGameOver = false
                    isPlaying = true
                    score = 0
                    obstacleX = screenWidthPx + 200f
                    dinoY = groundedDinoY
                    verticalVelocity = 0f
                } else if (!isPlaying) {
                    isPlaying = true
                } else if (dinoY >= groundedDinoY - 5f) { // Jump allowance
                    verticalVelocity = jumpStrength
                }
            }
    ) {
        // --- 1. The Rendering Canvas (Background & Game Objects) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Sky Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF87CEEB), Color(0xFFE0F6FF)),
                    endY = groundLevel
                ),
                size = Size(size.width, groundLevel)
            )

            // Draw Solid Ground
            drawRect(
                color = Color(0xFF8B5A2B), // Dirt brown
                topLeft = Offset(0f, groundLevel),
                size = Size(size.width, size.height - groundLevel)
            )

            // Draw Ground Top Line (Grass)
            drawLine(
                color = Color(0xFF4CAF50),
                start = Offset(0f, groundLevel),
                end = Offset(size.width, groundLevel),
                strokeWidth = 12f
            )

            // Draw Obstacle
            val obstacleY = groundLevel - obstacleSize
            drawImage(
                image = cactusImage,
                dstOffset = IntOffset(obstacleX.toInt(), obstacleY.toInt()),
                dstSize = IntSize(obstacleSize.toInt(), obstacleSize.toInt())
            )

            // Draw Kuro
            val currentDinoImage = if (dinoY < groundedDinoY) dinoFrames[0] else dinoFrames[currentFrameIndex]
            drawImage(
                image = currentDinoImage,
                dstOffset = IntOffset(dinoX.toInt(), dinoY.toInt()),
                dstSize = IntSize(dinoSize.toInt(), dinoSize.toInt())
            )
        }

        // --- 2. The UI Overlay ---

        // Home Button
        IconButton(
            onClick = onNavigateHome,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 30.dp)
                .align(Alignment.TopStart)
                .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Exit Game",
                tint = Color.DarkGray                                  
            )
        }

        // Score Pill
        Surface(
            color = Color.White.copy(alpha = 0.8f),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 30.dp)
                .align(Alignment.TopEnd)
        ) {
            Text(
                text = "Score: $score",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }

        // Pre-game / Game Over Menus
        if (!isPlaying) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = if (isGameOver) "GAME OVER" else "READY?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isGameOver) Color(0xFFD32F2F) else Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap anywhere to " + if (isGameOver) "restart" else "start",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}