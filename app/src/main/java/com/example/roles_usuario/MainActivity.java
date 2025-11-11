package com.example.roles_usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText edtNombreRol, edtDescripcionRol;
    Button btnGuardarRol;
    ListView lvRoles;
    ArrayAdapter<Rol> adapter;
    ArrayList<Rol> listaRoles;
    int idRolEnEdicion = -1;
    //variable para almacenar el ID
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtNombreRol = findViewById(R.id.edtNombreRol);
        edtDescripcionRol = findViewById(R.id.edtDescripcionRol);
        btnGuardarRol = findViewById(R.id.btnGuardarRol);
        lvRoles = findViewById(R.id.lvRoles);

        db = new DBHelper(this);

        cargarRoles();
        //listener para Editar
        lvRoles.setOnItemClickListener((parent, view, position, id) -> {
            Rol rolSeleccionado = listaRoles.get(position);

            edtNombreRol.setText(rolSeleccionado.getNombre());
            edtDescripcionRol.setText(rolSeleccionado.getDescripcion());

            idRolEnEdicion = rolSeleccionado.getId();
            btnGuardarRol.setText("Actualizar Rol");
            Toast.makeText(MainActivity.this, "Editando Rol ID: " + idRolEnEdicion, Toast.LENGTH_SHORT).show();
        });

        //listener para Eliminarion
        lvRoles.setOnItemLongClickListener((parent, view, position, id) -> {
            Rol rolSeleccionado = listaRoles.get(position);



            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Estás seguro de querer eliminar el rol '" + rolSeleccionado.getNombre() + "'?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Lógica de eliminación si el usuario presiona "Sí"
                        boolean exito = db.eliminarRol(rolSeleccionado.getId());

                        if (exito) {
                            Toast.makeText(MainActivity.this, "Rol '" + rolSeleccionado.getNombre() + "' eliminado correctamente", Toast.LENGTH_SHORT).show();
                            cargarRoles(); // Recarga la lista
                        } else {
                            Toast.makeText(MainActivity.this, "Error al eliminar el rol", Toast.LENGTH_SHORT).show();
                        }

                        if (idRolEnEdicion == rolSeleccionado.getId()) {
                            idRolEnEdicion = -1;
                            edtNombreRol.setText("");
                            edtDescripcionRol.setText("");
                            btnGuardarRol.setText("Guardar Rol");
                        }
                    })
                    .setNegativeButton("No", null) //cierra el diálogo si el usuario presiona "No"
                    .show();

            cargarRoles();
            return true;
        });


        btnGuardarRol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = edtNombreRol.getText().toString().trim();
                String descripcion = edtDescripcionRol.getText().toString().trim();

                if (nombre.isEmpty()) {
                    Toast.makeText(MainActivity.this, "El nombre del rol no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (idRolEnEdicion != -1) {
                    // Lógica de Actualización: Preguntar antes de continuar
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Confirmar Actualización")
                            .setMessage("¿Estás seguro de querer actualizar el rol a '" + nombre + "'?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                // Lógica de actualización si el usuario presiona "Sí"
                                Rol rol = new Rol(idRolEnEdicion, nombre, descripcion);
                                boolean exito = db.actualizarRol(rol);

                                if (exito) {
                                    Toast.makeText(MainActivity.this, "Rol actualizado correctamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error al actualizar el rol", Toast.LENGTH_SHORT).show();
                                }

                                idRolEnEdicion = -1; // Reinicia el ID de edición
                                edtNombreRol.setText("");
                                edtDescripcionRol.setText("");
                                btnGuardarRol.setText("Guardar Rol"); // Vuelve a la inserción
                                cargarRoles();
                            })
                            .setNegativeButton("No", null) // Cierra el diálogo sin hacer nada
                            .show();

                } else {
                    boolean exito = db.insertarRol(nombre, descripcion);
                    if (exito) {
                        Toast.makeText(MainActivity.this, "Rol insertado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al insertar el rol (¿Nombre duplicado?)", Toast.LENGTH_SHORT).show();
                    }

                    edtNombreRol.setText("");
                    edtDescripcionRol.setText("");
                    cargarRoles();
                }
            }
        });
    }

    private void cargarRoles() {
        listaRoles = db.obtenerRoles();

        if (listaRoles != null) {
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    listaRoles);
            lvRoles.setAdapter(adapter);
        } else {
            listaRoles = new ArrayList<>();
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    listaRoles);
            lvRoles.setAdapter(adapter);
            Toast.makeText(this, "No se pudieron cargar los roles de la base de datos.", Toast.LENGTH_LONG).show();
        }
    }
}