import java.math.BigInteger;
import java.util.Map;
import java.util.WeakHashMap;

import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;

public class Test extends Thread {
    public final static int sectorExpiry = 300000; // 5 Minutes
    private boolean running = true;

    public static void main(String[] args) {
	Test t = new Test();
	SectorStorage sx = new SectorStorage();
	SectorLocation sl = new SectorLocation(0, 0, 0);
	sx.empty = true;
	sx.lastUsed = System.currentTimeMillis();
	t.map.put(sl, sx);
	sl = new SectorLocation(0,0,0);
	sx = new SectorStorage();
	sx.empty = true;
	sx.lastUsed = System.currentTimeMillis();
	t.map.put(sl, sx);
	//t.start();
	t.list(new SectorLocation(0, 0, 0));
    }

    private Map<SectorLocation, SectorStorage> map = new WeakHashMap<SectorLocation, SectorStorage>();
    //private Object mutex = new Object();

    public Test() {
	super("SectorManagerTest");
    }

    public void list(SectorLocation l) {
	for (SectorLocation loc : map.keySet()) {
	    if (loc.equals(l))
		System.out.println(loc.toString());
	}
    }

    @Override
    public void run() {
	System.out.println("Started Sector Manager Thread");
	for (SectorLocation loc : map.keySet()) {
	    System.out.println(loc.toString());
	}
	System.out.println("Killed Sector Manager Thread");
    }

    public static class SectorStorage {
	public long lastUsed;
	public Sector data;
	public boolean empty = false;
    }
}
