package com.psdev.iot_hydropad;

import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

// Ejemplo cambiando el color del texto del botón al ser presionado
public class ButtonManager {
    public static void manageButton(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = button.getText().toString();
                switch (buttonText) {
                    case "Manual":
                        button.setText("Auto");
                        button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.someColor));
                        break;
                    case "Auto":
                        button.setText("Manual");
                        button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.someOtherColor));
                        break;
                    case "Off":
                        button.setText("On");
                        button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.anotherColor));
                        break;
                    case "On":
                        button.setText("Off");
                        button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.defaultColor));
                        break;
                }
            }
        });
    }
}

