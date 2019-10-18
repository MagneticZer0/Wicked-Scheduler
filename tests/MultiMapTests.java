import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class MultiMapTests {

	MultiMap<String, Integer> multimap;

	@BeforeEach
	public void setup() {
		multimap = new MultiMap<>();
		multimap.put("A", 1);
		multimap.put("A", 2);
		multimap.put("B", 2);
		multimap.put("B", 2);
		multimap.put("B", 3);
	}

	@Test
	public void clear() {
		assertFalse("MultiMap should not be empty yet!", multimap.isEmpty());
		multimap.clear();
		assertTrue("MultiMap is not empty after clearing!", multimap.isEmpty());
	}

	@DisplayName("Doesn't contain C, contain's A")
	@Test
	public void containsKey() {
		assertAll("MultiMap containsKey tests failed!", () -> assertFalse("MultiMap should not contain key \"C\"", multimap.containsKey("C")), () -> assertTrue("MultiMap should contain key \"A\"", multimap.containsKey("A")));
	}

	@ParameterizedTest
	@ValueSource(strings = {"A", "B"})
	public void containsKey2(String s) {
		assertTrue("MultiMap doesn't contain keys put in!", multimap.containsKey(s));
	}

	@Test
	public void containsValue() {
		assertAll("MultiMap doesn't contain values put in!", () -> assertTrue("MultiMap should contain int \"1\"", multimap.containsValue(1)), () -> assertTrue("MultiMap should contain int \"2\"", multimap.containsValue(2)));	
	}

	@Test
	public void entrySet() {
		assertEquals("MultiMap entry set doesn't contain only 2 entries", 2, multimap.entrySet().size());
	}

	@Test
	public void get() {
		assertTrue("Key wasn't put in, but it has values", multimap.get(new Object()).isEmpty());
	}

	@ParameterizedTest
	@CsvSource({"A, 1", "A, 2", "B, 2"})
	public void get(String key, int value) {
		assertTrue("MultiMap does not contain expected values", multimap.get(key).contains(value));
	}

	@Test
	public void get2() {
		assertEquals("MultiMap put in duplicate values!", 2, multimap.get("B").size());
	}

	@Test
	public void keySet() {
		assertEquals("MultiMap key set doesn't contain only 2 entries", 2, multimap.keySet().size());
	}

	@Test
	public void remove() {
		List<Integer> removeValues = multimap.remove("A");
		assertAll("MultiMap key removal didn't work!", () -> assertTrue("MultiMap key removal didn't return correct values", removeValues.contains(1) && removeValues.contains(2)), () -> assertTrue("MultiMap key remove didn't remove key", multimap.get("A").isEmpty()));
	}

	@Test
	public void size() {
		assertEquals("MultiMap size should only be 2", 2, multimap.size());
	}

	@Test
	public void values() {
		for(List<Integer> values : multimap.values()) {
			assertTrue("MultiMap values doesns't contain expected result", values.contains(2));
		}
	}

	@Test
	public void allValues() {
		assertAll("MultiMap all values doesnt' contain all values!", () -> assertTrue("MultiMap allValues doesn't contain int \"1\"!", multimap.allValues().contains(1)), () -> assertTrue("MultiMap allValues doesn't contain int \"2\"!", multimap.allValues().contains(2)), () -> assertTrue("MultiMap allValues doesn't contain int \"3\"!", multimap.allValues().contains(3)));
	}

	@Test
	public void equals() {
		MultiMap<String, Integer> cloneMap = new MultiMap<>();
		cloneMap.put("A", 1);
		cloneMap.put("A", 2);
		cloneMap.put("B", 2);
		cloneMap.put("B", 2);
		cloneMap.put("B", 3);
		assertEquals("MultiMap equals method doesn't correctly work!", cloneMap, multimap);
	}

	@Test
	public void equals2() {
		MultiMap<Character, Object> notEqualMap = new MultiMap<>();
		assertNotEquals("MultiMap equals method doesn't correctly work!", notEqualMap, multimap);
	}
}
