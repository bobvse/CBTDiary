package com.example.cbtdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cbtdiary.domain.model.Emotion
import com.example.cbtdiary.domain.model.EmotionCategory
import com.example.cbtdiary.domain.model.Emotions
import com.example.cbtdiary.ui.theme.CBTDiaryTheme

@Composable
fun EmotionSelector(
    selectedEmotions: List<String>,
    onEmotionToggle: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EmotionCategory.entries.forEach { category ->
            EmotionCategorySection(
                category = category,
                selectedEmotions = selectedEmotions,
                onEmotionToggle = onEmotionToggle
            )
        }
    }
}

@Composable
fun EmotionCategorySection(
    category: EmotionCategory,
    selectedEmotions: List<String>,
    onEmotionToggle: (String) -> Unit
) {
    val categoryEmotions = Emotions.getEmotionsByCategory(category)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(category.color).copy(alpha = 0.1f))
            .padding(12.dp)
    ) {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.titleSmall,
            color = Color(category.color),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        SimpleFlowRow(
            horizontalSpacing = 8.dp,
            verticalSpacing = 8.dp
        ) {
            categoryEmotions.forEach { emotion ->
                EmotionChip(
                    emotion = emotion,
                    isSelected = selectedEmotions.contains(emotion.name),
                    onClick = { onEmotionToggle(emotion.name) }
                )
            }
        }
    }
}

@Composable
fun SimpleFlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        
        var x = 0
        var y = 0
        var maxHeight = 0
        val rows = mutableListOf<MutableList<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        
        placeables.forEach { placeable ->
            if (x + placeable.width > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                x = 0
                y += maxHeight + verticalSpacing.roundToPx()
                maxHeight = 0
            }
            currentRow.add(placeable)
            x += placeable.width + horizontalSpacing.roundToPx()
            maxHeight = maxOf(maxHeight, placeable.height)
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }
        
        val width = constraints.maxWidth
        val height = rows.sumOf { row ->
            row.maxOfOrNull { it.height } ?: 0
        } + (rows.size - 1).coerceAtLeast(0) * verticalSpacing.roundToPx()
        
        layout(width, height) {
            var currentY = 0
            rows.forEach { row ->
                var currentX = 0
                val rowHeight = row.maxOfOrNull { it.height } ?: 0
                row.forEach { placeable ->
                    placeable.placeRelative(currentX, currentY)
                    currentX += placeable.width + horizontalSpacing.roundToPx()
                }
                currentY += rowHeight + verticalSpacing.roundToPx()
            }
        }
    }
}

@Composable
fun EmotionChip(
    emotion: Emotion,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color(emotion.category.color)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val textColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val borderColor = if (isSelected) {
        Color(emotion.category.color)
    } else {
        Color(emotion.category.color).copy(alpha = 0.5f)
    }
    
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = emotion.name,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true, name = "Emotion Selector")
@Composable
fun EmotionSelectorPreview() {
    CBTDiaryTheme {
        var selectedEmotions by remember { 
            mutableStateOf(listOf("ОБИДА", "ТРЕВОГА", "РАЗДРАЖЕНИЕ", "СЧАСТЬЕ", "ДОВЕРИЕ"))
        }
        
        EmotionSelector(
            selectedEmotions = selectedEmotions,
            onEmotionToggle = { emotion ->
                selectedEmotions = if (selectedEmotions.contains(emotion)) {
                    selectedEmotions - emotion
                } else {
                    selectedEmotions + emotion
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Emotion Category Section - Anger")
@Composable
fun EmotionCategorySectionPreviewAnger() {
    CBTDiaryTheme {
        var selectedEmotions by remember { 
            mutableStateOf(listOf("ОБИДА", "РАЗДРАЖЕНИЕ"))
        }
        
        EmotionCategorySection(
            category = EmotionCategory.ANGER,
            selectedEmotions = selectedEmotions,
            onEmotionToggle = { emotion ->
                selectedEmotions = if (selectedEmotions.contains(emotion)) {
                    selectedEmotions - emotion
                } else {
                    selectedEmotions + emotion
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Emotion Category Section - Joy")
@Composable
fun EmotionCategorySectionPreviewJoy() {
    CBTDiaryTheme {
        var selectedEmotions by remember { 
            mutableStateOf(listOf("СЧАСТЬЕ", "ВОСТОРГ"))
        }
        
        EmotionCategorySection(
            category = EmotionCategory.JOY,
            selectedEmotions = selectedEmotions,
            onEmotionToggle = { emotion ->
                selectedEmotions = if (selectedEmotions.contains(emotion)) {
                    selectedEmotions - emotion
                } else {
                    selectedEmotions + emotion
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Emotion Chip - Selected")
@Composable
fun EmotionChipPreviewSelected() {
    CBTDiaryTheme {
        val emotion = Emotion("ОБИДА", EmotionCategory.ANGER)
        EmotionChip(
            emotion = emotion,
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Emotion Chip - Unselected")
@Composable
fun EmotionChipPreviewUnselected() {
    CBTDiaryTheme {
        val emotion = Emotion("СЧАСТЬЕ", EmotionCategory.JOY)
        EmotionChip(
            emotion = emotion,
            isSelected = false,
            onClick = {}
        )
    }
}
