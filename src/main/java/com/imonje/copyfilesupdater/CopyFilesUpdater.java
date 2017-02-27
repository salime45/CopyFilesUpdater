/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.imonje.copyfilesupdater;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imonje
 */
public class CopyFilesUpdater {
    
    private static String source;
    private static String destiny;
    
    public static void main(String[] args) {

        //Comprobamos si hay suficientes argumentos
        if (args.length >= 2) {
            //Obtenemos la ruta origen
            source = args[0];
            //Obtenemos la ruta de destino
            destiny = args[1];
            //Copiamos los archivos, si falla informamos 
            if( !FileUtils.copyContent(source, destiny) ) {
                System.out.println("No se ha podido copiar el contendio en la ruta indicada.");
                Logger.getLogger(CopyFilesUpdater.class.getName()).log(Level.SEVERE, "No se ha podido copiar el contendio en la ruta indicada.");
            }
        }else{
            System.out.println("Parametros insuficientes.");
             Logger.getLogger(CopyFilesUpdater.class.getName()).log(Level.SEVERE, "Parametros insuficientes.");
        }

    }

}
