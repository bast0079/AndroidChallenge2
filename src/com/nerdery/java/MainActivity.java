package com.nerdery.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity  
{
	public ArrayList<Movie> moviesarray = null;
	public MovieAdapter movieAdapter;
	private static Dialog progressDialog;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listplaceholder);

		if(isNetworkAvailable() == true)
		{
			context = MainActivity.this;
			moviesarray = new ArrayList<Movie>();
			progressDialog = new Dialog(MainActivity.this);

			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setContentView(R.layout.progress_dialog);
			TextView message = (TextView)progressDialog.findViewById((R.id.textView_dialog_message));
			message.setText(R.string.welcome);
			try
			{
				new downloadMoviesTask().execute();
			}
			catch(IllegalStateException e)
			{
				Log.d("Debug", e.getLocalizedMessage());
			}
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.no_connection);
			builder.setMessage(R.string.view_favorites);
			builder.setCancelable(false);
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
					startActivity(intent);
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) 
				{
					dialog.cancel();
					MainActivity.this.finish();
				}
			});

			AlertDialog alert = builder.create();
			alert.show(); 
		}
	}
	
	private class downloadMoviesTask extends AsyncTask<String, Integer, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) 
		{
			getMovies();
			return "Complete";
		}
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			progressDialog.dismiss();
			movieAdapter = new MovieAdapter(MainActivity.this, R.layout.row, moviesarray);
			setListAdapter(movieAdapter);
		}
	}

	private void getMovies()
	{
		try
		{
			JSONObject json = JsonFunctions.getJSONfromURL(context.getString(R.string.json_string));
			JSONArray movies = json.getJSONArray("movies");

			for(int i=0; i<10; i++)
			{
				Movie movie = new Movie();

				JSONObject topten = movies.getJSONObject(i);
				JSONObject critic = topten.getJSONObject("ratings");
				JSONObject image = topten.getJSONObject("posters");
				JSONObject links = topten.getJSONObject("links");
				JSONArray cast = topten.getJSONArray("abridged_cast");

				String[] castmembers = new String[cast.length()];
				for(int j=0; j<cast.length() ; j++)
				{
					JSONObject actor = cast.getJSONObject(j);
					JSONArray roles = actor.getJSONArray("characters");
					castmembers[j] = actor.getString("name") + " as " + roles.getString(0);
				}

				String title = topten.getString("title");
				String mpaarating = topten.getString("mpaa_rating");
				String synopsis = topten.getString("synopsis");

				int criticscore = critic.getInt("critics_score");
				int runtime = topten.getInt("runtime");
				
				String movielink = links.getString("alternate");
				
				String thumbnail = image.getString("thumbnail");
				Bitmap posterThumbnail = getImage(thumbnail);
				
				String poster = image.getString("detailed");
				Bitmap posterDetailed = getImage(poster);
				
				String thumbpath = downloadToSDCard(posterThumbnail, title + "thumb");
				String detailedpath = downloadToSDCard(posterDetailed, title + "detailed");
				
				movie.setMovieName(title);
				movie.setMPAARating(mpaarating);
				movie.setCriticScore(criticscore);
				movie.setPosterBitmap(posterThumbnail);
				movie.setSynopsis(synopsis);
				movie.setRuntime(runtime);
				movie.setCast(castmembers);
				movie.setThumbPath(thumbpath);
				movie.setPosterPath(detailedpath);
				movie.setMovieLink(movielink);
				moviesarray.add(movie);
			}
		}
		catch(JSONException e)
		{
			Log.d("Debug", e.getLocalizedMessage());
		}
	}

	private String downloadToSDCard(Bitmap posterImage, String title) 
	{
		try 
		{
			String cleanTitle = title.replaceAll("[^\\p{Alpha}\\p{Digit}]+","");
			String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + 
					"/NerdMovies/" + cleanTitle;
			File dir = new File(filepath);
			if(!dir.exists())
				dir.mkdirs();
			File file = new File(dir, cleanTitle + ".png");
			FileOutputStream out = new FileOutputStream(file);

			posterImage.compress(Bitmap.CompressFormat.PNG, 85, out);
			out.flush();
			out.close();
			
			return filepath;
		}
		//NullPointerException,FileNotFoundException, IOException
		catch(Exception e)
		{
			Log.d("Debug", e.getLocalizedMessage());
		}
		return null;
	}

	private Bitmap getImage(String url)
	{
		URL myFileUrl =null; 
		Bitmap poster = null;
		try 
		{
			myFileUrl= new URL(url);
		}
		catch (MalformedURLException e) 
		{
			Log.d("Debug", e.getLocalizedMessage());
		}

		try 
		{
			HttpURLConnection conn= (HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();

			InputStream is = conn.getInputStream();
			poster = BitmapFactory.decodeStream(is);

		}
		//IllegalAccessException or IOException
		catch (Exception e) 
		{
			Log.d("Debug", e.getLocalizedMessage());
		}

		return poster;
	}

	private class MovieAdapter extends ArrayAdapter<Movie> 
	{
		private ArrayList<Movie> items;

		public MovieAdapter(Context context, int textViewResourceId, ArrayList<Movie> items) 
		{
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) 
		{	
			if (view == null) 
			{
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.row, null);
			}

			Movie movie = items.get(position);
			if (movie != null) 
			{
				TextView title = (TextView) view.findViewById(R.id.title);
				ProgressBar bar = (ProgressBar)view.findViewById(R.id.progressBarFreshness);
				ImageView posterimage = (ImageView)view.findViewById(R.id.poster);
				ImageView rating = (ImageView)view.findViewById(R.id.rating);
				String mpaa = movie.getMPAARating();

				if (title != null) 
					title.setText(movie.getMovieName()); 
				if(bar != null)
					bar.setProgress(movie.getCriticScore());
				if(posterimage != null)
					posterimage.setImageBitmap(movie.getPosterBitmap());
				if(rating != null)
				{
					if(mpaa.equals("G"))
						rating.setImageResource(R.drawable.g);
					if(mpaa.equals("PG"))
						rating.setImageResource(R.drawable.pg);
					if(mpaa.equals("PG-13"))
						rating.setImageResource(R.drawable.pg_13);
					if(mpaa.equals("R"))
						rating.setImageResource(R.drawable.r);
					if(mpaa.equals("NC-17"))
						rating.setImageResource(R.drawable.nc_17);
				}
				if (position % 2 == 1) 
					view.setBackgroundColor(getResources().getColor(R.color.listcolor1));  
				else 
					view.setBackgroundColor(getResources().getColor(R.color.listcolor2));  
			}
			return view;
		}
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) 
	{
		super.onListItemClick(list, view, position, id);
		String syn = moviesarray.get(position).getSynopsis();
		String title = moviesarray.get(position).getMovieName();
		int run = moviesarray.get(position).getRuntime();
		String[] cast = moviesarray.get(position).getCast();
		int score = moviesarray.get(position).getCriticScore();
		String rating = moviesarray.get(position).getMPAARating();
		String posterPath = moviesarray.get(position).getPosterPath();
		String thumbPath = moviesarray.get(position).getThumbPath();
		String link = moviesarray.get(position).getMovieLink();
		
		Intent intent = new Intent(MainActivity.this, InfoActivity.class);
		Bundle b = new Bundle();
		b.putString("TITLE", title);
		b.putString("MPAARATING", rating);
		b.putInt("RUNTIME", run);
		b.putString("SYNOPSIS", syn);
		b.putInt("CRITICSCORE", score);
		b.putStringArray("CAST", cast);
		b.putString("POSTER", posterPath);
		b.putString("THUMB", thumbPath);
		b.putString("LINK", link);
		
		intent.putExtras(b);
		
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case R.id.viewfavorites: 
		{
			Intent favoritesIntent = new Intent(MainActivity.this, FavoritesActivity.class);
			startActivity(favoritesIntent);
		}

		}
		return true;
	}

	private boolean isNetworkAvailable() 
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetworkInfo != null);
	}
}