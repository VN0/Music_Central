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

package com.absolute.groove.mcentral.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.provider.MediaStore.Audio.Playlists.Members.PLAYLIST_ID
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.absolute.groove.appthemehelper.util.MaterialUtil
import com.absolute.groove.mcentral.R
import com.absolute.groove.mcentral.extensions.extraNotNull
import com.absolute.groove.mcentral.util.PlaylistsUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RenamePlaylistDialog : DialogFragment() {


    @SuppressLint("InflateParams")
    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {
        val layout = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_playlist, null)
        val inputEditText: TextInputEditText = layout.findViewById(R.id.actionNewPlaylist)
        val nameContainer: TextInputLayout =
            layout.findViewById(R.id.actionNewPlaylistContainer)
        MaterialUtil.setTint(nameContainer, false)

        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert
        )
            .setTitle(R.string.rename_playlist_title)
            .setView(layout)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_rename) { _, _ ->
                val name = inputEditText.text.toString()
                if (name.isNotEmpty()) {
                    PlaylistsUtil.renamePlaylist(
                        requireContext(),
                        extraNotNull<Long>(PLAYLIST_ID).value,
                        name
                    )
                }
            }
            .create()
    }

    companion object {

        fun create(playlistId: Long): RenamePlaylistDialog {
            val dialog = RenamePlaylistDialog()
            val args = Bundle()
            args.putLong(PLAYLIST_ID, playlistId)
            dialog.arguments = args
            return dialog
        }
    }
}