package io.jenkins.plugins.sdx.domain;

import java.io.*;

public class JavaSp {
    public static void main(String[] args) throws IOException {
        File file=new File("/Users/dx/Desktop/source/jenkins-upload/left.txt/");
        System.out.println(file.getAbsolutePath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
        String content;
        while((content = reader.readLine() )!=null){
           int start= content.indexOf(">")+1;
           int end=content.lastIndexOf("<");
           String left=content.substring(start,end);
           System.out.println(left);
        }

    }
}
