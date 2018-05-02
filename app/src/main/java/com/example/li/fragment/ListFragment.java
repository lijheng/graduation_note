package com.example.li.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.li.activity.NoteActivity;
import com.example.li.adapter.ClassifyAdapter;
import com.example.li.bean.BNote;
import com.example.li.bean.NNote;
import com.example.li.data.AccessDataBySQLite;
import com.example.li.data.ClassifyManagement;
import com.example.li.graduation_note.R;
import com.example.li.util.BeanUtil;
import com.example.li.util.DateManagement;
import com.example.li.util.SystemUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * note以list方式呈现出
 */
public class ListFragment extends BaseFragment {

    private final String TAG="ListFragment";
//    sqLite操作类
    private AccessDataBySQLite accessDataBySQLite;

    private ClassifyAdapter classifyAdapter;

    private ListView listView;
    private List<BNote> bNotes;//存储从数据表中返回的notes
    private ArrayList<ClassifyManagement> classifyManagementList;
    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * 初始化view
     * @param view
     * @param saveInstanceState
     */
    @Override
    protected void initView(View view, Bundle saveInstanceState, ViewGroup container, Context context) {
        listView = view.findViewById(R.id.note_list);
        accessDataBySQLite = new AccessDataBySQLite(view.getContext());
        classifyAdapter = new ClassifyAdapter(getContext(),getDate());
        listView.setAdapter(classifyAdapter);
    }


    /**
     * 设置onCreateView中的layoutId
     * @return
     */
    @Override
    protected int setChildView() {
        return R.layout.fragment_list;
    }

    /**
     * 排列方式
     * @return true：ListView  false:GridView
     */
    @Override
    protected boolean getIsList() {
        return false;
    }

    @Override
    protected void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: "+classifyManagementList.get(position).getType());
                if (classifyManagementList.get(position).getType()==SystemUtil.CLASS_TITLE){
                    position++;
                }
                final long noteId=((NNote)(classifyManagementList.get(position).getObject())).getId();
                Intent intent = new Intent(getContext(), NoteActivity.class);
                intent.putExtra("noteId",noteId);
                intent.putExtra("action",SystemUtil.OPEN_NOTE);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void refresh() {
        getDate();
        classifyAdapter.notifyDataSetChanged();
    }


    /**
     * 根据后台获取的notes转换为分好类的ArrayList
     * @return
     */
    protected ArrayList<ClassifyManagement> getDate(){
        if (classifyManagementList==null){
            classifyManagementList = new ArrayList<>();
        }else {
            classifyManagementList.clear();
            bNotes.clear();
        }
        bNotes = accessDataBySQLite.queryNote();
        List<NNote> notes = new ArrayList<>();

        for (int i=0;i<bNotes.size();i++){
            NNote nNote = BeanUtil.bNoteToNNote(bNotes.get(i));
            notes.add(nNote);
        }
        Map<String,List<NNote>> map = new HashMap<>();
//        利用迭代器遍历notes
        Iterator iterator = notes.iterator();
        while (iterator.hasNext()){
            NNote note = (NNote) iterator.next();
//            将note的时间yyyy年MM月字符串作为键值
            String key = DateManagement.getStringShortDate(note.getTime());
//            如果这个key还没有对应的值,则初始化一个list<Note>用来存储值
            if (map.get(key)==null){
                List<NNote> list = new ArrayList<>();
                list.add(note);
                map.put(key,list);
            }else{
//                如果key中已经有值，则直接将当前note加入到对应的list中去
                map.get(key).add(note);
            }
        }
//        利用迭代器遍历map
        Iterator iterator1 = map.entrySet().iterator();
        while (iterator1.hasNext()){
            Map.Entry entry = (Map.Entry) iterator1.next();
            String key= (String) entry.getKey();
//            将key加入list
            classifyManagementList.add(new ClassifyManagement(SystemUtil.CLASS_TITLE,key));
            List<NNote> list = (List<NNote>) entry.getValue();
            Collections.sort(list);
            for (NNote note:list){
//                对应的值作为item
                classifyManagementList.add(new ClassifyManagement(SystemUtil.ITEM,note));
            }
        }
        return classifyManagementList;
    }

}
