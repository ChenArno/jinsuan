package com.tianji.jingsuan.utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.tianji.jingsuan.App;
import com.tianji.jingsuan.R;

import java.util.HashMap;

/**
 * Created by chencentury on 2018/5/7.
 */

//SoundsHelper.getInstance().playSound(SoundsHelper.OPEN_IN_DOOR_SUCCESS, 1, 0);  测试左声道
//SoundsHelper.getInstance().playSound(SoundsHelper.OPEN_IN_DOOR_SUCCESS, 0, 1);  测试右声道
public class SoundsHelper {
    public static final int DO_NOT_STACK_FRONT_SIDE= 1; // 正面放置，请勿堆叠
    public static final int IS_FAILE_PUT_AGAIN= 2; // 识别不成功，请重新放置
    public static final int PAY_SUCCESS_WECLOME_NEXT_TIME = 3; // 支付成功，欢迎下次光临
    // 太长需要单独用MediaPlayer播放
    public static final int HAS_NO_PAY_COMMODITY = 7;

    private static SoundPool mSoundPool;
    private static int streamIdIn;
    private static int streamIdOut;
    private static HashMap soundPoolMap;
    private static MediaPlayer mediaPlayer;

    private static SoundsHelper soundsHelper = null;

    public static SoundsHelper getInstance() {
        if (soundsHelper == null) {
            soundsHelper = new SoundsHelper();
            soundsHelper.initSoundPool();
        }
        return soundsHelper;
    }

    // 初始化SoundPool,加载好音频文件
    public void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0以上
            // 设置描述音频流信息的属性
            AudioAttributes aab = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(5) // 设置允许同时播放的流的最大值，就是允许多少个音频同时播放
                    .setAudioAttributes(aab) // 完全可以设置为null
                    .build();
        } else { // 5.0以下
            /**
             * SoundPool(int maxStreams, int streamType, int srcQuality)
             从android5.0开始此方法被标记为过时，稍微说以下几个参数。
             1.maxStreams :允许同时播放的流的最大值
             2.streamType ：音频流的类型描述，
             在Audiomanager中有种类型声明，游戏应用通常会使用流媒体音乐。
             3. srcQuality：采样率转化质量
             */
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(DO_NOT_STACK_FRONT_SIDE, mSoundPool.load(App.getContext(), R.raw.do_not_stack_the_front_side, 1));
        soundPoolMap.put(IS_FAILE_PUT_AGAIN, mSoundPool.load(App.getContext(), R.raw.is_faile_put_again, 1));
        soundPoolMap.put(PAY_SUCCESS_WECLOME_NEXT_TIME, mSoundPool.load(App.getContext(), R.raw.pay_success_and_welcome_next_time, 1));

        // 1. soundID：加载音乐资源时的返回值，int load(String path, int priority),这个int返回值就是soundID
        // 2. streamID：播放时返回的值，即play()方法的返回值
        /**SoundPool支持多个音频文件同时播放(组合音频也是有上限的)，延时短，比较适合短促、密集的场景，是游戏开发中音效播放的福音。*/
        // 加载指定音频，几个load方法和上文提到的MediaPlayer基本一致，不做多的解释
        // int load(AssetFileDescriptor afd, int priority)
        // int load(Context context, int resId, int priority)
        // int load(String path, int priority)
        // int load(FileDescriptor fd, long offset, long length, int priority)
        // 播放声音，soundID:音频id； left/rightVolume:左右声道(默认1,1)；loop:循环次数(-1无限循环)；rate:播放速率(1为标准)
        // final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        // 卸载指定音频 final boolean unload(int soundID)
        // 通过流id暂停播放 final void pause(int streamID)
        // 恢复播放 final void resume(int streamID)
        // 暂停所有音频的播放 final void autoPause()
        // 恢复所有暂停的音频播放 final void autoResum()
        // 停止指定音频播放 final void stop(int streamID)
        // 释放资源(很重要) final void release()
        // 设置指定id的音频循环播放次数 final void setLoop(int streamID, int loop)
        // 设置加载监听(因为加载是异步的，需要监听加载，完成后再播放) void setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener listener)
        // 设置优先级(同时播放个数超过最大值时，优先级低的先被移除) final void setPriority(int streamID, int priority)
        // 设置指定音频的播放速率，0.5~2.0(rate>1:加快播放，反之慢速播放) final void setRate(int streamID, float rate)
    }

    public void playSound(int streamID, int left, int right) {
        switch (streamID) {
            case HAS_NO_PAY_COMMODITY: // 太长需要单独用MediaPlayer播放
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(App.getContext(), R.raw.pay_success_and_welcome_next_time);
                    mediaPlayer.setVolume(left, right); // 这个音频要出门音响播放
                    mediaPlayer.start();
                } else {
                    mediaPlayer.start();
                }
                break;
            default: // 其他音频用SoundPool播放
                if (mSoundPool == null) {
                    initSoundPool();
                } else {
                    if (left == 1) {
                        mSoundPool.stop(streamIdIn);
                        streamIdIn = mSoundPool.play((int) soundPoolMap.get(streamID), left, right, 1, 0, 1);
                    } else if (right == 1) {
                        mSoundPool.stop(streamIdOut);
                        streamIdOut = mSoundPool.play((int) soundPoolMap.get(streamID), left, right, 1, 0, 1);
                    }
                }
                break;
        }
    }

    // 释放资源
    public void releaseAndDestroy() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
