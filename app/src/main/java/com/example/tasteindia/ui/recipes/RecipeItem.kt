package com.example.tasteindia.ui.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tasteindia.domain.model.Meal
import com.example.tasteindia.ui.theme.SpicePrimaryFixed
import com.example.tasteindia.ui.theme.SpiceSecondary
import com.example.tasteindia.ui.theme.SpiceSurfaceContainerLowest
import com.example.tasteindia.ui.theme.SpiceTertiary

@Composable
fun RecipeItem(
    meal: Meal,
    isFavourite: Boolean,
    onRecipeClick: (String) -> Unit,
    onFavouriteToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isVeg = meal.name.contains("dal", ignoreCase = true) ||
            meal.name.contains("paneer", ignoreCase = true) ||
            meal.name.contains("gobi", ignoreCase = true) ||
            meal.name.contains("veg", ignoreCase = true) ||
            meal.name.contains("chana", ignoreCase = true) ||
            meal.name.contains("aloo", ignoreCase = true)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onRecipeClick(meal.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SpiceSurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail image with Veg/Non-Veg Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(meal.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Veg / Non-Veg Indicator Badge
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SpiceSurfaceContainerLowest)
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isVeg) SpiceSecondary else SpiceTertiary)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Top Meta Line (Area • Time • Rating)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isVeg) "Vegetarian" else "Indian Cuisine",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isVeg) SpiceSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " • 35m • ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "4.9",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Title
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Tag Chips
                Row {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isVeg) "Classic" else "Curry",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(SpicePrimaryFixed)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Authentic",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Favourite Heart Button
            IconButton(
                onClick = { onFavouriteToggle(meal.id) },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isFavourite) SpicePrimaryFixed else MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavourite) {
                        "Remove ${meal.name} from favourites"
                    } else {
                        "Add ${meal.name} to favourites"
                    },
                    tint = if (isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
