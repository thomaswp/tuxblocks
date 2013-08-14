package tuxkids.tuxblocks.core;

import java.util.HashMap;

import tripleplay.sound.Clip;
import tripleplay.sound.Loop;
import tripleplay.sound.Playable;
import tripleplay.sound.SoundBoard;

public abstract class Audio extends PlayNObject {
	
	private static Audio bg = new BG(), se = new SE();
	
	public static Audio bg() {
		return bg;
	}
	
	public static Audio se() {
		return se;
	}
	
	public static void update(int delta) {
		bg.updateInstance(delta);
		se.updateInstance(delta);
	}

	protected abstract Playable load(String path);
	public abstract void stop();
	
	protected final SoundBoard soundboard;
	protected final HashMap<String, Playable> cache =
			new HashMap<String, Playable>();
	
	protected Playable lastPlayed;
	
	private Audio() {
		soundboard = new SoundBoard();
	}
	
	public float volume() {
		return soundboard.volume.get();
	}
	
	public void setVolume(float volume) {
		soundboard.volume.update(volume);
	}
	
	public void play(String path) {
		preload(path);
		Playable playing = cache.get(path);
		playing.play();
		lastPlayed = playing;
	}
	
	public void play(String path, float volume) {
		play(path);
		lastPlayed.setVolume(volume);
	}

	private void updateInstance(int delta) {
		soundboard.update(delta);
	}
	
	public void preload(String path) {
		if (cache.containsKey(path)) return;
		cache.put(path, load(path));
	}

	public static void clear() {
		bg = new BG();
		se = new SE();
	}

	public void stop(String path) {
		Playable p = cache.get(path);
		if (p != null) p.stop();
	}

	public boolean isPlaying(String path) {
		Playable p = cache.get(path);
		if (p != null) return p.isPlaying();
		return false;
	}
	
	protected static class BG extends Audio {

		@Override
		protected Playable load(String path) {
			return soundboard.getLoop(path);
		}

		public void play(String path) {
			for (Playable p : cache.values()) {
				if (p.isPlaying()) {
					((Loop) p).fadeOut(1000);
				}
			}
			super.play(path);
			((Loop) lastPlayed).fadeIn(1000);
		}
		
		@Override
		public void stop() {
			((Loop) lastPlayed).fadeOut(1000);
		}
		
	}
	
	protected static class SE extends Audio {

		@Override
		protected Playable load(String path) {
			Clip clip = soundboard.getClip(path);
			clip.preload();
			return clip;
		}
		
		public void play(String path) {
			preload(path);
			Playable playing = cache.get(path);
			if (playing != lastPlayed || lastPlayed == null || !lastPlayed.isPlaying()) {
				super.play(path);
			}
		}

		@Override
		public void stop() {
			
		}
		
	}
}
