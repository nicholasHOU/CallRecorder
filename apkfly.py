#!/usr/bin/python
# -*- coding: utf-8 -*-
"""This script is used to execute project commands in batches"""

import argparse
import os
import re

__author__ = "qiudongchao"
__version__ = "1.0.0"


class ApkUtils(object):
    """python apkfly.py [upload]
    """
    file_build_gradle = "build.gradle"
    dir_current = os.path.abspath(".")
    file_settings = os.path.join(dir_current, "settings.gradle")
    file_build = os.path.join(dir_current, file_build_gradle)
    dir_build = os.path.join(dir_current, "build")
    file_temp = os.path.join(dir_build, "apkfly-temp.txt")

    def __init__(self):
        if not os.path.exists(self.dir_build):
            os.mkdir(self.dir_build)

    def check_root_project(self):
        """校验当前工作空间是否合法
        """
        file_exist = os.path.exists(self.file_build) and os.path.exists(self.file_settings)
        is_file = os.path.isfile(self.file_settings) and os.path.isfile(self.file_build)
        return file_exist and is_file

    def check_sub_project(self, sub_project, is_formate):
        """校验子项目是否合法
        """
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

    def read_temp(self):
        """读取临时文件
        """
        temp_list = []
        if os.path.exists(self.file_temp):
            try:
                temp_file = open(self.file_temp, "r")
                temp_content = temp_file.read()
                temp_list = temp_content.split(",")
            finally:
                temp_file.close()
        return temp_list

    def write_temp(self, temp_list):
        """写入临时文件
        """
        try:
            temp_file = open(self.file_temp, "w")
            temp_file.write(','.join(temp_list))
        finally:
            temp_file.close()

    def exec_sub_project(self, cmd):
        """批量执行子项目命令
        """
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

    def setting(self):
        """重建setting文件
        """
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

    def git_cmd(self, cmd):
        """批量执行子项目git命令
        """
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

    def version_add(self, first_prop, last_prop):
        """版本号批量增加
        """
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


# sub-command fuction
def _upload(args):
    apk = ApkUtils()
    apk.exec_sub_project("uploadArchives")


def _setting(args):
    apk = ApkUtils()
    apk.setting()


def _version(args):
    apk = ApkUtils()
    # 此处可配置AAR版本修改范围，第一个参数为起始位置，第二个参数为终止位置
    apk.version_add("AAR_GFRAME_VERSION", "AAR_MAPP_VERSION")


def _pull(args):
    apk = ApkUtils()
    apk.git_cmd("git pull")


def _reset(args):
    apk = ApkUtils()
    apk.git_cmd("git reset --hard")


def _ar(args):
    apk = ApkUtils()
    apk.exec_sub_project("aR")


if __name__ == '__main__':
    """执行入口
    """

    parser = argparse.ArgumentParser(prog="apkfly", description="国美workspace帮助工具", epilog="make it easy!")
    subparsers = parser.add_subparsers(title="可用命令")
    # 添加子命令
    parser_upload = subparsers.add_parser("upload", help="按module名称 数字排列顺序 依次 执行gradle uploadArchives")
    parser_upload.set_defaults(func=_upload)

    parser_setting = subparsers.add_parser("setting", help="把workspace内所有的module配置到settings.gradle")
    parser_setting.set_defaults(func=_setting)

    parser_version = subparsers.add_parser("version", help="自增gradle.properties内的 aar 配置版本")
    parser_version.set_defaults(func=_version)

    parser_pull = subparsers.add_parser("pull", help="更新 项目代码")
    parser_pull.set_defaults(func=_pull)

    parser_reset = subparsers.add_parser("reset", help="重置 项目代码")
    parser_reset.set_defaults(func=_reset)

    parser_ar = subparsers.add_parser("ar", help="依次 编译 所有module")
    parser_ar.set_defaults(func=_ar)
    # 参数解析
    args = parser.parse_args()
    args.func(args)
