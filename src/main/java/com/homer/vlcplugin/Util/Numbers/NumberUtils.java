package com.homer.vlcplugin.Util.Numbers;



public class NumberUtils {

    private NumberUtils() {
    }
    
    
    
    public static int getValueFromPercentage(float percentage, int min, int max) {
        
        int result = min + Math.round(percentage * (max - min));
        
        return Math.min(Math.max(result, min), max);
    }
    
    public static float getPercentageFromValue(int value, int min, int max) {
        
        float result = (float)(value - min) / ((float)(max - min));
        
        return Math.min(Math.max(result, 0.0f), 1.0f);
    }
    
    
    
    public static String formatTime(long milliseconds) {
        
        int seconds = (int)(milliseconds / 1000L);
        
        int hh = seconds / 3600;
        int mm = (seconds % 3600) / 60;
        int ss = seconds % 60;
        
        if(hh > 0)
            return String.format("%02d:%02d:%02d", hh, mm, ss);
        else
            return String.format("%02d:%02d", mm, ss);
    }
}
