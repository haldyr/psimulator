package psimulator.userInterface.imageFactories;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Martin
 */
public class ImageBuffer {
    /* Data structures for buffering */
    // String is path to image = the identificator
    private HashMap<String,HashMap<Integer, Image>> hwComponentBuffer;
    private HashMap<String,HashMap<Integer, Image>> hwMarkedComponentBuffer;
    
    public ImageBuffer(){
        // create EnumMap with all HW components
        hwComponentBuffer = new HashMap<String,HashMap<Integer, Image>>();
        hwMarkedComponentBuffer = new HashMap<String,HashMap<Integer, Image>>();
        
    }
    
    /**
     * Clears alll Images in buffer
     */
    public void clearBuffer(){
        // each HW components HashMap is cleared
        for(Entry<String,HashMap<Integer, Image>> e : hwComponentBuffer.entrySet()){
            e.getValue().clear();
        }
        
        for(Entry<String,HashMap<Integer, Image>> e : hwMarkedComponentBuffer.entrySet()){
            e.getValue().clear();
        }
    }
    
    /**
     * Puts Image into buffer
     * @param path
     * @param size
     * @param image 
     * @param marked 
     */
    public void putBufferedImage(String path, Integer size, Image image, boolean marked){
        HashMap<String,HashMap<Integer, Image>> map;

        if(marked){
            map = hwMarkedComponentBuffer;
        }else{
            map = hwComponentBuffer;
        }
        
        // if map does not contains path
        if(!map.containsKey(path)){
            map.put(path, new HashMap<Integer, Image>());
        }
        
        map.get(path).put(size, image);
    }
    
    /**
     * Gets specified Image
     * @param path
     * @param size
     * @return Image if found, otherwise null
     * @param marked 
     */
    public BufferedImage getBufferedImage(String path, Integer size, boolean marked){
        HashMap<String,HashMap<Integer, Image>> map;
        
        if(marked){
            map = hwMarkedComponentBuffer;
        }else{
            map = hwComponentBuffer;
        }
        
        // if map does not contains path
        if(!map.containsKey(path)){
            return null;
        }
        
        // if is specified BufferedImage in buffer
        if(map.get(path).containsKey(size)){
            return (BufferedImage) map.get(path).get(size);
        }else{
            // if isn't
            return null;
        }
    }
    
    
}
