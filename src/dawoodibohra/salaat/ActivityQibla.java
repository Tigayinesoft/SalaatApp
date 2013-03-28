package dawoodibohra.salaat;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.view.View.OnTouchListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapController;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.view.MotionEvent;

public class ActivityQibla extends MapActivity {
	
	SharedPreferences prefs;
	Editor editor;
	Context context;
	
	ViewCompass compassView;
	MapView mapView;
	MapController mapController;
	GeoPoint geoPoint;
	
	Button satButton;
	Button modeButton;
	Button updateButton;
	Button setlocButton;
	Button compStateButton;
	TextView currentdirtext ;
	TextView qiblatext ;

	SensorManager sensorManager;
	Sensor accelerometer;
	Sensor magField;
	
	List<Float> myList = new LinkedList<Float>();
	protected Time time = new Time();

	float[] aValues = new float[3];
	float[] mValues = new float[3];
	int currentdir ;
	int offset ;
	int magdec ;
	int lat;
	int lng;
	int qibla;
	int mode;
	int mapViewMode;
	int compState;
	private double latitude;
	private double longitude;
	private double tz;
	private float scale=1F;
	private float padding=7F;
	int fontSize;

	private double pi = 3.1415926535897932;
	private double sunAzimuth=360.;
	private double sunAltitude;
	private double moonAzimuth=360.;
	private double moonAltitude;
	
	public static final String SETTING_COMPASS_SENSOR = "SETTING_COMPASS_SENSOR";
	public static final String SETTING_VIEW_MODE = "SETTING_VIEW_MODE";
	public static final String SETTING_MAP_MODE = "SETTING_MAP_MODE";
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
    
    private View.OnClickListener ChangeMode = new View.OnClickListener() {
    	public void onClick(View v) {
            if (mode==0) {
                mapView.setVisibility(View.VISIBLE);
                satButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.VISIBLE);
                setlocButton.setVisibility(View.VISIBLE);
                compassView.setVisibility(View.INVISIBLE);
                currentdirtext.setVisibility(View.INVISIBLE);
                compStateButton.setVisibility(View.INVISIBLE);
                qiblatext.setPadding(0,(int)(scale*padding),0,0);
            	editor.putInt(SETTING_VIEW_MODE, 1);
            	editor.commit();                
                mode=1;
                }
            else {
                mapView.setVisibility(View.INVISIBLE);
                satButton.setVisibility(View.INVISIBLE);
                updateButton.setVisibility(View.INVISIBLE);
                setlocButton.setVisibility(View.INVISIBLE);
                compassView.setVisibility(View.VISIBLE);
                currentdirtext.setVisibility(View.VISIBLE);
                compStateButton.setVisibility(View.VISIBLE);
                qiblatext.setPadding(0,0,0,0);
            	editor.putInt(SETTING_VIEW_MODE, 0);
            	editor.commit();                
                mode=0;
                }
    	}
    };
    
    private View.OnClickListener ChangeSat = new View.OnClickListener() {
    	public void onClick(View v) {
            if (mapViewMode==0) {
            	mapView.setSatellite(true);
            	satButton.setText("View: Satellite"); 
            	editor.putInt(SETTING_MAP_MODE, 1);
            	editor.commit();                
            	mapViewMode=1;
            	}
            else {
                mapView.setSatellite(false);
                satButton.setText("View: Map"); 
            	editor.putInt(SETTING_MAP_MODE, 0);
            	editor.commit();                
                mapViewMode=0;
                }
    	}
    };
    
    private View.OnClickListener CompState = new View.OnClickListener() {
    	public void onClick(View v) {
            if (compState==0) {
            	editor.putInt(SETTING_COMPASS_SENSOR, 1);
            	editor.commit();
            	compStateButton.setText("Sensor: ON"); 
            	compState=1;
            	sensorManager.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
            	sensorManager.registerListener(sensorEventListener,magField,SensorManager.SENSOR_DELAY_FASTEST);     	
            	}
            else {
            	editor.putInt(SETTING_COMPASS_SENSOR, 0);
            	editor.commit();
            	compStateButton.setText("Sensor: OFF"); 
            	compState=0;
                sensorManager.unregisterListener(sensorEventListener);
        		compassView.setBearing(0F);
        		compassView.invalidate();
        		currentdirtext.setText("Align manually");        
            	}
    	}
    };

    private View.OnClickListener UpdatePoint = new View.OnClickListener() {
    	public void onClick(View v) {
        	updateMap();
    	}
    };    
    
    private View.OnClickListener SetLoc = new View.OnClickListener() {
    	public void onClick(View v) {
        	updateMap();
			Geocoder myLocation = new Geocoder(context, Locale.getDefault());
	  	    GeoPoint currentGP = mapView.getMapCenter();
	  	    double currentLat = currentGP.getLatitudeE6()/1000000.;
	  	    double currentLng = currentGP.getLongitudeE6()/1000000.;			
			String city = ActivityLocationSettings.determineCity(myLocation,currentLat,currentLng);
	    	String toasty = "Location updated: " + city + "\nLatitude: " + ActivityNamaaz.truncateTo2(currentLat) + "°, Longitude: " + ActivityNamaaz.truncateTo2(currentLng) + "\nQibla: " + ActivityLocationSettings.determineQibla(currentLat,currentLng) + "° CW from North\nAuto Location Tracking Disabled" ;
	    	Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
	   		toast.show();
	   		Editor editor = prefs.edit();
	   		editor.putInt(ActivityLocationSettings.SETTING_LOC_METHOD, 3);
	   		editor.putBoolean(ActivityLocationSettings.SETTING_AUTOLOC, false);
	    	editor.putString(ActivityLocationSettings.SETTING_CITY, city);
	    	editor.putFloat(ActivityLocationSettings.SETTING_LAT, (float)currentLat);
	    	editor.putFloat(ActivityLocationSettings.SETTING_LNG, (float)currentLng);
	    	editor.putInt(ActivityLocationSettings.SETTING_QIBLA, ActivityLocationSettings.determineQibla(currentLat,currentLng));
	    	editor.putInt(ActivityLocationSettings.SETTING_MAGDEC, ActivityLocationSettings.determineMagDec(currentLat,currentLng));
	    	editor.commit();        	
    	}
    };    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.qibla);
    	context = getApplicationContext();
    	scale = context.getResources().getDisplayMetrics().density;
    	prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	editor = prefs.edit();
    	
    	qiblatext = (TextView) findViewById(R.id.QiblaDir);
    	currentdirtext = (TextView) findViewById(R.id.CurrentDir);
    	compassView = (ViewCompass)findViewById(R.id.CompassID);
    	
    	modeButton = (Button)findViewById(R.id.Mode);
        modeButton.setOnClickListener(ChangeMode);
        satButton = (Button)findViewById(R.id.Sat);
        satButton.setOnClickListener(ChangeSat);
        updateButton = (Button)findViewById(R.id.Update);
        updateButton.setOnClickListener(UpdatePoint);
        setlocButton = (Button)findViewById(R.id.SetLoc);
        setlocButton.setOnClickListener(SetLoc);
    	compStateButton = (Button)findViewById(R.id.CompassState);
        compStateButton.setOnClickListener(CompState);
    	
		mode=prefs.getInt(SETTING_VIEW_MODE, 0);
		mapViewMode=prefs.getInt(SETTING_MAP_MODE, 0);
		compState=prefs.getInt(SETTING_COMPASS_SENSOR, 1);
		
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    mapView.setClickable(true);
	    mapController = mapView.getController();
    }
  
  @Override
  public void onResume() {
  	super.onResume();
  	
	qibla = prefs.getInt(ActivityLocationSettings.SETTING_QIBLA, 0);
	magdec = prefs.getInt(ActivityLocationSettings.SETTING_MAGDEC, 0);
	fontSize = prefs.getInt(ActivitySettings.SETTING_ACTUAL_FONT_SIZE, 16);
	latitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LAT, 0); 
	longitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LNG, 0);
	tz = (double)prefs.getFloat(ActivityLocationSettings.SETTING_TZ, 0);
	
	satButton.setTextSize(fontSize);
	modeButton.setTextSize(fontSize);
	updateButton.setTextSize(fontSize);
	compStateButton.setTextSize(fontSize);
	setlocButton.setTextSize(fontSize);
	qiblatext.setTextSize(fontSize);
	currentdirtext.setTextSize(fontSize);
	
	time.setToNow();

	compassView.qibla = qibla;
	SunCalculator();
	if (sunAltitude > -5)
		compassView.sun = (int) sunAzimuth;
	else
		compassView.sun = 360; // sun go bye bye
	compassView.sunAltitude = (int) sunAltitude;
	MoonCalculator();	
	if (moonAltitude > -5)
		compassView.moon = (int) moonAzimuth;
	else
		compassView.moon = 360; // moon go bye bye
	compassView.moonAltitude = (int) moonAltitude;
		
	sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    updateOrientation(new float[] {0,0,0});
	accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
   	magField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    if (mapViewMode==0) {
        mapView.setSatellite(false);
        satButton.setText("View: Map");}
    else {
    	mapView.setSatellite(true);
    	satButton.setText("View: Satellite");}

    if (compState==0) {
        compStateButton.setText("Sensor: OFF");
        sensorManager.unregisterListener(sensorEventListener);
		compassView.setBearing(0F);
		compassView.invalidate();
		currentdirtext.setText("Align manually");        
    	}
    else {
    	compStateButton.setText("Sensor: ON");
    	sensorManager.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
    	sensorManager.registerListener(sensorEventListener,magField,SensorManager.SENSOR_DELAY_FASTEST);     	
    }

    if (mode==0) {
    	mapView.setVisibility(View.INVISIBLE);
        satButton.setVisibility(View.INVISIBLE);
        satButton.setText("View: Map");
        updateButton.setVisibility(View.INVISIBLE);
        setlocButton.setVisibility(View.INVISIBLE);
        compassView.setVisibility(View.VISIBLE);
        currentdirtext.setVisibility(View.VISIBLE);
        compStateButton.setVisibility(View.VISIBLE);
        qiblatext.setPadding(0,0,0,0);    	
    }
    else if (mode==1) {
        mapView.setVisibility(View.VISIBLE);
        satButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.VISIBLE);
        setlocButton.setVisibility(View.VISIBLE);
        compassView.setVisibility(View.INVISIBLE);
        currentdirtext.setVisibility(View.INVISIBLE);
        compStateButton.setVisibility(View.INVISIBLE);
        qiblatext.setPadding(0,(int)(scale*padding),0,0);
    }
    
    geoPoint = new GeoPoint ((int)(latitude*1E6),(int)(longitude*1E6));
    mapController.setCenter(geoPoint);
    mapController.setZoom(18);
    updateMap();
}
  
  	private void updateMap() {
  	    GeoPoint currentGP = mapView.getMapCenter();
  	    double currentLat = currentGP.getLatitudeE6()/1000000.;
  	    double currentLng = currentGP.getLongitudeE6()/1000000.;
  	    qibla = ActivityLocationSettings.determineQibla(currentLat, currentLng); 
  		qiblatext.setText("Qibla Bearing: " + qibla  + "°");
  	    
  		List<Overlay> mapOverlays = mapView.getOverlays();
  	    while (mapOverlays.size()>0) 
  	    	mapOverlays.remove(0);
  	    Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.qibla_dir);
  	    int width = bitmapOrg.getWidth();
  	    int height = bitmapOrg.getHeight();
  	    Matrix matrix = new Matrix();
  	    matrix.postRotate((float)qibla);
  	    Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);
  	    BitmapDrawable drawable = new BitmapDrawable(resizedBitmap);
  	    //Drawable drawable = this.getResources().getDrawable(R.drawable.icon_small);
  	    CustomItemizedOverlay itemizedoverlay = new CustomItemizedOverlay(drawable,context);
  	    OverlayItem overlayitem = new OverlayItem(currentGP, "Qibla", "Qibla");
  	    itemizedoverlay.addOverlay(overlayitem);
  	    mapOverlays.add(itemizedoverlay);
  	    mapView.invalidate();
  	}

	private void updateOrientation(float[] values) {
    	int avgsize = 25;
    	
    	if (values[0] != 0)
    		myList.add(values[0]);
    	
    	if (myList.size() > avgsize)
    		myList.remove(0);
    	
    	currentdir = (int)average() ;
    	
    	Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
    	int currentOrientation = display.getOrientation(); 
    	if (currentOrientation == 0)
    		offset = 0 ;
    	else if (currentOrientation == 1)
    		offset = 90 ;
    	else if (currentOrientation == 3)
    		offset = 270 ;
    	
    	currentdir = currentdir+offset;
    	currentdir = currentdir+magdec;
    	
    	currentdir = currentdir % 360 ;
    	if (currentdir < 0)
    		currentdir = currentdir + 360;

    	if (compassView != null) {
    		compassView.setBearing((float)currentdir);
    		compassView.invalidate();
    	}
    	    	
    	if (currentdirtext != null)
    		currentdirtext.setText("Current Bearing: " + currentdir + "°");
    	
    	if (compState==0) {
    		compassView.setBearing(0F);
    		compassView.invalidate();
    		currentdirtext.setText("Align manually");
    	}
    }
    
    private float average() {
	    float sum = 0 ;
	    float currentAvg = 0 ;
	    float x;
	    
	    for (int i=0; i<myList.size(); i++) {
	    	x = myList.get(i);
	    	if (i > 0) {
	    		currentAvg = sum/(float)i;
	    		if (x-currentAvg>180)
	    			x = x - 360;
	    		else if (x-currentAvg<-180)
	    			x = x + 360;
	    	}
    		sum = sum + x;
	    }
	    sum = sum / (float)myList.size();
	    return sum;
    }
    
    private float[] calculateOrientation() {
    	float[] values = new float[3];
    	float[] R = new float[9];
    	
    	SensorManager.getRotationMatrix(R, null, aValues, mValues);
    	SensorManager.getOrientation(R, values);
    	
    	values[0] = (float) Math.toDegrees(values[0]);
    	values[1] = (float) Math.toDegrees(values[1]);
    	values[2] = (float) Math.toDegrees(values[2]);
    	
    	return values;
    }
    
    private void SunCalculator() {
    	int day = time.monthDay ;
    	int month = time.month+1 ;
    	int year = time.year ;
    	double UT = ((time.hour + time.minute/60. + time.second/3600.)-tz);
    	
    	double Y = year-1900.;
    	double ZJ=0;
    	switch (month) {
    	case 1: ZJ=-0.5; break;
    	case 2: ZJ=30.5; break;
    	case 3: ZJ=58.5; break;
    	case 4: ZJ=89.5; break;
    	case 5: ZJ=119.5; break;
    	case 6: ZJ=150.5; break;
    	case 7: ZJ=180.5; break;
    	case 8: ZJ=211.5; break;
    	case 9: ZJ=242.5; break;
    	case 10: ZJ=272.5; break;
    	case 11: ZJ=303.5; break;
    	case 12: ZJ=333.5; break;
    	}
    	
    	if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) // leap
    		if (month == 1 || month == 2)
    			ZJ=ZJ-1;
    	
    	double D = (int)(365.25*Y) + ZJ + day + UT/24.;
    	double T = D/36525.;
    	double L = 279.697 + 36000.769*T;
    	L = L%360.;
    	double M = 358.476 + 35999.050*T;
    	M = M%360.;
    	double epsilon = 23.452 - 0.013*T;
    	double lambda = L + (1.919 - 0.005*T) * Math.sin(M*pi/180.) + 0.02 * Math.sin(2.*M*pi/180.); 
    	double alpha = Math.atan(Math.tan(lambda*pi/180.)*Math.cos(epsilon*pi/180.))*180./pi;
    	if (alpha < 0)
    		alpha = alpha + 180;
    	if ((alpha - lambda) > 90)
    		alpha = alpha - 180;
    	else if ((alpha - lambda) < -90)
    		alpha = alpha + 180;
    	double delta = Math.asin(Math.sin(lambda*pi/180.)*Math.sin(epsilon*pi/180.))*180./pi;
    	double HA = L - alpha + 180 + 15 * UT + longitude;
    	sunAltitude = Math.asin(Math.sin(latitude*pi/180.)*Math.sin(delta*pi/180.)+Math.cos(latitude*pi/180.)*Math.cos(delta*pi/180.)*Math.cos(HA*pi/180.))*180./pi;
    	sunAzimuth = Math.atan2(Math.sin(HA*pi/180.),(Math.cos(HA*pi/180.)*Math.sin(latitude*pi/180.)-Math.tan(delta*pi/180.)*Math.cos(latitude*pi/180.)))*180./pi;
    	sunAzimuth = sunAzimuth+180.;
    	sunAzimuth = sunAzimuth % 360.;
    }
    
    private void MoonCalculator() {
    	int day = time.monthDay ;
    	int month = time.month+1 ;
    	int year = time.year ;
    	double UT = ((time.hour + time.minute/60. + time.second/3600.)-tz);
    	double d = 367*year - 7 * ( year + (month+9)/12 ) / 4 + 275*month/9 + day - 730530 + UT/24.;
    	
        double N = 125.1228 - 0.0529538083 * d ;
        double i = 5.1454 ;
        double w = 318.0634 + 0.1643573223 * d ;
        double a = 60.2666 ; // (Earth radii)
        double e = 0.054900 ;
        double M = 115.3654 + 13.0649929509 * d ;
        
        double ecl = 23.4393 - 3.563E-7 * d ;

        double E = M + e*(180./pi) * Math.sin(M*pi/180.) * ( 1.0 + e * Math.cos(M*pi/180.) ) ;
        E = E - ( E - e*(180./pi)*Math.sin(E*pi/180.) - M ) / ( 1 - e * Math.cos(E*pi/180.) );
        double xv =  ( Math.cos(E*pi/180.) - e );
        double yv =  ( Math.sqrt(1.0 - e*e) * Math.sin(E*pi/180.) );
        double v = Math.atan2(yv,xv)*180./pi;
        double xh = ( Math.cos(N*pi/180.) * Math.cos((v+w)*pi/180.) - Math.sin(N*pi/180.) * Math.sin((v+w)*pi/180.) * Math.cos(i*pi/180.) );
        double yh = ( Math.sin(N*pi/180.) * Math.cos((v+w)*pi/180.) + Math.cos(N*pi/180.) * Math.sin((v+w)*pi/180.) * Math.cos(i*pi/180.) );
        double zh = ( Math.sin((v+w)*pi/180.) * Math.sin(i*pi/180.) );

        double xe = xh ;
        double ye = yh * Math.cos(ecl*pi/180.) - zh * Math.sin(ecl*pi/180.);
        double ze = yh * Math.sin(ecl*pi/180.) + zh * Math.cos(ecl*pi/180.);
        
    	double RA = Math.atan2(ye,xe)*180./pi;
    	double Dec = Math.atan2(ze,Math.sqrt(xe*xe+ye*ye))*180./pi;
    	double LST = 100.46 + 0.985647 * d + longitude + 15.*UT;
    	double HA = LST - RA;
    	
    	moonAltitude = Math.asin(Math.sin(Dec*pi/180.)*Math.sin(latitude*pi/180.)+Math.cos(Dec*pi/180.)*Math.cos(latitude*pi/180.)*Math.cos(HA*pi/180.))*180./pi;
    	moonAzimuth = Math.acos((Math.sin(Dec*pi/180.)-Math.sin(moonAltitude*pi/180.)*Math.sin(latitude*pi/180.))/(Math.cos(moonAltitude*pi/180.)*Math.cos(latitude*pi/180.)))*180./pi;
    	if (Math.sin(HA*pi/180.)>0)
    		moonAzimuth = 360 - moonAzimuth;
		moonAzimuth = moonAzimuth%360.;		  	
    }
    
    @Override
    protected void onPause() {
    	sensorManager.unregisterListener(sensorEventListener);
    	super.onPause();
    }
    
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
    	public void onSensorChanged(SensorEvent event) {
    		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
    			aValues = event.values;
    		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
    			mValues = event.values;
    		
    		updateOrientation(calculateOrientation());
    	}
    	
    	public void onAccuracyChanged (Sensor sensor, int accuracy) {}
    };

}