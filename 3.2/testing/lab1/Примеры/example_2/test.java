import java.util.Arrays;

class test {
	static void sortAndPrint(Contact[] pl) {
		// printout in table
		System.out.println("----- Sorted in natural order: ");
		Arrays.sort(pl);
		System.out.printf(Contact.format());
		System.out.println();
		for (Contact cnt : pl) {
			System.out.println(Contact.format(cnt));
		}
	}

	static void sortAndPrint(Contact[] pl, int sortBy) {
		// printout in table
		System.out.println("----- Sorted by: " + Contact.getSortByName(sortBy));
		Arrays.sort(pl, Contact.getComparator(sortBy));
		System.out.printf(Contact.format());
		System.out.println();
		for (Contact cnt : pl) {
			System.out.println(Contact.format(cnt));
		}
	}

	public static void main(String[] args) {
		try {
			//Create array of contacts:
			Contact[] pl = new Contact[4];
			pl[0] = new Contact("Joahn|1234567|9876543||joahn@gmail.com||");
			pl[1] = new Contact("Ann|2345678|8765432||nann@gmail.com||");
			pl[2] = new Contact("Mary", "3456789", "7654321", "", "mary@gmail.com");
			pl[3] = new Contact("Empty|0000000|||||");
			//Test Comparable:
			sortAndPrint(pl);
			//Test Comparator:
			sortAndPrint(pl, 0);
			sortAndPrint(pl, 1);
			sortAndPrint(pl, 2);
			sortAndPrint(pl, 4);
			// reconstruct object from result of toString();
			Contact c = pl[1];
			System.out.println("Source contact:\n\t" + Contact.format(c));
			c = new Contact(c.toString());
			System.out.println("Reconstructed contact:\n\t" + Contact.format(c));
			// exception test:
			new Contact("Test exception object");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}
