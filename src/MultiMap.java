import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiMap<K, V> extends HashMap<K, Set<V>> {

	private static final long serialVersionUID = 5254866262621327527L; // This is for serializable object compatability

	private HashMap<K, Set<V>> internalMap = new HashMap<>();

	@Override
	public void clear() {
		internalMap.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return internalMap.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		for(Set<V> set : internalMap.values()) {
			for (V value : set) {
				if (value.equals(arg0)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Set<Entry<K, Set<V>>> entrySet() {
		return internalMap.entrySet();
	}

	@Override
	public Set<V> get(Object arg0) {
		return internalMap.getOrDefault(arg0, new HashSet<>());
	}

	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return internalMap.keySet();
	}

	@Override
	public Set<V> put(K arg0, Set<V> arg1) {
		return internalMap.put(arg0, arg1);
	}

	public Set<V> put(K arg0, V arg1) {
		Set<V> set = null;
		if (internalMap.get(arg0) == null) {
			set = new HashSet<>();
		} else {
			set = internalMap.get(arg0);
		}
		set.add(arg1);
		put(arg0, set);
		return set;
	}

	@Override
	public void putAll(Map<? extends K, ? extends Set<V>> arg0) {
		internalMap.putAll(arg0);
	}

	@Override
	public Set<V> remove(Object arg0) {
		return internalMap.remove(arg0);
	}

	@Override
	public int size() {
		return internalMap.size();
	}

	@Override
	public Collection<Set<V>> values() {
		return internalMap.values();
	}

	public Collection<V> allValues() {
		ArrayList<V> list = new ArrayList<>();
		for(Set<V> set : values()) {
			for (V value : set) {
				list.add(value);
			}
		}
		return list;
	}

}
