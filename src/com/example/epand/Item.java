/**
 * @author Cristina Sánchez Ruiz
 */

package com.example.epand;

//Clase para crear un item personalizado para añadirlo a un ListView.
public class Item {
	
	  //Declaramos las variables que contendrán:
	  //Id del item
	  //Nombre de la imagen que queremos añadir a un ImageView
	  //Texto que queremos mostrar en un TextView
	  protected int id;
	  protected String nombreImagen="", nombre="";
	         	         
	  public Item(int id, String nombre, String nombreImagen) {
	    this.id = id;
	    this.nombre = nombre;
	    this.nombreImagen = nombreImagen;
	  }
	  	  
	  //A continuación se declaran los métodos get/set para cada variable.
	  public long getId() {
	    return id;
	  }
	     
	  public void setId(int id) {
	    this.id = id;
	  }
	     
	  public String getNombreImagen() {
	    return nombreImagen;
	  }
		     
	  public void setNombreImagen(String nombreImagen) {
	    this.nombreImagen = nombreImagen;
	  }
	     
	  public String getNombre() {
	    return nombre;
	  }
	     
	  public void setNombre(String nombre) {
	    this.nombre = nombre;
	  }
	}
