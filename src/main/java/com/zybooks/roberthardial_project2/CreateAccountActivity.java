package com.zybooks.roberthardial_project2;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText editNewUsername;
    private EditText editNewPassword;
    private Button btnCreateAccount;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        editNewUsername = findViewById(R.id.editUsername);
        editNewPassword = findViewById(R.id.editPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        databaseHelper = new DatabaseHelper(this);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editNewUsername.getText().toString();
                String newPassword = editNewPassword.getText().toString();

                // Insert the new user account into the database
                long newRowId = databaseHelper.addUser(newUsername, newPassword);

                if (newRowId != -1) {
                    // Account created successfully
                    Toast.makeText(CreateAccountActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                    // Navigate back to the login screen
                    Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity to remove it from the back stack
                } else {
                    // Error creating account
                    Toast.makeText(CreateAccountActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

