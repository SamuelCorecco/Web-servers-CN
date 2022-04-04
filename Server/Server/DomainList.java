package Server.Server;

import java.util.ArrayList;
import java.util.List;
import static Server.Server.Print.*;

public class DomainList {
    
    private final List<Domain> domains;

    public DomainList() {
        this.domains = new ArrayList<>();
    }

    public void addDomain(String hostname, String entryPointFile, String memberName, String memberEmail) {
        Domain d = new Domain(hostname, entryPointFile, memberName, memberEmail);
        domains.add(d);
    }

    /**
     * Check if a given host is specified in the hostname list
     * @param hostname
     * @return index of host if it exists, -1 if not
     */
    private int checkHostExists(String hostname) {
        for(int i = 0; i < domains.size(); i++) {
            Domain d = domains.get(i);
            if(d.getDomainName().equals(hostname)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get entry file name of specified host.
     * @param hostname hostname of which we want to know the entry-point file name
     * @return entry filename of specified host (or of default host if DNE)
     */
    public String getEntryPoint(String hostname) {
        int idx = checkHostExists(hostname);
        if(idx > 0) {
            return domains.get(idx).getEntryPointFile();
        } else {
            return getDefaultEntryPointFile();
        }
    }

    public String getMemberName(String hostname) {
        int idx = checkHostExists(hostname);
        if(idx > 0) {
            return domains.get(idx).getMemberName();
        } else {
            return domains.get(0).getMemberName();
        }
    }

    public String getMemberEmail(String hostname) {
        int idx = checkHostExists(hostname);
        if(idx > 0) {
            return domains.get(idx).getMemberEmail();
        } else {
            return domains.get(0).getMemberEmail();
        }
    }

    // === Default Host ===

    public String getDefaultHostname() {
        return domains.get(0).getDomainName();
    }

    public String getDefaultEntryPointFile() {
        return domains.get(0).getEntryPointFile();
    }

    public void printHostInfo(String hostname) {
        int idx = checkHostExists(hostname);
        if(idx > 0) {
            print(hostname + "," + getEntryPoint(hostname) + "," + getMemberName(hostname) + "," + getMemberEmail(hostname));
        } else {
            print("Host " + hostname + " does not exist.");
        }
    }

    public void printDomainList() {
        for(int i = 0; i < domains.size(); i++) {
            String d = domains.get(i).getDomainName();
            printHostInfo(d);
        }
    }

    // ===== Auxiliary Class =====

    public class Domain {
        private final String domainName;
        private final String entryPointFile;
        private final String memberFullName;
        private final String memberEmail;
    
        public Domain(String name, String entryPointFile, String memberName, String email) {
            this.domainName = name;
            this.entryPointFile = entryPointFile;
            this.memberFullName = memberName;
            this.memberEmail = email;
        }
    
        public String getDomainName() {
            return domainName;
        }
    
        public String getEntryPointFile() {
            return entryPointFile;
        }
    
        public String getMemberName() {
            return memberFullName;
        }
    
        public String getMemberEmail() {
            return memberEmail;
        }
    }
    
}
