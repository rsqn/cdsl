package tech.rsqn.cdsl.concurrency;

public class Lock {
    private String id;
    private String grantee;
    private String grantedResource;
    private long grantedMs;
    private long expiresMs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    public String getGrantedResource() {
        return grantedResource;
    }

    public void setGrantedResource(String grantedResource) {
        this.grantedResource = grantedResource;
    }

    public long getGrantedMs() {
        return grantedMs;
    }

    public void setGrantedMs(long grantedMs) {
        this.grantedMs = grantedMs;
    }

    public long getExpiresMs() {
        return expiresMs;
    }

    public void setExpiresMs(long expiresMs) {
        this.expiresMs = expiresMs;
    }

    public boolean hasExpired() {
        return expiresMs < System.currentTimeMillis();
    }
}
