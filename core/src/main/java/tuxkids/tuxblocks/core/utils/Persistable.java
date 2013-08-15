package tuxkids.tuxblocks.core.utils;

import java.util.ArrayList;
import java.util.List;

public interface Persistable {

	void persist(Data data) throws ParseDataException, NumberFormatException;
	
	public class ParseDataException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public ParseDataException(String string) {
			super(string);
		}
		
		public ParseDataException() {
			super();
		}
	}
	
	public interface Constructor {
		Persistable construct();
	}
	
//	public enum Type {
//		Int, Short, Long, Float, Double, Byte, Char, Boolean, String, Object
//	}
//	
//	public interface Constructor {
//		Type[] args();
//		Object construct(Object[] args);
//	}
//	
//	public abstract class NoArgsConstructor implements Constructor {
//		protected abstract Object construct();
//		
//		public Type[] args() { return new Type[] { }; }
//		
//		public Object construct(Object[] args) {
//			return construct();
//		}
//	}
	
	public class Data {
		private final boolean writeMode;
		
		public Data(boolean writeMode) {
			this.writeMode = writeMode;
		}

		public boolean writeMode() {
			return writeMode;
		}

		public boolean readMode() {
			return !writeMode;
		}
		
		public int persist(int x) throws ParseDataException, NumberFormatException {
			if (writeMode) {
				write(String.valueOf(x));
			} else {
				x = Integer.parseInt(read());
			}
			return x;
		}
		
		public long persist(long x) throws ParseDataException, NumberFormatException {
			if (writeMode) {
				write(String.valueOf(x));
			} else {
				x = Integer.parseInt(read());
			}
			return x;
		}
		
		public short persist(short x) throws ParseDataException, NumberFormatException {
			if (writeMode) {
				write(String.valueOf(x));	
			} else {
				x = Short.parseShort(read());
			}
			return x;
		}
		
		public float persist(float x) throws ParseDataException, NumberFormatException {
			if (writeMode) {
				write(String.valueOf(x));
			} else {
				x = Float.parseFloat(read());
			}
			return x;
		}
		
		public double persist(double x) throws ParseDataException, NumberFormatException {
			if (writeMode) {
				write(String.valueOf(x));
			} else {
				x = Double.parseDouble(read());
			}
			return x;
		}
		
		public byte persist(byte x) throws ParseDataException, NumberFormatException {
			if (writeMode) {
				write(String.valueOf(x));
			} else {
				x = Byte.parseByte(read());
			}
			return x;
		}
		
		public char persist(char x) throws ParseDataException { 
			if (writeMode) {
				write(String.valueOf(x));
			} else {
				String r = read();
				if (r == null || r.length() < 1) throw new ParseDataException();
				x = r.charAt(0);
			}
			return x;
		}
		
		public boolean persist(boolean x) throws ParseDataException {
			if (writeMode) {
				write(String.valueOf(x));
			} else {
				Boolean b = Boolean.parseBoolean(read());
				if (b == null) throw new ParseDataException();
				x = b;
			}
			return x;
		}
		
		public String persist(String x) throws ParseDataException {
			if (writeMode) {
				write(x);
			} else {
				x = read();
			}
			return x;
		}
		
		public int[] persistArray(int[] x) throws NumberFormatException, ParseDataException {
			if (writeMode) {
				int length = persist(x == null ? 0 : x.length);
				if (x != null) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < length; i++) {
						if (sb.length() != 0) sb.append(DIV);
						sb.append(String.valueOf(x[i]));
					}
					write(sb.toString());
				}
			} else {
				int length = persist(0);
				if (length != 0) {
					if (x != null){
						if (x.length != length) throw new ParseDataException();
					} else {
						x = new int[length];
					}
					String[] data = read().split("\\" + DIV);
					int i = 0;
					for (String part : data) {
						x[i++] = Integer.parseInt(part);
					}
				} else {
					x = null;
				}
			}
			return x;
		}
		
		public <T extends Persistable> List<T> persistList(List<T> x) throws NumberFormatException, ParseDataException {
			return persistList(x, null);
		}
		
		public <T extends Persistable> List<T> persistList(List<T> x, Class<? extends T> clazz) throws NumberFormatException, ParseDataException {
			if (writeMode) {
				persist(x == null ? -1 : x.size());
				if (x != null) {
					for (T t : x) {
						persist(t);
					}
				}
			} else {
				int length = persist(0);
				if (length >= 0) {
					if (x != null){
						x.clear();
					} else {
						x = new ArrayList<T>(length);
					}
					for (int i = 0; i < length; i++) {
						x.add(persist((T) null, clazz));
					}
				} else {
					x = null;
				}
			}
			return x;
		}
		
		public List<Integer> persistIntList(List<Integer> x) throws NumberFormatException, ParseDataException  {
			if (writeMode) {
				int[] array = new int[x == null ? 0 : x.size()];
				for (int i = 0; i < array.length; i++) {
					array[i] = x.get(i);
				}
				persistArray(array);
				return x;
			} else {
				int[] array = persistArray(null);
				if (array == null) return null;
				ArrayList<Integer> list = new ArrayList<Integer>();
				for (int i : array) list.add(i);
				return list;
			}
		}
		
		private final static String NULL = "null";
		private final static String DIV = "|";
		
		public <T extends Persistable> T persist(T x) throws ParseDataException, NumberFormatException {
			return persist(x, null);
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Persistable> T persist(T x, Class<? extends T> clazz) throws ParseDataException, NumberFormatException {
			String type;
			if (writeMode) {
				type = x == null ? NULL : x.getClass().getName();
				if (clazz != null) type = clazz.getName();
				persist(type);
				if (x != null) x.persist(this);
			} else {
				type = persist((String) null);
				if (type.equals(NULL)) {
					x = null;
				} else {
					if (x != null) {
						if (!x.getClass().getName().equals(type)) throw new ParseDataException();
					} else {
						x = (T) PersistUtils.construct(type);
					}
					x.persist(this);
				}
			}
//			Debug.write("end " + type);
			return x;
		}
		
		private String read() throws ParseDataException {
			return PersistUtils.read();
		}
		
		private void write(String data) {
			PersistUtils.write(data);
		}
	}
}
