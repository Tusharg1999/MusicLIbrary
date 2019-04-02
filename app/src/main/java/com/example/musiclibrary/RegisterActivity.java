package com.example.musiclibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.jaeger.library.StatusBarUtil;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private RelativeLayout mLayout;
    private Animation animationUtils;
    private EditText phonenumber, otp;
    private TextView termscondition;
    private Button verification, summit;
    private RadioButton terms;
    private String number;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private ProgressDialog mprogressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        animationUtils = AnimationUtils.loadAnimation(this, R.anim.layout_animation);
        initialization();
        StatusBarUtil.setTransparent(this);
        mLayout.setAnimation(animationUtils);
        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String y = phonenumber.getText().toString();
                if (TextUtils.isEmpty(y)) {
                    Toast.makeText(RegisterActivity.this, "Enter phone Number...", Toast.LENGTH_SHORT).show();

                } else {
                    if(terms.isChecked()) {
                        otp.setVisibility(View.VISIBLE);
                        verification.setVisibility(View.INVISIBLE);
                        phonenumber.setVisibility(View.INVISIBLE);
                        summit.setVisibility(View.VISIBLE);
                        mprogressbar.setMessage("Wait while we are creating account...");
                        mprogressbar.show();
                        terms.setVisibility(View.INVISIBLE);
                        termscondition.setVisibility(View.INVISIBLE);
                        String x = "+91";
                        number = x + y;
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                number,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                RegisterActivity.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Agree Terms and Conditions to proceed...", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential)
            {
                signInWithPhoneAuthCredential(credential);
            }


            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                otp.setVisibility(View.INVISIBLE);
                verification.setVisibility(View.VISIBLE);
                summit.setVisibility(View.INVISIBLE);
                phonenumber.setVisibility(View.VISIBLE);
                mprogressbar.dismiss();
                terms.setVisibility(View.VISIBLE);
                termscondition.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mprogressbar.dismiss();
                mVerificationId = verificationId;
                otp.setVisibility(View.VISIBLE);
                mprogressbar.setMessage("Automatic receiving OTP...");
                mprogressbar.show();
                verification.setVisibility(View.INVISIBLE);
                phonenumber.setVisibility(View.INVISIBLE);
                summit.setVisibility(View.VISIBLE);
                terms.setVisibility(View.INVISIBLE);
                termscondition.setVisibility(View.INVISIBLE);

                // ...
            }
        };
        summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=otp.getText().toString();
                if (TextUtils.isEmpty(code))
                {
                    Toast.makeText(RegisterActivity.this, "Enter OTP to proceed..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                        signInWithPhoneAuthCredential(credential);

                }
            }
        });

    }


        @Override
        protected void onResume () {
            super.onResume();
            mLayout.setAnimation(animationUtils);
        }

        private void initialization ()
    {
            mLayout = findViewById(R.id.textlayout);
            phonenumber = findViewById(R.id.phone_number);
            otp = findViewById(R.id.otp);
            verification = findViewById(R.id.send_verification);
            summit = findViewById(R.id.summit);
            terms = findViewById(R.id.radio_button);
            mprogressbar=new ProgressDialog(RegisterActivity.this);
            mprogressbar.setTitle("Loading");
            termscondition=findViewById(R.id.terms_condition);
            mAuth=FirebaseAuth.getInstance();
            mprogressbar.setCanceledOnTouchOutside(false);
        }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {goToMainActivity();
                        mprogressbar.dismiss();
                        } else
                            {
                                mprogressbar.dismiss();
                              String Error =task.getException().toString();
                                Toast.makeText(RegisterActivity.this, Error, Toast.LENGTH_SHORT).show();
                            }

                    }
                });

    }

    private void goToMainActivity() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
