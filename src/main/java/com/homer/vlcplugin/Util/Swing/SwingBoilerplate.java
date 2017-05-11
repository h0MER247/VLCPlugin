package com.homer.vlcplugin.Util.Swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class SwingBoilerplate {
    
    private SwingBoilerplate() {
    }
    
    
    
    public static ActionListener onButtonClicked(Runnable callback) {
        
        if(callback == null)
            throw new IllegalArgumentException("The callback can't be null");
        
        return (ActionEvent e) -> callback.run();
    }
    
    
    
    public static ItemListener onButtonToggled(Consumer<Boolean> callback) {
        
        if(callback == null)
            throw new IllegalArgumentException("The callback can't be null");
        
        return (ItemEvent e) -> callback.accept(e.getStateChange() == ItemEvent.SELECTED);
    }
    
    
    
    public static ChangeListener onSliderChanged(BiConsumer<Boolean, Integer> callback) {
        
        if(callback == null)
            throw new IllegalArgumentException("The callback can't be null");
        
        return (ChangeEvent e) -> {
        
            JSlider slider = (JSlider)e.getSource();
            
            callback.accept(slider.getValueIsAdjusting(),
                            slider.getValue());
        };
    }
}
