package com.nerdery.java;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MoviesDataSource 
{
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumns = {DBHelper.columnID, DBHelper.columnTitle, DBHelper.columnRating, DBHelper.columnRuntime, DBHelper.columnSynopsis,DBHelper.columnCast, DBHelper.columnCriticsScore, DBHelper.columnThumbPath, DBHelper.columnPosterPath, DBHelper.columnMovieLink};

	public MoviesDataSource(Context context)
	{
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}

	public void close()
	{
		dbHelper.close();
	}

	public Movie addFavoriteMovie(String title, String rating, int runtime, String synopsis, String caststring, int criticscore, String thumbpath, String posterpath, String movielink)
	{
		Cursor result = database.query(DBHelper.moviesTable, allColumns, DBHelper.columnTitle + " = " + "'" + title + "'", null, null, null, null);
		
		if(result.getCount() == 0)
		{
			ContentValues tablevalues = new ContentValues();
			tablevalues.put(DBHelper.columnTitle, title);
			tablevalues.put(DBHelper.columnRating, rating);
			tablevalues.put(DBHelper.columnRuntime, runtime);
			tablevalues.put(DBHelper.columnSynopsis, synopsis);
			tablevalues.put(DBHelper.columnCast, caststring);
			tablevalues.put(DBHelper.columnCriticsScore, criticscore);
			tablevalues.put(DBHelper.columnThumbPath, thumbpath);
			tablevalues.put(DBHelper.columnPosterPath, posterpath);
			tablevalues.put(DBHelper.columnMovieLink, movielink);

			long insertID = database.insert(DBHelper.moviesTable, null, tablevalues);
			Movie movie = new Movie(insertID, title, rating, runtime, synopsis, caststring, criticscore, thumbpath, posterpath, movielink);
			Cursor cursor = database.query(DBHelper.moviesTable, allColumns, DBHelper.columnID + " = " + insertID, null, null, null, null);
			cursor.moveToFirst();
			cursor.close();
			return movie;
		}
		else
			return null;
	}

	public void removeFavoriteMovie(Movie movie)
	{
		long id = movie.getID();
		database.delete(DBHelper.moviesTable, DBHelper.columnID + " = " + id, null);
	}
	
	public void removeFavoriteMovie(String title)
	{
		database.delete(DBHelper.moviesTable, DBHelper.columnTitle + " = " + "'" + title + "'", null);
	}

	public List<Movie> getAllFavorites()
	{
		List<Movie> favorites = new ArrayList<Movie>();

		Cursor cursor = database.query(DBHelper.moviesTable, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{

			try
			{
				long currentID = cursor.getLong(0);
				String title = cursor.getString(1);
				String rating = cursor.getString(2);
				int runtime = cursor.getInt(3);
				String synopsis = cursor.getString(4);
				String cast = cursor.getString(5);
				int score = cursor.getInt(6);
				String thumb = cursor.getString(7);
				String poster = cursor.getString(8);
				String movielink = cursor.getString(9);

				Movie movie = new Movie(currentID, title, rating, runtime,synopsis, cast, score, thumb, poster, movielink);
				favorites.add(movie);
			}
			catch(SQLException e)
			{
				Log.d("Debug", e.getMessage());
			}

			cursor.moveToNext();
		}

		cursor.close();
		return favorites;
	}


}
