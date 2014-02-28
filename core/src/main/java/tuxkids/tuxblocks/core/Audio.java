package tuxkids.tuxblocks.core;

import java.util.HashMap;

import playn.core.PlayN;
import tripleplay.sound.Clip;
import tripleplay.sound.Loop;
import tripleplay.sound.Playable;
import tripleplay.sound.SoundBoard;
import tuxkids.tuxblocks.core.utils.PlayNObject;

/**
 * Class for playing Audio. Contains a BG instance for background music
 * and an SE instance for sound effects. All methods are static.
 */
public abstract class Audio extends PlayNObject {
	
	private static Audio bg = new BG(), se = new SE();
	
	public static Audio bg() {
		return bg;
	}
	
	public static Audio se() {
		return se;
	}
	
	// called from TuxBlockGame
	public static void update(int delta) {
		bg.updateInstance(delta);
		se.updateInstance(delta);
	}

	// called from TuxBlockGame
	public static void clear() {
		bg = new BG();
		se = new SE();
	}

	protected abstract Playable load(String path);
	protected abstract String volumeKey();
	public abstract void stop();
	
	protected final SoundBoard soundboard;
	// cache sound effects
	protected final HashMap<String, Playable> cache =
			new HashMap<String, Playable>();
	
	protected Playable lastPlayed;
	protected static boolean muted;
	
	private Audio() {
		soundboard = new SoundBoard();
		//load previous volume
		String volume = PlayN.storage().getItem(volumeKey());
		String muted = PlayN.storage().getItem(Constant.KEY_MUTED);
		if (volume != null) {
			try {
				setVolume(Float.parseFloat(volume));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			setVolume(0.3f);
		}
		if (muted != null && muted.equals("true")) {
			soundboard.muted.update(true);
			Audio.muted = true;
		}
	}
	
	public static boolean muted() {
		return muted;
	}

	public static void setMuted(boolean muted) {
		if (muted != Audio.muted) {
			if (bg != null) bg.soundboard.muted.update(muted);
			if (se != null) se.soundboard.muted.update(muted);
			Audio.muted = muted;
			PlayN.storage().setItem(Constant.KEY_MUTED, "" + muted);
		}
	}
	
	public static void toggleMuted() {
		setMuted(!muted());
	}
	
	public float volume() {
		return soundboard.volume.get();
	}
	
	public void setVolume(float volume) {
		soundboard.volume.update(Math.min(Math.max(volume, 0), 1));
	}
	
	public void play(String path) {
		preload(path); // load the sound into the cache
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

	public void stop(String path) {
		Playable p = cache.get(path);
		if (p != null) p.stop();
	}

	public boolean isPlaying(String path) {
		Playable p = cache.get(path);
		if (p != null) return p.isPlaying();
		return false;
	}

	public void restart() {
		if (lastPlayed != null) {
			lastPlayed.stop();
			lastPlayed.play();
		}
	}
	
	protected static class BG extends Audio {

		@Override
		protected Playable load(String path) {
			return soundboard.getLoop(path);
		}

		public void play(String path) {
			// fade out other background music before playing
			for (Playable p : cache.values()) {
				if (soundboard.muted.get() || p.isPlaying()) {
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
		
		@Override
		public void setVolume(float volume) {
			super.setVolume(Math.max(volume, 0.001f));
		}

		@Override
		protected String volumeKey() {
			return Constant.KEY_BG_VOLUME;
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
			// Only play the sound effect if it's not already playing
			if (playing != lastPlayed || lastPlayed == null || !lastPlayed.isPlaying()) {
				super.play(path);
			}
		}

		@Override
		public void stop() {
			
		}
		
		@Override
		protected String volumeKey() {
			return Constant.KEY_SE_VOLUME;
		}
	}
}
