package com.test.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.test.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс для создания фрагмента воспроизведения аудио
 */

public class PlayFragment extends Fragment {

    private static String PATH_DIR = "/data/user/0/com.test/files/audiorecord/pcm";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_player, container, false);

        List<Item> image_details = getListData();
        final ListView listView = (ListView) root.findViewById(R.id.listView);
        listView.setAdapter(new MyCustomAdapter(requireContext(), image_details));

        return root;
    }

    private List<Item> getListData() {
        List<Item> list = new ArrayList<Item>();

        File directory = new File(PATH_DIR);
        File[] files = Objects.requireNonNull(directory.listFiles());


        for (File file : files) {
            list.add(new Item(file.getName()));
        }

        return list;
    }
}
