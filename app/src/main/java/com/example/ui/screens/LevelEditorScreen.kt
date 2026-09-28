package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.Direction
import com.example.game.LevelData
import com.example.ui.components.DpadControls
import com.example.ui.components.GameBoard
import com.example.viewmodel.EditorTool
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun LevelEditorScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showExportDialog by remember { mutableStateOf(false) }
  var showImportDialog by remember { mutableStateOf(false) }
  var importText by remember { mutableStateOf("") }
  var levelNameInput by remember { mutableStateOf("My Custom Sector") }
  var showSaveSuccess by remember { mutableStateOf(false) }

  val editorLevel = remember(
    viewModel.editorWidth,
    viewModel.editorHeight,
    viewModel.editorWalls,
    viewModel.editorTargets,
    viewModel.editorOrbs,
    viewModel.editorSwitches,
    viewModel.editorGates,
    viewModel.editorPortals
  ) {
    viewModel.buildEditorLevel(levelNameInput)
  }

  BackHandler {
    if (viewModel.isTestingPlay) {
      viewModel.toggleTestPlay()
    } else {
      viewModel.navigateTo(GameScreen.MENU)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF090D16))
  ) {
    // Header Row
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = {
            if (viewModel.isTestingPlay) viewModel.toggleTestPlay()
            else viewModel.navigateTo(GameScreen.MENU)
          },
          modifier = Modifier.testTag("editor_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text(
            text = if (viewModel.isTestingPlay) "TESTING LEVEL" else "LEVEL WORKSHOP",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (viewModel.isTestingPlay) Color(0xFF10B981) else Color.White
          )
          Text(
            text = "${viewModel.editorWidth}x${viewModel.editorHeight} Grid • ${viewModel.editorOrbs.size} Orbs • ${viewModel.editorTargets.size} Targets",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )
        }
      }

      // Actions: Test Play, Validate, Export/Import
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Test Play Toggle
        Button(
          onClick = { viewModel.toggleTestPlay() },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (viewModel.isTestingPlay) Color(0xFFEF4444) else Color(0xFF0284C7)
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("editor_test_play_button")
        ) {
          Icon(
            imageVector = if (viewModel.isTestingPlay) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (viewModel.isTestingPlay) "EDIT" else "TEST")
        }
      }
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp)
        .widthIn(max = 560.dp)
    ) {
      // Game Board
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
      ) {
        val activeOrbs = if (viewModel.isTestingPlay) {
          viewModel.testGameEngine?.currentOrbs ?: editorLevel.initialOrbs
        } else {
          editorLevel.initialOrbs
        }
        val activeGates = if (viewModel.isTestingPlay) {
          viewModel.testGameEngine?.gateStates ?: editorLevel.gates.associate { it.gateId to it.isInitiallyClosed }
        } else {
          editorLevel.gates.associate { it.gateId to it.isInitiallyClosed }
        }

        GameBoard(
          level = editorLevel,
          orbs = activeOrbs,
          gateStates = activeGates,
          swipeSensitivity = viewModel.config.swipeSensitivity,
          onMove = { dir ->
            if (viewModel.isTestingPlay) {
              viewModel.makeEditorTestMove(dir)
            }
          },
          onTileClick = { pos ->
            if (!viewModel.isTestingPlay) {
              viewModel.handleEditorTileClick(pos)
            }
          }
        )
      }

      // If testing play, show D-Pad for testing
      if (viewModel.isTestingPlay) {
        DpadControls(
          onDirection = { dir -> viewModel.makeEditorTestMove(dir) },
          modifier = Modifier.padding(vertical = 12.dp)
        )
      } else {
        // EDITOR CONTROLS & PALETTES

        // Grid Size Buttons
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
        ) {
          Text("Size:", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
          for (size in 4..8) {
            val isCurrentSize = viewModel.editorWidth == size
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrentSize) Color(0xFF0284C7) else Color(0xFF1E293B))
                .clickable {
                  viewModel.resetEditorGrid(size)
                  viewModel.soundManager.playButtonClick()
                }
            ) {
              Text("${size}x$size", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          Spacer(modifier = Modifier.weight(1f))

          IconButton(
            onClick = { viewModel.resetEditorGrid(viewModel.editorWidth) },
            modifier = Modifier.size(34.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Clear Grid", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
          }
        }

        // Palette Selector
        Text(
          text = "PALETTE: ${viewModel.selectedTool.label}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF38BDF8),
          modifier = Modifier
            .align(Alignment.Start)
            .padding(top = 4.dp, bottom = 6.dp)
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(bottom = 12.dp)
        ) {
          EditorTool.entries.forEach { tool ->
            val isSelected = viewModel.selectedTool == tool
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B))
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
                  shape = RoundedCornerShape(10.dp)
                )
                .clickable { viewModel.selectEditorTool(tool) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("tool_${tool.name}")
            ) {
              Text(
                text = tool.label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color(0xFF94A3B8)
              )
            }
          }
        }

        // Board Solvability Validator & Auto-Solver Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color(0xFFFBBF24),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "SOLVABILITY VALIDATOR",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color.White
                )
              }

              Button(
                onClick = { viewModel.validateEditorBoard() },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                modifier = Modifier.testTag("editor_validate_button")
              ) {
                Text("RUN SOLVER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            val validation = viewModel.editorValidation
            if (validation != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (validation.isSolvable) Icons.Default.Check else Icons.Default.Warning,
                  contentDescription = null,
                  tint = if (validation.isSolvable) Color(0xFF10B981) else Color(0xFFEF4444),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = validation.validationMessage,
                  color = if (validation.isSolvable) Color(0xFF10B981) else Color(0xFFEF4444),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              if (validation.suggestedFixes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("SUGGESTED FIXES:", fontSize = 11.sp, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold)
                validation.suggestedFixes.forEach { fix ->
                  Text("• $fix", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save & Export / Import Actions
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Button(
            onClick = {
              viewModel.saveCustomLevel(levelNameInput)
              showSaveSuccess = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("editor_save_button")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("SAVE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { showExportDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("editor_export_button")
          ) {
            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("EXPORT", fontSize = 12.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = { showImportDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("editor_import_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("IMPORT", fontSize = 12.sp, color = Color(0xFFC084FC), fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Export Dialog
  if (showExportDialog) {
    val jsonString = editorLevel.toJsonString()
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("Export Level Layout") },
      text = {
        Column {
          Text("Copy this external layout JSON to share or back up:")
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = jsonString,
            onValueChange = {},
            readOnly = true,
            maxLines = 8,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("GridPulse Level Layout", jsonString))
            viewModel.soundManager.playTargetReached()
            showExportDialog = false
          }
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("COPY JSON")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("CLOSE")
        }
      }
    )
  }

  // Import Dialog
  if (showImportDialog) {
    AlertDialog(
      onDismissRequest = { showImportDialog = false },
      title = { Text("Import External Layout") },
      text = {
        Column {
          Text("Paste JSON layout code below:")
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = importText,
            onValueChange = { importText = it },
            placeholder = { Text("Paste JSON here...") },
            maxLines = 8,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (viewModel.importLevelJson(importText)) {
              showImportDialog = false
            }
          }
        ) {
          Text("LOAD LAYOUT")
        }
      },
      dismissButton = {
        TextButton(onClick = { showImportDialog = false }) {
          Text("CANCEL")
        }
      }
    )
  }

  // Save Success Notification
  if (showSaveSuccess) {
    AlertDialog(
      onDismissRequest = { showSaveSuccess = false },
      title = { Text("Level Saved!") },
      text = { Text("Your custom level has been saved to the Custom Workshops tab.") },
      confirmButton = {
        Button(onClick = { showSaveSuccess = false }) {
          Text("GREAT")
        }
      }
    )
  }
}
