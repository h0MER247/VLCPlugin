package com.homer.vlcplugin.Util.Channels;

import java.util.ArrayList;



public class MessageChannel {
    
    private final ArrayList<Runnable> m_callbacks;
    
    
    
    public MessageChannel() {

        m_callbacks = new ArrayList<>();
    }
    
    
    
    public synchronized void subscribe(Runnable callback) {
        
        m_callbacks.add(callback);
    }
    
    public synchronized void unsubscribe(Runnable callback) {
        
        m_callbacks.remove(callback);
    }
    
    public synchronized void postMessage() {
        
        m_callbacks.forEach(c -> c.run());
    }
}
