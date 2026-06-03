package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.geometry.Offset
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.ApodEntity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodScreen(viewModel: ApodViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFetchingCustomDate by viewModel.isFetchingCustomDate.collectAsStateWithLifecycle()
    val customDateError by viewModel.customDateError.collectAsStateWithLifecycle()
    val showGrid by viewModel.showGrid.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "CELESTIAL OBSERVATORY",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.testTag("select_date_button")
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Travel to Date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { showPrivacyPolicy = true },
                            modifier = Modifier.testTag("privacy_policy_button")
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = "Privacy Protocols",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.refreshToday(forceFetch = true) },
                            modifier = Modifier.testTag("refresh_button")
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh Today",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val gridColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .drawBehind {
                    // Draw subtle coordinate grid markers for a high-tech observatory theme
                    val width = this.size.width
                    val height = this.size.height
                    
                    // Horizontal gridlines
                    for (i in 1..4) {
                        val y = (height / 5f) * i.toFloat()
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }
                    // Vertical gridlines
                    for (i in 1..4) {
                        val x = (width / 5f) * i.toFloat()
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, height),
                            strokeWidth = 1f
                        )
                    }
                }
        ) {
            when (val state = uiState) {
                is ApodUiState.Loading -> {
                    CosmicRadarLoader(modifier = Modifier.align(Alignment.Center))
                }
                is ApodUiState.Success -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isWide = maxWidth >= 768.dp

                        if (isWide) {
                            // SPLIT PANE RESPONSIVE LAYOUT (Tablets / Landscape foldables)
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Left Pane: Featured detailed card (scrollable)
                                Column(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .fillMaxHeight()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Error banner inside Left Pane if error exists
                                    customDateError?.let { err ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Warning,
                                                    contentDescription = "Error",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                                Spacer(Modifier.width(12.dp))
                                                Text(
                                                    text = err,
                                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(
                                                    onClick = { viewModel.clearCustomDateError() },
                                                    modifier = Modifier.testTag("clear_error_button")
                                                ) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Dismiss error",
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = "SELECTED OBSERVATION DETAILED READOUT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    state.featuredApod?.let { featured ->
                                        FeaturedApodCard(
                                            apod = featured,
                                            onPrevDay = { viewModel.selectAdjacentDate(-1) },
                                            onNextDay = { viewModel.selectAdjacentDate(1) },
                                            isLoadingNewCoord = isFetchingCustomDate,
                                            viewModel = viewModel
                                        )
                                    } ?: Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                            .background(
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                                RoundedCornerShape(24.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No celestial bodies targeted yet", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }

                                // Right Pane: Log Feed list
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "COSMIC REGISTRY & ARCHIVES",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    CosmicTabSwitcher(
                                        selectedTab = selectedTab,
                                        onTabSelected = { selectedTab = it }
                                    )

                                    val otherApods = state.apods.filter { it.date != state.featuredApod?.date }

                                    if (selectedTab == 0) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "CELESTIAL FEED (ARCHIVE)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                            IconButton(onClick = { viewModel.toggleGrid() }) {
                                                Icon(
                                                    if (showGrid) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                                                    contentDescription = "Toggle Grid",
                                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            }
                                        }

                                        if (otherApods.isEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "Syncing archive coordinates...",
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                LaunchedEffect(Unit) {
                                                    viewModel.fetchLast7Days()
                                                }
                                            }
                                        } else {
                                            if (showGrid) {
                                                LazyVerticalGrid(
                                                    columns = GridCells.Fixed(2),
                                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    items(otherApods, key = { it.date }) { historicalApod ->
                                                        GridLogCard(
                                                            apod = historicalApod,
                                                            onClick = { viewModel.selectDate(historicalApod.date) }
                                                        )
                                                    }
                                                }
                                            } else {
                                                LazyColumn(
                                                    modifier = Modifier.weight(1f),
                                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    items(otherApods, key = { it.date }) { historicalApod ->
                                                        CompactLogCard(
                                                            apod = historicalApod,
                                                            onClick = { viewModel.selectDate(historicalApod.date) }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // Bookmarked observations
                                        if (favorites.isEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "No favorite observations saved.",
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                items(favorites, key = { "fav_${it.date}" }) { favApod ->
                                                    CompactLogCard(
                                                        apod = favApod,
                                                        onClick = { viewModel.selectDate(favApod.date) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // COMPACT SINGLE COLUMN LAYOUT (Normal Mobile Phones)
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Error banner if any custom date fetch fail
                                customDateError?.let { err ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Warning,
                                                contentDescription = "Error",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                text = err,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { viewModel.clearCustomDateError() },
                                                modifier = Modifier.testTag("clear_error_button")
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Dismiss error",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }

                                // Scrollable content showing featured APOD & custom log
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    item {
                                        Text(
                                            text = "ACTIVE DISCOVERY STATE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                                        )
                                    }

                                    // Master Featured Item
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            state.featuredApod?.let { featured ->
                                                FeaturedApodCard(
                                                    apod = featured,
                                                    onPrevDay = { viewModel.selectAdjacentDate(-1) },
                                                    onNextDay = { viewModel.selectAdjacentDate(1) },
                                                    isLoadingNewCoord = isFetchingCustomDate,
                                                    viewModel = viewModel
                                                )
                                            } ?: Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(300.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                                        RoundedCornerShape(24.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("No celestial bodies targeted yet", color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }

                                    item {
                                        CosmicTabSwitcher(
                                            selectedTab = selectedTab,
                                            onTabSelected = { selectedTab = it }
                                        )
                                    }

                                    if (selectedTab == 0) {
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "CELESTIAL FEED (ARCHIVE)",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                )
                                                IconButton(onClick = { viewModel.toggleGrid() }) {
                                                    Icon(
                                                        if (showGrid) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                                                        contentDescription = "Toggle Grid",
                                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                    )
                                                }
                                            }
                                        }

                                        val otherApods = state.apods.filter { it.date != state.featuredApod?.date }
                                        if (otherApods.isEmpty()) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        "No older logs retrieving from NASA...",
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                                LaunchedEffect(Unit) {
                                                    viewModel.fetchLast7Days()
                                                }
                                            }
                                        } else {
                                            if (showGrid) {
                                                item {
                                                    LazyVerticalGrid(
                                                        columns = GridCells.Fixed(2),
                                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                        modifier = Modifier.heightIn(max = 1000.dp)
                                                    ) {
                                                        items(otherApods, key = { it.date }) { historicalApod ->
                                                            GridLogCard(
                                                                apod = historicalApod,
                                                                onClick = { viewModel.selectDate(historicalApod.date) }
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                items(otherApods, key = { it.date }) { historicalApod ->
                                                    CompactLogCard(
                                                        apod = historicalApod,
                                                        onClick = { viewModel.selectDate(historicalApod.date) }
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        if (favorites.isEmpty()) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 32.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        "No favorite observations saved.",
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        } else {
                                            items(favorites, key = { "fav_${it.date}" }) { favApod ->
                                                CompactLogCard(
                                                    apod = favApod,
                                                    onClick = { viewModel.selectDate(favApod.date) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is ApodUiState.Error -> {
                    ErrorState(state.message) { viewModel.refreshToday() }
                }
            }
        }

        if (showDatePicker) {
            ApodDatePickerDialog(
                onDateSelected = { selectedDate ->
                    viewModel.selectDate(selectedDate)
                },
                onDismiss = { showDatePicker = false }
            )
        }

        PrivacyPolicyDialog(
            isOpen = showPrivacyPolicy,
            onDismiss = { showPrivacyPolicy = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // NASA APOD started on June 16, 1995
                val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                val todayMillis = calendar.timeInMillis

                calendar.set(1995, java.util.Calendar.JUNE, 16, 0, 0, 0)
                val minMillis = calendar.timeInMillis

                return utcTimeMillis in minMillis..todayMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                return year in 1995..currentYear
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        val formattedDate = sdf.format(Date(millis))
                        onDateSelected(formattedDate)
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("confirm_date_button")
            ) {
                Text(
                    "LAUNCH VOYAGE", 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ABORT", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = "SELECT CELESTIAL STARDATE",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            },
            headline = {
                Text(
                    text = "Cosmic Coordinates",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                headlineContentColor = MaterialTheme.colorScheme.onSurface,
                weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                navigationContentColor = MaterialTheme.colorScheme.primary,
                yearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayContentColor = MaterialTheme.colorScheme.secondary,
                todayDateBorderColor = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Composable
fun ApodTelemetryDashboard(
    apod: ApodEntity
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "SECTOR COORD",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = apod.date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(modifier = Modifier.weight(1.2f)) {
            Text(
                text = "SPECTRAL FEED",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (apod.mediaType.lowercase() == "image") "HIGH-RES VISUAL" else "MOTION MATRIX",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "FEED STATUS",
                style = MaterialTheme.typography.labelSmall,
                color = if (apod.hdurl != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (apod.hdurl != null) "HD LINK FIXED" else "SD NOMINAL",
                style = MaterialTheme.typography.labelMedium,
                color = if (apod.hdurl != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun FeaturedApodCard(
    apod: ApodEntity,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    isLoadingNewCoord: Boolean,
    viewModel: ApodViewModel? = null
) {
    val context = LocalContext.current
    val cornerColorSelected = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(apod.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = apod.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        ShimmerImagePlaceholder()
                    }
                )

                // Image gradient overlay to enhance text readability of badges and corners
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // High-tech viewport targeting markings
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .drawBehind {
                            val sizePx = 16.dp.toPx()
                            val strokeWidth = 2.dp.toPx()
                            val cornerColor = cornerColorSelected
                            
                            val w = this.size.width
                            val h = this.size.height
                            
                            // Top-Left
                            drawLine(cornerColor, Offset.Zero, Offset(sizePx, 0f), strokeWidth)
                            drawLine(cornerColor, Offset.Zero, Offset(0f, sizePx), strokeWidth)
                            
                            // Top-Right
                            drawLine(cornerColor, Offset(w, 0f), Offset(w - sizePx, 0f), strokeWidth)
                            drawLine(cornerColor, Offset(w, 0f), Offset(w, sizePx), strokeWidth)
                            
                            // Bottom-Left
                            drawLine(cornerColor, Offset(0f, h), Offset(sizePx, h), strokeWidth)
                            drawLine(cornerColor, Offset(0f, h), Offset(0f, h - sizePx), strokeWidth)
                            
                            // Bottom-Right
                            drawLine(cornerColor, Offset(w, h), Offset(w - sizePx, h), strokeWidth)
                            drawLine(cornerColor, Offset(w, h), Offset(w, h - sizePx), strokeWidth)
                        }
                )
                
                // Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "STARDATE: ${apod.date}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (apod.mediaType.uppercase() != "IMAGE") {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = apod.mediaType.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Surfing Overlay Loader
                if (isLoadingNewCoord) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.75f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Acquiring stellar signals...",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Relative surf/arrow navigation overlays inside the image box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onPrevDay,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(50.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                            .testTag("prev_day_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Stardate",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onNextDay,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(50.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                            .testTag("next_day_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Stardate",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = apod.title.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                
                Spacer(Modifier.height(12.dp))
                
                // Beautiful Telemetry Metadata Dashboard
                ApodTelemetryDashboard(apod = apod)

                // Sleek Capsule Command Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // BOOKMARK (Favorite) button
                    val isFav = apod.isFavorite
                    val animFavColor by animateColorAsState(
                        targetValue = if (isFav) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        label = "FavColor"
                    )
                    val animFavContentColor by animateColorAsState(
                        targetValue = if (isFav) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        label = "FavContent"
                    )
                    
                    Row(
                        modifier = Modifier
                            .weight(1.1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(animFavColor)
                            .border(
                                width = 1.dp,
                                color = if (isFav) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f) 
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel?.toggleFavorite(apod) },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Bookmark",
                            tint = animFavContentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isFav) "BOOKMARKED" else "BOOKMARK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = animFavContentColor
                        )
                    }

                    // TRANSMIT (Share) button
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .clickable {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Observe: ${apod.title}\n\n${apod.url} - NASA Observatory")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Transmit coordinates..."))
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "TRANSMIT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }

                    // ACQUIRE (Download) button
                    val isImage = apod.mediaType.uppercase() == "IMAGE"
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isImage) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) 
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.03f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isImage) MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f) 
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable(enabled = isImage) {
                                DownloadUtils.downloadImage(context, apod.hdurl ?: apod.url, "APOD_${apod.date}.jpg")
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = if (isImage) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "ACQUIRE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isImage) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }

                // Beautifully designed paragraph container with asymmetrical line bracket
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .heightIn(min = 60.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                            .align(Alignment.CenterVertically)
                    )
                    
                    Column {
                        Text(
                            text = "OBSERVATIONAL MISSION READOUT:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = apod.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactLogCard(
    apod: ApodEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (apod.isFavorite) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f) 
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (apod.isFavorite) MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(apod.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        CompactShimmerPlaceholder()
                    }
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = apod.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (apod.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Saved Bookmark",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "COORD: ${apod.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
                    
                    Text(
                        text = if (apod.mediaType.uppercase() == "IMAGE") "VISUAL" else "MOTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = apod.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun GridLogCard(
    apod: ApodEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (apod.isFavorite) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f) 
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(apod.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = { CompactShimmerPlaceholder() }
                )
                
                if (apod.isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Saved",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = apod.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "STARDATE: ${apod.date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Cosmic Disconnection",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 24.dp),
            lineHeight = 20.sp
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag("retry_button")
        ) {
            Text("Retry Mission")
        }
    }
}

@Composable
fun ShimmerImagePlaceholder(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .background(brush)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "TRANSMITTING HIGH-RES IMAGERY...",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun CompactShimmerPlaceholder(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .background(brush)
            .fillMaxSize()
    )
}

@Composable
fun CosmicTabSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = listOf("CELESTIAL FEED", "BOOKMARKED PLANETS")
        
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            val animatedBgColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "BgColor"
            )
            val animatedBorderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "BorderColor"
            )
            val animatedTextColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "TextColor"
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(animatedBgColor)
                    .then(
                        if (isSelected) Modifier.border(1.dp, animatedBorderColor, RoundedCornerShape(8.dp))
                        else Modifier
                    )
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = animatedTextColor
                )
            }
        }
    }
}

@Composable
fun CosmicRadarLoader(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "RadarRotation")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing)
        ),
        label = "rotation"
    )
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    val w = this.size.width
                    val h = this.size.height
                    
                    // Draw outer telemetry circles
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.15f),
                        radius = w / 2f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = secondaryColor.copy(alpha = 0.1f),
                        radius = w * 0.35f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                    )
                    
                    // Draw a scanner sweep line
                    val sweepRadius = w / 2f
                    val angleRad = Math.toRadians(rotation.toDouble())
                    val endX = this.center.x + sweepRadius * Math.cos(angleRad).toFloat()
                    val endY = this.center.y + sweepRadius * Math.sin(angleRad).toFloat()
                    
                    drawLine(
                        color = primaryColor.copy(alpha = pulseAlpha),
                        start = this.center,
                        end = Offset(endX, endY),
                        strokeWidth = 2.dp.toPx()
                    )
                    
                    // Center core glow
                    drawCircle(
                        color = tertiaryColor.copy(alpha = pulseAlpha),
                        radius = 6.dp.toPx()
                    )
                }
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "CONNECTING DEEP SPACE TELEMETRY...",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            "Acquiring signal from NASA observatories",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
