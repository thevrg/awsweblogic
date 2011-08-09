package coherencemanager;

import com.tangosol.net.Member;
import com.tangosol.util.UID;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 *
 * @author vrg
 */
public class CacheNode {

    private final Member member;
    private final ObjectName objectName;

    public CacheNode(Member member) throws MalformedObjectNameException {
        this.member = member;
        this.objectName = new ObjectName("Coherence:type=Node,nodeId=" + member.getId());
    }

    public Member getMember() {
        return member;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    @Override
    public String toString() {
        String machineName = member.getMachineName();
        int machineId = member.getMachineId();
        String pid = member.getProcessName();
        String roleName = member.getRoleName();
        int id = member.getId();
        UID uid = member.getUid();
        String hostName = member.getAddress().getHostName();
        return "Member {machineName=" + machineName
                + ", hostName=" + hostName
                + ", machineId=" + machineId
                + ", pid=" + pid
                + ", roleName=" + roleName
                + ", id=" + id
                + ", uid=" + uid + "}";
    }
}
