package com.example.android.notepad;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

//自定义SimpleCursorAdapter适配器
public class MyAdapter extends SimpleCursorAdapter {
    private Cursor cursor = null;
    private Context m_context;
    private LayoutInflater miInflater;

    // 自定义视图
    private ListItemView listItemView = null;
    // 视图容器
    private LayoutInflater mListContainer;


    public MyAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to) {
        super(context, layout, c, from, to);
        mListContainer = LayoutInflater.from(context);
        m_context = context;
        cursor = c;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = null;
        listItemView = new ListItemView();
            convertView = mListContainer.inflate(
                    R.layout.noteslist_item, parent, false);
            // 获取控件对象
        listItemView.text1=(TextView)convertView
                .findViewById(android.R.id.text1);
        listItemView.time=(TextView)convertView
                .findViewById(R.id.time);
        // 初始化ListView中每一行布局中的LinearLayout
        listItemView.linearlayout = (LinearLayout) convertView
                .findViewById(R.id.mainlayout);
        // 设置控件集到convertView
        convertView.setTag(listItemView);
        return convertView;
    }

    // view newView函数的返回值
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        listItemView = (ListItemView) view.getTag();
        int bg_color = cursor.getInt(cursor
                .getColumnIndex(NotePad.Notes.COLUMN_NAME_ITEM_BACKGROUND));

//        //test
//        bg_color=context.getResources().getColor(R.color.item_blue);
//        view.setBackgroundColor(Color.rgb(254,250,205));
        listItemView.linearlayout.setBackgroundResource(bg_color);

        listItemView.text1.setText(cursor.getString(cursor
                .getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE)));
        listItemView.time.setText(cursor.getString(cursor
                .getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE)));

        int color=context.getResources().getColor(R.color.blue);
        switch (bg_color){
            case R.drawable.item_blue: color = context.getResources().getColor(R.color.blue);
                break;
            case R.drawable.item_green: color = context.getResources().getColor(R.color.green);
                break;
            case R.drawable.item_gray: color = context.getResources().getColor(R.color.gray);
                break;
            case R.drawable.item_yellow: color = context.getResources().getColor(R.color.yellow);
                break;
            case R.drawable.item_pink: color = context.getResources().getColor(R.color.pink);
                break;
            case R.drawable.item_violet: color = context.getResources().getColor(R.color.violet);
                break;
        }
        listItemView.text1.setTextColor(color);
        listItemView.time.setTextColor(color);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(200, 200, 200, 200);// 设置间距
//        view.setLayoutParams(lp);
    }

    public final class ListItemView {
        public LinearLayout linearlayout;
        public TextView text1;
        public TextView time;
    }
}
