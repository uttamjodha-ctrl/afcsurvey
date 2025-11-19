package com.company.attendancetracker.ui.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.company.attendancetracker.utils.LocationPermissionHelper

/**
 * Home Screen UI.
 *
 * Shows:
 * - Employee profile
 * - Punch IN/OUT buttons
 * - Current attendance status
 * - Location logs list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogout: () -> Unit
) {
    val employee by viewModel.employee.collectAsState()
    val activeAttendance by viewModel.activeAttendance.collectAsState()
    val locationLogs by viewModel.locationLogs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showBackgroundPermissionDialog by remember { mutableStateOf(false) }

    // Permission launchers
    val foregroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            // Check if we need background permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!LocationPermissionHelper.hasBackgroundLocationPermission(context)) {
                    showBackgroundPermissionDialog = true
                } else {
                    viewModel.punchIn()
                }
            } else {
                viewModel.punchIn()
            }
        }
    }

    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.punchIn()
        } else {
            // User denied, but we can still track in foreground
            viewModel.punchIn()
        }
    }

    // Handle logout
    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.LoggedOut) {
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Tracker") },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() }) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Employee Profile Card
            item {
                EmployeeProfileCard(
                    employee = employee,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Attendance Status Card
            item {
                AttendanceStatusCard(
                    activeAttendance = activeAttendance,
                    viewModel = viewModel,
                    onPunchIn = {
                        // Check permissions before punch in
                        if (!LocationPermissionHelper.hasForegroundLocationPermission(context)) {
                            showPermissionDialog = true
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                            !LocationPermissionHelper.hasBackgroundLocationPermission(context)
                        ) {
                            showBackgroundPermissionDialog = true
                        } else {
                            viewModel.punchIn()
                        }
                    },
                    onPunchOut = { viewModel.punchOut() },
                    isLoading = uiState is HomeUiState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Location Logs Section
            if (activeAttendance != null) {
                item {
                    Text(
                        text = "Location Logs (${locationLogs.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (locationLogs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = "No location logs yet. Location will be tracked periodically.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(locationLogs) { log ->
                        LocationLogItem(
                            log = log,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Show message/error
            item {
                when (val state = uiState) {
                    is HomeUiState.Success -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(2000)
                            viewModel.resetState()
                        }
                    }

                    is HomeUiState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    // Permission dialogs
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Location Permission Required") },
            text = { Text(LocationPermissionHelper.getForegroundLocationRationaleMessage()) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        foregroundPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBackgroundPermissionDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        AlertDialog(
            onDismissRequest = { showBackgroundPermissionDialog = false },
            title = { Text("Background Location Permission") },
            text = { Text(LocationPermissionHelper.getBackgroundLocationRationaleMessage()) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackgroundPermissionDialog = false
                        backgroundPermissionLauncher.launch(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBackgroundPermissionDialog = false
                        // Continue without background permission
                        viewModel.punchIn()
                    }
                ) {
                    Text("Skip")
                }
            }
        )
    }
}

@Composable
private fun EmployeeProfileCard(
    employee: com.company.attendancetracker.data.local.entity.Employee?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = employee?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = employee?.employeeId ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Divider()

            employee?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoItem(
                        icon = Icons.Default.Business,
                        label = "Department",
                        value = it.department.ifEmpty { "N/A" }
                    )
                    InfoItem(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = it.phone.ifEmpty { "N/A" }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AttendanceStatusCard(
    activeAttendance: com.company.attendancetracker.data.local.entity.Attendance?,
    viewModel: HomeViewModel,
    onPunchIn: () -> Unit,
    onPunchOut: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (activeAttendance != null)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's Attendance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (activeAttendance != null) {
                // Active session
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Column {
                        Text(
                            text = "Punched IN at",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = viewModel.formatTime(activeAttendance.punchInTime),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = onPunchOut,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Punch OUT")
                }
            } else {
                // Not punched in
                Text(
                    text = "Not punched in yet",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = onPunchIn,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Punch IN")
                }
            }
        }
    }
}

@Composable
private fun LocationLogItem(
    log: com.company.attendancetracker.data.local.entity.LocationLog,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = viewModel.formatTime(log.timestamp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lat: ${String.format("%.6f", log.latitude)}, " +
                            "Lng: ${String.format("%.6f", log.longitude)}",
                    style = MaterialTheme.typography.bodySmall
                )
                log.accuracy?.let {
                    Text(
                        text = "Accuracy: ${String.format("%.0f", it)}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
