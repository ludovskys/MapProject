package pckg.MapProject2;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

@SuppressWarnings("deprecation")
public class ListContactsActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.contactlist);

 

    	Cursor cursor = getContentResolver().query(People.CONTENT_URI, new String[] {People._ID, People.NAME, People.NUMBER}, null, null, null);
    	startManagingCursor(cursor);

    	// the desired columns to be bound
    	String[] columns = new String[] { People.NAME, People.NUMBER };
    	// the XML defined views which the data will be bound to
    	int[] to = new int[] { R.id.name_entry, R.id.number_entry };

    	// create the adapter using the cursor pointing to the desired data as well as the layout information
    	SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.contact_entry, cursor, columns, to);

    	// set this adapter as your ListActivity's adapter
    	this.setListAdapter(mAdapter);
    }
     
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
    	Cursor c = (Cursor) this.getListAdapter().getItem(position);
        
        MapProject2Activity.placesContactHome.set(ModifyPlaceActivity.positionPlace,c.getString(1)); 

    	finish();
    }
    
    
}


