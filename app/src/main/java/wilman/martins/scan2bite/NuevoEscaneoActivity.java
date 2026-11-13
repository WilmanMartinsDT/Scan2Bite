package wilman.martins.scan2bite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import wilman.martins.scan2bite.models.Producto;

public class NuevoEscaneoActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCTO = "extra_producto";

    private View layoutCabeceraNuevoEscaneo;
    private EditText etNombreProducto;
    private Spinner spCategoria;
    private EditText etAzucares;
    private Button btnCancelar;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_nuevo_escaneo);

        inicializarVistas();
        configurarEdgeToEdge();
        configurarSpinner();
        configurarEventos();
    }

    private void inicializarVistas() {
        layoutCabeceraNuevoEscaneo = findViewById(R.id.layoutCabeceraNuevoEscaneo);
        etNombreProducto = findViewById(R.id.etNombreProducto);
        spCategoria = findViewById(R.id.spCategoria);
        etAzucares = findViewById(R.id.etAzucares);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void configurarEdgeToEdge() {
        final int paddingTopCabecera = layoutCabeceraNuevoEscaneo.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainNuevoEscaneo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            layoutCabeceraNuevoEscaneo.setPadding(
                    layoutCabeceraNuevoEscaneo.getPaddingLeft(),
                    paddingTopCabecera + systemBars.top,
                    layoutCabeceraNuevoEscaneo.getPaddingRight(),
                    layoutCabeceraNuevoEscaneo.getPaddingBottom()
            );
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categorias_alimentos,
                R.layout.item_spinner_categoria
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_categoria_dropdown);
        spCategoria.setAdapter(adapter);
    }

    private void configurarEventos() {
        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {
        String nombre = etNombreProducto.getText().toString().trim();
        String azucaresTexto = etAzucares.getText().toString().trim().replace(',', '.');

        if (TextUtils.isEmpty(nombre)) {
            etNombreProducto.setError(getString(R.string.error_nombre_vacio));
            etNombreProducto.requestFocus();
            Toast.makeText(this, R.string.aviso_campos_incompletos, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(azucaresTexto)) {
            etAzucares.setError(getString(R.string.error_azucar_vacio));
            etAzucares.requestFocus();
            Toast.makeText(this, R.string.aviso_campos_incompletos, Toast.LENGTH_SHORT).show();
            return;
        }

        double azucares;
        try {
            azucares = Double.parseDouble(azucaresTexto);
        } catch (NumberFormatException e) {
            etAzucares.setError(getString(R.string.error_azucar_invalido));
            etAzucares.requestFocus();
            return;
        }

        if (azucares < 0) {
            etAzucares.setError(getString(R.string.error_azucar_invalido));
            etAzucares.requestFocus();
            return;
        }

        int indiceCategoria = spCategoria.getSelectedItemPosition();
        String categoria = spCategoria.getSelectedItem().toString();
        int puntuacion = calcularPuntuacion(indiceCategoria, azucares);
        String alternativa = obtenerAlternativa(indiceCategoria);

        Producto producto = new Producto(nombre, categoria, azucares, puntuacion, alternativa);

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_PRODUCTO, producto);

        Intent intentResultado = new Intent();
        intentResultado.putExtras(bundle);
        setResult(RESULT_OK, intentResultado);
        finish();
    }

    private int calcularPuntuacion(int indiceCategoria, double azucares) {
        double factorPenalizacion;
        switch (indiceCategoria) {
            case 0: // Bebidas
                factorPenalizacion = 4.5;
                break;
            case 1: // Bollería
                factorPenalizacion = 3.5;
                break;
            case 2: // Lácteos
                factorPenalizacion = 2.0;
                break;
            case 3: // Cereales
                factorPenalizacion = 2.5;
                break;
            case 4: // Snacks
            default:
                factorPenalizacion = 3.0;
                break;
        }

        int notaCalculada = (int) Math.round(100.0 - (azucares * factorPenalizacion));
        return Math.max(0, Math.min(100, notaCalculada));
    }

    private String obtenerAlternativa(int indiceCategoria) {
        switch (indiceCategoria) {
            case 0:
                return getString(R.string.alt_bebidas);
            case 1:
                return getString(R.string.alt_bolleria);
            case 2:
                return getString(R.string.alt_lacteos);
            case 3:
                return getString(R.string.alt_cereales);
            case 4:
            default:
                return getString(R.string.alt_snacks);
        }
    }
}
