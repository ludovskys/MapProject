package pckg.MapProject2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MyCustomMapView extends MapView {
	 
	private boolean mWasLongClick = false;

	private MyCustomMapView.OnLongpressListener longpressListener;

	public MyCustomMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public MyCustomMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyCustomMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnLongpressListener(MyCustomMapView.OnLongpressListener listener) {
		longpressListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		if(mWasLongClick) {
			mWasLongClick = false;
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	} 

	@SuppressWarnings("deprecation")
	final GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
		public void onLongPress(MotionEvent e) {
			if(longpressListener != null) {
				longpressListener.onLongpress(MyCustomMapView.this,
						getProjection().fromPixels((int) e.getX(), (int) e.getY()));
			}

			mWasLongClick = true;
		}
	});

	public static interface OnLongpressListener {
		public void onLongpress(MapView view, GeoPoint longpressLocation);
	}
}