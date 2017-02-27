package com.imonje.copyfilesupdater;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;


public class FileUtils {


	/**
	 * Copia el fichero origen en el fichero destino
	 * 
	 * @param orig Fichero de origen (debe existir)
	 * @param dest Fichero destino (si no existe, lo crea)
	 * @param overwrite Para indicar si hay que sobreescribir el fichero destino en caso de que exista
	 * 
	 * @return <code>true</code> si todo ha ido bien, <code>false</code> en caso contrario
	 */
	public static boolean copyFile(File orig, File dest, boolean overwrite){
		
		// Comprobamos si hay que sobreescribir
		System.out.println(">>>>COPY ["+orig.getAbsolutePath()+"]\n >>>>TO ["+dest.getAbsolutePath()+"] ...");
		if ( dest.exists() ){
			if(overwrite){
				//FileUtils.deleteFile(dest);
			} else{
				return true;
			}
		}
		
		// Inicializamos variables
		boolean result = false;
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		FileChannel source = null;
		FileChannel destination = null;
    	
    	try{
    		//Si el fichero de origen no existe, devolvemos false
    		if (!orig.exists() ) return result;
    		//Si el fichero de destino no existe, se crea
    		if ( !dest.exists() ){
    			dest.getParentFile().mkdirs();
    			dest.createNewFile();
    		}

    		fileInputStream = new FileInputStream(orig);
			source = fileInputStream.getChannel();
			fileOutputStream = new FileOutputStream(dest);
			destination = fileOutputStream.getChannel();
	        // Nº mágico en Windows (64Mb - 32Kb)
	        int maxCount = (64 * 1024 * 1024) - (32 * 1024);
	        long size = source.size();
	        long position = 0;
	        while (position < size) {
	        	position += source.transferTo(position, maxCount, destination);
	        }
    		
	        result = true;
		} catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error al copiar "+orig.getAbsolutePath()+" en "+dest.getAbsolutePath());
			result = false;
		} finally {
			close(fileInputStream);
			close(fileOutputStream);
			close(source);
			close(destination);
		}
		
    	return result;
	}
	
	/**
	 * Copia todo el contenido de la carpeta <code>orig_folder</code> a la carpeta <code>dest_folder</code>
	 * 
	 * @param orig_folder - Ruta origen
	 * @param dest_folder - Ruta destino
         * @return devuelve true si se copia correctamente, false en caso contrario
	 */
	public static boolean copyContent(File orig_folder, File dest_folder){		
		//Comprobamos que exista la ruta de origen
		if(!orig_folder.exists()){
			return false;
		}
		//Creamos la ruta destino
		dest_folder.mkdirs();
		if(!dest_folder.exists()){
			return false;
		}
		
		//Obtenemos el contenido de la ruta de origen
		File content[] = orig_folder.listFiles();
		for(int i=0; i<content.length; i++){
			
			File ini = content[i];
			File fin = new File(dest_folder.getAbsolutePath()+File.separator+ini.getName());
			if(ini.isDirectory()){
				//Si es un directorio, lo creamos y movemos el contenido
				fin.mkdirs();
				copyContent(ini,fin);
			} else{
				//Si es un fichero, lo copiamos tal cual
				FileUtils.copyFile(ini, fin, true);
			}
		}
		
		return true;
	}
	
        
        /**
	 * Copia todo el contenido de la carpeta <code>orig_folder</code> a la carpeta <code>dest_folder</code>
	 * 
	 * @param source - Ruta origen
	 * @param destiny - Ruta destino
         * @return devuelve true si se copia correctamente, false en caso contrario
	 */
	public static boolean copyContent(String source, String destiny){
            return copyContent(new File(source), new File(destiny));
        }
        
	/**
	 * Obtiene el nombre de un fichero sin la extensión
	 * @param filename String con el nombre del fichero a analizar
	 * @return El nombre del fichero sin extensión
	 */
	public static String removeExtension(String filename) {
		int pos = filename.lastIndexOf(".");
		if (pos == -1) {
			return filename;
		}
		return filename.substring(0, pos);
	}

	/**
	 * Devuelve la extensión de un fichero
	 * @param filename String con el nombre del fichero a analizar
	 * @return Extensión del fichero (sin el 'punto'), o una cadena vacía si no se ha podido obtener la extensión.
	 */
	public static String getExtension(String filename) {
		int pos = filename.lastIndexOf(".");
		if ((pos == -1) || (pos == (filename.length()-1))) {
			return "";
		}
		return filename.substring(pos+1, filename.length());
	}
	
        /**
	 * Método para cerrar un descriptor de fichero
	 * @param closeable Elemento a cerrar
	 */
	public static void close(Closeable closeable) {
	    try {
	    	if(closeable!=null) closeable.close();
	    } catch(IOException ignored) { }
	}
        
	/**
	 * Ejecuta un programa externo llamando al sistema operativo.
	 * 
	 * @param command Cadena con el comando a ejecutar
	 * @return <code>true</code> si la ejecución es correcta, <code>false</code> si se produce algún error
	 */
	public static boolean executeCommand(String command) {
		try{
			System.out.println("Ejecutar: "+command);
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String linea = null;
			while ((linea = br.readLine())!=null) {
				System.out.println(linea);
			}
			br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			if (br.ready()) {
				while ((linea = br.readLine())!=null) {
					System.err.println(linea);
				}				
			}
			return (process.waitFor() == 0);
		} catch(Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Duplica todas las barras ('/','\') transformándolas en una barra doble ('\\'). Para filtrar
	 * nombres de archivo en llamadas a la utilidad exiftool.
	 * 
	 * @param str Cadena a filtrar
	 * @return Cadena filtrada con barras dobles.
	 */
	public static String doubleSlashes(String str) {
		return str.replace("/","\\").replace("\\","\\\\");
	}
	
	
}
