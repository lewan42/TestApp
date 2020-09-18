package com.test.ui.recorder;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

import com.test.ui.AudioUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.test.ui.AudioUtils.AUDIO_ENCODE;
import static com.test.ui.AudioUtils.AUDIO_SAMPLE_RATE;

/**
 * Класс для записи аудио
 */

class AudioRecorder {

    private static final String TAG = "AudioRecorder";

    private int audioChannel = AudioFormat.CHANNEL_IN_MONO;

    private int bufferSizeInBytes = 0;
    private AudioRecord audioRecord;
    private Status status;
    private String pcmFileName;

    private FileOutputStream fosPcm;

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;


    AudioRecorder(Chronometer chronometer) {
        this.chronometer = chronometer;
        pcmFileName = AudioFileUtils.getPcmFileAbsolutePath();
        status = Status.STATUS_READY;

        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());
    }


    private void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    private void stopChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }


    void startRecord() {

        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, audioChannel, AUDIO_ENCODE);
        int audioInput = MediaRecorder.AudioSource.MIC;
        audioRecord = new AudioRecord(audioInput, AUDIO_SAMPLE_RATE, audioChannel, AUDIO_ENCODE, bufferSizeInBytes);

        if (status == Status.STATUS_NO_READY) {
            throw new IllegalStateException("not init");
        }
        if (status == Status.STATUS_START) {
            throw new IllegalStateException("is recording ");
        }

        Log.d("AudioRecorder", "===startRecord===" + audioRecord.getState());

        audioRecord.startRecording();
        startChronometer();

        new Thread(() -> {
            NoiseSuppressor noiseSuppressor = NoiseSuppressor.create(audioRecord.getAudioSessionId());
            if (noiseSuppressor != null) {
                noiseSuppressor.setEnabled(true);
            }

            AutomaticGainControl automaticGainControl = AutomaticGainControl.create(audioRecord.getAudioSessionId());
            if (automaticGainControl != null) {
                automaticGainControl.setEnabled(true);
            }

            recordToFile();
        }).start();
    }

    void stop() {
        if (status != Status.STATUS_START) {
            throw new IllegalStateException("not recording");

        } else {
            stopRecorder();
            stopChronometer();
            resetChronometer();

            rewriteAudioToULaw();
            status = Status.STATUS_READY;
        }
    }

    boolean isRecord() {
        return status == Status.STATUS_START;
    }


    private void rewriteAudioToULaw() {


        new Thread() {
            @Override
            public void run() {
                File file = new File(pcmFileName);
                try {

                    byte[] pcmData = new byte[(int) file.length()];

                    FileInputStream fis = new FileInputStream(file);
                    fis.read(pcmData);
                    fis.close();

                    //делаем компрес аудио
                    for (int i = 0; i < pcmData.length; i++) {
                        pcmData[i] = AudioUtils.compress(pcmData[i]);
                    }

                    OutputStream outputStream = new FileOutputStream(pcmFileName);
                    outputStream.write(pcmData);
                    outputStream.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                releaseRecorder();
            }
        }.start();
    }


    private void releaseRecorder() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    private void stopRecorder() {
        if (audioRecord != null) {
            try {
                audioRecord.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Записываем аудио в линейный PCM файл
     */
    private void recordToFile() {
        byte[] audiodata = new byte[bufferSizeInBytes];

        int readsize = 0;
        try {
            fosPcm = new FileOutputStream(pcmFileName, true);
        } catch (FileNotFoundException e) {
            Log.e("AudioRecorder", e.getMessage());
        }
        status = Status.STATUS_START;
        while (status == Status.STATUS_START && audioRecord != null) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fosPcm != null) {
                try {

                    int sum = 0;
                    for (int i = 0; i < readsize; i++) {
                        sum += Math.abs(audiodata[i]);
                    }

                    if (readsize > 0) {
                        int raw = sum / readsize;
                        int lastVolumn = raw > 32 ? raw - 32 : 0;
                        Log.i(TAG, "writeDataTOFile: volumn -- " + raw + " / lastvolumn -- " + lastVolumn);
                    }

                    if (readsize > 0 && readsize <= audiodata.length)
                        fosPcm.write(audiodata, 0, readsize);

                } catch (IOException e) {
                    Log.e("AudioRecorder", e.getMessage());
                }
            }
        }
        try {
            if (fosPcm != null) {
                fosPcm.close();
            }
        } catch (IOException e) {
            Log.e("AudioRecorder", e.getMessage());
        }
    }


    public enum Status {
        STATUS_NO_READY,
        STATUS_READY,
        STATUS_START
    }
}