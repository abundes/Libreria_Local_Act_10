package com.example.act_final_libreria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String CONTRASENA_ALMACENADA = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onLoginButtonClicked(View view) {
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        TextView resultTextView = findViewById(R.id.resultTextView);

        String inputPassword = passwordEditText.getText().toString();

        if (CONTRASENA_ALMACENADA.equals(inputPassword)) {
            resultTextView.setText("¡Inicio de sesión exitoso! Bienvenido.");

            // Redirigir a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Cierra LoginActivity para que no se pueda volver atrás desde MainActivity
        } else {
            resultTextView.setText("Contraseña incorrecta. Acceso denegado.");
        }
    }

}
