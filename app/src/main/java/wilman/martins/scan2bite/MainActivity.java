package wilman.martins.scan2bite;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import wilman.martins.scan2bite.models.Producto;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_LISTA_PRODUCTOS = "key_lista_productos";

    private final ArrayList<Producto> listaProductos = new ArrayList<>();
    private final DecimalFormat formatoAzucar = new DecimalFormat("0.##");

    private View layoutCabecera;
    private TextView tvContadorProductos;
    private Button btnAnalizarProducto;
    private LinearLayout contenedorProductos;
    private TextView tvEstadoVacio;

    private ActivityResultLauncher<Intent> nuevoEscaneoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        restaurarEstado(savedInstanceState);
        inicializarVistas();
        configurarEdgeToEdge();
        inicializarLauncher();
        configurarEventos();
        actualizarLista();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_LISTA_PRODUCTOS, listaProductos);
    }

    private void restaurarEstado(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        Object guardado;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            guardado = savedInstanceState.getSerializable(KEY_LISTA_PRODUCTOS, ArrayList.class);
        } else {
            guardado = savedInstanceState.getSerializable(KEY_LISTA_PRODUCTOS);
        }
        if (guardado instanceof ArrayList<?>) {
            listaProductos.clear();
            for (Object item : (ArrayList<?>) guardado) {
                if (item instanceof Producto) {
                    listaProductos.add((Producto) item);
                }
            }
        }
    }

    private void inicializarVistas() {
        layoutCabecera = findViewById(R.id.layoutCabecera);
        tvContadorProductos = findViewById(R.id.tvContadorProductos);
        btnAnalizarProducto = findViewById(R.id.btnAnalizarProducto);
        contenedorProductos = findViewById(R.id.contenedorProductos);
        tvEstadoVacio = findViewById(R.id.tvEstadoVacio);
    }

    private void configurarEdgeToEdge() {
        final int paddingTopCabecera = layoutCabecera.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            layoutCabecera.setPadding(
                    layoutCabecera.getPaddingLeft(),
                    paddingTopCabecera + systemBars.top,
                    layoutCabecera.getPaddingRight(),
                    layoutCabecera.getPaddingBottom()
            );
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void inicializarLauncher() {
        nuevoEscaneoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Producto producto = extraerProducto(extras);
                            if (producto != null) {
                                listaProductos.add(producto);
                                actualizarLista();
                            }
                        }
                    }
                }
        );
    }

    private Producto extraerProducto(Bundle extras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return extras.getSerializable(NuevoEscaneoActivity.EXTRA_PRODUCTO, Producto.class);
        }
        Object serializable = extras.getSerializable(NuevoEscaneoActivity.EXTRA_PRODUCTO);
        if (serializable instanceof Producto) {
            return (Producto) serializable;
        }
        return null;
    }

    private void configurarEventos() {
        btnAnalizarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NuevoEscaneoActivity.class);
            nuevoEscaneoLauncher.launch(intent);
        });
    }

    private void actualizarLista() {
        tvContadorProductos.setText(getString(R.string.contador_productos, listaProductos.size()));
        if (listaProductos.isEmpty()) {
            tvEstadoVacio.setVisibility(View.VISIBLE);
        } else {
            tvEstadoVacio.setVisibility(View.GONE);
        }
        pintarProductos();
    }

    private void pintarProductos() {
        contenedorProductos.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Producto producto : listaProductos) {
            View tarjeta = inflater.inflate(R.layout.item_producto, contenedorProductos, false);

            ImageView imgCategoria = tarjeta.findViewById(R.id.imgCategoria);
            TextView tvNombreProducto = tarjeta.findViewById(R.id.tvNombreProducto);
            TextView tvDetalleProducto = tarjeta.findViewById(R.id.tvDetalleProducto);
            TextView tvAlternativa = tarjeta.findViewById(R.id.tvAlternativa);
            TextView tvPuntuacion = tarjeta.findViewById(R.id.tvPuntuacion);

            tvNombreProducto.setText(producto.getNombre());
            String azucaresFormateados = formatoAzucar.format(producto.getAzucares());
            tvDetalleProducto.setText(getString(
                    R.string.formato_categoria_azucar,
                    producto.getCategoria(),
                    azucaresFormateados
            ));
            tvAlternativa.setText(producto.getAlternativa());
            tvPuntuacion.setText(getString(R.string.formato_puntuacion, producto.getPuntuacion()));

            asignarIconoCategoria(imgCategoria, producto.getCategoria());
            asignarColorPuntuacion(tvPuntuacion, producto.getPuntuacion());

            contenedorProductos.addView(tarjeta);
        }
    }

    private void asignarIconoCategoria(ImageView imgCategoria, String categoria) {
        int indiceCategoria = obtenerIndiceCategoria(categoria);
        switch (indiceCategoria) {
            case 0:
                imgCategoria.setImageResource(R.drawable.ic_bebida);
                break;
            case 1:
                imgCategoria.setImageResource(R.drawable.ic_bolleria);
                break;
            case 2:
                imgCategoria.setImageResource(R.drawable.ic_lacteo);
                break;
            case 3:
                imgCategoria.setImageResource(R.drawable.ic_cereal);
                break;
            case 4:
            default:
                imgCategoria.setImageResource(R.drawable.ic_snack);
                break;
        }
    }

    private int obtenerIndiceCategoria(String categoria) {
        String[] categorias = getResources().getStringArray(R.array.categorias_alimentos);
        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(categoria)) {
                return i;
            }
        }
        return -1;
    }

    private void asignarColorPuntuacion(TextView tvPuntuacion, int puntuacion) {
        int colorRes;
        if (puntuacion >= 70) {
            colorRes = R.color.score_alto;
        } else if (puntuacion >= 40) {
            colorRes = R.color.score_medio;
        } else {
            colorRes = R.color.score_bajo;
        }
        int color = ContextCompat.getColor(this, colorRes);
        ViewCompat.setBackgroundTintList(tvPuntuacion, ColorStateList.valueOf(color));
    }
}
