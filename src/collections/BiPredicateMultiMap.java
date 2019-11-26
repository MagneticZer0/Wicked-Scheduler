package collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.List;

/**
 * This is an extension of Java's HashMap, it maps one key to one, or many,
 * different values. Values cannot be duplicated for a given key.
 * 
 * @author MagneticZero
 *
 * @param <T> The type of elements stored within the map
 */
public class BiPredicateMultiMap<T> extends MultiMap<T, T> {

	/**
	 * This is for serializable object compatability
	 */
	private static final long serialVersionUID = 278891569418788532L;

	/**
	 * The internal implementation of the MultiMap is a HashMap that maps keys to a
	 * list of values.
	 */
	private BiPredicate<T, T> predicate;

	public BiPredicateMultiMap(BiPredicate<T, T> predicate) {
		this.predicate = predicate;
	}

	/**
	 * Adds the specified value with the specified key in this map. If the map
	 * previously contained a mapping for the key, the new value is added to the
	 * list of values.
	 *
	 * @param arg0 key with which the specified value is to be associated
	 * @param arg1 value to be added with the specified key
	 * @return the previous value associated with <tt>key</tt>, or an empty list if
	 *         there was no mapping for <tt>key</tt>.
	 */
	public List<T> put(T arg0) {
		if (!containsKey(arg0)) {
			boolean added = false;
			Set<T> set = new HashSet<>(keySet());
			for (T key : set) {
				if (predicate.test(arg0, key)) {
					putSingle(arg0, key);
					putSingle(key, arg0);
					added = true;
				}
			}
			if (!added) {
				put(arg0, new ArrayList<>());
			}
		}
		return get(arg0);
	}
}
