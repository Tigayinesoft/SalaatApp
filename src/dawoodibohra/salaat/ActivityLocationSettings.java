package dawoodibohra.salaat;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import android.hardware.GeomagneticField;

/* Location Settings Activity */
public class ActivityLocationSettings extends Activity implements LocationListener{

	private Spinner locmethodSpinner;
	private Spinner TZmethodSpinner;
	private Spinner TZSpinner;
	private String errorMessage;

	public static final String SETTING_LOC_METHOD = "SETTING_LOC_METHOD";
	public static final String SETTING_TZ_METHOD = "SETTING_TZ_METHOD";
	public static final String SETTING_CITY = "SETTING_CITY";
	public static final String SETTING_LAT = "SETTING_LAT";
	public static final String SETTING_LNG = "SETTING_LNG";
	public static final String SETTING_QIBLA = "SETTING_QIBLA";
	public static final String SETTING_MAGDEC = "SETTING_MAGDEC";
	public static final String SETTING_TZ = "SETTING_TZ";
	public static final String SETTING_AUTOLOC = "SETTING_AUTOLOC";
	public static final String SETTING_AUTOTZ = "SETTING_AUTOTZ";
	
	SharedPreferences prefs;
	
	EditText latEditText ;
	EditText lngEditText ;
	EditText locEditText ;
	
	TextView latTextView ;
	TextView lngTextView ;
	TextView locTextView ;
	TextView locNoteTextView ;
	TextView TZTextView ;
	TextView GPSNoteTextView ;
	
	CheckBox AutoLoc;
	CheckBox AutoTZ;
	
	private LocationManager myManager;
	double gpsLat = 0;
	double gpsLng = 0;
	int gpsLock = 1;
	public static final double LatLngAccuracy = 0.1;
	
    @Override
    protected void onDestroy() {
    	stopListening();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        stopListening();
        super.onStop();
    }
    
    @Override
    protected void onPause() {
        stopListening();
        super.onPause();
    }

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_settings);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
    	Context context = getApplicationContext();

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
    	double latitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LAT, 0); 
    	double longitude = (double)prefs.getFloat(ActivityLocationSettings.SETTING_LNG, 0);
    	double tz = (double)prefs.getFloat(ActivityLocationSettings.SETTING_TZ, 0);
    	int magdec = prefs.getInt(ActivityLocationSettings.SETTING_MAGDEC, 0); 
    	
    	String temp;
    	
    	TextView text;
    	text = (TextView) findViewById(R.id.Label);
    	
    	temp = "Location: " + prefs.getString(ActivityLocationSettings.SETTING_CITY, "");
    	text = (TextView) findViewById(R.id.PlaceInfo);
    	text.setText(temp);
    	
    	temp = "Latitude: " + ActivityNamaaz.truncateTo2(latitude) + "°, Longitude: " + ActivityNamaaz.truncateTo2(longitude) + "°";
    	text = (TextView) findViewById(R.id.PlaceInfo2);
    	text.setText(temp);
    	
    	temp = "Timezone (UTC): " + tz ;
    	text = (TextView) findViewById(R.id.Timezone);
    	text.setText(temp);
    	
    	temp = "Magnetic Declination: " + magdec + "°" ;
    	text = (TextView) findViewById(R.id.MagDec);
    	text.setText(temp);
    	
        myManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        locmethodSpinner = (Spinner)findViewById(R.id.SpinnerLocMethod);
        TZmethodSpinner = (Spinner)findViewById(R.id.SpinnerTZMethod);
        TZSpinner = (Spinner)findViewById(R.id.SpinnerTZ);
        
        latEditText = (EditText)findViewById(R.id.lat);
        lngEditText = (EditText)findViewById(R.id.lng);
        locEditText = (EditText)findViewById(R.id.loc);
        latTextView = (TextView)findViewById(R.id.latTextView);
        lngTextView = (TextView)findViewById(R.id.lngTextView);
        locTextView = (TextView)findViewById(R.id.locTextView);
        locNoteTextView = (TextView)findViewById(R.id.locTextViewNote);
        TZTextView = (TextView)findViewById(R.id.TZTextView);
        GPSNoteTextView = (TextView)findViewById(R.id.GPSNote);
        
        AutoLoc = (CheckBox)findViewById(R.id.AutoTrackLoc);
        AutoTZ = (CheckBox)findViewById(R.id.AutoTrackTZ);

        populateSpinners() ;
        
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        updateUIFromPreferences();
        
        locmethodSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	if (position == 0 || position == 1 || position == 2)
            		AutoLoc.setVisibility(View.VISIBLE);
            	else
            		AutoLoc.setVisibility(View.GONE);
            	
            	if (position == 2) {
                    gpsLock = 0;
                    startListening();
                    GPSNoteTextView.setText("Now obtaining GPS fix... Please wait until GPS status icon becomes stable (i.e. stops blinking) before pressing 'Update'.");
                    GPSNoteTextView.setVisibility(View.VISIBLE);
        		}
            	else {
            		stopListening();
            		GPSNoteTextView.setVisibility(View.GONE);
        		}
            		
                if (position == 3) {
                	latEditText.setVisibility(View.VISIBLE);
                	latTextView.setVisibility(View.VISIBLE);
                	lngEditText.setVisibility(View.VISIBLE);
                	lngTextView.setVisibility(View.VISIBLE);	
                }
                else {
                	latEditText.setVisibility(View.GONE);
                	latTextView.setVisibility(View.GONE);
                	lngEditText.setVisibility(View.GONE);
                	lngTextView.setVisibility(View.GONE);	
                }
                
                if (position == 4) {
                	locEditText.setVisibility(View.VISIBLE);
                	locTextView.setVisibility(View.VISIBLE);
                	locNoteTextView.setVisibility(View.VISIBLE);
                }
                else {
                	locEditText.setVisibility(View.GONE);
                	locTextView.setVisibility(View.GONE);
                	locNoteTextView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            	latEditText.setVisibility(View.GONE);
            	latTextView.setVisibility(View.GONE);
            	lngEditText.setVisibility(View.GONE);
            	lngTextView.setVisibility(View.GONE);
            	locEditText.setVisibility(View.GONE);
            	locTextView.setVisibility(View.GONE);
            	AutoLoc.setVisibility(View.GONE);
            }
        });
        
        TZmethodSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1) {
                	TZSpinner.setVisibility(View.VISIBLE);
                	TZTextView.setVisibility(View.VISIBLE);
                	AutoTZ.setVisibility(View.GONE);
                }
                else {
                	TZSpinner.setVisibility(View.GONE);
                	TZTextView.setVisibility(View.GONE);
                	AutoTZ.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            	TZSpinner.setVisibility(View.GONE);
            	TZTextView.setVisibility(View.GONE);
            	AutoTZ.setVisibility(View.GONE);
            }
        });

        Button saveButton = (Button)findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
				if (savePreferences()) {
					finish();
				}
				else {
					Context context = getApplicationContext();
			    	Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
			    	toast.show();
				}
			}
		});
        
		if (!prefs.contains(SETTING_LAT) || !prefs.contains(SETTING_LNG) || !prefs.contains(SETTING_TZ) || !prefs.contains(SETTING_QIBLA) || !prefs.contains(SETTING_CITY)) {
		  	String toasty = "If you are unsure on how to configure your location, simply press 'Update' to continue." ;
	    	Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
	   		toast.show();
		}

    }
    
    public void populateSpinners() {
    	ArrayAdapter<CharSequence> fAdapter;
    	int spinner_dd_item;
    	
    	fAdapter = ArrayAdapter.createFromResource(this, R.array.locmethod_options,
    			                                  android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
    	fAdapter.setDropDownViewResource(spinner_dd_item);
    	locmethodSpinner.setAdapter(fAdapter);
    	
    	fAdapter = ArrayAdapter.createFromResource(this, R.array.tz_options,
    			                                  android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
    	fAdapter.setDropDownViewResource(spinner_dd_item);
    	TZSpinner.setAdapter(fAdapter);
    	
    	fAdapter = ArrayAdapter.createFromResource(this, R.array.tzmethod_options,
		                android.R.layout.simple_spinner_item);
		spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		TZmethodSpinner.setAdapter(fAdapter);
    	}

    public void updateUIFromPreferences() {
    	int locmethodIndex = prefs.getInt(SETTING_LOC_METHOD, 0);
    	locmethodSpinner.setSelection(locmethodIndex);

    	int TZmethodIndex = prefs.getInt(SETTING_TZ_METHOD, 0);
    	TZmethodSpinner.setSelection(TZmethodIndex);
    	
    	String s = Double.toString(ActivityNamaaz.truncateTo2((double)prefs.getFloat(SETTING_LAT, 0)));
    	latEditText.setText(s); 
    	s = Double.toString(ActivityNamaaz.truncateTo2((double)prefs.getFloat(SETTING_LNG, 0)));
    	lngEditText.setText(s);
    	s = prefs.getString(SETTING_CITY, "");
    	locEditText.setText(s);
    	
    	double tz = (double)prefs.getFloat(SETTING_TZ, 0);
    	int TZIndex = getTZIndex(tz);
    	TZSpinner.setSelection(TZIndex);
    	
    	AutoLoc.setChecked(prefs.getBoolean(SETTING_AUTOLOC, true));
    	AutoTZ.setChecked(prefs.getBoolean(SETTING_AUTOTZ, true));
    	
    }

    public boolean savePreferences() {

    	int locmethodIndex = locmethodSpinner.getSelectedItemPosition();
    	int TZmethodIndex = TZmethodSpinner.getSelectedItemPosition();
    	int TZindex = TZSpinner.getSelectedItemPosition();
    	boolean AutoLocBool = AutoLoc.isChecked();
    	boolean AutoTZBool = AutoTZ.isChecked();
    	
    	double lat = 0;
    	double lng = 0;
    	double tz = 0;
    	Time time = new Time() ;
    	time.setToNow();
    	
    	switch (locmethodIndex) {
    	
    	case 0: // Use last Phone fix
        	if(myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) { 
        		lat = myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude(); 
        		lng = myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude(); 
        	}
        	else {
       			errorMessage = "Could not find last Network fix information. Make sure Network connectivity is present and try again.";
           		return false;
           	}
        	break;

    	case 1: // Use last GPS fix
        	if(myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) { 
        		lat = myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(); 
        		lng = myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude(); 
        	}
        	else {
       			errorMessage = "Could not find last GPS fix information. Try obtaining new GPS lock.";
           		return false;
           	}
        	break;

    	case 2: // Obtain new GPS fix
        	if (gpsLock == 1 && myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) { 
        		lat = gpsLat; 
        		lng = gpsLng; 
        	}
        	else {
       			errorMessage = "Could not obtain GPS fix. Please wait further until a GPS fix is obtained. Also, make sure GPS usage is enabled in the device settings.";
           		return false;
           	}
        	break;

    	case 3: // Manual lat/lng
    		Editable ed = latEditText.getText();
       		lat = Double.parseDouble(ed.toString());
       		ed = lngEditText.getText();
       		lng = Double.parseDouble(ed.toString());
       		if (lat > 90 || lat < -90 || lng > 180 || lng < -180) {
       			errorMessage = "Incorrect latitude/longitude settings.";
       			return false;
       		}
       		AutoLocBool = false;
        	break;

    	case 4: // Location
    		Editable ed2 = locEditText.getText();
    		Geocoder fwdGeocoder = new Geocoder(this,Locale.getDefault());
    		List<Address> locations=null;
    		try {
    			locations = fwdGeocoder.getFromLocationName(ed2.toString(),1);
    		} catch(Exception e){
    			errorMessage = "Could not find location. Check to see if active Internet connection is present.";
    			return false;
    		}

    		if (!(locations.isEmpty()) && locations != null) {
        		Address add = locations.get(0);
        		lat = add.getLatitude();
        		lng = add.getLongitude();
    		}
    		else {
    			errorMessage = "Could not find location";
    			return false;
    		}
    		AutoLocBool = false;
    		break;
    	}
    	
    	switch (TZmethodIndex) {
    	case 0 :
           	tz = ((double) time.gmtoff)/3600;
            break;
    	case 1 :
       		tz = getTZ(TZindex);
       		AutoTZBool = false;
       		break ;
    	}
    	
    	Geocoder myLocation = new Geocoder(this, Locale.getDefault());
    	String city = determineCity(myLocation,lat,lng);
   		
    	String toasty = "Location: " + city + "\nLatitude: " + ActivityNamaaz.truncateTo2(lat) + "°, Longitude: " + ActivityNamaaz.truncateTo2(lng) + "°\nTimezone (UTC): " + tz + "\nQibla: " + determineQibla(lat,lng) + "° CW from North" ;
    	Context context = getApplicationContext();
    	Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
   		toast.show();

    	Editor editor = prefs.edit();
    	editor.putInt(SETTING_LOC_METHOD, locmethodIndex);
    	editor.putInt(SETTING_TZ_METHOD, TZmethodIndex);
    	editor.putString(SETTING_CITY, city);
    	editor.putFloat(SETTING_LAT, (float)lat);
    	editor.putFloat(SETTING_LNG, (float)lng);
    	editor.putFloat(SETTING_TZ, (float)tz);
    	editor.putInt(SETTING_QIBLA, determineQibla(lat,lng));
    	editor.putInt(SETTING_MAGDEC, determineMagDec(lat,lng));
    	editor.putBoolean(SETTING_AUTOLOC, AutoLocBool);
    	editor.putBoolean(SETTING_AUTOTZ, AutoTZBool);
    	editor.commit();
    	
    	return true;
    }
    
    public static int determineQibla(double lat, double lng){
    	double latk = 21.42252;
    	double lngk = 39.82621;
    	double pi = 3.1415 ;
    	
    	double qibladir = Math.atan2(Math.sin((lngk-lng)*pi/180.),(Math.cos(lat*pi/180.)*Math.tan(latk*pi/180.)-Math.sin(lat*pi/180.)*Math.cos((lngk-lng)*pi/180.)))*180/pi;
    	if (qibladir < 0)
    		qibladir = qibladir + 360.;
    	return (int)Math.round(qibladir);
    }
    
    public static int determineMagDec(double lat, double lng){
    	Time time = new Time();
    	time.setToNow();
    	GeomagneticField geomag = new GeomagneticField((float)lat, (float)lng, (float)0, time.toMillis(false));
    	
    	return (int)geomag.getDeclination();
    }
    
    public static String determineCity(Geocoder myLocation, double lat, double lng) {
    
    	List<Address> myList = null ;
    	String city = "Unknown";
    	
    	try {
			myList = myLocation.getFromLocation(lat, lng, 1);
    	} 
    	catch (Exception e) {
    		return city ;
    	}

    	if (!(myList.isEmpty()) && myList != null) {
	    	Address add = myList.get(0);
	    	
	    	if (add.getAddressLine(2) != null)
	    		city = add.getAddressLine(1) + ", " + add.getAddressLine(2);
	    	else if (add.getAddressLine(1) != null)
	    		city = add.getAddressLine(0) + ", " + add.getAddressLine(1);
	    	else if (add.getAddressLine(0) != null)
	    		city = add.getAddressLine(0) ;
    	}

    	return city;
    }
    
    public static boolean getDefaultSettings(LocationManager myManager, SharedPreferences prefs, Context context) {
    	if(myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
    		double lat = myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
    		double lng = myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
		
	    	Time time = new Time() ;
	    	time.setToNow();
	    	double tz = ((double) time.gmtoff)/3600;
	    	
	    	Geocoder myLocation = new Geocoder(context, Locale.getDefault());
	    	String city = determineCity(myLocation,lat,lng);
	    	
	    	String toasty = "Location: " + city + "\nLatitude: " + ActivityNamaaz.truncateTo2(lat) + "°, Longitude: " + ActivityNamaaz.truncateTo2(lng) + "°\nTimezone (UTC): " + tz + "\nQibla: " + ActivityLocationSettings.determineQibla(lat,lng) + "° CW from North" ;
	    	Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
	   		toast.show();
	   		int locmethodIndex=0;
	   		int TZmethodIndex=0;
	    	Editor editor = prefs.edit();
	    	editor.putInt(SETTING_LOC_METHOD, locmethodIndex);
	    	editor.putInt(SETTING_TZ_METHOD, TZmethodIndex);
	    	editor.putString(SETTING_CITY, city);
	    	editor.putFloat(SETTING_LAT, (float)lat);
	    	editor.putFloat(SETTING_LNG, (float)lng);
	    	editor.putFloat(SETTING_TZ, (float)tz);
	    	editor.putInt(SETTING_QIBLA, determineQibla(lat,lng));
	    	editor.putInt(SETTING_MAGDEC, determineMagDec(lat,lng));
	    	editor.commit();
	    	
	    	return true;
    	}
    	else
    		return false;
    }
    
    public static void LocationUpdater (LocationManager myManager, SharedPreferences prefs, Context context) {
		if (prefs.getBoolean(SETTING_AUTOLOC, true)) {
			double currentLat = (double)prefs.getFloat(SETTING_LAT, 0);
			double currentLng = (double)prefs.getFloat(SETTING_LNG, 0);
			
			double realLat = currentLat;
			double realLng = currentLng;
			
			if (prefs.getInt(SETTING_LOC_METHOD, 0) == 0 && myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
	    		realLat = myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude(); 
	    		realLng = myManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
			}
			
			if ((prefs.getInt(SETTING_LOC_METHOD, 0) == 1 || prefs.getInt(SETTING_LOC_METHOD, 0) == 2) && myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
	        	realLat = myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(); 
	        	realLng = myManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();  
			}		
			
			if (Math.abs(realLat - currentLat) > LatLngAccuracy ||  Math.abs(realLng - currentLng) > LatLngAccuracy) {
				double lat = realLat;
				double lng = realLng;
				
				Geocoder myLocation = new Geocoder(context, Locale.getDefault());
				String city = determineCity(myLocation,lat,lng);
		    	
		    	String toasty = "Namaaz Times" + "\nLocation updated: " + city + "\nLatitude: " + ActivityNamaaz.truncateTo2(lat) + "°, Longitude: " + ActivityNamaaz.truncateTo2(lng) + "\nQibla: " + ActivityLocationSettings.determineQibla(lat,lng) + "° CW from North" ;
		    	Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
		   		toast.show();
		   		
		   		Editor editor = prefs.edit();
		    	editor.putString(SETTING_CITY, city);
		    	editor.putFloat(SETTING_LAT, (float)lat);
		    	editor.putFloat(SETTING_LNG, (float)lng);
		    	editor.putInt(SETTING_QIBLA, determineQibla(lat,lng));
		    	editor.putInt(SETTING_MAGDEC, determineMagDec(lat,lng));
		    	editor.commit();
	    	}
		}
    }
    
    public static void TimeZoneUpdater (SharedPreferences prefs, Context context) {
    	if (prefs.getInt(SETTING_TZ_METHOD, 0) == 0 && prefs.getBoolean(SETTING_AUTOTZ, true)) {
    		double currentTz = (double)prefs.getFloat(SETTING_TZ, 0);
			Time timeForTz = new Time();
	    	timeForTz.setToNow();
	    	double realTz = (double) (timeForTz.gmtoff)/3600 ; 
	    	if (realTz != currentTz) {
	        	Editor editor = prefs.edit();
		    	editor.putFloat(SETTING_TZ, (float)realTz);
		    	editor.commit();
		    	String toasty = "Namaaz Times" + "\nTime zone updated" ;
		    	Toast toast = Toast.makeText(context, toasty, Toast.LENGTH_LONG);
		   		toast.show();
	    	}
		}
    }
    
    private static int getTZIndex (double tz) {
    	int index = 0;
    	
    	if (tz ==-12) index = 0;
    	else if (tz ==-11) index = 1;
    	else if (tz ==-10.5) index = 2;
    	else if (tz ==-10) index = 3;
    	else if (tz ==-9.5) index = 4;
    	else if (tz ==-9) index = 5;
    	else if (tz ==-8.5) index = 6;
    	else if (tz ==-8) index = 7;
    	else if (tz ==-7) index = 8;
    	else if (tz ==-6) index = 9;
    	else if (tz ==-5) index = 10;
    	else if (tz ==-4.5) index = 11;
    	else if (tz ==-4) index = 12;
    	else if (tz ==-3.5) index = 13;
    	else if (tz ==-3) index = 14;
    	else if (tz ==-2.5) index = 15;
    	else if (tz ==-2) index = 16;
    	else if (tz ==-1) index = 17;
    	else if (tz ==0) index = 18;
    	else if (tz ==1) index = 19;
    	else if (tz ==2) index = 20;
    	else if (tz ==3) index = 21;
    	else if (tz ==3.5) index = 22;
    	else if (tz ==4) index = 23;
    	else if (tz ==4.5) index = 24;
    	else if (tz ==5) index = 25;
    	else if (tz ==5.5) index = 26;
    	else if (tz ==5.75) index = 27;
    	else if (tz ==6) index = 28;
    	else if (tz ==6.5) index = 29;
    	else if (tz ==7) index = 30;
    	else if (tz ==8) index = 31;
    	else if (tz ==8.75) index = 32;
    	else if (tz ==9) index = 33;
    	else if (tz ==9.5) index = 34;
    	else if (tz ==9.75) index = 35;
    	else if (tz ==10) index = 36;
    	else if (tz ==10.5) index = 37;
    	else if (tz ==11) index = 38;
    	else if (tz ==11.5) index = 39;
    	else if (tz ==12) index = 40;
    	else if (tz ==12.75) index = 41;
    	else if (tz ==13) index = 42;
    	else if (tz ==13.75) index = 43;
    	else if (tz ==14) index = 44;

    	return index;
    }
    private double getTZ (int TZindex) {
    	
    	double tz=0;
    	switch (TZindex) {
	    	case 0:
	    	tz = -12;
	    	break;
	    	
	    	case 1:
	    	tz = -11;
	    	break;
	    	
	    	case 2:
	    	tz = -10.5;
	    	break;
	    	
	    	case 3:
	    	tz = -10;
	    	break;
	    	
	    	case 4:
	    	tz = -9.5;
	    	break;
	
	    	case 5:
	    	tz = -9;
	    	break;
	
	    	case 6:
	    	tz = -8.5;
	    	break;
	
	    	case 7:
	    	tz = -8;
	    	break;
	
	    	case 8:
	    	tz = -7;
	    	break;
	
	    	case 9:
	    	tz = -6;
	    	break;
	
	    	case 10:
	    	tz = -5;
	    	break;
	
	    	case 11:
	    	tz = -4.5;
	    	break;
	
	    	case 12:
	    	tz = -4;
	    	break;
	
	    	case 13:
	    	tz = -3.5;
	    	break;
	
	    	case 14:
	    	tz = -3;
	    	break;
	
	    	case 15:
	    	tz = -2.5;
	    	break;
	
	    	case 16:
	    	tz = -2;
	    	break;
	
	    	case 17:
		    tz = -1;
		    break;
	    	
	    	case 18:
	    	tz = 0;
	    	break;
	
	    	case 19:
	    	tz = 1;
	    	break;
	
	    	case 20:
	    	tz = 2;
	    	break;
	
	    	case 21:
	    	tz = 3;
	    	break;
	
	    	case 22:
	    	tz = 3.5;
	    	break;
	
	    	case 23:
	    	tz = 4;
	    	break;
	
	    	case 24:
	    	tz = 4.5;
	    	break;
	
	    	case 25:
	    	tz = 5;
	    	break;
	
	    	case 26:
	    	tz = 5.5;
	    	break;
	
	    	case 27:
	    	tz = 5.75;
	    	break;
	
	    	case 28:
	    	tz = 6;
	    	break;
	
	    	case 29:
	    	tz = 6.5;
	    	break;
	
	    	case 30:
	    	tz = 7;
	    	break;
	
	    	case 31:
	    	tz = 8;
	    	break;
	
	    	case 32:
	    	tz = 8.75;
	    	break;
	
	    	case 33:
	    	tz = 9;
	    	break;
	
	    	case 34:
	    	tz = 9.5;
	    	break;
	
	    	case 35:
	    	tz = 9.75;
	    	break;
	
	    	case 36:
	    	tz = 10;
	    	break;
	
	    	case 37:
	    	tz = 10.5;
	    	break;
	
	    	case 38:
	    	tz = 11;
	    	break;
	
	    	case 39:
	    	tz = 11.5;
	    	break;
	
	    	case 40:
	    	tz = 12;
	    	break;
	
	    	case 41:
	    	tz = 12.75;
	    	break;
	
	    	case 42:
	    	tz = 13;
	    	break;
	
	    	case 43:
	    	tz = 13.75;
	    	break;
	
	    	case 44:
	    	tz = 14;
	    	break;
    	}
    	return tz;
    }
    
    /**********************************************************************
     * helpers for starting/stopping monitoring of GPS changes below 
     **********************************************************************/
    private void startListening() {
        myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private void stopListening() {
        if (myManager != null)
                myManager.removeUpdates(this);
    }
    
    /**********************************************************************
     * LocationListener overrides below 
     **********************************************************************/
    @Override
    public void onLocationChanged(Location location) {
        gpsLat = location.getLatitude();
        gpsLng = location.getLongitude();
        gpsLock = 1;
        GPSNoteTextView.setText("New GPS fix obtained. You can now press 'Update'.");
    }    

    @Override
    public void onProviderDisabled(String provider) {}    

    @Override
    public void onProviderEnabled(String provider) {}    

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
 }