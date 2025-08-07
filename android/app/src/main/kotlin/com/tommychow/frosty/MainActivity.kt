package com.tommychow.frosty

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import cl.puntito.simple_pip_mode.PipCallbackHelperActivityWrapper
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: PipCallbackHelperActivityWrapper() {
    private val CHANNEL = "com.frosty.app/audio"
    private var audioFocusRequest: AudioFocusRequest? = null
    private var audioManager: AudioManager? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            if (call.method == "requestAudioFocus") {
                requestAudioFocus()
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun requestAudioFocus() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                        .build()
                )
                .setOnAudioFocusChangeListener { /* IGNORE focus changes */ }
                .build()
            audioFocusRequest = focusRequest
            audioManager?.requestAudioFocus(focusRequest)
        } else {
            audioManager?.requestAudioFocus(
                { /* IGNORE */ },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }
}
