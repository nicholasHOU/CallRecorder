package com.android.callrecorder.manager;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import com.android.callrecorder.R;
import com.android.callrecorder.home.ui.callrecord.CallRecordViewHolder;
import com.android.callrecorder.utils.DateUtil;
import com.android.callrecorder.utils.Logs;
import com.android.callrecorder.utils.ToastUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RecordPlayerManager {
    private WeakReference<CallRecordViewHolder> mCurViewRef;
    private Disposable mDisposable;
    private MediaPlayer mPlayer;
    private Drawable mThumb_normal;
    private Drawable mThumb_pressed;
    private static volatile RecordPlayerManager INSTANCE;

    public static RecordPlayerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RecordPlayerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecordPlayerManager();
                }
            }
        }
        return INSTANCE;
    }

    private RecordPlayerManager() {
    }

    /**
     * 将上一个播放暂停，并隐藏播放进度
     */
    public void clearLast() {
        if (this.mCurViewRef != null) {
            CallRecordViewHolder holder = this.mCurViewRef.get();
            if ((holder != null) && (holder.rlProgress.getVisibility() == View.VISIBLE)) {
                holder.rlProgress.setVisibility(View.GONE);
                holder.ivPlay.setImageResource(R.drawable.ic_play);
                if (this.mPlayer != null) {
                    this.mPlayer.stop();
                }
            }
        }
        if ((this.mDisposable != null) && (!this.mDisposable.isDisposed())) {
            this.mDisposable.dispose();
        }
    }

    /**
     * 更新播放进度
     *
     * @param paramViewHolder
     */
    private void updateProgress(final CallRecordViewHolder paramViewHolder) {
        this.mDisposable = Observable.interval(100L, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                if (RecordPlayerManager.this.mDisposable.isDisposed()) {
                    return;
                }
                if (RecordPlayerManager.this.mPlayer != null) {
                    int progress =RecordPlayerManager.this.mPlayer.getCurrentPosition()*100/RecordPlayerManager.this.mPlayer.getDuration();
                    Logs.e("RecordPlayerManager","progress = "+ progress);
                    Logs.e("RecordPlayerManager","current = "+ RecordPlayerManager.this.mPlayer.getCurrentPosition());
                    paramViewHolder.lProgress.setProgress(progress);
                    paramViewHolder.tvPlayProgressTime.setText(DateUtil.formatTime(false, RecordPlayerManager.this.mPlayer.getCurrentPosition()));
                }
            }
        });
    }

    public void cancelTimerTask() {
        Disposable localDisposable = this.mDisposable;
        if ((localDisposable != null) && (!localDisposable.isDisposed())) {
            this.mDisposable.dispose();
        }
    }

    public int getDuration(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return 0;
        }
        if (this.mPlayer == null) {
            this.mPlayer = new MediaPlayer();
        }
        try {
            this.mPlayer.reset();
            this.mPlayer.setDataSource(filePath);
            this.mPlayer.prepare();
            int i = this.mPlayer.getDuration();
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void play(final CallRecordViewHolder paramViewHolder, String recordFilePath) {
        WeakReference localWeakReference = this.mCurViewRef;
        if ((localWeakReference != null) && (localWeakReference.get() != paramViewHolder)) {
            clearLast();
        }
        this.mCurViewRef = new WeakReference(paramViewHolder);
        if (paramViewHolder.rlProgress.getVisibility() == View.GONE) {
            paramViewHolder.rlProgress.setVisibility(View.VISIBLE);
            paramViewHolder.ivPlay.setImageResource(R.drawable.ic_stop);
            if (this.mPlayer != null) {
                this.mPlayer.stop();
            }
            if ((this.mDisposable != null) && (!this.mDisposable.isDisposed())) {
                this.mDisposable.dispose();
            }
//            return;
        }
        try {
            if (this.mPlayer == null) {
                this.mPlayer = new MediaPlayer();
            }
            this.mPlayer.reset();
            this.mPlayer.setDataSource(recordFilePath);
            this.mPlayer.prepare();
            this.mPlayer.start();
            updateProgress(paramViewHolder);
            this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer paramAnonymousMediaPlayer) {
                    Logs.e("RecordPlayerManager","onCompletion =   ");

                    if (paramViewHolder != null) {
                        paramViewHolder.ivPlay.setImageResource(R.drawable.ic_play);
                        paramViewHolder.rlProgress.setVisibility(View.GONE);
                        paramViewHolder.tvPlayProgressTime.setText("00:00");
                    }
                    if ((RecordPlayerManager.this.mDisposable != null) && (!RecordPlayerManager.this.mDisposable.isDisposed())) {
                        RecordPlayerManager.this.mDisposable.dispose();
                    }
                }
            });
            return;
        } catch (IOException e) {
            e.printStackTrace();
            paramViewHolder.ivPlay.setImageResource(R.drawable.ic_play);
            paramViewHolder.rlProgress.setVisibility(View.GONE);
            paramViewHolder.tvPlayProgressTime.setText("00:00");
            ToastUtil.showToast("录音文件播放失败");
        }
    }

    public void releaseMediaPlayer() {
        MediaPlayer localMediaPlayer = this.mPlayer;
        if (localMediaPlayer != null) {
            localMediaPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
        }
    }

    public void setSeekBar(final CallRecordViewHolder paramViewHolder, int paramInt) {
        if ((this.mThumb_normal == null) || (this.mThumb_pressed == null)) {
            this.mThumb_pressed = paramViewHolder.rlProgress.getContext().getResources().getDrawable(R.drawable.thumb);
            this.mThumb_normal = paramViewHolder.rlProgress.getContext().getResources().getDrawable(R.drawable.thumb);
        }
        SeekBar localSeekBar = (SeekBar) paramViewHolder.lProgress;
        localSeekBar.setMax(100);
        localSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean) {
                if ((RecordPlayerManager.this.mPlayer != null) && (paramAnonymousBoolean)) {
                    RecordPlayerManager.this.mPlayer.seekTo(paramAnonymousInt);
                }
                paramViewHolder.tvPlayProgressTime.setText(DateUtil.formatTime(false, RecordPlayerManager.this.mPlayer.getCurrentPosition()));
            }

            public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar) {
                paramAnonymousSeekBar.setThumb(RecordPlayerManager.this.mThumb_pressed);
            }

            public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar) {
                paramAnonymousSeekBar.setThumbOffset(0);
                paramAnonymousSeekBar.setThumb(RecordPlayerManager.this.mThumb_normal);
            }
        });
    }
}
