package de.malteans.recipes.core.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.core.presentation.components.CustomDialog
import de.malteans.recipes.core.presentation.components.CustomPlanDialog
import de.malteans.recipes.core.presentation.components.RatingBar
import de.malteans.recipes.core.presentation.details.components.*
import de.malteans.recipes.core.presentation.plan.components.toUiText
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.*
import kotlin.math.roundToInt

@Composable
fun DetailsScreenRoot(
    viewModel: DetailsViewModel = koinViewModel(),
    onBack: (Pair<Long, Long?>?) -> Unit,
    onEdit: (Long) -> Unit,
    onSave: () -> Unit,
    recipeId: Long?,
) {
    if (recipeId != null) viewModel.setRecipeId(recipeId)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.deleteFinished) {
        if (state.deleteFinished) onBack(null)
    }

    DetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DetailsAction.OnBack -> onBack(state.recipe?.let { it.id to it.cloudId })
                is DetailsAction.OnEdit -> onEdit(recipeId ?: state.recipe?.id
                    ?: throw IllegalStateException("recipeId and recipe are null"))
                is DetailsAction.OnSave -> {
                    viewModel.onAction(DetailsAction.OnSave)
                    onSave()
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    onAction: (DetailsAction) -> Unit
) {
    val localClipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    if (state.showPlanDialog && state.recipe != null) {
        CustomPlanDialog(
            onDismiss = { onAction(DetailsAction.DismissPlanDialog) },
            onSubmit = { onAction(DetailsAction.OnPlan(it.date, it.timeOfDay)) },
            initialRecipe = state.recipe,
        )
    }

    val pagerState = rememberPagerState { 2 }
    val ingredientListState = rememberScrollState()
    val preparationListState = rememberScrollState()

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(DetailsAction.OnTabSelected(pagerState.currentPage))
    }

    LaunchedEffect(state.recipe) {
        if (state.recipe != null) {
            ingredientListState.scrollTo(0)
            preparationListState.scrollTo(0)
        }
    }

    // Dialog to disable UI
    AnimatedVisibility(
        visible = state.isDeleting,
        enter = fadeIn(animationSpec = tween(200)),
    ) {
        Dialog(
            onDismissRequest = {  },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            )
        ) { }
    }

    // Dialog for custom servings
    var showServingsDialog by remember { mutableStateOf(false) }
    if (showServingsDialog && state.recipe?.servings != null) {
        var validToFinish by remember { mutableStateOf(false) }
        var servingsString by remember { mutableStateOf(state.customServings?.toString() ?: state.recipe.servings.toString()) }

        LaunchedEffect(servingsString) {
            val servingsInt = servingsString.toIntOrNull()
            validToFinish = servingsInt != null && servingsInt > 0
        }

        CustomDialog(
            onDismissRequest = { showServingsDialog = false },
            titleText = stringResource(Res.string.custom_servings),
            leftIcon = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable { showServingsDialog = false }
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset to default",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable {
                                onAction(DetailsAction.CustomServings(null))
                                showServingsDialog = false
                            }
                    )
                }
            },
            rightIcon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit",
                    tint = if (validToFinish) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier
                        .clickable {
                            if (validToFinish) {
                                onAction(DetailsAction.CustomServings(
                                    servingsString.toInt().takeIf { it != state.recipe.servings }
                                ))
                                showServingsDialog = false
                            }
                        }
                )
            }
        ) {
            Text(
                text = stringResource(Res.string.custom_servings_desc),
                style = MaterialTheme.typography.bodySmall,
            )
            OutlinedTextField(
                value = servingsString,
                onValueChange = { servingsString = it },
                label = { Text(stringResource(Res.string.custom_servings)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (validToFinish) {
                            onAction(DetailsAction.CustomServings(servingsString.toInt()))
                            showServingsDialog = false
                        }
                    }
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
        }
    }

    // Dialog for rating
    var showRatingDialog by remember { mutableStateOf(false) }
    if (showRatingDialog && state.recipe != null) {
        var rating by remember { mutableStateOf<Int?>(state.recipe.rating) }

        CustomDialog(
            onDismissRequest = { showRatingDialog = false },
            titleText = stringResource(Res.string.rating),
            rightIcon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit",
                    tint = if (rating != state.recipe.rating) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier
                        .clickable {
                            if (rating != state.recipe.rating) {
                                onAction(DetailsAction.OnRatingChanged(rating))
                                showRatingDialog = false
                            }
                        }
                )
            }
        ) {
            RatingBar(
                value = rating,
                onChange = { newRating ->
                    rating = newRating
                }
            )
        }
    }

    ImageBackground(
        imageUrl = state.recipe?.imageUrl,
        onBackClick = { onAction(DetailsAction.OnBack) },
        rightIcons = @Composable {
            if (state.recipe?.sourceUrl != null && state.recipe.sourceUrl.isNotBlank() && state.recipe.sourceUrl.startsWith("https://")) {
                IconButton(
                    onClick = { uriHandler.openUri(state.recipe.sourceUrl) },
                ) {
                    Icon(
                        imageVector = CustomOpenInBrowserIcon,
                        contentDescription = "Open Source",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            if (state.recipe?.isCloudOnly() == false) {
                val deleteClicked = remember { mutableStateOf(false) }
                LaunchedEffect(deleteClicked.value) {
                    if (deleteClicked.value) {
                        delay(3000)
                        deleteClicked.value = false
                    }
                }
                IconButton(
                    onClick = { onAction(DetailsAction.OnEdit) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Recipe",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(
                    onClick = {
                        if (!deleteClicked.value) {
                            deleteClicked.value = true
                        } else {
                            onAction(DetailsAction.OnDelete)
                            deleteClicked.value = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Recipe",
                        tint = if (deleteClicked.value) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            AnimatedVisibility(
                visible = state.recipe?.isCloudOnly() == true,
                enter = EnterTransition.None,
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(
                        delayMillis = 2000,
                        durationMillis = 300,
                    )
                ),
            ) {
                IconButton(
                    onClick = { onAction(DetailsAction.OnSave) }
                ) {
                    Icon(
                        imageVector = if (state.recipe?.isCloudOnly() == true) CustomCloudDownloadIcon
                            else CustomDownloadDoneIcon,
                        contentDescription = "Save Recipe locally",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
    ) { enableScrolling, dragProgress ->
        Column(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
        ) {
            state.recipe?.let { recipe ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = recipe.getState().uiText.asString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (recipe.description.isNotBlank()) {
                            Text(
                                text = recipe.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (recipe.sourceUrl != null && recipe.sourceUrl.isNotBlank() && recipe.sourceUrl.startsWith("https://")) {
                            Icon(
                                imageVector = CustomOpenInBrowserIcon,
                                contentDescription = "Open Source",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .combinedClickable (
                                        onClick = { uriHandler.openUri(recipe.sourceUrl) },
                                        onLongClick = { // copy sourceUrl to clipboard
                                            localClipboardManager.setText(
                                                AnnotatedString(recipe.sourceUrl)
                                            )
                                        }
                                    )
                                    .size(28.dp)
                            )
                        }
                        if (recipe.isCloudOnly()) {
                            Icon(
                                imageVector = CustomCloudDownloadIcon,
                                contentDescription = "Save Recipe locally",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable { onAction(DetailsAction.OnSave) }
                                    .size(28.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Recipe",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable { onAction(DetailsAction.OnEdit) }
                                    .size(28.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Plan Recipe",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable { onAction(DetailsAction.ShowPlanDialog) }
                                    .size(28.dp)
                            )
                        }
                    }
                }
                // -------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f + 0.4f * dragProgress)
                        ),
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth(),
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Row {
                                RatingBar(
                                    value = recipe.rating ?: recipe.onlineRating?.times(5)?.roundToInt(),
                                    online = recipe.rating == null,
                                    modifier = Modifier
                                        .then(
                                            if (recipe.rating != null) Modifier
                                            else Modifier.background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                        )
                                        .clickable {
                                            showRatingDialog = true
                                        }
                                )
                                if (recipe.rating == null && recipe.onlineRating != null) {
                                    Text(
                                        text = "(${stringResource(Res.string.online)})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        if (state.recipe.servings != null) {
                                            showServingsDialog = true
                                        }
                                    }
                            ) {
                                Text(
                                    text = "${stringResource(Res.string.servings)}: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = state.customServings?.toString() ?: recipe.servings?.toString() ?: "–",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (state.customServings != null) MaterialTheme.colorScheme.onSecondary
                                        else MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier
                                        .background(
                                            color = if (state.customServings != null) MaterialTheme.colorScheme.secondary
                                                else MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = CustomClockIcon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(42.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(start = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (recipe.workTime != null && recipe.workTime != recipe.totalTime) {
                                    Text(
                                        text = "${recipe.workTime} ${stringResource(Res.string.min)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Text(
                                    text = if (recipe.totalTime != null) "${recipe.totalTime} ${
                                        stringResource(
                                            Res.string.min
                                        )
                                    }"
                                    else "–",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    TabRow(
                        selectedTabIndex = state.selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                            )
                        },
                    ) {
                        Tab(
                            selected = state.selectedTabIndex == 0,
                            onClick = { onAction(DetailsAction.OnTabSelected(0)) },
                            modifier = Modifier
                                .weight(1f),
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(Res.string.ingredients),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        bottom = 4.dp,
                                    )
                            )
                        }
                        Tab(
                            selected = state.selectedTabIndex == 1,
                            onClick = { onAction(DetailsAction.OnTabSelected(1)) },
                            modifier = Modifier
                                .weight(1f),
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            Text(
                                text = stringResource(Res.string.preparation),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        bottom = 4.dp,
                                    )
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                    ) { pageIndex ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            when (pageIndex) {
                                0 -> { // Ingredients
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(
                                                state = ingredientListState,
                                                enabled = enableScrolling,
                                            )
                                    ) {
                                        recipe.ingredients.forEach { (ingredient, amount, overrideUnit) ->
                                            val factor = if (state.customServings != null && recipe.servings != null
                                                    && state.customServings != recipe.servings) {
                                                 state.customServings.toDouble() / recipe.servings.toDouble()
                                            } else 1.0

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .weight(0.3f)
                                                ) {
                                                    Row {
                                                        Text(
                                                            text = (amount?.times(factor)).toNiceString(),
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = if (factor != 1.0) MaterialTheme.colorScheme.secondary
                                                                else MaterialTheme.colorScheme.onSurface,
                                                        )
                                                        Text(
                                                            text = overrideUnit ?: ingredient.unit,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                        )
                                                    }
                                                }
                                                Column(
                                                    modifier = Modifier
                                                        .weight(0.7f)
                                                ) {
                                                    Text(
                                                        text = ingredient.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                1 -> { // Preparation
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(
                                                state = preparationListState,
                                                enabled = enableScrolling,
                                            )
                                    ) {
                                        recipe.steps.forEachIndexed { index, step ->
                                            Text(
                                                text = stringResource(Res.string.step, index + 1),
                                                style = MaterialTheme.typography.titleSmall,
                                            )
                                            Text(
                                                text = step,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            if (index != recipe.steps.size - 1) { // Don't show divider after last step
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 2.dp)
                                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                                ){
                                                    Spacer(modifier = Modifier.height(1.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Text("Recipe not found", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

fun Double?.toNiceString(): String {
    if (this == null) return ""
    if (this % 1.0 == 0.0) {
        return "${this.toInt()} "
    }
    return "$this "
}

@Composable
fun LocalDate.asString(): String {
    return "${this.dayOfWeek.toUiText().asString()}, " +
            "${day.toString().padStart(2, '0')}.${month.number.toString().padStart(2, '0')}.${this.year}"
}