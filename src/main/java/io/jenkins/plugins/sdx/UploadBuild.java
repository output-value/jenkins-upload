package io.jenkins.plugins.sdx;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import io.jenkins.plugins.sdx.domain.ResultItem;
import io.jenkins.plugins.sdx.domain.UploadInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dx on 19-1-18.
 */
public class UploadBuild extends Recorder {


    public final String upload;

    public final String params;


    @DataBoundConstructor
    public UploadBuild(String upload, String params) {
        this.upload = upload;
        this.params = params;
    }

    //和前台配置相关
    public DescriptorImpl getDescriptor() {
        return new DescriptorImpl();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        if (build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            return false;
        }
        EnvVars environment = build.getEnvironment(listener);

        if (params == null) {
            listener.getLogger().println("是不是忘了设置获取的参数啊");
            return false;
        }
        List<String> fileName = findFile(build.getWorkspace());
        //构建类型..当然可以修改为自己的参数
        String[] list = params.split("\\$");
        Map<String, String> mapParams = new HashMap<>();
        listener.getLogger().println("upload params:");
        for (String param : list) {
            if (param != null && param.length() > 0) {
                String value = environment.get(param, "");
                listener.getLogger().println(param.toLowerCase() + ":" + value);

                mapParams.put(param.toLowerCase(), value);
            }
        }
        List<ResultItem> items = new ArrayList<>();
        listener.getLogger().println("upload start");
        for (String path : fileName) {
            UploadInfo info = UploadClient.postFile(upload, new File(path), mapParams);
            ResultItem resultItem = new ResultItem();
            resultItem.setUrl(info.getUrl());
            resultItem.setChannel(info.getChannel());
            items.add(resultItem);
            listener.getLogger().println(info);
        }
        listener.getLogger().println("upload end,the result is:");
        String result = JSONArray.fromObject(items).toString();
        listener.getLogger().println(result);
        return true;
    }


    private List<String> findFile(FilePath directory) throws IOException, InterruptedException {

        List<FilePath> files = directory.list();
        if (files == null) {
            files = new ArrayList<>();
        }
        List<String> tempList = new ArrayList<>();
        for (FilePath file : files) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith(".apk")) {
                    tempList.add(file.getRemote());
                }
            } else {
                tempList.addAll(findFile(file));
            }
        }

        return tempList;
    }


    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        public DescriptorImpl(String upload) {
            load();
        }

        public DescriptorImpl(String upload, String submit) {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return true;
        }


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "upload";
        }

    }
}
