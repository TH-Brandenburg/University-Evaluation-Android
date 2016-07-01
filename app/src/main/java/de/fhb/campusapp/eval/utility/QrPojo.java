package de.fhb.campusapp.eval.utility;

/**
 * Created by Basti on 30.06.2015.
 */
public class QrPojo {
    private String voteToken;
    private String host;

    public QrPojo(String voteToken, String host, String deviceID) {
        this.voteToken = voteToken;
        this.host = host;
    }

    public QrPojo() {
    }

    public String getVoteToken() {
        return voteToken;
    }

    public void setVoteToken(String voteToken) {
        this.voteToken = voteToken;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
