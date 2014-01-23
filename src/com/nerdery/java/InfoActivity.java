package com.nerdery.java;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends Activity
{
	private MoviesDataSource datasource;

	TextView textviewCast;
	TextView textviewSynopsis;
	TextView textviewMovieData;
	TextView textviewTitle;
	ImageView imageviewPoster;

	String title;
	String mpaarating;
	int runtime;
	String synopsis;
	int criticsscore;
	String[] cast;
	String posterPath;
	Bitmap posterimage;
	String thumbPath;
	String caststring;
	String movielink;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movieinfo);
		Bundle b = getIntent().getExtras();
		initVars(b);
		initWidgets();
		datasource = new MoviesDataSource(this);
		datasource.open();
	}

	private void initVars(Bundle b)
	{

		title = b.getString("TITLE");
		mpaarating = b.getString("MPAARATING");
		runtime = b.getInt("RUNTIME");
		synopsis = b.getString("SYNOPSIS");
		criticsscore = b.getInt("CRITICSCORE");
		cast = b.getStringArray("CAST");
		caststring = b.getString("CASTSTRING");
		posterPath = b.getString("POSTER");
		thumbPath = b.getString("THUMB");
		movielink = b.getString("LINK");

	}

	private void initWidgets()
	{
		textviewTitle = (TextView)findViewById(R.id.textview_title);
		textviewTitle.setText(title);

		textviewSynopsis = (TextView)findViewById(R.id.textview_synopsis_data);
		textviewSynopsis.setText(synopsis);

		textviewCast = (TextView)findViewById(R.id.textview_cast_data);
		if(cast !=null)
		{
			textviewCast.setText(transformCast(cast));
		}
		else
		{
			textviewCast.setText(caststring);
		}

		textviewMovieData = (TextView)findViewById(R.id.textview_movie_data);
		textviewMovieData.setText("Rated:" + mpaarating + "  " + "Freshness:" + Integer.valueOf(criticsscore).toString()  + "  " + transformRuntime(runtime));

		imageviewPoster = (ImageView)findViewById(R.id.imageview_poster);
		imageviewPoster.setImageBitmap(retrievePosterBitmap(posterPath, title + "detailed"));
	}

	private Bitmap retrievePosterBitmap(String filepath, String title)
	{	
		try
		{
			String cleanTitle = title.replaceAll("[^\\p{Alpha}\\p{Digit}]+","");
			File file = new  File(filepath + "/" + cleanTitle + ".png");
			if(file.exists())
			{
				Bitmap poster = BitmapFactory.decodeFile(file.getAbsolutePath());
				return poster;
			}
			return null;
		}
		catch(NullPointerException e)
		{
			Log.d("Debug", e.getLocalizedMessage());
			return null;
		}
	}

	private String transformCast(String[] cast)
	{
		String finalCast = null;
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<cast.length; i++)
		{
			builder.append(cast[i] + "\n");
		}
		finalCast = builder.toString();
		caststring = finalCast;
		return finalCast;
	}

	private String transformRuntime(int runtime)
	{	
		int minutes = runtime % 60;
		int hours = runtime / 60;

		return ("Runtime:" + Integer.valueOf(hours).toString() + "Hrs " + Integer.valueOf(minutes).toString() + "Min");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case R.id.tweet_movie:
		{
			final EditText edittext = new EditText(this);

			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setMessage("Type your message")
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					try
					{
						Editable message = edittext.getText();
						String text = message.toString();
						String tweetUrl = "https://twitter.com/intent/tweet?text="+text+"&url="+ movielink;
						Uri uri = Uri.parse(tweetUrl);
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}
					catch(NullPointerException e)
					{
						Log.d("Debug", e.getLocalizedMessage());
						Toast msg = Toast.makeText(InfoActivity.this, "Tweet could not be posted at this time", Toast.LENGTH_LONG);
						msg.show();
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					dialog.cancel();
				}
			});
			AlertDialog alert = alertDialog.create();
			alert.setTitle("Tweet about it");
			alert.setIcon(R.drawable.twitter);
			alert.setView(edittext);
			alert.show();

			break;
		}
		case R.id.add_favorite:
		{
			try
			{
				Movie result = new Movie();
				result = datasource.addFavoriteMovie(title, mpaarating, runtime, synopsis, caststring, criticsscore, thumbPath, posterPath, movielink);
				if(result != null)
				{
					Toast msg = Toast.makeText(InfoActivity.this, "Saved: " + title, Toast.LENGTH_LONG);
					msg.show();
				}
				else
				{
					Toast msg = Toast.makeText(InfoActivity.this, "This movie is already a favorite", Toast.LENGTH_LONG);
					msg.show();
				}
			}
			catch(SQLiteException e)
			{
				Log.d("Debug", e.getLocalizedMessage());
				Toast msg = Toast.makeText(InfoActivity.this, "Movie could not be saved at this time " , Toast.LENGTH_LONG);
				msg.show();
			}
			break;
		}
		case R.id.remove_favorite:
		{
			try
			{
				datasource.removeFavoriteMovie(title);
			}
			catch(SQLException e)
			{
				Log.d("Debug", e.getLocalizedMessage());
				Toast msg = Toast.makeText(InfoActivity.this, "Movie could not be deleted at this time", Toast.LENGTH_LONG);
				msg.show();
			}
			break;
		}

		}
		return true;
	}

	@Override
	protected void onResume() 
	{
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() 
	{
		datasource.close();
		super.onPause();
	}
}
