package audio;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/*
 * Written by Abszol (Kyle Darling)
 * Developed to run audio on Linux through Java
 */
public class SystemAudio {

	private final int BUFFER_SIZE = 128000;
    private URL soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    private FloatControl volume;
    
    private boolean mute = false;
    private boolean loop = false;
    private float setVolume = 0f;
    
	public void load(String n) throws MalformedURLException {
		
		try {
			soundFile = SystemAudio.class.getResource(n);
		} catch (Exception e) {
			soundFile = new File(n).toURI().toURL();
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
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	public void loop() {
		loop = true;
		Thread tmp = new Thread(new Runnable() {
			public void run() {
				while(loop) {
					play();
				}
			}
		});
		tmp.start();
	}
	
	public void play() {
		
		if(sourceLine != null) {
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
		
	}
	
	public boolean setVolume(float v) {
		if(sourceLine != null) {
			try {
				sourceLine.open();
				if(sourceLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					volume = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
					if(v >= volume.getMinimum() && v <= volume.getMaximum()) {
						setVolume = v;
						volume.setValue(v);
						return true;
					} else
						return false;
				}else
					return false;
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void mute(boolean m) {
		
	}
	
	public void close() {
		if(loop) loop = false;
		if(mute) mute = false;
		if(sourceLine != null) {
			sourceLine.drain();
			sourceLine.close();
		}
	}
	
	public FloatControl getVolumeControl() {
		return volume;
	}
	
}
