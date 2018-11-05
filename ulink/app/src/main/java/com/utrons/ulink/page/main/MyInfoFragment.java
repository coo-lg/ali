package com.utrons.ulink.page.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.utrons.ulink.R;
import com.utrons.ulink.page.account.AccountInfoActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInfoFragment extends Fragment {
    private View mLayoutMyInfo;
    private TextView mTvUsername;

    private View mLayoutOta;
    private View mLayoutMessage;
    private View mLayoutAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_my_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        this.mTvUsername.setText(getNickName());

        this.mLayoutMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginBusiness.isLogin()) {
                    LoginBusiness.login(new ILoginCallback() {
                        @Override
                        public void onLoginSuccess() {
                            mTvUsername.setText(getNickName());
                        }

                        @Override
                        public void onLoginFailed(int code, String error) {
                            Toast.makeText(getActivity(), String.format("登录失败[%s: %s]", code, error), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                Intent intent = new Intent(getContext(), AccountInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(View view) {
        this.mLayoutMyInfo = view.findViewById(R.id.layout_my_info);
        this.mTvUsername = view.findViewById(R.id.tv_username);

        this.mLayoutOta = view.findViewById(R.id.layout_ota);
        this.mLayoutMessage = view.findViewById(R.id.layout_message);
        this.mLayoutAbout = view.findViewById(R.id.layout_about);

    }

    private String getNickName() {
        if (LoginBusiness.isLogin()) {
            UserInfo userInfo = LoginBusiness.getUserInfo();
            String userName = "";
            if (userInfo != null) {
                if (!TextUtils.isEmpty(userInfo.userNick)) {
                    userName = userInfo.userNick;
                } else if (!TextUtils.isEmpty(userInfo.userPhone)) {
                    userName = userInfo.userPhone;
                } else if (!TextUtils.isEmpty(userInfo.userEmail)) {
                    userName = userInfo.userEmail;
                }else{
                    userName = "未获取到用户名";
                }
            }
            return userName;
        }
        return null;
    }
}
