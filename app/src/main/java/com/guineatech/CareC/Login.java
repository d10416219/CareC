package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    TextView text_mail,text_pwd,text_forgot;
    EditText ed_mail,ed_pwd;
    Button  bt_ok;
    String email,pwd;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final GridLayout LO = (GridLayout) findViewById( R.id.layout1 );
        LO.setColumnCount(3);
        LO.setRowCount(2);
//
       // text_mail=(TextView)findViewById(R.id.text_Mail);
       // text_pwd=(TextView)findViewById(R.id.text_pwd);
        text_forgot=(TextView)findViewById(R.id.text_Forgot);
        bt_ok=(Button)findViewById(R.id.bt_Ok);
        ed_mail=(EditText)findViewById(R.id.et_Mail);
        ed_pwd=(EditText)findViewById(R.id.et_pwd);

        Intent it=this.getIntent();
        email=it.getStringExtra("email");
        pwd=it.getStringExtra("pwd");
        ed_mail.setText(email);
        ed_pwd.setText(pwd);
        AppHelper.checkpool(this);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppHelper.userid==null)
                {
                    AppHelper.userid= ed_mail.getText().toString().replace("@","-at-");
                }
                AppHelper.getPool().getUser(AppHelper.userid).getSessionInBackground(authenticationHandler);
                showWaitDialog("Login......");

            }
        });
    }
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            closeWaitDialog();
            String idToken= cognitoUserSession.getIdToken().getJWTToken();
            Map<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_Aj3frUrZo",idToken);
            AppHelper.credentialsProvider.setLogins(logins);
            showDialogMessage("Login","Success",true);


        }
//
        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            //Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_LONG).show();
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            closeWaitDialog();
           Toast.makeText(getApplicationContext(),"3",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            showDialogMessage("Sign in","Fail "+e,false);
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user

            } else if ("SELECT_MFA_TYPE".equals(continuation.getChallengeName())) {

            }
        }
    };

    private void getUserAuthentication(AuthenticationContinuation continuation, String username)
    {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, ed_pwd.getText().toString(), null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }


    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        exhere();
                    }
                } catch (Exception e) {
                    if(exit) {
                        exhere();
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exhere()
    {
        Intent it =new Intent();
        it.setClass(Login.this,Mainpage.class);
        startActivity(it);
        finish();
    }
}