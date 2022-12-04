package etithespirit.orimod.util.collection;

import java.util.ArrayList;
import java.util.List;

public final class Breaker {
	
	private Breaker() {}
	
	/**
	 * Breaks the given list in half at the given element (around). If includeAroundIn is -1, the element `around` will be discarded.
	 * If includeAroundIn is 0, it will be included in the first half. If includeAroundIn is 1, it will be included in the second half. If it is anything else, {@link IllegalArgumentException} will be raised.
	 * @param list The list to split.
	 * @param around The element to split around.
	 * @param includeAroundIn -1 to remove around from both result lists, 0 to include it in the first list, or 1 to include it in the second list.
	 * @param <T> The type of the list.
	 * @return Two lists that represent the contents of the input list.
	 * @throws NullPointerException If around is not part of list.
	 * @throws IllegalArgumentException If includeAroundIn is not -1, 0, or 1.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T>[] breakApart(List<T> list, T around, int includeAroundIn) throws NullPointerException, IllegalArgumentException {
		int index = list.indexOf(around);
		if (index == -1) throw new NullPointerException("Parameter 'around' is not a part of the input list.");
		List<T> alpha = new ArrayList<>();
		List<T> bravo = new ArrayList<>();
		if (includeAroundIn == -1) {
			for (int idx = 0; idx < index - 1; idx++) {
				alpha.add(list.get(idx));
			}
			for (int idx = index + 1; idx < list.size(); idx++) {
				bravo.add(list.get(idx));
			}
		} else if (includeAroundIn == 0) {
			for (int idx = 0; idx < index; idx++) {
				alpha.add(list.get(idx));
			}
			for (int idx = index + 1; idx < list.size(); idx++) {
				bravo.add(list.get(idx));
			}
		} else if (includeAroundIn == 1) {
			for (int idx = 0; idx < index - 1; idx++) {
				alpha.add(list.get(idx));
			}
			for (int idx = index; idx < list.size(); idx++) {
				bravo.add(list.get(idx));
			}
		} else {
			throw new IllegalArgumentException("Parameter 'around' is not -1, 0, or 1!");
		}
		return (List<T>[])(new List<?>[] { alpha, bravo });
	}
	
}
