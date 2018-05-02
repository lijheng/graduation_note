package com.example.li.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.li.activity.NoteActivity;
import com.example.li.activity.SearchActivity;
import com.example.li.bean.NNote;
import com.example.li.data.ClassifyManagement;
import com.example.li.graduation_note.R;
import com.example.li.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

//    上下文
    private Activity mActivity;
    private Context context;

    private int count=0;

    private final String TAG="BaseFragment调试";

    private View view;
    protected ConstraintLayout constraintLayout;

    private SearchView searchView;
    private ImageButton btnAdd;

    public BaseFragment() {
        // Required empty public constructor
        super();
    }


    /**
     * 初始化view
     * @param view
     * @param saveInstanceState
     */
    protected  abstract void initView(View view,Bundle saveInstanceState,ViewGroup container,Context context);


    /**
     * 设置onCreateView中的layoutId
     * @return
     */
    protected abstract int setChildView();

    /**
     * 排列方式
     * @return true：ListView  false:GridView
     */
    protected abstract boolean getIsList();

    /**
     * 设置监听器
     */
    protected abstract void setListener();

    private void initControl(){
        searchView = view.findViewById(R.id.base_search);
        btnAdd = view.findViewById(R.id.add_note);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        this.context = context.getApplicationContext();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view!=null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent!=null){
                parent.removeView(view);
            }
        }else {
            view = inflater.inflate(R.layout.fragment_base, container, false);
            initControl();
            setSearchView();
//        设置子布局，由子类实现
            constraintLayout = view.findViewById(R.id.fragment_other_content);
//        将子类的布局加载到基类中
            View childView = LayoutInflater.from(context).inflate(setChildView(), constraintLayout);
            initView(childView, savedInstanceState, constraintLayout, context);
            setListener();
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clickListener();
    }

    protected void clickListener(){
//        添加note的按钮的监听时间
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), NoteActivity.class);
                intent.putExtra("action",SystemUtil.NEW_NOTE);
                startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
//        监听searchView

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
//                    将当前Fragment是否是ListFragment传过去
                    Intent intent = new Intent(mActivity,SearchActivity.class);
                    intent.putExtra("isList",getIsList());
//                    将Fragment中的SearchView隐藏，并不占用布局空间
                    searchView.setVisibility(View.GONE);
                    startActivity(intent);
                }
                searchView.clearFocus();
            }
        });
    }

//    从Activity回到Fragment时,Fragment依次执行onStart()->onResume
    @Override
    public void onStart() {
//        显示searchView，用于从后续界面返回时
        if (count>0){
            refresh();
        }else {
            count++;
        }
        searchView.setVisibility(View.VISIBLE);
        super.onStart();
    }

    protected abstract void refresh();

    private void setSearchView(){
        if (searchView==null){
            return;
        }
//        改变searchView中的文字颜色
        SearchView.SearchAutoComplete text =
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        text.setTextColor(getResources().getColor(R.color.colorSilverWhite));
//        让searchView默认展开，图标在搜索框外
        searchView.setIconifiedByDefault(false);
//        让searchView默认不聚焦
        searchView.setFocusable(false);
    }

}
