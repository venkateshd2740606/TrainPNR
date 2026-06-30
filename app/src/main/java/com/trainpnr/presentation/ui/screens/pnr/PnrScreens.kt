package com.trainpnr.presentation.ui.screens.pnr

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trainpnr.domain.model.SavedPnr
import com.trainpnr.engine.PnrEngine
import com.trainpnr.presentation.viewmodel.TrainPNRViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
private const val IRCTC_URL = "https://www.irctc.co.in/nget/train-search"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckPnrScreen(
    onShowStatus: () -> Unit,
    viewModel: TrainPNRViewModel = hiltViewModel()
) {
    val pnrInput by viewModel.pnrInput.collectAsStateWithLifecycle()
    val pasteInput by viewModel.pasteInput.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var nickname by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar({ Text("Check PNR") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Enter your 10-digit Indian Railways PNR to save it and learn how to check live status.",
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                value = pnrInput,
                onValueChange = { viewModel.updatePnrInput(it) },
                label = { Text("PNR number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = { Text("${pnrInput.length}/10 digits") }
            )
            Text("Paste SMS (optional)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = pasteInput,
                onValueChange = { viewModel.updatePasteInput(it) },
                label = { Text("Paste booking SMS") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            OutlinedButton(onClick = { viewModel.applyPaste() }, modifier = Modifier.fillMaxWidth()) {
                Text("Extract PNR from SMS")
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = { if (viewModel.checkPnr()) onShowStatus() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Check PNR") }
            OutlinedButton(
                onClick = { viewModel.saveCurrent(nickname) },
                modifier = Modifier.fillMaxWidth(),
                enabled = PnrEngine.isValid(pnrInput)
            ) { Text("Save to favorites") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PnrStatusScreen(onBack: () -> Unit, viewModel: TrainPNRViewModel = hiltViewModel()) {
    val pnr = viewModel.checkedPnr.collectAsStateWithLifecycle().value ?: run {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PNR $pnr") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Educational status guide", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "This app does not connect to IRCTC. Use the steps below to check live status on official channels.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Text("How to check on IRCTC / NTES", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            PnrEngine.checkSteps.forEachIndexed { i, step ->
                Text("${i + 1}. $step", style = MaterialTheme.typography.bodyLarge)
            }
            Text("Sample status codes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            PnrEngine.statusGuide.forEach { (code, desc) ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(desc, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Button(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(IRCTC_URL)))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.OpenInBrowser, null)
                Spacer(Modifier.width(8.dp))
                Text("Open IRCTC")
            }
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://enquiry.indianrail.gov.in/ntes/")))
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Open NTES") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPnrScreen(
    onOpenPnr: (String) -> Unit,
    viewModel: TrainPNRViewModel = hiltViewModel()
) {
    val saved by viewModel.savedPnrs.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar({ Text("Saved PNRs") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (saved.isEmpty()) {
                item { Text("No saved PNRs yet. Check a PNR and tap Save to favorites.") }
            }
            items(saved, key = { it.pnr }) { entry ->
                SavedPnrCard(
                    entry,
                    onOpen = {
                        viewModel.loadSaved(entry.pnr)
                        onOpenPnr(entry.pnr)
                    },
                    onDelete = { viewModel.deleteSaved(entry.pnr) }
                )
            }
        }
    }
}

@Composable
private fun SavedPnrCard(entry: SavedPnr, onOpen: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(entry.nickname, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("PNR ${entry.pnr}", style = MaterialTheme.typography.bodyLarge)
                Text("Saved ${dateFmt.format(Date(entry.savedAt))}", style = MaterialTheme.typography.bodySmall)
                entry.lastStatusText?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PnrGuideScreen() {
    Scaffold(topBar = { TopAppBar({ Text("PNR Guide") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("What is a PNR?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "PNR (Passenger Name Record) is a unique 10-digit number on your train ticket. " +
                        "It is used to check booking status, coach, and berth details.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                Text("Where to find it", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Printed ticket, IRCTC e-ticket PDF, confirmation SMS, or email from IRCTC.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                Text("Official status check", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(PnrEngine.checkSteps.withIndex().toList()) { (i, step) ->
                Text("${i + 1}. $step", style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Text("Status abbreviations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(PnrEngine.statusGuide) { (code, desc) ->
                Text("$code — $desc", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PnrSettingsScreen(viewModel: TrainPNRViewModel = hiltViewModel()) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar({ Text("Settings") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Show ads", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                Switch(checked = prefs.adsEnabled, onCheckedChange = { viewModel.setAds(it) })
            }
            Text(
                "TrainPNR stores saved PNRs locally on your device. Live status requires IRCTC or NTES.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
