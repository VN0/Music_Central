package com.absolute.groove.mcentral.fragments.player.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.absolute.groove.appthemehelper.util.ATHUtil
import com.absolute.groove.appthemehelper.util.ToolbarContentTintHelper
import com.absolute.groove.mcentral.R
import com.absolute.groove.mcentral.fragments.base.AbsPlayerFragment
import com.absolute.groove.mcentral.helper.MusicPlayerRemote
import com.absolute.groove.mcentral.helper.MusicProgressViewUpdateHelper
import com.absolute.groove.mcentral.model.Song
import com.absolute.groove.mcentral.util.MusicUtil
import com.absolute.groove.mcentral.util.color.MediaNotificationProcessor
import kotlinx.android.synthetic.main.fragment_home_player.*


class HomePlayerFragment : AbsPlayerFragment(), MusicProgressViewUpdateHelper.Callback {
    private var lastColor: Int = 0
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlayerToolbar()

    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()

    }

    override fun playerToolbar(): Toolbar? {
        return playerToolbar
    }

    override fun onShow() {

    }

    override fun onHide() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        super.onPlayingMetaChanged()
        updateSong()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSong
        title.text = song.title
        text.text = song.artistName
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun toolbarIconColor(): Int {
        return Color.WHITE
    }

    override val paletteColor: Int
        get() = lastColor

    override fun onColorChanged(color: MediaNotificationProcessor) {
        lastColor = color.backgroundColor
        callbacks?.onPaletteColorChanged()
        ToolbarContentTintHelper.colorizeToolbar(
            playerToolbar,
            Color.WHITE,
            requireActivity()
        )
    }

    override fun toggleFavorite(song: Song) {
        super.toggleFavorite(song)
        if (song.id == MusicPlayerRemote.currentSong.id) {
            updateIsFavorite()
        }
    }

    override fun onFavoriteToggled() {
        toggleFavorite(MusicPlayerRemote.currentSong)
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        songTotalTime.text = MusicUtil.getReadableDurationString(progress.toLong())
    }

    private fun setUpPlayerToolbar() {
        playerToolbar.inflateMenu(R.menu.menu_player)
        playerToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        playerToolbar.setOnMenuItemClickListener(this)

        ToolbarContentTintHelper.colorizeToolbar(
            playerToolbar,
            ATHUtil.resolveColor(requireContext(), R.attr.colorControlNormal),
            requireActivity()
        )
    }
}