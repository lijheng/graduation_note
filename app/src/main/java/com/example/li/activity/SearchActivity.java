package com.example.li.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.li.adapter.RecyclerAdapter;
import com.example.li.bean.BNote;
import com.example.li.data.AccessDataBySQLite;
import com.example.li.data.ClassifyManagement;
import com.example.li.graduation_note.R;
import com.example.li.util.DateManagement;
import com.example.li.util.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SearchActivity extends AppCompatActivity {

    private boolean isList;

//    上下文
    private Context context;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private AccessDataBySQLite accessDataBySQLite;
    private List<ClassifyManagement> list=null;
    private List<BNote> bNoteList=null;

    private String TAG="SearchView测试:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        初始化控件
        initControl();
//        初始化数据
        initData();
//        Toolbar相关设置
        setToolbar();
    }



    private void initData(){
        Intent intent = getIntent();
        isList = intent.getBooleanExtra("isList",isList);
        context = SearchActivity.this;
        accessDataBySQLite = new AccessDataBySQLite(this);
        recyclerAdapter = new RecyclerAdapter(list,context);
        recyclerView.setAdapter(recyclerAdapter);
    }
    private void initControl(){
        toolbar = findViewById(R.id.search_activity_toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
//        添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    private void setToolbar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        设置状态栏透明
        if (SystemUtil.setStatusBarColor(this)){
            SystemUtil.statusBarTransparent(this);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar_menu,menu);
//        获取menu菜单项得search项
        MenuItem menuItem =  menu.findItem(R.id.search_toolbar_menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
//        设置searchView初始为展开状态
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);
//        searchView.setFocusable(false);
//        给searchView设置边框
        searchView.setBackground(getDrawable(R.drawable.border));
//        根据SearchView类中的id得到SearchView的SearchButton
        ImageView mCollapsedIcon =  searchView.findViewById(R.id.search_mag_icon);
//       设置SearchView中的searchButton图标
        mCollapsedIcon.setImageDrawable(getDrawable(R.mipmap.search_icon));
//       根据SearchView类中的id得到SearchView的CloseButton
        ImageView mCloseButton = searchView.findViewById(R.id.search_close_btn);
//        设置SearchView中loseButton图标
        mCloseButton.setImageDrawable(getDrawable(R.mipmap.delete_src_text_icon));
//        根据SearchView类中的相应id得到SearchView的mSearchSrcTextView
        TextView mSearchSrcTextView = searchView.findViewById(R.id.search_src_text);
        mSearchSrcTextView.setTextColor(getResources().getColor(R.color.colorSilverWhite));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bNoteList = newText.equals("")?null:accessDataBySQLite.queryNote(newText);
                list = bNoteList==null?null:getClassifyList(bNoteList);
                recyclerAdapter = new RecyclerAdapter(list,context);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        long id = list.get(position).getType()==SystemUtil.ITEM?((BNote)list.get(position).getObject()).getId():SystemUtil.INVALID_CLICK;
                        if (id!=SystemUtil.INVALID_CLICK){
                            Intent intent = new Intent(context,NoteActivity.class);
                            intent.putExtra("noteId",id);
                            startActivity(intent);
                        }
                    }
                });
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private List<ClassifyManagement> getClassifyList(List<BNote> list){
        List<ClassifyManagement> classifyManagementList = new ArrayList<>();
        Map<String,List<BNote>> map=new HashMap<>();
//        利用迭代器遍历list
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            BNote note = (BNote) iterator.next();
            String key = DateManagement.getStringShortDate(note.getDate());
            if (map.get(key)==null){
                List<BNote> list1 = new ArrayList<>();
                list1.add(note);
                map.put(key,list1);
            }else {
                map.get(key).add(note);
            }
        }
//        利用迭代器遍历map
        Iterator iterator1 = map.entrySet().iterator();
        while (iterator1.hasNext()){
            Map.Entry entry = (Map.Entry) iterator1.next();
            String key= (String) entry.getKey();
            classifyManagementList.add(new ClassifyManagement(SystemUtil.CLASS_TITLE,key));
            List<BNote> bNotes = (List<BNote>) entry.getValue();
            for (BNote note:list){
//                对应的值作为item
                classifyManagementList.add(new ClassifyManagement(SystemUtil.ITEM,note));
            }
        }
        return classifyManagementList;
    }
}
