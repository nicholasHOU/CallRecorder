#!/usr/bin/python
# -*- coding: utf-8 -*-
"""This script is used to manage android project"""
import argparse
import os
import re
import subprocess
import sys
import time
import xml
from collections import Counter
from xml.dom import minidom
import platform

__author__ = "qiudongchao<1162584980@qq.com>"
__version__ = "5.1.2"

# 解决win命令行乱码问题
reload(sys)
sys.setdefaultencoding('utf-8')
##########################################

file_build_gradle = "build.gradle"
dir_current = os.path.abspath(".")
file_settings = os.path.join(dir_current, "settings.gradle")
file_build = os.path.join(dir_current, file_build_gradle)

# 上传finder配置
UPLOAD_ACCOUNT = 'gome'
UPLOAD_PW = 'jdkfjkd'
UPLOAD_URL = 'http://10.115.3.134:8085/upload'

# apk上传到finder的这个目录中 ------  可修改  ------
JOB_NAME = 'Location-App-Script'

# 寻找apk的特殊路径标示
APK_SPECIAL_PATH = os.path.join('build', 'outputs', 'apk')

# 在线生成二维码api
QRCODE_API = 'http://qr.topscan.com/api.php?text='
# 二维码缓存目录
QR_CODE_IMG_CACHE_PAHT = os.path.join('.idea', 'caches', 'qr.png')

###################################################################
### 全局公共方法
###################################################################

def slog(message, loading=False, line=False):
    """
    打印日志
    :param message: 日志信息
    :param loading: 是否 显示进行中 状态
    :param line: 是否 换行
    :return:
    """
    temp = ">>> " + message
    if loading:
        temp = temp + "..."
    if line:
        temp = temp + "\n"
    print temp


def sloge(message):
    """
    打印异常日志
    :param message:
    :return:
    """
    print "[Exception] %s" % message


def slogr(success):
    """
    打印 任务执行结果
    :param success: 是否 成功
    :return:
    """
    slog(u"O(∩_∩)O哈哈~" if success else u"╮(╯▽╰)╭哎")


def check_root_project():
    """
    校验当前工作空间是否合法
    :return: True or False
    """
    # file_exist = os.path.exists(file_build) and os.path.exists(file_settings)
    # is_file = os.path.isfile(file_settings) and os.path.isfile(file_build)
    file_exist = os.path.exists(file_build)
    is_file = os.path.isfile(file_build)
    result = file_exist and is_file
    if not result:
        raise Exception(u"工作空间校验失败")


def check_sub_project(sub_project, is_formate):
    """
    校验子项目是否合法
    :param sub_project: 子项目
    :param is_formate: 是否 校验 排序规则
    :return: True or False
    """
    check_result = False
    if os.path.isdir(sub_project):
        if os.path.exists(os.path.join(dir_current, sub_project, file_build_gradle)):
            if is_formate:
                p = re.compile(r"^\d{3}-[A-Za-z0-9-]+$")
                if p.match(sub_project):
                    check_result = True
            else:
                check_result = True
    return check_result


def exec_sub_project(cmd, args):
    """
    批量执行子项目命令
    :param cmd: 命令
    :param args: 命令行参数
    :return:
    """
    check_root_project()

    print ">>>>>>start to running<<<<<<"
    start_project = args.start
    only = args.only
    start_flag = False
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
        # 中断
        if not start_flag:
            continue
        # 是否只执行一个子项目
        if only:
            start_flag = False
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
        else:
            print ">>>Error project:%s" % sub_file
            break
    print ">>>>>>running stop<<<<<<"


def cmd_upload(args):
    """
    upload
    :param args:
    :return:
    """
    exec_sub_project("uploadArchives", args)


def cmd_close_awb(args):
    """
    关闭awb版本
    :param args:
    :return:
    """
    modifyXMLNode("projects.xml", "false")
    updateVersion("awb", "plus", args.all)


def cmd_open_awb(args):
    """
    开启awb
    :param args:
    :return:
    """
    modifyXMLNode("projects.xml", "true")
    updateVersion("plus", "awb", args.all)


def cmd_upload_awb(args):
    exec_awb_sub_project("uploadArchives")


def modifyXMLNode(manifest, isbundleopen):
    try:
        root = minidom.parse(manifest)
    except (OSError, xml.parsers.expat.ExpatError) as e:
        raise Exception("error parsing manifest %s: %s" % (manifest, e))

    if not root or not root.childNodes:
        raise Exception("no root node in %s" % (manifest,))

    for manifestnode in root.childNodes:
        if manifestnode.nodeName == 'manifest':
            manifestnode.attributes["bundleOpen"].value = isbundleopen
            break
        else:
            raise Exception("no <manifest> in %s" % (manifestnode,))

    # 文件写入
    with open(manifest, "w") as manifestxml:
        manifestxml.write(root.toxml())


def updateVersion(fromv, tov, all):
    first_prop = 'AAR_GFRAME_UTILS_VERSION' if all else 'AAR_GFINANCE_VERSION'
    last_prop = 'AAR_MAPP_VERSION' if all else 'AAR_GTQ_DETAIL_VERSION'
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
                # 此处可对版本号格式进行修改，当前仅适配GomePlus
                new_aar = re.sub(fromv, tov, aar)
                prop_file_content = prop_file_content.replace(aar, new_aar)
                print ">>>replace ", aar, " to ", new_aar
        # 文件写入
        with open(prop_file_path, "w") as prop_file_w:
            prop_file_w.write(prop_file_content)
        print ">>>>>>running stop<<<<<<"
    else:
        print ">>>>>>error: gradle.properties not exit <<<<<<"


def exec_awb_sub_project(cmd):
    """批量执行子项目命令【gradle】
    """
    check_root_project()

    print ">>>>>>start to running<<<<<<"
    projects = XmlProject.parser_manifest("projects.xml")
    sub_file_list = [x for x in os.listdir(dir_current) if
                     check_sub_project(x, True)]
    # 按文件名排序
    sub_file_list.sort()
    for sub_file in sub_file_list:
        isbundle = is_bundle_project(sub_file, projects)
        if (isbundle):
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
            else:
                print ">>>Error project:%s" % sub_file
    print ">>>>>>running stop<<<<<<"


def is_bundle_project(sub_file, projects):
    checkbundle = False
    for project in projects:
        if sub_file.endswith(project.path) and project.bundleopen:
            checkbundle = True
            break

    return checkbundle


def cmd_setting(args):
    """
    将当前工作空间的项目部署到setting配置文件
    :param args:
    :return:
    """
    check_root_project()

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


def cmd_version_add(args):
    """
    版本号 批量增加
    :param args:
    :return:
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
                if args.value:
                    index_num = args.value
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


def cmd_prop(args):
    """
    提交gradle.properties到git服务器
    :param args:
    :return:
    """
    prop_file_path = os.path.join(dir_current, "gradle.properties")
    if os.path.exists(prop_file_path) and os.path.isfile(prop_file_path):
        message = u"AAR批量打包:"
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


def cmd_dep(args):
    """
    分析依赖关系
    :param args: 命令行参数
    :return:
    """
    project = args.project
    if os.path.exists(os.path.join(dir_current, project)) and os.path.isdir(project):
        deps_cmd = "gradle -q %s:dependencies --configuration compile" % project
        deps_result = os.popen(deps_cmd)
        content_list = deps_result.readlines()
        dep_list = []
        p = re.compile(r".*?(\S*:\S*:\S*)\s*$")
        for content in content_list:
            print content.rstrip()
            mvn_list = p.findall(content)
            if len(mvn_list) == 1:
                dep_list.append(mvn_list[0])
        # 去重-所有依赖内容
        dep_list = list(set(dep_list))
        dep_list.sort()
        # dep截取list
        dep_sub_list = [x[0: x.rfind(":")] for x in dep_list]
        sub_counter = Counter(dep_sub_list)
        dep_rep_list = [str(k) for k, v in dict(sub_counter).items() if v > 1]
        index = 1
        for rep in dep_rep_list:
            print "------------------------------------------"
            print index, "-", [x for x in dep_list if x.find(rep) != -1]
            index += 1
        if len(dep_rep_list) != 0:
            print "------------------------------------------"
    else:
        print ">>>>>>error: project %s not exit <<<<<<" % project


###################################################################
### Jenkins 自动更新代码
###################################################################

def _git_clone_ser(project_name, git_url, git_branch):
    os.chdir(dir_current)
    clone_cmd = os.popen("git clone %s -b %s %s" % (git_url, git_branch, project_name))
    print clone_cmd.read()


def _git_pull_ser(project_name, git_branch):
    os.chdir(os.path.join(dir_current, project_name))
    pull_cmd = os.popen("git pull origin %s" % git_branch)
    print pull_cmd.read()


def cmd_update_project(args):
    """
    更新源码for jenkins
    :param args:
    :return:
    """
    check_root_project()
    is_order = args.order
    allow_private = args.allow_private
    groups = args.by_group
    projects = args.by_project
    ignore_app = args.ignore_app

    try:
        groups_size = len(groups)
        projects_size = len(projects)
        if groups_size > 0 and projects_size > 0:
            raise Exception(u"by_group 和 by_project 不能同时使用")
        os.chdir(dir_current)
        projects = XmlProject.parser_manifest("projects.xml", by_group=groups, by_project=projects,
                                              allow_private=allow_private, order=is_order,
                                              ignore_app=ignore_app)
        setting_content = ""
        for project in projects:
            os.chdir(dir_current)
            key = project.path
            git_branch = project.branch
            git_url = project.url
            # 获取最新项目源码
            if os.path.exists(os.path.join(dir_current, key)) and os.path.isdir(key):
                print u">>>项目%s存在，更新代码..." % key
                _git_pull_ser(key, git_branch)
            else:
                print u">>>项目%s不存在，克隆代码..." % key
                _git_clone_ser(key, git_url, git_branch)
            # 构建setting content
            if setting_content == "":
                setting_content = "include \":%s\"" % key
            else:
                setting_content += "\ninclude \":%s\"" % key
        print u">>>子项目写入setting"
        with open(file_settings, "w") as setting_file:
            setting_file.write(setting_content)
    except Exception, e:
        sloge(e.message)


###################################################################
### git 操作
###################################################################

class XmlProject(object):
    """
    manifest and parser
    """

    def __init__(self, url, branch, path, app, groups, bundleopen):
        if not url.endswith('.git'):
            raise Exception("%s error" % url)
        self.url = url
        self.branch = branch
        self.path = path
        self.app = app
        self.groups = groups
        self.bundleopen = bundleopen

    @staticmethod
    def parser_manifest(manifest, by_group=[], by_project=[], allow_private=False, order=False,
                        ignore_app=False):
        """
        projects.xml 解析
        :param manifest:
        :param by_group:
        :param by_project:
        :param allow_private:
        :param order:
        :param ignore_app:
        :return:
        """
        try:
            root = minidom.parse(manifest)
        except (OSError, xml.parsers.expat.ExpatError) as e:
            raise Exception("error parsing manifest %s: %s" % (manifest, e))

        if not root or not root.childNodes:
            raise Exception("no root node in %s" % (manifest,))

        for manifest in root.childNodes:
            if manifest.nodeName == 'manifest':
                break
            else:
                raise Exception("no <manifest> in %s" % (manifest,))

        host = manifest.getAttribute("host")
        if not host:
            raise Exception("no host attr in %s" % (manifest,))

        base_branch = manifest.getAttribute("branch")
        if not base_branch:
            raise Exception("no branch attr in %s" % (manifest,))

        index = 1
        projects = []
        for node in manifest.childNodes:
            if node.nodeName == 'project':
                url = node.getAttribute("url")
                if not url:
                    raise Exception("no %s in <%s> within %s" %
                                    ("url", "project", manifest))
                if not url.startswith("http") and not url.startswith("git@"):
                    url = host + url
                branch = node.getAttribute("branch")
                if not branch:
                    branch = base_branch
                path = node.getAttribute("path")
                if not path:
                    path = url.split('/')[-1].split('.')[0]
                app = True if "true" == node.getAttribute("app") else False
                groups = node.getAttribute("groups")
                bundleopen = node.getAttribute("bundleOpen")
                allow = False
                if len(by_group) > 0:
                    for group in by_group:
                        if groups and group in groups:
                            allow = True
                            break
                elif len(by_project) > 0:
                    for pro in by_project:
                        if path == pro:
                            allow = True
                            break
                else:
                    allow = True

                # 过滤私有
                if groups and "private" in groups and not allow_private:
                    allow = False
                # 过滤App
                if ignore_app and app:
                    allow = False

                if allow:
                    if order:
                        path = "%s-%s" % (str(index).zfill(3), path)
                    project = XmlProject(url, branch, path, app, groups, bundleopen)
                    projects.append(project)
                    index = index + 1
        return projects


def cmd_clone(args):
    """
    克隆子项目
    :param args:
    :return:
    """
    is_order = args.order
    allow_private = args.allow_private
    groups = args.by_group
    projects = args.by_project
    ignore_app = args.ignore_app

    try:
        groups_size = len(groups)
        projects_size = len(projects)
        if groups_size > 0 and projects_size > 0:
            raise Exception(u"by_group 和 by_project 不能同时使用")
        os.chdir(dir_current)
        projects = XmlProject.parser_manifest("projects.xml", by_group=groups, by_project=projects,
                                              allow_private=allow_private, order=is_order,
                                              ignore_app=ignore_app)
        for project in projects:
            if not os.path.exists(os.path.join(dir_current, project.path)):
                slog(u"Module:%s  Branch：%s" % (project.path, project.branch))
                slog("Url:%s" % project.url)
                cmd = "git clone %s -b %s %s" % (project.url, project.branch, project.path)
                os.popen(cmd)
                print ""  # 换行
            else:
                slog("%s has already existed" % project.path)
    except Exception, e:
        sloge(e.message)


def _git_projects():
    """
    获取git子项目
    :return:
    """
    check_root_project()

    sub_projects = []
    sub_file_list = [x for x in os.listdir(dir_current) if
                     check_sub_project(x, False)]
    for sub_file in sub_file_list:
        dir_git = os.path.join(dir_current, sub_file, ".git")
        if os.path.exists(dir_git) and os.path.isdir(dir_git):
            sub_projects.append(sub_file)
        else:
            print ">>>>>>%s is not git repo" % sub_file
    return sub_projects


def _git_check(branch_name, sub_projects, cmd_list):
    """
    校验git项目合法性
    :param branch_name: 分支名称
    :param sub_projects: 子项目列表list
    :param cmd_list: 命令列表list
    :return: 校验结果
    """
    slog(u"子项目合法性校验", loading=True)
    result = []
    for sub_file in sub_projects:
        process_status = subprocess.Popen(["git", "status"], stderr=subprocess.PIPE,
                                          stdout=subprocess.PIPE,
                                          cwd=os.path.join(dir_current, sub_file))
        code_status = process_status.wait()
        if code_status == 0:
            result_status = process_status.stdout.read()
            if ("working directory clean" not in result_status) and ("working tree clean" not in result_status):
                result.append(u"子项目[%s] not clean" % sub_file)
                continue
        else:
            result.append(u"子项目[%s]运行[git status]异常" % sub_file)
            continue

        process_check = subprocess.Popen(cmd_list, stderr=subprocess.PIPE, stdout=subprocess.PIPE,
                                         cwd=os.path.join(dir_current, sub_file))
        code_check = process_check.wait()
        if code_check == 0:
            result_check = [x.rstrip() for x in process_check.stdout.readlines()]
            for branch in result_check:
                if branch.endswith(branch_name):
                    result.append(u"子项目[%s] - [%s]存在" % (sub_file, branch_name))
                    break
        else:
            result.append(u"子项目[%s]运行[git branch -a / git tag]异常" % sub_file)
    return result


def _git_create_push(branch_name, sub_projects, cmd_list, is_push):
    """
    创建分支 or Tag
    :param branch_name:
    :param sub_projects:
    :return:
    """
    result_list = []
    for sub_file in sub_projects:
        process_branch = subprocess.Popen(cmd_list, stderr=subprocess.PIPE,
                                          stdout=subprocess.PIPE,
                                          cwd=os.path.join(dir_current, sub_file))
        code_check = process_branch.wait()
        if code_check == 0:
            slog(u"%s 创建 %s 成功" % (sub_file, branch_name))
        else:
            result_list.append(u"%s 创建 %s 失败" % (sub_file, branch_name))
            continue

        if is_push:
            process_push = subprocess.Popen(["git", "push", "-u", "origin", branch_name],
                                            stderr=subprocess.PIPE,
                                            stdout=subprocess.PIPE,
                                            cwd=os.path.join(dir_current, sub_file))
            code_push = process_push.wait()
            if code_push == 0:
                slog(u"%s push %s 成功" % (sub_file, branch_name))
            else:
                result_list.append(u"%s push %s 失败" % (sub_file, branch_name))

    if len(result_list) > 0:
        slog("-----------------")
        for result in result_list:
            slog(result)
        slogr(False)
    else:
        slogr(True)


def _git_delete_push(branch_name, sub_projects, local_list, push_list, is_push):
    """
    创建分支 or Tag
    :param branch_name:
    :param sub_projects:
    :return:
    """
    result_list = []
    for sub_file in sub_projects:
        process_branch = subprocess.Popen(local_list, stderr=subprocess.PIPE,
                                          stdout=subprocess.PIPE,
                                          cwd=os.path.join(dir_current, sub_file))
        code_check = process_branch.wait()
        if code_check == 0:
            slog(u"%s 删除 %s 成功" % (sub_file, branch_name))
        else:
            result_list.append(
                u"%s 删除 %s 失败\n%s" % (sub_file, branch_name, process_branch.stderr.read()))

        if is_push:
            process_push = subprocess.Popen(push_list, stderr=subprocess.PIPE,
                                            stdout=subprocess.PIPE,
                                            cwd=os.path.join(dir_current, sub_file))
            code_push = process_push.wait()
            if code_push == 0:
                slog(u"%s 删除远程 %s 成功" % (sub_file, branch_name))
            else:
                result_list.append(
                    u"%s 删除远程 %s 失败\n%s" % (sub_file, branch_name, process_push.stderr.read()))

    if len(result_list) > 0:
        slog("-----------------")
        for result in result_list:
            slog(result)
        slogr(False)
    else:
        slogr(True)


def cmd_branch(args):
    """
    创建分支
    :param args:
    :return:
    """
    branch_name = args.name
    is_delete = args.delete
    is_push = args.push

    sub_projects = _git_projects()
    if is_delete:
        _git_delete_push(branch_name, sub_projects, ["git", "branch", "-d", branch_name],
                         ["git", "push", "origin", ":" + branch_name], is_push)
    else:
        result_list = _git_check(branch_name, sub_projects, ["git", "branch", "-a"])
        if len(result_list) > 0:
            for mess in result_list:
                slog(mess)
            slogr(False)
            return

        slog(u"批量创建分支[%s]" % branch_name, loading=True)

        _git_create_push(branch_name, sub_projects, ["git", "checkout", "-b", branch_name], is_push)


def cmd_tag(args):
    """
    创建tag
    :param args:
    :return:
    """
    is_delete = args.delete
    tag_name = args.name
    tag_message = args.message
    if not tag_message:
        tag_message = "tag at" + time.strftime(" %Y-%m-%d %H:%M:%S", time.localtime(time.time()))

    sub_projects = _git_projects()
    if is_delete:
        _git_delete_push(tag_name, sub_projects, ["git", "tag", "-d", tag_name],
                         ["git", "push", "origin", ":refs/tags/" + tag_name], True)
    else:
        result_list = _git_check(tag_name, sub_projects, ["git", "tag"])
        if len(result_list) > 0:
            for mess in result_list:
                slog(mess)
            slogr(False)
            return

        slog(u"批量创建Tag[%s]" % tag_name, loading=True)

        _git_create_push(tag_name, sub_projects, ["git", "tag", "-a", tag_name, "-m", tag_message],
                         True)


def cmd_pull(args):
    """
    git pull
    :param args:
    :return:
    """
    cmd = "git pull"
    try:
        sub_projects = _git_projects()
        for sub_file in sub_projects:
            slog("git pull [%s]" % sub_file)
            os.chdir(os.path.join(dir_current, sub_file))
            result = os.popen(cmd).read().strip()
            print "--\n%s\n--" % result
            # if "Updating" in result:
            #     raise Exception("[%s] maybe needs to merge" % sub_file)
        slog("All projects have been updated\n")
    except Exception, e:
        sloge(e.message)
    except KeyboardInterrupt:
        sloge("Cancel")


def cmd_reset(args):
    """
    git reset
    :param args:
    :return:
    """
    cmd = "git reset --hard"
    sub_projects = _git_projects()
    print ">>>>>>start to running<<<<<<"
    for sub_file in sub_projects:
        print ">>>>>>Run [git %s] at dir [%s]" % (cmd, sub_file)
        os.chdir(os.path.join(dir_current, sub_file))
        git_cmd = os.popen(cmd)
        print git_cmd.read()
    print ">>>>>>running stop<<<<<<"


###################################################################
### apk操作：安装、上传等
###################################################################

# 提示用户选择一个数字
def getNum(numRang):
    try:
        inputNum = int(raw_input('please input num: '))
    except NameError and ValueError:
        print 'input err, num rang: (1 - %s)' % numRang
        return getNum(numRang)
    else:
        if inputNum not in range(1, numRang + 1):
            print 'input err, num rang: (1 - %s)' % numRang
            return getNum(numRang)
        else:
            return inputNum


# 系统打开文件
def showFile(path):
    userPlatform = platform.system()					# 获取操作系统
    if userPlatform == 'Darwin':						# Mac
        subprocess.call(['open', path])
    elif userPlatform == 'Linux':						# Linux
        subprocess.call(['xdg-open', path])
    else:												# Windows
        os.startfile(path)

# 生成二维码
def generateQRCode(text):
    try:
        import qrcode
        img = qrcode.make(data=text) # 生成二维码
        img.show() # 直接显示二维码
        # img.save("baidu.jpg") # 保存二维码为文件
        return 1
    except ImportError:
        # print "Please install python qrcode lib, can generate QR code !"
        pass

    try:
        import requests
        response = requests.get(QRCODE_API + text)
        if response.status_code == 200:
            # 先判断缓存目录是否存在
            if not os.path.exists(os.path.dirname(QR_CODE_IMG_CACHE_PAHT)):
                os.mkdir(os.path.dirname(QR_CODE_IMG_CACHE_PAHT))
            # 保存二维码
            with open(QR_CODE_IMG_CACHE_PAHT,'wb')as img:
                img.write(response.content)
            # 显示二维码
            showFile(QR_CODE_IMG_CACHE_PAHT)
            return 2
        else:
            return 0
    except ImportError:
        print "Please install python requests lib !"
        return 0

# 上传apk
def uploadApk(apkPath):
    # 上传到Finder中后的名字，如：20200521-10:25:00.apk
    apkFile = open(apkPath, 'rb')
    upApkName = os.path.basename(apkFile.name).replace('.apk', '%s%s.apk' % ('-', time.strftime("%Y-%m-%d-%H:%M:%S", time.localtime())))
    files = {
        'file': (upApkName, apkFile),
        'job': (None, JOB_NAME),
        'platform': (None, 'android'),
    }

    try:
        import requests
        response = requests.post(UPLOAD_URL, files=files, auth=(UPLOAD_ACCOUNT, UPLOAD_PW))
        if response.status_code == 200:
            return response.text
        else:
            return ''
    except ImportError:
        print "Please install python requests lib !"
        return ''

# 寻找apkDir目录下的所有apk文件
def findApkPathByDir(apkDir, apkList):
    dir = os.listdir(apkDir)
    for p in dir:
        absP = os.path.join(apkDir, p)
        if os.path.isdir(absP):
            findApkPathByDir(absP, apkList)
        elif absP.endswith('.apk'):
            apkList.append(absP)

# 找到要操作的apk
def findApkPath():
    hasApkPath = False
    rootDir = os.listdir('.')
    for childDir in rootDir:
        if os.path.exists(os.path.join(childDir, APK_SPECIAL_PATH)):
            apkDir = os.path.join(childDir, APK_SPECIAL_PATH)
            hasApkPath = True
            break
    if hasApkPath:
        apks = []
        findApkPathByDir(apkDir, apks)
        apkNum = len(apks)
        if apkNum == 0:
            print 'Not find apk in path: ' + apkDir
            return ''
        elif apkNum == 1:
            return apks[0]
        else:
            print 'Apk num > 1, please choose one:'
            print '------------------------------'
            for i in range(apkNum):
                print '%s. %s' % (i + 1, apks[i])
            print '------------------------------'
            num = getNum(apkNum)
            return apks[num - 1]
    else:
        return ''

def cmd_apk(args):

    # 命令
    upload = args.upload
    install = args.install
    deps = args.deps

    if upload:
        apkPath = findApkPath()
        if os.path.exists(apkPath):
            print '1.Successful find apk, start upload it: ' + apkPath
            downloadUrl = uploadApk(apkPath)
            if len(downloadUrl) > 1 and downloadUrl.startswith('http'):
                print '2.Upload apk succeeded, download url:'
                print '  %s' % downloadUrl
                result = generateQRCode(downloadUrl)
                if result == 1:
                    print "3.QR code generate succeeded, from 'python qrcode lib'"
                elif result == 2:
                    print "3.QR code generate succeeded, from 'online api'"
                    print '  QR code path: %s' % QR_CODE_IMG_CACHE_PAHT
                else:
                    print '3.QR code generate failed'
            else:
                print '2.Upload apk failed, %s!' % downloadUrl
        else:
            print 'Not find apk, check the exec cmd directory is in WorkSpace --- Chinglish !!!'
    elif install:
        apkPath = findApkPath()
        if os.path.exists(apkPath):
            print '1. Start install apk: ' + apkPath
            install_output = os.popen("adb install -r %s" % (apkPath)).read()
            print install_output
            if 'Success' in install_output:
                startApp(apkPath)
            else:
                print 'install fail'
        else:
            print 'Not find apk, check the exec cmd directory is in WorkSpace --- Chinglish !!!'
    elif deps:
        print u'开始部署依赖'
        # 读取setting中的include 项目
        # 找到项目对应的maven id
        # 修改主项目中deps.gradle中的配置（根据gradle.properties中变量判断是哪个app, gome, bang, mini）
            # 添加configurations排除依赖
            # 添加项目compile依赖
        # 关闭debug下的混淆开关
        includeMoudles = []
        for line in open(file_settings):
            line = line.strip()
            if not (line.startswith('//') or line.startswith('/') or line.startswith('*')):
                ls = line.split('\"')
                moduleName = ls[1].replace(':', '')
                includeMoudles.append(moduleName)

        modules = []
        start = False
        for line in open(file_build):
            line = line.strip()
            if not (line == "" or line.startswith('//') or line.startswith('/') or line.startswith('*')):
                if line.startswith('ext.deps'):
                    start = True
                    continue
                elif line.startswith(']') and start:
                    start = False
                # 开始解析 ext.deps[ ] 中的配置
                if  start:
                    lines = line.split(' ', 1)
                    moduleName = lines[0]
                    if moduleName in includeMoudles:
                        matchObj = re.match(u".*'((com|cn)\.gome\.[^']*)'", lines[1], re.M|re.I)
                        if matchObj:
                            ga = matchObj.group(1)
                            gas = ga.split(':')
                            module = ModuleInfo(moduleName, gas[0], gas[1])
                            modules.append(module)
        for m in modules:
            print m

# module 的信息（名字、groupId、artifactId）
class ModuleInfo(object):
    def __init__(self, name, groupId, artifactId):
        self.name = name
        self.groupId = groupId
        self.artifactId = artifactId
    def __str__(self):
        return 'name:%s  groupId:%s  artifactId:%s' %(self.name,self.groupId,self.artifactId)

# 启动app
def startApp(apkPath):
    # 1、先找到aapt命令
    # 2、通过aapt命令查询包名和launch页信息
    print '2. Use aapt cmd, find app package and launch'
    dump_output = os.popen("%s dump badging %s" % ("aapt", apkPath))
    dump_output_lines = dump_output.readlines()
    package = ""
    launch = ""
    for line in dump_output_lines:
        if line.startswith("package:"):
            # print line # package: name='cn.gome.bangbang' versionCode='206' versionName='8.0.6'
            d = splitKV(line)
            try:
                package = d['name']
            except KeyError:
                print 'package find fail'
        if line.startswith("launchable-activity:"):
            # print line # launchable-activity: name='com.gome.ecmall.home.LaunchActivity'
            d = splitKV(line)
            try:
                launch = d['name']
            except KeyError:
                print 'launchable-activity find fail'
    if package == "" or launch == "":
        print 'find app info fail'
        return
    #/3、再通过adb命令启动app
    print '3. Start open app...'
    start_output = os.popen("adb shell am start -n %s/%s" % (package, launch))
    print start_output.read()

# 分解aapt查找出的信息，组装成字典
def splitKV(line):
    d = {}
    kvs = line.split(' ')
    for kvStr in kvs:
        if '=' in kvStr:
            kv = kvStr.split('=')
            d[kv[0]] = kv[1].replace('\'', '')
    return d

def swRemoteHost(host, moduleDir):
    if os.path.isdir(moduleDir) and ".git" in os.listdir(moduleDir):
        print moduleDir

        # 查看远程地址
        remoteV = os.popen("cd %s && git remote -v" % (moduleDir)).read()

        # 分割，找到具体url
        gitOldUrl = remoteV.split("\n")[0].split()[1]
        print u"原git地址: %s" % gitOldUrl

        # 切换远程地址
        gitOldHost = gitOldUrl.split(":")
        # print u"原git host地址: %s" % gitOldHost[0]
        gitNewUrl = gitOldUrl.replace(gitOldHost[0], host)
        print u"新git地址: %s" % gitNewUrl
        cmdSet = "git remote set-url origin %s" % gitNewUrl
        cmdSet = "cd %s && %s" % (moduleDir, cmdSet)
        # print cmdSet
        os.popen(cmdSet).read()
        print u"%s 切换远程地址执行完成 ！\n" % moduleDir

def set_remote(args):
    set = args.set
    if set:
        newHostUrl = set[0]

        rootDir = os.listdir('.')
        for childDir in rootDir:
            swRemoteHost(newHostUrl, childDir)

        swRemoteHost(newHostUrl, os.path.abspath('.'))

        print u" ~~~全部执行完毕 ！！！"

###################################################################
### 主程序入口
###################################################################
if __name__ == '__main__':
    """执行入口
    """
    # debug
    # sys.argv.append("serv-update")

    # 默认打印帮助信息
    if len(sys.argv) == 1:
        sys.argv.append('--help')
    # 创建命令行解析器
    parser = argparse.ArgumentParser(prog="apkfly", description=u"国美workspace帮助工具",
                                     epilog="make it easy!")
    subparsers = parser.add_subparsers(title=u"可用命令")
    subparsers.required = True

    # 添加子命令

    # 把workspace内所有的module配置到settings.gradle
    parser_setting = subparsers.add_parser("setting",
                                           help=u"把workspace内所有的module配置到settings.gradle")
    parser_setting.set_defaults(func=cmd_setting)

    # 提交gradle.properties到git服务器
    parser_setting = subparsers.add_parser("pushprop", help=u"提交gradle.properties到git服务器")
    parser_setting.set_defaults(func=cmd_prop)
    parser_setting.add_argument('-m', type=str, help=u'push评论信息')
    parser_setting.add_argument('-b', type=str, default='mergeDev', help=u'push 分支')

    # 关闭awb
    parser_close_awb = subparsers.add_parser("closeawb", help=u"关闭awb开关")
    parser_close_awb.set_defaults(func=cmd_close_awb)
    parser_close_awb.add_argument('-a', "--all", help=u'所有版本为awb', default=True)

    # 打开awb
    parser_open_awb = subparsers.add_parser("openawb", help=u"打开awb开关")
    parser_open_awb.set_defaults(func=cmd_open_awb)
    parser_open_awb.add_argument('-a', "--all", help=u'所有版本为awb', default=True)

    # 版本自增
    parser_version = subparsers.add_parser("version", help=u"自增gradle.properties内的 aar 配置版本")
    parser_version.set_defaults(func=cmd_version_add)
    parser_version.add_argument('-s', "--start", type=str, default='AAR_GFRAME_HTTP_VERSION',
                                help=u'起始AAR版本【例：AAR_MFRAME2_VERSION】')
    parser_version.add_argument('-e', "--end", type=str, default='AAR_MAPP_VERSION',
                                help=u'终止AAR版本')
    parser_version.add_argument('-i', "--index", type=int, default=2, choices=[1, 2, 3],
                                help=u'自增版本索引【1大版本，2中间版本，3小版本】')
    parser_version.add_argument('-v', '--value', type=int, help=u'版本，默认值')

    # 批量生成aar并提交至maven私服
    parser_upload = subparsers.add_parser("upload",
                                          help=u"按module名称 数字排列顺序 依次 执行gradle uploadArchives")
    parser_upload.set_defaults(func=cmd_upload)
    parser_upload.add_argument('-s', "--start", type=str, help=u'执行起始点【项目名前三位，例：027】')
    parser_upload.add_argument('-o', "--only", help=u'只执行一个', action='store_true',
                               default=False)
    # 批量生成awb并提交至maven私服
    parserawb_upload = subparsers.add_parser("uploadawb",
                                             help=u"按module名称 数字排列顺序 依次 执行gradle uploadArchives")
    parserawb_upload.set_defaults(func=cmd_upload_awb)

    # 分析项目依赖关系
    parser_deps = subparsers.add_parser("deps", help=u"项目依赖关系分析")
    parser_deps.set_defaults(func=cmd_dep)
    parser_deps.add_argument("project", type=str, help=u'待分析依赖关系的项目名称')

    # 更新代码
    parser_pull = subparsers.add_parser("pull", help=u"更新 项目代码")
    parser_pull.set_defaults(func=cmd_pull)

    # reset
    parser_reset = subparsers.add_parser("reset", help=u"重置 项目代码")
    parser_reset.set_defaults(func=cmd_reset)

    # 克隆
    parser_clone = subparsers.add_parser("clone", help=u"克隆子工程")
    parser_clone.set_defaults(func=cmd_clone)
    parser_clone.add_argument("-o", "--order", help=u'对子项目进行排序', action='store_true', default=False)
    parser_clone.add_argument("-a", "--allow_private", help=u'包含私有项目', action='store_true',
                              default=False)
    parser_clone.add_argument("-g", "--by_group", help=u'根据组进行克隆', action='append', default=[])
    parser_clone.add_argument("-p", "--by_project", help=u'根据项目名进行克隆', action='append', default=[])
    parser_clone.add_argument("-i", "--ignore_app", help=u'忽略App', action='store_true',
                              default=False)

    # 创建branch
    parser_branch = subparsers.add_parser("branch", help=u"创建分支")
    parser_branch.set_defaults(func=cmd_branch)
    parser_branch.add_argument("name", help=u'分支名称', action='store')
    parser_branch.add_argument("-p", "--push", help=u'是否推送到服务器', action='store_true', default=False)
    parser_branch.add_argument("-d", "--delete", help=u'删除分支', action='store_true', default=False)

    # 创建tag
    parser_tag = subparsers.add_parser("tag", help=u"打tag")
    parser_tag.set_defaults(func=cmd_tag)
    parser_tag.add_argument("name", help=u'tag名称', action='store')
    parser_tag.add_argument("-m", "--message", help=u'评论信息', action='store')
    parser_tag.add_argument("-d", "--delete", help=u'删除分支', action='store_true', default=False)

    # 仅用于Jenkins更新构建源码
    parser_apk = subparsers.add_parser("serv-update", help=u"打包for jenkins")
    parser_apk.set_defaults(func=cmd_update_project)
    parser_apk.add_argument("-o", "--order", help=u'对子项目进行排序', action='store_true', default=False)
    parser_apk.add_argument("-a", "--allow_private", help=u'包含私有项目', action='store_true',
                            default=False)
    parser_apk.add_argument("-g", "--by_group", help=u'根据组进行克隆', action='append', default=[])
    parser_apk.add_argument("-p", "--by_project", help=u'根据项目名进行克隆', action='append', default=[])
    parser_apk.add_argument("-i", "--ignore_app", help=u'忽略App', action='store_true',
                            default=False)

    # 操作apk文件
    parser_apk_ = subparsers.add_parser("deploy", help=u"开发部署工具")
    parser_apk_.set_defaults(func=cmd_apk)
    parser_apk_.add_argument("-u", "--upload", help=u'上传apk到finder', action='store_true', default=False)
    parser_apk_.add_argument("-i", "--install", help=u'自动寻找apk，并安装到手机', action='store_true', default=False)
    # parser_apk_.add_argument("-di", "--debugInstall", help=u'构建Debug包，并安装到手机', action='store_true', default=False)
    # parser_apk_.add_argument("-ri", "--releaseInstall", help=u'构建Release包，并安装到手机', action='store_true', default=False)
    parser_apk_.add_argument("-d", "--deps", help=u'根据setting中的配置的项目，部署依赖配置', action='store_true', default=False)


    # 切换远程地址
    parser_remote = subparsers.add_parser("remote", help=u"远程地址")
    parser_remote.set_defaults(func=set_remote)
    parser_remote.add_argument("-s", "--set", help=u'切换远程地址Host, apkfly.py remote -s git@code.gome.inc', action='append', default=[])

    # 参数解析
    args = parser.parse_args()
    args.func(args)
