package com.example.li.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.li.data.CacheData;
import com.example.li.fragment.GridFragment;
import com.example.li.fragment.ListFragment;
import com.example.li.graduation_note.R;
import com.example.li.util.SystemUtil;


public class MainActivity extends AppCompatActivity {
//    图标RGB:20D8E6
    private Toolbar toolbar;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private static final String TAG="MainActivity调试:";

    private static boolean isList;

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        初始化控件
        initControl();
//        初始化数据
        initData();
        setToolBar();

    }


    /*初始化控件*/
    private void initControl(){
        toolbar = findViewById(R.id.toolbar);
        fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout_for_fragment);
    }

    /*初始化数据*/
    private void initData(){
        isList = (boolean) CacheData.get(context,"isList",true);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        如果是列表布局
        if (isList){
            fragment = new ListFragment();
            fragmentTransaction.replace(R.id.frameLayout_for_fragment,fragment);
            fragmentTransaction.commit();
        }else{
            fragment = new GridFragment();
            fragmentTransaction.replace(R.id.frameLayout_for_fragment,fragment);
            fragmentTransaction.commit();
        }
    }


    /*设置toolBar相关属性*/
    private void setToolBar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getDrawable(R.mipmap.gridview_icon));

//        如果可以设置状态栏为亮色
        if (SystemUtil.setStatusBarColor(this)) {
            //        设置状态栏透明
            SystemUtil.statusBarTransparent(this);
        }
        toolbar.setNavigationIcon(R.drawable.genericfoldericon);
//        设置NavigationIcon的点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_toolbar_menu,menu);
        isList = (boolean) CacheData.get(context,"isList",true);
        if (isList){
            menu.findItem(R.id.action_grid).setVisible(true);
            menu.findItem(R.id.action_list).setVisible(false);
        }else{
            menu.findItem(R.id.action_grid).setVisible(false);
            menu.findItem(R.id.action_list).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        isList = !isList;
//        保存用户的排列方式
        CacheData.put(context,"isList",isList);
        invalidateOptionsMenu();//更新menu
        switch (item.getItemId()){
            case R.id.action_grid:
//                将原ListFragment替换为GridFragment
                GridFragment gridFragment = new GridFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                将list_fragment中替换为gridFragment
                transaction.replace(R.id.frameLayout_for_fragment,gridFragment);
//                将该事物添加到返回堆栈，以便用户可以向后导航
//                transaction.addToBackStack(null);
//                提交事务
                transaction.commit();
                break;
            case R.id.action_list:
//                将原GridFragment替换为ListFragment
                ListFragment listFragment = new ListFragment();
                FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                transaction1.replace(R.id.frameLayout_for_fragment,listFragment);
//                transaction1.addToBackStack(null);
                transaction1.commit();
                break;
                default:break;
        }
        return super.onOptionsItemSelected(item);
    }

//    用于更新menu,在需要更新的地方调用invalidateOptionsMenu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        isList = (boolean) CacheData.get(context,"isList",true);
        if (isList){
            menu.findItem(R.id.action_grid).setVisible(true);
            menu.findItem(R.id.action_list).setVisible(false);
        }else {
            menu.findItem(R.id.action_grid).setVisible(false);
            menu.findItem(R.id.action_list).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
