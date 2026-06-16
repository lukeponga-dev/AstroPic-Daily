package com.example.ui

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.ApodEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Zenith Minimalist Design: A spacious, high-contrast, modern UI for celestial exploration.
 * Focuses on negative space, breathable typography, and refined Material 3 components.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodScreen(viewModel: ApodViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFetchingCustomDate by viewModel.isFetchingCustomDate.collectAsStateWithLifecycle()
    val customDateError by viewModel.customDateError.collectAsStateWithLifecycle()
    val showGrid by viewModel.showGrid.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    BoxWithConstraints(modifier = Modifier.fillMaxSize().testTag("main_container")) {
        val isWideScreen = maxWidth >= 600.dp

        val topBarContent: @Composable () -> Unit = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "Astropic",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Daily",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }, modifier = Modifier.testTag("date_button")) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                    }
                    IconButton(onClick = { showPrivacyPolicy = true }, modifier = Modifier.testTag("privacy_button")) {
                        Icon(Icons.Default.Shield, contentDescription = "Privacy")
                    }
                    IconButton(onClick = { viewModel.refreshToday(forceFetch = true) }, modifier = Modifier.testTag("refresh_button")) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        val contentBody: @Composable (PaddingValues) -> Unit = { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (val state = uiState) {
                    is ApodUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(strokeWidth = 2.dp)
                        }
                    }
                    is ApodUiState.Success -> {
                        AnimatedContent(targetState = selectedTab, label = "TabTransition") { tab ->
                            when (tab) {
                                0 -> ObservatoryTab(state, isFetchingCustomDate, customDateError, viewModel, isWideScreen)
                                1 -> SearchTab(searchQuery, searchResults, viewModel, isWideScreen) { selectedTab = it }
                                2 -> FeedTab(state, showGrid, viewModel, isWideScreen)
                                3 -> VaultTab(favorites, viewModel, isWideScreen)
                            }
                        }
                    }
                    is ApodUiState.Error -> {
                        ErrorView(state.message) { viewModel.refreshToday() }
                    }
                }
            }
        }

        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxHeight(),
                    header = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 16.dp)) {
                            Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                    }
                ) {
                    Spacer(Modifier.height(32.dp))
                    NavigationRailItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Explore, null) },
                        label = { Text("Explore") }
                    )
                    NavigationRailItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Search, null) },
                        label = { Text("Search") }
                    )
                    NavigationRailItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.AutoAwesomeMotion, null) },
                        label = { Text("Feed") }
                    )
                    NavigationRailItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Bookmarks, null) },
                        label = { Text("Vault") }
                    )
                }

                Scaffold(
                    topBar = topBarContent,
                    modifier = Modifier.weight(1f)
                ) { innerPadding ->
                    contentBody(innerPadding)
                }
            }
        } else {
            Scaffold(
                topBar = topBarContent,
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.Explore, null) },
                            label = { Text("Explore") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.Search, null) },
                            label = { Text("Search") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(Icons.Default.AutoAwesomeMotion, null) },
                            label = { Text("Feed") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer)
                        )
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(Icons.Default.Bookmarks, null) },
                            label = { Text("Vault") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.tertiaryContainer)
                        )
                    }
                }
            ) { innerPadding ->
                contentBody(innerPadding)
            }
        }

        if (showDatePicker) {
            SimpleDatePickerDialog(
                onDateSelected = { viewModel.selectDate(it) },
                onDismiss = { showDatePicker = false }
            )
        }

        PrivacyPolicyDialog(
            isOpen = showPrivacyPolicy,
            onDismiss = { showPrivacyPolicy = false }
        )
    }
}

@Composable
fun ObservatoryTab(
    state: ApodUiState.Success,
    isFetching: Boolean,
    error: String?,
    viewModel: ApodViewModel,
    isWideScreen: Boolean
) {
    val horizontalPadding = if (isWideScreen) 32.dp else 20.dp
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        error?.let {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(12.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.clearCustomDateError() }) {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        state.featuredApod?.let { apod ->
            FeaturedApodSection(apod, isFetching, viewModel, isWideScreen)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun FeaturedApodSection(
    apod: ApodEntity,
    isFetching: Boolean,
    viewModel: ApodViewModel,
    isWideScreen: Boolean
) {
    val context = LocalContext.current

    if (isWideScreen) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .aspectRatio(1f) // Ensure image is well proportioned and large
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ApodImageWithOverlays(apod, isFetching, viewModel)
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ApodDetailsAndActions(apod, context)
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ApodImageWithOverlays(apod, isFetching, viewModel)
            }
            ApodDetailsAndActions(apod, context)
        }
    }
}

@Composable
private fun ApodImageWithOverlays(apod: ApodEntity, isFetching: Boolean, viewModel: ApodViewModel) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(apod.url)
            .crossfade(true)
            .build(),
        contentDescription = apod.title,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        loading = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        }
    )

    if (isFetching) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }

    // Top overlay tags
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = CircleShape,
        ) {
            Text(
                apod.date,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
        
        IconButton(
            onClick = { viewModel.toggleFavorite(apod) },
            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = if (apod.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (apod.isFavorite) Color.Red else Color.White
            )
        }
    }
    
    // Interaction arrows
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { viewModel.selectAdjacentDate(-1) },
            modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
        IconButton(
            onClick = { viewModel.selectAdjacentDate(1) },
            modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White)
        }
    }
}

@Composable
private fun ApodDetailsAndActions(apod: ApodEntity, context: android.content.Context) {
    Text(
        text = apod.title,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
        color = MaterialTheme.colorScheme.onSurface
    )

    if (!apod.copyright.isNullOrBlank()) {
        Text(
            text = "© ${apod.copyright.replace("\n", " ").trim()}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Observation: ${apod.title}\n\n${apod.url}")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Share")
        }

        Button(
            onClick = {
                DownloadUtils.downloadImage(context, apod.hdurl ?: apod.url, "APOD_${apod.date}.jpg")
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Acquire")
        }
    }

    Text(
        text = apod.explanation,
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun FeedTab(
    state: ApodUiState.Success,
    showGrid: Boolean,
    viewModel: ApodViewModel,
    isWideScreen: Boolean
) {
    val items = state.apods
    val horizontalPadding = if (isWideScreen) 32.dp else 20.dp
    
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Archives",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = { viewModel.toggleGrid() }) {
                Icon(if (showGrid) Icons.Default.ViewList else Icons.Default.GridView, null)
            }
        }

        if (showGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.date }) { apod ->
                    GridApodCard(apod) { viewModel.selectDate(apod.date) }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 350.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items, key = { it.date }) { apod ->
                    ListApodCard(apod) { viewModel.selectDate(apod.date) }
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        if (items.size < 5) viewModel.fetchLast7Days()
    }
}

@Composable
fun VaultTab(
    favorites: List<ApodEntity>,
    viewModel: ApodViewModel,
    isWideScreen: Boolean
) {
    val horizontalPadding = if (isWideScreen) 32.dp else 20.dp

    if (favorites.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(16.dp))
                Text("No archives saved in vault", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 350.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = horizontalPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Secure Vault",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(favorites, key = { "fav_${it.date}" }) { apod ->
                ListApodCard(apod) { viewModel.selectDate(apod.date) }
            }
        }
    }
}

@Composable
fun GridApodCard(apod: ApodEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            SubcomposeAsyncImage(
                model = apod.url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    apod.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    apod.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun ListApodCard(apod: ApodEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = apod.url,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    apod.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    apod.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Default.WifiOff, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
            Text("Sync error", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(message, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Retry Connection")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    onDateSelected(sdf.format(Date(millis)))
                }
                onDismiss()
            }) {
                Text("Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun SearchTab(
    searchQuery: String,
    searchResults: List<ApodEntity>,
    viewModel: ApodViewModel,
    isWideScreen: Boolean,
    onTabSwitch: (Int) -> Unit
) {
    val horizontalPadding = if (isWideScreen) 32.dp else 20.dp
    
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalPadding)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            placeholder = { Text("Search by keywords or date (yyyy-mm-dd)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.performSearchAction()
                val query = searchQuery.trim()
                if (query.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                    onTabSwitch(0)
                }
            }),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        if (searchQuery.isBlank()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Enter a keyword or a full date like 2023-10-15 to search local archives.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else if (searchResults.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No matching archives found locally. Try fetching a specific date via the top right icon.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 350.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults, key = { "search_${it.date}" }) { apod ->
                    ListApodCard(apod) {
                        viewModel.selectDate(apod.date)
                        onTabSwitch(0)
                    }
                }
            }
        }
    }
}
