package com.nerdery.java;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FavoritesActivity extends ListActivity
{
	private MoviesDataSource datasource;
	private ArrayList<Movie> favoritemovies; 
	@Override
	public void onCreate(Bundle sis)
	{
		super.onCreate(sis);
		setContentView(R.layout.listplaceholder);
		datasource = new MoviesDataSource(this);
		datasource.open();

		favoritemovies = (ArrayList<Movie>) datasource.getAllFavorites();
		MovieAdapter movieAdapter = new MovieAdapter(FavoritesActivity.this, R.layout.row, favoritemovies);
		setListAdapter(movieAdapter);
		
		setLongClickListener();
	}
	
	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) 
	{
		super.onListItemClick(list, view, position, id);
		String syn = favoritemovies.get(position).getSynopsis();
		String title = favoritemovies.get(position).getMovieName();
		int run = favoritemovies.get(position).getRuntime();
		String cast = favoritemovies.get(position).getCaststring();
		int score = favoritemovies.get(position).getCriticScore();
		String rating = favoritemovies.get(position).getMPAARating();
		String posterPath = favoritemovies.get(position).getPosterPath();
		String thumbPath = favoritemovies.get(position).getThumbPath();
		String movielink = favoritemovies.get(position).getMovieLink();
		
		Intent intent = new Intent(FavoritesActivity.this, InfoActivity.class);
		Bundle b = new Bundle();
		b.putString("TITLE", title);
		b.putString("MPAARATING", rating);
		b.putInt("RUNTIME", run);
		b.putString("SYNOPSIS", syn);
		b.putInt("CRITICSCORE", score);
		b.putString("CASTSTRING", cast);
		b.putString("POSTER", posterPath);
		b.putString("THUMB", thumbPath);
		b.putString("LINK", movielink);
		
		intent.putExtras(b);
		
		startActivity(intent);
	}

	private void setLongClickListener() 
	{	
		ListView lv = getListView();
		lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@SuppressWarnings("unchecked")
			public boolean onItemLongClick(AdapterView<?> adapter, View v,   final int position, long id) 
			{
				final ArrayAdapter<Movie> currentAdapter = (ArrayAdapter<Movie>) getListAdapter();
				final AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
				builder.setMessage("Would you like to remove this favorite from your list?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						if(getListAdapter().getCount() > 0)
						{
							Movie movie = currentAdapter.getItem(position);
							datasource.removeFavoriteMovie(movie);
							currentAdapter.remove(movie);
							currentAdapter.notifyDataSetChanged();
						}
					}
				});

				builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
					}
				});

				final AlertDialog alert = builder.create();
				alert.show();

				return true;
			}
		});
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
					posterimage.setImageBitmap(retrievePosterBitmap(movie.getThumbPath(), movie.getMovieName() + "thumb"));
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

	private Bitmap retrievePosterBitmap(String filepath, String title)
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
