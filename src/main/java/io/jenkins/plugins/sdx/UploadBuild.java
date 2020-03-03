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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Created by dx on 19-1-18.
 */
public class UploadBuild extends Recorder {

    //上传url
    public final String upload;
    //参数
    public final String params;
    //路径
    public final String path;


    @DataBoundConstructor
    public UploadBuild(String upload, String params, String path) {
        this.upload = upload;
        this.params = params;
        this.path = path;
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
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        if (build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            return false;
        }
        EnvVars environment = build.getEnvironment(listener);

        if (params == null) {
            listener.error("错误").println("是不是忘了设置获取的参数啊");
            return false;
        }
        FilePath rootFilePath = new FilePath(build.getWorkspace(), path);
        listener.getLogger().println("开始记录");
        listener.getLogger().println(rootFilePath.getRemote());
        listener.getLogger().println("结束记录");
        List<String> fileName = findFile(rootFilePath);
        //构建类型..当然可以修改为自己的参数
        String[] list = params.split("\\$");
        final Map<String, String> mapParams = new HashMap<>();
        listener.getLogger().println("upload params:");
        for (String param : list) {
            if (param != null && param.length() > 0) {
                String value = environment.get(param, "");
                listener.getLogger().println(param.toLowerCase() + ":" + value);

                mapParams.put(param.toLowerCase(), value);
            }
        }
        final List<ResultItem> items = new ArrayList<>();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long start = System.currentTimeMillis();
        listener.getLogger().println("upload start"+format.format(start));
        List<Future<?>> submitList=new ArrayList<>();
        for (final String path : fileName) {
            Future<?> submit = ThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    UploadInfo info = null;
                    try {
                        info = UploadClient.postFile(upload, new File(path), mapParams);
                    } catch (IOException e) {
                        info = new UploadInfo();
                        info.setResult(false);
                        info.setErrorMsg(e.getMessage() + "错误");
                    }
                    if (!info.isResult()) {
                        listener.getLogger().println("-------上传失败-------");
                        listener.getLogger().println(info.getErrorMsg());
                        listener.getLogger().println("--------------");
                    }
                    ResultItem resultItem = new ResultItem();
                    resultItem.setUrl(info.getUrl());
                    resultItem.setChannel(info.getChannel());
                    items.add(resultItem);
                    listener.getLogger().println(info);
                    listener.getLogger().println("上传消耗时间" + (System.currentTimeMillis() - startTime) / 1000 + "秒");
                }
            });
            submitList.add(submit);
        }
        for (Future<?> future : submitList) {
            try {
                Object o = future.get();
            } catch (ExecutionException e) {

            }
        }
        long end= System.currentTimeMillis();
        listener.getLogger().println("upload end"+format.format(end));
        listener.getLogger().println("耗时："+(end-start)/1000+"秒");
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
