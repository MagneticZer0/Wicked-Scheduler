package collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * This is an extension of Java's HashMap, it maps one key to one, or many,
 * different values. Values cannot be duplicated for a given key.
 * 
 * @author MagneticZero
 *
 * @param <K> The type of keys maintained by the MultiMap
 * @param <V> The type of mapped values
 */
public class MultiMap<K, V> extends HashMap<K, List<V>> {

	/**
	 * This is for serializable object compatability
	 */
	private static final long serialVersionUID = 5254866262621327527L;

	/**
	 * The internal implementation of the MultiMap is a HashMap that maps keys to a
	 * List of values, I use List because there's no need for having duplicates values
	 * as far as I can think of.
	 */
	private HashMap<K, List<V>> internalMap = new HashMap<>();

	/**
	 * Removes all of the mappings from this map. The map will be empty after this
	 * call returns.
	 */
	@Override
	public void clear() {
		internalMap.clear();
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified key.
	 *
	 * @param arg0 The key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified key.
	 */
	@Override
	public boolean containsKey(Object arg0) {
		return internalMap.containsKey(arg0);
	}

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
		for (List<V> List : internalMap.values()) {
			for (V value : List) {
				if (value.equals(arg0)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a {@link List} view of the mappings contained in this map. The List is
	 * backed by the map, so changes to the map are reflected in the List, and
	 * vice-versa. If the map is modified while an iteration over the List is in
	 * progress (except through the iterator's own <tt>remove</tt> operation, or
	 * through the <tt>ListValue</tt> operation on a map entry returned by the
	 * iterator) the results of the iteration are undefined. The List supports
	 * element removal, which removes the corresponding mapping from the map, via
	 * the <tt>Iterator.remove</tt>, <tt>List.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a List view of the mappings contained in this map
	 */
	@Override
	public Set<Entry<K, List<V>>> entrySet() {
		return internalMap.entrySet();
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
	 * A return value of an empty List does indicate that the map contains no mapping
	 * for the key; it's also possible that the map explicitly maps the key to an
	 * empty List. The {@link #containsKey containsKey} operation may be used to
	 * distinguish these two cases.
	 *
	 * @param arg0 The key to get the value(s) of
	 * @return The value(s) for the given key, or an empty List if none exist.
	 * @see #put(Object, Object)
	 */
	@Override
	public List<V> get(Object arg0) {
		return internalMap.getOrDefault(arg0, new ArrayList<>());
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	/**
	 * Returns a {@link List} view of the keys contained in this map. The List is
	 * backed by the map, so changes to the map are reflected in the List, and
	 * vice-versa. If the map is modified while an iteration over the List is in
	 * progress (except through the iterator's own <tt>remove</tt> operation), the
	 * results of the iteration are undefined. The List supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>List.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a List view of the keys contained in this map
	 */
	@Override
	public Set<K> keySet() {
		return internalMap.keySet();
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map
	 * previously contained a mapping for the key, the old value is replaced.
	 *
	 * @param arg0 key with which the specified value is to be associated
	 * @param arg1 value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or an empty List if
	 *         there was no mapping for <tt>key</tt>. (An empty List return can also
	 *         indicate that the map previously associated an empty List with
	 *         <tt>key</tt>.)
	 */
	@Override
	public List<V> put(K arg0, List<V> arg1) {
		return internalMap.put(arg0, arg1);
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
	public List<V> put(K arg0, V arg1) {
		List<V> list = null;
		if (internalMap.get(arg0) == null) {
			list = new ArrayList<>();
		} else {
			list = internalMap.get(arg0);
		}
		if (!list.contains(arg1)) {
			list.add(arg1);
		}
		put(arg0, list);
		return list;
	}

	/**
	 * Copies all of the mappings from the specified map to this map. These mappings
	 * will replace any mappings that this map had for any of the keys currently in
	 * the specified map.
	 *
	 * @param arg0 mappings to be stored in this map
	 * @throws NullPointerException if the specified map is null
	 */
	@Override
	public void putAll(Map<? extends K, ? extends List<V>> arg0) {
		internalMap.putAll(arg0);
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 *
	 * @param arg0 key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or an empty List if
	 *         there was no mapping for <tt>key</tt>. (An empty List return can also
	 *         indicate that the map previously associated an empty List with
	 *         <tt>key</tt>.)
	 */
	@Override
	public List<V> remove(Object arg0) {
		return internalMap.remove(arg0);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return the number of key-value mappings in this map
	 */
	@Override
	public int size() {
		return internalMap.size();
	}

	/**
	 * Returns a {@link Collection} view of the Lists of values contained in this
	 * map. The collection is backed by the map, so changes to the map are reflected
	 * in the collection, and vice-versa. If the map is modified while an iteration
	 * over the collection is in progress (except through the iterator's own
	 * <tt>remove</tt> operation), the results of the iteration are undefined. The
	 * collection supports element removal, which removes the corresponding mapping
	 * from the map, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations. It does
	 * not support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a view of the List of values contained in this map
	 */
	@Override
	public Collection<List<V>> values() {
		return internalMap.values();
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
		ArrayList<V> list = new ArrayList<>();
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
	 * represent the same mappings if <tt>m1.entryList().equals(m2.entryList())</tt>.
	 * This ensures that the <tt>equals</tt> method works properly across different
	 * implementations of the <tt>Map</tt> interface.
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
