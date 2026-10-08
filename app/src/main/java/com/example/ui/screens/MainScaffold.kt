package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val activeNews by viewModel.activeNews.collectAsState()

    var showSpinWheel by remember { mutableStateOf(false) }

    // Trigger Spin Wheel on login if welcome spin has not been claimed yet
    LaunchedEffect(currentUser) {
        if (currentUser != null && !currentUser!!.hasClaimedSpin) {
            showSpinWheel = true
        }
    }

    // Handle system back navigation
    BackHandler(enabled = currentScreen != AppScreen.Jobs || drawerState.isOpen) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (currentScreen != AppScreen.Jobs) {
            viewModel.navigateTo(AppScreen.Jobs)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                // Drawer Header
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.siam_nexa_icon),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize().padding(2.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column {
                                Text(
                                    text = currentUser?.name ?: "SIAM NEXA",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "@${currentUser?.username ?: "user"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                                if (currentUser?.isAdmin == true) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFF59E0B),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = "ADMINISTRATOR",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Balance Chip in Drawer
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ব্যালেন্স:",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "৳ ${String.format("%.2f", currentUser?.balance ?: 0.0)} BDT",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Drawer Nav Items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Work, contentDescription = null) },
                    label = { Text("অনলাইনে কাজ / মাইক্রো জব") },
                    selected = currentScreen == AppScreen.Jobs,
                    onClick = {
                        viewModel.navigateTo(AppScreen.Jobs)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                    label = { Text("জব পোস্ট করুন (Post Job)") },
                    selected = currentScreen == AppScreen.PostJob,
                    onClick = {
                        viewModel.navigateTo(AppScreen.PostJob)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    label = { Text("ডিপোজিট করুন (Deposit)") },
                    selected = currentScreen == AppScreen.Deposit,
                    onClick = {
                        viewModel.navigateTo(AppScreen.Deposit)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    label = { Text("উইথড্র করুন (১২০ টাকা মিনিমাম)") },
                    selected = currentScreen == AppScreen.Withdraw,
                    onClick = {
                        viewModel.navigateTo(AppScreen.Withdraw)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Share, contentDescription = null) },
                    label = { Text("রেফার ও আয় (প্রতি রেফারে ৫ টাকা)") },
                    selected = currentScreen == AppScreen.Referrals,
                    onClick = {
                        viewModel.navigateTo(AppScreen.Referrals)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Casino, contentDescription = null, tint = Color(0xFFD97706)) },
                    label = { Text("ওয়েলকাম স্পিন (৫ টাকা ফ্রি)") },
                    selected = false,
                    onClick = {
                        showSpinWheel = true
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null) },
                    label = { Text("আমার কাজ ও ইতিহাস (My Tasks)") },
                    selected = currentScreen == AppScreen.MyTasks,
                    onClick = {
                        viewModel.navigateTo(AppScreen.MyTasks)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null) },
                    label = { Text("আজকের আপডেট নিউজ দেখুন") },
                    selected = currentScreen == AppScreen.News,
                    onClick = {
                        viewModel.navigateTo(AppScreen.News)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                if (currentUser?.isAdmin == true) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = { Text("এডমিন প্যানেল (Admin Panel)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                        selected = currentScreen == AppScreen.AdminPanel,
                        onClick = {
                            viewModel.navigateTo(AppScreen.AdminPanel)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()

                // Logout
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    label = { Text("লগআউট (Logout)", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        coroutineScope.launch { drawerState.close() }
                        Toast.makeText(context, "লগআউট করা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.siam_nexa_icon),
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Text(
                                text = "SIAM NEXA",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { coroutineScope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Spin Gift Button
                        IconButton(
                            onClick = { showSpinWheel = true },
                            modifier = Modifier.testTag("header_spin_wheel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Spin Wheel",
                                tint = Color(0xFFD97706)
                            )
                        }

                        // Balance Chip
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { viewModel.navigateTo(AppScreen.Withdraw) }
                                .testTag("header_balance_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Balance",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "৳ ${String.format("%.2f", currentUser?.balance ?: 0.0)}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        // Hidden/Direct Admin Gateway Icon
                        if (currentUser?.isAdmin == true) {
                            IconButton(
                                onClick = { viewModel.navigateTo(AppScreen.AdminPanel) },
                                modifier = Modifier.testTag("header_admin_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Panel",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.Jobs,
                        onClick = { viewModel.navigateTo(AppScreen.Jobs) },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Jobs") },
                        label = { Text("কাজ (Jobs)", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PostJob,
                        onClick = { viewModel.navigateTo(AppScreen.PostJob) },
                        icon = { Icon(Icons.Default.PostAdd, contentDescription = "Post") },
                        label = { Text("পোস্ট করুন", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.Deposit,
                        onClick = { viewModel.navigateTo(AppScreen.Deposit) },
                        icon = { Icon(Icons.Default.Payments, contentDescription = "Deposit") },
                        label = { Text("ডিপোজিট", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.Withdraw,
                        onClick = { viewModel.navigateTo(AppScreen.Withdraw) },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Withdraw") },
                        label = { Text("উইথড্র", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.Referrals,
                        onClick = { viewModel.navigateTo(AppScreen.Referrals) },
                        icon = { Icon(Icons.Default.Share, contentDescription = "Refer") },
                        label = { Text("রেফার", fontSize = 11.sp) }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Fixed Top Banner Ad (Adsterra Iframe)
                AdsterraBannerAd()

                // Horizontal Scrolling News Ticker
                NewsTickerView(
                    newsList = activeNews,
                    onViewAllClick = { viewModel.navigateTo(AppScreen.News) }
                )

                // Live Floating Withdrawal & Deposit Toast / Activity Ticker
                LiveTransactionTicker()

                // Screen Switcher
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (currentScreen) {
                        is AppScreen.Jobs -> JobsScreen(viewModel)
                        is AppScreen.PostJob -> PostJobScreen(viewModel)
                        is AppScreen.Deposit -> DepositScreen(viewModel)
                        is AppScreen.Withdraw -> WithdrawScreen(viewModel)
                        is AppScreen.MyTasks -> MyTasksScreen(viewModel)
                        is AppScreen.News -> NewsScreen(viewModel)
                        is AppScreen.Referrals -> ReferralScreen(viewModel)
                        is AppScreen.AdminPanel -> AdminPanelScreen(viewModel)
                        else -> JobsScreen(viewModel)
                    }
                }
            }
        }
    }

    // Welcome Bonus Spin Wheel Dialog
    if (showSpinWheel) {
        SpinWheelDialog(
            onDismiss = { showSpinWheel = false },
            onRewardClaimed = { amount ->
                viewModel.claimSpinBonus(amount)
                Toast.makeText(context, "৳${amount.toInt()} টাকা আপনার একাউন্টে যোগ হয়েছে!", Toast.LENGTH_LONG).show()
            }
        )
    }
}
