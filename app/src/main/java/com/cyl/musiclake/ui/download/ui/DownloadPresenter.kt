package com.cyl.musiclake.ui.download.ui

import com.cyl.musiclake.common.Constants
import com.cyl.musiclake.ui.base.BasePresenter
import com.cyl.musiclake.ui.download.DownloadLoader
import com.cyl.musiclake.event.DownloadEvent
import com.cyl.musiclake.event.PlaylistEvent
import com.cyl.musiclake.utils.LogUtil
import com.google.android.exoplayer2.offline.Downloader
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

/**
 * Author   : D22434
 * version  : 2018/1/24
 * function :
 */

class DownloadPresenter @Inject
constructor() : BasePresenter<DownloadContract.View>(), DownloadContract.Presenter {

    var isCache = true

    override fun attachView(view: DownloadContract.View) {
        super.attachView(view)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun reloadDownloadMusic(event: DownloadEvent) {
        loadDownloadMusic(isCache)
        loadDownloading()
    }

    override fun detachView() {
        super.detachView()
        EventBus.getDefault().unregister(this)
    }

    override fun loadDownloadMusic(isCache: Boolean) {
        this.isCache = isCache
        mView?.showLoading()
        doAsync {
            val data = DownloadLoader.getDownloadList(isCache)
            uiThread {
                mView?.showSongs(data)
                mView?.hideLoading()
            }
        }
    }

    override fun loadDownloading() {
        mView?.showLoading()
        doAsync {
            val data = DownloadLoader.getDownloadingList()
            uiThread {
                mView?.showDownloadList(data)
                mView?.hideLoading()
            }
        }
    }

    fun deleteAll() {
        mView?.showLoading()
        doAsync {
            val data = DownloadLoader.clearDownloadList()
            uiThread {
                LogUtil.d("DownloadLoader", "data =${data.size}")
                EventBus.getDefault().post(PlaylistEvent(Constants.PLAYLIST_DOWNLOAD_ID))
                mView?.showSongs(data)
                mView?.hideLoading()
            }
        }
    }

}
