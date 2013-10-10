/**
 * @author Cristina Sánchez Ruiz
 */

package com.example.epand;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.dropbox.client2.DropboxAPI.Entry;

import android.util.Log;
import android.widget.Toast;

import com.dropbox.android.sample.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class Main extends Activity {
	
	final private String TAG = "Epand";

    //App Key y App Secret asignadas por Dropbox.
    final static private String APP_KEY = "2am9pecdzbwii2f";
    final static private String APP_SECRET = "l1popcb8a4cwo0m";

    //Tipo de acceso que queremos tener a Dropbox.
    final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

    DropboxAPI<AndroidAuthSession> mApi; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Creamos una nueva sesión para usar la API de Dropbox.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        mApi.getSession().startAuthentication(Main.this);
        //Ejecutamos download para buscar los libros. 
        Descargas download = new Descargas(this);
        download.execute();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();
        //Para completar la autorización correctamente...
        if (session.authenticationSuccessful()) {
            try {
                //Llamada obligatoria para completar la autorización.
                session.finishAuthentication();
            } catch (IllegalStateException e) {
                mostrarToast("No se pudo autentificar con Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error autentificando", e);
            }
        }
    }

    private void mostrarToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Construimos la sesión para conectar con Dropbox.
     * @return - sesión construida.
     */
    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);//Par de llaves
        AndroidAuthSession session;
        session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        return session;
    }
    
    //Clase asíncrona utilizada para buscar los archivos .epub.
    class Descargas extends AsyncTask<Void, Long, Boolean> {

    	private Context mContext;
        private final ProgressDialog mDialog;
        private ArrayList<Entry> libros = new ArrayList<Entry>();
        private int ebooks=0;
        private Long mFileLen;
        private String mErrorMsg;

        public Descargas(Context context) {
            mContext = context.getApplicationContext();
            //Preparamos el ProgressDialog
            mDialog = new ProgressDialog(context);
            mDialog.setMessage("Descargando Ebooks"); 
            mDialog.setCancelable(false);
            mDialog.show();
        }

        /**
         * Método que nos permitirá recorrer un directorio.
         * @param directorio - Directorio que queremos examinar.
         */
        private void recorrer(Entry directorio){
        	//Recorremos el directorio en busca de archivos .epub
            for(int i=0;i<directorio.contents.size();i++){
            	//Si es un directorio --> entramos en él para recorrerlo
            	if(directorio.contents.get(i).isDir==true){
            		//Obtenemos el nombre del directorio
            		String mPath = directorio.contents.get(i).fileName();
            		try {
            			//Almacenamos los metadatos del directorio
    					Entry dir = mApi.metadata("/"+mPath, 1000, null, true, null);
    					//Y si no está vacío entramos en él para recorrerlo
    					if (dir != null) {
    						recorrer(dir);
    					}					
    				} catch (DropboxException e) {}     	
            	}
            	//Si es un archivo --> miramos si tiene extensión .epub
            	else{
            		if(directorio.contents.get(i).fileName().endsWith(".epub")){
            			libros.add(directorio.contents.get(i));
            			ebooks++;
            		}
            	}
            }
            mErrorMsg = "Ebooks encontrados:"+ebooks;
        }
        
        @Override
        protected Boolean doInBackground(Void... params) {
            try {               
                //Obtenemos los metadatos del directorio principal
                Entry dirent = mApi.metadata("/.", 1000, null, true, null);
                //Si el directorio no está vacío --> lo recorremos
                if (dirent.contents != null) {
                    recorrer(dirent);
                }
                return true;

            } catch (DropboxUnlinkedException e) {
                // The AuthSession wasn't properly authenticated or user unlinked.
            } catch (DropboxPartialFileException e) {
                // We canceled the operation
                mErrorMsg = "Download canceled";
            } catch (DropboxServerException e) {
                // Server-side exception.  These are examples of what could happen,
                // but we don't do anything special with them here.
                if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                    // won't happen since we don't pass in revision with metadata
                } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                    // Unauthorized, so we should unlink them.  You may want to
                    // automatically log the user out in this case.
                } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                    // Not allowed to access this
                } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                    // path not found (or if it was the thumbnail, can't be
                    // thumbnailed)
                } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                    // too many entries to return
                } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                    // can't be thumbnailed
                } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                    // user is over quota
                } else {
                    // Something else
                }
                // This gets the Dropbox error, translated into the user's language
                mErrorMsg = e.body.userError;
                if (mErrorMsg == null) {
                    mErrorMsg = e.body.error;
                }
            } catch (DropboxIOException e) {
                // Happens all the time, probably want to retry automatically.
                mErrorMsg = "Network error.  Try again.";
            } catch (DropboxParseException e) {
                // Probably due to Dropbox server restarting, should retry
                mErrorMsg = "Dropbox error.  Try again.";
            } catch (DropboxException e) {
                // Unknown error
                mErrorMsg = "Unknown error.  Try again.";
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Long... progress) {
            int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
            mDialog.setProgress(percent);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDialog.dismiss();
            if (result) {
            	//Realizamos un Intent a la clase Biblioteca.
            	Intent I = new Intent(Main.this, Biblioteca.class);
            	//A esta clase le vamos a pasar un Array de nombres y otro con fechas de ebooks.
            	I.putExtra("nameEbooks", nameEbooks());
            	I.putExtra("revEbooks", revEbooks());
            	Main.this.startActivity(I);//Iniciamos esta actividad.
            	Main.this.finish();//Cerramos la actual.
            	showToast(mErrorMsg);
            } else {
                //Mostramos el error.
                showToast(mErrorMsg);
            }
        }
        
        /**
         * Método para mostrar un Toast por pantalla.
         * @param msg - Mensaje a mostrar.
         */
        private void showToast(String msg) {
            Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            error.show();
        }
        
        /**
         * Método que obtiene el nombre de todos los ebooks encontrados.
         * @return - Array con los nombres de los ebooks.
         */
        private String[] nameEbooks(){
        	String[] nameE = new String[libros.size()];
        	for(int i=0;i<libros.size();i++){
        		nameE[i] = libros.get(i).fileName();
        	}
        	return nameE;
        }
        
        /**
         * Método que obtiene la fecha de creación de todos los ebooks encontrados.
         * @return - Array con las fechas de creación de los ebooks.
         */
        private String[] revEbooks(){
        	String[] revE = new String[libros.size()];
        	for(int i=0;i<libros.size();i++){
        		revE[i] = (libros.get(i).modified).substring(0, libros.get(i).modified.length()-5);   		
        	}
        	return revE;
        }
        
    }
}