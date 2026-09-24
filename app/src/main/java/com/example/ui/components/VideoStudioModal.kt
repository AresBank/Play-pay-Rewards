package com.example.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.WarmGold
import java.io.File

@Composable
fun VideoStudioModal(
    onDismiss: () -> Unit,
    onUploadAndShowInFeed: (
        title: String,
        description: String,
        category: String,
        rewardCoins: Int,
        videoUri: String,
        coverResName: String,
        soundTitle: String,
        textSticker: String,
        filter: String,
        durationSeconds: Int
    ) -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) } // 1: Record/Pick, 2: Edit, 3: Details & Reward, 4: Publishing

    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoName by remember { mutableStateOf("grabacion_camara_1.mp4") }
    var selectedCoverRes by remember { mutableStateOf("thumb_cyber_ai") }
    var videoDurationSeconds by remember { mutableIntStateOf(30) }

    // Edit Studio states
    var isPreviewPlaying by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf("Original") }
    var textSticker by remember { mutableStateOf("⚡ ¡Gana 200 monedas viéndolo!") }
    var stickerColor by remember { mutableStateOf(NeonCyan) }
    var stickerPlacement by remember { mutableStateOf("Abajo") } // Arriba, Centro, Abajo
    var selectedSound by remember { mutableStateOf("CyberSynth • Neon Pulse 140BPM") }

    // Details & Reward states
    var title by remember { mutableStateOf("Mi video exclusivo en Play&Pay ⚡") }
    var description by remember { mutableStateOf("Descubre cómo ganar recompensas diarias y transferir tus ganancias a Mercado Pago o PayPal #parati #recompensas #playandpay") }
    var selectedCategory by remember { mutableStateOf("Tech & AI") }
    var selectedRewardCoins by remember { mutableIntStateOf(500) }
    var isScanningWithAi by remember { mutableStateOf(false) }

    // Camera video capture launcher using FileProvider
    val tempVideoUri = remember {
        try {
            val file = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.mp4")
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            null
        }
    }

    val cameraVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && tempVideoUri != null) {
            selectedVideoUri = tempVideoUri
            selectedVideoName = "camara_grabacion.mp4"
            currentStep = 2 // Move directly to Edit studio!
        }
    }

    // Gallery video picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedVideoName = uri.lastPathSegment?.substringAfterLast('/') ?: "video_galeria.mp4"
            currentStep = 2 // Move directly to Edit studio!
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PitchBlack)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header with Step Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (currentStep > 1 && !isScanningWithAi) currentStep-- else onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }

                    Text(
                        text = when (currentStep) {
                            1 -> "Paso 1: Grabar o Seleccionar Video"
                            2 -> "Paso 2: Estudio de Edición & Filtros"
                            3 -> "Paso 3: Título, Categoría & Recompensa"
                            else -> "Paso 4: Verificación IA & Publicación"
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "$currentStep / 4", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // STEP 1: RECORD OR SELECT
                if (currentStep == 1) {
                    StepOneRecordOrPick(
                        onRecordCamera = {
                            if (tempVideoUri != null) {
                                cameraVideoLauncher.launch(tempVideoUri)
                            }
                        },
                        onPickGallery = {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        },
                        onSelectPreset = { presetThumb, presetTitle ->
                            selectedCoverRes = presetThumb
                            selectedVideoName = "$presetTitle.mp4"
                            title = "Nuevo: $presetTitle ⚡"
                            currentStep = 2
                        }
                    )
                }

                // STEP 2: EDIT STUDIO (Trim, Filters, Text Sticker, Sound)
                if (currentStep == 2) {
                    StepTwoEditStudio(
                        videoName = selectedVideoName,
                        coverResName = selectedCoverRes,
                        isPlaying = isPreviewPlaying,
                        onTogglePlay = { isPreviewPlaying = !isPreviewPlaying },
                        filter = selectedFilter,
                        onSelectFilter = { selectedFilter = it },
                        textSticker = textSticker,
                        onTextStickerChange = { textSticker = it },
                        stickerColor = stickerColor,
                        onSelectStickerColor = { stickerColor = it },
                        stickerPlacement = stickerPlacement,
                        onSelectStickerPlacement = { stickerPlacement = it },
                        sound = selectedSound,
                        onSelectSound = { selectedSound = it },
                        durationSeconds = videoDurationSeconds,
                        onDurationChange = { videoDurationSeconds = it },
                        onNext = { currentStep = 3 }
                    )
                }

                // STEP 3: DETAILS, DESCRIPTION & REWARD COINS
                if (currentStep == 3) {
                    StepThreeDetailsAndRewards(
                        title = title,
                        onTitleChange = { title = it },
                        description = description,
                        onDescriptionChange = { description = it },
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        selectedRewardCoins = selectedRewardCoins,
                        onSelectRewardCoins = { selectedRewardCoins = it },
                        onNext = {
                            currentStep = 4
                            isScanningWithAi = true
                        }
                    )
                }

                // STEP 4: AI SCAN & PUBLISH TO PARA TI
                if (currentStep == 4) {
                    StepFourAiPublish(
                        isScanning = isScanningWithAi,
                        title = title,
                        category = selectedCategory,
                        rewardCoins = selectedRewardCoins,
                        onPublishConfirmed = {
                            onUploadAndShowInFeed(
                                title,
                                description,
                                selectedCategory,
                                selectedRewardCoins,
                                selectedVideoUri?.toString() ?: "custom_stream_${System.currentTimeMillis()}",
                                selectedCoverRes,
                                selectedSound,
                                textSticker,
                                selectedFilter,
                                videoDurationSeconds
                            )
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun StepOneRecordOrPick(
    onRecordCamera: () -> Unit,
    onPickGallery: () -> Unit,
    onSelectPreset: (String, String) -> Unit
) {
    Column {
        Text(
            text = "¿Cómo quieres crear tu video hoy?",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Graba en vivo con tu cámara, elige de tu dispositivo o usa una plantilla vertical.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Big Record Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFFE11D48), Color(0xFF9333EA)))
                )
                .clickable { onRecordCamera() }
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = "🎥 Grabar con Cámara", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text(text = "Captura video vertical con audio y resolución HD", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Big Gallery Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCard)
                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { onPickGallery() }
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = "📁 Seleccionar de Galería", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text(text = "Elige un archivo MP4 o video guardado en tu móvil", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "⚡ O elige un Set de Producción Rápido:",
            color = WarmGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        val presets = listOf(
            Triple("thumb_cyber_ai", "Cyber AI Mix", "Música & Sintetizadores"),
            Triple("thumb_crypto_future", "Cripto & Retiros SPEI", "Fintech & Mercado Pago"),
            Triple("thumb_gamer_neon", "Gamer Pro Play", "Torneo CyberArena"),
            Triple("thumb_tech_review", "Tech Review Holograma", "Gadgets & Novedades")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.forEach { (thumb, pTitle, pDesc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(1.dp, Color(0xFF262638), RoundedCornerShape(12.dp))
                        .clickable { onSelectPreset(thumb, pTitle) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = WarmGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = pTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = pDesc, color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                    Text(text = "Usar →", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StepTwoEditStudio(
    videoName: String,
    coverResName: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    filter: String,
    onSelectFilter: (String) -> Unit,
    textSticker: String,
    onTextStickerChange: (String) -> Unit,
    stickerColor: Color,
    onSelectStickerColor: (Color) -> Unit,
    stickerPlacement: String,
    onSelectStickerPlacement: (String) -> Unit,
    sound: String,
    onSelectSound: (String) -> Unit,
    durationSeconds: Int,
    onDurationChange: (Int) -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(coverResName) {
        val id = context.resources.getIdentifier(coverResName, "drawable", context.packageName)
        if (id != 0) id else context.resources.getIdentifier("thumb_cyber_ai", "drawable", context.packageName)
    }

    Column {
        Text(text = "Estudio de Edición Creativa", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Text(text = "Personaliza filtros, texto superpuesto, recorte y banda sonora.", color = Color(0xFF94A3B8), fontSize = 12.sp)

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Video Preview Box with filters & sticker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(2.dp, NeonCyan, RoundedCornerShape(16.dp))
                .clickable { onTogglePlay() },
            contentAlignment = Alignment.Center
        ) {
            if (resId != 0) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Filter Color Tint Overlay
            when (filter) {
                "Neon Cyber" -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(NeonCyan.copy(alpha = 0.25f), NeonMagenta.copy(alpha = 0.35f))
                            )
                        )
                )
                "Matrix Code" -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF10B981).copy(alpha = 0.28f))
                )
                "Warm Gold" -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WarmGold.copy(alpha = 0.25f))
                )
                "Noir" -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            }

            // Custom On-Screen Text Sticker / Watermark
            if (textSticker.isNotBlank()) {
                val alignMod = when (stickerPlacement) {
                    "Arriba" -> Modifier.align(Alignment.TopCenter).padding(top = 18.dp)
                    "Centro" -> Modifier.align(Alignment.Center)
                    else -> Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
                }
                Box(
                    modifier = alignMod
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, stickerColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = textSticker,
                        color = stickerColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Play / Pause central indicator
            if (!isPlaying) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Trim Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Duración del Clip: ${durationSeconds}s", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = "Recompensa ajustada a este tiempo", color = NeonCyan, fontSize = 10.sp)
        }
        Slider(
            value = durationSeconds.toFloat(),
            onValueChange = { onDurationChange(it.toInt()) },
            valueRange = 10f..60f,
            steps = 5,
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = Color(0xFF2E2E40)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filters Selector
        Text(text = "Filtro Visual Cyberpunk:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Original", "Neon Cyber", "Matrix Code", "Warm Gold", "Noir").forEach { f ->
                val isSel = f == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) NeonCyan else DarkCard)
                        .clickable { onSelectFilter(f) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = f,
                        color = if (isSel) PitchBlack else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Text Sticker Input & Placement
        Text(text = "Sticker de Texto en Pantalla:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = textSticker,
            onValueChange = onTextStickerChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard,
                focusedIndicatorColor = NeonCyan,
                unfocusedIndicatorColor = Color(0xFF333348),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Arriba", "Centro", "Abajo").forEach { pos ->
                    val isSel = pos == stickerPlacement
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) Color(0xFF2E2E44) else DarkCard)
                            .border(1.dp, if (isSel) NeonCyan else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { onSelectStickerPlacement(pos) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = pos, color = if (isSel) NeonCyan else Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }

            // Color dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(NeonCyan, WarmGold, NeonMagenta, Color.White).forEach { col ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(col)
                            .border(if (stickerColor == col) 2.dp else 0.dp, Color.White, CircleShape)
                            .clickable { onSelectStickerColor(col) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Soundtrack selector
        Text(text = "Pista de Audio / Música:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        listOf(
            "CyberSynth • Neon Pulse 140BPM",
            "Fintech Vibes • Cashflow Mix",
            "CyberArena EDM • Drop",
            "Audio Original de Cámara"
        ).forEach { s ->
            val isSel = s == sound
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) DarkCard else DarkSurface)
                    .border(1.dp, if (isSel) NeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                    .clickable { onSelectSound(s) }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = if (isSel) NeonCyan else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = s, color = if (isSel) Color.White else Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = PitchBlack),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Continuar a Metadatos & Recompensa →", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StepThreeDetailsAndRewards(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    selectedRewardCoins: Int,
    onSelectRewardCoins: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column {
        Text(text = "Detalles & Recompensa para Espectadores", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Text(text = "Define cuánto ganarán los usuarios en el Anillo de Monedas al ver tu video.", color = Color(0xFF94A3B8), fontSize = 12.sp)

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Título del Video", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard,
                focusedIndicatorColor = NeonCyan,
                unfocusedIndicatorColor = Color(0xFF333348),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Descripción & Hashtags", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            minLines = 3,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard,
                focusedIndicatorColor = NeonCyan,
                unfocusedIndicatorColor = Color(0xFF333348),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "Categoría del Video", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Tech & AI", "Crypto & Finanzas", "Gaming", "Estilo de Vida").forEach { cat ->
                val isSel = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) NeonCyan else DarkCard)
                        .clickable { onSelectCategory(cat) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        color = if (isSel) PitchBlack else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // REWARD COINS SELECTION
        Text(text = "💰 Recompensa para la Audiencia (Mínimo $5.00 MXN netos):", color = WarmGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Text(
            text = "Los usuarios acumularán estas ganancias ($5.00 MXN = 500 monedas) en tiempo real mientras miran tu video.",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(500, 600, 750, 1000).forEach { coins ->
                val isSel = coins == selectedRewardCoins
                val pesos = coins / 100
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) WarmGold else DarkCard)
                        .border(1.dp, if (isSel) WarmGold else Color(0xFF333348), RoundedCornerShape(10.dp))
                        .clickable { onSelectRewardCoins(coins) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$$pesos MXN\n($coins🪙)",
                        color = if (isSel) PitchBlack else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = WarmGold, contentColor = PitchBlack),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Publicar & Validar con Gemini IA →", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StepFourAiPublish(
    isScanning: Boolean,
    title: String,
    category: String,
    rewardCoins: Int,
    onPublishConfirmed: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))

        if (isScanning) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E1065)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(48.dp), strokeWidth = 3.dp)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Gemini 2.5 Flash Moderando Contenido...",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Analizando seguridad comunitaria, cumplimiento y asignando puntuación de recomendación algorítmica.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF064E3B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(52.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Video Verificado & Listo para Para Ti!",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Tu contenido cumple al 100% las directrices. Al presionar el botón abajo, serás redirigido inmediatamente a 'Para Ti' con tu video en reproducción y el anillo de monedas activo con +$rewardCoins monedas.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Título:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(title.take(24) + "...", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Categoría:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(category, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Recompensa asignada:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text("+$rewardCoins Monedas", color = WarmGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Destino inmediato:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text("Feed Principal 'Para Ti' (Top 1)", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onPublishConfirmed,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = PitchBlack),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mostrar en Para Ti Ahora", fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        }
    }
}
