package io.jenkins.plugins;


import groovy.json.JsonOutput;
import hudson.FilePath;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;
import java.util.ArrayList;

public class UploadBuilderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

//    final String name = "Bobby";


    //    @Test
//    public void testBuild() throws Exception {
//        FileFinder finder=new FileFinder();
//        List<FilePath> invoke = finder.findFile(new FilePath(new File("./")));
//        System.out.println(invoke);
//        System.out.println(invoke.get(0).getBaseName());
//        System.out.println(invoke.get(0).getRemote());
//        assert invoke!=null;
//    }
    @Test
    public void testFilePath() throws Exception {
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("sdfa");
        arrayList.add("ewf");
        arrayList.add("fg");
        arrayList.add("fg");
        String s = JSONArray.fromObject(arrayList).toString();
        System.out.println(s
        );
        assert arrayList.size()>0;
    }
}