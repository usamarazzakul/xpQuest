package com.brosfactory.xpQuest.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()

    // If data isn't loaded yet, show a spinner
    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Shows the spinning circle
        }
        return
    }

    val user = userProfile!!

    // ... the rest of your UI code ...

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- 1. Top Bar: Player Stats ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = user.profileUrl.ifEmpty { "https://ui-avatars.com/api/?name=${user.username}" },
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(50.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Welcome back,", style = MaterialTheme.typography.bodySmall)
                    Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }

            // Coins & XP Badge
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.MonetizationOn, contentDescription = "Coins", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(" ${user.coin}  | ", fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Star, contentDescription = "XP", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                    Text(" ${user.xp}", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. Recently Played Games ---
        Text("Recently Played", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(2) { index ->
                val gameName = if (index == 0) "2D Platformer" else "Endless Runner"
                Card(modifier = Modifier.width(140.dp).height(100.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(gameName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. Last Played Quiz ---
        Text("Jump Back In", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth().height(80.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
            Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Quiz Category", style = MaterialTheme.typography.bodySmall)
                    Text("Cybersecurity Basics", fontWeight = FontWeight.Bold)
                }
                Button(onClick = { /* TODO: Launch Quiz */ }) {
                    Text("Play")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. Invite & Earn Card ---
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Invite Friends, Earn Coins!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Share your code and you both get +50 coins.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(
                    value = user.referralCode,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Copy to clipboard */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share Code")
                        }
                    }
                )
            }
        }
    }
}