package dawoodibohra.salaat;

import android.text.format.Time;

public class UtilCalendar {
	
	public static int[] getMisriDate (Time time) {
    	// don't question the math, it works
    	
    	double year = time.year;
    	double month = time.month;
    	double day = time.monthDay;
    	
    	double m = month + 1;
    	double y = year;
    	
    	if (m < 3) {
    		y = y - 1;
    		m = m + 12;
    	}
    	
		double a = Math.floor(year/100.);
		double b = 2-a+Math.floor(a/4.);
		if (y < 1583) b = 0;
		if (y == 1582) {
			if(m > 10) b = -10;
			if(m == 10) {
				b = 0;
				if(day > 4) b = -10;
			}
		}	    	

    	double jd = Math.floor(365.25*(y+4716))+Math.floor(30.6001*(m+1))+day+b-1524;
    	
    	b = 0;
    	if(jd>2299160){
    		a = Math.floor((jd-1867216.25)/36524.25);
    		b = 1+a-Math.floor(a/4.);
    	}
    	double bb = jd+b+1524;
    	double cc = Math.floor((bb-122.1)/365.25);
    	double dd = Math.floor(365.25*cc);
    	double ee = Math.floor((bb-dd)/30.6001);
    	day =(bb-dd)-Math.floor(30.6001*ee);
    	month = ee-1;
    	if(ee>13) {
    		cc = cc + 1;
    		month = ee-13;
    	}
    	year = cc-4716;
    	
    	double iyear = 10631./30.;
    	double epochastro = 1948084;

    	double shift3 = 0.01/60.; // results in years 2, 5, 8, 10, 13, 16, 19, 21, 24, 27 & 29 as leap years

    	double z = jd-epochastro;
    	double cyc = Math.floor(z/10631.);
    	z = z-10631*cyc;
    	double j = Math.floor((z-shift3)/iyear);
    	double iy = 30*cyc+j;
    	z = z-Math.floor(j*iyear+shift3);
    	double im = Math.floor((z+28.5001)/29.5);
    	if(im==13) im = 12;
    	double id = z-Math.floor(29.5001*im-29);
    	im = im - 1 ;
    	
    	int[] date = {(int)id, (int)im, (int)iy} ;

    	return date ;
    }
	
	public static Time getGregDate(int[] hijriDate) {
		Time time = new Time();
		time.set(1,0,2005);
		int[] trialHijriDate = getMisriDate(time);
		
		int yearDifference;
		long yearDifferenceInMillis;
		int monthDifference;
		long monthDifferenceInMillis;
		int dayDifference;
		long dayDifferenceInMillis;
		
		int n = 0;
		
		while (trialHijriDate != hijriDate && n < 5) {
			yearDifference = hijriDate[2]-trialHijriDate[2];
			yearDifferenceInMillis = (long)yearDifference * 30617280000L;
			monthDifference = hijriDate[1]-trialHijriDate[1];
			monthDifferenceInMillis = (long)monthDifference * 2551440000L;
			dayDifference = hijriDate[0]-trialHijriDate[0];
			dayDifferenceInMillis = (long)dayDifference * 86400000L;
			time.set(time.toMillis(false)+yearDifferenceInMillis+monthDifferenceInMillis+dayDifferenceInMillis);
			trialHijriDate = getMisriDate(time);
			n++;
		}
		return time;
	}
    
    public static String getGregDay(int day){
    	String dayName = "" ;
        switch(day) { 
		    case 0:
		        dayName =  "Sunday";
		        break;
		    case 1:
		        dayName = "Monday";
		        break;
		    case 2:
		        dayName = "Tuesday";
		        break;
		    case 3:
		        dayName = "Wednesday";
		        break;
		    case 4:
		        dayName = "Thursday";
		        break;
		    case 5:
		        dayName = "Friday";
		        break;
		    case 6:
		        dayName = "Saturday";
		        break;
        }
        return dayName ;
    }
    
    public static String getMisriDay(int day){
    	String dayName = "" ;
        switch(day) { 
		    case 0:
		        dayName =  "Yawm al-Ahad";
		        break;
		    case 1:
		        dayName =  "Yawm al-Ithnain";
		        break;
		    case 2:
		        dayName =  "Yawm al-Thalatha";
		        break;
		    case 3:
		        dayName =  "Yawm al-Arba'a";
		        break;
		    case 4:
		        dayName =  "Yawm al-Khamis";
		        break;
		    case 5:
		        dayName =  "Yawm al-Jum'a";
		        break;
		    case 6:
		        dayName =  "Yawm al-Sabt";
		        break;
        }
        return dayName ;
    }
    
    public static String getGregMonth(int month){
    	String monthName = "" ;
        switch(month) { 
		    case 0:
		        monthName = "January";
		        break;
		    case 1:
		        monthName = "February";
		        break;
		    case 2:
		        monthName = "March";
		        break;
		    case 3:
		        monthName = "April";
		        break;
		    case 4:
		        monthName = "May";
		        break;
		    case 5:
		        monthName = "June";
		        break;
		    case 6:
		        monthName = "July";
		        break;
		    case 7:
		        monthName = "August";
		        break;
		    case 8:
		        monthName = "September";
		        break;
		    case 9:
		        monthName = "October";
		        break;
		    case 10:
		        monthName = "November";
		        break;
		    case 11:
		        monthName = "December";
		        break;
        }
        return monthName ;
    }

    public static String getMisriMonth(int month){
    	String monthName = "" ;
        switch(month) { 
		    case 0:
		        monthName = "Muharram al-Haraam";
		        break;
		    case 1:
		        monthName = "Safar al-Muzaffar";
		        break;
		    case 2:
		        monthName = "Rabi al-Awwal";
		        break;
		    case 3:
		        monthName = "Rabi al-Aakhar";
		        break;
		    case 4:
		        monthName = "Jumada al-Ula";
		        break;
		    case 5:
		        monthName = "Jumada al-Ukhra";
		        break;
		    case 6:
		        monthName = "Rajab al-Asab";
		        break;
		    case 7:
		        monthName = "Shaban al-Karim";
		        break;
		    case 8:
		        monthName = "Ramadan al-Moazzam";
		        break;
		    case 9:
		        monthName = "Shawwal al-Mukarram";
		        break;
		    case 10:
		        monthName = "Zilqad al-Haraam";
		        break;
		    case 11:
		        monthName = "Zilhaj al-Haraam";
		        break;
        }
        return monthName;
    }
    
    public static int getDayOfHijriYear(int[] date) {
    	int day = date[0] + (date[1]/2)*59 + (date[1]%2)*30 ;
    	return day;
    }
    
    public static boolean isLeap(int[] date) {
    	int year = date[2] % 30; // Year in Mod 30
    	boolean isLeap;
    	if (year == 2 || year == 5 || year == 8 || year == 10 || year == 13 || year == 16 || year == 19 || year == 21 || year == 24 || year == 27 || year == 29)
    		isLeap = true;
    	else
    		isLeap = false;
    	return isLeap;
    }
    
    public static int getYearSize(int[] date) {
    	if (isLeap(date))
    		return 355;
    	else
    		return 354;
    }
    
    public static int getMonthSize(int[] date) {
    	int month = date[1] % 2; // Month in Mod 2
    	int monthSize;
    	if (month == 0)
    		monthSize = 30;
    	else
    		monthSize = 29;
    	if ((date[1] == 11) && isLeap(date))
    		monthSize = 30;
    	return monthSize;
    }
    
    public static String getMiqaat(int[] date) {
    	
    	Time gregDate = getGregDate(date);
    	
		int day = getDayOfHijriYear(date);
		String miqaatDescription = new String();
		
		miqaatDescription = "" ;
		
		// Calendar Miqaats + Panjetan/Aimmat
		switch(day) {
	    case 2:	miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: First Waaz"; break;
	    case 3: miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Second Waaz"; break;
	    case 4: miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Third Waaz"; break;
	    case 5: miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Fourth Waaz"; break;
	    case 6: miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Fifth Waaz"; break;
	    case 7:	miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Sixth Waaz"; break;
	    case 8: miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Seventh Waaz"; break;
	    case 9: miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Eigth Waaz (Tasu)"; break;
	    case 10: miqaatDescription = miqaatDescription + "\n• Ashara Mubaraka: Ninth Waaz (Ashura)"; break;
	    case 50: miqaatDescription = miqaatDescription + "\n• Chehlum: Imam Husain AS"; break;
	    case 58: miqaatDescription = miqaatDescription + "\n• Shahadat: Imam Hasan AS"; break;
	    case 71: miqaatDescription = miqaatDescription + "\n• Milad: Rasulullah SAW"; break;
	    case 93: miqaatDescription = miqaatDescription + "\n• Milad: Imam-uz-Zaman SA"; break;
	    case 128: miqaatDescription = miqaatDescription + "\n• Shahadat: Moulatena Fatema-tuz-Zahra SA"; break;
	    case 178: miqaatDescription = miqaatDescription + "\n• Namaaz: Washeq Raat (pehli raat)"; break;
	    case 190: miqaatDescription = miqaatDescription + "\n• Milad: Moulana Ali ibne Abi Talib SA"; break;
	    //case 191: miqaatDescription = miqaatDescription + "\n• Urus: Moulatena Zainab binte Maulana Ali SA"; break;
	    case 204: miqaatDescription = miqaatDescription + "\n• Namaaz: Washeq Raat (27mi raat), Laylatul Meraj";
	        miqaatDescription = miqaatDescription + "\n• Rozu: Yawm-ul-Mabas (Moti-us-Sawalat)"; break;
	    case 222: miqaatDescription = miqaatDescription + "\n• Namaaz: Washeq Raat (15mi raat), Laylatul Nisf"; break;
	    case 229: miqaatDescription = miqaatDescription + "\n• Urus: Moulatena Hurratul Maleka Arwa binte Ahmed AQ (Yemen Sanaa)"; break;
	    case 253: miqaatDescription = miqaatDescription + "\n• Namaaz: Washeq Raat (17mi raat), Layali Fazila"; break;
	    case 255: miqaatDescription = miqaatDescription + "\n• Namaaz: Washeq Raat (19mi raat), Layali Fazila";
	        miqaatDescription = miqaatDescription + "\n• Shahadat: Moulana Ali ibne Abi Talib SA"; break;
	    case 257: miqaatDescription = miqaatDescription + "\n• Namaaz: Washeq Raat (21mi raat), Layali Fazila"; break;
	    case 259: miqaatDescription = miqaatDescription + "\n• Namaaz: Washeq Raat (23mi raat), Laylatul Qadr"; break;
	    case 267: miqaatDescription = miqaatDescription + "\n• Eid & Takbeera: Eid-ul-Fitr"; break;
	    //case 276: miqaatDescription = miqaatDescription + "\n• Urus: Dai-ud-Duat Syedna Hebatullah Al Moayyed Fid Deen As Shirazi RA (Misr)"; break;
	    case 334: miqaatDescription = miqaatDescription + "\n• Namaaz & Rozu: Yawm-e-Arafah";
	        miqaatDescription = miqaatDescription + "\n• Takbeera: Eid-ul-Adha"; break;
	    case 335: miqaatDescription = miqaatDescription + "\n• Eid & Takbeera: Eid-ul-Adha"; break;
	    case 336: miqaatDescription = miqaatDescription + "\n• Takbeera: Eid-ul-Adha"; break;
	    case 337: miqaatDescription = miqaatDescription + "\n• Takbeera: Eid-ul-Adha"; break;
	    case 338: miqaatDescription = miqaatDescription + "\n• Takbeera: Eid-ul-Adha"; break;
	    case 343: miqaatDescription = miqaatDescription + "\n• Eid & Rozu: Ghadeer-e-Khum"; break; }
		
		//if (date[0]==1) miqaatDescription = miqaatDescription + "\n• Pehli Tarikh";
        
		if (date[0]==13 && ((gregDate.weekDay==3 && date[1]!=11) || (date[1]==6)))
        	miqaatDescription = miqaatDescription + "\n• Rozu: Ayyam-ul-Beez";
        if (date[0]==14 && ((gregDate.weekDay==4 && date[1]!=11) || (date[1]==6)))
        	miqaatDescription = miqaatDescription + "\n• Rozu: Ayyam-ul-Beez";
        if (date[0]==15 && ((gregDate.weekDay==5 && date[1]!=11) || (date[1]==6))) {
        	miqaatDescription = miqaatDescription + "\n• Rozu & Namaaz: Ayyam-ul-Beez, Salaat-uz-Zawaal & Das Surat"; }
        
        if (date[1]==8 && date[0] > 23) {
        	if (date[0]==24 && gregDate.weekDay==5)
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam after Isha";
        	else if (date[0]==25 && (gregDate.weekDay==5 || gregDate.weekDay==6))
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam after Isha";
        	else if (date[0]==26 && (gregDate.weekDay==5 || gregDate.weekDay==6 || gregDate.weekDay==0))
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam after Isha";
        	else if (date[0]==27 && (gregDate.weekDay==5 || gregDate.weekDay==6 || gregDate.weekDay==0 || gregDate.weekDay==1))
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam after Isha";
        	else if (date[0]==28 && (gregDate.weekDay==5 || gregDate.weekDay==6 || gregDate.weekDay==0 || gregDate.weekDay==1 || gregDate.weekDay==2))
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam after Isha";
        	else if (date[0]==29 && (gregDate.weekDay==5 || gregDate.weekDay==6 || gregDate.weekDay==0 || gregDate.weekDay==1 || gregDate.weekDay==2 || gregDate.weekDay==3))
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam after Isha";
        	else if (date[0]==30)
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam after Isha";
        	if (gregDate.weekDay==5)
        		miqaatDescription = miqaatDescription + "\n• Tasbeeh: Nabi na Naam at Zawaal"; }
		
        // Duato Urus
        switch(day) {
        case 7:	miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ismail Badruddin RA [38th Dai] (India Jamnagar)"; break;
        case 10: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Zoeb Bin Moosa RA [1st Dai] (Yemen Hoos)"; break;
        case 16: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Hatim Bin Syedna Ibrahim RA [3rd Dai] (Yemen Hutaib Mubarak)"; break;
        case 17: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ibrahim Wajihuddin RA [39th Dai] (India Ujjain)"; break;
        case 31: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Bin Moulaya Hasan RA [10th Dai] (Yemen Sanaa)"; break;
        case 33: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Shamsuddin RA [18th Dai] (Yemen Shareqa)"; break;
        case 34: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abduttayyeb Zakiuddin RA [41st Dai] (India Burhanpur)"; break;
        case 52: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Husain Bin Syedna Ali RA [8th Dai] (Yemen Sanaa)"; break;
        case 57: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Mohammed Ezzuddin RA [23rd Dai] (Yemen Zabeed)"; break;
        case 61: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abduttayyeb Zakiuddin RA [29th Dai] (India Ahmedabad)"; break;
        case 69: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abdullah Badruddin RA [50th Dai] (India Surat)"; break;
        case 82: miqaatDescription = miqaatDescription + "\n• Urus Syedna Ali bin Hanzala RA [6th Dai] (Yemen Hamadan)"; break;
        case 84: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Shamsuddin RA [30th Dai] (Yemen Hisne Afedat)"; break;
        case 105: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Jalaal Shamsuddin ibne Hasan RA [25th Dai] (India Ahmedabad)"; break;
        case 111: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Moosa Kalimuddin ibne Syedna Zakiuddin RA [36th Dai] (India Jamnagar)"; break;
        case 116: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Dawood ibne Ajab Shah Burhanuddin RA [26th Dai] (India Ahmedabad)"; break;
        case 163: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Dawood ibne Qutub Shah Burhanuddin RA [27th Dai] (India Ahmedabad)"; break;
        case 166: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Yusuf Najmuddin RA [42nd Dai] (India Surat)"; break;
        case 171: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ismail Badruddin bin Moulaya Raj RA [34th Dai] (India Jamnagar)"; break;
        case 175: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Qutbuddin Shaheed RA [32nd Dai] (India Ahmedabad)"; break;
        case 176: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ahmed bin Al Mubarak RA [7th Dai] (Yemen Hamdaan)"; break;
        case 177: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Mohammed Badruddin RA [46th Dai] (India Surat)"; break;
        case 181: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Noor Mohammed Nooruddin RA [37th Dai] (India Kutch-Mandvi)"; break;
        case 184: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Shaikh Adam Safiuddin RA [28th Dai] (India Ahmedabad)"; break;
        case 195: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Shamsuddin RA [13th Dai] (Yemen Zamarmar)"; break;
        case 196: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Taher Saifuddin RA [51st Dai] (India Mumbai)"; break;
        case 201: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abdul Muttalib Najmuddin bin Syedna Mohammed bin Hatim RA [14th Dai] (Yemen Zamarmar)"; break ;
        case 203: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abdul Qadir Najmuddin RA [47th Dai] (India Ujjain)"; break;
        case 208: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Hebatullah Al Moayyed ibne Syedna Ibrahim Vajihuddin RA [40th Dai] (India Ujjain)"; break;
        case 222: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Hassan Badruddin ibne Syedna Idris Imaduddin RA [20th Dai] (Yemen Masaar)"; break;
        case 223: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ibrahim bin Syedna Hussain Al Hamidi RA [2nd Dai] (Yemen Hamdaan)"; break;
        case 234: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali bin Mohammed bin Al Waleed RA [5th Dai] (Yemen Haraaz)"; break ;
        case 245: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abdullah Fakhruddin ibne Ali RA [16th Dai] (Yemen Zamarmar)"; break;
        case 255: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Mohammed Ezzuddin ibne Syedi Jeevanji Saheb RA [44th Dai] (India Surat)"; break;
        case 272: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Hasan Badruddin RA [17th Dai] (Yemen Zamarmar)"; break;
        case 274: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abbas Bin Syedna Mohammed RA [15th Dai] (Yemen Hisne Afedat)"; break;
        case 275: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Qasimkhan Zainuddin RA [31st Dai] (India Ahmedabad)"; break;
        case 276: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ibrahim Bin Syedna Husain RA [11th Dai] (Yemen Hisne Afedat)";
		miqaatDescription = miqaatDescription + "\n• Urus: Syedna Husain Husamuddin RA [21st Dai] (Yemen Masaar)"; break;
        case 304: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Feerkhan Shujauddin RA [33rd Dai] (India Ahmedabad)"; break;
        case 307: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abduttayyeb Zakiuddin RA [35th Dai] (India Jamnagar)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abdeali Saifuddin RA [43rd Dai] (India Surat)"; break;
        case 308: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Bin Syedna Husain RA [9th Dai] (Yemen Sanaa)"; break;
        case 310: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Tayyeb Zainuddin RA [45th Dai] (India Surat)"; break;
        case 314: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Idris Imaduddin RA [19th Dai] (Yemen Shibaam)"; break;
        case 316: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Shamsuddin RA [22nd Dai] (Yemen Masaar)"; break;
        case 320: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Bin Syedna Hatim RA [4th Dai] (Yemen Sanaa)"; break;
        case 326: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Mohammed bin Syedi Hatim RA [12th Dai] (Yemen Hisne Afedat)"; break;
        case 341: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Yusuf Najmuddin RA [24th Dai] (Yemen Taibah)"; break;
        case 352: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Abdul Hussain Husamuddin RA [48th Dai] (India Ahmedabad)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedna Mohammed Burhanuddin RA [49th Dai] (India Surat)"; break; }
        
        // Hudood Fozola
        switch(day) {
        // moharram
        case 1: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Abdullah AQ (India Khambat)"; break;
        case 2:	miqaatDescription = miqaatDescription + "\n• Urus: Syedi Khanji Feer Saheb AQ [Mukasir] (India Udaipur)";
        miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Raj Bin Maulaya Hasan AQ (India Ahmedabad)"; break;
        case 10: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Ahmed AQ (India Khambat)"; break;
        case 14: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Luqmanji Bin Mulla Ali Bhai AQ (India Wankaner)"; break;
        case 17: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Masood Bin Moulaya Sulaiman AQ (India Dhangadhra)"; break;
        case 18: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Ghani Feer Bin Dawoodji Shaheed AQ (India Kalavad)"; break;
        case 23: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Hasanfeer Shaheed AQ (India Denmaal)";
                miqaatDescription = miqaatDescription + "\n• Urus: Maisaab (India Daandi Gaam)";
                break;
        case 27: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Fakhruddin Shaheed AQ (India Taherabad)"; break;
        case 28: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Moosanji Bin Taj Saheb AQ (India Baroda)"; break;
        case 29: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Hasan Bin Moulaya Adam AQ (India Ahmedabad)"; break;
        // safar
        case 36: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Abdeali Imaduddin Bin Shaikh Jeeva Bhai AQ (India Surat)"; break;
        case 38: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Al Khattaab Bin Hasan Al Hamdani AQ"; break;
        case 39: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Tayyeb BS Zainuddin AQ (India Surat)"; break;
        case 42: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Ahmed Hameeduddin AQ Bin Syedna Abdullah RA"; break;
        case 43: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Adam Bin Sulaiman AQ (India Ahmedabad)"; break;
        case 44: miqaatDescription = miqaatDescription + "\n• Urus: Kaka Akela Kaki Akela AQ (India Khambat)";
        miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Nooh Saheb AQ (India Selaavi)"; break;
        case 45: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Hamza Bhai Bin Syedi Qasim Khan AQ (India Surat)"; break;
        case 47: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Shaikh Saheb Bin Sulaimanji AQ (Khwaja Mohammed Bin Ishaq Shaheed) (India Ahmedabad)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shaikh Ibrahim AQ & Shaikh Abdullah Saheb Shaheed AQ (India Chechat)"; break;
        // rabi ul awwal
        case 60: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shaikh Adam Safiyuddin AQ (India Jamnagar)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Jamaluddin bin Shaikh Adam (India Jamnagar)"; break;
        case 63: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Habibullah bin Mulla Adamji Bin Syedi Bawa Mulla Khan Saheb AQ (India Ujjain)"; break;
        case 66: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Abdeali Mohyuddin AQ (India Surat)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shaikh Dawoodbhai Mulla Mehmoodji AQ (India Udaipur)"; break;
        case 71: miqaatDescription = miqaatDescription + "\n• Urus: Amatullah Aai Saheba AQ Aqeelato Dai al-Asr Syedna Mohammed Burhanuddin TUS (UK London)"; break;
        case 73: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Miyaji Mulla Taj Saheb AQ (India Umreth)"; break;
        case 81: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Dawood Bin Moulaya Raj Saheb AQ (India Morbi)"; break;
        case 82: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Raj Saheb AQ (India Morbi)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Qazi Khan Bin Ameenshah AQ [Mazoon] (India Halwad)"; break;
        case 86: miqaatDescription = miqaatDescription + "\n• Urus Mohammad bin Hasan Saheb (India Dhinoj)"; break;				
        // rabi ul akhar
        case 97: miqaatDescription = miqaatDescription + "\n• Urus: Mulla Raj ibne Mulla Adam (India Jamnagar)"; break;
        case 99: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Abdul Rasul Shaheed AQ (India Banswara)"; break;
        case 103: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Ismailji Shaheed (India Godhra)"; break;
        case 109: miqaatDescription = miqaatDescription + "\n• Milad: Dai al-Asr Syedna Mohammed Burhanuddin TUS"; break;
        case 111: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Habibullah ibne Shaikh Sultan Ali (India Bharuch)"; break;
        case 118: miqaatDescription = miqaatDescription + "\n• Milad: Mazoon ud-Dawat Syedi Khuzaima Bhaisaheb Qutbuddin TUS"; break;
        // jamadil awwal
        case 119: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ahmed bin Syedna Ali Al Mukarram Sulaihi AQ (Yemen Sanaa)"; break;
        case 121: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Qazikhan ibne Ali (India Sidhpur)"; break;
        case 126: miqaatDescription = miqaatDescription + "\n• Urus: Mulla Vahed Bhaisaheb Mulla Ibrahim (India Surat)"; break;
        case 129: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Nooruddin AQ (India Dongaam)"; break;
        case 133: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Dawood bin Kazi Ahmed Saheb (India Ahmedabad)"; break;
        case 134: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Mohammed bin Idris Saheb AQ (Yemen)"; break;
        case 139: miqaatDescription = miqaatDescription + "\n• Urus: Sheth Chandabhai ibne Kareembhai (India Mumbai)"; break;
        case 141: miqaatDescription = miqaatDescription + "\n• Urus: Mulla Jaaferji Jivaji Saheb (India Amreli)"; break;
        // jamadil akhar
        case 156: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Luqmaanji Moulaya Habibullah (India Surat)"; break;
        case 162: miqaatDescription = miqaatDescription + "\n• Urus: Ganje Shohada (India Ahmedabad)"; break;
        case 166: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Adam ibne Dawood (India Jamnagar)";
        miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Burhanuddin ibne Khoj (India Pisavada)"; break;
        case 174: miqaatDescription = miqaatDescription + "\n• Milad: Mukasir ud-Dawat Syedi Hussain Bhaisaheb Husamuddin TUS"; break;
        case 175: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Lamak Bin Malik (Yemen)"; break;
        case 176: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Yahya bin Lamak AQ (Yemen)"; break;
        //case 177: 
        //miqaatDescription = miqaatDescription + "\n• Urus: Syedna Qazi Noman bin Mohammed (Misr)";
        //miqaatDescription = miqaatDescription + "\n• Urus: Bhensaheb Ajab bu binte Syedna Qutbuddin Shaheed (India Ahmedabad)";
        //break;
        // rajab
        case 179:
    	miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Raj bin Dawood (India Ahmedabad)";
        //miqaatDescription = miqaatDescription + "\n• Urus: Bhaiji Bhai ibne Qazi Bhai (Pakistan Karachi)"; 
        break;
        case 181: miqaatDescription = miqaatDescription + "\n• Urus: Syedji Hasanji Badshah (India Ujjain)"; break;
        case 182: miqaatDescription = miqaatDescription + "\n• Urus: Syeda Fadela Fatema Aaisaheba (India Kutch-Mandvi)"; break;
        case 185: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Saifuddin Saheb (India Jamnagar)"; break;
        case 188: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Raj ibne Dawood (India Ahmedabad)"; break;
        case 189: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Najamkhan bin Syedna Feerkhan (India Aurangabad)"; break;
        case 191: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Yaqub Saheb (India Patan)"; break;
        case 194: miqaatDescription = miqaatDescription + "\n• Ayyam-ul-Barakatul Khuldiya"; break;
        case 195: miqaatDescription = miqaatDescription + "\n• Ayyam-ul-Barakatul Khuldiya"; break;
        //case 198: miqaatDescription = miqaatDescription + "\n• Urus: Miyasaheb Ibrahim Al Yamani AQ"; break;
        case 201: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Qamruddin Bhaisaheb bin Syedna Hebatullah Al Moayyed AQ (India Ujjain)"; break ;
        case 204: miqaatDescription = miqaatDescription + "\n• Urus: Aminji Shahid [Urus done on the 24th] (India Paddhari)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Miasaheb Alibhai bin Peeriji (India Radhanpur)"; break;
        case 206: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Luqmanji bin Syedi Dawoodji (India Udaipur)"; break;
        // shaban
        case 226: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Saleh Bhaisaheb Safiyuddin AQ [Mukasir] (India Mumbai)"; break;
        case 229: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shaikh Feer bin Dawood Shaheed (India Peenpur-Saurashtra)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shaikh Valibhai ibne Syedi Habibullah [Urus on 2nd of Ramadan, recited on 22nd Shaban] (India Parda-Malwa)"; break;
        case 232: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shams Khan ibne Syedi Yusufji (India Surat)"; break;
        case 236: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Jeevanji bin Shaikh Dawood Bhaisaheb (India Burhanpur)"; break;
        // ramadan
        case 237: miqaatDescription = miqaatDescription + "\n• Urus: Shaikh Dawood Bhaisaheb (India Dhinoj)"; break;
        case 238: miqaatDescription = miqaatDescription + "\n• Urus: Shaikh Valibhai bin Shaikh Habibullah (India Parda-Malwa)"; break;
        case 239: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Tayeb Bhaisaheb Zainuddin bin Syedna Abdul Qadir Najmuddin (India Surat)"; break;
        case 244: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Fazl Qutbuddin ibne Syedna Abdullah Badruddin AQ [Mazoon] (India Surat)"; break;
        case 252: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Hebatullah Jamaluddin [Mazoon] (India Jamnagar)"; break;
        case 259: miqaatDescription = miqaatDescription + "\n• Milad: Mansoos ud-Dai-al-Asr Syedi Aali Qadr Mufaddal Bhaisaheb Saifuddin TUS"; break;
        // shawwal
        case 270: miqaatDescription = miqaatDescription + "\n• Urus: Shehzadi Sakina Baisaheba binte Syedna Taher Saifuddin RA (India Mumbai)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Yusufji & Syedi Tayyebji Shaheed AQ (India Ahmedabad)"; break;
        case 271: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Abdul Qadir Hakmuddin RA [Mazoon] - 1st Urus (India Burhanpur)"; break;
        case 273: miqaatDescription = miqaatDescription + "\n• Urus: Syedna Mohammed Bin Taher RA [Mazoon] (Yemen)"; break;
        case 279: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Ameenji Bin Jalal (India Ahmedabad)"; break;
        case 290: miqaatDescription = miqaatDescription + "\n• Urus: Shaikh Qutubuddin Bhai Bin Sulaimanji (India Pune)"; break;
        case 291: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Abde Moosa bin Syedna Ismail Badruddin RA (India Jamnagar)"; break;
        case 292: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Qasim Khan Bin Syed Hamza Bhai RA [Mazoon] (India Surat)"; break;
        case 293: 
    	miqaatDescription = miqaatDescription + "\n• Urus: Syedi Abdul Qadir Hakmuddin RA [Mazoon] - 2nd Urus (India Burhanpur)";
        miqaatDescription = miqaatDescription + "\n• Urus: Miyasaheb Abdul Ali Waliyullah AQ (India Jaora)"; break;
        case 295: 
    	miqaatDescription = miqaatDescription + "\n• Urus: Mulla Saleh Bhai bin Najam Khan (India Ahmedabad)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Bawa Mulla Khan AQ (India Rampura)"; break;
        // zilqad
        case 299: miqaatDescription = miqaatDescription + "\n• Urus: Amatullah Aai Saheba AQ Aqeelato Syedna Mohammed Burhanuddin RA [49th Dai]"; break;
        case 303: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shaikh Adam Safiyuddin Bin Syedna Noor Mohammed Nooruddin RA"; break;
        case 306: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Hasan Bin Nooh Bharuchi AQ (Yemen Masaar)";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedna Ali Bin Mohammed As Sulayhi (Yemen)"; break;
        case 310: miqaatDescription = miqaatDescription + "\n• Urus: Rani Bai Saheba AQ binte Syedna Ismail Badruddin RA"; break;
        case 315: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Mulla Wali Bhai Shaheed bin Syedi Jivanji AQ"; break;
        case 317: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Shaikh Sadiqali Saheb AQ (India Surat)"; break;
        case 322: miqaatDescription = miqaatDescription + "\n• Milad: Syedna Taher Saifuddin RA [51st Dai]";
        miqaatDescription = miqaatDescription + "\n• Urus: Syedi Yusuf Khan bin Syedi Shams Khan AQ"; break;
        // zilhajj
        case 331: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Khoj bin Malak (India Kaparwanj)"; break;
        case 338: miqaatDescription = miqaatDescription + "\n• Urus: Moulaya Feroz bin Ismailji AQ (India Ahmedabad)"; break;
        //case 340: miqaatDescription = miqaatDescription + "\n• Milad: Amatullah Aai Saheba AQ Aqeelato Dai al-Asr Syedna Mohammed Burhanuddin TUS"; break;
        case 341: miqaatDescription = miqaatDescription + "\n• Urus: Syedi Ishaq Bhaisaheb Jamaluddin AQ [Mukasir]"; break;
        case 352: miqaatDescription = miqaatDescription + "\n• Urus: Ganj Shohoda AQ (India Ahmednagar)"; break;  
		
		}
        
		return miqaatDescription ;
    }
}