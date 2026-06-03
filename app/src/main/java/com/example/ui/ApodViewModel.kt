package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ApodEntity
import com.example.data.ApodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ApodUiState {
    object Loading : ApodUiState
    data class Success(
        val apods: List<ApodEntity>,
        val featuredApod: ApodEntity?
    ) : ApodUiState
    data class Error(val message: String) : ApodUiState
}

class ApodViewModel(private val repository: ApodRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ApodUiState>(ApodUiState.Loading)
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()

    private val _favorites = MutableStateFlow<List<ApodEntity>>(emptyList())
    val favorites: StateFlow<List<ApodEntity>> = _favorites.asStateFlow()
    
    private val _showGrid = MutableStateFlow(false)
    val showGrid: StateFlow<Boolean> = _showGrid.asStateFlow()

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    private val _isFetchingCustomDate = MutableStateFlow(false)
    val isFetchingCustomDate: StateFlow<Boolean> = _isFetchingCustomDate.asStateFlow()

    private val _customDateError = MutableStateFlow<String?>(null)
    val customDateError: StateFlow<String?> = _customDateError.asStateFlow()

    init {
        observeApods()
        observeFavorites()
        refreshToday()
    }

    private fun observeApods() {
        viewModelScope.launch {
            combine(repository.allApods, _selectedDate) { apods, selectedDate ->
                if (apods.isNotEmpty()) {
                    val featured = if (selectedDate != null) {
                        apods.find { it.date == selectedDate } ?: apods.firstOrNull()
                    } else {
                        apods.firstOrNull()
                    }
                    ApodUiState.Success(apods = apods, featuredApod = featured)
                } else {
                    ApodUiState.Loading
                }
            }
            .catch { e ->
                _uiState.value = ApodUiState.Error(e.message ?: "Unknown error")
            }
            .collect { state ->
                if (state is ApodUiState.Success) {
                    _uiState.value = state
                }
            }
        }
    }
    
    private fun observeFavorites() {
        viewModelScope.launch {
            repository.favorites.collect { favs ->
                _favorites.value = favs
            }
        }
    }
    
    fun toggleGrid() {
        _showGrid.value = !_showGrid.value
    }
    
    fun toggleFavorite(apod: ApodEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(apod.date, !apod.isFavorite)
        }
    }
    
    fun fetchLast7Days() {
        viewModelScope.launch {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val todayStr = repository.getTodayString()
            val today = sdf.parse(todayStr) ?: return@launch
            
            for (i in 0..6) {
                val cal = java.util.Calendar.getInstance()
                cal.time = today
                cal.add(java.util.Calendar.DATE, -i)
                val targetDateStr = sdf.format(cal.time)
                
                if (repository.getApodByDate(targetDateStr) == null) {
                    try {
                        repository.refreshApod(targetDateStr)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun selectAdjacentDate(days: Int) {
        val currentState = _uiState.value
        val currentDateStr = if (currentState is ApodUiState.Success) {
            currentState.featuredApod?.date ?: repository.getTodayString()
        } else {
            _selectedDate.value ?: repository.getTodayString()
        }
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        try {
            val date = sdf.parse(currentDateStr) ?: return
            val cal = java.util.Calendar.getInstance()
            cal.time = date
            cal.add(java.util.Calendar.DATE, days)
            val adjacentDateStr = sdf.format(cal.time)
            selectDate(adjacentDateStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun selectDate(date: String) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        try {
            val parsedDate = sdf.parse(date)
            val minDate = sdf.parse("1995-06-16")
            val maxDate = java.util.Date()
            
            if (parsedDate != null && (parsedDate.before(minDate) || parsedDate.after(maxDate))) {
                _customDateError.value = "Galaxy logs exist only from 1995-06-16 to today!"
                return
            }
        } catch (e: Exception) {
            _customDateError.value = "Invalid stardate coordinate format."
            return
        }

        _selectedDate.value = date
        _customDateError.value = null
        
        viewModelScope.launch {
            val localApod = repository.getApodByDate(date)
            if (localApod == null) {
                _isFetchingCustomDate.value = true
                try {
                    repository.refreshApod(date)
                } catch (e: Exception) {
                    _customDateError.value = e.message ?: "Failed to contact NASA mainframe for date $date"
                } finally {
                    _isFetchingCustomDate.value = false
                }
            }
        }
    }

    fun clearCustomDateError() {
        _customDateError.value = null
    }

    fun refreshToday(forceFetch: Boolean = false) {
        viewModelScope.launch {
            val todayStr = repository.getTodayString()
            if (!forceFetch) {
                val localApod = repository.getApodByDate(todayStr)
                if (localApod != null) {
                    _selectedDate.value = todayStr
                    return@launch
                }
            }

            val currentState = _uiState.value
            if (currentState !is ApodUiState.Success) {
                _uiState.value = ApodUiState.Loading
            }
            try {
                repository.refreshApod()
                _selectedDate.value = todayStr
            } catch (e: Exception) {
                if (_uiState.value !is ApodUiState.Success) {
                    _uiState.value = ApodUiState.Error(e.message ?: "Failed to fetch data")
                }
            }
        }
    }
}
