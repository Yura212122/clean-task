package academy.prog.julia.json_responses;

public class CertificateResponse {
    private Long id;
    private String uniqueId;
    private String groupName;

    public CertificateResponse(Long id, String uniqueId, String groupName) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.groupName = groupName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
