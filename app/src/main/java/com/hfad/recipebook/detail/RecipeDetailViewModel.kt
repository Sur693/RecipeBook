package com.hfad.recipebook.detail
/*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hfad.recipebook.Routes
import com.hfad.recipebook.data.converters.DataConverter
import com.hfad.recipebook.data.locale.MealsDao
import com.hfad.recipebook.data.remote.FoodApi
import com.hfad.recipebook.model.RecipeDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface RecipeDetailUiState {
    data object Loading : RecipeDetailUiState
    data class Recipe(
        val value: RecipeDetail
    ) : RecipeDetailUiState
}

class RecipeDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val mealsDao: MealsDao,
    private val converter: DataConverter
): ViewModel() {

    private val route = savedStateHandle.toRoute<Routes.RecipeDetail>()
    private val mutableState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val state = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val recipe = getLocalRecipe(route.id) ?: getRemoteRecipe(route.id)?.also {
                writeCache(it)
            } ?: return@launch

            mutableState.update {
                RecipeDetailUiState.Recipe(
                    value = recipe
                )
            }
        }
    }
    private suspend fun getLocalRecipe(id: String) : RecipeDetail?{
        return mealsDao.getById(id)?.let{converter.mealDataModelToDetail(it)}
    }

    private suspend fun writeCache(detail: RecipeDetail) {
        mealsDao.insertAll(converter.recipeDetailToEntity(detail))
    }

    private suspend fun getRemoteRecipe(id: String): RecipeDetail?{
        return FoodApi.retrofitService.getRecipeById(id).meals?.firstOrNull()?.let {converter.mealDataModelToDetail(it)}
    }
}

*/

