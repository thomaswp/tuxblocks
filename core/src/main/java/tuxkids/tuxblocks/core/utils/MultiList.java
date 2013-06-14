package tuxkids.tuxblocks.core.utils;

import java.util.ArrayList;
import java.util.List;

public class MultiList<T> {
	private List<List<? extends T>> lists = new ArrayList<List<? extends T>>();

	public MultiList(List<? extends T>... lists) {
		for (List<? extends T> list : lists) this.lists.add(list);
	}
	
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

	public boolean add(List<T> e) {
		return lists.add(e);
	}

	public boolean remove(List<T> o) {
		return lists.remove(o);
	}
	
	public boolean remove(T o) {
		for (List<?> list : lists) {
			if (list.remove(o)) return true;
		}
		return false;
	}

	public void clear() {
		lists.clear();
	}

	public T get(int index) {
		for (int i = 0; i < lists.size(); i++) {
			List<? extends T> list = lists.get(i);
			if (index < list.size()) return list.get(index);
			index -= list.size();
		}
		throw new IndexOutOfBoundsException();
	}
}
