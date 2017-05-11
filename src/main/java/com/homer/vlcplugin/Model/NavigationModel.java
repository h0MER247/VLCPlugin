package com.homer.vlcplugin.Model;

import com.homer.vlcplugin.Util.Channels.MessageChannel;
import java.util.Optional;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.TrackDescription;



public class NavigationModel {
    
    // Messages to observers
    public final MessageChannel MESSAGE_DURATION_CHANGED = new MessageChannel();
    public final MessageChannel MESSAGE_POSITION_CHANGED = new MessageChannel();
    public final MessageChannel MESSAGE_PLAYER_STATE_CHANGED = new MessageChannel();
    public final MessageChannel MESSAGE_VOLUME_CHANGED = new MessageChannel();
    
    // Helper for the current state of the media player
    private enum PlayerState {
        
        Stopped, Playing, Paused, Finished
    };
    private PlayerState m_playerState;
    
    // The media player from VideoModel::getMediaPlayer()
    private MediaPlayer m_mediaPlayer;
    
    
    
    public NavigationModel() {
        
        m_playerState = PlayerState.Stopped;
    }
    
    
    
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        
        m_mediaPlayer = mediaPlayer;
        m_mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            
            @Override
            public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
                
                MESSAGE_DURATION_CHANGED.postMessage();
            }
            
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                
                MESSAGE_POSITION_CHANGED.postMessage();
            }
           
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                
                m_playerState = PlayerState.Finished;
                MESSAGE_PLAYER_STATE_CHANGED.postMessage();
                MESSAGE_POSITION_CHANGED.postMessage();
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                
                m_playerState = PlayerState.Stopped;
                MESSAGE_PLAYER_STATE_CHANGED.postMessage();
                MESSAGE_POSITION_CHANGED.postMessage();
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                
                m_playerState = PlayerState.Paused;
                MESSAGE_PLAYER_STATE_CHANGED.postMessage();
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                
                disableSubtitles();
                
                m_playerState = PlayerState.Playing;
                MESSAGE_PLAYER_STATE_CHANGED.postMessage();
            }
        });
        
        // Update UI
        MESSAGE_DURATION_CHANGED.postMessage();
        MESSAGE_POSITION_CHANGED.postMessage();
        MESSAGE_PLAYER_STATE_CHANGED.postMessage();
        MESSAGE_VOLUME_CHANGED.postMessage();
    }
    
    private MediaPlayer getMediaPlayer() {
        
        if(m_mediaPlayer == null)
            throw new IllegalStateException("The reference to the media player is null");
        
        return m_mediaPlayer;
    }
    
    private void disableSubtitles() {
        
        Optional<TrackDescription> track;
        if((track = getMediaPlayer().getSpuDescriptions()
                                    .stream()
                                    .filter(desc -> desc.description().equals("Disable"))
                                    .findFirst()).isPresent()) {
            
            m_mediaPlayer.setSpu(track.get().id());
        }
    }
    
    
    
    public void playMedia(String mrl) {
        
        getMediaPlayer().playMedia(mrl);
    }
    
    public void play() {
        
        getMediaPlayer().play();
    }
    
    public void pause() {
    
        getMediaPlayer().pause();
    }
    
    public void stop() {
        
        getMediaPlayer().stop();
    }
    
    
    
    public int getVolume() {
        
        return getMediaPlayer().getVolume();
    }
    
    public void setVolume(int volume) {
        
        getMediaPlayer().setVolume(volume);
    }
    
    public void mute(boolean isMuted) {
        
        getMediaPlayer().mute(isMuted);
    }
    
    
    
    public float getPosition() {
        
        return getMediaPlayer().getPosition();
    }
    
    public void setPosition(float position) {
        
        getMediaPlayer().setPosition(position);
    }
    
    
    
    public long getDuration() {
        
        return getMediaPlayer().getLength();
    }
    
    
    
    public boolean isFinished() {
        
        return m_playerState == PlayerState.Finished;
    }
    
    public boolean isPaused() {
        
        return m_playerState == PlayerState.Paused;
    }
    
    public boolean isPlaying() {
        
        return m_playerState == PlayerState.Playing;
    }
    
    public boolean isStopped() {
        
        return m_playerState == PlayerState.Stopped;
    }
    
    public boolean isMuted() {
        
        return getMediaPlayer().isMute();
    }
    
    
    
    public void release() {
        
        m_mediaPlayer = null;
        m_playerState = PlayerState.Stopped;
    }
}
