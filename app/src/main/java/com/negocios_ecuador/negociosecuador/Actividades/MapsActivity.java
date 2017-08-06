package com.negocios_ecuador.negociosecuador.Actividades;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.negocios_ecuador.negociosecuador.Otros.DirectionsJSONParser;
import com.negocios_ecuador.negociosecuador.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.design.R.attr.colorPrimary;
import static com.negocios_ecuador.negociosecuador.R.mipmap.marker_actual;
import static com.negocios_ecuador.negociosecuador.R.mipmap.marker_negocio;
import static com.negocios_ecuador.negociosecuador.R.id.map;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private String provider;
    private final int FINE_LOCATION_PERMISSION = 9999;
    private int marcador_usuario_continuar = 0;
    public LatLng usuario = new LatLng(-1.671398, -78.635889);
    public LatLng negocio = new LatLng(-1.653221, -78.665237);
    public Button btn_navegar;
    public Spinner spinner;
    public String latitud, longitud;
    public String nombreNegocio = "Mapa";


    public ArrayList<LatLng> markerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);




        // Showing and Enabling clicks on the Home/Up button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        //Request Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
        }


        //Recibe las coordendas desde la activity anteior
        Intent intent = getIntent();
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        latitud = b.getString("LATITUD");
        longitud = b.getString("LONGITUD");
        nombreNegocio = b.getString("NOMBRE");
        setTitle(nombreNegocio);
        negocio=new LatLng(Double.valueOf(latitud), Double.valueOf(longitud)); //Asigna las coordendas al punto para el mapa

        //Castea los elementos de la interfaz
        spinner = (Spinner) findViewById(R.id.spinner);

        Button btn_actualizar = (Button) findViewById(R.id.btn_actualizar);
        btn_navegar = (Button) findViewById(R.id.btn_navegar);


        //Acciones para el botón actualizar
        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarMapa();
            }
        });

        //Acciones para el botón navegar
        btn_navegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navegar();
            }
        });

        if(Integer.valueOf(android.os.Build.VERSION.SDK) > 17){

            //Cambia el tipo del mapa dependiendo de  la opción del spinner
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0: // Normal
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case 1: // Hibrido
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                        case 2: // Satelite
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        case 3: // Tierra
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            break;
                        case 4: // Terrain
                            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                            break;
                        default:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spinner.setSelection(0);

        } else{
            spinner.setVisibility(View.INVISIBLE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            //Location Manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);

            provider = locationManager.getBestProvider(criteria, false);

            //revisa los permisos
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null)
                Log.i("Log info", "Location Archived");
            else
                Log.i("Log info", "No location");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    //Set para las coordenadas del negocio
    private void setNegocio(double lat, double lon){

        LatLng coordenadasNegocio = new LatLng(lat,lon); //Toma los valores de las coordendas y crea un punto
        negocio = coordenadasNegocio; //Asigna el negocio a un nuevo lugar
    }

    //Set para las coordenadas del usuario
    private void setUsuario(double lat, double lon){

        LatLng coordenadasNegocio = new LatLng(lat,lon); //Toma los valores de las coordendas y crea un punto
        usuario = coordenadasNegocio; //Asigna el negocio a un nuevo lugar
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)  {
        inicializaMapa(googleMap);  //Inicializa el mapa
    }



    private void setUsuario(LatLng coordenadasUsuario) {
        negocio = coordenadasUsuario;
    }


    //Cuando la ubicación del usuario cambia o se lo encuentra por primera vez
    @Override
    public void onLocationChanged(Location location) {

        ubicarUsuario(location); //Metodo que ubica al usuario y lo grafica en el mapa

    }

    //Método para reiniciar el mapa
    public void reiniciarMapa() {
        mMap.clear(); //Limpia el mapa de marcadores y zoom
        onMapReady(mMap); //Inicializa el mapa otra vez
        marcador_usuario_continuar = 0; //Da visto bueno para buscar la ubicación del usuario de nuevo
        btn_navegar.setVisibility(View.GONE);
    }


    //Metodo que ubica al usuario y lo grafica en el mapa
    public void ubicarUsuario(Location location) {
        //Permite crear un marcador cuando la ubicación por GPS es hecha, luego de la primera vez deja de crear maradores.
        if (marcador_usuario_continuar == 0) {
            Double lat, lng;
            lat = location.getLatitude();
            lng = location.getLongitude();
            LatLng myPosition = new LatLng(lat, lng);
            usuario = myPosition;

            //añade marcador de usuario
            mMap.addMarker(new MarkerOptions()
                    .position(myPosition) //asigna la posicion
                    .title("Usted está aquí")  //Pone una etiqueta
                    .draggable(true)    //Lo hace arrastrable en caso de que sea inexacto
                    .icon(BitmapDescriptorFactory.fromResource(marker_actual))); //Asigna un ícono de la carpeta Drawable

            mMap.setOnInfoWindowClickListener(this);

            // Crea límites de mapas que abarquen las ubicaciones del marcador y el usuario
            //LatLngBounds RUTA = new LatLngBounds(
            //myPosition, negocio);
            // Set the camera to the greatest possible zoom level that includes the bounds
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(RUTA, 40));
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(negocio, 14));


            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(RUTA.getCenter(), 14));
            marcador_usuario_continuar = +1; //Hace que deje de dibujar el marcador y actualizar la ubicación
            btn_navegar.setVisibility(View.VISIBLE); //muestra el botón de navegar

            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 14));
            //Log.i("Log info", " Lat : "+lat+" Long : "+lng);
        }
    }



    public void inicializaMapa(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            uiSettings.setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);

        }

        //Añade marcador del negocio
        mMap.addMarker(new MarkerOptions()
                .position(this.negocio)  //asigna la posicion
                .title("negocio")   //Pone una etiqueta
                .icon(BitmapDescriptorFactory.fromResource(marker_negocio))); //Asigna un ícono de la carpeta Drawable

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(negocio, 13));

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                clickMapa(latLng);
            }
        });

    }

    public void clickMapa(LatLng point) {




    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    //método que permite la navegación
    private void navegar() {
        LatLng origin = usuario;
        LatLng dest = negocio;

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Marker " + marker.getTitle() + " is clicked", Toast.LENGTH_SHORT).show();
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("fail downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }


}
