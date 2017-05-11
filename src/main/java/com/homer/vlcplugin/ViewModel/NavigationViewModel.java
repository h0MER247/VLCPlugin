package com.homer.vlcplugin.ViewModel;

import com.homer.vlcplugin.Util.Swing.SwingBoilerplate;
import com.homer.vlcplugin.Model.NavigationModel;
import static com.homer.vlcplugin.Util.Numbers.NumberUtils.getPercentageFromValue;
import static com.homer.vlcplugin.Util.Numbers.NumberUtils.getValueFromPercentage;
import static com.homer.vlcplugin.Util.Numbers.NumberUtils.formatTime;
import com.homer.vlcplugin.View.NavigationView;
import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import uk.co.caprica.vlcj.filter.MediaFileFilter;



public class NavigationViewModel {
    
    // Unicode characters for various symbols
    private final String ICON_MUTED = "\ud83d\udd07"; // 🔇
    private final String ICON_UNMUTED = "\ud83d\udd0a"; // 🔊
    private final String ICON_PLAY = "\u25b6"; // ▶
    private final String ICON_PAUSE = "\u23f8"; // ⏸
    
    // Model / View
    private final NavigationModel m_model;
    private final NavigationView m_view;
    
    // Position slider status flags
    private final AtomicBoolean m_isPositionUpdating = new AtomicBoolean();
    private final AtomicBoolean m_isUserAdjustingSlider = new AtomicBoolean();
    
    
    
    public NavigationViewModel(NavigationView view, NavigationModel model) {
        
        m_view = view;
        m_model = model;
        
        wireView();
        wireModel();
    }
    
    
    
    // View / Model wiring
    
    private void wireView() {
        
        m_view.addVolumeSliderCallback(SwingBoilerplate.onSliderChanged(this::viewOnVolumeChanged));
        m_view.addMediaPositionSliderCallback(SwingBoilerplate.onSliderChanged(this::viewOnMediaPositionChanged));
        m_view.addPlayButtonCallback(SwingBoilerplate.onButtonClicked(this::viewOnPlayButtonPressed));
        m_view.addStopButtonCallback(SwingBoilerplate.onButtonClicked(this::viewOnStopButtonPressed));
        m_view.addLoadButtonCallback(SwingBoilerplate.onButtonClicked(this::viewOnLoadButtonPressed));
        m_view.addVolumeButtonCallback(SwingBoilerplate.onButtonToggled(this::viewOnVolumeButtonPressed));
    }
    
    private void wireModel() {
        
        m_model.MESSAGE_DURATION_CHANGED.subscribe(this::modelOnMediaDurationChanged);
        m_model.MESSAGE_POSITION_CHANGED.subscribe(this::modelOnMediaPositionChanged);
        m_model.MESSAGE_PLAYER_STATE_CHANGED.subscribe(this::modelOnPlayerStateChanged);
        m_model.MESSAGE_VOLUME_CHANGED.subscribe(this::modelOnVolumeChanged);
    }
    
    
    
    // Model callbacks
    
    private void modelOnMediaDurationChanged() {
        
        m_view.setMediaDuration(formatTime(m_model.getDuration()));
    }
    
    private void modelOnMediaPositionChanged() {
        
        float positionInPercent = m_model.getPosition();
        int durationInMS = (int)m_model.getDuration();
        
        m_view.setMediaPosition(formatTime(getValueFromPercentage(positionInPercent,
                                                                  0,
                                                                  durationInMS)));
        
        if(!m_isUserAdjustingSlider.get()) {
            
            m_isPositionUpdating.set(true);
            m_view.setMediaPositionSlider(getValueFromPercentage(positionInPercent,
                                                                 NavigationView.MIN_POSITION,
                                                                 NavigationView.MAX_POSITION));
            m_isPositionUpdating.set(false);
        }
    }
    
    private void modelOnPlayerStateChanged() {
        
        if(m_model.isFinished()) {
            
            m_model.stop();
        }
        else if(m_model.isPaused()) {
            
            m_view.setPlayButtonText(ICON_PLAY);
        }
        else if(m_model.isPlaying()) {
            
            m_view.setPlayButtonText(ICON_PAUSE);
        }
        else if(m_model.isStopped()) {
            
            m_view.setPlayButtonText(ICON_PLAY);
        }
        EventQueue.invokeLater(m_view::repaint);
    }
    
    private void modelOnVolumeChanged() {
        
        m_view.setVolumeSlider(m_model.getVolume());
        m_view.setVolumeButtonToggled(m_model.isMuted());
    }
    
    
    
    // View callbacks
    
    private void viewOnVolumeChanged(boolean isAdjusting, int value) {
        
        if(isAdjusting)
            m_model.setVolume(value);
    }
    
    private void viewOnMediaPositionChanged(boolean isAdjusting, int value) {
        
        if(!m_isPositionUpdating.get()) {
            
            m_isUserAdjustingSlider.set(isAdjusting);
            if(isAdjusting) {
                
                m_model.setPosition(getPercentageFromValue(value,
                                                           NavigationView.MIN_POSITION,
                                                           NavigationView.MAX_POSITION));
            }
        }
    }
    
    private void viewOnPlayButtonPressed() {
        
        if(m_model.isPlaying())
            m_model.pause();
        else
            m_model.play();
    }
    
    private void viewOnStopButtonPressed() {
        
        if(!m_model.isStopped())
            m_model.stop();
    }
    
    private void viewOnLoadButtonPressed() {
        
        EventQueue.invokeLater(() -> {
            
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileFilter(new FileFilter() {
                
                private final MediaFileFilter m_filter = new MediaFileFilter();
                
                @Override
                public boolean accept(File f) { return f.isDirectory() || m_filter.accept(f); }
                
                @Override
                public String getDescription() { return "All supported media files"; }
            });
            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                
                m_model.playMedia(chooser.getSelectedFile().toString());
            }
        });
    }
    
    private void viewOnVolumeButtonPressed(boolean isVolumeMuted) {
        
        m_model.mute(isVolumeMuted);
        m_view.setVolumeButtonText(isVolumeMuted ? ICON_MUTED : ICON_UNMUTED);
    }
}
