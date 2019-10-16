import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is an extension of Java's HashMap, it maps one key to one, or many,
 * different values. Values cannot be duplicated for a given key.
 * 
 * @author MagneticZero
 *
 * @param <K> The type of keys maintained by the MultiMap
 * @param <V> The type of mapped values
 */
public class MultiMap<K, V> extends HashMap<K, Set<V>> {

	/**
	 * This is for serializable object compatability
	 */
	private static final long serialVersionUID = 5254866262621327527L;

	/**
	 * The internal implementation of the MultiMap is a HashMap that maps keys to a
	 * Set of values, I use Set because there's no need for having duplicates values
	 * as far as I can think of.
	 */
	private HashMap<K, Set<V>> internalMap = new HashMap<>();

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
		for (Set<V> set : internalMap.values()) {
			for (V value : set) {
				if (value.equals(arg0)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this map. The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own <tt>remove</tt> operation, or
	 * through the <tt>setValue</tt> operation on a map entry returned by the
	 * iterator) the results of the iteration are undefined. The set supports
	 * element removal, which removes the corresponding mapping from the map, via
	 * the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	@Override
	public Set<Entry<K, Set<V>>> entrySet() {
		return internalMap.entrySet();
	}

	/**
	 * Returns the value to which the specified key is mapped, or an empty set if
	 * this map contains no mapping for the key.
	 *
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a value
	 * {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise it returns an
	 * empty set. (There can be at most one such mapping.)
	 *
	 * <p>
	 * A return value of an empty set does indicate that the map contains no mapping
	 * for the key; it's also possible that the map explicitly maps the key to an
	 * empty set. The {@link #containsKey containsKey} operation may be used to
	 * distinguish these two cases.
	 *
	 * @param arg0 The key to get the value(s) of
	 * @return The value(s) for the given key, or an empty set if none exist.
	 * @see #put(Object, Object)
	 */
	@Override
	public Set<V> get(Object arg0) {
		return internalMap.getOrDefault(arg0, new HashSet<>());
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
	 * Returns a {@link Set} view of the keys contained in this map. The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own <tt>remove</tt> operation), the
	 * results of the iteration are undefined. The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the keys contained in this map
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
	 * @return the previous value associated with <tt>key</tt>, or an empty set if
	 *         there was no mapping for <tt>key</tt>. (An empty set return can also
	 *         indicate that the map previously associated an empty set with
	 *         <tt>key</tt>.)
	 */
	@Override
	public Set<V> put(K arg0, Set<V> arg1) {
		return internalMap.put(arg0, arg1);
	}

	/**
	 * Adds the specified value with the specified key in this map. If the map
	 * previously contained a mapping for the key, the new value is added to the
	 * set.
	 *
	 * @param arg0 key with which the specified value is to be associated
	 * @param arg1 value to be added with the specified key
	 * @return the previous value associated with <tt>key</tt>, or an empty set if
	 *         there was no mapping for <tt>key</tt>. (An empty set return can also
	 *         indicate that the map previously associated an empty set with
	 *         <tt>key</tt>.)
	 */
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

	/**
	 * Copies all of the mappings from the specified map to this map. These mappings
	 * will replace any mappings that this map had for any of the keys currently in
	 * the specified map.
	 *
	 * @param arg0 mappings to be stored in this map
	 * @throws NullPointerException if the specified map is null
	 */
	@Override
	public void putAll(Map<? extends K, ? extends Set<V>> arg0) {
		internalMap.putAll(arg0);
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 *
	 * @param arg0 key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or an empty set if
	 *         there was no mapping for <tt>key</tt>. (An empty set return can also
	 *         indicate that the map previously associated an empty set with
	 *         <tt>key</tt>.)
	 */
	@Override
	public Set<V> remove(Object arg0) {
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
	 * Returns a {@link Collection} view of the sets of values contained in this
	 * map. The collection is backed by the map, so changes to the map are reflected
	 * in the collection, and vice-versa. If the map is modified while an iteration
	 * over the collection is in progress (except through the iterator's own
	 * <tt>remove</tt> operation), the results of the iteration are undefined. The
	 * collection supports element removal, which removes the corresponding mapping
	 * from the map, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations. It does
	 * not support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a view of the set of values contained in this map
	 */
	@Override
	public Collection<Set<V>> values() {
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
		for (Set<V> set : values()) {
			for (V value : set) {
				list.add(value);
			}
		}
		return list;
	}

	/**
	 * Compares the specified object with this map for equality. Returns
	 * <tt>true</tt> if the given object is also a map and the two maps represent
	 * the same mappings. More formally, two maps <tt>m1</tt> and <tt>m2</tt>
	 * represent the same mappings if <tt>m1.entrySet().equals(m2.entrySet())</tt>.
	 * This ensures that the <tt>equals</tt> method works properly across different
	 * implementations of the <tt>Map</tt> interface.
	 *
	 * <p>
	 * This implementation first checks if the specified object is this map; if so
	 * it returns <tt>true</tt>. Then, it checks if the specified object is a map
	 * whose size is identical to the size of this map; if not, it returns
	 * <tt>false</tt>. If so, it iterates over this map's <tt>entrySet</tt>
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
