/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.absolute.groove.mcentral.fragments.player.peak

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import com.absolute.groove.appthemehelper.ThemeStore
import com.absolute.groove.appthemehelper.util.ATHUtil
import com.absolute.groove.appthemehelper.util.MaterialValueHelper
import com.absolute.groove.appthemehelper.util.TintHelper
import com.absolute.groove.mcentral.R
import com.absolute.groove.mcentral.extensions.applyColor
import com.absolute.groove.mcentral.fragments.base.AbsPlayerControlsFragment
import com.absolute.groove.mcentral.helper.MusicPlayerRemote
import com.absolute.groove.mcentral.helper.MusicProgressViewUpdateHelper
import com.absolute.groove.mcentral.helper.PlayPauseButtonOnClickHandler
import com.absolute.groove.mcentral.misc.SimpleOnSeekbarChangeListener
import com.absolute.groove.mcentral.service.MusicService
import com.absolute.groove.mcentral.util.MusicUtil

import com.absolute.groove.mcentral.util.PreferenceUtilKT
import com.absolute.groove.mcentral.util.color.MediaNotificationProcessor
import kotlinx.android.synthetic.main.fragment_peak_control_player.*

/**
 * Created by hemanths on 2019-10-04.
 */

class PeakPlayerControlFragment : AbsPlayerControlsFragment() {

    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper
    private var lastPlaybackControlsColor: Int = 0
    private var lastDisabledPlaybackControlsColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_peak_control_player, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpMusicControllers()
    }

    override fun show() {
    }

    override fun hide() {
    }

    override fun setColor(color: MediaNotificationProcessor) {
        val controlsColor =
            if (PreferenceUtilKT.isAdaptiveColor) {
                color.primaryTextColor
            } else {
                ThemeStore.accentColor(requireContext())
            }
        progressSlider.applyColor(controlsColor)
        volumeFragment?.setTintableColor(controlsColor)
        playPauseButton.setColorFilter(controlsColor, PorterDuff.Mode.SRC_IN)
        nextButton.setColorFilter(controlsColor, PorterDuff.Mode.SRC_IN)
        previousButton.setColorFilter(controlsColor, PorterDuff.Mode.SRC_IN)

        if (!ATHUtil.isWindowBackgroundDark(requireContext())) {
            lastPlaybackControlsColor =
                MaterialValueHelper.getSecondaryTextColor(requireContext(), true)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getSecondaryDisabledTextColor(requireContext(), true)
        } else {
            lastPlaybackControlsColor =
                MaterialValueHelper.getPrimaryTextColor(requireContext(), false)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getPrimaryDisabledTextColor(requireContext(), false)
        }
        updateRepeatState()
        updateShuffleState()
    }

    private fun updatePlayPauseDrawableState() {
        if (MusicPlayerRemote.isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause_white_24dp)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_32dp)
        }
    }

    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpRepeatButton()
        setUpShuffleButton()
        setUpProgressSlider()
    }

    private fun setUpShuffleButton() {
        shuffleButton.setOnClickListener {
            MusicPlayerRemote.toggleShuffleMode()
        }
    }

    private fun setUpRepeatButton() {
        repeatButton.setOnClickListener {
            MusicPlayerRemote.cycleRepeatMode()
        }
    }

    override fun setUpProgressSlider() {
        progressSlider.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress)
                    onUpdateProgressViews(
                        MusicPlayerRemote.songProgressMillis,
                        MusicPlayerRemote.songDurationMillis
                    )
                }
            }
        })
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressSlider.max = total

        val animator = ObjectAnimator.ofInt(progressSlider, "progress", progress)
        animator.duration = SLIDER_ANIMATION_TIME
        animator.interpolator = LinearInterpolator()
        animator.start()

        songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }

    private fun setUpPlayPauseFab() {
        TintHelper.setTintAuto(playPauseButton, Color.WHITE, true)
        TintHelper.setTintAuto(playPauseButton, Color.BLACK, false)
        playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun setUpPrevNext() {
        updatePrevNextColor()
        nextButton.setOnClickListener { MusicPlayerRemote.playNextSong() }
        previousButton.setOnClickListener { MusicPlayerRemote.back() }
    }

    private fun updatePrevNextColor() {
        nextButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
        previousButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
    }

    override fun updateShuffleState() {
        when (MusicPlayerRemote.shuffleMode) {
            MusicService.SHUFFLE_MODE_SHUFFLE -> shuffleButton.setColorFilter(
                lastPlaybackControlsColor,
                PorterDuff.Mode.SRC_IN
            )
            else -> shuffleButton.setColorFilter(
                lastDisabledPlaybackControlsColor,
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    override fun updateRepeatState() {
        when (MusicPlayerRemote.repeatMode) {
            MusicService.REPEAT_MODE_NONE -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                repeatButton.setColorFilter(
                    lastDisabledPlaybackControlsColor,
                    PorterDuff.Mode.SRC_IN
                )
            }
            MusicService.REPEAT_MODE_ALL -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
            MusicService.REPEAT_MODE_THIS -> {
                repeatButton.setImageResource(R.drawable.ic_repeat_one_white_24dp)
                repeatButton.setColorFilter(lastPlaybackControlsColor, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    override fun onPlayStateChanged() {
        super.onPlayStateChanged()
        updatePlayPauseDrawableState()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updatePlayPauseDrawableState()
    }

    override fun onRepeatModeChanged() {
        updateRepeatState()
    }

    override fun onShuffleModeChanged() {
        updateShuffleState()
    }


}