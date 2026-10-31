package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProSubscriptionDialog
import com.example.ui.screens.ReelEditorScreen
import com.example.ui.screens.SavedReelsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ReelMagenta
import com.example.ui.theme.ReelOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.ReelViewModel

enum class MainNavTab(val label: String) {
    HOME("Studio"),
    EDITOR("Editor"),
    TEMPLATES("Templates"),
    PROJECTS("Projects")
}

class MainActivity : ComponentActivity() {
    private val viewModel: ReelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                ReelMakerApp(viewModel)
            }
        }
    }
}

@Composable
fun ReelMakerApp(viewModel: ReelViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedReels by viewModel.savedReels.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(MainNavTab.HOME) }
    var showProDialog by remember { mutableStateOf(false) }

    // Android Hardware back button handler
    BackHandler(enabled = currentTab != MainNavTab.HOME) {
        currentTab = MainNavTab.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DarkBg,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(
                        width = 1.dp,
                        color = DarkCardBorder,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                // Studio Tab
                NavigationBarItem(
                    selected = currentTab == MainNavTab.HOME,
                    onClick = { currentTab = MainNavTab.HOME },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Studio",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Studio",
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == MainNavTab.HOME) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ReelOrange,
                        selectedTextColor = ReelOrange,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = ReelMagenta.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_studio")
                )

                // Reel Editor Tab
                NavigationBarItem(
                    selected = currentTab == MainNavTab.EDITOR,
                    onClick = { currentTab = MainNavTab.EDITOR },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.EDITOR) Icons.Filled.Videocam else Icons.Outlined.Videocam,
                            contentDescription = "Editor",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Editor",
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == MainNavTab.EDITOR) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ReelOrange,
                        selectedTextColor = ReelOrange,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = ReelMagenta.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_editor")
                )

                // Templates Tab
                NavigationBarItem(
                    selected = currentTab == MainNavTab.TEMPLATES,
                    onClick = { currentTab = MainNavTab.TEMPLATES },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.TEMPLATES) Icons.Filled.ViewCarousel else Icons.Outlined.ViewCarousel,
                            contentDescription = "Templates",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Templates",
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == MainNavTab.TEMPLATES) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ReelOrange,
                        selectedTextColor = ReelOrange,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = ReelMagenta.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_templates")
                )

                // Projects Tab
                NavigationBarItem(
                    selected = currentTab == MainNavTab.PROJECTS,
                    onClick = { currentTab = MainNavTab.PROJECTS },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.PROJECTS) Icons.Filled.FolderSpecial else Icons.Outlined.FolderSpecial,
                            contentDescription = "Projects",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Projects",
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == MainNavTab.PROJECTS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ReelOrange,
                        selectedTextColor = ReelOrange,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = ReelMagenta.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_projects")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "main_screen_transition"
            ) { tab ->
                when (tab) {
                    MainNavTab.HOME -> {
                        HomeScreen(
                            isPro = uiState.isProSubscriber,
                            savedReels = savedReels,
                            onOpenEditor = { currentTab = MainNavTab.EDITOR },
                            onMediaSelected = { uris -> viewModel.addClipsFromUris(uris) },
                            onLoadSampleStock = { viewModel.loadSampleStockClips() },
                            onTemplateSelect = { tmpl ->
                                viewModel.applyTemplate(tmpl)
                                currentTab = MainNavTab.EDITOR
                            },
                            onOpenProModal = { showProDialog = true },
                            onOpenTemplatesTab = { currentTab = MainNavTab.TEMPLATES }
                        )
                    }

                    MainNavTab.EDITOR -> {
                        ReelEditorScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onNavigateBack = { currentTab = MainNavTab.HOME },
                            onOpenProModal = { showProDialog = true }
                        )
                    }

                    MainNavTab.TEMPLATES -> {
                        TemplatesScreen(
                            isPro = uiState.isProSubscriber,
                            onTemplateSelect = { tmpl ->
                                viewModel.applyTemplate(tmpl)
                                currentTab = MainNavTab.EDITOR
                            },
                            onProUpgradeClick = { showProDialog = true }
                        )
                    }

                    MainNavTab.PROJECTS -> {
                        SavedReelsScreen(
                            savedReels = savedReels,
                            onDeleteReel = { id -> viewModel.deleteSavedReel(id) },
                            onCreateNewClick = { currentTab = MainNavTab.HOME }
                        )
                    }
                }
            }
        }
    }

    // Pro Subscription Paywall Dialog
    ProSubscriptionDialog(
        isOpen = showProDialog,
        isAlreadyPro = uiState.isProSubscriber,
        onDismiss = { showProDialog = false },
        onUpgradeConfirmed = {
            viewModel.upgradeToPro()
        }
    )
}
