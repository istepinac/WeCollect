package com.wedesign.wecollectfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText LoginEmail, LoginPassword;
    Button LoginButton;
    TextView RegisterRedirectText, ForgotPassword;
    CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        LoginEmail = findViewById(R.id.Login_Email);
        LoginPassword = findViewById(R.id.Login_Password);
        LoginButton = findViewById(R.id.Login_button);
        RegisterRedirectText = findViewById(R.id.LoginRedirectText);
        ForgotPassword = findViewById(R.id.forgot_password);
        checkBox = findViewById(R.id.checkBox);

        LoginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX() >= LoginPassword.getRight() - LoginPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()){
                        if(LoginPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                            LoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            LoginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_remove_red_eye_24, 0);
                        } else {
                            LoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            LoginPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        });



        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String check = preferences.getString("remember", "");

        if(check.equals("true")){

            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);

        }

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = LoginEmail.getText().toString().trim();
                String pass = LoginPassword.getText().toString().trim();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please verify your email address to login", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        LoginPassword.setError("Enter your password");
                    }
                } else if (email.isEmpty()) {
                    LoginEmail.setError("Enter your email");
                } else {
                    LoginEmail.setError("Email must be valid");
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked()){

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();

                }else{

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();

                }
            }
        });

        RegisterRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
                }
                dialog.show();
            }
        });
    }
}
