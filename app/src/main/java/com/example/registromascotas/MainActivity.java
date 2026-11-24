package com.example.registromascotas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Declaro variables
    private EditText txtCodigo, txtNombre, txtDueño, txtDireccion;
    private ListView lista;
    private Spinner spMascota;

    // Variable de la conexión de Firestore
    private FirebaseFirestore db;

    // Datos del spinner
    String[] TiposMascotas = {"Perro", "Gato", "Pájaro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializo Firestore
        db = FirebaseFirestore.getInstance();

        // Uno las variables con los Id del XML
        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtDueño = findViewById(R.id.txtDueño);
        txtDireccion = findViewById(R.id.txtDireccion);
        spMascota = findViewById(R.id.spMascota);
        lista = findViewById(R.id.lista);

        // Poblar Spinner de los tipos de Mascotas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TiposMascotas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMascota.setAdapter(adapter);

        // Llamamos al método que cargará la lista
        CargarListaFirestore();
    }

    // Método Enviar Datos
    public void enviarDatosFirestore(View view) {
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String dueño = txtDueño.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String tipoMascota = spMascota.getSelectedItem().toString();

        Map<String, Object> mascota = new HashMap<>();
        mascota.put("codigo", codigo);
        mascota.put("nombre", nombre);
        mascota.put("dueño", dueño);
        mascota.put("direccion", direccion);
        mascota.put("tipoMascota", tipoMascota);

        db.collection("mascotas")
                .document(codigo)
                .set(mascota)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Datos enviados a Firestore correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al enviar datos a Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Botón que Carga la Lista
    public void CargarLista(View view) {
        CargarListaFirestore();
    }

    // Método Cargar Lista
    public void CargarListaFirestore() {
        db.collection("mascotas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listaMascotas = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String linea = "||" + document.getString("codigo") + "||" +
                                        document.getString("nombre") + "||" +
                                        document.getString("dueño") + "||" +
                                        document.getString("direccion");
                                listaMascotas.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, listaMascotas);
                            lista.setAdapter(adaptador);
                        } else {
                            Log.e("TAG", "Error al obtener datos de Firestore", task.getException());
                        }
                    }
                });
    }
}