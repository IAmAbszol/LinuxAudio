package audio;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SystemAudio {

    private final int BUFFER_SIZE = 128000;
    private URL soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;
    private FloatControl volume;
    
    private boolean mute = false;
    private boolean loop = false;
    
    private float preVolume = 0f;
    private float setVolume = 0f;
    private float minVolume = 0f;
    private float maxVolume = 0f;
    
    public void close() {
    }
    
    public void stop() {
    	try {
	    	if(sourceLine != null) {
	    		sourceLine.stop();
	    		sourceLine.close();
	    	}
	    	if(loop) loop = false;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public void load(String filename) {
    	try {
        	soundFile = SystemAudio.class.getResource(filename);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
        }
        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
            sourceLine.open();
            if( sourceLine.isControlSupported( FloatControl.Type.MASTER_GAIN)) {
                volume = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
                minVolume = volume.getMinimum();
                maxVolume = volume.getMaximum();
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void mute(boolean b) {
    	Thread tmp = new Thread(new Runnable() {
    		public void run() {
    			preVolume = setVolume;
    			try {
    				while(volume == null) {
    					Thread.sleep(1);
    				}
    				if(b) {
    					mute = true;
    					setVolume = volume.getMinimum();
    					volume.setValue(setVolume);
    				} else {
    					mute = false;
    					setVolume = preVolume;
    					volume.setValue(setVolume);
    				}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	});
    	tmp.start();
    }
    
    public void setVolume(float v) {
    	Thread tmp = new Thread(new Runnable() {
    		public void run() {
    			try {
    				while(volume == null) {
    					Thread.sleep(1);
    				}
    				if(v >= volume.getMinimum() && v <= volume.getMaximum()) {
    					preVolume = setVolume = v;
    					volume.setValue(setVolume);
    				} else
    					if(v < minVolume) {
    						preVolume = setVolume = minVolume;
        					volume.setValue(minVolume);
    					} else
    						if(v > maxVolume) {
    							preVolume = setVolume = maxVolume;
            					volume.setValue(maxVolume);
    						}
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	});
    	tmp.start();
    }
    
    public void loop() {
    	
    	loop = true;
    	
    	Thread t = new Thread(new Runnable() {
    
    		public void run() {
    			
    			while(loop) {
    				play();
    			}
    			
    		}
    		
    	});
    	t.start();
    	
    }
    
    private void play() {
    	
    	try {
	         audioStream = AudioSystem.getAudioInputStream(soundFile);
	     } catch (Exception e){
	         e.printStackTrace();
	     }
   	
       audioFormat = audioStream.getFormat();

       DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
       try {
           sourceLine = (SourceDataLine) AudioSystem.getLine(info);
           sourceLine.open(audioFormat);
           sourceLine.open();
           if( sourceLine.isControlSupported( FloatControl.Type.MASTER_GAIN)) {
               volume = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
               volume.setValue(setVolume);
           }
       } catch (LineUnavailableException e) {
           e.printStackTrace();
       } catch (Exception e) {
           e.printStackTrace();
       }

       sourceLine.start();

       int nBytesRead = 0;
       byte[] abData = new byte[BUFFER_SIZE];
       while (nBytesRead != -1) {
           try {
               nBytesRead = audioStream.read(abData, 0, abData.length);
           } catch (IOException e) {
               e.printStackTrace();
           }
           if (nBytesRead >= 0) {
               @SuppressWarnings("unused")
               int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
           }
       }
       sourceLine.drain();
       sourceLine.close();
    	
    }
    
    public void playSound(){

    	Thread t = new Thread(new Runnable() {
    		public void run() {
    			try {
    		         audioStream = AudioSystem.getAudioInputStream(soundFile);
    		     } catch (Exception e){
    		         e.printStackTrace();
    		     }
    	    	
    	        audioFormat = audioStream.getFormat();

    	        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    	        try {
    	            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
    	            sourceLine.open(audioFormat);
    	            sourceLine.open();
    	            if( sourceLine.isControlSupported( FloatControl.Type.MASTER_GAIN)) {
    	                volume = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
    	                volume.setValue(setVolume);
    	            }
    	        } catch (LineUnavailableException e) {
    	            e.printStackTrace();
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }

    	        sourceLine.start();

    	        int nBytesRead = 0;
    	        byte[] abData = new byte[BUFFER_SIZE];
    	        while (nBytesRead != -1) {
    	            try {
    	                nBytesRead = audioStream.read(abData, 0, abData.length);
    	            } catch (IOException e) {
    	                e.printStackTrace();
    	            }
    	            if (nBytesRead >= 0) {
    	                @SuppressWarnings("unused")
    	                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
    	            }
    	        }
    	        sourceLine.drain();
    	        sourceLine.close();
    		}
    	});
    	
    	t.start();
    	
    }
    
    public boolean isMuted() {
    	return mute;
    }
    
    public float getMiniumumVolume() {
    	return minVolume;
    }
    
    public float getMaximumVolume() {
    	return maxVolume;
    }
    
}
