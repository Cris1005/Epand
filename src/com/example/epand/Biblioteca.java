/**
 * @author Cristina S�nchez Ruiz
 */

package com.example.epand;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.dropbox.android.sample.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class Biblioteca extends Activity implements OnItemSelectedListener, OnItemClickListener{
		
	Spinner ordenar;//Spinner que contendr� las opciones de ordenaci�n.
	ListView lvLibros;//ListView que mostrar� cada uno de los libros.
	//ArrayList que contendr� [nombreEbook, fechaCreacion] para cada ebook.
	ArrayList<String[]> Ebooks = new ArrayList<String[]>();
	String nameEbooks[], revEbooks[];
	int libros=0;//N�mero de ebooks que tenemos.
	ArrayList<Item> items;//ArrayList de Items.
	ItemAdapter adapter;//Adaptador que usaremos con el ListView.
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_biblioteca);
        //Recuperamos nuestros widgets del layout
        lvLibros = (ListView) findViewById(R.id.lvLibros);
        lvLibros.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvLibros.setOnItemClickListener(this); 
        ordenar = (Spinner) findViewById(R.id.ordenar);
        ordenar.setOnItemSelectedListener(this);
        getExtras();//Obtenemos la informaci�n de la otra Actividad (Main)
        infoEbooks();//Extraemos la informaci�n de los ebooks.    
        rellenarSpinner();//Rellenamos el Spinner.
	}
	
	/**
	 * M�todo que obtiene los datos del Intent que proviene de la clase Main
	 */
	private void getExtras(){
		Bundle extras = getIntent().getExtras();
		nameEbooks = extras.getStringArray("nameEbooks");
		revEbooks = extras.getStringArray("revEbooks");
		libros = nameEbooks.length;
	}
	
	/**
	 * M�todo que rellenar el Spinner con los m�todos de ordenaci�n que queremos.
	 */
	private void rellenarSpinner(){
    	String array[] = new String[2];
    	array[0] = "Ordenar por t�tulo";
    	array[1] = "Ordenar por fecha de creaci�n";
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	ordenar.setAdapter(adapter);
    }
	
	/**
	 * M�todo que nos permite rellenar el ListView con los datos de los Ebooks que tenemos.
	 */
	private void rellenarListView(){
		items = new ArrayList<Item>();
		//Para cada ebook...
		for(int i=0;i<libros;i++){
			String info[] = Ebooks.get(i);
			//Obtenemos el nombre del ebook y su fecha de creaci�n.
			String name = info[0], rev = info[1];
			//Formamos el item con los datos que queremos.
			items.add(new Item(i,name+" - "+rev , "ebook1"));
		} 
		adapter = new ItemAdapter(Biblioteca.this, items); 
        lvLibros.setAdapter(adapter);
	}
		
	/**
	 * Formamos una lista de Ebooks.
	 * Cada Ebook contendr� su t�tulo y fecha de creaci�n.
	 */
	private void infoEbooks(){
		String info[];
		for(int i=0;i<libros;i++){
			info = new String[2];
			info[0]=nameEbooks[i];
			info[1]=revEbooks[i];
			Ebooks.add(info);
		}
	}
	
	/**
	 * M�todo que nos permite reordenar los Ebooks por nombre.
	 */
	private void reordenarPorNombre(){
		//Ordenamos los ebooks por nombre.
		for(int j=0;j<Ebooks.size();j++){
			for (int i=j+1 ; i<Ebooks.size(); i++){
				 String info[] = Ebooks.get(i);
				 String info1[] = Ebooks.get(j);
		         if(recortarCadena(info[0]).compareTo(recortarCadena(info1[0]))<0){
		             String temp[]= Ebooks.get(j);
		             Ebooks.set(j, Ebooks.get(i));
		             Ebooks.set(i, temp);
		         }
		     }		      
		}
		//Actualizamos el ListView.
		rellenarListView();
		adapter.notifyDataSetChanged(); 
        lvLibros.invalidateViews();
	}
	
	/**
	 * M�todo para obtener el nombre del archivo sin la extensi�n.
	 * @param s - String al que le queremos quitar la extensi�n.
	 * @return - String sin extensi�n.
	 */
	private String recortarCadena(String s){
		String c = s.substring(0, s.length()-5);
		return c;
	}
	
	/**
	 * M�todo que nos permite reordenar los Ebooks por fecha de creaci�n.
	 */
	private void reordenarPorFecha(){
		//Reordenamos los ebooks por fecha.
		for(int j=0;j<Ebooks.size();j++){
			for (int i=j+1 ; i<Ebooks.size(); i++){
				 String info[] = Ebooks.get(i);
				 String info1[] = Ebooks.get(j);
		         if(StringDate(info[1]).compareTo(StringDate(info1[1]))<0){
		             String temp[]= Ebooks.get(j);
		             Ebooks.set(j, Ebooks.get(i));
		             Ebooks.set(i, temp);
		         }
		     }		      
		}
		//Actualizamos el ListView.
		rellenarListView();
		adapter.notifyDataSetChanged(); 
        lvLibros.invalidateViews();
	}
	
	/**
	 * M�todo que permite transformar una fecha (String) a Date.
	 * @param fecha - Fecha en tipo String.
	 * @return - Fecha en tipo Date.
	 */
	public Date StringDate(String fecha){
		String pattern = "ccc, dd MMM yyyy HH:mm:ss";
		Date date = null;
		try{
			date = new SimpleDateFormat(pattern, Locale.US).parse(fecha);
        } catch (java.text.ParseException ex) {}
		return date;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Intent I = new Intent(this, Portada.class);
		String info[] = Ebooks.get(position);
		I.putExtra("nombreEbook", recortarCadena(info[0]));
		this.startActivity(I);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		//Si en el Spinner seleccionamos el primer item reordenamos por nombre.
		if(position==0){
			reordenarPorNombre();
		//Sino reordenamos por fecha.
		}else{
			reordenarPorFecha();
		}	
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}	
}
