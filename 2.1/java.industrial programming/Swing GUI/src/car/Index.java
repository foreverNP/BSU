package car;

import java.io.*;
import java.util.*;
import java.util.zip.*;

class KeyComp implements Comparator<String> {
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}

class KeyCompReverse implements Comparator<String> {
    public int compare(String o1, String o2) {
        return o2.compareTo(o1);
    }
}

interface IndexBase {
    String[] getKeys(Comparator<String> comp);

    void put(String key, long value);

    boolean contains(String key);

    Long[] get(String key);
}

class IndexOne2One implements Serializable, IndexBase {
    // Unique keys
    private static final long serialVersionUID = 3442L;

    private TreeMap<String, Long> map;

    public IndexOne2One() {
        map = new TreeMap<String, Long>();
    }

    public String[] getKeys(Comparator<String> comp) {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value) {
        map.put(key, value);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Long[] get(String key) {
        long pos = map.get(key).longValue();
        return new Long[]{pos};
    }
}

class IndexOne2N implements Serializable, IndexBase {
    // Not unique keys
    private static final long serialVersionUID = 11232L;

    private TreeMap<String, Vector<Long>> map;

    public IndexOne2N() {
        map = new TreeMap<String, Vector<Long>>();
    }

    public String[] getKeys(Comparator<String> comp) {
        String[] result = map.keySet().toArray(new String[0]);
        Arrays.sort(result, comp);
        return result;
    }

    public void put(String key, long value) {
        Vector<Long> arr = map.get(key);
        if (arr == null) {
            arr = new Vector<Long>();
        }
        arr.add(value);
        map.put(key, arr);
    }

    public void put(String keys, String keyDel, long value) {
        StringTokenizer st = new StringTokenizer(keys, keyDel);
        int num = st.countTokens();
        for (int i = 0; i < num; i++) {
            String key = st.nextToken();
            key = key.trim();
            put(key, value);
        }
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Long[] get(String key) {
        return map.get(key).toArray(new Long[0]);
    }
}

public class Index implements Serializable, Closeable {
    private static final long serialVersionUID = 1932L;

    IndexOne2One registrationNumber;
    IndexOne2N brand;
    IndexOne2N model;

    private transient String filename = null;

    public void test(CarRegistration regNum) {
        assert (regNum != null);

        if (registrationNumber.contains(regNum.getRegistrationNumber())) {
            throw new IllegalArgumentException("Key not unique: registration number");
        }
    }

    public void put(CarRegistration regNum, long value) {
        test(regNum);

        registrationNumber.put(regNum.getRegistrationNumber(), value);
        brand.put(regNum.getBrand(), value);
        model.put(regNum.getModel(), value);
    }

    public Index() {
        registrationNumber = new IndexOne2One();
        brand = new IndexOne2N();
        model = new IndexOne2N();
    }

    public static Index load(String name) throws IOException, ClassNotFoundException {
        Index obj = null;
        try {
            FileInputStream file = new FileInputStream(name);
            try (ZipInputStream zis = new ZipInputStream(file)) {
                ZipEntry zen = zis.getNextEntry();
                if (zen.getName().equals(Buffer.zipEntryName) == false) {
                    throw new IOException("Invalid block format");
                }
                try (ObjectInputStream ois = new ObjectInputStream(zis)) {
                    obj = (Index) ois.readObject();
                }
            }
        } catch (FileNotFoundException e) {
            obj = new Index();
        }
        if (obj != null) {
            obj.save(name);
        }
        return obj;
    }

    public void save(String name) {
        filename = name;
    }

    public void saveAs(String name) throws IOException {
        FileOutputStream file = new FileOutputStream(name);
        try (ZipOutputStream zos = new ZipOutputStream(file)) {
            zos.putNextEntry(new ZipEntry(Buffer.zipEntryName));
            zos.setLevel(ZipOutputStream.DEFLATED);
            try (ObjectOutputStream oos = new ObjectOutputStream(zos)) {
                oos.writeObject(this);
                oos.flush();
                zos.closeEntry();
                zos.flush();
            }
        }
    }

    public void close() throws IOException {
        saveAs(filename);
    }
}
