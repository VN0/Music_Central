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

package com.absolute.groove.mcentral.appshortcuts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.absolute.groove.mcentral.activities.SearchActivity
import com.absolute.groove.mcentral.appshortcuts.shortcuttype.LastAddedShortcutType
import com.absolute.groove.mcentral.appshortcuts.shortcuttype.SearchShortCutType
import com.absolute.groove.mcentral.appshortcuts.shortcuttype.ShuffleAllShortcutType
import com.absolute.groove.mcentral.appshortcuts.shortcuttype.TopTracksShortcutType
import com.absolute.groove.mcentral.model.Playlist
import com.absolute.groove.mcentral.model.smartplaylist.LastAddedPlaylist
import com.absolute.groove.mcentral.model.smartplaylist.MyTopTracksPlaylist
import com.absolute.groove.mcentral.model.smartplaylist.ShuffleAllPlaylist
import com.absolute.groove.mcentral.service.MusicService
import com.absolute.groove.mcentral.service.MusicService.*

class AppShortcutLauncherActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var shortcutType = SHORTCUT_TYPE_NONE

        // Set shortcutType from the intent extras
        val extras = intent.extras
        if (extras != null) {
            shortcutType = extras.getInt(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE)
        }

        when (shortcutType) {
            SHORTCUT_TYPE_SHUFFLE_ALL -> {
                startServiceWithPlaylist(
                    MusicService.SHUFFLE_MODE_SHUFFLE, ShuffleAllPlaylist(applicationContext)
                )
                DynamicShortcutManager.reportShortcutUsed(this, ShuffleAllShortcutType.id)
            }
            SHORTCUT_TYPE_TOP_TRACKS -> {
                startServiceWithPlaylist(
                    MusicService.SHUFFLE_MODE_NONE, MyTopTracksPlaylist(applicationContext)
                )
                DynamicShortcutManager.reportShortcutUsed(this, TopTracksShortcutType.id)
            }
            SHORTCUT_TYPE_LAST_ADDED -> {
                startServiceWithPlaylist(
                    MusicService.SHUFFLE_MODE_NONE, LastAddedPlaylist(applicationContext)
                )
                DynamicShortcutManager.reportShortcutUsed(this, LastAddedShortcutType.id)
            }
            SHORTCUT_TYPE_SEARCH -> {
                startActivity(Intent(this, SearchActivity::class.java))
                DynamicShortcutManager.reportShortcutUsed(this, SearchShortCutType.id)
            }
        }
        finish()
    }

    private fun startServiceWithPlaylist(shuffleMode: Int, playlist: Playlist) {
        val intent = Intent(this, MusicService::class.java)
        intent.action = ACTION_PLAY_PLAYLIST

        val bundle = Bundle()
        bundle.putParcelable(INTENT_EXTRA_PLAYLIST, playlist)
        bundle.putInt(INTENT_EXTRA_SHUFFLE_MODE, shuffleMode)

        intent.putExtras(bundle)

        startService(intent)
    }

    companion object {
        const val KEY_SHORTCUT_TYPE = "com.absolute.groove.mcentral.appshortcuts.ShortcutType"
        const val SHORTCUT_TYPE_SHUFFLE_ALL = 0
        const val SHORTCUT_TYPE_TOP_TRACKS = 1
        const val SHORTCUT_TYPE_LAST_ADDED = 2
        const val SHORTCUT_TYPE_SEARCH = 3
        const val SHORTCUT_TYPE_NONE = 4
    }
}
