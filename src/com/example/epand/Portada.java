/**
 * @author Cristina Sánchez Ruiz
 */

package com.example.epand;

import java.net.HttpURLConnection;
import java.net.URL;

import com.dropbox.android.sample.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class Portada extends Activity {
	
	ImageView portada;
	String nombre = "";//Nombre del libro del que queremos su portada
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.activity_portada);
        portada = (ImageView) findViewById(R.id.portada);
        getExtras();
        Toast.makeText(this, nombre, Toast.LENGTH_LONG).show();
        try {
			cargarImagen();
		} catch (Exception e) {}
	}
	
	/**
	 * Obtenemos la información que pasamos desde Biblioteca
	 */
	private void getExtras(){
		Bundle extras = getIntent().getExtras();
		nombre = extras.getString("nombreEbook");
	}
	
	/**
	 * Método que se encarga de cargar una imagen en nuestro ImageView
	 * @throws Exception
	 */
	private void cargarImagen() throws Exception{
		//Especificamos la ruta de la imagen
		URL imageUrl = new URL("http://3.bp.blogspot.com/-JGj1Xy59h1g/UKO39-rzmpI/AAAAAAAADi0/jmp_A-YTLGk/s1600/pideme-lo-que-quieras_9788408034513.jpg");
		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
        conn.connect();
        //Decodificamos la imagen en un Bitmap
        Bitmap loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
        //Asignamos el Bitmap a nuestro ImageView
        portada.setImageBitmap(loadedImage);
	}

}
