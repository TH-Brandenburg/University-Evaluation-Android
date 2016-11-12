package de.fhb.campusapp.eval.utility.vos;

/**
 * Created by Basti on 30.06.2015.
 */
public class QrDataVo {
    private String voteToken;
    private String host;

    public QrDataVo(String voteToken, String host, String deviceID) {
        this.voteToken = voteToken;
        this.host = host;
    }

    public QrDataVo() {
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
