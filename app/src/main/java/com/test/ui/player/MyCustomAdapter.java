package com.test.ui.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.test.R;

import java.util.List;

/**
 * Класс для отображения списка
 */

public class MyCustomAdapter extends BaseAdapter {

    private List<Item> listData;
    private LayoutInflater layoutInflater;
    private static AudioPlayer player;

    MyCustomAdapter(Context aContext, List<Item> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        player = new AudioPlayer();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Создание отдельных элементов списка
     *
     * @param position    позиция элемента
     * @param convertView использование заново уже существующего элемента списка, который не отображается
     * @param parent      представление родителя
     * @return элемент списка
     */

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.list_item_string);

            convertView.setTag(holder);

            ImageButton btn_play_audio = (ImageButton) convertView.findViewById(R.id.btn_play);

            btn_play_audio.setOnClickListener(v -> {

                if (holder.name.getText().equals(player.getFileName())) {

                    if (player.isRunning())
                        player.stop();

                    else player.play(btn_play_audio, holder.name.getText().toString());

                } else {

                    if (player.isRunning())
                        player.stop();

                    player.play(btn_play_audio, holder.name.getText().toString());
                }
            });


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = this.listData.get(position);
        holder.name.setText(item.getName());

        return convertView;
    }

    public static void stopAudio() {
        if (player != null && player.isRunning()) player.stop();
    }

    static class ViewHolder {
        TextView name;
    }
}

