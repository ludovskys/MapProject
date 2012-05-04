package pckg.MapProject2;

import java.util.ArrayList;
import java.util.List;

//importation of the google map API
import com.google.android.maps.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;

import android.graphics.drawable.Drawable;

import android.location.Location;
import android.location.LocationListener;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Contacts.People;

import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem; 
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MapProject2Activity extends MapActivity implements LocationListener {
    /** Called when the activity is first created. */

	private MyCustomMapView mapView;
	private MyItemizedOverlay itemizedoverlay;
	private GeoPoint myPoint;
	private OverlayItem myOverlayItem;
	private MapController mapController;
	
	private Context mContext;
	
	public static ArrayAdapter<String> listAdapter;
	
	private Button bModifyPlace;
	
	/*
	private String bestProvider;
	private LocationManager lm;
	private Projection projection;  
	*/
	
	private List<Overlay> mapOverlays;
	
	private ListView listPlaces;
	
	private double lat = 0;
	private double lng = 0;
	
	// lists which will contain information about places
	public static ArrayList<String> placesName = new ArrayList<String>();
	public static ArrayList<String> placesStreet = new ArrayList<String>();
	public static ArrayList<Double> placesCoorLong = new ArrayList<Double>();
	public static ArrayList<Double> placesCoorLat = new ArrayList<Double>();
	
	// menu
	public static final int MENU_OPTIONS = 0;
	public static final int MENU_QUITTER = 1;
	public static final int SOUS_MENU_OPTIONS_MAP_MODE = 2;
	public static final int SOUS_MENU_MAP_MODE_SATELLITE = 3;
	public static final int SOUS_MENU_MAP_MODE_TRAFFIC = 4;
	
	// contextual menu
	public static final int CONTEXTUAL_MENU_ADD_POINT = 1;
	
	public static final String PREFS_PLACES = "MyPlacesFile";
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	public int compteurPlaces;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        settings = getSharedPreferences(PREFS_PLACES, 0);
        editor = settings.edit();
        
        // if compteurPlaces is not set (very first start of the application)
        if ( (compteurPlaces = settings.getInt("counterPlaces", 0)) == 0 )
        {
        	editor.putInt("counterPlaces", 1);
            editor.commit(); 
        }  
        
        initUI();
  
    } // end public void onCreate(Bundle savedInstanceState)
    
    // called when the orientation changes
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main);

        initUI();
    }
    
    // procedure which init the view
    public void initUI()
    {
    	mContext = getApplicationContext();
        
        mapView = (MyCustomMapView) findViewById(R.id.mapview);
        
        if (mapView != null )
        {
        	mapView.setBuiltInZoomControls(true);
        	mapView.setTraffic(true);
        	mapView.setSatellite(false);
        	
        	mapController = mapView.getController();
            mapOverlays = mapView.getOverlays(); 
            
            Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
            itemizedoverlay = new MyItemizedOverlay(drawable, this);
            
            // example point, located in Mexico, Mexique
            GeoPoint examplePoint = new GeoPoint(19240000,-99120000);
            OverlayItem overlayitem = new OverlayItem(examplePoint, "Hola, Mundo!", "I'm in Mexico City!");
            
            itemizedoverlay.addOverlay(overlayitem);
            mapOverlays.add(itemizedoverlay);
            
            mapController.setCenter(examplePoint);
            mapController.setZoom(4);
            
            /*
            lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
            
    		Criteria criteria = new Criteria();
    		bestProvider = lm.getBestProvider(criteria, false);
    		
    		Log.d("MapProject2Activity", bestProvider);
            
            Location myLoc = new Location(bestProvider);
            
            //Location myLoc = lm.getLastKnownLocation(bestProvider);
            
            lat = myLoc.getLatitude();
            lng = myLoc.getLongitude();
            
            myPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
            myOverlayItem = new OverlayItem(myPoint, "Hello", "That's me !");
            
            mapController.setCenter(myPoint);
            mapController.setZoom(18);
            
            itemizedoverlay.addOverlay(myOverlayItem);
            mapOverlays.add(itemizedoverlay);*/
            
            // if the lists are empty (happens when the orientation change)
            if (placesName.isEmpty())
            {
            	placesName.add("Maison");
                placesName.add("Resto préféré");
                placesName.add("Wind River");
                
                
                placesStreet.add("Strada Garii");
            	placesStreet.add("Strada Domneasca");
            	placesStreet.add("Strada Domneasca");
            	
            	
            	placesCoorLat.add(45.442957);
            	placesCoorLat.add(45.444058);
            	placesCoorLat.add(45.438296);
            	
            	
            	placesCoorLong.add(28.050767);
            	placesCoorLong.add(28.055171);
            	placesCoorLong.add(28.056276); 
            	
            	// recovering of the places previously added by the user in other sessions
            	for (int i=1; i < compteurPlaces; i++)
            	{
            		if ( !settings.getString("name"+Integer.toString(i),"").equals("") )
            		{
            			placesName.add(settings.getString("name"+Integer.toString(i),""));
            			placesStreet.add(settings.getString("address"+Integer.toString(i),""));
            			placesCoorLat.add(Double.parseDouble(settings.getString("lat"+Integer.toString(i),"")));
            			placesCoorLong.add(Double.parseDouble(settings.getString("long"+Integer.toString(i),"")));
            		}
            	}
            	
            } // end if (placesName.isEmpty())

            listPlaces = (ListView)findViewById(R.id.listViewPlaces); 
            
            // filling of the list
      	  	listAdapter = new ArrayAdapter<String>(this, 
      	  			android.R.layout.simple_list_item_1, placesName);
            
      	  	listPlaces.setAdapter(listAdapter);

      	  	listPlaces.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> list, View v,
						int pos, long id) {
					String item = (String) list.getItemAtPosition(pos);
					
					int itemPosition = pos;
					
					displayModifyPlaceButton(pos);
					
					// creation of the chosen point
					GeoPoint p = new GeoPoint((int) (placesCoorLat.get(itemPosition) * 1E6), 
							(int) (placesCoorLong.get(itemPosition) * 1E6));
					
					OverlayItem overlay = new OverlayItem(p, item, placesStreet.get(itemPosition));
					
					itemizedoverlay.addOverlay(overlay);
		            mapOverlays.add(itemizedoverlay);
		            
		            mapController.setCenter(p);
				}   	
            }); // end new OnItemClickListener()
      	  	  	
      	  	mapView.setOnLongpressListener(new MyCustomMapView.OnLongpressListener() 
      	  	{
      	  		public void onLongpress(final MapView view, final GeoPoint longpressLocation) {
      	  			runOnUiThread(new Runnable() {
      	  				public void run() {	   	  
      	  					bModifyPlace.setVisibility(View.GONE);
      	  					// creation and opening of the contextual menu
      	  					registerForContextMenu(view);
      	  					openContextMenu(view);
      	  					
      	  					myPoint = longpressLocation;
      	  				}
      	  			});
      	  		}
      	  	});
      	  	/*
      	  	mapView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
					Toast.makeText(mContext, "coucou touch map", Toast.LENGTH_SHORT).show();
					
					return false;
				}
      	  		
      	  	});*/
      	  	
        } // end if (mapView != null )
      	  	

        
        Button bAddPlace = (Button) findViewById(R.id.button_add_place);
        
        if ( bAddPlace != null )
        {
        	bAddPlace.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {		
					bModifyPlace.setVisibility(View.GONE);
					// go to the view to add a place
					Intent i = new Intent (MapProject2Activity.this, AddPlaceActivity.class);
					startActivity(i);	
				} 		
        	});
        } // end if ( bAddPlace != null )
        
        bModifyPlace = (Button) findViewById(R.id.button_modify_place);
        
        if (bModifyPlace != null)
        {
        	bModifyPlace.setVisibility(View.GONE);
        	
        	bModifyPlace.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Intent i = new Intent (MapProject2Activity.this, ModifyPlaceActivity.class);
					startActivity(i);
					
					
					
				}
        		
        	});
        }
        
        
        
    } // end public void initUI()
    
    public void displayModifyPlaceButton(int placeID)
    {
    	//Log.d("MapProject2Activity", "lol");
    	
    	bModifyPlace.setVisibility(View.VISIBLE);
    	
    	String place = placesName.get(placeID);
    	
    	Toast.makeText(mContext, place, Toast.LENGTH_SHORT).show();
    	
    	
    }

    // creation of the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
    	
    	SubMenu fileOptions = menu.addSubMenu(0,MENU_OPTIONS,1,R.string.options);
    	SubMenu fileMapMode = fileOptions.addSubMenu(0, SOUS_MENU_OPTIONS_MAP_MODE, 
    			1, R.string.mapmode);
    	
    	fileMapMode.addSubMenu(0, SOUS_MENU_MAP_MODE_SATELLITE, 1, R.string.satellite);
    	fileMapMode.addSubMenu(0, SOUS_MENU_MAP_MODE_TRAFFIC, 2, R.string.traffic);
    		
    	menu.addSubMenu(1,MENU_QUITTER, 2, R.string.quit);
    	
    	return true;
    } // end public boolean onCreateOptionsMenu(Menu menu)
    
    // when a menu's item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch (item.getItemId())
    	{
    		case MENU_QUITTER:
    			finish(); // end of the application
    			return true;
    			
    		case SOUS_MENU_MAP_MODE_SATELLITE:
    			if (!mapView.isSatellite()) { 	
    				mapView.setSatellite(true); 
    				mapView.setTraffic(false); 	
    			}
    			return true;
    			
    		case SOUS_MENU_MAP_MODE_TRAFFIC:  
    			if (!mapView.isTraffic()) {
    				mapView.setTraffic(true); 
    				mapView.setSatellite(false);	
    			}
    			return true;
    			
    		default :
    			return super.onContextItemSelected(item);
    	}
    } // end public boolean onOptionsItemSelected(MenuItem item)
    
    // creation of the contextual menu
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
    {  
		super.onCreateContextMenu(menu, v, menuInfo);  
				
		menu.setHeaderTitle(R.string.addpointquestion);  
		menu.add(0, CONTEXTUAL_MENU_ADD_POINT, 0, R.string.yes);  
		menu.add(0, v.getId(), 0, R.string.cancel); 
    }  // end public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo)
    
    // when a contextual menu's item is selected
    @Override  
    public boolean onContextItemSelected(MenuItem item) 
    {  
        if(item.getItemId() == CONTEXTUAL_MENU_ADD_POINT)
        {
        	Toast.makeText(mContext, "The point was added on the list", Toast.LENGTH_LONG).show();
        	addPoint();
        }  
        else {return false;}  
        return true;  
    } // end public boolean onContextItemSelected(MenuItem item)
    
    // procedure which add a chosen point
    public void addPoint()
    {
    	placesName.add("MyTestPoint"+compteurPlaces);
    	   
		placesStreet.add("");

		placesCoorLong.add((double) myPoint.getLongitudeE6() / 1E6);

		placesCoorLat.add((double) myPoint.getLatitudeE6() / 1E6);

		listAdapter.notifyDataSetChanged();
		
		editor.putString("name"+Integer.toString(compteurPlaces), "MyTestPoint"+compteurPlaces);
		editor.putString("address"+Integer.toString(compteurPlaces), "");
		
		editor.putString("long"+Integer.toString(compteurPlaces), 
				Integer.toString(myPoint.getLongitudeE6()));
		
		editor.putString("lat"+Integer.toString(compteurPlaces), 
				Integer.toString(myPoint.getLatitudeE6()));
		
        OverlayItem overlay = new OverlayItem(myPoint, "MyTestPoint"+compteurPlaces, 
        		"");
		
		itemizedoverlay.addOverlay(overlay);
        mapOverlays.add(itemizedoverlay);
        
        mapController.setCenter(myPoint);
        
        compteurPlaces++;
		
		editor.putInt("counterPlaces", compteurPlaces);
        editor.commit();
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		lat = location.getLatitude();
		lng = location.getLongitude();
		
		Toast.makeText(getBaseContext(),
				"Location change to : Latitude = " + lat + " Longitude = " + lng,
				Toast.LENGTH_SHORT).show();
		
		myPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		
		mapController.setCenter(myPoint);
        mapController.setZoom(18);
        
        itemizedoverlay.addOverlay(myOverlayItem);
        mapOverlays.add(itemizedoverlay);
		
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}

