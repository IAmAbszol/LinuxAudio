package Test;

import java.net.MalformedURLException;

import audio.SystemAudio;

public class testing {
	
	public static void main(String[] args) throws MalformedURLException {
    	
    	SystemAudio s1 = new SystemAudio();
    	s1.load("/MenuAudio.wav");
    	
    	SystemAudio s2 = new SystemAudio();
    	s2.load("/menu.wav");
    	
    	s1.loop();
    	s2.play();
    	
    	//s2.setVolume(s2.getVolumeControl().getMinimum());
    	
    }

}
