package com.example.roles_usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RolAdapter.OnItemClickListener, RolAdapter.OnItemLongClickListener {

    EditText edtNombreRol, edtDescripcionRol;
    Button btnGuardarRol;
    RecyclerView rvRoles;
    RolAdapter adapter;
    ArrayList<Rol> listaRoles;
    int idRolEnEdicion = -1;
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
        rvRoles = findViewById(R.id.lvRoles);

        db = new DBHelper(this);

        // Configuración del RecyclerView
        rvRoles.setLayoutManager(new LinearLayoutManager(this));
        listaRoles = new ArrayList<>();
        adapter = new RolAdapter(this, listaRoles, this, this);
        rvRoles.setAdapter(adapter);

        cargarRoles();

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

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Confirmar Actualización")
                            .setMessage("¿Estás seguro de querer actualizar el rol a '" + nombre + "'?")
                            .setPositiveButton("Sí", (dialog, which) -> {

                                Rol rol = new Rol(idRolEnEdicion, nombre, descripcion);
                                boolean exito = db.actualizarRol(rol);

                                if (exito) {
                                    Toast.makeText(MainActivity.this, "Rol actualizado correctamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error al actualizar el rol (¿Nombre duplicado?)", Toast.LENGTH_SHORT).show();
                                }

                                limpiarFormulario();
                                cargarRoles();
                            })
                            .setNegativeButton("No", null)
                            .show();

                } else {
                    // Lógica de Inserción
                    boolean exito = db.insertarRol(nombre, descripcion);
                    if (exito) {
                        Toast.makeText(MainActivity.this, "Rol insertado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al insertar el rol (¿Nombre duplicado?)", Toast.LENGTH_SHORT).show();
                    }

                    limpiarFormulario();
                    cargarRoles();
                }
            }
        });
    }

    // metodo para Editar
    @Override
    public void onItemClick(Rol rolSeleccionado) {

        edtNombreRol.setText(rolSeleccionado.getNombre());
        edtDescripcionRol.setText(rolSeleccionado.getDescripcion());

        idRolEnEdicion = rolSeleccionado.getId();
        btnGuardarRol.setText("Actualizar Rol");
        Toast.makeText(MainActivity.this, "Editando Rol ID: " + idRolEnEdicion, Toast.LENGTH_SHORT).show();
    }

    // metodo para Eliminar
    @Override
    public void onItemLongClick(Rol rolSeleccionado) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de querer eliminar el rol '" + rolSeleccionado.getNombre() + "'?")
                .setPositiveButton("Sí", (dialog, which) -> {

                    boolean exito = db.eliminarRol(rolSeleccionado.getId());

                    if (exito) {
                        Toast.makeText(MainActivity.this, "Rol '" + rolSeleccionado.getNombre() + "' eliminado correctamente", Toast.LENGTH_SHORT).show();
                        cargarRoles();

                        if (idRolEnEdicion == rolSeleccionado.getId()) {
                            limpiarFormulario();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error al eliminar el rol", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)

                .show();
    }

    private void limpiarFormulario() {
        idRolEnEdicion = -1;
        edtNombreRol.setText("");
        edtDescripcionRol.setText("");
        btnGuardarRol.setText("Guardar Rol");
    }

    private void cargarRoles() {
        ArrayList<Rol> rolesObtenidos = db.obtenerRoles();

        if (rolesObtenidos != null) {
            adapter.setRoles(rolesObtenidos);
            this.listaRoles = rolesObtenidos;

        } else {
            Toast.makeText(this, "No se pudieron cargar los roles de la base de datos.", Toast.LENGTH_LONG).show();
        }
    }
}