package com.test.ui.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.ImageButton;

import com.test.R;
import com.test.ui.AudioUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.test.ui.AudioUtils.AUDIO_ENCODE;
import static com.test.ui.AudioUtils.AUDIO_SAMPLE_RATE;

/**
 * Класс для умравлением воспроизведением аудио
 */

public class AudioPlayer {

    private Singer singer;
    private ImageButton btn_play;
    private String fileName;


    /**
     * Создает класс Singer для воспроизведения аудио
     *
     * @param btn_play кнопка для воспроизведения и остановки аудио
     * @param fileName местоположение запускаевого
     */
    void play(ImageButton btn_play, String fileName) {
        this.fileName = fileName;
        this.btn_play = btn_play;
        singer = new Singer(btn_play, "data/user/0/com.test/files/audiorecord/pcm/" + fileName);
        singer.start();
        btn_play.setBackgroundResource(R.drawable.btn_stop);
    }

    boolean isRunning() {
        if (singer == null) return false;
        return singer.isAlive();
    }

    void stop() {
        btn_play.setBackgroundResource(R.drawable.btn_play);
        singer.stopSing();
        singer.interrupt();
    }

    String getFileName() {
        return fileName;
    }


    /**
     * Класс для воспроизведения аудио
     */

    static class Singer extends Thread {

        private String filePath;
        private AudioTrack audioTrack;
        private ImageButton btn_play;

        Singer(ImageButton btn_play, String filePath) {
            this.btn_play = btn_play;
            this.filePath = filePath;
        }

        void stopSing() {
            audioTrack.stop();
        }

        @Override
        public void run() {

            byte[] byteData;
            File file;
            file = new File(filePath);
            byteData = new byte[(int) file.length()];

            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                in.read(byteData);

                //производим декомпрессию аудио
                AudioUtils.decompress(byteData);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert in != null;
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int intSize = android.media.AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_ENCODE);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_ENCODE, intSize, AudioTrack.MODE_STREAM);

            audioTrack.play();

            audioTrack.write(byteData, 0, byteData.length);

            audioTrack.stop();
            btn_play.setBackgroundResource(R.drawable.btn_play);
            audioTrack.release();
        }
    }
}
