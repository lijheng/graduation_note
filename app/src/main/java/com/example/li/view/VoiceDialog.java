package com.example.li.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.li.graduation_note.R;
import com.example.li.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by li on 2018/4/21.
 * 语音输入dialog
 */

public class VoiceDialog extends Dialog implements View.OnTouchListener {
    private final String TAG = "VoiceDialog";
    private Context context;
    private AnimationDrawable animationDrawable;
    private MediaRecorder mediaRecorder;//录音
    private String filePath;
    private ExecutorService executorService;
//    录音开始时间和结束时间
    private long startTime,stopTime;

    private DialogCancelListener dialogCancelListener;
    private ImageView imageView;

    public VoiceDialog(Context context){
        super(context,R.style.MyDialogTheme);
        this.context = context;
        init();
    }

    /**
     * 通过该接口传值
     */
    public interface DialogCancelListener{
        //        具体的方法
        public void dialogCancel(String msg);
    }

    public void setDialogCancelListener(DialogCancelListener dialogCancelListener) {
        this.dialogCancelListener = dialogCancelListener;
    }

    /**
     * 初始化dialog
     */
    private void init(){
        this.setContentView(R.layout.dialog_voice_input);

        /*dialog = new Dialog(context, R.style.MyDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_voice_input,null);
//        dialog.show();
        dialog.setContentView(view);*/
        imageView = this.findViewById(R.id.dialog_voice_input);
        imageView.setBackgroundResource(R.drawable.voice_dialog_anim);

        imageView.setOnTouchListener(this);
//        animationDrawable = (AnimationDrawable) imageView.getBackground();
//        animationDrawable.start();
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.width = SystemUtil.dpToPx(260,context);
        layoutParams.height = SystemUtil.dpToPx(260,context);
        layoutParams.alpha = 0.7f;
        this.getWindow().setAttributes(layoutParams);
//        使用单线程
        executorService = Executors.newSingleThreadExecutor();
    }


    /**
     * 准备开始录音
     */
    private void prepareStartRecord(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                releaseRecord();
                if (!startRecord()){
                    failRecord();
                }
            }
        });
    }

    /**
     * 准备结束录音
     */
    private void prepareStopRecord(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (!stopRecord()){
                    failRecord();
                }
                releaseRecord();
                if (dialogCancelListener!=null){
                    dialogCancelListener.dialogCancel(filePath);
                }
                executorService.shutdown();
                dismiss();
                cancel();
            }
        });
    }

    /**
     * 开始录音
     * @return 录音是否成功
     */
    private boolean startRecord(){
        File dir = SystemUtil.makeDir(context,"noteVoice");
//        使用UUID作为文件名，防止重复
        String fileName = UUID.randomUUID().toString()+".amr";
        Log.d(TAG, "recording: "+fileName);
        File file = new File(dir,fileName);
        filePath = file.getAbsolutePath();
//        初始化音频录制类
        mediaRecorder = new MediaRecorder();
//        设置音频输出位置
        mediaRecorder.setOutputFile(filePath);
//        设置麦克风为音源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        设置音频格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
//        设置音频编码
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
//        准备 开始
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
//            开始时间
            startTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 停止录音
     * @return
     */
    private boolean stopRecord(){
        mediaRecorder.stop();
        stopTime = System.currentTimeMillis();
//        录音时间
        final int second = (int) ((stopTime-startTime)/1000);
//        录音少于2分钟，不保存
        if (second<2){
            releaseRecord();
            if (filePath!=null){
                File file = new File(filePath);
                file.delete();
                filePath=null;
            }
            return false;
        }
        return true;
    }
    /**
     * 录音失败
     */
    private void failRecord(){
        Toast.makeText(context, "录音失败，请重新记录", Toast.LENGTH_SHORT).show();
    }
    /**
     * 释放上一次录音
     */
    private void releaseRecord(){
        if (mediaRecorder!=null){
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        当手指按下时
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            animationDrawable = (AnimationDrawable) imageView.getBackground();
            animationDrawable.start();
//            准备开始录音
            prepareStartRecord();
        } else if (event.getAction() == MotionEvent.ACTION_UP){
//            当手指抬起时
            animationDrawable = (AnimationDrawable) imageView.getBackground();
            animationDrawable.stop();
            imageView.setBackgroundResource(R.drawable.voice_dialog_anim);
//            准备结束录音
            prepareStopRecord();
        }
        return true;
    }

    @Override
    public void show() {
        super.show();
    }
}
