package de.malteans.recipes.core.presentation.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.recipes.core.domain.RecipeRepository
import de.malteans.recipes.core.presentation.plan.components.PlannedDayData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PlanViewModel(
    private val repository: RecipeRepository
): ViewModel() {

    private val _jumpToIndex = MutableStateFlow<Int?>(null)

    private val _allRecipes = repository.getAllRecipes() // TODO: optimize, just get recipes that match query

    private val _allPlannedRecipes = repository.getPlannedRecipes()

    private val _expandedDay = MutableStateFlow<LocalDate?>(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    private val _allPlannedDayData = combine(
        _allPlannedRecipes,
        _expandedDay
    ) { allPlannedRecipes, expandedDay ->

        val result = allPlannedRecipes.groupBy { plannedRecipe -> plannedRecipe.date }.map { (date, plannedRecipes) ->
            PlannedDayData(
                date = date,
                recipes = plannedRecipes.sortedBy { it.timeOfDay },
                isExpanded = date == expandedDay
            )
        }.sortedBy { it.date }

        if (_jumpToIndex.value == null) {
            _jumpToIndex.update {
                val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                result.indexOfFirst {
                    it.date >= currentDate
                }
            }
        }

        return@combine result
    }

    private val _state = MutableStateFlow(PlanState())

    val state = combine(
        _state,
        _allRecipes,
        _allPlannedDayData,
        _jumpToIndex
    ) { state, allRecipes, allPlannedDayData, jumpToIndex ->
        state.copy(
            allRecipes = allRecipes,
            allPlannedDayData = allPlannedDayData,
            jumpToIndex = jumpToIndex,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlanState()
    )

    fun onAction(action: PlanAction) {
        when (action) {
            is PlanAction.OnExpandDay -> {
                _expandedDay.update {
                    if (action.expand) action.date
                    else null
                }
            }
            is PlanAction.OnDeleteAllPlans -> {
                viewModelScope.launch {
                    _allPlannedRecipes.first().forEach {
                        repository.deletePlan(it.id)
                    }
                }
            }
            // Edit Methods -----------------------------------------------------------------------
            is PlanAction.ShowEditPlanDialog -> {
                _state.update { it.copy(
                        showEditPlanDialog = true,
                        planToEdit = action.plannedRecipe,
                ) }
            }
            is PlanAction.DismissEditPlanDialog -> {
                _state.update { it.copy(
                    showEditPlanDialog = false,
                    planToEdit = null,
                ) }
            }
            is PlanAction.OnEditPlan -> {
                viewModelScope.launch {
                    repository.updatePlan(action.plannedRecipe)
                }
            }

            is PlanAction.OnDeletePlan -> {
                viewModelScope.launch {
                    repository.deletePlan(action.planId)
                }
            }
            else -> throw IllegalArgumentException("Action not implemented in ViewModel: $action")
        }
    }
}

