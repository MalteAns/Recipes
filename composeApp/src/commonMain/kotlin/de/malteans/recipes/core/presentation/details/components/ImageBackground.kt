package de.malteans.recipes.core.presentation.details.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import de.malteans.recipes.core.data.network.ApiConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.app_icon
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageBackground(
    imageUrl: String?,
    onBackClick: () -> Unit,
    rightIcons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Boolean, Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // State to hold the intrinsic size of the loaded image.
    var imageIntrinsicSize by remember { mutableStateOf(Size.Unspecified) }

    // Load the image asynchronously.
    var imageLoadResult by remember { mutableStateOf<Result<Painter>?>(null) }
    val context = LocalPlatformContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .httpHeaders(NetworkHeaders.Builder().set("Authorization", "Bearer ${ApiConfig.apiToken}").build())
            .build(),
        onSuccess = {
            val size = it.painter.intrinsicSize
            imageIntrinsicSize = size
            imageLoadResult = if (size.width > 1 && size.height > 1) {
                Result.success(it.painter)
            } else {
                Result.failure(Exception("Invalid image dimensions"))
            }
        },
        onError = {
            it.result.throwable.printStackTrace()
        }
    )
    LaunchedEffect(imageUrl) {
        if (imageUrl != null && imageLoadResult == null) {
            painter.restart()
        }
    }

    // Compute minSheetFraction:
    // If an image is loaded, calculate as:
    // (containerHeight - (displayedImageHeight - 32.dp)) / containerHeight.
    // Otherwise, use 0.85f.
    var minSheetFraction by remember { mutableStateOf(0.85f) }

    // Animatable for the sheet height fraction.
    val sheetHeightAnimatable = remember { Animatable(0f) }

    fun customBackHandling() {
        scope.launch {
            sheetHeightAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250, easing = EaseInOut)
            )
            onBackClick()
        }
    }
    BackHandler { customBackHandling() }

    val navigationProgress by animateFloatAsState(
        targetValue = 300f,
        animationSpec = tween(durationMillis = 300)
    )
    var initialScrollFinished by remember { mutableStateOf(false) }

    // Startup animation: animate from 0f to the initial minSheetFraction over 800ms.
    LaunchedEffect(Unit) {
        delay((300 - navigationProgress.toLong()).milliseconds)
        sheetHeightAnimatable.animateTo(
            targetValue = minSheetFraction,
            animationSpec = tween(durationMillis = 800, easing = EaseInOut)
        )
        initialScrollFinished = true
    }

    // Recalculate minSheetFraction when image load result or container size changes.
    // It uses the container width and height to calculate the displayed image height.
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(imageLoadResult, containerSize) {
        val newMin = if (imageLoadResult?.isSuccess == true &&
            containerSize.height > 0 &&
            imageIntrinsicSize != Size.Unspecified &&
            imageIntrinsicSize.width > 0f
        ) {
            val extraPaddingPx = with(density) { 32.dp.toPx() }
            // Compute displayed image height:
            val displayedImageHeightPx = containerSize.width * (imageIntrinsicSize.height / imageIntrinsicSize.width)
            // Calculate the fraction based on container height.
            ((containerSize.height - (displayedImageHeightPx - extraPaddingPx)) / containerSize.height).coerceIn(0f, 1f)
        } else {
            0.85f
        }
        minSheetFraction = newMin
        // If the sheet is still at 0.85f (i.e. user hasn’t scrolled) and now an image is loaded, animate to 0.7f.
        if ((sheetHeightAnimatable.value == 0.85f) || !initialScrollFinished) {
            delay((300 - navigationProgress.toLong()).milliseconds)
            sheetHeightAnimatable.animateTo(
                targetValue = if (newMin < 0.7f) 0.7f else newMin,
                animationSpec = tween(durationMillis = 800, easing = EaseOut)
            )
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { containerSize = it }
            .background(Color.Transparent)
            .fillMaxSize()
    ) {
        // Background Image
        AnimatedVisibility(
            visible = imageUrl != null,
            enter = fadeIn()
        ) {
            Image(
                painter = if (imageLoadResult?.isSuccess == true) painter
                else painterResource(Res.drawable.app_icon),
                contentDescription = "Background Image",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .blur(30.dp)
            )

            val animatedScale by animateFloatAsState(
                targetValue = if(imageLoadResult?.isSuccess == true) 0.2f else 0f,
                animationSpec = tween(durationMillis = 600, easing = EaseOutBack)
            )

            // Top Image
            Image(
                painter = if (imageLoadResult?.isSuccess == true) painter
                    else painterResource(Res.drawable.app_icon),
                contentDescription = "Top Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .graphicsLayer {
                        val scale = if (imageLoadResult?.isSuccess == true) 0.8f + animatedScale else 1f
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }

        // Draggable Bottom Sheet Surface.
        // Its height is set to sheetHeightAnimatable.value * full screen height.
        val screenHeightPx = containerSize.height
        val draggableState = rememberDraggableState { delta ->
            if (screenHeightPx > 0) {
                val fractionDelta = delta / screenHeightPx.toFloat()
                // Adjust the fraction based on drag delta.
                val newValue = (sheetHeightAnimatable.value - fractionDelta).coerceIn(minSheetFraction, 0.9f)
                scope.launch {
                    sheetHeightAnimatable.snapTo(newValue)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(sheetHeightAnimatable.value)
                .align(Alignment.BottomCenter)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Vertical
                )
        ) {
            // Enable vertical scrolling of the content only when the sheet is fully expanded.
            val isFullyExpanded = sheetHeightAnimatable.value > 0.89f
            content(isFullyExpanded, (sheetHeightAnimatable.value - minSheetFraction) / (0.9f - minSheetFraction))
        }

        // Back button in the top-left corner
        // on click, animate the sheet height to 0 (with 250ms tween) then call onBackClick.
        IconButton(
            onClick = { customBackHandling() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go Back",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Edit and Delete buttons in the top-right corner.

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rightIcons()
        }
    }
}