package com.twentyab.tracker.ui.sessions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twentyab.tracker.data.model.SessionSummary
import com.twentyab.tracker.util.Formatters

@Composable
fun SessionsScreen(
    viewModel: SessionsViewModel,
    onCreateSession: () -> Unit,
    onShowStatistics: () -> Unit,
    onSessionSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsState()
    SessionsContent(
        sessions = sessions,
        onCreateSession = onCreateSession,
        onShowStatistics = onShowStatistics,
        onSessionSelected = onSessionSelected,
        modifier = modifier
    )
}

@Composable
private fun SessionsContent(
    sessions: List<SessionSummary>,
    onCreateSession: () -> Unit,
    onShowStatistics: () -> Unit,
    onSessionSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Stammtisch App",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                CardStackIllustration(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1.2f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HomeActionButton(
                        icon = Icons.Filled.Favorite,
                        label = "Neuer Stammtisch",
                        onClick = onCreateSession
                    )
                    HomeActionButton(
                        icon = Icons.Filled.AttachMoney,
                        label = "Statistik",
                        onClick = onShowStatistics
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Divider()
            }
        }
        if (sessions.isEmpty()) {
            item {
                Text(
                    text = "Noch keine Stammtische angelegt. Starte mit einem neuen Stammtisch!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            item {
                Text(
                    text = "Letzte Stammtische",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(sessions, key = { it.id }) { session ->
                SessionCard(
                    session = session,
                    onClick = { onSessionSelected(session.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SessionCard(session: SessionSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = Formatters.formatDate(session.date),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sitzreihenfolge: " +
                    session.seatOrder.sortedBy { it.seatIndex }.joinToString(" ") { it.name },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Spiele: ${session.gameCount}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun HomeActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun CardStackIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cardWidth = size.width * 0.55f
        val cardHeight = cardWidth * 1.4f
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val spacing = cardWidth * 0.2f
        val backdropOffsetY = cardHeight * 0.1f
        val cardColor = MaterialTheme.colorScheme.surface
        val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        val accent = MaterialTheme.colorScheme.primary
        val secondaryAccent = MaterialTheme.colorScheme.tertiary
        val borderWidth = cardWidth * 0.02f

        fun drawCard(offsetX: Float, rotation: Float, alpha: Float = 1f) {
            withTransform({
                translate(left = centerX - cardWidth / 2 + offsetX, top = centerY - cardHeight / 2 + backdropOffsetY)
                rotate(degrees = rotation, pivot = Offset(cardWidth / 2, cardHeight / 2))
            }) {
                drawRoundRect(
                    color = cardColor.copy(alpha = alpha),
                    size = Size(cardWidth, cardHeight),
                    cornerRadius = CornerRadius(cardWidth * 0.08f, cardWidth * 0.08f)
                )
                drawRoundRect(
                    color = borderColor.copy(alpha = alpha),
                    size = Size(cardWidth, cardHeight),
                    cornerRadius = CornerRadius(cardWidth * 0.08f, cardWidth * 0.08f),
                    style = Stroke(width = borderWidth)
                )
            }
        }

        drawCard(offsetX = -spacing, rotation = -12f, alpha = 0.7f)
        drawCard(offsetX = spacing, rotation = 12f, alpha = 0.7f)

        withTransform({
            translate(left = centerX - cardWidth / 2, top = centerY - cardHeight / 2)
        }) {
            drawRoundRect(
                color = cardColor,
                size = Size(cardWidth, cardHeight),
                cornerRadius = CornerRadius(cardWidth * 0.08f, cardWidth * 0.08f)
            )
            drawRoundRect(
                color = borderColor,
                size = Size(cardWidth, cardHeight),
                cornerRadius = CornerRadius(cardWidth * 0.08f, cardWidth * 0.08f),
                style = Stroke(width = borderWidth)
            )

            val figureWidth = cardWidth * 0.35f
            val figureHeight = cardHeight * 0.45f
            val figureTop = cardHeight * 0.28f

            drawRoundRect(
                color = secondaryAccent,
                topLeft = Offset((cardWidth - figureWidth) / 2f, figureTop),
                size = Size(figureWidth, figureHeight),
                cornerRadius = CornerRadius(cardWidth * 0.04f, cardWidth * 0.04f)
            )

            drawRoundRect(
                color = accent,
                topLeft = Offset((cardWidth - figureWidth * 0.9f) / 2f, figureTop + figureHeight * 0.55f),
                size = Size(figureWidth * 0.9f, figureHeight * 0.35f),
                cornerRadius = CornerRadius(cardWidth * 0.03f, cardWidth * 0.03f)
            )

            drawCircle(
                color = accent,
                radius = figureWidth * 0.3f,
                center = Offset(cardWidth / 2f, figureTop - figureWidth * 0.15f)
            )

            val heartSize = cardWidth * 0.12f
            val heartTopOffset = cardHeight * 0.12f
            drawHeart(
                color = MaterialTheme.colorScheme.error,
                topLeft = Offset(cardWidth * 0.18f, heartTopOffset),
                size = Size(heartSize, heartSize)
            )
            drawHeart(
                color = MaterialTheme.colorScheme.error,
                topLeft = Offset(cardWidth * 0.7f - heartSize, cardHeight - heartTopOffset - heartSize),
                size = Size(heartSize, heartSize)
            )

            val acornSize = Size(cardWidth * 0.12f, cardWidth * 0.16f)
            val acornTopOffset = cardHeight * 0.18f
            drawAcorn(
                color = MaterialTheme.colorScheme.primary,
                topLeft = Offset(cardWidth * 0.66f, acornTopOffset),
                size = acornSize
            )
            drawAcorn(
                color = MaterialTheme.colorScheme.primary,
                topLeft = Offset(cardWidth * 0.22f - acornSize.width, cardHeight - acornTopOffset - acornSize.height),
                size = acornSize
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeart(
    color: Color,
    topLeft: Offset,
    size: Size
) {
    val path = Path().apply {
        val width = size.width
        val height = size.height
        moveTo(topLeft.x + width / 2f, topLeft.y + height)
        cubicTo(
            topLeft.x + width * 1.1f,
            topLeft.y + height * 0.75f,
            topLeft.x + width * 0.9f,
            topLeft.y + height * 0.2f,
            topLeft.x + width / 2f,
            topLeft.y + height * 0.35f
        )
        cubicTo(
            topLeft.x + width * 0.1f,
            topLeft.y + height * 0.2f,
            topLeft.x - width * 0.1f,
            topLeft.y + height * 0.75f,
            topLeft.x + width / 2f,
            topLeft.y + height
        )
        close()
    }
    drawPath(path = path, color = color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAcorn(
    color: Color,
    topLeft: Offset,
    size: Size
) {
    val capHeight = size.height * 0.35f
    val bodyHeight = size.height - capHeight
    drawRoundRect(
        color = color.copy(alpha = 0.85f),
        topLeft = topLeft,
        size = Size(size.width, capHeight),
        cornerRadius = CornerRadius(size.width * 0.5f, size.width * 0.5f)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(topLeft.x + size.width * 0.2f, topLeft.y + capHeight * 0.6f),
        size = Size(size.width * 0.6f, bodyHeight),
        cornerRadius = CornerRadius(size.width * 0.3f, size.width * 0.3f)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(topLeft.x + size.width * 0.45f, topLeft.y + capHeight + bodyHeight * 0.6f),
        size = Size(size.width * 0.1f, bodyHeight * 0.6f),
        cornerRadius = CornerRadius(0f, 0f)
    )
}
