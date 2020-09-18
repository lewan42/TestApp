package com.test.ui;

import android.media.AudioFormat;

/**
 * Класс для компрессии и декомпрессии аудио
 */

public class AudioUtils {
    public static int AUDIO_ENCODE = AudioFormat.ENCODING_PCM_16BIT;
    public static int AUDIO_SAMPLE_RATE = 192000;


    public static byte compress(short sample) {
        final short BIAS = 132;
        final short CLIP = 32635;

        int sign = sample & 0x8000;
        if (sign != 0) {
            sample = (short) -sample;
            sign = 0x80;
        }

        if (sample > CLIP) sample = CLIP;

        sample += BIAS;

        int exp;

        short temp = (short) (sample << 1);

        for (exp = 7; exp > 0; exp--) {
            if ((temp & 0x8000) != 0) break;
            temp = (short) (temp << 1);
        }

        temp = (short) (sample >> (exp + 3));

        int mantis = temp & 0x000f;

        byte ulawByte = (byte) (sign | (exp << 4) |
                mantis);

        return (byte) ~ulawByte;
    }

    public static void decompress(byte[] ulawBytes) {

        for (int i = 0; i < ulawBytes.length; i++) {

            ulawBytes[i] = (byte) (~ulawBytes[i]);

            int sign = ulawBytes[i] & 0x80;

            int exp = (ulawBytes[i] & 0x70) >> 4;

            int mantis = ulawBytes[i] & 0xf;

            int rawValue = (mantis << (12 - 8 + (exp - 1))) + (132 << exp) - 132;

            if (((sign != 0))) {
                ulawBytes[i] = (byte) -rawValue;

            } else ulawBytes[i] = (byte) rawValue;
        }

    }
}
