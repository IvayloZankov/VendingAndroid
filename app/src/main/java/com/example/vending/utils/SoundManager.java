package com.example.vending.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.vending.R;

public class SoundManager {

    private static SoundManager manager;

    public static SoundManager getInstance() {
        if (manager == null) {
            manager = new SoundManager();
        }
        return manager;
    }

    private final SoundPool soundPool;

    public SoundManager() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder().setMaxStreams(3).setAudioAttributes(audioAttributes).build();
    }

    private int productClick, coinClick, error;

    public void loadSounds(Context context) {
        productClick = soundPool.load(context, R.raw.click_default, 1);
        coinClick = soundPool.load(context, R.raw.click_coin, 1);
        error = soundPool.load(context, R.raw.out_of_order, 1);
    }

    public void playClick() {
        soundPool.play(productClick, 1f, 1f, 1, 0, 1);
    }

    public void playCoin() {
        soundPool.play(coinClick, 1f, 1f, 1, 0, 1);
    }

    public void playError() {
        soundPool.play(error, 1f, 1f, 1, 0, 1);
    }

    public void cleanUp() {
        soundPool.release();
        manager = null;
    }
}
