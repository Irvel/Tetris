/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.net.URL;

/**
 * SoundClip
 *
 * Clase utilizada para almacenar y reproducir archivos de sonido
 *
 * @author Irvel
 * @author Jorge
 * @version 0.2
 */

public class SoundClip {
    private AudioInputStream auiSample;
    private Clip cClip;
    private boolean bLooping = false;
    private int repeat = 0;
    private String filename = "";
    
    /**
     * 
     * Constructor default
     * 
     * Crea el objeto Soundclip con el buffer del sonido
     */
    public SoundClip(){
        try{
            cClip = AudioSystem.getClip();
        }catch (LineUnavailableException e){
            System.out.println("Error en " + e.toString());
        }
    }
    /**
     * Soundclip(String filename)
     * 
     * Función que manda a llamar el constructor default y carga
     * el sonido con el nombre del archivo dado como parametro
     * 
     * @param filename 
     */
    public SoundClip(String filename){
        this();
        load(filename);
    }
    /**
     * setLooping(boolean bLooping)
     * 
     * Función que se encarga de especificar si el archivo de sonido
     * se repite o no
     * 
     * @param bLooping 
     */
    public void setLooping(boolean bLooping){
        this.bLooping = bLooping;
    }
    /**
     * 
     * setRepeat(int repeat)
     * 
     * Función que se de especificar cuantas veces se desea repetir el archivo
     * de sonido
     * 
     * @param repeat 
     */
    public void setRepeat(int repeat){
        this.repeat = repeat;
    }
    /**
     * 
     * setFilename(String filename)
     * 
     * Función que recibe el nombre del archivo como parametro
     * y se lo asigna a el atributo filename
     * 
     * @param filename 
     */
    public void setFilename(String filename){
        this.filename = filename;
    }
    /**
     * 
     * getClip()
     * 
     * Función que regresa el clip de audio cargado
     * 
     * @return clip
     */
    public Clip getClip(){
        return cClip;
    }
    /**
     * 
     * isLooping()
     * 
     * Función que regresa un booleano para verificar si la canción se debe
     * repetir o no
     * 
     * @return boolean
     */
    public boolean isLooping(){
        return bLooping;
    }
    /**
     * 
     * getRepeat()
     * 
     * Función que regresa la cantidad de veces que se debe de repetir
     * el clip de audio
     * 
     * @return repeat
     */
    public int getRepeat(){
        return repeat;
    }
    /**
     * 
     * getFilename()
     * 
     * Función que regresa el nombre del archivo de audio
     * 
     * 
     * @return filename
     */
    public String getFilename(){
        return filename;
    }
    /**
     * 
     * getURL(String filename)
     * 
     * Función que se encarga de regresar el URL de donde se encuentra el 
     * archivo de musica
     * 
     * @param filename
     * @return url
     */
    private URL getURL(String filename){
        URL url = null;
        try{
            url = this.getClass().getResource(filename);
        }catch(Exception e){
            System.out.println("Error en " + e.toString());
        }
        return url;
    }
    /**
     * 
     * isLoaded()
     * 
     * Función que se encarga de checar si el archivo esta cargado o no
     * 
     * @return boolean
     */
    public boolean isLoaded(){
        return (boolean)(auiSample != null);
    }
    /**
     * 
     * load()
     * 
     * Función que carga el archivo de audio, recibe como parametro el nombre 
     * del archivo
     * 
     * @param audiofile
     * @return boolean
     */
    public boolean load(String audiofile){
        try{
            setFilename(audiofile);
            auiSample = AudioSystem.getAudioInputStream(getURL(filename));
            cClip.open(auiSample);
            return true;
        }catch (IOException e){
            System.out.println("Error en " + e.toString());
            return false;
        }catch (UnsupportedAudioFileException e){
            System.out.println("Error en " + e.toString());
            return false;
        }catch (LineUnavailableException e){
            System.out.println("Error en " + e.toString());
            return false;
        }
        
    }
    /**
     * 
     * play()
     * 
     * Función que se encarga primero de verificar si el archivo esta cargado
     * además de reproducir el archivo de forma continua o no
     * 
     */
    public void play(){
        if (!isLoaded())
            return;
        cClip.setFramePosition(0);
        if (bLooping)
            cClip.loop(Clip.LOOP_CONTINUOUSLY);
        else
            cClip.loop(repeat);
    }
    /**
     * 
     * stop()
     * 
     * Función que detiene la reproducción del archivo de sonido
     * 
     */
    public void stop(){
        cClip.stop();
    }
    
}
