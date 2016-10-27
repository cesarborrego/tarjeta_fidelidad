package com.neology.loyaltycard;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.neology.loyaltycard.adapters.PlaceAutocompleteAdapter;
import com.neology.loyaltycard.common.activities.SampleActivityBase;
import com.neology.loyaltycard.utils.CheckInternetConnection;
import com.neology.loyaltycard.utils.LocationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends SampleActivityBase
        implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

//    AIzaSyALC76IHvlOodORN9ehqxuOxs3HhVg2sVc

    public static final String TAG = MapsActivity.class.getSimpleName();
    public static String urlRoute = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    //Implement distance
    public static String urlDistance = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=19.4139631,-99.1750271&destinations=19.4905431,-99.1975396&mode=driving&language=es_ES";
    LocationHelper locationHelper;
    double lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, lat4, lon4, lat5, lon5, lat6, lon6, lat7, lon7, lat8, lon8, lat9, lon9;
    Marker newPoint;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private FloatingActionButton navigationBtn;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public static List<LatLng> decode(final String encodedPath) {
        int len = encodedPath.length();
        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;
        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }
        return path;
    }

    public static String getRequestUrl(double latOri, double lngOri, double latDes, double lngDes) {
        return urlRoute + latOri + "," + lngOri + "&destination=" + latDes + "," + lngDes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        navigationBtn = (FloatingActionButton) findViewById(R.id.navigationBtnID);
        setUpMapIfNeeded();
//        createPointsRandom();
//        createMarquersGasStation();
        connectGoogleServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void connectGoogleServices() {
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
//        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
//        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));

            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }
            Log.i(TAG, "Place details received: " + place.getName());
            mMap.clear();
            createGasMarker(place.getLatLng().latitude, place.getLatLng().longitude);
            places.release();

        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                getLocation();
//                createRoute();
//                createMarquersGasStation();
            }
        }
    }

    private void getLocation() {
        locationHelper = new LocationHelper(getApplicationContext());
        if (CheckInternetConnection.isConnectedToInternet(getApplicationContext())) {
            if (locationHelper.canGetLocation()) {
                lat = locationHelper.getLocation().getLatitude();
                lon = locationHelper.getLocation().getLongitude();
                setUpMap();
            } else {
                Toast.makeText(getApplicationContext(), "NO GPS", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "NO WIFI", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        configCamera();
    }

    private void configCamera() {
        CameraPosition cameraPosition = new CameraPosition(new LatLng(lat, lon), 17, 20, 40);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
    }

    private void createRoute(LatLng latLng) {
        makeJson(latLng.latitude, latLng.longitude);
        initNavigation(latLng.latitude, latLng.longitude);

//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                mMap.clear();
//                createMarquersGasStation();
//                if (newPoint == null) {
//                    createNewMarker(latLng.latitude, latLng.longitude);
//                } else {
//                    newPoint.remove();
//                    createNewMarker(latLng.latitude, latLng.longitude);
//                }
//                makeJson(latLng.latitude, latLng.longitude);
//                initNavigation(latLng.latitude, latLng.longitude);
//            }
//        });
    }

    private void createNewMarker(double lat, double lon) {
        newPoint = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon)));
        newPoint.showInfoWindow();
    }

    private void createGasMarker(double lat, double lon) {
        Log.d(null, "lat "+lat);
        Log.d(null, "lon "+lon);
         Marker newPoint = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon)));
        newPoint.showInfoWindow();
        createRoute(new LatLng(lat, lon));
    }

    private void makeJson(double latDes, double lngDes) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getRequestUrl(lat, lon, latDes, lngDes),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        parseJsonResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );

        // Adding request to request queue
        VolleyApp.getmInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void parseJsonResponse(JSONObject response) {
        if (response == null || response.length() == 0) {
            return;
        }
        try {
            if (response.has("status")) {
                String status = response.getString("status");
                if (status.equals("OK")) {
                    JSONArray jsonArray = response.getJSONArray("routes");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONObject overview_polyline = jsonObject.getJSONObject("overview_polyline");
                    String points = overview_polyline.getString("points");

                    List<LatLng> listaCoordenadasRuta = decode(points);

                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.argb(150, 0, 181, 247)).width(25);
                    for (LatLng latLng : listaCoordenadasRuta) {
                        polylineOptions.add(latLng);
                    }
                    mMap.addPolyline(polylineOptions);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initNavigation(final double latDes, final double lngDes) {
        navigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "waze://?ll=" + latDes + "," + lngDes + "&z=10&navigate=yes";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }
            }
        });

//        navigationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), GooglePlacesAutocompleteActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void createPointsRandom() {
        if (lat != 0.0 && lon != 0.0) {
            String latMark1 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio = String.valueOf((int) (Math.random() * 2000 + 1));
            lat1 = Double.parseDouble(latMark1 + numeroAleatorio);
            String lngMark1 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio1 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon1 = Double.parseDouble(lngMark1 + numeroAleatorio1);


            String latMark2 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio2 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat2 = Double.parseDouble(latMark2 + numeroAleatorio2);
            String lngMark2 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio3 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon2 = Double.parseDouble(lngMark2 + numeroAleatorio3);


            String latMark3 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio4 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat3 = Double.parseDouble(latMark3 + numeroAleatorio4);
            String lngMark3 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio5 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon3 = Double.parseDouble(lngMark3 + numeroAleatorio5);


            String latMark4 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio6 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat4 = Double.parseDouble(latMark4 + numeroAleatorio6);
            String lngMark4 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio7 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon4 = Double.parseDouble(lngMark4 + numeroAleatorio7);


            String latMark5 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio8 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat5 = Double.parseDouble(latMark5 + numeroAleatorio8);
            String lngMark5 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio9 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon5 = Double.parseDouble(lngMark5 + numeroAleatorio9);


            String latMark6 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio10 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat6 = Double.parseDouble(latMark6 + numeroAleatorio10);
            String lngMark6 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio11 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon6 = Double.parseDouble(lngMark6 + numeroAleatorio11);


            String latMark7 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio12 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat7 = Double.parseDouble(latMark7 + numeroAleatorio12);
            String lngMark7 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio13 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon7 = Double.parseDouble(lngMark7 + numeroAleatorio13);


            String latMark8 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio14 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat8 = Double.parseDouble(latMark8 + numeroAleatorio14);
            String lngMark8 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio15 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon8 = Double.parseDouble(lngMark8 + numeroAleatorio15);


            String latMark9 = String.valueOf(lat).substring(0, 5);
            String numeroAleatorio16 = String.valueOf((int) (Math.random() * 2000 + 1));
            lat9 = Double.parseDouble(latMark9 + numeroAleatorio16);
            String lngMark9 = String.valueOf(lon).substring(0, 6);
            String numeroAleatorio17 = String.valueOf((int) (Math.random() * 2000 + 1));
            lon9 = Double.parseDouble(lngMark9 + numeroAleatorio17);
        }
    }

    private void createMarquersGasStation() {
        if (lat != 0.0 && lon != 0.0) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat1, lon1))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat2, lon2))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat3, lon3))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat4, lon4))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat5, lon5))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat6, lon6))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat7, lon7))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat8, lon8))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat9, lon9))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shell_map))
                    .title("GASOLINERA"));
        }


//        String [][] latLIng = {
//                {"19.4126514", "-99.1720576"},
//                {"19.411972","-99.1709786"},
//                {"19.4210185","-99.1648878"},
//                {"19.4126514","-99.1720576"},
//                {"19.4126514","-99.1720576"},
//                {"19.411972","-99.1709786"},
//                {"19.4126514","-99.1720576"},
//                {"19.4171051","-99.1753572"},
//                {"19.4303392","-99.1698364"},
//                {"19.4040058","-99.176098"}};
//        for (int i =0 ; i<latLIng.length; i ++) {
//            for (int j =0; j<latLIng[i].length; j++) {
//                Log.d(null, "Coord " + latLIng[i][j]);
//
//                Marker marker = mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(Double.parseDouble(latLIng[i][j].toString()), Double.parseDouble(latLIng[i][j].toString())))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_gas_station_black_24dp))
//                        .title("GASOLINERA"));
//                marker.showInfoWindow();
//            }
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}

//    public static AlertDialog showAlertDialogConfigs(int dialogType, final Activity a) {
//
//        final Intent intent = new Intent();
//        String mensaje = "";
//
//        switch(dialogType){
//            case 0:
//                intent.setAction(Settings.ACTION_NFC_SETTINGS);
//                mensaje = "NFC DESACTIVADO";
//                break;
//            case 1:
//                intent.setAction(Settings.ACTION_SETTINGS);
//                mensaje = "CONEXIÓN WIFI DESACTIVADA";
//                break;
//            case 2:
//                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mensaje = a.getResources().getString(R.string.gpsDesactivado);
//                break;
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(a);
//        builder.setTitle(R.string.titleAlertDialog);
//        builder.setMessage(mensaje)
//                .setPositiveButton(R.string.btnTextAceptar,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                a.startActivity(intent);
//                            }
//
//                        })
//                .setNegativeButton(R.string.btnTextCancelar,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                dialog.cancel();
//                                //				finish();
//                            }
//                        });
//        AlertDialog alertDialog = builder.create();
//        return alertDialog;
//    }

