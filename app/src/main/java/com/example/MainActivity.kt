package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.repository.AgencyRepository
import com.example.ui.screens.admin.AdminMainScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.client.ClientMainNavigation
import com.example.ui.screens.client.ClientProjectDetailScreen
import com.example.ui.theme.MyAgencyTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: AgencyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = AgencyRepository(applicationContext)

        setContent {
            val isDarkMode by repository.isDarkMode.collectAsState()

            MyAgencyTheme(darkTheme = isDarkMode) {
                // Mandatory Arabic RTL Layout Direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MyAgencyAppRoot(repository = repository)
                    }
                }
            }
        }
    }
}

@Composable
fun MyAgencyAppRoot(repository: AgencyRepository) {
    val currentUser by repository.currentUser.collectAsState()
    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var forceAdminView by remember { mutableStateOf(false) }

    when {
        forceAdminView || (currentUser?.isAdmin == true) -> {
            AdminMainScreen(
                repository = repository,
                onExitAdmin = {
                    forceAdminView = false
                    if (currentUser?.isAdmin == true) {
                        repository.logout()
                    }
                }
            )
        }
        currentUser != null -> {
            val user = currentUser!!
            if (selectedProjectId != null) {
                ClientProjectDetailScreen(
                    projectId = selectedProjectId!!,
                    repository = repository,
                    onBack = { selectedProjectId = null }
                )
            } else {
                ClientMainNavigation(
                    user = user,
                    repository = repository,
                    onOpenProject = { id -> selectedProjectId = id },
                    onLogout = {
                        selectedProjectId = null
                        repository.logout()
                    }
                )
            }
        }
        else -> {
            AuthScreen(
                repository = repository,
                onAuthSuccess = {
                    selectedProjectId = null
                },
                onAdminLogin = {
                    forceAdminView = true
                }
            )
        }
    }
}
