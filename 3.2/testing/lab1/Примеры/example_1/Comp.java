import java.util.*;

public class Comp implements Comparable<Comp>, Iterable<Object>, Iterator<Object> {

	private String str;
	private int number;

	public Comp(String str, int number) {
		this.str = str;
		this.number = number;
	}

	@Override
	public int compareTo(Comp entry) {

		int result = str.compareTo(entry.str);
		if (result != 0) {
			return result;
		}

		result = number - entry.number;
		if (result != 0) {
			return (int) result / Math.abs(result);
		}
		return 0;
	}

	@Override
	public Iterator<Object> iterator() {
		resetIterator();
		return this;
	}

	private int iterator_idx = -1;

	private void resetIterator() {
		iterator_idx = -1;
	}

	@Override
	public boolean hasNext() {
		return iterator_idx < 1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object next() {
		++iterator_idx;
		if (iterator_idx == 0) {
			return str;
		}
		if (iterator_idx == 1) {
			return new Integer(number);
		} 
		throw new NoSuchElementException();
	}
}
