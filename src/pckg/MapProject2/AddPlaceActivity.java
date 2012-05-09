package pckg.MapProject2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class AddPlaceActivity extends Activity 
{
	
	private ListView listViewChoices;
	private ArrayList<String> listChoices;
	private List<Address> listAd;
	 
	private Context mContext;
	
	private String placeName, placeAddress;
	
	private Button bAdd;
	private TextView tvError;
	
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private int compteurPlaces;
	
	public static final String PREFS_PLACES = "MyPlacesFile15";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplace);
        
        mContext = getApplicationContext(); 
        
        listViewChoices = (ListView)findViewById(R.id.listviewchoices); 
        bAdd = (Button) findViewById(R.id.confirmplace);
        tvError = (TextView) findViewById(R.id.labelerror);
        
        settings = getSharedPreferences(PREFS_PLACES, 0);
        editor = settings.edit();
        
        compteurPlaces = settings.getInt("counterPlaces", 0);
    
        bAdd.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				EditText etName = (EditText) findViewById(R.id.entryname);
				EditText etAddr = (EditText) findViewById(R.id.entryadress);
				
				placeName = etName.getText().toString();
				placeAddress = etAddr.getText().toString();
				
				// if one or several textfields are empty
				if (placeName.equals("") || placeAddress.equals(""))
				{
					tvError.setText(R.string.fieldserror);
				}
				else
				{
					Geocoder gc = new Geocoder(getApplicationContext());
					
					try 
					{
						// getFromLocationName returns a list of address (size of 3) corresponding to the given address
						listAd = gc.getFromLocationName(placeAddress,3);
						
						// if no address was found
						if (listAd.isEmpty())
						{
							tvError.setText(R.string.coornotfound);
							
							listViewChoices.setAdapter(null);
						}
						else
						{
							// if several addresses were found
							if (listAd.size() > 1 )
							{
								listChoices = new ArrayList<String>();
								
								for (int i = 0; i < listAd.size(); i++ )
								{
									 listChoices.add("Country: " +listAd.get(i).getCountryName() + ", Locality: " + 
												listAd.get(i).getLocality());
									 
								}
					            
								// displaying of the choices
					      	  	ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext, 
					      	  			android.R.layout.simple_list_item_1, listChoices);
					            
					      	  	listViewChoices.setAdapter(listAdapter);
					      	  	
					      	  	tvError.setText(R.string.severalloc);
							}
							// if just one address was found
							else
							{
								compteurPlaces = settings.getInt("counterPlaces", 0);
								
								MapProject2Activity.placesName.add(placeName);
								
								placeAddress = listAd.get(0).getAddressLine(0) + " " +
										listAd.get(0).getAddressLine(1);
							
								MapProject2Activity.placesStreet.add(placeAddress);
								
								MapProject2Activity.placesCoorLong.add(listAd.get(0).getLongitude());
								
								MapProject2Activity.placesCoorLat.add(listAd.get(0).getLatitude());
								
								MapProject2Activity.placesNumber.add(compteurPlaces);
								
								MapProject2Activity.placesContactHome.add(null);
								
								// update of the list of places
								MapProject2Activity.listAdapter.notifyDataSetChanged();
								
								// save on user preferences
								editor.putString("name"+Integer.toString(compteurPlaces), placeName);
								editor.putString("address"+Integer.toString(compteurPlaces), placeAddress);
								
								editor.putString("long"+Integer.toString(compteurPlaces), 
										Double.toString(listAd.get(0).getLongitude()));
								
								editor.putString("lat"+Integer.toString(compteurPlaces), 
										Double.toString(listAd.get(0).getLatitude()));
								   
								editor.putInt("number"+Integer.toString(compteurPlaces), 
										compteurPlaces); 
								
								editor.putString("contact"+Integer.toString(compteurPlaces), 
										"");
								   
								compteurPlaces++;
								
								editor.putInt("counterPlaces", compteurPlaces);
					            editor.commit();
								
					            // we return to the previous view
								finish();
							} 
							
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} // end if (placeName.equals("") || placeAddress.equals("")) else

			} // end public void onClick(View v)

        }); // end bAdd.setOnClickListener(new OnClickListener()
        
        listViewChoices.setOnItemClickListener(new OnItemClickListener()
        {
			@Override
			public void onItemClick(AdapterView<?> list, View v,
					int pos, long id) 
			{
				compteurPlaces = settings.getInt("counterPlaces", 0);
				
				MapProject2Activity.placesName.add(placeName);
				
				placeAddress = listAd.get(pos).getAddressLine(0) + " " +
						listAd.get(pos).getAddressLine(1);
				
				MapProject2Activity.placesStreet.add(placeAddress);
				
				MapProject2Activity.placesCoorLong.add(listAd.get(pos).getLongitude());
				
				MapProject2Activity.placesCoorLat.add(listAd.get(pos).getLatitude());
				
				MapProject2Activity.placesNumber.add(compteurPlaces);
				
				MapProject2Activity.placesContactHome.add(null);
				
				// save on user preferences
				editor.putString("name"+Integer.toString(compteurPlaces), placeName);
				editor.putString("address"+Integer.toString(compteurPlaces), 
						placeAddress); 
				
				editor.putString("long"+Integer.toString(compteurPlaces), 
						Double.toString(listAd.get(pos).getLongitude()));
				
				editor.putString("lat"+Integer.toString(compteurPlaces), 
						Double.toString(listAd.get(pos).getLatitude()));
				
				editor.putInt("number"+Integer.toString(compteurPlaces), 
						compteurPlaces);
				
				editor.putString("contact"+Integer.toString(compteurPlaces), 
						"");
				
				compteurPlaces++;
				
				editor.putInt("counterPlaces", compteurPlaces);
	            editor.commit();
				
				MapProject2Activity.listAdapter.notifyDataSetChanged();
				
				finish();

			}
        	
        }); // end listViewChoices.setOnItemClickListener(new OnItemClickListener()
        
	} // end public void onCreate(Bundle savedInstanceState)

} // end public class AddPlaceActivity extends Activity 
