import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Contact implements Comparable<Contact>, Iterable<String> {

	// argument exception
	public static class ArgException extends Exception {
		private static final long serialVersionUID = 1L;

		ArgException(String arg) {
			super("Invalid argument: " + arg);
		}
	}

	// area names
	public static final String[] names = { 
		"*Contact", 
		"*Mobile", 
		"Work",
		"Home", 
		"E-mail", 
		"WWW-page", 
		"Address" 
		};

	// format strings for area printout
	public static String[] formatStr = { 
		"%-9s", //
		"%-9s", //
		"%-9s", //
		"%-9s", //
		"%-17s", //
		"%-17s", //
		"%-9s" //
		};

	// sort
	public static String getSortByName(int sortBy) {
		return Contact.names[sortBy];
	}

	public static Comparator<Contact> getComparator(final int sortBy) {
		if (sortBy >= names.length || sortBy < 0) {
			throw new IndexOutOfBoundsException();
		}
		return new Comparator<Contact>() {
			@Override
			public int compare(Contact c0, Contact c1) {
				return c0.areas[sortBy].compareTo(c1.areas[sortBy]);
			}
		};
	}

	// checkers (stubs)
	protected boolean validName(String str) {
		return str != null && str.length() > 0;
	}

	protected boolean validMobile(String str) {
		return str != null && str.length() > 0;
	}

	protected boolean validPhone(String str) {
		return str != null;
	}

	protected boolean validAddress(String str) {
		return str != null;
	}

	protected boolean validEMail(String str) {
		return str != null;
	}

	protected boolean validWWWPage(String str) {
		return str != null;
	}

	// areas container
	private String[] areas = null;

	// indexator:
	public int length() {
		return areas.length;
	}

	public String getArea(int idx) {
		if (idx >= length() || idx < 0) {
			throw new IndexOutOfBoundsException();
		}
		return areas[idx];
	}

	public void setArea(int idx, String value) throws ArgException {
		if (idx >= length() || idx < 0) {
			throw new IndexOutOfBoundsException();
		}
		if ((idx == 0 && validName(value) == false)
				|| (idx == 1 && validMobile(value) == false)
				|| (idx > 1 && idx < 4 && validPhone(value) == false)
				|| (idx == 4 && validEMail(value) == false)
				|| (idx == 5 && validWWWPage(value) == false)
				|| (idx == 6 && validAddress(value) == false)) {
			throw new ArgException(value);
		}
		areas[idx] = value;
	}

	// Iterable<String>
	@Override
	public Iterator<String> iterator() {
		//Iterator<String>
		return new Iterator<String>() {
			//private Contact contact = thisContact;
			private int iterator_idx = -1;

			@Override
			public boolean hasNext() {
				return iterator_idx < (areas.length - 1);
			}

			@Override
			public String next() {
				if (iterator_idx < (areas.length - 1)) {
					return areas[++iterator_idx];
				} 
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	// Comparable<Contact>
	@Override
	public int compareTo(Contact cy) {
		return Contact.format(this).compareTo(Contact.format(cy));
	}

	// toString
	@Override
	public String toString() {
		if (areas == null) {
			return " | | | | | | ";
		}
		String res = areas[0];
		for (int i = 1; i < areas.length; i++) {
			res += "|" + areas[i];
		}
		return res;
	}

	// constructors:
	// public Contact() {}
	private void setup(String[] args) throws ArgException {
		if (args == null) {
			throw new ArgException("null pointer passed for args");
		}
		if (args.length < 2 || args.length > names.length) {
			throw new ArgException(Arrays.toString(args));
		}
		areas = new String[names.length];
		int i = 0;
		for (; i < args.length; i++) {
			setArea(i, args[i]);
		}
		while (i < names.length) {
			areas[i++] = "";
		}
	}

	public Contact(String str) throws ArgException {
		if (str == null) {
			throw new ArgException("null pointer passed for str");
		}
		setup(str.split("\\|"));
	}

	public Contact(String... args) throws ArgException {
		setup(args);
	}

	private static String format(Iterable<String> what) {
		String result = "";
		int idx = 0;
		for (String str : what) {
			result += String.format(formatStr[idx++], str);
		}
		return result;
	}

	public static String format() {
		return Contact.format(Arrays.asList(Contact.names));
	}

	public static String format(Contact cn) {
		return Contact.format(((Iterable<String>) cn));
	}
}
