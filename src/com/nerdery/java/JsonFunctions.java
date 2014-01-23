package com.nerdery.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonFunctions 
{
	public static JSONObject getJSONfromURL(String url)
	{
		InputStream inputstream=null;
		String result = "";
		JSONObject jArray = null;

		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			inputstream = entity.getContent();
		}
		catch(IOException e)
		{
			Log.d("Debug", e.getMessage());
			Log.d("Debug", e.getLocalizedMessage());
		}

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
			StringBuilder builder = new StringBuilder();
			String currentline = null;

			while((currentline = reader.readLine()) != null)
			{
				builder.append(currentline + "\n");
			}
			inputstream.close();
			result=builder.toString();
		}
		catch(IOException e)
		{
			Log.d("Debug", e.getMessage());
			Log.d("Debug", e.getLocalizedMessage());
		}
		try
		{
			jArray = new JSONObject(result);
		}
		catch(JSONException e)
		{
			Log.d("Debug", e.getMessage());
			Log.d("Debug", e.getLocalizedMessage());
		}

		return jArray;
	}

}
