package com.example.tasteindia.ui.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tasteindia.domain.model.Ingredient
import com.example.tasteindia.domain.model.MealDetails
import com.example.tasteindia.ui.theme.SpiceOnPrimaryFixed
import com.example.tasteindia.ui.theme.SpicePrimary
import com.example.tasteindia.ui.theme.SpicePrimaryFixed
import com.example.tasteindia.ui.theme.SpiceSecondary
import com.example.tasteindia.ui.theme.SpiceSecondaryContainer
import com.example.tasteindia.ui.theme.SpiceSecondaryFixedVariant
import com.example.tasteindia.ui.theme.SpiceSurfaceContainerHigh
import com.example.tasteindia.ui.theme.SpiceSurfaceContainerLow
import com.example.tasteindia.ui.theme.SpiceSurfaceContainerLowest
import com.example.tasteindia.ui.theme.SpiceTertiary
import com.example.tasteindia.ui.theme.SpiceTertiaryFixed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: String,
    uiState: RecipeDetailsUiState,
    onLoadDetails: (String) -> Unit,
    onFavouriteToggle: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(recipeId) {
        if (recipeId.isNotBlank()) {
            onLoadDetails(recipeId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Recipe Detail",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "TasteIndia",
                            style = MaterialTheme.typography.labelSmall,
                            color = SpicePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (uiState is RecipeDetailsUiState.Success) {
                        IconButton(
                            onClick = onFavouriteToggle,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SpiceSurfaceContainerLow)
                        ) {
                            Icon(
                                imageVector = if (uiState.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (uiState.isFavourite) {
                                    "Remove ${uiState.details.name} from favourites"
                                } else {
                                    "Add ${uiState.details.name} to favourites"
                                },
                                tint = if (uiState.isFavourite) SpicePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is RecipeDetailsUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = SpicePrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading recipe details...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                is RecipeDetailsUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Unable to load recipe details.",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.message,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { onLoadDetails(recipeId) }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
                is RecipeDetailsUiState.Success -> {
                    DetailContent(
                        details = uiState.details,
                        onOpenUrl = { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                // Gracefully ignore invalid URL handling
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    details: MealDetails,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var servingsCount by remember { mutableIntStateOf(4) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        // Verified Banner & Chef Badge Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(SpiceSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = SpiceSecondaryFixedVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "CHEF VERIFIED RECIPE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SpiceSecondary
                )
            }
        }

        // Hero Media Card with Ambient Overlay & Quick Metrics Ribbon
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SpiceSurfaceContainerHigh)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(details.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = details.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Scrim for Legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Rating Badge (Top Right)
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = SpiceSurfaceContainerLowest.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = SpicePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "4.9",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "(2.4k)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Metrics Ribbon (Bottom)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SpiceSurfaceContainerLowest.copy(alpha = 0.95f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time Metric
                    Column {
                        Text(
                            text = "35 Mins",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cook Time",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(SpiceSurfaceContainerHigh)
                    )

                    // Spice Level Metric
                    Column {
                        Text(
                            text = "Med Spice",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Balanced",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(SpiceSurfaceContainerHigh)
                    )

                    // Servings Metric
                    Column {
                        Text(
                            text = "Serves $servingsCount",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Portion",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Block & Narrative
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (details.category.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SpicePrimaryFixed)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = details.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SpiceOnPrimaryFixed
                        )
                    }
                }
                if (details.area.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SpiceSurfaceContainerHigh)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${details.area} Origin",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Title & Italic Subtitle
            Text(
                text = details.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Hashtag Cloud
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tagList = details.tags.ifEmpty { listOf("Indian", "Authentic", "Curry", "Tandoori") }
                tagList.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SpicePrimaryFixed.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            color = SpicePrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Servings Scaler Widget
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpiceSurfaceContainerLow)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Adjust Servings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Quantities recalibrate live",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Plus / Minus Buttons
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = SpiceSurfaceContainerLowest,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (servingsCount > 1) servingsCount-- },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = servingsCount.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { if (servingsCount < 12) servingsCount++ },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Increase servings",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ingredients Section
        if (details.ingredients.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${details.ingredients.size} items",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ingredients Card Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SpiceSurfaceContainerLowest)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    details.ingredients.forEach { ingredient ->
                        IngredientRow(ingredient = ingredient, servingsCount = servingsCount)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Chef Secret Technique Pro-Tip Callout Card
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpiceTertiaryFixed)
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SpiceTertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chef's Secret Technique",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SpiceTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gently toast dried spices on a lukewarm tawa for 15 seconds before sprinkling into the gravy. This awakens the essential oils for a true restaurant-grade aroma!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Step-by-Step Instructions
        if (details.instructions.isNotBlank()) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Preparation Steps",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                val steps = details.instructions
                    .split(Regex("\r?\n|\r"))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                val displaySteps = if (steps.isEmpty()) listOf(details.instructions) else steps

                displaySteps.forEachIndexed { index, stepText ->
                    PreparationStepCard(stepNumber = index + 1, stepText = stepText)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // External Link Action Buttons (YouTube & Source)
        if (!details.youtubeUrl.isNullOrBlank() || !details.sourceUrl.isNullOrBlank()) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                details.youtubeUrl?.let { youtube ->
                    Button(
                        onClick = { onOpenUrl(youtube) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpicePrimary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Watch Video Tutorial (YouTube)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                details.sourceUrl?.let { source ->
                    OutlinedButton(
                        onClick = { onOpenUrl(source) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Source Recipe & History",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientRow(
    ingredient: Ingredient,
    servingsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SpicePrimary)
            )
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val scaledMeasure = scaleMeasureText(ingredient.measure, servingsCount)
        Text(
            text = scaledMeasure,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = SpicePrimary
        )
    }
}

@Composable
private fun PreparationStepCard(
    stepNumber: Int,
    stepText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpiceSurfaceContainerLowest)
            .padding(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (stepNumber == 1) SpicePrimary else SpicePrimaryFixed),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = if (stepNumber == 1) MaterialTheme.colorScheme.onPrimary else SpiceOnPrimaryFixed
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getStepTitle(stepNumber),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SpicePrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stepText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun scaleMeasureText(rawMeasure: String, servings: Int): String {
    if (rawMeasure.isBlank()) return ""
    val ratio = servings / 4.0
    val regex = Regex("""^(\d+(?:\.\d+)?)\s*(.*)$""")
    val match = regex.find(rawMeasure.trim())
    return if (match != null) {
        val baseVal = match.groupValues[1].toDoubleOrNull()
        val unit = match.groupValues[2]
        if (baseVal != null) {
            val scaled = baseVal * ratio
            val formatted = if (scaled % 1.0 == 0.0) scaled.toInt().toString() else String.format("%.1f", scaled)
            "$formatted $unit".trim()
        } else {
            rawMeasure
        }
    } else {
        rawMeasure
    }
}

private fun getStepTitle(stepNumber: Int): String {
    return when (stepNumber) {
        1 -> "MARINATION & PREP"
        2 -> "CHARRING & SEARING"
        3 -> "MAKHANI GRAVY SIMMER"
        4 -> "ASSEMBLY & FINISH"
        else -> "STEP $stepNumber"
    }
}
