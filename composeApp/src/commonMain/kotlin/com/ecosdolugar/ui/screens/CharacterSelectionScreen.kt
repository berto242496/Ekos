package com.ecosdolugar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.model.CharacterItem
import com.ecosdolugar.ui.theme.*

@Composable
fun CharacterSelectionScreen(
    currentCharacterId: String,
    onSelectCharacter: (CharacterItem) -> Unit,
    onConfirm: (CharacterItem) -> Unit,
    onBack: () -> Unit,
    userEmail: String? = null
) {
    var selectedChar by remember {
        mutableStateOf(CharactersRepository.getById(currentCharacterId))
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PineDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ECOS DA MITOLOXÍA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = TerracottaFixedDim
                    )
                    Text(
                        text = "Elige o teu personaxe",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Decorative badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(selectedChar.avatarEmoji, fontSize = 20.sp)
                }
            }

            // Google account banner if connected
            if (userEmail != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Conta Google activa:",
                            fontSize = 12.sp,
                            color = SurfaceContainerHighest
                        )
                        Text(
                            text = userEmail,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TerracottaFixedDim
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Subtitle intro
            Text(
                text = "Cada personaxe custodia un don especial para descubrir os segredos da Raia e descifrar os enigmas dos devanceiros.",
                fontSize = 13.sp,
                color = SurfaceContainerHighest.copy(alpha = 0.9f),
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Character Carousel Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CharactersRepository.characters) { charItem ->
                    val isSelected = charItem.id == selectedChar.id
                    val charColor = Color(charItem.accentColorHex)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(88.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) Color.White.copy(alpha = 0.15f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) charColor else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedChar = charItem
                                onSelectCharacter(charItem)
                            }
                            .padding(vertical = 12.dp, horizontal = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    if (isSelected) charColor.copy(alpha = 0.25f)
                                    else Color.Black.copy(alpha = 0.3f),
                                    CircleShape
                                )
                                .border(1.dp, if (isSelected) charColor else Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(charItem.avatarEmoji, fontSize = 26.sp)
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = charItem.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else SurfaceContainerHighest,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )

                        Text(
                            text = charItem.archetype,
                            fontSize = 9.sp,
                            color = if (isSelected) charColor else OutlineVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Main Character Detail Card
            val activeColor = Color(selectedChar.accentColorHex)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F2618),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.verticalGradient(
                        listOf(activeColor.copy(alpha = 0.6f), Color.White.copy(alpha = 0.1f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(activeColor.copy(alpha = 0.2f), CircleShape)
                                    .border(1.5.dp, activeColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(selectedChar.avatarEmoji, fontSize = 30.sp)
                            }

                            Column {
                                Text(
                                    text = selectedChar.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = selectedChar.galicianTitle,
                                    fontSize = 12.sp,
                                    color = activeColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = activeColor.copy(alpha = 0.15f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(activeColor.copy(alpha = 0.4f), activeColor.copy(alpha = 0.2f)))
                            )
                        ) {
                            Text(
                                text = selectedChar.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = activeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Lore & Quote
                    Text(
                        text = selectedChar.lore,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = SurfaceCream
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedChar.quote,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = TerracottaFixedDim,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    // Special Ability / Don
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = activeColor.copy(alpha = 0.1f),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(activeColor.copy(alpha = 0.3f), activeColor.copy(alpha = 0.1f)))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("✨", fontSize = 14.sp)
                                Text(
                                    text = "DON DO PERSONAXE: ${selectedChar.abilityName.uppercase()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeColor,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = selectedChar.abilityDescription,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 17.sp
                            )
                        }
                    }

                    // Sacred Item & Element
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.2f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "OBXECTO SAGRADO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OutlineVariant
                                )
                                Text(
                                    text = selectedChar.sacredItem,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.2f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "ELEMENTO VITAL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OutlineVariant
                                )
                                Text(
                                    text = selectedChar.element,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Character Stats
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ATRIBUTOS DO ESPÍRITO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeColor,
                            letterSpacing = 1.sp
                        )

                        selectedChar.stats.forEach { stat ->
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(stat.label, fontSize = 11.sp, color = SurfaceContainerHighest)
                                    Text("${stat.value}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                LinearProgressIndicator(
                                    progress = stat.value / 100f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = activeColor,
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Confirm Selection Button
            Button(
                onClick = { onConfirm(selectedChar) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor,
                    contentColor = Color(0xFF0F1B12)
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Comezar expedición como ${selectedChar.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
