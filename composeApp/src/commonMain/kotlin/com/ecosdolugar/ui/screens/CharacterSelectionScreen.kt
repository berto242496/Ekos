package com.ecosdolugar.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.ecosdolugar.ui.components.CharacterAvatarImage
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.data.CharactersRepository
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.characterSelectionStrings
import com.ecosdolugar.model.CharacterItem
import com.ecosdolugar.model.Scoring
import com.ecosdolugar.ui.theme.*

@Composable
fun CharacterSelectionScreen(
    currentCharacterId: String?,
    language: AppLanguage,
    onSelectCharacter: (CharacterItem) -> Unit,
    onConfirm: (CharacterItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedChar by remember(currentCharacterId) {
        mutableStateOf(CharactersRepository.getById(currentCharacterId ?: Scoring.CHARACTER_MEIGA))
    }
    val strings = characterSelectionStrings(language, selectedChar.id)
    val scrollState = rememberScrollState()
    val activeColor = Color(selectedChar.accentColorHex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(activeColor.copy(alpha = 0.35f), PineDeep, PineDeep)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
        ) {
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.backLabel,
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = strings.eyebrow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = TerracottaFixedDim,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = strings.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CharacterAvatarImage(selectedChar.avatarImage, Modifier.padding(2.dp))
                }
            }

            Text(
                text = strings.introduction,
                fontSize = 13.sp,
                color = SurfaceContainerHighest.copy(alpha = 0.9f),
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Text(
                text = strings.selectorLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TerracottaFixedDim,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
            )

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
                            CharacterAvatarImage(charItem.avatarImage, Modifier.padding(2.dp))
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
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

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
                            CharacterAvatarImage(selectedChar.avatarImage, Modifier.padding(2.dp))
                        }
                        Text(
                            text = selectedChar.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = strings.description,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = SurfaceCream
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = activeColor.copy(alpha = 0.1f),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                listOf(activeColor.copy(alpha = 0.3f), activeColor.copy(alpha = 0.1f))
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = strings.powerLabel.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = activeColor,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = strings.powerDescription,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { onConfirm(selectedChar) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor,
                    contentColor = Color(0xFF0F1B12)
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${strings.confirmPrefix} ${selectedChar.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = strings.changeHint,
                fontSize = 11.sp,
                color = SurfaceContainerHighest.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
            )
        }
    }
}
