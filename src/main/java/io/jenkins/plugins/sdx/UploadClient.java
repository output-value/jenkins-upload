package io.jenkins.plugins.sdx;

import io.jenkins.plugins.sdx.domain.UploadInfo;
import net.sf.json.JSONObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by dx on 19-1-18.
 */
class UploadClient {
    static UploadInfo postFile(String url, File file, Map<String, String> mapParams) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .callTimeout(120, TimeUnit.SECONDS)
                .build();
        okHttpClient.readTimeoutMillis();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file));

        Set<Map.Entry<String, String>> entries = mapParams.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        UploadInfo info = new UploadInfo();
        info.setFilePath(file.getAbsolutePath());
        if (response.code() == 200) {
            String string = response.body().string();
            System.out.println(string);
            JSONObject jsonObject = JSONObject.fromObject(string);
            String code = jsonObject.getString("code");
            if ("1".equals(code)) {
                String downloadUrl = jsonObject.getString("url");
                String channel = jsonObject.getString("channel");
                info.setResult(true);
                info.setUrl(downloadUrl);
                info.setChannel(channel);
            } else {
                info.setResult(false);
            }
        } else {
            info.setResult(false);
        }

        return info;

    }
}
