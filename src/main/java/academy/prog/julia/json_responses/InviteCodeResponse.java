package academy.prog.julia.json_responses;

/**
 * This class represents the response object for an invite code request.
 *
 * It contains the invite code that will be returned as part of the response.
 */
public class InviteCodeResponse {

    private String invite;

    /**
     * Constructor to initialize the InviteCodeResponse with an invite code.
     *
     * @param invite the invite code to be included in the response
     */
    public InviteCodeResponse(String invite) {
        this.invite = invite;
    }

    /**
     * Getter method for the invite code.
     *
     * @return the invite code
     */
    public String getInvite() {
        return invite;
    }

    /**
     * Setter method for the invite code.
     *
     * @param invite the invite code to set
     */
    public void setInvite(String invite) {
        this.invite = invite;
    }

}
