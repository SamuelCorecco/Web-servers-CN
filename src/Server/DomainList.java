package src.Server;

import java.util.ArrayList;
import java.util.List;
import static src.Server.Print.*;

public class DomainList {
    
    private final List<Domain> domains;

    /**
     * Initialise the domain list
     */
    public DomainList() {
        this.domains = new ArrayList<>();
    }

    /**
     * Add a domain
     * @param hostname hostname
     * @param entryPointFile entry file for the given host (eg: home.html)
     * @param memberName full name of member
     * @param memberEmail email of member
     */
    public void addDomain(String hostname, String entryPointFile, String memberName, String memberEmail) {
        Domain d = new Domain(hostname, entryPointFile, memberName, memberEmail);
        domains.add(d);
    }

    /**
     * Check if a given host is specified in the hostname list
     * @param hostname
     * @return index of host if it exists, -1 if not
     */
    public int checkHostExists(String hostname) {
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

    /**
     * Get member name of the given host
     * @param hostname hostname
     * @return member name
     */
    public String getMemberName(String hostname) {
        int idx = checkHostExists(hostname);
        if(idx > 0) {
            return domains.get(idx).getMemberName();
        } else {
            return domains.get(0).getMemberName();
        }
    }

    /**
     * Get email associated with a given hostname
     * @param hostname hostname
     * @return email
     */
    public String getMemberEmail(String hostname) {
        int idx = checkHostExists(hostname);
        if(idx > 0) {
            return domains.get(idx).getMemberEmail();
        } else {
            return domains.get(0).getMemberEmail();
        }
    }

    // === Default Host ===

    /**
     * Get default hostname
     * @return hostname
     */
    public String getDefaultHostname() {
        return domains.get(0).getDomainName();
    }

    /**
     * Get the entry point file of the default host
     * @return path to file
     */
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

    /**
     * Print list of domains with their information
     */
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
    
        /**
         * Constructor for domain
         * @param name domain name
         * @param entryPointFile entry point file of host
         * @param memberName member name
         * @param email member email
         */
        public Domain(String name, String entryPointFile, String memberName, String email) {
            this.domainName = name;
            this.entryPointFile = entryPointFile;
            this.memberFullName = memberName;
            this.memberEmail = email;
        }
    
        /**
         * Get domain name 
         * @return name
         */
        public String getDomainName() {
            return domainName;
        }
    
        /**
         * Get entry point file of domain
         * @return filename
         */
        public String getEntryPointFile() {
            return entryPointFile;
        }
    
        /**
         * Get member name
         * @return name
         */
        public String getMemberName() {
            return memberFullName;
        }
    
        /**
         * Get member email
         * @return email
         */
        public String getMemberEmail() {
            return memberEmail;
        }
    }
    
}
