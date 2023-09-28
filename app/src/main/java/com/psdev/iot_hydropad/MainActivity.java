package com.psdev.iot_hydropad;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import android.widget.TextView;
import com.psdev.iot_hydropad.NotificationTank;



public class MainActivity extends AppCompatActivity {

    private NotificationTank notificationTank;
    private DatabaseReference mDatabaseReference1, mDatabaseReference2, mDatabaseReference3, mDatabaseReference4,
            mDatabaseReference5, mDatabaseReferenceEstado, mDatabaseReferenceTimestamp;;
    private TubeSpeedometer speedTanque, speedTurbuidez, speedTemperatura, speedpH;
    private TextView distanciaTextView;
    private TextView statusIoT; // Añade esta línea
    private boolean estado = false;


    //llamar sonido
    private MediaPlayer mediaPlayer;
    private ChildEventListener estadoEventListener;



    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            statusIoT.setBackgroundResource(R.drawable.rounded_status_background);
            mDatabaseReferenceTimestamp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long timestamp = dataSnapshot.getValue(Long.class);
                    statusIoT = findViewById(R.id.statusIoT);
                    if (timestamp != null) {
                        long currentTimestamp = System.currentTimeMillis();
                        if (currentTimestamp - timestamp > 5000) {
                            mDatabaseReferenceEstado.setValue(false);
                            speedTanque.setSpeedAt(0);
                            speedTurbuidez.setSpeedAt(0);
                            speedTemperatura.setSpeedAt(0);
                            speedpH.setSpeedAt(0);
                            distanciaTextView.setText("Tank distance: 0 cm");


                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("MainActivity", "Failed to read value.", databaseError.toException());
                }
            });
            handler.postDelayed(this, 15000); // Revisar cada 5 segundos
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusIoT = findViewById(R.id.statusIoT);
        notificationTank = new NotificationTank(this);




        // Configuración de los velocímetros
        // ...

        String userId = "kevin"; // Reemplaza con el ID de tu usuario.

        // Referencias a diferentes rutas en Firebase
        mDatabaseReference1 = FirebaseDatabase.getInstance().getReference("/IoTUsers/" + userId + "/waterLevelPer");
        mDatabaseReference2 = FirebaseDatabase.getInstance().getReference("/IoTUsers/" + userId + "/Turbidez");
        mDatabaseReference3 = FirebaseDatabase.getInstance().getReference("/IoTUsers/" + userId + "/Temperatura");
        mDatabaseReference4 = FirebaseDatabase.getInstance().getReference("/IoTUsers/" + userId + "/pH");
        mDatabaseReference5 = FirebaseDatabase.getInstance().getReference("/IoTUsers/" + userId + "/distance");
        mDatabaseReferenceEstado = FirebaseDatabase.getInstance().getReference("/IoTUsers/" + userId + "/IoTStatus"+ "/Status");
        mDatabaseReferenceTimestamp = FirebaseDatabase.getInstance().getReference("/IoTUsers/" + userId + "/IoTStatus" + "/Timestamp");
        // Lectura en tiempo real para speedView1

        mDatabaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if (value != null && estado) { // Solo actualiza el valor del velocímetro si el estado es 'On'
                    speedTanque = findViewById(R.id.speedTanque);
                    speedTanque.setMaxSpeed(100);
                    speedTanque.setSpeedAt(value);
                    speedTanque.setUnit("%");
                    notificationTank.sendNotification();

                    if (value < 10) { // Si el valor es menor que el 10%
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.alert_water); // Reemplaza 'tu_sonido' con el nombre de tu archivo de sonido.
                        }
                        mediaPlayer.start(); // Reproduce el sonido.
                    } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop(); // Detén el sonido si el valor es 10% o más.
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                } else {
                    // Puedes poner el valor del velocímetro a 0 si el estado es 'Off'
                    speedTanque = findViewById(R.id.speedTanque);
                    speedTanque.setSpeedAt(0);

                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) mediaPlayer.stop(); // Detén el sonido si el estado es 'Off'.
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });

        // Lectura en tiempo real para speedView2
        mDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                speedTurbuidez = findViewById(R.id.speedTurbuidez);
                if (value != null && estado) {
                    speedTurbuidez.setMaxSpeed(50);
                    speedTurbuidez.speedTo(value, 0);
                } else {
                    speedTurbuidez.speedTo(0, 0);
                }
                speedTurbuidez.setUnit(" NTU");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });



        // Lectura en tiempo real para speedView3
        mDatabaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                speedTemperatura = findViewById(R.id.speedTemperatura);
                if (value != null && estado) {
                    speedTemperatura.setMinSpeed(1);
                    speedTemperatura.setMaxSpeed(150);
                    speedTemperatura.speedTo(value, 0);
                } else {
                    speedTemperatura.speedTo(0, 0);
                }
                speedTemperatura.setUnit(" °C");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });

        // Lectura en tiempo real para speedView4
        mDatabaseReference4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                speedpH = findViewById(R.id.speedpH);
                if (value != null && estado) {
                    speedpH.setMinSpeed(1);
                    speedpH.setMaxSpeed(14);
                    speedpH.speedTo(value, 0);
                } else {
                    speedpH.speedTo(0, 0);
                }
                speedpH.setUnit(" pH");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });

        //LEER DATOS DE DISTANCIA
        distanciaTextView = findViewById(R.id.distanciaTextView);
        mDatabaseReference5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (estado) { // Si el estado es true (ON)
                    if (dataSnapshot.exists()) {
                        Double distancia = dataSnapshot.getValue(Double.class);
                        if (distancia != null)
                            distanciaTextView.setText(String.format("Tank distance: %.2f cm", distancia));
                    }
                } else { // Si el estado es false (OFF)
                    distanciaTextView.setText("Tank distance: 0 cm"); // o puedes poner "Datos no disponibles" o algo similar.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Error al leer la distancia", databaseError.toException());
            }
        });

        //LEER DATO DE STATUS Y ACTUALIZAR

        statusIoT.setBackgroundResource(R.drawable.rounded_status_background);
        // ...

        mDatabaseReferenceEstado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean estadoFirebase = dataSnapshot.getValue(Boolean.class);
                if (estadoFirebase != null) {
                    estado = estadoFirebase; // actualizamos la variable estado con el valor leído de Firebase.
                    statusIoT.setSelected(estado); // true para "On" (verde), false para "Off" (rojo)
                    statusIoT.setText(estado ? "On" : "Off");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });





        // para función de los botones
        Button button1 = findViewById(R.id.btn_start);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button1.getText().equals("Manual")) {
                    button1.setText("Auto");
                } else {
                    button1.setText("Manual");
                }
            }
        });

        Button button2 = findViewById(R.id.btn_stop);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button2.getText().equals("Off")) {
                    button2.setText("On");
                } else {
                    button2.setText("Off");
                }
            }
        });

        Button button3 = findViewById(R.id.btn_reset);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button3.getText().equals("Off")) {
                    button3.setText("On");
                } else {
                    button3.setText("Off");
                }
            }
        });
        handler.post(runnable);

    }


    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable); // Reanudar cuando la app esté en primer plano
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Detener cuando la app no está en primer plano
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Detener cuando la actividad se destruye
    }


}





