package com.example.li.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.li.graduation_note.R;
import com.example.li.util.ImageManagement;


import java.util.Stack;

import static android.text.style.DynamicDrawableSpan.ALIGN_BASELINE;
import static android.text.style.DynamicDrawableSpan.ALIGN_BOTTOM;

/**
 * 可以插入图片、语音的EditText
 * 支持撤销、恢复
 * Created by li on 2018/4/20.
 */

public class EditTextImage extends AppCompatEditText {
    private Editable mEditable;
//    上下文
    private Context mContext;
//    return栈
    private Stack<EditTextAction> returnStack = new Stack<>();
//    redo栈
    private Stack<EditTextAction> redoStack = new Stack<>();
//    操作序号
    private int index = 0;
//    是否用户通过软键盘进行操作
    private boolean actionByKeyboard = true;

    public EditTextImage(Context context) {
        super(context);
        mContext = context;
    }

    public EditTextImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mEditable = getText();
        this.addTextChangedListener(new MyWatcher());
    }

    public EditTextImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mEditable = getText();
        this.addTextChangedListener(new MyWatcher());
    }

    public Stack<EditTextAction> getRedoStack() {
        return redoStack;
    }

    public Stack<EditTextAction> getReturnStack() {
        return returnStack;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        防止自动滑到焦点位置
        if (event.getAction()==MotionEvent.ACTION_MOVE){
            this.clearFocus();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 插入图片
     * @param imgPath 插入图片的路径
     */
    public void insertImage(String imgPath){
//        屏幕宽度
        int screenWidth;
        String str = "<img src=\""+imgPath+"\"/>";
        Bitmap bitmap = null;

        SpannableString spannableString = new SpannableString(str);

//        BitmapFactory.Option:Bitmap参数设置的类
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[100*1024];
//        设置图片大小为原来的1/2;
        options.inSampleSize = 2;
        bitmap = BitmapFactory.decodeFile(imgPath,options);
//        将bitmap转换为drawable
        Drawable drawable = new BitmapDrawable(bitmap);

//        获取屏幕的宽度
        Resources resources = mContext.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
//        density = dpi/160
        float density = displayMetrics.density;
        screenWidth = displayMetrics.widthPixels;
//        获取图片的高宽,返回值单位应是dp,px=dp*(dpi/160)
        float imgWidth = drawable.getIntrinsicWidth();
        float imgHeight = drawable.getIntrinsicHeight();
//        如果图片的宽度与屏幕宽度不一
        if (imgWidth*density!=screenWidth){
            float scale = screenWidth/imgWidth;
            imgHeight = scale*imgHeight;
            imgWidth = scale*imgWidth;
        }
//        重新设置图片的宽高
        drawable.setBounds(0,0,(int)imgWidth,(int)imgHeight);

//        ImageSpan imageSpan = new ImageSpan(drawable,ALIGN_BASELINE);
//        设置成ALiGN_BASELINE，会掩盖部分字体
       ImageSpan imageSpan = new ImageSpan(drawable,ALIGN_BOTTOM);
//        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE:表示前后都不应用该效果
        spannableString.setSpan(imageSpan,0,spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getText的返回类型为Editable，Editable是一个接口类型，对它实例化对象做出的任何操作都是
//        对原有实例化对象操作的，内存地址是一个
        Editable editable = getText();
//        如果插入图片在第一行，且第一行有内容
//        当前光标位置
        int selectionStart = getSelectionStart();
//        光标不在控件的首位置
        if (selectionStart!=0){
//              获取光标前一个字符
            char[] dest=new char[1];
            editable.getChars(selectionStart-1,selectionStart,dest,0);
//            如果前一个字符不是换行符
            if (dest[0]!='\n'){
                editable.insert(getSelectionStart(),"\n");
                editable.insert(getSelectionStart(),spannableString);
                editable.insert(getSelectionStart(),"\n");
            }else {
                editable.insert(getSelectionStart(),spannableString);
                editable.insert(getSelectionStart(),"\n");
            }
        }else {
            editable.insert(getSelectionStart(),spannableString);
            editable.insert(getSelectionStart(),"\n");
        }

    }

    public void insertVoice(String voicePath){
        if (voicePath==null){
            return;
        }
        String str = "<voice src=\""+voicePath+"\"/>";
        SpannableString spannableString = new SpannableString(str);
        ImageSpan imageSpan = ImageManagement.getImageSpan(R.mipmap.voice_play,mContext);
        spannableString.setSpan(imageSpan,0,spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable editable = getText();
        editable.insert(getSelectionStart(),spannableString);
    }

    /**
     * 重做
     */
    public void actionRedo(){
        if (redoStack.empty())return;
        actionByKeyboard = false;
        EditTextAction editTextAction = redoStack.pop();
        returnStack.push(editTextAction);
        if (editTextAction.actionAdd){
            mEditable.insert(editTextAction.startCursor,editTextAction.actionTarget);
            if (editTextAction.startCursor == editTextAction.endCursor){
                this.setSelection(editTextAction.startCursor+editTextAction.actionTarget.length());
            }else{
                this.setSelection(editTextAction.startCursor,editTextAction.endCursor);
            }
        }else{
            mEditable.delete(editTextAction.startCursor,
                    editTextAction.startCursor+editTextAction.actionTarget.length());
            this.setSelection(editTextAction.startCursor,editTextAction.startCursor);
        }
        actionByKeyboard = true;
        if (!redoStack.empty()&&redoStack.peek().index==editTextAction.index){
            actionRedo();
        }
    }

    /**
     * 撤销
     */
    public void actionReturn(){
        if (returnStack.empty())return;
        actionByKeyboard = false;
        EditTextAction editTextAction = returnStack.pop();
        redoStack.push(editTextAction);
        if (editTextAction.actionAdd){
//            撤销添加
            mEditable.delete(editTextAction.startCursor,
                    editTextAction.startCursor+editTextAction.actionTarget.length());
            this.setSelection(editTextAction.startCursor,editTextAction.startCursor);
        }else{
//            撤销删除
            mEditable.insert(editTextAction.startCursor,editTextAction.actionTarget);
            if (editTextAction.startCursor==editTextAction.endCursor){
                this.setSelection(editTextAction.startCursor+editTextAction.actionTarget.length());
            }else {
                this.setSelection(editTextAction.startCursor,editTextAction.endCursor);
            }
        }
        actionByKeyboard = true;
        if (!returnStack.empty()&&returnStack.peek().index == editTextAction.index){
            actionReturn();
        }
    }

    private class EditTextAction{
//        操作字符
        CharSequence actionTarget;
//        光标开始位置
        int startCursor;
//        光标结束位置
        int endCursor;
//        操作类型
        boolean actionAdd;
//        操作序号
        int index;

        public EditTextAction(CharSequence actionTarget,int startCursor,boolean actionAdd){
            this.actionTarget = actionTarget;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.actionAdd = actionAdd;
        }

        public void setIndex(int index){
            this.index = index;
        }

        public void setSelectCount(int count){
            this.endCursor = endCursor+count;
        }
    }

    private class MyWatcher implements TextWatcher{
        /*原有的文本s中，从start开始的count个字符将会被一个新的长度为after的文本替换，
        注意这里是将被替换，还没有被替换*/
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (!actionByKeyboard)return;
            int end = start+count;
            if (end>start&&end<=s.length()){
//                charSequence为新增的序列，subSequence返回一个新的字符序列[start,end)
                CharSequence charSequence = s.subSequence(start,end);

                if (charSequence.length()>0){
                    EditTextAction editTextAction = new EditTextAction(charSequence,start,false);
//                    用户进行了选择并操作
                    if (count>1){
                        editTextAction.setSelectCount(count);
                    }else if (count==1&&count==after){
//                        一个字符的选择操作
                        editTextAction.setSelectCount(count);
                    }
                    returnStack.push(editTextAction);
                    redoStack.clear();
                    editTextAction.setIndex(++index);
                }
            }
        }

        /*在原有的文本s中，从start开始的count个字符替换长度为before的旧文本，
        注意这里没有将要之类的字眼，也就是说一句执行了替换动作*/
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!actionByKeyboard)return;
            int end = start+count;
            if (end>start){
                CharSequence charSequence = s.subSequence(start,end);
//                添加文字
                if (charSequence.length()>0){
                    EditTextAction editTextAction = new EditTextAction(charSequence,start,true);
                    returnStack.push(editTextAction);
                    redoStack.clear();
                    if (before>0){
                        editTextAction.setIndex(index);
                    }else {
                        editTextAction.setIndex(++index);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s!=mEditable){
                mEditable = s;
            }
        }
    }
}
