package com.homer.vlcplugin.ViewModel;

import com.homer.vlcplugin.Model.NavigationModel;
import com.homer.vlcplugin.Model.VideoModel;
import com.homer.vlcplugin.View.VideoView;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;



public class VideoViewModel {
    
    // View / Models
    private final VideoView m_view;
    private final VideoModel m_model;
    private final NavigationModel m_navModel;
    
    // Reference to the image created from VideoModel
    private Image m_image;
    
    
    
    public VideoViewModel(VideoView view, VideoModel model, NavigationModel navModel) {
        
        m_view = view;
        m_model = model;
        m_navModel = navModel;
        
        wireView();
        wireModel();
    }
    
    
    
    // View / Model wiring
    
    private void wireView() {
        
        m_view.setPaintCallback(this::viewOnRedraw);
    }
    
    private void wireModel() {
        
        m_model.MESSAGE_IMAGE_CREATED.subscribe(this::modelOnImageCreated);
        m_model.MESSAGE_IMAGE_REDRAWN.subscribe(this::modelOnImageRedrawn);
    }
    
    
    
    // View callbacks
    
    private void viewOnRedraw(Graphics2D g) {
        
        int pnlW = m_view.getWidth();
        int pnlH = m_view.getHeight();
        
        if(m_image == null || m_navModel.isFinished() || m_navModel.isStopped()) {

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, pnlW, pnlH);
        }
        else {
            
            int imgW = m_image.getWidth(null);
            int imgH = m_image.getHeight(null);
            int imgX;
            int imgY;
            
            // Resize image with respect to m_image's aspect ratio so that it
            // fits inside m_view and center it
            float smallestRatio = Math.min((float)pnlW / (float)imgW,
                                           (float)pnlH / (float)imgH);

            imgW = Math.round(imgW * smallestRatio);
            imgH = Math.round(imgH * smallestRatio);
            imgX = (pnlW - imgW) / 2;
            imgY = (pnlH - imgH) / 2;

            // Draw the image with a bilinear filter
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g.drawImage(m_image, imgX, imgY, imgW, imgH, Color.black, null);
        }
    }
    
    
    
    // Model callbacks
    
    private void modelOnImageCreated(Image image) {
        
        m_image = image;
    }
    
    private void modelOnImageRedrawn() {
        
        m_view.repaint();
    }
}
