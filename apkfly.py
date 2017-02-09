# -*- coding: utf-8 -*-
"""This script is used to execute project commands in batches"""

import os
import re
import sys

__author__ = "qiudongchao"
__version__ = "1.0.0"


# ~ python apkfly.py [upload]
class ApkUtils(object):
    file_build_gradle = "build.gradle"
    dir_current = os.path.abspath(".")
    file_settings = os.path.join(dir_current, "settings.gradle")
    file_build = os.path.join(dir_current, file_build_gradle)
    dir_build = os.path.join(dir_current, "build")
    file_temp = os.path.join(dir_build, "apkfly-temp.txt")

    def __init__(self):
        if not os.path.exists(self.dir_build):
            os.mkdir(self.dir_build)

    # 校验当前工作空间是否合法
    def check_root_project(self):
        file_exist = os.path.exists(self.file_build) and os.path.exists(self.file_settings)
        is_file = os.path.isfile(self.file_settings) and os.path.isfile(self.file_build)
        return file_exist and is_file

    # 校验子项目是否合法
    def check_sub_project(self, sub_project, is_formate):
        check_result = False
        if os.path.isdir(sub_project):
            if os.path.exists(os.path.join(self.dir_current, sub_project, self.file_build_gradle)):
                if is_formate:
                    p = re.compile(r"^\d{3}-[A-Za-z0-9]+$")
                    if p.match(sub_project):
                        check_result = True
                else:
                    check_result = True
        return check_result

    # 读取临时文件
    def read_temp(self):
        temp_list = []
        if os.path.exists(self.file_temp):
            try:
                temp_file = open(self.file_temp, "r")
                temp_content = temp_file.read()
                temp_list = temp_content.split(",")
            finally:
                temp_file.close()
        return temp_list

    # 写入临时文件
    def write_temp(self, temp_list):
        try:
            temp_file = open(self.file_temp, "w")
            temp_file.write(','.join(temp_list))
        finally:
            temp_file.close()

    # 批量执行子项目命令
    def exec_sub_project(self, cmd):
        if self.check_root_project():
            print ">>>>>>start to running<<<<<<"
            temp_list = self.read_temp()
            run_flag = True
            sub_file_list = [x for x in os.listdir(self.dir_current) if
                             self.check_sub_project(x, True)]
            sub_file_list.sort()
            for sub_file in sub_file_list:
                if sub_file in temp_list:
                    continue
                setting = file(self.file_settings, "w")
                print ">>>Running project:%s" % sub_file
                try:
                    setting.write("include \":%s\"" % sub_file)
                finally:
                    setting.close()
                # exec gradle clean uploadArchives
                clean_output = os.popen("gradle :%s:%s" % (sub_file, "clean"))
                print clean_output.read()
                cmd_output = os.popen("gradle :%s:%s" % (sub_file, cmd))
                cmd_result = cmd_output.read()
                print cmd_result
                if cmd_result.find("BUILD SUCCESSFUL") != -1:
                    print ">>>Success project:%s" % sub_file
                    temp_list.append(sub_file)
                else:
                    print ">>>Error project:%s" % sub_file
                    run_flag = False
                    break
            if run_flag:
                if os.path.exists(self.file_temp):
                    os.remove(self.file_temp)
            else:
                self.write_temp(temp_list)
            print ">>>>>>running stop<<<<<<"
        else:
            print ">>>>>>check project error<<<<<<"

    # 重建setting文件
    def setting(self):
        if self.check_root_project():
            print ">>>>>>start to running<<<<<<"
            sub_file_list = [x for x in os.listdir(self.dir_current) if
                             self.check_sub_project(x, False)]
            setting_content = ""
            for sub_file in sub_file_list:
                if setting_content == "":
                    setting_content = "include \":%s\"" % sub_file
                else:
                    setting_content += "\ninclude \":%s\"" % sub_file
            try:
                setting = file(self.file_settings, "w")
                setting.write(setting_content)
            finally:
                setting.close()
            print ">>>>>>running stop<<<<<<"
        else:
            print ">>>>>>check project error<<<<<<"

    # 批量执行子项目git命令
    def git_cmd(self, cmd):
        if self.check_root_project():
            print ">>>>>>start to running<<<<<<"
            sub_file_list = [x for x in os.listdir(self.dir_current) if
                             self.check_sub_project(x, False)]
            for sub_file in sub_file_list:
                dir_git = os.path.join(self.dir_current, sub_file, ".git")
                if os.path.exists(dir_git) and os.path.isdir(dir_git):
                    print ">>>>>>Run [git %s] at dir [%s]" % (cmd, sub_file)
                    os.chdir(os.path.join(self.dir_current, sub_file))
                    # print os.path.abspath(".")
                    git_cmd = os.popen(cmd)
                    print git_cmd.read()
                else:
                    print ">>>>>>%s is not git repo" % sub_file
            print ">>>>>>running stop<<<<<<"
        else:
            print ">>>>>>check project error<<<<<<"

    # 版本号批量增加
    def version_add(self, first_prop, last_prop):
        prop_file_path = os.path.join(self.dir_current, "gradle.properties")
        if os.path.exists(prop_file_path) and os.path.isfile(prop_file_path):
            print ">>>>>>start to version auto increment<<<<<<"
            aar_list = []
            is_aar = False
            # 遍历所有内容，获取 需要版本号变更的条目
            with open(prop_file_path, "r") as prop_file:
                prop_file_list = prop_file.readlines()
                for prop in prop_file_list:
                    if prop.startswith(first_prop):
                        is_aar = True
                        aar_list.append(prop.strip())
                    elif prop.startswith(last_prop):
                        aar_list.append(prop.strip())
                        is_aar = False
                    else:
                        if is_aar and prop.strip() != '' and not prop.startswith('#'):
                            aar_list.append(prop.strip())
            # 遍历所有内容,并版本号自增
            with open(prop_file_path, "r") as prop_file_r:
                # 获取文件内容
                prop_file_content = prop_file_r.read()
                # 遍历需要版本变更的条目，动态升级版本号
                for aar in aar_list:
                    third_num_list = re.findall(r"^[A-Za-z0-9_]+\s*=\s*\d+\.(\d+)\.\d+", aar)
                    if len(third_num_list) == 1:
                        third_num = third_num_list[0]
                    else:
                        raise ValueError("third num error for [" + aar + "]")
                    third_num = int(third_num) + 1
                    # 此处可对版本号格式进行修改，当前仅适配GomePlus
                    new_aar = re.sub(r"\.\d+\.", "." + str(third_num) + ".", aar)
                    prop_file_content = prop_file_content.replace(aar, new_aar)
                    print ">>>replace ", aar, " to ", new_aar
            # 文件写入
            with open(prop_file_path, "w") as prop_file_w:
                prop_file_w.write(prop_file_content)
            print ">>>>>>running stop<<<<<<"
        else:
            print ">>>>>>error: gradle.properties not exit <<<<<<"


# 执行入口
if __name__ == '__main__':
    args = sys.argv
    apk = ApkUtils()
    if len(args) == 2:
        if args[1] == "upload":
            apk.exec_sub_project("uploadArchives")
        elif args[1] == "setting":
            apk.setting()
        elif args[1] == "version":
            # 此处可配置AAR版本修改范围，第一个参数为起始位置，第二个参数为终止位置
            apk.version_add("AAR_GFRAME_VERSION", "AAR_MAPP_VERSION")
        elif args[1] == "pull":
            apk.git_cmd("git pull")
        elif args[1] == "reset":
            apk.git_cmd("git reset --hard")
        else:
            print ">>>>>>cmd error<<<<<<"
    else:
        apk.exec_sub_project("aR")
