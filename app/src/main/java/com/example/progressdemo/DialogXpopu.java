package com.example.progressdemo;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.core.PositionPopupView;

/**
 * @Description TODO
 * @Author pt
 * @Date 2022/8/17 9:10
 */
public class DialogXpopu extends AttachPopupView {
    public DialogXpopu(@NonNull  Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_dialog;
    }
}
