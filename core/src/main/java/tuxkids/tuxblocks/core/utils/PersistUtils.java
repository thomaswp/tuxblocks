package tuxkids.tuxblocks.core.utils;

import java.util.HashMap;

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
	
	public static void persist(Persistable persistable, String tag) {
		PlayN.storage().setItem(tag, "");
		Data data = new Data(true);
		PersistUtils.tag = tag;
		n = 0;
		try {
			persistable.persist(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tag = null;
		n = 0;
	}

	@SuppressWarnings("unchecked")
	public static <T> T fetch(Class<T> clazz, String tag) {
		Data data = new Data(false);
		PersistUtils.tag = tag;
		n = 0;
		try {
			Persistable obj = construct(clazz.getName());
			obj.persist(data);
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		tag = null;
		n = 0;
		return null;
	}
	
	public static boolean stored(String tag) {
		return PlayN.storage().getItem(tag) != null;
	}

	private static String nextKey() {
		return tag + n++;
	}
	
	public static String read() throws ParseDataException {
		String key = nextKey();
		String data = PlayN.storage().getItem(key);
		Debug.write("Read %s: %s", key, data);
		return data;
	}

	public static void write(String data) {
		String key = nextKey();
		Debug.write("Write %s: %s", key, data);
		PlayN.storage().setItem(key, data);
	}

	public static void clear(String tag) {
		for (String key : PlayN.storage().keys()) {
			if (key.startsWith(tag)) {
				PlayN.storage().removeItem(key);
			}
		}
	}

}
