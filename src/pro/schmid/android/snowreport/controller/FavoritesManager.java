package pro.schmid.android.snowreport.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class FavoritesManager {

	private final static String filename = "favorites.dat";
	private static HashSet<String> favorites;
	private static FavoritesChangedListener fcl;

	@SuppressWarnings("unchecked")
	private static HashSet<String> getFavorites(Activity a) {

		if(favorites != null)
			return favorites;

		try {
			FileInputStream fis = a.openFileInput(filename);
			ObjectInputStream in = new ObjectInputStream(fis);
			
			Object o = in.readObject();
			
			if(o instanceof HashSet<?>) {
				favorites = (HashSet<String>) o;
				
				Log.d(FavoritesManager.class.toString(), favorites.toString());
			}
			
			return favorites;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		favorites = new HashSet<String>();
		
		return favorites;
	}

	public static boolean isFavorite(Activity a, String id) {
		HashSet<String> l = getFavorites(a);

		if(l == null)
			return false;
		else
			return l.contains(id);
	}

	public static void setFavorite(Activity a, String fav, boolean addIt) {
		
		HashSet<String> l = getFavorites(a);

		if(addIt) {
			l.add(fav);
		} else {
			l.remove(fav);
		}

		FileOutputStream fos;
		try {
			fos = a.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(l);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fcl.favoritesChanged();
	}

	public static int size(Activity a) {
		return getFavorites(a).size();
	}

	public static String getId(Activity a, int position) {
		
		HashSet<String> r = getFavorites(a);
		
		int i = 0;
		for(String s : r) {
			if(i++ == position)
				return s;
		}
		
		return new String();
	}

	
	public static void setFavoritesListener(FavoritesChangedListener fcl) {
		FavoritesManager.fcl = fcl;
	}

	public static void toggleFavorite(Activity a, String id) {
		setFavorite(a, id, !isFavorite(a, id));
	}
}
