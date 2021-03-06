package collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This is an extension of Java's HashMap, it maps one key to one, or many,
 * different values. Values cannot be duplicated for a given key.
 * 
 * @param <K> The type of keys maintained by the MultiMap
 * @param <V> The type of mapped values
 */
public class MultiMap<K, V> extends HashMap<K, List<V>> {

	/**
	 * This is for serializable object compatibility
	 */
	private static final long serialVersionUID = 6098566057097491786L;

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified
	 * value.
	 *
	 * @param arg0 value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps one or more keys to the specified
	 *         value
	 */
	@Override
	public boolean containsValue(Object arg0) {
		for (List<V> list : super.values()) {
			for (V value : list) {
				if (value.equals(arg0)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the value to which the specified key is mapped, or an empty List if
	 * this map contains no mapping for the key.
	 *
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a value
	 * {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise it returns an
	 * empty List. (There can be at most one such mapping.)
	 *
	 * <p>
	 * A return value of an empty List does indicate that the map contains no
	 * mapping for the key; it's also possible that the map explicitly maps the key
	 * to an empty List. The {@link #containsKey containsKey} operation may be used
	 * to distinguish these two cases.
	 *
	 * @param arg0 The key to get the value(s) of
	 * @return The value(s) for the given key, or an empty List if none exist.
	 * @see #put(Object, Object)
	 */
	@Override
	public List<V> get(Object arg0) {
		return super.getOrDefault(arg0, new ArrayList<>());
	}

	/**
	 * Adds the specified value with the specified key in this map. If the map
	 * previously contained a mapping for the key, the new value is added to the
	 * List.
	 *
	 * @param arg0 key with which the specified value is to be associated
	 * @param arg1 value to be added with the specified key
	 * @return the previous value associated with <tt>key</tt>, or an empty List if
	 *         there was no mapping for <tt>key</tt>. (An empty List return can also
	 *         indicate that the map previously associated an empty List with
	 *         <tt>key</tt>.)
	 */
	public List<V> putSingle(K arg0, V arg1) {
		List<V> list = null;
		if (super.get(arg0) == null) {
			list = new ArrayList<>();
		} else {
			list = super.get(arg0);
		}
		if (!list.contains(arg1)) {
			list.add(arg1);
		}
		put(arg0, list);
		return list;
	}

	/**
	 * Returns a {@link Collection} view of the values contained in this map. The
	 * collection is backed by the map, so changes to the map are reflected in the
	 * collection, and vice-versa. If the map is modified while an iteration over
	 * the collection is in progress (except through the iterator's own
	 * <tt>remove</tt> operation), the results of the iteration are undefined. The
	 * collection supports element removal, which removes the corresponding mapping
	 * from the map, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations. It does
	 * not support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a view of the values contained in this map
	 */
	public Collection<V> allValues() {
		List<V> list = new ArrayList<>();
		for (List<V> List : values()) {
			for (V value : List) {
				list.add(value);
			}
		}
		return list;
	}

	/**
	 * Compares the specified object with this map for equality. Returns
	 * <tt>true</tt> if the given object is also a map and the two maps represent
	 * the same mappings. More formally, two maps <tt>m1</tt> and <tt>m2</tt>
	 * represent the same mappings if
	 * <tt>m1.entryList().equals(m2.entryList())</tt>. This ensures that the
	 * <tt>equals</tt> method works properly across different implementations of the
	 * <tt>Map</tt> interface.
	 *
	 * <p>
	 * This implementation first checks if the specified object is this map; if so
	 * it returns <tt>true</tt>. Then, it checks if the specified object is a map
	 * whose size is identical to the size of this map; if not, it returns
	 * <tt>false</tt>. If so, it iterates over this map's <tt>entryList</tt>
	 * collection, and checks that the specified map contains each mapping that this
	 * map contains. If the specified map fails to contain such a mapping,
	 * <tt>false</tt> is returned. If the iteration completes, <tt>true</tt> is
	 * returned.
	 *
	 * @param o object to be compared for equality with this map
	 * @return <tt>true</tt> if the specified object is equal to this map
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (!(o instanceof MultiMap)) {
			return false;
		} else {
			return super.equals(o);
		}
	}
}
