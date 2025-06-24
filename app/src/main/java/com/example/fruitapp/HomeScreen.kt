package com.example.fruitapp

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// --------- ViewModel ---------
class RecordViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).recordDao()
    val records = dao.getAllRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addRecord(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val record = Record(timestamp = now, message = message)
            withContext(Dispatchers.IO) {
                dao.insert(record)
            }
        }
    }
}


data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val drawerItems = listOf(
    DrawerItem(title = "Home", icon = Icons.Filled.Home, route = "home"),
    DrawerItem(title = "Profile", icon = Icons.Filled.Person, route = "profile"),
    DrawerItem(title = "Settings", icon = Icons.Filled.Settings, route = "settings")
)

// --------- HomeScreen ---------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedRoute by remember { mutableStateOf("home") }

    // Obtain ViewModel
    val context = LocalContext.current.applicationContext as Application
    val viewModel: RecordViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "水果辨識應用",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        selected = selectedRoute == item.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            selectedRoute = item.route
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (selectedRoute) {
                                "home" -> "水果辨識"
                                "profile" -> "個人資料"
                                "settings" -> "設定"
                                else -> "水果辨識應用"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "開啟選單")
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedRoute) {
                    "home" -> TakePhotoScreen(viewModel)
                    "profile" -> ProfileScreenContent(viewModel)
                    "settings" -> SettingsScreenContent()
                }
            }
        }
    }
}

// --------- Profile Screen ---------
@Composable
fun ProfileScreenContent(viewModel: RecordViewModel) {
    val records by viewModel.records.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            "辨識紀錄",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (records.isEmpty()) {
            Text("目前尚無紀錄", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn {
                items(records) { record ->
                    val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date(record.timestamp))
                    Text("$time：${record.message}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// --------- Settings Screen ---------
@Composable
fun SettingsScreenContent() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            "設定",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("此處可以進行應用程式的相關設定")
    }
}