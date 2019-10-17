import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
		multimap.containsKey(s);
	}

	@Test
	public void containsValue() {
		
	}
}
