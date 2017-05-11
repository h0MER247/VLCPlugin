package com.homer.vlcplugin.Util.Channels;

import java.util.ArrayList;
import java.util.function.Consumer;



public class DataChannel<T> {
    
    private final ArrayList<Consumer<T>> m_callbacks;
    
    
    
    public DataChannel() {

        m_callbacks = new ArrayList<>();
    }
    
    
    
    public synchronized void subscribe(Consumer<T> callback) {
        
        m_callbacks.add(callback);
    }
    
    public synchronized void unsubscribe(Consumer<T> callback) {
        
        m_callbacks.remove(callback);
    }
    
    public synchronized void postData(T data) {
        
        m_callbacks.forEach(c -> c.accept(data));
    }
}
