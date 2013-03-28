package dawoodibohra.salaat;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.EditText;

public class ActivityCalendar extends Activity {
	
	private Time time = new Time();
	
	private Dialog datePickerDialog;
    private Spinner DateSpinner; 
    private Spinner MonthSpinner;
    private EditText YearEditText;
    private boolean dialogType;
	private final static int DIALOG_GREGPICKER_ID = 0;
	private final static int DIALOG_HIJRIPICKER_ID = 1;

	private final long yearOffset = 30617280000L; // 354.3666666.. days in a avg year
	private final long monthOffset = 2551440000L; // 29.5305555.. days in a avg month
	private final long weekOffset = 604800000L; // 7 days
	private long Offset;
	private boolean specificDate;
	
	private Context context;
	
	boolean showGreg;
	boolean showAll;
	boolean showDB;
	int typeOfCalendar;
	int fontSize;
	Button nextbutton;
	Button prevbutton;
	Button todaybutton;
	Button gregdatebutton;
	Button hijridatebutton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.calendar);

		nextbutton = (Button)findViewById(R.id.NextButton);
		nextbutton.setOnClickListener(Next);

		prevbutton = (Button)findViewById(R.id.PrevButton);
		prevbutton.setOnClickListener(Prev);
        
		todaybutton = (Button)findViewById(R.id.TodayButton);
		todaybutton.setOnClickListener(Today);
		
		gregdatebutton = (Button)findViewById(R.id.ButtonGreg);
		gregdatebutton.setOnClickListener(GregDate);
		
		hijridatebutton = (Button)findViewById(R.id.ButtonHijri);
		hijridatebutton.setOnClickListener(HijriDate);
    }
	
	@Override
	public void onStart() {
		super.onStart();
		specificDate = true;
		setToToday();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
    	NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	mNM.cancel(ServiceNotifications.NOTIFIER_MIQAAT);
    	
		context = getApplicationContext();
		
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	Editor editor = prefs.edit();
		
    	if (!prefs.contains(ActivitySettings.SETTING_SHOWDB)) {
	    	editor.putBoolean(ActivitySettings.SETTING_SHOWDB, true); // show db miqaats
	    	editor.commit();
    	}

    	if (!prefs.contains(ActivitySettings.SETTING_SHOWGREG)) {
	    	editor.putBoolean(ActivitySettings.SETTING_SHOWGREG, true); // show greg by default
	    	editor.commit();
    	}

    	if (!prefs.contains(ActivitySettings.SETTING_SHOWALL)) {
	    	editor.putBoolean(ActivitySettings.SETTING_SHOWALL, true); // show all
	    	editor.commit();
    	}
    	
    	if (!prefs.contains(ActivitySettings.SETTING_CALTYPE)) {
    		editor.putInt(ActivitySettings.SETTING_CALTYPE, 1); // weekly
	    	editor.commit();
    	}

    	showGreg = (boolean)prefs.getBoolean(ActivitySettings.SETTING_SHOWGREG, true);
    	showAll = (boolean)prefs.getBoolean(ActivitySettings.SETTING_SHOWALL, true);
    	showDB = (boolean)prefs.getBoolean(ActivitySettings.SETTING_SHOWDB, true);
    	typeOfCalendar = (int)prefs.getInt(ActivitySettings.SETTING_CALTYPE, 1); // 0 = day, 1 = week, 1 = month, 2 = year
    	fontSize = (int)prefs.getInt(ActivitySettings.SETTING_ACTUAL_FONT_SIZE, 16);
    	
    	nextbutton.setTextSize(fontSize);
    	prevbutton.setTextSize(fontSize);
    	todaybutton.setTextSize(fontSize);
    	gregdatebutton.setTextSize(fontSize);
    	hijridatebutton.setTextSize(fontSize);
    	if (typeOfCalendar == 0)
    		Offset = 86400000L; 
    	else if (typeOfCalendar == 1)
    		Offset = weekOffset;
    	else if (typeOfCalendar == 2)
    		Offset = monthOffset;
    	else
    		Offset = yearOffset;

		main();
	}
	
    private View.OnClickListener Next = new View.OnClickListener() {
    	public void onClick(View v) {
			time.set(time.toMillis(false)+Offset);
			time.normalize(true);
    		specificDate = false;
    		main();
    	}
    };
   
    private View.OnClickListener Prev = new View.OnClickListener() {
    	public void onClick(View v) {
			time.set(time.toMillis(false)-Offset);
			time.normalize(true);
			specificDate = false;
    		main();
    			
    	}
    };
    
    private View.OnClickListener Today = new View.OnClickListener() {
    	public void onClick(View v) {
    		setToToday();
    		specificDate = true;
    		main();
    	}
    };
    
    private View.OnClickListener GregDate = new View.OnClickListener() {
    	public void onClick(View v) {
    		dialogType = true; //true means greg
    		showDialog(DIALOG_GREGPICKER_ID);
    	}
    };
    
    private View.OnClickListener HijriDate = new View.OnClickListener() {
    	public void onClick(View v) {
    		dialogType = false; //false means hijri 
    		showDialog(DIALOG_HIJRIPICKER_ID);
    	}
    };
    
    private View.OnClickListener Go = new View.OnClickListener() {
    	public void onClick(View v) {
    		Editable ed = YearEditText.getText();
       		int year = Integer.parseInt(ed.toString());
    		if (dialogType)
        		time.set(DateSpinner.getSelectedItemPosition()+1, MonthSpinner.getSelectedItemPosition(), year);
    		else {
    			int[] hijriDate = {DateSpinner.getSelectedItemPosition()+1, MonthSpinner.getSelectedItemPosition(), year}; 
    			time.set(UtilCalendar.getGregDate(hijriDate));
    		}
    		time.normalize(true);
    		specificDate = true;
    		datePickerDialog.dismiss();
    		if (dialogType)
    			removeDialog(DIALOG_GREGPICKER_ID);
    		else
    			removeDialog(DIALOG_HIJRIPICKER_ID);
    		main();
    	}
    };
    
    private View.OnClickListener Cancel = new View.OnClickListener() {
    	public void onClick(View v) {
    		datePickerDialog.dismiss();
    		if (dialogType)
    			removeDialog(DIALOG_GREGPICKER_ID);
    		else
    			removeDialog(DIALOG_HIJRIPICKER_ID);
    	}
    };

    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_GREGPICKER_ID:
            processDialog();
            break;
        case DIALOG_HIJRIPICKER_ID:
            processDialog();
            break;
        default:
            datePickerDialog = null;
        }
        return datePickerDialog;
    }
    
	private void setToToday() {
		time.setToNow();
		time.normalize(true);
		UtilNamaazTimesCalculator namaazTimesCalculator = new UtilNamaazTimesCalculator();
		
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		if (!prefs.contains(ActivityLocationSettings.SETTING_LAT) || !prefs.contains(ActivityLocationSettings.SETTING_LNG) || !prefs.contains(ActivityLocationSettings.SETTING_TZ)) {
        	Intent i = new Intent (this, ActivityLocationSettings.class);
        	startActivity(i);
		}

		namaazTimesCalculator.setLocation((double)prefs.getFloat(ActivityLocationSettings.SETTING_LAT, 0), (double)prefs.getFloat(ActivityLocationSettings.SETTING_LNG, 0));
    	namaazTimesCalculator.setTimezone((double)prefs.getFloat(ActivityLocationSettings.SETTING_TZ, 0));
    	namaazTimesCalculator.setTime(time);
    	double[] currentState = namaazTimesCalculator.getState();
    	
    	if (currentState[0] >=6 && time.hour >= 12) {
       		time.set(time.toMillis(false)+86400000L);
       		time.normalize(true);
    	}
	}
    
	private void processDialog() { // dialogType : true=greg, false=hijri
	
        datePickerDialog = new Dialog(this) ;
        datePickerDialog.setContentView(R.layout.date_picker);
        
        DateSpinner = (Spinner)datePickerDialog.findViewById(R.id.SpinnerDate); 
        MonthSpinner = (Spinner)datePickerDialog.findViewById(R.id.SpinnerMonth);
        YearEditText = (EditText)datePickerDialog.findViewById(R.id.EditTextYear);
        
        if (dialogType)
        	datePickerDialog.setTitle("Pick a Gregorian Date");
        else
        	datePickerDialog.setTitle("Pick a Hijri Date");
        
    	ArrayAdapter<CharSequence> fAdapter;
    	int spinner_dd_item;
    	
    	if (dialogType)
    		fAdapter = ArrayAdapter.createFromResource(this, R.array.gregdate_options, android.R.layout.simple_spinner_item);
    	else
    		fAdapter = ArrayAdapter.createFromResource(this, R.array.hijridate_options, android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		DateSpinner.setAdapter(fAdapter);
		
		if (dialogType)
			fAdapter = ArrayAdapter.createFromResource(this, R.array.gregmonth_options, android.R.layout.simple_spinner_item);
		else
			fAdapter = ArrayAdapter.createFromResource(this, R.array.hijrimonth_options, android.R.layout.simple_spinner_item);
    	spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		MonthSpinner.setAdapter(fAdapter);

        if (dialogType) {
        	DateSpinner.setSelection(time.monthDay-1);
        	MonthSpinner.setSelection(time.month);
        	YearEditText.setText(Integer.toString(time.year));
        }
    	else {
        	int[] date = UtilCalendar.getMisriDate(time);
        	DateSpinner.setSelection(date[0]-1);
        	MonthSpinner.setSelection(date[1]);
        	YearEditText.setText(Integer.toString(date[2]));
    	}

        Button gobutton = (Button)datePickerDialog.findViewById(R.id.ButtonGo);
		gobutton.setOnClickListener(Go);
		
		Button cancelbutton = (Button)datePickerDialog.findViewById(R.id.ButtonCancel);
		cancelbutton.setOnClickListener(Cancel);
	}

    private void main() {
    	final TextView oldMiqaatsView = (TextView)findViewById(R.id.OldMiqaats);
    	TextView todaysMiqaatView = (TextView)findViewById(R.id.TodaysMiqaat);
    	TextView newMiqaatsView = (TextView)findViewById(R.id.NewMiqaats);
    	TextView TitleView = (TextView)findViewById(R.id.Title);
    	
    	TitleView.setTextSize((int) (fontSize*1.5F));
    	oldMiqaatsView.setTextSize(fontSize);
    	newMiqaatsView.setTextSize(fontSize);
    	todaysMiqaatView.setTextSize(fontSize);
    	
    	int date[];
        
        String currentMiqaat = "" ;
        String textToWrite = "" ;
        
    	date = UtilCalendar.getMisriDate(time);
    	
    	if (typeOfCalendar == 0) // single date calendar
    		specificDate = true;

    	if (specificDate) {
        	currentMiqaat = UtilCalendar.getMiqaat(date);
        	textToWrite = formatTextToShow(date,currentMiqaat);
        	todaysMiqaatView.setText(textToWrite);
        	todaysMiqaatView.setVisibility(View.VISIBLE);
        	newMiqaatsView.setVisibility(View.VISIBLE);
        }
    	else {
    		todaysMiqaatView.setVisibility(View.GONE);
    		newMiqaatsView.setVisibility(View.GONE);
    	}

        int dayOfHijriYear = UtilCalendar.getDayOfHijriYear(date);
        int daysOffset;
        long daysOffsetInMillis;
        int totalDays;
        long timeToRetain = time.toMillis(false);
        
        if (typeOfCalendar == 0) {
        	TitleView.setText(date[0] + " " + UtilCalendar.getMisriMonth(date[1]) + " " + date[2] + "H");
    		daysOffset = 0;
    		totalDays = 1;
        }
        else if (typeOfCalendar == 1) {
        	TitleView.setText(UtilCalendar.getMisriMonth(date[1]) + " " + date[2] + "H");
    		daysOffset = time.weekDay; // to start with sunday, yawm ul ahad
    		totalDays = 7;
        }
        else if (typeOfCalendar == 2) {
    		TitleView.setText(UtilCalendar.getMisriMonth(date[1]) + " " + date[2] + "H");
    		daysOffset = date[0]-1;
    		totalDays = UtilCalendar.getMonthSize(date);
    	}
    	else {
    		TitleView.setText(date[2]+"H");
    		daysOffset = dayOfHijriYear-1;
    		totalDays = UtilCalendar.getYearSize(date); 
    	}
    		
    	textToWrite = "";
    	
    	daysOffsetInMillis = 86400000L*(long)daysOffset;
    	time.set(time.toMillis(false)-daysOffsetInMillis);
    	time.normalize(true);
    	
    	int i;
    	
    	if (!specificDate)
    		daysOffset = totalDays;
    	
    	for (i = 0; i < daysOffset; i++) {
        	date = UtilCalendar.getMisriDate(time);
        	currentMiqaat = UtilCalendar.getMiqaat(date);
        	if (currentMiqaat != "" || showAll || !showDB)
        		textToWrite = textToWrite + "\n" + formatTextToShow(date,currentMiqaat) + "\n" ;
        	time.set(time.toMillis(false)+86400000L);
        	time.normalize(true);
        }
        
    	oldMiqaatsView.setText(textToWrite);
    	    	
        if (specificDate) {
        	i++;
        	time.set(time.toMillis(false)+86400000L);
        	time.normalize(true);

        	textToWrite = "";
        
	    	for (; i < totalDays; i++) {
	        	date = UtilCalendar.getMisriDate(time);
	        	currentMiqaat = UtilCalendar.getMiqaat(date);
	        	if (currentMiqaat != "" || showAll || !showDB) {
	        		if (!(i==daysOffset))
	        			textToWrite = textToWrite + "\n";
	        		textToWrite = textToWrite + formatTextToShow(date,currentMiqaat) + "\n" ;
	        	}
	        	time.set(time.toMillis(false)+86400000L);
	        	time.normalize(true);
	        }
	    	
	    	newMiqaatsView.setText(textToWrite);
	    	// android 1.5 is mad buggy - it wont run this next bit of code when started from onCreate... wtf?.. from onResume and etc. is ok, even onCreate is ok if you add like a toast after it.. WTF
			final ScrollView scrollView = (ScrollView)findViewById(R.id.ScrollView02);
	    	scrollView.post(new Runnable() {
	            public void run() {
	                scrollView.smoothScrollTo(0, oldMiqaatsView.getHeight()-22);
	            }
	        });
    	}
        
    	else {
			final ScrollView scrollView = (ScrollView)findViewById(R.id.ScrollView02);
	    	scrollView.post(new Runnable() {
	            public void run() {
	                scrollView.smoothScrollTo(0, 0);
	            }
	        });
    	}
        
    	time.set(timeToRetain);
    	time.normalize(true);

    }
    
    private String formatTextToShow (int[] date, String currentMiqaat) {
    	String theText;
    	//theText = UtilCalendar.getMisriDay(time.weekDay) + ", " + date[0] + " " + UtilCalendar.getMisriMonth(date[1]) + " " + date[2] + "H";
    	theText = date[0] + " " + UtilCalendar.getMisriMonth(date[1]) + " " + date[2] + "H";
        if (showGreg)
			theText = theText + "\n" + UtilCalendar.getGregDay(time.weekDay) + ", " + time.monthDay + " " + UtilCalendar.getGregMonth(time.month) + " " + time.year;
        if (showDB && currentMiqaat != "")
        	//theText = theText + "\n" + "Miqaats:" + currentMiqaat ;
        	theText = theText + currentMiqaat ;
        return theText;
    }
}		