package com.example.li.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.li.bean.BNote;
import com.example.li.data.ClassifyManagement;
import com.example.li.graduation_note.R;
import com.example.li.util.SystemUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnClickListener {
//    List<BNote> bNoteList;
    private List<ClassifyManagement> list;
    private Context context;
    public OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    public RecyclerAdapter(List<ClassifyManagement> list,Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener!=null&&(Integer)v.getTag()!=SystemUtil.INVALID_CLICK){
            itemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_recycler_list,null,false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.get(position).getType()== SystemUtil.CLASS_TITLE){
            holder.tvClassify.setText(list.get(position).getObject().toString());
            holder.tvClassify.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
            holder.tvDate.setVisibility(View.GONE);
            holder.tvContent.setVisibility(View.GONE);
            holder.itemView.setTag(SystemUtil.INVALID_CLICK);
        }else{
            holder.tvClassify.setVisibility(View.GONE);
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.VISIBLE);
            int imageCount =0;
            int voiceCount =0;
            String content = ((BNote)(list.get(position).getObject())).getContent();
            Pattern pattern1 = Pattern.compile("<img .*?src=['\"](.*?)['\"].*?/>");
            Pattern pattern2 = Pattern.compile("<voice .*?src=['\"](.*?)['\"].*?/>");
            Matcher matcher1 = pattern1.matcher(content);
            while (matcher1.find()){
                imageCount++;
                if (imageCount==1){
                    String src = matcher1.group(1);
                    Bitmap bitmap = BitmapFactory.decodeFile(src).copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.setWidth(64);
                    bitmap.setHeight(64);
                    holder.img.setImageBitmap(bitmap);
                }
                content = content.replace(matcher1.group(),"");
            }
            Matcher matcher2 = pattern2.matcher(content);
            while(matcher2.find()){
                voiceCount++;
                if (voiceCount==1&&imageCount==0){
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.voice_play).copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.setHeight(64);
                    bitmap.setWidth(64);
                    holder.img.setImageBitmap(bitmap);
                }
                content = content.replace(matcher2.group(),"");
            }
            holder.tvContent.setText(content);
            holder.tvDate.setText(((BNote)(list.get(position).getObject())).getDate());
//            用于监听回调
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvClassify;
        public TextView tvContent;
        public TextView tvDate;
        public ImageView img;
        public ViewHolder(View itemView) {
            super(itemView);
            tvClassify = itemView.findViewById(R.id.recycler_list_classify);
            tvContent = itemView.findViewById(R.id.recycler_list_content);
            tvDate = itemView.findViewById(R.id.recycler_list_date);
            img = itemView.findViewById(R.id.recycler_list_image);
        }
    }

}
