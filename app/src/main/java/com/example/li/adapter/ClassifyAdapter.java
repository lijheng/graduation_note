package com.example.li.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.li.bean.NNote;
import com.example.li.data.ClassifyManagement;
import com.example.li.graduation_note.R;
import com.example.li.util.SystemUtil;

import java.util.ArrayList;

/**
 * Created by li on 2018/4/17.
 */

public class ClassifyAdapter extends BaseAdapter {
    private ArrayList<ClassifyManagement> list;//经过处理的list数据集
    private Context mContext;
    private LayoutInflater layoutInflater;

    public ClassifyAdapter(Context context,ArrayList<ClassifyManagement> list){
        this.list = list;
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override

    public int getCount() {
        return list.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)){
//            该条数据是内容
            case SystemUtil.ITEM:
                ViewHolder2 viewHolder2 = null;
                if (convertView==null){
                    viewHolder2 = new ViewHolder2();
                    convertView = layoutInflater.inflate(R.layout.layout_list_note,null);
                    viewHolder2.tvNoteTitle = convertView.findViewById(R.id.note_title);
                    viewHolder2.tvNoteContent = convertView.findViewById(R.id.note_content_text);
                    viewHolder2.tvNoteTime = convertView.findViewById(R.id.note_time);
                    viewHolder2.imgNoteContent = convertView.findViewById(R.id.note_content_img);
                    convertView.setTag(viewHolder2);
                }else{
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                }
                viewHolder2.tvNoteTitle.setText(((NNote)(list.get(position).getObject())).getTitle());
                viewHolder2.tvNoteContent.setText(((NNote)(list.get(position).getObject())).getText());
                viewHolder2.tvNoteTime.setText(((NNote)(list.get(position).getObject())).getTime());
                String src = ((NNote)(list.get(position).getObject())).getImage();
                if (src!=null){
//                    根据路径获取bitmap，并且让bitmap的isMutable=true,即bitmap可修改
                    Bitmap bitmap = BitmapFactory.decodeFile(src).copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.setWidth(64);
                    bitmap.setHeight(64);
                    viewHolder2.imgNoteContent.setImageBitmap(bitmap);
                }else if(((NNote)(list.get(position).getObject())).getVoice()!=null){
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                            R.mipmap.voice_play).copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.setHeight(64);
                    bitmap.setWidth(64);
                    viewHolder2.imgNoteContent.setImageBitmap(bitmap);
                }else {
                    viewHolder2.imgNoteContent.setImageBitmap(null);
                }
                break;
//            该条数据是时间分类
            case SystemUtil.CLASS_TITLE:
                ViewHolder1 viewHolder1 =null;
                if (convertView==null){
                    viewHolder1 = new ViewHolder1();
                    convertView = layoutInflater.inflate(R.layout.layout_classify,null);
                    viewHolder1.tvDate = convertView.findViewById(R.id.id_date);
                    convertView.setTag(viewHolder1);
                }else{
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                }
                viewHolder1.tvDate.setText((String)list.get(position).getObject());
                break;
                default:break;
        }
        return convertView;
    }

//    分类
    class ViewHolder1{
        TextView tvDate;
    }

//    具体item
    class ViewHolder2{
        TextView tvNoteTitle;
        TextView tvNoteContent;
        TextView tvNoteTime;
        ImageView imgNoteContent;
    }
}
