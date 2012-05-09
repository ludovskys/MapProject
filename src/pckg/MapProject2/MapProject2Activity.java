package pckg.MapProject2;

import java.util.ArrayList;
import java.util.List;

//importation of the google map API
import com.google.android.maps.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.graphics.drawable.Drawable;

import android.location.Location;
import android.location.LocationListener;

import android.os.Bundle;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem; 
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

	private List<Overlay> mapOverlays;
	
	private Button bAddPlace, bModifyPlace;
	
	private ListView listPlaces;
	
	public TextView tvInfo, tvFirstPoint, tvSecondPoint, tvDistance, tvNameFirstPoint, 
					tvNameSecondPoint, tvResultDistance;
	
	private double lat = 0;
	private double lng = 0;
	
	// lists which will contain information about places
	public static ArrayList<String> placesName = new ArrayList<String>();
	public static ArrayList<String> placesStreet = new ArrayList<String>();
	public static ArrayList<Double> placesCoorLong = new ArrayList<Double>();
	public static ArrayList<Double> placesCoorLat = new ArrayList<Double>();
	public static ArrayList<Integer> placesNumber = new ArrayList<Integer>();
	public static ArrayList<String> placesContactHome = new ArrayList<String>();
	
	// menu
	private static final int MENU_OPTIONS = 0;
	private static final int MENU_QUITTER = 1;
	private static final int SOUS_MENU_OPTIONS_MAP_MODE = 2;
	private static final int SOUS_MENU_MAP_MODE_SATELLITE = 3;
	private static final int SOUS_MENU_MAP_MODE_TRAFFIC = 4;
	private static final int SOUS_MENU_OPTIONS_ADD_MODIFY_POINT = 5;
	private static final int SOUS_MENU_OPTIONS_DISTANCE_POINT = 6;
	
	// contextual menu
	private static final int CONTEXTUAL_MENU_ADD_POINT = 1; 
	
	public static final String PREFS_PLACES = "MyPlacesFile16"; 
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private int compteurPlaces;
	
	// contain the position in the list of the place to modify
	public static int posPlaceModify;

	private static final String ADDMOD="Add";	
	private static final String DISTANCEMOD="Distance";
	private static String mod = ADDMOD;
	
	private String place1 ="", place2 = ""; 
	
	private Location loc1, loc2;
	
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
    	tvInfo = (TextView) findViewById(R.id.textviewinfos);
        tvFirstPoint = (TextView) findViewById(R.id.textviewpoint1);
        tvSecondPoint = (TextView) findViewById(R.id.textviewpoint2);
        tvDistance = (TextView) findViewById(R.id.textviewdistance);
        
        tvNameFirstPoint = (TextView) findViewById(R.id.textviewnamepoint1);
        tvNameSecondPoint = (TextView) findViewById(R.id.textviewnamepoint2);
        tvResultDistance = (TextView) findViewById(R.id.textviewresultdistance);
        
        tvFirstPoint.setVisibility(View.GONE);
        tvSecondPoint.setVisibility(View.GONE);
        tvDistance.setVisibility(View.GONE);
        
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
              
            // filling of the lists
            fillLists();

            listPlaces = (ListView)findViewById(R.id.listViewPlaces); 
            
            // filling of the list adapter
      	  	listAdapter = new ArrayAdapter<String>(this, 
      	  			android.R.layout.simple_list_item_1, placesName);
            
      	  	listPlaces.setAdapter(listAdapter);

      	  	listPlaces.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> list, View v,
						int pos, long id) {
					String item = (String) list.getItemAtPosition(pos);
					
					int itemPosition = pos;
					
					if(mod.equals(ADDMOD))
					{
						displayModifyPlaceButton();
					}
					else if (mod.equals(DISTANCEMOD))
					{
						if (place1.equals(""))
						{
							place1 = item;
							tvNameFirstPoint.setText(item);
							
							loc1 = new Location("gps");
							loc1.setLatitude(placesCoorLat.get(itemPosition)); 
							loc1.setLongitude(placesCoorLong.get(itemPosition)); 
						}
						else if (place2.equals(""))
						{
							place2 = item;
							tvNameSecondPoint.setText(item);
							
							loc2 = new Location("gps");
							loc2.setLatitude(placesCoorLat.get(itemPosition)); 
							loc2.setLongitude(placesCoorLong.get(itemPosition));
							
							float d = loc1.distanceTo(loc2);
							
							tvResultDistance.setText(Double.toString(Math.round(d/1000)) + " km");
						}
						else if (!tvResultDistance.equals(""))
						{
							loc1 = null;
							loc2 = null;
							place2 = "";
									
							tvResultDistance.setText("");
							tvNameSecondPoint.setText("");
							
							place1 = item;
							tvNameFirstPoint.setText(item);
							
							loc1 = new Location("gps");
							loc1.setLatitude(placesCoorLat.get(itemPosition)); 
							loc1.setLongitude(placesCoorLong.get(itemPosition));
						}
					}
					
					// creation of the chosen point
					GeoPoint p = new GeoPoint((int) (placesCoorLat.get(itemPosition) * 1E6), 
							(int) (placesCoorLong.get(itemPosition) * 1E6)); 
					
					String subTitle = placesStreet.get(itemPosition);
					
					if (!placesContactHome.get(itemPosition).equals(""))
					{
						subTitle = subTitle + "\nHome of " + placesContactHome.get(itemPosition);
					}
					
					OverlayItem overlay = new OverlayItem(p, item, subTitle);
					
					itemizedoverlay.addOverlay(overlay);
		            mapOverlays.add(itemizedoverlay);
		            
		            mapController.setCenter(p);
   
		            posPlaceModify = itemPosition;
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
  	  						
  	  						myPoint = new GeoPoint(longpressLocation.getLatitudeE6(), 
  	  								longpressLocation.getLongitudeE6());
  	  					
  	  						//myPoint = longpressLocation;
      	  				}
      	  			});
      	  		}
      	  	});

      	  	
        } // end if (mapView != null )
      	  	

         
        bAddPlace = (Button) findViewById(R.id.button_add_place);
        
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
					//startActivity(i);
					
					startActivity(i);
					
				}
        		
        	}); 
        }
        
        
        
    } // end public void initUI()
    
    public void fillLists()
    {
    	// if the lists are empty (happens when the orientation change)
        if (placesName.isEmpty()) 
        {
        	// recovering of the places previously added by the user in other sessions
        	for (int i=1; i < compteurPlaces; i++)
        	{
        		if ( !settings.getString("name"+Integer.toString(i),"").equals("") )
        		{
        			placesName.add(settings.getString("name"+Integer.toString(i),""));
        			placesStreet.add(settings.getString("address"+Integer.toString(i),""));
        			placesCoorLat.add(Double.parseDouble(settings.getString("lat"+Integer.toString(i),"")));
        			placesCoorLong.add(Double.parseDouble(settings.getString("long"+Integer.toString(i),"")));
        			placesNumber.add(i);
        			placesContactHome.add(settings.getString("contact"+Integer.toString(i),""));
        		}
        	}
        	
        } // end if (placesName.isEmpty())
    }
    
    public void displayModifyPlaceButton()
    {	
    	bModifyPlace.setVisibility(View.VISIBLE);
    }
    

    // creation of the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);
    	
    	SubMenu fileOptions = menu.addSubMenu(0,MENU_OPTIONS,1,R.string.options);
    	SubMenu fileMapMode = fileOptions.addSubMenu(0, SOUS_MENU_OPTIONS_MAP_MODE, 
    			1, R.string.mapmode);
    	
    	fileOptions.addSubMenu(0,SOUS_MENU_OPTIONS_ADD_MODIFY_POINT,2,"Display/Add/Modify point");
    	fileOptions.addSubMenu(0,SOUS_MENU_OPTIONS_DISTANCE_POINT,3,"Distance between points");
    	
    	
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
    			
    		case SOUS_MENU_OPTIONS_ADD_MODIFY_POINT:
    			mod = ADDMOD;
    			addModifyDeletePointsMod();
    			tvInfo.setText(R.string.text_pres1);
    			return true;
    			
    		case SOUS_MENU_OPTIONS_DISTANCE_POINT:
    			mod = DISTANCEMOD;
    			distancePointsMod();
    			tvInfo.setText(R.string.text_pres2);
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
    	compteurPlaces = settings.getInt("counterPlaces", 0);
    	
    	placesName.add("MyTestPoint"+compteurPlaces);
    	   
		placesStreet.add("");

		placesCoorLong.add((double) myPoint.getLongitudeE6() / 1E6);

		placesCoorLat.add((double) myPoint.getLatitudeE6() / 1E6);
		 
		placesNumber.add(compteurPlaces);  
		
		placesContactHome.add("");
 
		listAdapter.notifyDataSetChanged();
		
		editor.putString("name"+Integer.toString(compteurPlaces), "MyTestPoint"+compteurPlaces);
		editor.putString("address"+Integer.toString(compteurPlaces), "");
		
		editor.putString("long"+Integer.toString(compteurPlaces), 
				Double.toString((double) myPoint.getLongitudeE6() / 1E6));
		
		editor.putString("lat"+Integer.toString(compteurPlaces), 
				Double.toString((double) myPoint.getLatitudeE6() / 1E6));
		
		editor.putString("contact"+Integer.toString(compteurPlaces), 
				"");
		
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
	
	// procedure called if the application is on the "add point" mod
	public void addModifyDeletePointsMod()
	{
		bAddPlace.setVisibility(View.VISIBLE);
		bModifyPlace.setVisibility(View.GONE);
		
		tvNameFirstPoint.setText("");
		tvNameSecondPoint.setText("");
		tvResultDistance.setText("");
		
		loc1 = null;
		loc2 = null;
		
		place1 = "";
		place2 = "";
		 
		tvFirstPoint.setVisibility(View.GONE);
        tvSecondPoint.setVisibility(View.GONE);
        tvDistance.setVisibility(View.GONE);
        tvNameFirstPoint.setVisibility(View.GONE);
        tvNameSecondPoint.setVisibility(View.GONE);
        tvResultDistance.setVisibility(View.GONE);
	}
	
	// procedure called if the application is on the "distance" mod
	public void distancePointsMod()
	{
		bAddPlace.setVisibility(View.GONE);
		bModifyPlace.setVisibility(View.GONE);
		
		//mapView.setTop(100);
		
		tvFirstPoint.setVisibility(View.VISIBLE);
        tvSecondPoint.setVisibility(View.VISIBLE);
        tvDistance.setVisibility(View.VISIBLE); 
        
        tvNameFirstPoint.setVisibility(View.VISIBLE);
        tvNameSecondPoint.setVisibility(View.VISIBLE);
        tvResultDistance.setVisibility(View.VISIBLE);
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

