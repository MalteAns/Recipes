package de.malteans.recipes.core.presentation.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.presentation.add.components.IngredientListItem
import de.malteans.recipes.core.presentation.add.components.rememberImagePickerLauncher
import de.malteans.recipes.core.presentation.components.AnimatedDoubleIconButton
import de.malteans.recipes.core.presentation.components.CustomDialog
import de.malteans.recipes.core.presentation.components.SearchableDropdown
import de.malteans.recipes.core.presentation.components.SnackbarManager
import de.malteans.recipes.core.presentation.util.UiText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.*
import kotlin.time.Duration.Companion.seconds

@Composable
fun AddScreenRoot(
    viewModel: AddViewModel = koinViewModel(),
    onRecipeShow: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AddAction.OnRecipeShow -> onRecipeShow(action.id)
                else -> viewModel.onAction(action)
            }
        },
        onRecipeAdd = { viewModel.onRecipeAdd() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    state: AddState,
    onAction: (AddAction) -> Unit,
    onRecipeAdd: suspend () -> Long, // TODO: refactor with event on finish instead
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState { 3 }

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage to pagerState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { (currentPage, isScrolling) ->
                if (!isScrolling) {
                    onAction(AddAction.OnTabSelect(currentPage))
                }
            }
    }

    var validToAdd by remember { mutableStateOf(false) }

    LaunchedEffect(state.name) {
        validToAdd = state.name.isNotBlank()
    }

    val imagePickerLauncher = rememberImagePickerLauncher { pickedImageData ->
        onAction(AddAction.OnUploadImage(pickedImageData))
    }

    if (state.showIngredientDialog && state.currentIngredient != null) {
        var amount by remember { mutableStateOf(state.ingredients[state.currentIngredient]?.first?.toString() ?: "") }
        var unit by remember { mutableStateOf(state.ingredients[state.currentIngredient]?.second ?: state.currentIngredient.unit) }

        val isValid by remember(amount) { derivedStateOf {
            amount.isBlank() || (amount.toDoubleOrNull() != null && amount.toDouble() > 0)
        } }

        val onEndRequest = {
            if (isValid) {
                onAction(AddAction.OnSubmitIngredientDialog(
                    state.currentIngredient,
                    amount.ifBlank { null }?.toDoubleOrNull(), unit.ifBlank { null } ))
            }
        }

        CustomDialog(
            title = {
                Text(
                    text = stringResource(
                        if (state.isEditingIngredient) Res.string.edit_dialog_title
                            else Res.string.add_dialog_title,
                        state.currentIngredient.name),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            },
            rightIcon = {
                IconButton(onClick = { onEndRequest() }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Submit",
                        tint = if (isValid) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            onDismissRequest = {
                onAction(AddAction.OnIngredientDialogDismiss)
            },
        ) {
            OutlinedTextField(
                value = amount,
                singleLine = true,
                isError = amount.isNotBlank() && (amount.toDoubleOrNull() == null || amount.toDouble() <= 0),
                onValueChange = { amount = it },
                label = { Text(stringResource(Res.string.amount)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onEndRequest()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            OutlinedTextField(
                value = unit,
                singleLine = true,
                onValueChange = { unit = it },
                label = { Text(stringResource(Res.string.unit)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onEndRequest()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if(state.editingRecipe != null) stringResource(Res.string.edit_recipe)
                            else stringResource(Res.string.add_recipe),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    if (state.editingRecipe == null) {
                        IconButton(onClick = {
                            onAction(AddAction.OnClear)
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear the form"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            onAction(AddAction.OnBack)
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back without saving"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            scope.launch {
                                val id = onRecipeAdd()
                                onAction(AddAction.OnClear)
                                if (state.editingRecipe != null) {
                                    onAction(AddAction.OnRecipeShow(id))
                                } else {
                                    SnackbarManager.showSnackbar(
                                        message = UiText.Resource(Res.string.recipe_added_snackbar, state.name),
                                        actionLabel = UiText.Resource(Res.string.show),
                                        onAction = { onAction(AddAction.OnRecipeShow(id)) },
                                        duration = SnackbarDuration.Long,
                                        withDismissAction = true,
                                    )
                                }
                            }
                        },
                        enabled = validToAdd,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Add the recipe",
                            tint = if (validToAdd) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { paddingValues ->
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth(),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Tab(
                        selected = state.selectedTabIndex == 0,
                        onClick = {
                            onAction(AddAction.OnTabSelect(0))
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.general),
                            modifier = Modifier
                                .padding(top = 18.dp, bottom = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = {
                            onAction(AddAction.OnTabSelect(1))
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.ingredients),
                            modifier = Modifier
                                .padding(top = 18.dp, bottom = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex == 2,
                        onClick = {
                            onAction(AddAction.OnTabSelect(2))
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.preparation),
                            modifier = Modifier
                                .padding(top = 18.dp, bottom = 12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { pageIndex ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        when (pageIndex) {
                            0 -> { // General -----------------------------------------------------
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxSize()
                                ) {
                                    // Name -------------------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = state.name,
                                            onValueChange = {
                                                onAction(AddAction.OnNameChange(it))
                                            },
                                            singleLine = true,
                                            label = {
                                                Text(stringResource(Res.string.name))
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(
                                                imeAction = ImeAction.Next
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onNext = {
                                                    focusManager.moveFocus(FocusDirection.Down)
                                                }
                                            )
                                        )
                                    }
                                    // Description ------------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = state.description,
                                            onValueChange = {
                                                onAction(AddAction.OnDescriptionChange(it))
                                            },
                                            label = {
                                                Text(stringResource(Res.string.description))
                                            },
                                            minLines = 3,
                                            maxLines = 5,
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next,
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onNext = {
                                                    focusManager.moveFocus(FocusDirection.Down)
                                                }
                                            )
                                        )
                                    }
                                    // Rating + Servings ------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                        ) {
                                            OutlinedTextField(
                                                value = state.rating?.toString() ?: "",
                                                onValueChange = { str ->
                                                    val int = str.toIntOrNull()
                                                    if ((int != null && int in 1..5) || str.isBlank()) {
                                                        onAction(AddAction.OnRatingChange(int))
                                                    }
                                                },
                                                label = {
                                                    Text(stringResource(Res.string.rating))
                                                },
                                                suffix = {
                                                    Text(" / 5")
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        focusManager.moveFocus(FocusDirection.Right)
                                                    }
                                                ),
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                        ) {
                                            OutlinedTextField(
                                                value = state.servings?.toString() ?: "",
                                                onValueChange = { str ->
                                                    val int = str.toIntOrNull()
                                                    if (int != null || str.isBlank()) {
                                                        onAction(AddAction.OnServingsChange(int))
                                                    }
                                                },
                                                label = {
                                                    Text(stringResource(Res.string.servings))
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        focusManager.moveFocus(FocusDirection.Down)
                                                    }
                                                ),
                                            )
                                        }
                                    }
                                    // Times ------------------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                        ) {
                                            OutlinedTextField(
                                                value = state.workTime?.toString() ?: "",
                                                onValueChange = { str ->
                                                    val int = str.toIntOrNull()
                                                    if (int != null || str.isBlank()) {
                                                        onAction(AddAction.OnWorkTimeChange(int))
                                                    }
                                                },
                                                label = {
                                                    Text(stringResource(Res.string.work_time))
                                                },
                                                suffix = {
                                                    Text(stringResource(Res.string.min))
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        focusManager.moveFocus(FocusDirection.Right)
                                                    }
                                                ),
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(
                                            modifier = Modifier
                                                .weight(1f),
                                        ) {
                                            OutlinedTextField(
                                                value = state.totalTime?.toString() ?: "",
                                                onValueChange = { str ->
                                                    val int = str.toIntOrNull()
                                                    if (int != null || str.isBlank()) {
                                                        onAction(AddAction.OnTotalTimeChange(int))
                                                    }
                                                },
                                                label = {
                                                    Text(stringResource(Res.string.total_time))
                                                },
                                                suffix = {
                                                    Text(stringResource(Res.string.min))
                                                },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        focusManager.moveFocus(FocusDirection.Down)
                                                    }
                                                ),
                                            )
                                        }
                                    }
                                    // ImageUrl ---------------------------------------------------
                                    // TODO: Implement drag and drop for upload
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        OutlinedTextField(
                                            value = state.imageUrl,
                                            singleLine = true,
                                            enabled = !state.imageUploadInProgress,
                                            onValueChange = {
                                                onAction(AddAction.OnImageUrlChange(it))
                                            },
                                            label = {
                                                Text("Image URL")
                                            },
                                            trailingIcon = {
                                                AnimatedDoubleIconButton(
                                                    showSecondary = state.imageUploadInProgress,
                                                    enabled = !state.imageUploadInProgress,
                                                    onClick = { imagePickerLauncher.launch() },
                                                    primaryIcon = {
                                                        Icon(
                                                            imageVector = Icons.Default.FileUpload,
                                                            contentDescription = stringResource(Res.string.upload_image)
                                                        )
                                                    },
                                                    secondaryIcon = {
                                                        CircularProgressIndicator(
                                                            strokeWidth = 3.dp,
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                        )
                                                    }
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Uri,
                                                imeAction = ImeAction.Next
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onNext = {
                                                    focusManager.moveFocus(FocusDirection.Down)
                                                }
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                    // SourceUrl ---------------------------------------------------
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth(),
                                    ) {
                                        OutlinedTextField(
                                            value = state.sourceUrl,
                                            singleLine = true,
                                            onValueChange = {
                                                onAction(AddAction.OnSourceUrlChange(it))
                                            },
                                            label = {
                                                Text(stringResource(Res.string.source_url))
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Uri,
                                                imeAction = ImeAction.Next
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onNext = {
                                                    focusManager.clearFocus()
                                                    onAction(AddAction.OnTabSelect(1))
                                                }
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                            1 -> { // Ingredients -------------------------------------------------
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize()
                                ) {
                                    SearchableDropdown(
                                        options = state.allIngredients.associateWith { it.name },
                                        selectedOption = Pair(null, ""),
                                        onValueAdded = {
                                            val ingredient = Ingredient(
                                                name = it,
                                                unit = "",
                                            )
                                            onAction(AddAction.OnIngredientAdd(ingredient))
                                        },
                                        onValueChanged = {
                                            onAction(AddAction.OnIngredientAdd(it))
                                        },
                                        label = { Text(text = stringResource(Res.string.add_ingredient) + "…") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    if (state.ingredients.isNotEmpty()) {
                                        LazyColumn(
                                            modifier = Modifier
                                                .padding(top = 12.dp)
                                                .weight(1f)
                                                .fillMaxWidth()
                                        ) {
                                            items(state.ingredients.map { Triple(it.key, it.value.first, it.value.second) }) { triple ->
                                                val ingredient = triple.first
                                                val amount = triple.second
                                                val unit = if (triple.third != null && triple.third != ingredient.unit) triple.third
                                                    else ingredient.unit
                                                IngredientListItem(
                                                    ingredient = ingredient,
                                                    amount = amount,
                                                    unit = unit,
                                                    onEdit = {
                                                        onAction(AddAction.OnIngredientEdit(ingredient))
                                                    },
                                                    onDelete = {
                                                        onAction(AddAction.OnIngredientRemove(ingredient))
                                                    }
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = stringResource(Res.string.no_ingredients),
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .padding(top = 16.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                            2 -> { // Preparation -------------------------------------------------
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxSize()
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        item {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp)
                                                    .clickable {
                                                        onAction(AddAction.OnStepAdd(0))
                                                    }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add step",
                                                )
                                            }
                                        }
                                        items(state.steps.size) { index ->
                                            var deleteClicked by remember { mutableStateOf(false) }
                                            LaunchedEffect(state.steps[index]) {
                                                deleteClicked = false
                                            }
                                            LaunchedEffect(deleteClicked) {
                                                if (deleteClicked) {
                                                    delay(3.seconds)
                                                    deleteClicked = false
                                                }
                                            }

                                            Row (
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    OutlinedTextField(
                                                        value = state.steps[index],
                                                        onValueChange = {
                                                            onAction(AddAction.OnStepChange(
                                                                index = index,
                                                                newValue = it
                                                            ))
                                                        },
                                                        label = {
                                                            Text(stringResource(Res.string.step, index + 1))
                                                        },
                                                        minLines = 3,
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                    )
                                                }
                                                Column {
                                                    IconButton(
                                                        onClick = {
                                                            if (!deleteClicked) {
                                                                deleteClicked = true
                                                            } else {
                                                                onAction(AddAction.OnStepRemove(index))
                                                                deleteClicked = false
                                                            }
                                                        },
                                                        enabled = state.steps.size > 1
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = "Remove step",
                                                            tint = when {
                                                                state.steps.size == 1 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                                                deleteClicked -> MaterialTheme.colorScheme.error
                                                                else -> MaterialTheme.colorScheme.onSurface
                                                            },
                                                        )
                                                    }
                                                }
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp)
                                                    .clickable {
                                                        onAction(AddAction.OnStepAdd(index + 1))
                                                    },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add step",
                                                    tint = MaterialTheme.colorScheme.onSurface,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

