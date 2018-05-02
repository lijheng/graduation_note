package com.example.li.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.li.bean.BNote;
import com.example.li.data.AccessDataBySQLite;
import com.example.li.graduation_note.R;
import com.example.li.util.DateManagement;
import com.example.li.util.ImageManagement;
import com.example.li.util.SystemUtil;
import com.example.li.view.EditTextImage;
import com.example.li.view.VoiceDialog;

import java.io.IOException;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NoteActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, VoiceDialog.DialogCancelListener, DialogInterface.OnDismissListener {

    private static final String IMAGE_TYPE = "image/*";
    private static final int PHOTO_RESULT_GALLERY = 1;

    private EditTextImage editTextImage;
    private ImageButton btnSpeech;
    private ImageButton btnReturn;
    private ImageButton btnRedo;
    private ImageButton btnImage;
    private ImageButton btnDelete;
    private TextView tvNewDate;
    private TextView tvNewTime;
    private Button btnNewFinish;
    private Toolbar toolbar;

    private Context context;
    private MediaPlayer mediaPlayer;
    private VoiceDialog voiceDialog;
    private Date date;
    private int action;//新建还是打开
    private BNote openBNote;//存储一个note
    private AccessDataBySQLite accessDataBySQLite;//数据库
//    用于存储当前note的voice地址
    private String voicePath;
    private long noteId;//存储打开的id
//    private boolean hasText;//
    private final String TAG = "NoteActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Intent intent = getIntent();
        action = intent.getIntExtra("action",2);
        noteId = intent.getLongExtra("noteId",-1);
        init();
        initData();
        initDate();
        setListener();
    }

    private void init(){
        editTextImage = findViewById(R.id.note_editText);
        btnSpeech = findViewById(R.id.speech_icon);
        btnReturn = findViewById(R.id.return_icon);
        btnRedo = findViewById(R.id.redo_icon);
        btnDelete = findViewById(R.id.delete_icon);
        btnImage = findViewById(R.id.image_icon);
        tvNewDate = findViewById(R.id.new_note_date);
        tvNewTime = findViewById(R.id.new_note_time);
        btnNewFinish = findViewById(R.id.new_note_finish);
        toolbar = findViewById(R.id.activity_note_toolbar);
        date = new Date();
        accessDataBySQLite = new AccessDataBySQLite(this);
        context = this;
//        如果可以设置状态栏为亮色
        if (SystemUtil.setStatusBarColor(this)) {
//            设置状态栏透明
            SystemUtil.statusBarTransparent(this);
        }
    }

    /**
     * 时间初始化
     */
    private void initDate(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (action==SystemUtil.NEW_NOTE){
            tvNewDate.setText(DateManagement.getMonthDay(date));
            tvNewTime.setText(DateManagement.getHourMinute(date));
        }else if (action==SystemUtil.OPEN_NOTE){
            date = DateManagement.transDate(openBNote.getDate());
            tvNewDate.setText(DateManagement.getMonthDay(date));
            tvNewTime.setText(DateManagement.getHourMinute(date));
        }

    }

    /**
     * 数据初始化，用于打开一个已存在的note
     */
    private void initData(){
        if (action== SystemUtil.NEW_NOTE||noteId==-1)return;
//        隐藏软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        openBNote = accessDataBySQLite.queryNote(noteId);
        editTextImage.setText(openBNote.getContent());
        Editable editable = editTextImage.getText();
        Pattern pattern1 = Pattern.compile("<img .*?src=['\"](.*?)['\"].*?/>");
        Pattern pattern2 = Pattern.compile("<voice .*?src=['\"](.*?)['\"].*?/>");
        Matcher matcher1 = pattern1.matcher(editable);
        Matcher matcher2 = pattern2.matcher(editable);
        while (matcher1.find()){
            SpannableString spannableString = new SpannableString(matcher1.group());
            ImageSpan imageSpan = ImageManagement.getImageSpan(matcher1.group(1),this);
            spannableString.setSpan(imageSpan,0,spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editable.replace(matcher1.start(),matcher1.end(),spannableString);
        }
        while (matcher2.find()){
            SpannableString spannableString = new SpannableString(matcher2.group());
            spannableString.setSpan(ImageManagement.getImageSpan(R.mipmap.voice_play,context),
                    0,spannableString.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editable.replace(matcher2.start(),matcher2.end(),spannableString);
        }
    }
    private void setListener(){
        btnSpeech.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        btnRedo.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        editTextImage.addTextChangedListener(this);
        btnNewFinish.setOnClickListener(this);
        editTextImage.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.speech_icon:
                Log.d(TAG, "onClick: 语音输入");
                voiceDialog = new VoiceDialog(this);
                voiceDialog.setDialogCancelListener(this);
                voiceDialog.setOnDismissListener(this);
                voiceDialog.show();
                editTextImage.insertVoice(voicePath);
                break;
            case R.id.return_icon:
                editTextImage.actionReturn();
                break;
            case R.id.redo_icon:
                editTextImage.actionRedo();
                break;
            case R.id.image_icon:
                Log.d(TAG, "onClick: 插入图片");
//                打开相册
                openGallery();
                break;
            case R.id.new_note_finish:
//                关闭当前Activity
                finish();
                break;
            case R.id.note_editText:
                ImageClick();
                break;
            case R.id.delete_icon:
                delete(noteId);
                finish();
                break;
            default:break;
        }
    }

    /**
     * 图片点击事件
     */
    private void ImageClick() {
        Editable editable = editTextImage.getText();
        if (editable!=null&&!editable.toString().equals("")) {
            Pattern pattern = Pattern.compile("<voice .*?src=['\"](.*?)['\"].*?/>");
            Matcher matcher = pattern.matcher(editable);
//        如果没有语音，则返回
            int selectStart = editTextImage.getSelectionStart();
            while (matcher.find()){
                int start = matcher.start();
                int end = matcher.end();
//                点击的voice图标
                if (start<=selectStart&&selectStart<=end){
                    SpannableString spannableString = new SpannableString(matcher.group());
//                    播放语音
                    preparePlayVoice(matcher.group(1));
//                    更换语音图片
                    spannableString.setSpan(ImageManagement.getImageSpan(
                            R.mipmap.voice_pause,context), 0,
                            spannableString.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    editable.replace(start,end,spannableString);
                }
            }
        }

    }

    /**
     * 删除笔记
     * @param noteId
     */
    private void delete(long noteId) {
        accessDataBySQLite.deleteNote(noteId);
    }

    /**
     * 打开相册
     */
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(IMAGE_TYPE);
//        打开一个带返回值的activity
        startActivityForResult(intent,PHOTO_RESULT_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        从相册返回数据
        if (requestCode==PHOTO_RESULT_GALLERY){
            if (data!=null){
//                获取系统返回照片的Uri
                Uri uri = data.getData();
//                根据uri从系统表中查询出对应的图片
                Cursor cursor = getContentResolver().query(uri,
                        new String[]{MediaStore.Images.ImageColumns.DATA},
                        null,null,null);
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                获取图片路径
                String imgPath = cursor.getString(index);
                cursor.close();
//                将图片插入到EditTextImage中
                editTextImage.insertImage(imgPath);
            }
        }
    }


    /**
     * 准备播放录音
     */
    private void preparePlayVoice(final String src){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                playVoice(src);
            }
        });

    }

    private void playVoice(String src){
        mediaPlayer = new MediaPlayer();
//        设置文件源
        try {
            mediaPlayer.setDataSource(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        是否循环
        mediaPlayer.setLooping(false);
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        设置监听回调
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                停止播放
                mediaPlayer.release();
                mediaPlayer = null;

            }
        });
        mediaPlayer.start();
    }

    /**
     * 保存当前note，注意保存时如果有语音笔记，则需要将语音图标的imageSpan换成如下格式的字符串：
     * <voice src="voicePath"/>
     */
    private void save(){
        if (editTextImage.getText()==null||editTextImage.getText().toString()=="")return;
//        保存Note的一个对象
        BNote bNote = new BNote();
//            用时间戳充当id
        bNote.setId(System.currentTimeMillis());
        bNote.setContent(editTextImage.getText().toString());
        bNote.setDate(DateManagement.getStringLongDate(date));
//          存入数据库
        accessDataBySQLite.insertNote(bNote);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().equals("")){
            btnNewFinish.setVisibility(View.VISIBLE);
        }else {
            btnNewFinish.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * dialog关闭的监听
     * @param msg
     */
    @Override
    public void dialogCancel(String msg) {
        if (msg==null){
            return;
        }
        voicePath = msg;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss: "+voicePath);
        editTextImage.insertVoice(voicePath);
    }

    /**
     * activity关闭，此时对笔记存储
     * 经测试，finish不会马上执行onStop()、onDestroy()方法
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (!editTextImage.getText().toString().isEmpty()&&action==SystemUtil.NEW_NOTE){
            save();
        }else if (!editTextImage.getText().toString().isEmpty()&&action==SystemUtil.OPEN_NOTE){
            update();
        }
        accessDataBySQLite.closeDB();
    }

    /**
     * 更新数据
     */
    private void update() {
        if (editTextImage.getText().toString()==""||editTextImage.getText()==null) {
            delete(noteId);
            return;
        }
        BNote bNote = new BNote();
        bNote.setId(noteId);
        bNote.setContent(editTextImage.getText().toString());
        bNote.setDate(DateManagement.getStringLongDate(new Date()));
        accessDataBySQLite.updateNote(bNote);
    }
}
