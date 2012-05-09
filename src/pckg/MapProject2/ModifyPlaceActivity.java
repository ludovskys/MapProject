package pckg.MapProject2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyPlaceActivity extends Activity {
	
	public static int positionPlace;
	
	private String placeToModify, addrToModify;
	
	private int placeNumber;
	
	private EditText tvNamePlace, tvAddrPlace;
	private TextView tvContactHome;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	private static final int CONTEXTUAL_MENU_DELETE_POINT = 1;
	
	private static final String PREFS_PLACES = "MyPlacesFile15"; 
	
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifyplace);
        
        settings = getSharedPreferences(PREFS_PLACES, 0);
        editor = settings.edit();
        
        // get the place to modify
        positionPlace = MapProject2Activity.posPlaceModify; 
        placeToModify = MapProject2Activity.placesName.get(positionPlace);
        addrToModify = MapProject2Activity.placesStreet.get(positionPlace); 
        placeNumber = MapProject2Activity.placesNumber.get(positionPlace);
        
        tvNamePlace = (EditText) findViewById(R.id.entrymodifyplace);
        tvAddrPlace = (EditText) findViewById(R.id.entrymodifyaddress);
        Button bModify = (Button) findViewById(R.id.button_confirm_modif);
        Button bSuppr = (Button) findViewById(R.id.button_suppr); 
        Button bSetHomeAddr = (Button) findViewById(R.id.button_set_home_addr);
        tvContactHome = (TextView) findViewById(R.id.labelmodifycontacthome);
          
         
        tvNamePlace.setText(placeToModify);
        
        // if the address is not set
        if (addrToModify == null )
        {
        	tvAddrPlace.setText(""); 
        }
        else
        {
        	tvAddrPlace.setText(addrToModify);
        }
        
        
        
        bModify.setOnClickListener(new OnClickListener(){ 

			@Override
			public void onClick(View v) {
				
				// remove the place from the preferences
				editor.remove("name"+Integer.toString(placeNumber));
				editor.remove("address"+Integer.toString(placeNumber));
				editor.remove("long"+Integer.toString(placeNumber));
				editor.remove("lat"+Integer.toString(placeNumber));
				editor.remove("lat"+Integer.toString(placeNumber));
				editor.remove("contact"+Integer.toString(placeNumber));
 
				editor.commit();
				
				/* save the place modify
				 * (we cannot modify directly on the preferences, so we have to delete and readd)
				 */
				editor.putString("name"+Integer.toString(placeNumber), tvNamePlace.getText().toString());
				editor.putString("address"+Integer.toString(placeNumber), tvAddrPlace.getText().toString());
				
				editor.putString("long"+Integer.toString(placeNumber), 
						Double.toString(MapProject2Activity.placesCoorLong.get(positionPlace)));

				editor.putString("lat"+Integer.toString(placeNumber), 
						Double.toString(MapProject2Activity.placesCoorLat.get(positionPlace)));
				
				editor.putString("contact"+Integer.toString(placeNumber), 
						tvContactHome.getText().toString());
				
				editor.commit();
				
				MapProject2Activity.placesName.set(positionPlace, tvNamePlace.getText().toString());
				
				MapProject2Activity.placesStreet.set(positionPlace, tvAddrPlace.getText().toString());
				
				MapProject2Activity.placesContactHome.set(positionPlace, tvContactHome.getText().toString());
				
				// update of the list of places
				MapProject2Activity.listAdapter.notifyDataSetChanged();
				
				// return to the main view
				finish();
				
			}
        	
        });
        
        bSuppr.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v) {
				
				// creation and opening of the contextual menu
				registerForContextMenu(v);
				openContextMenu(v);
						
			}
        	
        });
        
        
        bSetHomeAddr.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				// opening of the contact list activity to choose a contact
				Intent i = new Intent (ModifyPlaceActivity.this, ListContactsActivity.class);
				startActivity(i);
				
			}
        	
        });
        
	}
	
	// creation of the contextual menu
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
    {  
		super.onCreateContextMenu(menu, v, menuInfo);  

		menu.setHeaderTitle(R.string.supprpointquestion);  
		menu.add(0, CONTEXTUAL_MENU_DELETE_POINT, 0, R.string.yes);  
		menu.add(0, v.getId(), 0, R.string.cancel); 

		
    }  // end public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo)
    
    // when a contextual menu's item is selected
    @Override  
    public boolean onContextItemSelected(MenuItem item) 
    {  
        if(item.getItemId() == CONTEXTUAL_MENU_DELETE_POINT)
        {
        	Toast.makeText(getApplicationContext(), "Point deleted", Toast.LENGTH_LONG).show();
        	suppresionPoint();
        }  
        else {return false;}  
        return true;  
    } // end public boolean onContextItemSelected(MenuItem item)
    
    // procedure who suppress a point
    public void suppresionPoint()
    {
    	editor.remove("name"+Integer.toString(placeNumber));
		editor.remove("address"+Integer.toString(placeNumber));
		editor.remove("long"+Integer.toString(placeNumber));
		editor.remove("lat"+Integer.toString(placeNumber));
		editor.remove("contact"+Integer.toString(placeNumber));
		
		editor.commit();
		
		// verif
		MapProject2Activity.placesName.remove(positionPlace);	
		MapProject2Activity.placesStreet.remove(positionPlace);	
		MapProject2Activity.placesCoorLong.remove(positionPlace);	
		MapProject2Activity.placesCoorLat.remove(positionPlace);
		MapProject2Activity.placesNumber.remove(positionPlace);	 
		MapProject2Activity.placesContactHome.remove(positionPlace);
		 
		// update of the list of places
		MapProject2Activity.listAdapter.notifyDataSetChanged();
		
		// we return to the main view
		finish();
    }
    
    @Override
    protected void onResume()
    {
    	super.onResume();
    	
    	tvContactHome.setText(MapProject2Activity.placesContactHome.get(positionPlace));
    }

}
