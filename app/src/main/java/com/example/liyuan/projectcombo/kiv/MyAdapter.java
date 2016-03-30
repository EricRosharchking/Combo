package com.example.liyuan.projectcombo.kiv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liyuan.projectcombo.R;

/**
 * Created by happyoyeah on 30/3/16.
 */
public class MyAdapter extends BaseAdapter {
        private String att_email;
        private String att_name;
        private Context context;
        String[] tool_list;
        int[] images;// = {R.drawable.createnewsong, R.drawable.save, R.drawable.edit, R.drawable.addlyrics, R.drawable.recordlists, R.drawable.share};

        public MyAdapter(Context context, String email, String name, String[] toollist, int[] images) {
            this.context = context;
            this.att_name = name;
            this.att_email = email;
            this.images = images;
//            tool_list = context.getResources().getStringArray(R.array.navigation_toolbox);
            this.tool_list = toollist;
        }

        @Override
        public int getCount() {

            return tool_list.length;
        }
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
        @Override
        public boolean isEnabled(int position) {
            if (position == 0) {
                return false;
            } else {
                return true;
            }
        }
        @Override
        public Object getItem(int i) {
            return tool_list[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
//        public void setImages(int[] images){
//            this.images = images;
//        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View row = null;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            if (view == null) {
                row = inflater.inflate(R.layout.custom_row, viewGroup, false);
            } else {
                row = view;
            }
            TextView titleTextView2 = (TextView) row.findViewById(R.id.textView);
            ImageView titleImageView2 = (ImageView) row.findViewById(R.id.imageView);
            titleTextView2.setText(tool_list[i]);
            titleImageView2.setImageResource(images[i]);
            return row;
        }
}
