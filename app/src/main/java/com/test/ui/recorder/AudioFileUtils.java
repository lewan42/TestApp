package com.test.ui.recorder;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Класс для создания аудио файла
 */

class AudioFileUtils {

    private static String rootPath = "audiorecord";
    private final static String AUDIO_PCM_BASEPATH = "/" + rootPath + "/pcm/";


    static String getPcmFileAbsolutePath() {

        Date currentDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);

        String fileName = dateText + " " + timeText;

        String mAudioRawPath;
        if (!fileName.endsWith(".pcm")) {
            fileName = fileName + ".pcm";
        }
        String fileBasePath = RecordFragment.getInstance().getFilesDir() + AUDIO_PCM_BASEPATH;

        File file = new File(fileBasePath);

        if (!file.exists()) {
            file.mkdirs();
        }

        mAudioRawPath = fileBasePath + fileName;

        return mAudioRawPath;
    }
}
