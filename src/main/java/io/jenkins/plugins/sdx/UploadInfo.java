package io.jenkins.plugins.sdx;


/**
 * Created by dx on 19-1-18.
 */
public class UploadInfo {
    private String url;
    private boolean result;
    private String filePath;

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

    @Override
    public String toString() {
        return "UploadInfo{" +
                "url='" + url + '\'' +
                ", result=" + result +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
