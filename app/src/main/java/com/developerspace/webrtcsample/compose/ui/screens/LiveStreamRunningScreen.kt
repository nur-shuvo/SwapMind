package com.developerspace.webrtcsample.compose.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.developerspace.webrtcsample.BuildConfig
import com.developerspace.webrtcsample.compose.ui.util.ChannelNameInput
import com.developerspace.webrtcsample.compose.ui.util.TwoVideoView
import com.developerspace.webrtcsample.compose.ui.util.VideoStatsInfo
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.ClientRoleOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.RtcEngineConfig.AreaCode
import io.agora.rtc2.SimulcastStreamConfig
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LiveStreamRunningScreen(
    navController: NavController? = null,
    client_role: Int,
    channel_name: String
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val keyboard = LocalSoftwareKeyboardController.current
    var isJoined by rememberSaveable { mutableStateOf(false) }
    var localLarge by rememberSaveable { mutableStateOf(false) }
    var channelName by rememberSaveable { mutableStateOf(channel_name) }
    var localUid by rememberSaveable { mutableIntStateOf(0) }
    var remoteUid by rememberSaveable { mutableIntStateOf(0) }
    var localStats by remember { mutableStateOf(VideoStatsInfo()) }
    var remoteStats by remember { mutableStateOf(VideoStatsInfo()) }
    var clientRole by remember { mutableStateOf(client_role) }

    val rtcEngine = remember {
        RtcEngine.create(RtcEngineConfig().apply {
            mAreaCode = AreaCode.AREA_CODE_GLOB
            mContext = context
            mAppId = BuildConfig.AGORA_APP_ID
            mEventHandler = object : IRtcEngineEventHandler() {
                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    super.onJoinChannelSuccess(channel, uid, elapsed)
                    isJoined = true
                    localUid = uid
                }

                override fun onLeaveChannel(stats: RtcStats?) {
                    super.onLeaveChannel(stats)
                    isJoined = false
                    localUid = 0
                    localStats = VideoStatsInfo()
                    remoteUid = 0
                    remoteStats = VideoStatsInfo()
                }

                override fun onUserJoined(uid: Int, elapsed: Int) {
                    super.onUserJoined(uid, elapsed)
                    remoteUid = uid
                    remoteStats = VideoStatsInfo()
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    super.onUserOffline(uid, reason)
                    if (remoteUid == uid) {
                        remoteUid = 0
                        remoteStats = VideoStatsInfo()
                    }
                }

                override fun onRtcStats(stats: RtcStats?) {
                    super.onRtcStats(stats)
                    localStats.copy(rtcStats = stats).let {
                        localStats = it
                    }
                }

                override fun onLocalVideoStats(
                    source: Constants.VideoSourceType?,
                    stats: LocalVideoStats?
                ) {
                    super.onLocalVideoStats(source, stats)
                    localStats.copy(localVideoStats = stats).let {
                        localStats = it
                    }
                }

                override fun onLocalAudioStats(stats: LocalAudioStats?) {
                    super.onLocalAudioStats(stats)
                    localStats.copy(localAudioStats = stats).let {
                        localStats = it
                    }
                }

                override fun onRemoteVideoStats(stats: RemoteVideoStats?) {
                    super.onRemoteVideoStats(stats)
                    val uid = stats?.uid ?: return
                    if (remoteUid == uid) {
                        remoteStats.copy(remoteVideoStats = stats).let {
                            remoteStats = it
                        }
                    }
                }

                override fun onRemoteAudioStats(stats: RemoteAudioStats?) {
                    super.onRemoteAudioStats(stats)
                    val uid = stats?.uid ?: return
                    if (remoteUid == uid) {
                        remoteStats.copy(remoteAudioStats = stats).let {
                            remoteStats = it
                        }
                    }
                }

                override fun onClientRoleChanged(
                    oldRole: Int,
                    newRole: Int,
                    newRoleOptions: ClientRoleOptions?
                ) {
                    super.onClientRoleChanged(oldRole, newRole, newRoleOptions)
                    clientRole = newRole
                }
            }
        }).apply {
            setVideoEncoderConfiguration(
                VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_960x540,
                    FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
                )
            )
            enableVideo()
            setDualStreamMode(
                Constants.SimulcastStreamMode.ENABLE_SIMULCAST_STREAM,
                SimulcastStreamConfig(
                    VideoEncoderConfiguration.VideoDimensions(
                        100, 100
                    ), 100, 15
                )
            )
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            rtcEngine.stopPreview()
            rtcEngine.leaveChannel()
            RtcEngine.destroy()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val allGranted = grantedMap.values.all { it }
            if (allGranted) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show()
                val mediaOptions = ChannelMediaOptions()
                mediaOptions.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
                mediaOptions.clientRoleType = clientRole
                rtcEngine.joinChannel(null, channelName, 0, mediaOptions)
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }

    LaunchedEffect(Unit) {
        delay(1.seconds)
        keyboard?.hide()
        permissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
            )
        )
    }

    LiveStreamingView(
        rtcEngine = rtcEngine,
        channelName = channelName,
        isJoined = isJoined,
        clientRole = clientRole,
        localUid = localUid,
        remoteUid = remoteUid,
        localStats = null,
        remoteStats = null,
        localLarge = localLarge,
        onSwitch = {
            localLarge = !localLarge
        },
        onJoinClick = {
            keyboard?.hide()
            channelName = it
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAMERA
                )
            )
        },
        onLeaveClick = {
            rtcEngine.stopPreview()
            rtcEngine.leaveChannel()
        }
    )
}

@Composable
private fun LiveStreamingView(
    rtcEngine: RtcEngine? = null,
    channelName: String,
    isJoined: Boolean,
    clientRole: Int = Constants.CLIENT_ROLE_AUDIENCE,
    localUid: Int = 0,
    remoteUid: Int = 0,
    localLarge: Boolean = true,
    localStats: VideoStatsInfo? = null,
    remoteStats: VideoStatsInfo? = null,
    onSwitch: () -> Unit = {},
    onJoinClick: (String) -> Unit,
    onLeaveClick: () -> Unit
) {
    Box {
        Column {
            Box(modifier = Modifier.weight(1.0f)) {
                TwoVideoView(
                    modifier = Modifier.fillMaxHeight(),
                    localUid = localUid,
                    remoteUid = remoteUid,
                    localStats = localStats,
                    remoteStats = remoteStats,
                    secondClickable = isJoined && remoteUid != 0,
                    localPrimary = localLarge || remoteUid == 0,
                    onSecondClick = onSwitch,
                    localRender = { view, uid, isFirstSetup ->
                        if (clientRole == Constants.CLIENT_ROLE_BROADCASTER) {
                            rtcEngine?.setupLocalVideo(
                                VideoCanvas(
                                    view,
                                    Constants.RENDER_MODE_HIDDEN,
                                    uid
                                )
                            )
                            if (isFirstSetup) {
                                rtcEngine?.startPreview()
                            }
                        }
                    },
                    remoteRender = { view, uid, _ ->
                        rtcEngine?.setupRemoteVideo(
                            VideoCanvas(
                                view,
                                Constants.RENDER_MODE_HIDDEN,
                                uid
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun LiveStreamingViewPreview() {
    Box {
        LiveStreamingView(
            channelName = "Channel Name",
            isJoined = false,
            onJoinClick = {},
            onLeaveClick = {},
        )
    }
}