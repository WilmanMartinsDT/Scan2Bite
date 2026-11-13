================================================================================
SCAN2BITE - ANALIZADOR NUTRICIONAL DE PRODUCTOS ALIMENTICIOS
================================================================================

1. DATOS DEL PROYECTO
--------------------------------------------------------------------------------
- Nombre de la aplicación: Scan2Bite
- Autor: Wilman Martins
- Tecnología: Android nativo (Java y XML)
- Gestión de datos: En memoria durante la ejecución (ArrayList<Producto>)


2. DESCRIPCIÓN Y GUÍA DE USO PASO A PASO
--------------------------------------------------------------------------------
Scan2Bite es un prototipo funcional en memoria de un analizador nutricional de
productos alimenticios. Permite al usuario simular el escaneo/análisis de
productos introduciendo su nombre, su categoría alimentaria y los gramos de
azúcar por cada 100 g, calculando automáticamente una puntuación nutricional
(de 0 a 100) y recomendando una alternativa saludable adaptada a la categoría.

Cómo se utiliza paso a paso:
  Paso 1: Al abrir la aplicación se muestra la pantalla principal (Historial de
          escaneos). Inicialmente el contador indica "Productos analizados: 0"
          y aparece el mensaje de estado vacío.
  Paso 2: Pulsa el botón "Analizar nuevo producto" para abrir la pantalla del
          simulador de análisis.
  Paso 3: Escribe el nombre del producto, selecciona una de las 5 categorías en
          el desplegable (Bebidas, Bollería, Lácteos, Cereales o Snacks) e
          introduce los gramos de azúcar por cada 100 g.
  Paso 4: Pulsa "Guardar en historial" para validar los datos, calcular la
          puntuación y volver a la pantalla principal con el producto añadido,
          o pulsa "Cancelar" para volver sin guardar cambios.
  Paso 5: En el historial verás cada producto en una tarjeta con su icono de
          categoría, sus gramos de azúcar, su alternativa saludable recomendada
          y su insignia de puntuación coloreada (verde, naranja o rojo).


3. CUMPLIMIENTO DE LOS 7 REQUISITOS TÉCNICOS (ARCHIVOS Y CLASES)
--------------------------------------------------------------------------------
1) Restricciones técnicas obligatorias (Java, XML, sin BD/permisos, strings.xml):
   - Todo el código fuente está desarrollado exclusivamente en Java
     (app/src/main/java/wilman/martins/scan2bite/) y las vistas en XML
     (app/src/main/res/layout/).
   - Las vistas se enlazan mediante findViewById de forma modular con métodos
     auxiliares: inicializarVistas(), inicializarLauncher(), configurarEventos(),
     actualizarLista() y pintarProductos() en MainActivity.java; e
     inicializarVistas(), configurarSpinner(), configurarEventos() y
     guardarProducto() en NuevoEscaneoActivity.java.
   - Los datos residen en memoria en el ArrayList<Producto> listaProductos de
     MainActivity.java.
   - app/src/main/AndroidManifest.xml no declara permisos de cámara ni internet.
   - app/src/main/res/values/strings.xml centraliza el 100% de los textos de la
     interfaz, títulos, hints, botones, mensajes de error, alternativas y el
     <string-array name="categorias_alimentos"> del Spinner (cero textos
     hardcodeados en XML ni en Java).

2) Recursos gráficos (app/src/main/res/drawable/):
   - Logo de la app: ic_scan2bite_logo.xml
   - Iconos de categorías: ic_bebida.xml, ic_bolleria.xml, ic_lacteo.xml,
     ic_cereal.xml e ic_snack.xml
   - Fondos redondeados: bg_tarjeta_producto.xml (tarjetas de producto) y
     bg_badge_score.xml (insignia de puntuación).
   - Paleta verde esmeralda y colores de puntuación definidos en
     app/src/main/res/values/colors.xml.

3) Modelo de datos (models/Producto.java):
   - Archivo: app/src/main/java/wilman/martins/scan2bite/models/Producto.java
   - Clase: wilman.martins.scan2bite.models.Producto (implementa Serializable).
   - Atributos: nombre (String), categoria (String), azucares (double),
     puntuacion (int, 0..100) y alternativa (String).
   - Métodos: Constructor completo y getters/setters para todos los atributos.

4) Pantalla 1 - Historial de escaneos (MainActivity.java y activity_main.xml):
   - Diseño: app/src/main/res/layout/activity_main.xml
     * Cabecera en verde esmeralda (#2E7D32) con ic_scan2bite_logo, título y
       TextView contador (tvContadorProductos).
     * Botón destacado btnAnalizarProducto.
     * ScrollView (scrollProductos) con LinearLayout vertical interior
       (contenedorProductos) y TextView de estado vacío (tvEstadoVacio).
   - Lógica: app/src/main/java/wilman/martins/scan2bite/MainActivity.java
     * Mantiene ArrayList<Producto> listaProductos.
     * Usa ActivityResultLauncher<Intent> (nuevoEscaneoLauncher) para lanzar
       NuevoEscaneoActivity y procesar RESULT_OK extrayendo el Producto del
       Bundle (ignorando RESULT_CANCELED).
     * pintarProductos() invoca contenedorProductos.removeAllViews(), recorre
       listaProductos, infla item_producto.xml con LayoutInflater, rellena los
       campos y añade cada vista al contenedor.

5) Pantalla 2 - Simulador de análisis (NuevoEscaneoActivity.java y
   activity_nuevo_escaneo.xml):
   - Diseño: app/src/main/res/layout/activity_nuevo_escaneo.xml
     * Título de pantalla, EditText etNombreProducto, Spinner spCategoria
       (cargado desde @array/categorias_alimentos), EditText numérico
       etAzucares (inputType="numberDecimal") y botones btnCancelar y btnGuardar.
   - Lógica: app/src/main/java/wilman/martins/scan2bite/NuevoEscaneoActivity.java
     * Registrada en app/src/main/AndroidManifest.xml.
     * Valida campos no vacíos y valor numérico válido (>= 0).
     * Impacto real del Spinner en calcularPuntuacion(): parte de 100 y resta
       azucares * factor según categoría (Bebidas: 4.5, Bollería: 3.5,
       Snacks: 3.0, Cereales: 2.5, Lácteos: 2.0), acotado entre 0 y 100.
     * Asigna alternativa saludable por categoría en obtenerAlternativa().
     * Empaqueta el Producto en un Bundle, asigna setResult(RESULT_OK, intent)
       y finaliza con finish(). El botón Cancelar hace setResult(RESULT_CANCELED)
       y finish().

6) Tarjeta reutilizable (app/src/main/res/layout/item_producto.xml):
   - Izquierda: ImageView imgCategoria asignado dinámicamente en MainActivity
     mediante switch (asignarIconoCategoria) con ic_bebida, ic_bolleria,
     ic_lacteo, ic_cereal o ic_snack.
   - Centro: TextView en negrita (tvNombreProducto), TextView con categoría y
     gramos de azúcar (tvDetalleProducto) y TextView en verde oscuro con la
     alternativa recomendada (tvAlternativa).
   - Derecha: TextView tvPuntuacion ("XX/100") con fondo bg_badge_score cuyo
     tinte cambia dinámicamente en asignarColorPuntuacion() (verde >= 70,
     naranja >= 40, rojo < 40).

7) Documentación (README.txt):
   - Archivo README.txt en la raíz del proyecto con todos los apartados
     solicitados.


4. PRUEBAS DE FUNCIONAMIENTO Y RESULTADOS ESPERADOS
--------------------------------------------------------------------------------
Prueba 1: Estado inicial vacío
- Acción: Iniciar la aplicación por primera vez.
- Resultado esperado:
  * La cabecera muestra "Scan2Bite" y "Productos analizados: 0".
  * El área del historial muestra el mensaje "Aún no has analizado ningún
    producto" visible y no hay tarjetas en pantalla.

Prueba 2: Inserción de 3 productos
- Acción: Pulsar "Analizar nuevo producto" e insertar 3 productos distintos:
  1) Nombre: "Yogur natural", Categoría: "Lácteos", Azúcar: "4" ->
     Puntuación: 92/100 (verde >= 70), icono ic_lacteo, alternativa de lácteos.
  2) Nombre: "Refresco cola", Categoría: "Bebidas", Azúcar: "11" ->
     Puntuación: 50/100 (naranja >= 40), icono ic_bebida, alternativa de bebidas.
  3) Nombre: "Bollo chocolate", Categoría: "Bollería", Azúcar: "25" ->
     Puntuación: 13/100 (rojo < 40), icono ic_bolleria, alternativa de bollería.
- Resultado esperado:
  * El texto de estado vacío desaparece.
  * El contador se actualiza progresivamente hasta "Productos analizados: 3".
  * Se muestran las 3 tarjetas con sus respectivos iconos, detalles,
    alternativas saludables y colores de insignia (verde, naranja y rojo).

Prueba 3: Cancelación sin añadir datos
- Acción: Estando con 3 productos en el historial, pulsar "Analizar nuevo
  producto", rellenar o dejar vacío el formulario y pulsar "Cancelar" (o el
  botón Atrás del sistema).
- Resultado esperado:
  * NuevoEscaneoActivity devuelve RESULT_CANCELED y se cierra.
  * MainActivity no añade ningún elemento a listaProductos.
  * El contador permanece en "Productos analizados: 3" y el historial no cambia.

Prueba 4: Navegación manteniendo elementos sin duplicar
- Acción: Con 3 productos ya presentes en el historial, volver a pulsar
  "Analizar nuevo producto", añadir un 4º producto (por ejemplo "Avena tostada",
  "Cereales", "2") y pulsar "Guardar en historial".
- Resultado esperado:
  * MainActivity recibe RESULT_OK, añade el 4º producto a listaProductos y
    ejecuta actualizarLista() -> pintarProductos().
  * Gracias a contenedorProductos.removeAllViews(), las vistas anteriores se
    limpian antes de volver a pintarse.
  * Se muestran exactamente 4 tarjetas (sin duplicar las 3 anteriores) y el
    contador marca "Productos analizados: 4".
================================================================================
