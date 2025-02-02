package com.it2161.dit233774U.movieviewer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MovieViewModel, navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val currentUser by viewModel.currentUser.collectAsState()
    val loginError by viewModel.loginError.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("movieList")
        }
    }

    val exitTransition = remember {
        slideOutHorizontally(
            targetOffsetX = { -300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    }
    val enterTransition = remember {
        slideInHorizontally(
            initialOffsetX = { 300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Login") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User ID") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = {
                            isPasswordVisible = !isPasswordVisible
                        }) {
                            Icon(imageVector = image, contentDescription = if (isPasswordVisible) "Hide password" else "Show password")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.loginUser(userId, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isOffline
                ) {
                    Text("Login")
                }

                AnimatedVisibility(
                    visible = loginError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    loginError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isOffline
                ) {
                    Text("Don't have an account? Register")
                }

                if (isOffline) {
                    Text(
                        "Offline mode: Login and registration are unavailable",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: MovieViewModel, navController: NavController) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var preferredName by remember { mutableStateOf("") }
    val isOffline by viewModel.isOffline.collectAsState()

    var userIdError by remember { mutableStateOf<String?>(null) }
    var passwordRequirements by remember { mutableStateOf(mapOf(
        "At least 8 characters long" to false,
        "At least one digit" to false,
        "At least one lowercase letter" to false,
        "At least one uppercase letter" to false,
        "At least one special character (@#$%^&+=)" to false,
        "No spaces allowed" to true
    )) }
    var preferredNameError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var showRequirements by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun validateUserId(id: String): Boolean {
        return if (id.length < 3) {
            userIdError = "User ID must be at least 3 characters long"
            false
        } else {
            userIdError = null
            true
        }
    }

    fun validatePassword(pass: String): Boolean {
        passwordRequirements = mapOf(
            "At least 8 characters long" to (pass.length >= 8),
            "At least one digit" to pass.contains(Regex("[0-9]")),
            "At least one lowercase letter" to pass.contains(Regex("[a-z]")),
            "At least one uppercase letter" to pass.contains(Regex("[A-Z]")),
            "At least one special character (@#$%^&+=)" to pass.contains(Regex("[@#$%^&+=]")),
            "No spaces allowed" to !pass.contains(" ")
        )
        return passwordRequirements.all { it.value }
    }

    fun validatePreferredName(name: String): Boolean {
        return if (name.length < 3) {
            preferredNameError = "Preferred name must be at least 3 characters long"
            false
        } else {
            preferredNameError = null
            true
        }
    }

    fun validateConfirmPassword(pass: String, confirmPass: String): Boolean {
        return if (pass != confirmPass) {
            confirmPasswordError = "Passwords do not match"
            false
        } else {
            confirmPasswordError = null
            true
        }
    }

    suspend fun checkUserIdExists(id: String): Boolean {
        return viewModel.checkUserIdExists(id)
    }

    val exitTransition = remember {
        slideOutHorizontally(
            targetOffsetX = { -300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    }
    val enterTransition = remember {
        slideInHorizontally(
            initialOffsetX = { 300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Registration") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("login") }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create an Account",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = userId,
                        onValueChange = {
                            userId = it
                            validateUserId(it)
                        },
                        label = { Text("User ID") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User ID") },
                        isError = userIdError != null,
                        supportingText = { userIdError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            validatePassword(it)
                            showRequirements = !passwordRequirements.all { it.value }
                            validateConfirmPassword(password, confirmPassword)
                        },
                        label = { Text("Password") },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isPasswordFocused = focusState.isFocused
                                showRequirements = isPasswordFocused || !passwordRequirements.all { it.value }
                            },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        isError = !passwordRequirements.all { it.value },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        enabled = !isLoading
                    )
                    AnimatedVisibility(
                        visible = showRequirements,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                            passwordRequirements.forEach { (requirement, isMet) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isMet) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = if (isMet) "Requirement met" else "Requirement not met",
                                        tint = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        requirement,
                                        color = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            validateConfirmPassword(password, it)
                        },
                        label = { Text("Confirm Password") },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        isError = confirmPasswordError != null,
                        supportingText = { confirmPasswordError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = preferredName,
                        onValueChange = {
                            preferredName = it
                            validatePreferredName(it)
                        },
                        label = { Text("Preferred Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Preferred Name") },
                        isError = preferredNameError != null,
                        supportingText = { preferredNameError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        enabled = !isLoading
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (userId.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && preferredName.isNotBlank() &&
                                    validateUserId(userId) && validatePassword(password) && validateConfirmPassword(password, confirmPassword) && validatePreferredName(preferredName)) {

                                    isLoading = true
                                    if (checkUserIdExists(userId)) {
                                        userIdError = "User ID already exists"
                                        snackbarHostState.showSnackbar(
                                            message = "User ID already exists",
                                            duration = SnackbarDuration.Short,
                                            withDismissAction = true
                                        )
                                        isLoading = false
                                    } else {
                                        viewModel.registerUser(userId, password, preferredName)
                                        snackbarHostState.showSnackbar(
                                            message = "Account successfully created",
                                            duration = SnackbarDuration.Short,
                                            withDismissAction = true
                                        )
                                        navController.navigate("login")
                                    }
                                } else {
                                    // Show error message for empty fields
                                    if (userId.isBlank()) userIdError = "User ID is required"
                                    if (password.isBlank()) showRequirements = true
                                    if (confirmPassword.isBlank()) confirmPasswordError = "Confirm password is required"
                                    if (preferredName.isBlank()) preferredNameError = "Preferred name is required"
                                    snackbarHostState.showSnackbar(
                                        message = "Please fill all fields correctly",
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isOffline && userIdError == null && passwordRequirements.all { it.value } && confirmPasswordError == null && preferredNameError == null
                    ) {
                        Text("Register")
                    }

                    if (isOffline) {
                        Text(
                            "Offline mode: Registration is unavailable",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(viewModel: MovieViewModel, navController: NavController) {
    val movies by viewModel.movies.collectAsState()
    var selectedCategory by remember { mutableStateOf("Popular") }
    val isOffline by viewModel.isOffline.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    // Snackbar state and coroutine scope for notifications
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedCategory, isOffline) {
        when (selectedCategory) {
            "Popular" -> viewModel.getPopularMovies()
            "Top Rated" -> viewModel.getTopRatedMovies()
            "Now Playing" -> viewModel.getNowPlayingMovies()
            "Upcoming" -> viewModel.getUpcomingMovies()
        }
    }

    val exitTransition = remember {
        slideOutHorizontally(
            targetOffsetX = { -300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    }
    val enterTransition = remember {
        slideInHorizontally(
            initialOffsetX = { 300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movies", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { navController.navigate("search") }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                CategorySelector(selectedCategory) { newCategory ->
                    selectedCategory = newCategory
                }

                AnimatedVisibility(visible = isOffline) {
                    Text(
                        "Offline mode: Showing cached movies",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        ErrorMessage(error!!)
                    }
                    movies.isEmpty() -> {
                        EmptyState("No movies found")
                    }
                    else -> {
                        LazyColumn {
                            items(movies) { movie ->
                                // Check if this movie is in favorites
                                val isFav = favoriteMovies.any { it.movieId == movie.id }
                                MovieListItem(
                                    movie = movie,
                                    isFavorite = isFav,
                                    onFavoriteClick = {
                                        if (isFav) {
                                            viewModel.removeFavoriteMovie(movie.id)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Removed movie from favorites")
                                            }
                                        } else {
                                            viewModel.addFavoriteMovie(movie)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added movie to favorites")
                                            }
                                        }
                                    },
                                    onClick = {
                                        navController.navigate("movieDetail/${movie.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieListItem(
    movie: Movie,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.poster_path?.let { "https://image.tmdb.org/t/p/w185$it" },
                contentDescription = movie.title,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.release_date ?: "Release date unknown",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vote Average: ${movie.vote_average?.toString() ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
fun CategorySelector(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("Popular", "Top Rated", "Now Playing", "Upcoming")
    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        edgePadding = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEach { category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                text = { Text(category) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(viewModel: MovieViewModel, movieId: Int, navController: NavController) {
    val movieDetails by viewModel.movieDetails.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val similarMovies by viewModel.similarMovies.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    // Snackbar state and coroutine scope for showing notifications
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId)
        if (!isOffline) {
            viewModel.getMovieReviews(movieId)
            viewModel.getSimilarMovies(movieId)
        }
        viewModel.checkIfFavorite(movieId)
    }

    val exitTransition = remember {
        slideOutHorizontally(
            targetOffsetX = { -300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    }
    val enterTransition = remember {
        slideInHorizontally(
            initialOffsetX = { 300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Movie title
                            Text(
                                text = movieDetails?.title ?: "Movie Details",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold
                            )
                            // Favorite icon button beside the title
                            IconButton(onClick = {
                                movieDetails?.let { movie ->
                                    if (isFavorite) {
                                        viewModel.removeFavoriteMovie(movie.id)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Removed movie from favorites")
                                        }
                                    } else {
                                        viewModel.addFavoriteMovie(movie)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Added movie to favorites")
                                        }
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = Color(0xFFF44336)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = MaterialTheme.colorScheme.onSecondary,
                        actionIconContentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                movieDetails?.let { movie ->
                    AsyncImage(
                        model = movie.poster_path?.let { "https://image.tmdb.org/t/p/original$it" },
                        contentDescription = movie.title,
                        modifier = Modifier
                            .height(400.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MovieInfoItem("Release Date", movie.release_date)
                        MovieInfoItem("Vote Average", movie.vote_average?.toString())
                        MovieInfoItem("Adult", movie.adult?.toString())
                        MovieInfoItem("Runtime", movie.runtime?.let { "$it minutes" })
                        MovieInfoItem("Vote Count", movie.vote_count?.toString())
                        MovieInfoItem("Revenue", movie.revenue?.let { "$it USD" })
                        MovieInfoItem("Original Language", movie.original_language)

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Overview", style = MaterialTheme.typography.titleMedium)
                        Text(
                            movie.overview ?: "No overview available",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        movie.genres?.let { genres ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Genres", style = MaterialTheme.typography.titleMedium)
                            Text(
                                genres.joinToString { it.name },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (!isOffline) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Reviews", style = MaterialTheme.typography.titleLarge)
                            if (reviews.isEmpty()) {
                                Text("No reviews available", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                reviews.forEach { review ->
                                    ReviewItem(review)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Similar Movies", style = MaterialTheme.typography.titleLarge)
                            if (similarMovies.isEmpty()) {
                                Text("No similar movies found", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    items(similarMovies) { similarMovie ->
                                        SimilarMovieItem(similarMovie) {
                                            navController.navigate("movieDetail/${similarMovie.id}")
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                "Reviews and similar movies unavailable in offline mode",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                } ?: run {
                    EmptyState("Movie details not available")
                }
            }
        }
    }
}

@Composable
fun MovieInfoItem(label: String, value: String?) {
    value?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = review.author, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))

            if (review.content.length <= 200 || expanded) {
                Text(
                    text = review.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                val annotatedText = buildAnnotatedString {
                    append(review.content.take(200))
                    append("... ")
                    pushStringAnnotation(tag = "ViewMore", annotation = "View More")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("View More")
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(tag = "ViewMore", start = offset, end = offset)
                            .firstOrNull()?.let {
                                expanded = true
                            }
                    }
                )
            }

            AnimatedVisibility(visible = expanded && review.content.length > 200) {
                val annotatedText = buildAnnotatedString {
                    pushStringAnnotation(tag = "ViewLess", annotation = "View Less")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("View Less")
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(tag = "ViewLess", start = offset, end = offset)
                            .firstOrNull()?.let {
                                expanded = false
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun SimilarMovieItem(movie: Movie, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = movie.poster_path?.let { "https://image.tmdb.org/t/p/w185$it" },
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MovieViewModel, navController: NavController) {
    val currentUser by viewModel.currentUser.collectAsState()

    val exitTransition = remember {
        slideOutHorizontally(
            targetOffsetX = { -300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    }
    val enterTransition = remember {
        slideInHorizontally(
            initialOffsetX = { 300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentUser?.let { user ->
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(user.userId, style = MaterialTheme.typography.headlineMedium)
                    Text("Preferred Name: ${user.preferredName}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("favorites") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Favorites")
                    }
                    Button(
                        onClick = {
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo("movieList") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Logout")
                    }
                } ?: run {
                    Text("Not logged in", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Login")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteMoviesScreen(viewModel: MovieViewModel, navController: NavController) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val exitTransition = remember {
        slideOutHorizontally(
            targetOffsetX = { -300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    }
    val enterTransition = remember {
        slideInHorizontally(
            initialOffsetX = { 300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Favorite Movies", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            if (favoriteMovies.isEmpty()) {
                EmptyState("No favorite movies yet")
            } else {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(favoriteMovies) { fav ->
                        MovieListItem(
                            movie = Movie(
                                id = fav.movieId,
                                title = fav.title,
                                poster_path = fav.poster_path,
                                release_date = fav.release_date,
                                vote_average = fav.vote_average,
                                overview = fav.overview,
                                adult = fav.adult,
                                genres = fav.genres,
                                original_language = fav.original_language,
                                runtime = fav.runtime,
                                vote_count = fav.vote_count,
                                revenue = fav.revenue
                            ),
                            isFavorite = true,
                            onFavoriteClick = {
                                viewModel.removeFavoriteMovie(fav.movieId)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Removed movie from favorites")
                                }
                            },
                            onClick = { navController.navigate("movieDetail/${fav.movieId}") }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MovieViewModel, navController: NavController) {
    val searchResults by viewModel.movies.collectAsState()
    var query by remember { mutableStateOf("") }
    val isOffline by viewModel.isOffline.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val exitTransition = remember {
        slideOutHorizontally(
            targetOffsetX = { -300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    }
    val enterTransition = remember {
        slideInHorizontally(
            initialOffsetX = { 300 },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300))
    }

    AnimatedVisibility(
        visible = true,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Search Movies", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    windowInsets = WindowInsets(0.dp)
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search movies") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.searchMovies(query) },
                            enabled = !isOffline
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )

                if (isOffline) {
                    Text(
                        "Offline mode: Searching in cached movies",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    searchResults.isEmpty() -> {
                        EmptyState("No results found")
                    }
                    else -> {
                        LazyColumn {
                            items(searchResults) { movie ->
                                val isFav = favoriteMovies.any { it.movieId == movie.id }
                                MovieListItem(
                                    movie = movie,
                                    isFavorite = isFav,
                                    onFavoriteClick = {
                                        if (isFav) {
                                            viewModel.removeFavoriteMovie(movie.id)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Removed movie from favorites")
                                            }
                                        } else {
                                            viewModel.addFavoriteMovie(movie)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added movie to favorites")
                                            }
                                        }
                                    },
                                    onClick = { navController.navigate("movieDetail/${movie.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

