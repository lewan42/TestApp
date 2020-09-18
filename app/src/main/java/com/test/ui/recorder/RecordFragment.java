package com.test.ui.recorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.test.MainActivity;
import com.test.R;

/**
 * Класс для создания фрагмента записи аудио
 */

public class RecordFragment extends Fragment {

    private ImageButton btn_recorder;
    private static Context context;
    private AudioRecorder recorder;


    /**
     * Разрешение на использование микрофона
     */
    private void getPermissionToRecord() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 123);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recorder, container, false);

        Chronometer chronometer = root.findViewById(R.id.chronometer);

        getPermissionToRecord();

        context = requireContext();
        recorder = new AudioRecorder(chronometer);
        btn_recorder = root.findViewById(R.id.record);

        btn_recorder.setOnClickListener(e -> {

            int permissionStatus = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO);

            if (permissionStatus != 0) {
                getPermissionToRecord();

            } else if (recorder == null || !recorder.isRecord()) {

                recorder = new AudioRecorder(chronometer);
                recorder.startRecord();
                btn_recorder.setBackgroundResource(R.drawable.img_stop_record);

            } else {

                recorder.stop();
                MainActivity.incrementBadge();
                btn_recorder.setBackgroundResource(R.drawable.img_start_record);
            }
        });

        return root;
    }

    static Context getInstance() {
        return context;
    }
}

