package io.jenkins.plugins.sdx.domain;


/**
 * Created by dx on 19-1-18.
 */
public class UploadInfo {
    private String url;
    private boolean result;
    private String filePath;
    private String channel;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "UploadInfo{" +
                "url='" + url + '\'' +
                ", result=" + result +
                ", filePath='" + filePath + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
