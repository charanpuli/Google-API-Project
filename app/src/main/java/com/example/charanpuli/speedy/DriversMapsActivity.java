package com.example.charanpuli.speedy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DriversMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    private Button LogoutDriverButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean currentLogoutDriverStatus=false;
    private String driverID,customerID="";
    private DatabaseReference AssignedCustomerRef;
    private DatabaseReference AssignedCustomerPickupRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        driverID=mAuth.getCurrentUser().getUid();

        LogoutDriverButton=(Button)findViewById(R.id.driver_logout_button);



        LogoutDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLogoutDriverStatus=true;
                disconnectTheDriver();
               mAuth.signOut();
                LogoutDriver();
            }
        });
        GetAssignedCustomerRequest();



    }



    private void GetAssignedCustomerRequest()
    {
         AssignedCustomerRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers")
                 .child(driverID).child("CustomerRideID");

         AssignedCustomerRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot)
             {
                 if(dataSnapshot.exists())
                 {
                     customerID=dataSnapshot.getValue().toString();
                     GetAssignedCustomerPickUpLocation();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }

    private void GetAssignedCustomerPickUpLocation()
    {
         AssignedCustomerPickupRef=FirebaseDatabase.getInstance().getReference().child("Customer's Requests")
                 .child(customerID).child("l");

         AssignedCustomerPickupRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists())
                 {
                     List<Object> CustomerLocationMap=(List<Object>)dataSnapshot.getValue();
                     double LocationLat=0;
                     double LocationLng=0;


                     if(CustomerLocationMap.get(0) !=null)
                     {
                         LocationLat=Double.parseDouble(CustomerLocationMap.get(0).toString());
                     }
                     if(CustomerLocationMap.get(1) !=null)
                     {
                         LocationLng=Double.parseDouble(CustomerLocationMap.get(1).toString());
                     }
                     LatLng DriverLatLng=new LatLng(LocationLat,LocationLng);
                     mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("Customer PickUp Location"));
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null) {
            lastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));


            String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference DriverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");

            GeoFire geoFireAvailability = new GeoFire(DriverAvailabilityRef);
            DatabaseReference DriverWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");

            GeoFire geoFireWorking = new GeoFire(DriverWorkingRef);
           switch (customerID)
           {
               case "":
                   geoFireWorking.removeLocation(UserID);
                   geoFireAvailability.setLocation(UserID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                   break;
                   default:
                       geoFireAvailability.removeLocation(UserID);

                       geoFireWorking.setLocation(UserID, new GeoLocation(location.getLatitude(), location.getLongitude()));


                       break;
           }


        }



    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

       if(!currentLogoutDriverStatus)
       {
           disconnectTheDriver();
       }

    }

    private void disconnectTheDriver()
    {
        String UserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference DriverAvailabilityRef=FirebaseDatabase.getInstance().getReference().child("Drivers Working");

        GeoFire geoFire=new GeoFire(DriverAvailabilityRef);
        geoFire.removeLocation(UserID);


    }
    private void LogoutDriver()
    {
        Intent welcomeIntent=new Intent(DriversMapsActivity.this,WelcomeActivity.class);
        welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(welcomeIntent);
        finish();
    }

}
