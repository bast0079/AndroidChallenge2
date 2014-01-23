package com.nerdery.java;

import android.graphics.Bitmap;

public class Movie 
{
	private String moviename;
	private String rating;
	private String synopsis;
	private String[] cast;
	private int runtime;
	private int criticscore;
	private Bitmap posterBitmap;
	private String posterPath;
	private long ID;
	private String thumbPath;
	private String caststring;
	private String movielink;
	
	public Movie(){}
	
	public Movie(long id, String title, String rating, int runtime, String synopsis, int score, Bitmap poster)
	{
		this.ID = id;
		this.moviename = title;
		this.rating = rating;
		this.runtime = runtime;
		this.synopsis = synopsis;
		this.criticscore = score;
		this.posterBitmap = poster;
	}
	
	public Movie(long id, String title, String rating, int runtime, String synopsis, String cast, int score, String thumbpath, String posterpath, String movielink)
	{
		this.ID = id;
		this.moviename = title;
		this.rating = rating;
		this.runtime = runtime;
		this.synopsis = synopsis;
		this.caststring = cast;
		this.criticscore = score;
		this.thumbPath = thumbpath;
		this.posterPath = posterpath;
		this.movielink = movielink;
	}
	
	public int getRuntime()
	{
		return runtime;
	}
	
	public void setRuntime(int runtime)
	{
		this.runtime = runtime;
	}
	
	public String getSynopsis()
	{
		return synopsis;
	}
	
	public void setSynopsis(String synopsis)
	{
		this.synopsis = synopsis;
	}
	
	public String[] getCast()
	{
		return cast;
	}
	
	public void setCaststring(String cast)
	{
		this.caststring = cast;
	}
	
	public String getCaststring()
	{
		return caststring;
	}
	
	public void setCast(String[] cast)
	{
		this.cast = cast;
	}
	
	public String getMovieName()
	{
		return moviename;
	}
	
	public void setMovieName(String moviename)
	{
		this.moviename = moviename;
	}
	
	public String getMPAARating()
	{
		return rating;
	}
	
	public void setMPAARating(String rating)
	{
		this.rating = rating;
	}
	
	public int getCriticScore()
	{
		return criticscore;
	}
	
	public void setCriticScore(int criticscore)
	{
		this.criticscore = criticscore;
	}
	
	public Bitmap getPosterBitmap()
	{
		return posterBitmap;
	}
	
	public void setPosterBitmap(Bitmap poster)
	{
		this.posterBitmap = poster;
	}
	
	public String getThumbPath()
	{
		return thumbPath;
	}
	
	public void setThumbPath(String thumbpath)
	{
		this.thumbPath = thumbpath;
	}
	
	public String getPosterPath()
	{
		return posterPath;
	}
	
	public void setPosterPath(String poster)
	{
		this.posterPath = poster;
	}
	
	public String getMovieLink()
	{
		return movielink;
	}
	
	public void setMovieLink(String link)
	{
		this.movielink = link;
	}
	
	public long getID()
	{
		return ID;
	}
	
	public void setID(int id)
	{
		this.ID = id;
	}
	

}
