#!/usr/bin/python
# -*- coding: utf-8 -*-
"""This script is used to execute project commands in batches"""
import argparse
import logging
import os
import platform
import re
import subprocess
import sys
import time
import xml
from collections import Counter
from xml.dom import minidom

__author__ = "qiudongchao<1162584980@qq.com>"
__version__ = "4.0.0"

# 解决win命令行乱码问题
reload(sys)
sys.setdefaultencoding('utf-8')

file_build_gradle = "build.gradle"
dir_current = os.path.abspath(".")
file_settings = os.path.join(dir_current, "settings.gradle")
file_build = os.path.join(dir_current, file_build_gradle)
dir_build = os.path.join(dir_current, "build")

# , filename='sync.log', filemode='a'
logging.basicConfig(level=logging.DEBUG,
                    format='[%(asctime)s %(levelname)s] %(message)s',
                    datefmt='%Y-%m-%d %H:%M:%S')


def slog(message, loading=False, line=False):
    """日志打印
    """
    temp = ">>> " + message
    if loading:
        temp = temp + "..."
    if line:
        temp = temp + "\n"
    print temp


def slogr(success):
    slog(u"O(∩_∩)O哈哈~" if success else u"╮(╯▽╰)╭哎")


###################################################################
### 工作空间  校验
###################################################################
def check_root_project():
    """校验当前工作空间是否合法
    """
    file_exist = os.path.exists(file_build) and os.path.exists(file_settings)
    is_file = os.path.isfile(file_settings) and os.path.isfile(file_build)
    result = file_exist and is_file
    if not result:
        raise Exception(u"工作空间校验失败")


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


###################################################################
### 批量执行子项目命令
###################################################################
def exec_sub_project(cmd, args):
    """批量执行子项目命令【gradle】
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


def _upload(args):
    exec_sub_project("uploadArchives", args)


def _ar(args):
    exec_sub_project("aR", args)

##### 关闭awb版本
def close_awb(args):
    modifyXMLNode("projects.xml","false")
    updateVersion("awb","plus",args.all)
#### 开启awb
def open_awb(args):
    modifyXMLNode("projects.xml","true")
    updateVersion("plus","awb",args.all)

def _uploadawb(args):
    exec_awb_sub_project("uploadArchives")

def modifyXMLNode(manifest,isbundleopen):
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


def updateVersion(fromv,tov,all):
    first_prop = 'AAR_GFRAME_VERSION' if all else 'AAR_GFINANCE_VERSION'
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
        isbundle = is_bundle_project(sub_file,projects)
        if(isbundle):
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

def is_bundle_project(sub_file,projects):
    checkbundle = False
    for project in projects:
        if sub_file.endswith(project.path) and project.bundleopen:
            checkbundle = True
            break

    return checkbundle
###################################################################
### 将当前工作空间的项目部署到setting配置文件
###################################################################
def _setting(args):
    """重建setting文件
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


###################################################################
### 版本自增
###################################################################
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


###################################################################
### 提交 gradle.properties 到服务器
###################################################################
def _push_prop(args):
    """提交gradle.properties到git服务器
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


###################################################################
### 依赖分析
###################################################################
def _deps(args):
    """分析依赖关系"""
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
# 打包配置项目
APK_CONFIG = {
    "GomePlus": {
        "url": "git@gitlab.ds.gome.com.cn:mobile-android/GomePlus.git",
        "branch": "mergeDev"
    },
    "MApp": {
        "url": "git@gitlab.ds.gome.com.cn:mobile-android-business/MApp.git",
        "branch": "mergeDev"
    }
}


def _git_clone_ser(project_name, git_url, git_branch):
    os.chdir(dir_current)
    clone_cmd = os.popen("git clone %s -b %s %s" % (git_url, git_branch, project_name))
    print clone_cmd.read()


def _git_pull_ser(project_name, git_branch):
    os.chdir(os.path.join(dir_current, project_name))
    pull_cmd = os.popen("git pull origin %s" % git_branch)
    print pull_cmd.read()


def _update_project(args):
    """更新源码for jenkins
    """
    check_root_project()

    # main_project = args.main # 暂不用
    setting_content = ""
    for (key, value) in APK_CONFIG.items():
        # 获取 url,branch
        for (git_key, git_value) in value.items():
            if git_key == "url":
                git_url = git_value
            if git_key == "branch":
                git_branch = git_value
        if key and git_url and git_branch:
            os.chdir(dir_current)
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
        else:
            raise Exception(u"APK_CONFIG 配置错误")
    print u">>>子项目写入setting"
    with open(file_settings, "w") as setting_file:
        setting_file.write(setting_content)


###################################################################
### git 操作
###################################################################

class XmlProject(object):
    """manifest and parser
    """

    def __init__(self, url, branch, path, app, groups,bundleopen):
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
        """projects.xml 解析
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
                    project = XmlProject(url, branch, path, app, groups,bundleopen)
                    projects.append(project)
                    index = index + 1
        return projects


def _git_clone(args):
    """克隆子项目
    """
    is_order = args.order
    allow_private = args.allow_private
    groups = args.by_group
    projects = args.by_project
    ignore_app = args.ignore_app

    groups_size = len(groups)
    projects_size = len(projects)
    if groups_size > 0 and projects_size > 0:
        raise Exception(u"by_group 和 by_project 不能同时使用")

    projects = XmlProject.parser_manifest("projects.xml", by_group=groups, by_project=projects,
                                          allow_private=allow_private, order=is_order,
                                          ignore_app=ignore_app)
    for project in projects:
        if not os.path.exists(os.path.join(dir_current, project.path)):
            print u">>>克隆:%s  分支：%s... url:%s" % (project.path, project.branch, project.url)
            cmd = "git clone %s -b %s %s" % (project.url, project.branch, project.path)
            # if platform.system() == "Linux":
            #     process = subprocess.Popen(cmd, stderr=subprocess.PIPE,
            #                                stdout=subprocess.PIPE, cwd=dir_current, shell=True)
            #     code = process.wait()
            #     slogr(code == 0)
            # else:
            os.popen(cmd).read().strip()
            print ""  # 换行


def _git_projects():
    """获取git子项目
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
    """校验git项目合法性
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
            process_push = subprocess.Popen(["git", "push", "origin", branch_name],
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


def _git_branch(args):
    """ 创建分支
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


def _git_tag(args):
    """ 创建tag
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


def _git_pull(args):
    cmd = "git pull"
    sub_projects = _git_projects()
    print ">>>>>>start to running<<<<<<"
    for sub_file in sub_projects:
        print ">>>>>>Run [git %s] at dir [%s]" % (cmd, sub_file)
        os.chdir(os.path.join(dir_current, sub_file))
        git_cmd = os.popen(cmd)
        print git_cmd.read()
    print ">>>>>>running stop<<<<<<"


def _git_reset(args):
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
### 主程序入口
###################################################################


if __name__ == '__main__':
    """执行入口
    """
    # 创建build目录
    if not os.path.exists(dir_build):
        os.mkdir(dir_build)
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
    parser_setting.set_defaults(func=_setting)

    # 提交gradle.properties到git服务器
    parser_setting = subparsers.add_parser("pushprop", help=u"提交gradle.properties到git服务器")
    parser_setting.set_defaults(func=_push_prop)
    parser_setting.add_argument('-m', type=str, help=u'push评论信息')
    parser_setting.add_argument('-b', type=str, default='mergeDev', help=u'push 分支')
    # 关闭awb
    parser_close_awb = subparsers.add_parser("closeawb", help=u"关闭awb开关")
    parser_close_awb.set_defaults(func=close_awb)
    parser_close_awb.add_argument('-a', "--all",help=u'所有版本为awb',default=True)
    # 打开awb
    parser_open_awb = subparsers.add_parser("openawb", help=u"打开awb开关")
    parser_open_awb.set_defaults(func=open_awb)
    parser_open_awb.add_argument('-a', "--all",help=u'所有版本为awb',default=True)

    # 版本自增
    parser_version = subparsers.add_parser("version", help=u"自增gradle.properties内的 aar 配置版本")
    parser_version.set_defaults(func=_version_add)
    parser_version.add_argument('-s', "--start", type=str, default='AAR_GFRAME_VERSION',
                                help=u'起始AAR版本【例：AAR_MFRAME2_VERSION】')
    parser_version.add_argument('-e', "--end", type=str, default='AAR_MAPP_VERSION',
                                help=u'终止AAR版本')
    parser_version.add_argument('-i', "--index", type=int, default=2, choices=[1, 2, 3],
                                help=u'自增版本索引【1大版本，2中间版本，3小版本】')
    parser_version.add_argument('-v', '--value', type=int, help=u'版本，默认值')

    # 批量编译module
    parser_ar = subparsers.add_parser("ar", help=u"依次 编译 所有module")
    parser_ar.set_defaults(func=_ar)
    parser_ar.add_argument('-s', "--start", type=str, help=u'执行起始点【项目名前三位，例：027】')
    parser_ar.add_argument('-o', "--only", help=u'只执行一个', action='store_true',
                           default=False)

    # 批量生成aar并提交至maven私服
    parser_upload = subparsers.add_parser("upload",
                                          help=u"按module名称 数字排列顺序 依次 执行gradle uploadArchives")
    parser_upload.set_defaults(func=_upload)
    parser_upload.add_argument('-s', "--start", type=str, help=u'执行起始点【项目名前三位，例：027】')
    parser_upload.add_argument('-o', "--only", help=u'只执行一个', action='store_true',
                               default=False)
    # 批量生成awb并提交至maven私服
    parserawb_upload = subparsers.add_parser("uploadawb",
                                          help=u"按module名称 数字排列顺序 依次 执行gradle uploadArchives")
    parserawb_upload.set_defaults(func=_uploadawb)

    # 分析项目依赖关系
    parser_deps = subparsers.add_parser("deps", help=u"项目依赖关系分析")
    parser_deps.set_defaults(func=_deps)
    parser_deps.add_argument("project", type=str, help=u'待分析依赖关系的项目名称')

    # 更新代码
    parser_pull = subparsers.add_parser("pull", help=u"更新 项目代码")
    parser_pull.set_defaults(func=_git_pull)

    # reset
    parser_reset = subparsers.add_parser("reset", help=u"重置 项目代码")
    parser_reset.set_defaults(func=_git_reset)

    # 克隆
    parser_clone = subparsers.add_parser("clone", help=u"克隆子工程")
    parser_clone.set_defaults(func=_git_clone)
    parser_clone.add_argument("-o", "--order", help=u'对子项目进行排序', action='store_true', default=False)
    parser_clone.add_argument("-a", "--allow_private", help=u'包含私有项目', action='store_true',
                              default=False)
    parser_clone.add_argument("-g", "--by_group", help=u'根据组进行克隆', action='append', default=[])
    parser_clone.add_argument("-p", "--by_project", help=u'根据项目名进行克隆', action='append', default=[])
    parser_clone.add_argument("-i", "--ignore_app", help=u'忽略App', action='store_true',
                              default=False)

    # 创建branch
    parser_branch = subparsers.add_parser("branch", help=u"创建分支")
    parser_branch.set_defaults(func=_git_branch)
    parser_branch.add_argument("name", help=u'分支名称', action='store')
    parser_branch.add_argument("-p", "--push", help=u'是否推送到服务器', action='store_true', default=False)
    parser_branch.add_argument("-d", "--delete", help=u'删除分支', action='store_true', default=False)

    # 创建tag
    parser_tag = subparsers.add_parser("tag", help=u"打tag")
    parser_tag.set_defaults(func=_git_tag)
    parser_tag.add_argument("name", help=u'tag名称', action='store')
    parser_tag.add_argument("-m", "--message", help=u'评论信息', action='store')
    parser_tag.add_argument("-d", "--delete", help=u'删除分支', action='store_true', default=False)

    # 仅用于Jenkins更新构建源码
    parser_apk = subparsers.add_parser("serv-update", help=u"打包for jenkins")
    parser_apk.set_defaults(func=_update_project)
    parser_apk.add_argument('-m', "--main", type=str, default='GomePlus', help=u'主工程')

    # 参数解析
    args = parser.parse_args()
    args.func(args)
