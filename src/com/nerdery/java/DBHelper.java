package com.nerdery.java;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	public static final String moviesTable = "moviesTable";
	public static final String columnID = "_id";
	public static final String columnTitle = "title";
	public static final String columnRating = "rating";
	public static final String columnRuntime = "runtime";
	public static final String columnSynopsis = "synopsis";
	public static final String columnCast = "caststring";
	public static final String columnCriticsScore = "criticscore";
	public static final String columnThumbPath = "thumbnail";
	public static final String columnPosterPath = "poster";
	public static final String columnMovieLink = "movielink";
	private static final String dbName = "Favorites.db";
	private static final int dbVersion = 200;
	
	private static final String createDBStatement = "create table "+ moviesTable 
							+ "( " + columnID + " 			integer 	primary key autoincrement, " 
							     + columnTitle + " 			text  		null, "
							     + columnRating + " 		text  		null, "
							     + columnRuntime + " 		integer  	null, "
							     + columnSynopsis + " 		text  		null, "
							     + columnCast + "			text		null, "
							     + columnCriticsScore + " 	integer  	null, " 
							     + columnThumbPath + "      text        null, "
							     + columnPosterPath + "		text		null, "
							     + columnMovieLink + "      text        null)";

	public DBHelper(Context context) 
	{
		super(context, dbName, null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(createDBStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		db.execSQL("DROP TABLE IF EXISTS " + moviesTable);
		onCreate(db); 
	}
	
}
