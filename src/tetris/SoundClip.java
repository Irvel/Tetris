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
    
    public SoundClip(){
        try{
            cClip = AudioSystem.getClip();
        }catch (LineUnavailableException e){
            System.out.println("Error en " + e.toString());
        }
    }
    public SoundClip(String filename){
        this();
        load(filename);
    }
    public void setLooping(boolean bLooping){
        this.bLooping = bLooping;
    }
    public void setRepeat(int repeat){
        this.repeat = repeat;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }
    public Clip getClip(){
        return cClip;
    }
    public boolean isLooping(){
        return bLooping;
    }
    public int getRepeat(){
        return repeat;
    }
    public String getFilename(){
        return filename;
    }
    private URL getURL(String filename){
        URL url = null;
        try{
            url = this.getClass().getResource(filename);
        }catch(Exception e){
            System.out.println("Error en " + e.toString());
        }
        return url;
    }
    public boolean isLoaded(){
        return (boolean)(auiSample != null);
    }
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
    public void play(){
        if (!isLoaded())
            return;
        cClip.setFramePosition(0);
        if (bLooping)
            cClip.loop(Clip.LOOP_CONTINUOUSLY);
        else
            cClip.loop(repeat);
    }
    public void stop(){
        cClip.stop();
    }
    
}
