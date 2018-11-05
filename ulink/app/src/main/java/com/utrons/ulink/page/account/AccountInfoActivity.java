package com.utrons.ulink.page.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.openaccount.model.User;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.data.UserInfo;
import com.utrons.ulink.R;
import com.utrons.ulink.StartActivity;
import com.utrons.ulink.page.BaseActivity;
import com.utrons.ulink.page.main.MainActivity;

public class AccountInfoActivity extends BaseActivity {

    private TextView mTvNickname;
    private TextView mTvAccount;
    private TextView mTvEamil;
    private TextView mTvAccountLocation;

    private View mLayoutChangePassword;
    private View mLayoutDeleteAccount;
    private View mLayoutLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        initView();
        initData();

        this.mLayoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(AccountInfoActivity.this).setTitle("确定要退出当前账号吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    private void initView() {
        this.mTvNickname = findViewById(R.id.tv_nickname);
        this.mTvAccount = findViewById(R.id.tv_account);
        this.mTvEamil = findViewById(R.id.tv_email);
        this.mTvAccountLocation = findViewById(R.id.tv_account_location);

        this.mLayoutChangePassword = findViewById(R.id.layout_change_password);
        this.mLayoutDeleteAccount = findViewById(R.id.layout_delete_account);
        this.mLayoutLogout = findViewById(R.id.layout_logout);
    }

    private void initData() {
        if (LoginBusiness.isLogin()) {
            setUserInfo(LoginBusiness.getUserInfo());
        } else {
            LoginBusiness.login(new ILoginCallback() {
                @Override
                public void onLoginSuccess() {
                    setUserInfo(LoginBusiness.getUserInfo());
                }

                @Override
                public void onLoginFailed(int code, String error) {
                    setUserInfo(null);
                    Toast.makeText(getApplicationContext(), String.format("登录失败[%s: %s]", code, error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setUserInfo(UserInfo user) {
        if (null == user) {
            mTvNickname.setText(null);
            mTvAccount.setText(null);
            mTvEamil.setText(null);
            mTvAccountLocation.setText(null);
            return;
        }
        mTvNickname.setText(user.userNick);
        mTvAccount.setText(user.userPhone);
        mTvEamil.setText(user.userEmail);
        mTvAccountLocation.setText(user.mobileLocationCode);
    }

    private void logout() {
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                Toast.makeText(getApplicationContext(), String.format("登出成功"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLogoutFailed(int code, String error) {
                Toast.makeText(getApplicationContext(), String.format("登出失败[%s: %s]", code, error), Toast.LENGTH_LONG).show();
            }
        });
    }
}
