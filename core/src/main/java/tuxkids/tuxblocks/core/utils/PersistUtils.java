package tuxkids.tuxblocks.core.utils;

import java.util.HashMap;
import java.util.LinkedList;

import playn.core.PlayN;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.defense.round.Level.Level1;
import tuxkids.tuxblocks.core.defense.round.Reward;
import tuxkids.tuxblocks.core.defense.select.Problem;
import tuxkids.tuxblocks.core.solve.blocks.BlockHolder;
import tuxkids.tuxblocks.core.solve.blocks.Equation;
import tuxkids.tuxblocks.core.solve.blocks.HorizontalModifierGroup;
import tuxkids.tuxblocks.core.solve.blocks.MinusBlock;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlock;
import tuxkids.tuxblocks.core.solve.blocks.NumberBlockProxy;
import tuxkids.tuxblocks.core.solve.blocks.OverBlock;
import tuxkids.tuxblocks.core.solve.blocks.PlusBlock;
import tuxkids.tuxblocks.core.solve.blocks.TimesBlock;
import tuxkids.tuxblocks.core.solve.blocks.VariableBlock;
import tuxkids.tuxblocks.core.solve.blocks.VerticalModifierGroup;
import tuxkids.tuxblocks.core.title.Difficulty;
import tuxkids.tuxblocks.core.utils.Persistable.Constructor;
import tuxkids.tuxblocks.core.utils.Persistable.Data;
import tuxkids.tuxblocks.core.utils.Persistable.ParseDataException;

public class PersistUtils {

	private static HashMap<Class<?>, Constructor> constructorMap = 
			new HashMap<Class<?>, Persistable.Constructor>();
	private static HashMap<String, Class<?>> nameMap = 
			new HashMap<String, Class<?>>();
	static {
		constructorMap.put(GameState.class, GameState.constructor());
		constructorMap.put(Difficulty.class, Difficulty.constructor());
		constructorMap.put(Problem.class, Problem.constructor());
		constructorMap.put(Reward.class, Reward.constructor());
		constructorMap.put(Equation.class, Equation.constructor());
		constructorMap.put(VariableBlock.class, VariableBlock.constructor());
		constructorMap.put(NumberBlock.class, NumberBlock.constructor());
		constructorMap.put(NumberBlockProxy.class, NumberBlock.constructor());
		constructorMap.put(BlockHolder.class, BlockHolder.constructor());
		constructorMap.put(PlusBlock.class, PlusBlock.constructor());
		constructorMap.put(MinusBlock.class, MinusBlock.constructor());
		constructorMap.put(TimesBlock.class, TimesBlock.constructor());
		constructorMap.put(OverBlock.class, OverBlock.constructor());
		constructorMap.put(VerticalModifierGroup.class, VerticalModifierGroup.constructor());
		constructorMap.put(HorizontalModifierGroup.class, HorizontalModifierGroup.constructor());
		constructorMap.put(Level1.class, Level1.constructor());
		
		for (Class<?> key : constructorMap.keySet()) {
			nameMap.put(key.getName(), key);
		}
	}
	
	public static Persistable construct(String type) throws ParseDataException {
		Class<?> c = nameMap.get(type);
		if (c == null) throw new ParseDataException("No constructor for type: " + type);
		Constructor constructor = constructorMap.get(c);
		return constructor.construct();
	}
	
	private static String tag;
	private static int n;
	private static LinkedList<String> store = new LinkedList<String>();
	
	private static String NULL = "<NULL>";
	
	private static void saveStore(String tag) {
		StringBuilder sb = new StringBuilder();
		for (String line : store) {
			if (sb.length() > 0) sb.append("\n");
			sb.append(line == null ? NULL : line);
		}
		String value = sb.toString();
		PlayN.storage().setItem(tag, value);
		store.clear();
	}
	
	private static void loadStore(String tag) {
		store.clear();
		String data = PlayN.storage().getItem(tag);
		if (data == null) return;
		String[] lines = data.split("\n");
		for (String line : lines) {
			store.add(line.equals(NULL) ? null : line);
		}
	}
	
	public static void persist(Persistable persistable, String tag) {
//		PlayN.storage().setItem(tag, "");
		Data data = new Data(true);
		PersistUtils.tag = tag;
		n = 0;
		store.clear();
		try {
			persistable.persist(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveStore(tag);
		tag = null;
		n = 0;
	}

	@SuppressWarnings("unchecked")
	public static <T> T fetch(Class<T> clazz, String tag) {
		Data data = new Data(false);
		PersistUtils.tag = tag;
		n = 0;
		try {
			loadStore(tag);
			if (store == null) return null;
			
			Persistable obj = construct(clazz.getName());
			obj.persist(data);
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tag = null;
			n = 0;
			store.clear();
		}
		return null;
	}
	
	public static boolean stored(String tag) {
		return PlayN.storage().getItem(tag) != null;
	}

	private static String nextKey() {
		return tag + n++;
	}
	
	public static String read() throws ParseDataException {
//		String key = nextKey();
//		String data = PlayN.storage().getItem(key);
//		Debug.write("Read %s: %s", key, data);
//		return data;
		
		if (store.isEmpty()) throw new ParseDataException("No data");
		return store.removeFirst();
	}

	public static void write(String data) {
//		String key = nextKey();
//		Debug.write("Write %s: %s", key, data);
//		PlayN.storage().setItem(key, data);
		
		store.add(data);
	}

	public static void clear(String tag) {
		for (String key : PlayN.storage().keys()) {
			if (key.startsWith(tag)) {
				PlayN.storage().removeItem(key);
			}
		}
	}

}
