/**
 * @author Cristina Sánchez Ruiz
 */

package com.example.epand;

import java.util.ArrayList;

import com.dropbox.android.sample.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//Clase con la que vamos a crear un Adapter personalizado.
public class ItemAdapter extends BaseAdapter {
	
	  protected Activity activity;//Activity desde la que lo usamos.
	  protected ArrayList<Item> items;//Lista de Items.
	         
	  public ItemAdapter(Activity activity, ArrayList<Item> items) {
	    this.activity = activity;
	    this.items = items;
	  }
	 
	  /**
	   * Devuelve la cantidad de items que contiene el adapter
	   */
	  @Override
	  public int getCount() {
	    return items.size();
	  }
	 
	  /**
	   * Devuelve el item que se encuentra en la posición que se pasa como parámetro
	   */
	  @Override
	  public Object getItem(int position) {
	    return items.get(position);
	  }
	 
	  /**
	   * Devuelve el id del item de la posición indicada
	   */
	  @Override
	  public long getItemId(int position) {
	    return items.get(position).getId();
	  }
	 
	  /**
	   * Muestra los items dentro del ListView
	   * Llamado cada vez que hay que pintar un item del ListView en la pantalla 
	   */
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View vi=convertView;	         
	    if(convertView == null) {
	      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      vi = inflater.inflate(R.layout.list_item_layout, null);
	    }
	    //Creamos el Item
	    Item item = items.get(position);
	    ImageView image = (ImageView) vi.findViewById(R.id.imagen);
	 	Drawable drawable = activity.getResources().getDrawable(activity.getResources().getIdentifier(item.getNombreImagen(), "drawable", activity.getPackageName()));
	 	image.setImageDrawable(drawable);
	 	TextView nombre = (TextView) vi.findViewById(R.id.nombre);
	 	nombre.setText(item.getNombre()); 	     
	    return vi;
	  }
}
