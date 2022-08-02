package com.hou.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版本号同步工具类
 */
public class MyClass {

    private static String K_VERSION = "_VERSION";
    private static String K_SPLIT = "=";

    //FLUTTER  以及直播相关库不同步代码，黑名单都在这里处理
    private static String[] BLACK_KEYS =
            {"AAR_FLUTTER"
                    ,"AAR_GZHIBO_BUS_VERSION"};


    public static void main(String[] args) {
//        String currentPath = "";
        String currentPath = "/Users/houbiaofeng/AndroidStudioProjects/GSpaceDev/GomePlus/staff.properties";
        String sourcePath = "/Users/houbiaofeng/AndroidStudioProjects/GSpaceDev/GomePlus/gome.properties";
        String target = "/Users/houbiaofeng/AndroidStudioProjects/GSpaceDev/GomePlus/result.properties";

        replaceVersionFile(currentPath,sourcePath, target);
    }


    /**
     * 替换版本文件
     *
     * @param currentPath 当前被替换的文件
     * @param sourcePath  参考的版本号文件
     * @param resultPath  修改后的版本文件
     */
    static void replaceVersionFile(String currentPath, String sourcePath, String resultPath) {
        try {
            BufferedWriter tartetVersion = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath), "UTF-8"));

            // 读取文件内容到Stream流中，按行读取
            List<Object> fromLines = Files.lines(Paths.get(currentPath)).collect(Collectors.toList());
            List<Object> toLines = Files.lines(Paths.get(sourcePath)).collect(Collectors.toList());
            // 随机行顺序进行数据处理

            String findVerison;
            for (int i = 0; i < fromLines.size(); i++) {
                String fromLine = (String) fromLines.get(i);
                findVerison = null;
                if (isNeedFind(fromLine)) {
                    for (int j = 0; j < toLines.size(); j++) {
                        String toLine = (String) toLines.get(j);
                        if (fromLine.contains("-plus")){
                            findVerison = replacePlusVersion(fromLine, toLine);
                        }else {
                            findVerison = replaceVersion(fromLine, toLine);
                        }
                        if (findVerison != null) {
                            break;
                        }
                    }
                    if (findVerison != null) {
                        tartetVersion.write(findVerison);
                    } else {
                        tartetVersion.write(fromLine);
                    }
                } else {
                    tartetVersion.write(fromLine);
                }
                tartetVersion.write("\n");
            }
            tartetVersion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 兼容 版本号  -plus
     * @param currentVersion
     * @param sourceVersion
     * @return
     */
    private static String replacePlusVersion(String currentVersion, String sourceVersion) {
        try {
            StringBuilder resultVersion = new StringBuilder();
            String[] currentVersions = currentVersion.split(K_SPLIT);
            if (sourceVersion.contains(K_VERSION)) {
                String[] toVersion = sourceVersion.split(K_SPLIT);
                if (currentVersions[0].equals(toVersion[0])) {//版本号命中，替换版本值
                    //判断命中的版本号值，两者取最大值

                    String retVersion = getLargeVersion(currentVersions[1].replace("-plus",""), toVersion[1].replace("-plus",""));
                    resultVersion.append(currentVersions[0]).append(K_SPLIT).append(retVersion).append("-plus");
                }
            }
            if (resultVersion.length() == 0) {
                return null;
            } else {
//                System.out.println("=====true=====" + resultVersion.toString());
                return resultVersion.toString();
            }
        } catch (Exception e) {
            System.out.println("=====currentVersion=====" + currentVersion + "=====sourceVersion=====" + sourceVersion);
            e.printStackTrace();
            return currentVersion;
        }
    }

    /**
     * 是否需要替换
     *
     * @param fromVersion
     * @return
     */
    private static boolean isNeedFind(String fromVersion) {
        for (String key :BLACK_KEYS){
            if (fromVersion.contains(key)){
                return false;
            }
        }
        return fromVersion.contains(K_VERSION);
    }

    /**
     * 替换版本号
     *
     * @param currentVersion
     * @param sourceVersion
     * @return
     */
    private static String replaceVersion(String currentVersion, String sourceVersion) {
        try {
            StringBuilder resultVersion = new StringBuilder();
            String[] currentVersions = currentVersion.split(K_SPLIT);
            if (sourceVersion.contains(K_VERSION)) {
                String[] toVersion = sourceVersion.split(K_SPLIT);
                if (currentVersions[0].equals(toVersion[0])) {//版本号命中，替换版本值
                    //判断命中的版本号值，两者取最大值
                    String retVersion = getLargeVersion(currentVersions[1], toVersion[1]);
                    resultVersion.append(currentVersions[0]).append(K_SPLIT).append(retVersion);
                }
            }
            if (resultVersion.length() == 0) {
                return null;
            } else {
//                System.out.println("=====true=====" + resultVersion.toString());
                return resultVersion.toString();
            }
        } catch (Exception e) {
            System.out.println("=====currentVersion=====" + currentVersion + "=====sourceVersion=====" + sourceVersion);
            e.printStackTrace();
            return currentVersion;
        }
    }

    /**
     * 获取较大版本号
     *
     * @param v1
     * @param v2
     * @return
     */
    private static String getLargeVersion(String v1, String v2) {
        try {
            String resultVersion = "";
            String version1[] = v1.split("[.]");
            String version2[] = v2.split("[.]");

            for (int i = 0; i < version1.length; i++) {
                if (version1[i].equals(version2[i])) {
                    resultVersion = v1;
                    continue;
                } else if (Integer.parseInt(version1[i]) > Integer.parseInt(version2[i])) {
                    resultVersion = v1;
                    break;
                } else {
                    resultVersion = v2;
                    break;
                }
            }
            return resultVersion;
        } catch (Exception e) {//抛出异常，版本号不更新
            System.out.println("=====v1=====" + v1 + "=====v2=====" + v2);
            return v1;
        }
    }


    private static void findVersion(String fromPath, String targetPath) throws IOException {
        String target = "/Users/houbiaofeng/AndroidStudioProjects/GWorkspace/lib/result.properties";
        BufferedReader fromVersion = new BufferedReader(new InputStreamReader(new FileInputStream(fromPath), "UTF-8"));
        BufferedReader toVersion = new BufferedReader(new InputStreamReader(new FileInputStream(targetPath), "UTF-8"));

        BufferedWriter tartetVersion = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), "UTF-8"));

        String fromLine; //如果文件读完后还读，就返回 null
        String toLine; //如果文件读完后还读，就返回 null
        String findVerison = null; //如果文件读完后还读，就返回 null

        while ((fromLine = fromVersion.readLine()) != null) {
            System.out.println("=====from=====" + fromLine);

            while ((toLine = toVersion.readLine()) != null) {
                System.out.println("=====to=====" + toLine);
                findVerison = replaceVersion(fromLine, toLine);

            }
            if (findVerison != null) {
//                System.out.println("=====1=====" + findVerison);
                tartetVersion.write(findVerison);
            } else {
//                System.out.println("======0====" + fromLine);
                tartetVersion.write(fromLine);
            }
            tartetVersion.write("\n");
        }

        tartetVersion.close();
    }

}