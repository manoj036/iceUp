//package com.example.manojkumar.iceup;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.IBinder;
//import android.service.quicksettings.Tile;
//import android.service.quicksettings.TileService;
//import android.support.annotation.RequiresApi;
//import android.util.Log;
//
//@RequiresApi(api = Build.VERSION_CODES.N)
//public class IceUpTileService extends TileService {
//    private Tile tile;
//    public IceUpTileService() {
//        tile=getQsTile();
//    }
//
//    @Override
//    public void onStartListening() {
//        super.onStartListening();
//        if(tile.getState()==Tile.STATE_ACTIVE){
//            startService(new Intent(this, iceUpService.class));
//        }else if(tile.getState()==Tile.STATE_INACTIVE){
//            iceUpService.service.stopSelf();
//        }
//        Log.d("TAG", "onStartListening: Coool");
//        tile.setState(Tile.STATE_ACTIVE);
//        tile.updateTile();
//    }
//
//    @Override
//    public void onClick() {
//        Log.d("QS", "Tile tapped");
//        updateTile();
//    }
//    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
//    private static final String PREFERENCES_KEY = "com.google.android_quick_settings";
//
//// Other class members ...
//
//    // Changes the appearance of the tile.
//    private void updateTile() {
//        Tile tile = this.getQsTile();
//        boolean isActive = getServiceStatus();
//        int newState;
//        if (isActive) {
//            newState = Tile.STATE_ACTIVE;
//        } else {
//            newState = Tile.STATE_INACTIVE;
//        }
//        tile.setState(newState);
//        tile.updateTile();
//    }
//
//    private boolean getServiceStatus() {
//
//        SharedPreferences prefs =
//                getApplicationContext()
//                        .getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
//        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
//        isActive = !isActive;
//        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();
//        return isActive;
//    }
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//}
