package com.homer.vlcplugin.Model;

import com.homer.vlcplugin.Util.Channels.DataChannel;
import com.homer.vlcplugin.Util.Channels.MessageChannel;
import com.sun.jna.Memory;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.nio.IntBuffer;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;



public class VideoModel {
    
    // Messages to observers
    public final MessageChannel MESSAGE_IMAGE_REDRAWN = new MessageChannel();
    public final DataChannel<Image> MESSAGE_IMAGE_CREATED = new DataChannel<>();
    
    // The direct media player component of vlcj
    private DirectMediaPlayerComponent m_directMediaPlayer;
    
    // Buffer format callback for vlcj
    private final BufferFormatCallback m_bufferFormatCallback;
    
    // Buffers to draw video data
    private MemoryImageSource m_imageSource;
    private int[] m_imageBuffer;
    private Image m_image;
    
    
    
    public VideoModel() {
        
        m_bufferFormatCallback = (srcW, srcH) -> {
            
            // Create the raw image data buffer
            m_imageBuffer = new int[srcW * srcH];
            
            // Create an awt image for this buffer
            m_imageSource = new MemoryImageSource(srcW, srcH, ColorModel.getRGBdefault(), m_imageBuffer, 0, srcW);
            m_imageSource.setAnimated(true);
            m_image = Toolkit.getDefaultToolkit().createImage(m_imageSource);
            
            // Notify observers about the created image
            MESSAGE_IMAGE_CREATED.postData(m_image);
            
            // Return the buffer format
            return new RV32BufferFormat(srcW, srcH);
        };
    }
    
    
    
    public MediaPlayer getMediaPlayer() {
        
        if(m_directMediaPlayer == null) {
            
            m_directMediaPlayer = new DirectMediaPlayerComponent(m_bufferFormatCallback) {

                @Override
                public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {

                    if(nativeBuffers == null)
                        return;
                    
                    // Copy native buffer data into m_imageBuffer...
                    Memory memory = nativeBuffers[0];
                    IntBuffer buffer = memory.getByteBuffer(0L, memory.size()).asIntBuffer();
                    buffer.get(m_imageBuffer, 0, bufferFormat.getWidth() * bufferFormat.getHeight());

                    // ... and update m_image from m_imageBuffer
                    m_imageSource.newPixels();

                    // Notify observers to redraw the image
                    MESSAGE_IMAGE_REDRAWN.postMessage();
                }
            };
        }
        
        return m_directMediaPlayer.getMediaPlayer();
    }
    
    
    
    public void release() {
        
        if(m_directMediaPlayer != null) {
        
            m_directMediaPlayer.release();
            m_directMediaPlayer = null;
            
            // Update UI
            MESSAGE_IMAGE_CREATED.postData(null);
            MESSAGE_IMAGE_REDRAWN.postMessage();
        }
    }
}
