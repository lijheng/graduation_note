package com.example.li.util;

import android.util.Log;

import com.example.li.bean.BNote;
import com.example.li.bean.NNote;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by li on 2018/4/23.
 */

public class BeanUtil {
    /**
     * 将B类转换为N类
     * @param bNote
     */
    public static NNote bNoteToNNote(BNote bNote){
        NNote nNote = new NNote();
        nNote.setId(bNote.getId());
        String content = bNote.getContent();
//        根据正则表达式判断content是否含有图片
        Pattern pattern01 = Pattern.compile("<img .*?src=['\"](.*?)['\"].*?/>");
        Pattern pattern02 = Pattern.compile("<voice .*?src=['\"](.*?)['\"].*?/>");
//        取出content中第一行内容
        int end= content.indexOf('\n');
        if (end!=-1){
//            取出文本第一行内容
            String oneLine = content.substring(0,end);
            Matcher matcher01 = pattern01.matcher(oneLine);
            Matcher matcher02 = pattern02.matcher(oneLine);
//            如果第一行为图片
            if (matcher01.find()){
                nNote.setTitle("[图片]");
                nNote.setImage(matcher01.group(1));
                nNote.setVoice(null);
            }else if (matcher02.find()){
//                如果第一行是语音
                nNote.setTitle("[语音]");
                nNote.setVoice(matcher02.group(1));
                nNote.setImage(null);
            }else {
//                如果第一行是纯文本
                nNote.setTitle(oneLine);
            }
//            取出第一行行以外的文本
            String childContent = content.substring(end+1,content.length());

            int end2= childContent.indexOf('\n');
            if (end2!=-1){
//                取出第二行文本
                String twoLine = childContent.substring(0,end2);
                Matcher matcher11 = pattern01.matcher(twoLine);
                Matcher matcher12 = pattern02.matcher(twoLine);
                if (matcher11.find()){
                    nNote.setText("[图片]");
                    if (nNote.getImage()==null){
                        nNote.setImage(matcher11.group(1));
                    }
                }else if (matcher12.find()){
                    nNote.setText("[语音]");
                    if (nNote.getVoice()==null){
                        nNote.setVoice(matcher12.group(1));
                    }
                }else {
                    nNote.setText(twoLine);
                    Matcher matcher1 = pattern01.matcher(childContent);
                    Matcher matcher2 = pattern02.matcher(childContent);
                    if (matcher1.find()){
                        nNote.setImage(matcher1.group(1));
                    }else if (matcher2.find()){
                        nNote.setVoice(matcher2.group(1));
                    }
                }
            }else {
//                纯文本
                nNote.setText(childContent);
            }

        }else {
//            content中只有文本，且没有换行符
            nNote.setTitle(content);
            nNote.setText("");
            nNote.setImage(null);
            nNote.setVoice(null);
        }
        nNote.setTime(bNote.getDate());
        return nNote;
    }
}
