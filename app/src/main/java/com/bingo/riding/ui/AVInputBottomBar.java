package com.bingo.riding.ui;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.bingo.riding.ChatActivity;
import com.bingo.riding.R;
import com.bingo.riding.dao.ChatMessage;
import com.bingo.riding.event.InputBottomBarEvent;
import com.bingo.riding.event.InputBottomBarTextEvent;
import com.bingo.riding.utils.DaoUtils;

import de.greenrobot.event.EventBus;


/**
 * Created by wli on 15/7/24.
 * 专门负责输入的底部操作栏，与 activity 解耦
 * 当点击相关按钮时发送 InputBottomBarEvent，需要的 View 可以自己去订阅相关消息
 */
public class AVInputBottomBar extends FrameLayout {

    /**
     * 最小间隔时间为 1 秒，避免多次点击
     */
    private final int MIN_INTERVAL_SEND_MESSAGE = 1000;

    /**
     * 发送文本的Button
     */
    private FrameLayout groupchat_sendBtn;
    private EditText groupchat_input;

    private String conversationId = null;
    private DaoUtils daoUtils;

    public AVInputBottomBar(Context context) {
        super(context);
        initView(context);
    }

    public AVInputBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(final Context context) {
        View.inflate(context, R.layout.input_bottom_bar, this);

        groupchat_sendBtn = (FrameLayout) findViewById(R.id.groupchat_sendBtn);
        groupchat_input = (EditText) findViewById(R.id.groupchat_input);

        setEditTextChangeListener();

        groupchat_sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = groupchat_input.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getContext(), R.string.message_is_null, Toast.LENGTH_SHORT).show();
                    return;
                }

                groupchat_input.setText("");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        groupchat_sendBtn.setEnabled(true);
                    }
                }, MIN_INTERVAL_SEND_MESSAGE);

                if (conversationId == null){
                    conversationId = ((ChatActivity)context).getConversationId();
                    daoUtils = DaoUtils.getInstance(context.getApplicationContext());
                }

                ChatMessage chatMessage = new ChatMessage();

                chatMessage.setTimestamp(System.currentTimeMillis());
                chatMessage.setIoType(AVIMMessage.AVIMMessageIOType.AVIMMessageIOTypeOut.getIOType());
                chatMessage.setIsRead(true);
                chatMessage.setConversationId(conversationId);
                chatMessage.setContent(content);
                daoUtils.insertChatMessage(chatMessage);

                EventBus.getDefault().post(
                        new InputBottomBarTextEvent(InputBottomBarEvent.INPUTBOTTOMBAR_SEND_TEXT_ACTION, content, conversationId));
            }
        });
    }

    private void setEditTextChangeListener() {
        groupchat_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}
