package etithespirit.etimod.util.collection;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentBag<T> extends ConcurrentHashMap<T, T> implements Iterable<T> {
	
	private static final long serialVersionUID = -6864871734920425388L;
	
	public void add(T item) {
		this.put(item, item);
	}

	@Override
	public Iterator<T> iterator() {
		return keySet().iterator();
	}


}
