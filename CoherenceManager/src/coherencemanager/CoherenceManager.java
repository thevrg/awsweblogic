package coherencemanager;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.Member;
import com.tangosol.net.MemberEvent;
import com.tangosol.net.MemberListener;
import com.tangosol.net.NamedCache;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;

/**
 *
 * @author vrg
 */
public class CoherenceManager implements MemberListener {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, MalformedObjectNameException {
        CoherenceManager cm = new CoherenceManager();
    }
    private NamedCache cache;
    private List<CacheNode> cacheNodes;
    private MBeanServer mbeanServer;
    private int clusterFreeMem;
    private int clusterMaxMem;
    private int clusterNodes;

    public CoherenceManager() throws IOException, MalformedObjectNameException {
        cache = CacheFactory.getCache("session-storage");
        cache.getCacheService().addMemberListener(this);


        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        checkStorage();
        while (true) {
            try {
                Thread.sleep(20000L);
                refresh();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private synchronized void refresh() {
        clusterFreeMem = 0;
        clusterMaxMem = 0;
        clusterNodes = 0;
        for (CacheNode node : cacheNodes) {
            try {
                AttributeList attributes = mbeanServer.getAttributes(node.getObjectName(), new String[]{"MemoryAvailableMB", "MemoryMaxMB"});
                int freeMem = 0;
                int maxMem = 0;
                for (Attribute attribute : attributes.asList()) {
                    if ("MemoryAvailableMB".equals(attribute.getName())) {
                        freeMem = (Integer) attribute.getValue();
                    } else if ("MemoryMaxMB".equals(attribute.getName())) {
                        maxMem = (Integer) attribute.getValue();
                    }
                }
                clusterMaxMem += maxMem;
                clusterFreeMem += freeMem;
                clusterNodes++;
                int usedMem = maxMem - freeMem;
                String nodeName = node.getMember().getMemberName();
                System.out.println(nodeName + ".allMem=" + maxMem);
                System.out.println(nodeName + ".freeMem=" + freeMem);
                System.out.println(nodeName + ".usedMem=" + usedMem);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        double clusterRatio = 100D * clusterFreeMem / clusterMaxMem;

        System.out.println("Custer statistics (free/max/ratio)" + clusterFreeMem + "/" + clusterMaxMem + "/" + clusterRatio);
        int size = 0;
        try {
            size = cache.size();
        } catch (Exception ex) {
        }
        System.out.println("storage objects: " + size);
        AWSNotifier.getInstance().sendMetrics(clusterNodes, clusterFreeMem, clusterMaxMem, size, new Date());

    }

    private synchronized void checkStorage() {
        Iterator it = cache.getCacheService().getCluster().getMemberSet().iterator();
        cacheNodes = new ArrayList<CacheNode>();
        while (it.hasNext()) {
            try {
                Member member = (Member) it.next();
                String roleName = member.getRoleName();
                if ("CoherenceServer".equals(roleName)) {
                    cacheNodes.add(new CacheNode(member));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (cacheNodes.isEmpty()) {
            AWSNotifier.getInstance().sendMetrics(0, 0, 0, 0, new Date());
            return;
        }

        refresh();
    }

    @Override
    public void memberJoined(MemberEvent evt) {
        System.out.println("member Joined " + evt.getMember().getMachineName());
        checkStorage();
    }

    @Override
    public void memberLeaving(MemberEvent evt) {
        System.out.println("member Leaving " + evt.getMember().getMachineName());
        checkStorage();
    }

    @Override
    public void memberLeft(MemberEvent evt) {
        System.out.println("member Left " + evt.getMember().getMachineName());
        checkStorage();
    }
}
