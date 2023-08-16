package com.zybooks.roberthardial_project2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;


public class LoginActivity extends AppCompatActivity {
    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin;
    private Button btnCreateAccount;
    private DatabaseHelper databaseHelper;
    private static final int REQUEST_SMS_PERMISSION = 123; // You can change the value to any unique integer
    private static final String SMS_PERMISSION = Manifest.permission.SEND_SMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_display);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                // Perform login validation
                User user = databaseHelper.getUserByUsername(username);
                Integer userId = user.getId();
                if (user != null && user.getPassword().equals(password)) {
                    // Successful login, proceed to the next screen or action

                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", user.getId()); // Assuming User class has getId() method
                    editor.apply();

                    showPermissionDialog(userId);
                } else {
                    // Invalid login, show error message
                    Toast.makeText(LoginActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open create account screen or activity here
                // You can start a new activity or navigate to a fragment
                // For example:
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showPermissionDialog(int userId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.permissin_dialog, null);
        dialogBuilder.setView(dialogView);

        Button buttonAcceptPermission = dialogView.findViewById(R.id.buttonAcceptPermission);
        Button buttonDenyPermission = dialogView.findViewById(R.id.buttonDenyPermission);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonAcceptPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the permission response as granted for the current user
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("sms_permission_granted_" + userId, true);
                editor.apply();

                proceedToDataDisplay();
                alertDialog.dismiss();
            }
        });

        buttonDenyPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the permission response as denied for the current user
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("sms_permission_granted_" + userId, false);
                editor.apply();

                proceedToDataDisplay();
                alertDialog.dismiss();
            }
        });
    }



    private void proceedToDataDisplay() {
        Intent intent = new Intent(LoginActivity.this, DataDisplayActivity.class);
        startActivity(intent);
        finish();
    }
}
