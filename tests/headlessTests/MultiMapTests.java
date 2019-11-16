package headlessTests;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import collections.MultiMap;

public class MultiMapTests {

	MultiMap<String, Integer> multimap;

	@BeforeEach
	public void setup() {
		multimap = new MultiMap<>();
		multimap.putSingle("A", 1);
		multimap.putSingle("A", 2);
		multimap.putSingle("B", 2);
		multimap.putSingle("B", 2);
		multimap.putSingle("B", 3);
	}

	@Test
	public void clear() {
		assertFalse(multimap.isEmpty(), "MultiMap should not be empty yet!");
		multimap.clear();
		assertTrue(multimap.isEmpty(), "MultiMap is not empty after clearing!");
	}

	@DisplayName("Doesn't contain C, contain's A")
	@Test
	public void containsKey() {
		assertAll("MultiMap containsKey tests failed!", () -> assertFalse(multimap.containsKey("C"), "MultiMap should not contain key \"C\""), () -> assertTrue(multimap.containsKey("A"), "MultiMap should contain key \"A\""));
	}

	@ParameterizedTest
	@ValueSource(strings = { "A", "B" })
	public void containsKey2(String s) {
		assertTrue(multimap.containsKey(s), "MultiMap doesn't contain keys put in!");
	}

	@Test
	public void containsValue() {
		assertAll("MultiMap doesn't contain values put in!", () -> assertTrue(multimap.containsValue(1), "MultiMap should contain int \"1\""), () -> assertTrue(multimap.containsValue(2), "MultiMap should contain int \"2\""));
	}

	@Test
	public void entrySet() {
		assertEquals(2, multimap.entrySet().size(), "MultiMap entry set doesn't contain only 2 entries");
	}

	@Test
	public void get() {
		assertTrue(multimap.get(new Object()).isEmpty(), "Key wasn't put in, but it has values");
	}

	@ParameterizedTest
	@CsvSource({ "A, 1", "A, 2", "B, 2" })
	public void get(String key, int value) {
		assertTrue(multimap.get(key).contains(value), "MultiMap does not contain expected values");
	}

	@Test
	public void get2() {
		assertEquals(2, multimap.get("B").size(), "MultiMap put in duplicate values!");
	}

	@Test
	public void keySet() {
		assertEquals(2, multimap.keySet().size(), "MultiMap key set doesn't contain only 2 entries");
	}

	@Test
	public void remove() {
		List<Integer> removeValues = multimap.remove("A");
		assertAll("MultiMap key removal didn't work!", () -> assertTrue(removeValues.contains(1) && removeValues.contains(2), "MultiMap key removal didn't return correct values"), () -> assertTrue(multimap.get("A").isEmpty(), "MultiMap key remove didn't remove key"));
	}

	@Test
	public void size() {
		assertEquals(2, multimap.size(), "MultiMap size should only be 2");
	}

	@Test
	public void values() {
		for (List<Integer> values : multimap.values()) {
			assertTrue(values.contains(2), "MultiMap values doesns't contain expected result");
		}
	}

	@Test
	public void allValues() {
		assertAll("MultiMap all values doesnt' contain all values!", () -> assertTrue(multimap.allValues().contains(1), "MultiMap allValues doesn't contain int \"1\"!"), () -> assertTrue(multimap.allValues().contains(2), "MultiMap allValues doesn't contain int \"2\"!"), () -> assertTrue(multimap.allValues().contains(3), "MultiMap allValues doesn't contain int \"3\"!"));
	}

	@Test
	public void equals() {
		MultiMap<String, Integer> cloneMap = new MultiMap<>();
		cloneMap.putSingle("A", 1);
		cloneMap.putSingle("A", 2);
		cloneMap.putSingle("B", 2);
		cloneMap.putSingle("B", 2);
		cloneMap.putSingle("B", 3);
		assertEquals(cloneMap, multimap, "MultiMap equals method doesn't correctly work!");
	}

	@Test
	public void equals2() {
		MultiMap<Character, Object> notEqualMap = new MultiMap<>();
		assertNotEquals(notEqualMap, multimap, "MultiMap equals method doesn't correctly work!");
	}
}
