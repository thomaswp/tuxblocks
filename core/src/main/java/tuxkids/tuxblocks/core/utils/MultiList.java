package tuxkids.tuxblocks.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A composite {@link List} which emulates containing the elements of
 * each of its sub lists. This is essentially a way to flatten a list of lists.
 */
public class MultiList<T> implements Iterable<T> {
	private List<List<? extends T>> lists = new ArrayList<List<? extends T>>();
	
	/** Provide a list of each list whose elements this list should encompass */
	public MultiList(List<? extends T>... lists) {
		for (List<? extends T> list : lists) this.lists.add(list);
	}
	
	/** Returns the combined size of all the lists */
	public int size() {
		int size = 0;
		for (List<?> list : lists) size += list.size();
		return size;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean contains(Object o) {
		for (List<?> list : lists) if (list.contains(o)) return true;
		return false;
	}

	/** Adds a List to the composite list */
	public boolean add(List<T> e) {
		return lists.add(e);
	}

	/** Removes a List from the composite list */
	public boolean remove(List<T> o) {
		return lists.remove(o);
	}
	
	/** Removes the given element from the first list which contains it */
	public boolean remove(T o) {
		for (List<?> list : lists) {
			if (list.remove(o)) return true;
		}
		return false;
	}

	/** Clears the list of lists (but not the lists themselves) */
	public void clear() {
		lists.clear();
	}

	/** Gets the element at the given index, assuming each list was flattened into one */
	public T get(int index) {
		for (int i = 0; i < lists.size(); i++) {
			List<? extends T> list = lists.get(i);
			if (index < list.size()) return list.get(index);
			index -= list.size();
		}
		throw new IndexOutOfBoundsException();
	}

	/** Returns the index of the given element, assmuning each list was flattened into one  */
	public int indexOf(T element) {
		int offset = 0;
		for (int i = 0; i < lists.size(); i++) {
			List<? extends T> list = lists.get(i);
			int index = list.indexOf(element);
			if (index >= 0) return index + offset;
			offset += list.size();
		}
		return -1;
	}

	/** Gets an {@link Iterator} over each element in each list */
	@Override
	public Iterator<T> iterator() {
		return new MultiIterator();
	}
	
	private class MultiIterator implements Iterator<T> {

		int index;
		
		@Override
		public boolean hasNext() {
			return index < size();
		}

		@Override
		public T next() {
			return get(index++);
		}

		@Override
		public void remove() {
			MultiList.this.remove(get(index));
		}
		
	}
}
