package headlessTests;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import collections.BiPredicateMultiMap;

public class BiPredicateMultiMapTests {

	BiPredicateMultiMap<String> transitiveMap = new BiPredicateMultiMap<>((x, y) -> x.length() == y.length());

	@BeforeEach
	public void reset() {
		transitiveMap.clear();
	}

	@ParameterizedTest
	@CsvSource({ "A, B, C", "12, 21, 65", "1dm, 10d, 10a", "9!a, (*y, M@$" })
	public void add(String input1, String input2, String input3) {
		transitiveMap.put(input2 + input2);
		transitiveMap.put(input3 + input3);
		transitiveMap.put(input1 + input1);
		transitiveMap.put(input2);
		transitiveMap.put(input2 + input2 + input2);
		transitiveMap.put(input1);
		transitiveMap.put(input1 + input1 + input1);
		assertAll("Transitivity is not held correctly!", () -> assertAll("Transitivity for 1 length elements not witheld!", () -> assertThat("Map is not transitive " + input1 + " + -> " + input2, transitiveMap.get(input1), hasItem(input2)), () -> assertThat("Map is not transitive " + input2 + " + -> " + input1, transitiveMap.get(input2), hasItem(input1))), () -> assertAll("Transitivity for 2 length elements not witheld!", () -> assertThat("Map is not transitive " + input1 + input1 + " + -> " + input2 + input2, transitiveMap.get(input1 + input1), hasItem(input2 + input2)), () -> assertThat("Map is not transitive " + input2 + input2 + " + -> " + input1 + input1, transitiveMap.get(input2 + input2), hasItem(input1 + input1)), () -> assertThat("Map is not transitive " + input1 + input1 + " + -> " + input3 + input3, transitiveMap.get(input1 + input1), hasItem(input3 + input3)),
				() -> assertThat("Map is not transitive " + input3 + input3 + " + -> " + input1 + input1, transitiveMap.get(input3 + input3), hasItem(input1 + input1)), () -> assertThat("Map is not transitive " + input2 + input2 + " + -> " + input3 + input3, transitiveMap.get(input2 + input2), hasItem(input3 + input3)), () -> assertThat("Map is not transitive " + input3 + input3 + " + -> " + input2 + input2, transitiveMap.get(input3 + input3), hasItem(input2 + input2))), () -> assertAll("Transitivity for 3 length elements not witheld!", () -> assertThat("Map is not transitive " + input1 + input1 + input1 + " + -> " + input2 + input2 + input2, transitiveMap.get(input1 + input1 + input1), hasItem(input2 + input2 + input2)), () -> assertThat("Map is not transitive " + input2 + input2 + input2 + " + -> " + input1 + input1 + input1, transitiveMap.get(input2 + input2 + input2), hasItem(input1 + input1 + input1))));
	}

	@Test
	public void add() {
		transitiveMap.put("A");
		transitiveMap.put("BB");
		assertThat(transitiveMap.get("A"), not(hasItem("BB")));
	}

}
