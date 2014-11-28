package com.example.locationlab;

import android.content.Intent;

public class LocationItem {

	private String mName = new String();
	private String mCountry = new String();
	private String mFlag = new String();

	LocationItem(Intent data){
		mName = data.getExtras().getString(AddLocationActivity.NAME_STRING);
		mCountry = data.getExtras().getString(AddLocationActivity.COUNTRY_STRING);
		mFlag = data.getExtras().getString(AddLocationActivity.FLAG_STRING);
	}

	LocationItem(String name, String country, String flag){
		mName = name;
		mCountry = country;
		mFlag = flag;
	}

	public String getName(){
		return mName;
	}

	public String getCountry(){
		return mCountry;
	}

	public String getFlag(){
		return mFlag;
	}

	public boolean isSame(LocationItem item){
		boolean a = item.getName().equals(this.mName);
		boolean b = item.getCountry().equals(this.mCountry);
		boolean c = item.getFlag().equals(this.getFlag());
		
		return a && b && c;
	}

}
