package com.example.locationlab;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AddLocationActivity extends Activity{

	public static final String NAME_STRING = "NAME";
	public static final String COUNTRY_STRING = "COUNTRY";
	public static final String FLAG_STRING = "FLAG";
	public List<String> responseString;
	private EditText mLatText;
	private EditText mLongText;
	private String[] coords = new String[2];
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_loc);
        
        mLatText = (EditText) findViewById(R.id.latitude);
        mLongText = (EditText) findViewById(R.id.longitude);
        
        
        final TextView submitButton = (TextView) findViewById(R.id.set_loc);
        submitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				coords[0] = mLatText.getText().toString();
				coords[1] = mLongText.getText().toString();
				
				int lat = Integer.parseInt(coords[0]);
				int lng = Integer.parseInt(coords[1]);
				
				if ( lat < -90 || lat > 90 || lng < -180 || lng > 180){					
					setResult(LocationManagerActivity.ERROR_INPUT);
					finish();
				} else {
					new NetworkingTask().execute(coords);
				}
			}});
	}
	
	private class NetworkingTask extends AsyncTask<String, Void, List<String>> {
		private static final String USER_NAME = "pmasigla";
		
		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
		
		@Override
		protected List<String> doInBackground(String... params) {
			String URL = "http://api.geonames.org/findNearbyPlaceName?lat=" + params[0] +"&lng=" + params[1] + "&username=" + USER_NAME;
			HttpGet request = new HttpGet(URL);
			
			Log.i("AddLocationActivity", "got HttpGet request");
			
			XMLResponseHandler responseHandler = new XMLResponseHandler();
			
			try {
				Log.i("AddLocationActivity", "executing mClient");
				return mClient.execute(request, responseHandler);
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			return null;
		}
		
		protected void onPostExecute(List<String> result){
			if(null != mClient)
				mClient.close();
			
			if(result.size() == 0) {
				setResult(LocationManagerActivity.ERROR_NOT_FOUND);
			} else {
				Intent data = new Intent();
				data.putExtra(NAME_STRING, result.get(0));
				data.putExtra(COUNTRY_STRING, result.get(1));
				data.putExtra(FLAG_STRING, "http://www.geonames.org/flags/x/" + result.get(2) + ".gif");
				setResult(RESULT_OK, data);
			}
			
			finish();
		}
		
	}
	
	private class XMLResponseHandler implements ResponseHandler<List<String>>{
		private static final String NAME_TAG ="name";
		private static final String COUNTRY_CODE_TAG ="countryCode";
		private static final String COUNTRY_TAG ="countryName";
		private String mName, mCtryCode, mCtryName;
		private boolean mIsParsingName, mIsParsingCtryCode, mIsParsingCtryName;
		private final List<String> mResults = new ArrayList<String>();
		
		@Override
		public List<String> handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			try{
				Log.i("AddLocationActivity", "executing responsehandler");
				// create pull parser
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser xpp = factory.newPullParser();
				
				// set parser's input to be XML document in http response
				xpp.setInput(new InputStreamReader(response.getEntity().getContent()));
				
				// get first parser event
				int eventType = xpp.getEventType();
				
				// iterate over XML document
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if(eventType == XmlPullParser.START_TAG) {
						startTag(xpp.getName());
					} else if (eventType == XmlPullParser.END_TAG) {
						endTag(xpp.getName());
					} else if (eventType == XmlPullParser.TEXT) {
						text(xpp.getText());
					}
					eventType = xpp.next();
				}
				
				return mResults;
			} catch (XmlPullParserException e) {
			}
			return null;
		}
		
		public void startTag(String localName){
			if(localName.equals(NAME_TAG)){
				mIsParsingName = true;
			} else if(localName.equals(COUNTRY_CODE_TAG)){
				mIsParsingCtryCode = true;
			} else if(localName.equals(COUNTRY_TAG)){
				mIsParsingCtryName = true;
			}
		}
		
		public void endTag(String localName){
			if(localName.equals(NAME_TAG)){
				mIsParsingName = false;
			} else if(localName.equals(COUNTRY_CODE_TAG)){
				mIsParsingCtryCode = false;
			} else if(localName.equals(COUNTRY_TAG)){
				mIsParsingCtryName = false;
			} else if(localName.equals("geoname")){
				mResults.add(mName);
				mResults.add(mCtryName);
				mResults.add(mCtryCode.toLowerCase());

				mName = null;
				mCtryCode = null;
				mCtryName = null;
			}
		}
		
		public void text(String text){
			if (mIsParsingName){
				mName = text.trim();
			} else if (mIsParsingCtryCode){
				mCtryCode = text.trim();
			} else if (mIsParsingCtryName){
				mCtryName = text.trim();
			}
		}
		
	}

}
