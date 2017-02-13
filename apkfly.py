#!/usr/bin/python
# -*- coding: utf-8 -*-
"""This script is used to execute project commands in batches"""

import argparse
import os
import re
import time

__author__ = "qiudongchao<1162584980@qq.com>"
__version__ = "2.0.0"

file_build_gradle = "build.gradle"
dir_current = os.path.abspath(".")
file_settings = os.path.join(dir_current, "settings.gradle")
file_build = os.path.join(dir_current, file_build_gradle)
dir_build = os.path.join(dir_current, "build")
file_temp = os.path.join(dir_build, "apkfly-temp.txt")


def check_root_project():
    """校验当前工作空间是否合法
    """
    file_exist = os.path.exists(file_build) and os.path.exists(file_settings)
    is_file = os.path.isfile(file_settings) and os.path.isfile(file_build)
    return file_exist and is_file


def check_sub_project(sub_project, is_formate):
    """校验子项目是否合法
    """
    check_result = False
    if os.path.isdir(sub_project):
        if os.path.exists(os.path.join(dir_current, sub_project, file_build_gradle)):
            if is_formate:
                p = re.compile(r"^\d{3}-[A-Za-z0-9]+$")
                if p.match(sub_project):
                    check_result = True
            else:
                check_result = True
    return check_result


def read_temp():
    """读取临时文件
    """
    temp_list = []
    if os.path.exists(file_temp):
        with open(file_temp, "r") as temp_file:
            temp_content = temp_file.read()
            temp_list = temp_content.split(",")
    return temp_list


def write_temp(temp_list):
    """写入临时文件
    """
    with open(file_temp, "w") as temp_file:
        temp_file.write(','.join(temp_list))


def exec_sub_project(cmd, args):
    """批量执行子项目命令【gradle】
    """
    if check_root_project():
        print ">>>>>>start to running<<<<<<"
        start_project = args.start
        start_flag = False
        temp_list = read_temp()
        run_flag = True
        sub_file_list = [x for x in os.listdir(dir_current) if
                         check_sub_project(x, True)]
        # 按文件名排序
        sub_file_list.sort()
        for sub_file in sub_file_list:
            if start_project:
                if sub_file.startswith(start_project):
                    start_flag = True
            else:
                start_flag = True
            if sub_file in temp_list or not start_flag:
                continue
            print ">>>Running project:%s" % sub_file
            # 在settings.gradle 配置子项目
            with open(file_settings, "w") as setting:
                setting.write("include \":%s\"" % sub_file)
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
        # 运行成功，删除临时文件
        if run_flag and os.path.exists(file_temp):
            os.remove(file_temp)
        else:
            # 运行失败，将完成子项目写入临时文件
            write_temp(temp_list)
        print ">>>>>>running stop<<<<<<"
    else:
        print ">>>>>>check project error<<<<<<"


def _setting(args):
    """重建setting文件
    """
    if check_root_project():
        print ">>>>>>start to running<<<<<<"
        sub_file_list = [x for x in os.listdir(dir_current) if
                         check_sub_project(x, False)]
        setting_content = ""
        for sub_file in sub_file_list:
            if setting_content == "":
                setting_content = "include \":%s\"" % sub_file
            else:
                setting_content += "\ninclude \":%s\"" % sub_file
        # 写入settings.gradle文件
        with open(file_settings, "w") as setting:
            setting.write(setting_content)
        print ">>>>>>running stop<<<<<<"
    else:
        print ">>>>>>check project error<<<<<<"


def git_cmd(cmd):
    """批量执行子项目git命令
    """
    if check_root_project():
        print ">>>>>>start to running<<<<<<"
        sub_file_list = [x for x in os.listdir(dir_current) if
                         check_sub_project(x, False)]
        for sub_file in sub_file_list:
            dir_git = os.path.join(dir_current, sub_file, ".git")
            if os.path.exists(dir_git) and os.path.isdir(dir_git):
                print ">>>>>>Run [git %s] at dir [%s]" % (cmd, sub_file)
                os.chdir(os.path.join(dir_current, sub_file))
                # print os.path.abspath(".")
                git_cmd = os.popen(cmd)
                print git_cmd.read()
            else:
                print ">>>>>>%s is not git repo" % sub_file
        print ">>>>>>running stop<<<<<<"
    else:
        print ">>>>>>check project error<<<<<<"


def _version_add(args):
    """版本号批量增加
    """
    first_prop = args.start
    last_prop = args.end
    index = args.index
    prop_file_path = os.path.join(dir_current, "gradle.properties")
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
                if index == 1:
                    num_list = re.findall(r"^[A-Za-z0-9_]+\s*=\s*(\d+)\.\d+\.\d+", aar)
                elif index == 2:
                    num_list = re.findall(r"^[A-Za-z0-9_]+\s*=\s*\d+\.(\d+)\.\d+", aar)
                else:
                    num_list = re.findall(r"^[A-Za-z0-9_]+\s*=\s*\d+\.\d+\.(\d+)", aar)
                if len(num_list) == 1:
                    index_num = num_list[0]
                else:
                    raise ValueError("third num error for [" + aar + "]")
                index_num = int(index_num) + 1
                # 此处可对版本号格式进行修改，当前仅适配GomePlus
                if index == 1:
                    new_aar = re.sub(r"=\s*\d+", "=" + str(index_num), aar)
                elif index == 2:
                    new_aar = re.sub(r"\.\d+\.", "." + str(index_num) + ".", aar)
                else:
                    new_aar = re.sub(r"\.\d+-", "." + str(index_num) + "-", aar)
                prop_file_content = prop_file_content.replace(aar, new_aar)
                print ">>>replace ", aar, " to ", new_aar
        # 文件写入
        with open(prop_file_path, "w") as prop_file_w:
            prop_file_w.write(prop_file_content)
        print ">>>>>>running stop<<<<<<"
    else:
        print ">>>>>>error: gradle.properties not exit <<<<<<"


def _push_prop(args):
    """提交gradle.properties到git服务器
    """
    prop_file_path = os.path.join(dir_current, "gradle.properties")
    if os.path.exists(prop_file_path) and os.path.isfile(prop_file_path):
        message = "AAR批量打包:"
        branch = args.b
        if args.m:
            message = message + args.m
        else:
            time_info = time.strftime(" %Y-%m-%d %H:%M:%S", time.localtime(time.time()))
            message = message + time_info
        add_cmd = os.popen("git add gradle.properties")
        print add_cmd.read()
        commit_cmd = os.popen("git commit -m '%s'" % message)
        print commit_cmd.read()
        push_cmd = os.popen("git push origin %s" % branch)
        print push_cmd.read()
    else:
        print ">>>>>>error: gradle.properties not exit <<<<<<"


####################### function for sub-command ########################

def _git_pull(args):
    git_cmd("git pull")


def _git_reset(args):
    git_cmd("git reset --hard")


def _upload(args):
    exec_sub_project("uploadArchives", args)


def _ar(args):
    exec_sub_project("aR", args)


if __name__ == '__main__':
    """执行入口
    """
    # 创建build目录
    if not os.path.exists(dir_build):
        os.mkdir(dir_build)
    # 创建命令行解析器
    parser = argparse.ArgumentParser(prog="apkfly", description="国美workspace帮助工具", epilog="make it easy!")
    subparsers = parser.add_subparsers(title="可用命令")
    # 添加子命令

    parser_setting = subparsers.add_parser("setting", help="把workspace内所有的module配置到settings.gradle")
    parser_setting.set_defaults(func=_setting)

    parser_setting = subparsers.add_parser("pushprop", help="提交gradle.properties到git服务器")
    parser_setting.set_defaults(func=_push_prop)
    parser_setting.add_argument('-m', type=str, help='push评论信息')
    parser_setting.add_argument('-b', type=str, default='mergeDev', help='push 分支')

    parser_version = subparsers.add_parser("version", help="自增gradle.properties内的 aar 配置版本")
    parser_version.set_defaults(func=_version_add)
    parser_version.add_argument('-s', "--start", type=str, default='AAR_GFRAME_VERSION',
                                help='起始AAR版本【例：AAR_MFRAME2_VERSION】')
    parser_version.add_argument('-e', "--end", type=str, default='AAR_MAPP_VERSION', help='终止AAR版本')
    parser_version.add_argument('-i', "--index", type=int, default=2, choices=[1, 2, 3],
                                help='自增版本索引【1大版本，2中间版本，3小版本】')

    parser_pull = subparsers.add_parser("pull", help="更新 项目代码")
    parser_pull.set_defaults(func=_git_pull)

    parser_reset = subparsers.add_parser("reset", help="重置 项目代码")
    parser_reset.set_defaults(func=_git_reset)

    parser_ar = subparsers.add_parser("ar", help="依次 编译 所有module")
    parser_ar.set_defaults(func=_ar)
    parser_ar.add_argument('-s', "--start", type=str, help='执行起始点【项目名前三位，例：027】')

    parser_upload = subparsers.add_parser("upload", help="按module名称 数字排列顺序 依次 执行gradle uploadArchives")
    parser_upload.set_defaults(func=_upload)
    parser_upload.add_argument('-s', "--start", type=str, help='执行起始点【项目名前三位，例：027】')
    # 参数解析
    args = parser.parse_args()
    args.func(args)
