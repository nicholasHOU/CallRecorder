#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import re
import sys

# 解决win命令行乱码问题
reload(sys)
sys.setdefaultencoding('utf-8')
##########################################

file_build_gradle = "build.gradle"
dir_current = os.path.abspath(".")
file_settings = os.path.join(dir_current, "settings.gradle")
file_build = os.path.join(dir_current, file_build_gradle)

def deployDeps():
    print u'开始部署依赖'
    # 读取setting中的include 项目
    # 找到项目对应的maven id
    # 修改主项目中deps.gradle中的配置（根据gradle.properties中变量判断是哪个app, gome, bang, mini）
    # 添加configurations排除依赖
    # 添加项目compile依赖
    # 关闭debug下的混淆开关

    # 1、settings.gradle中include的所有module
    includeModules = getIncludeModule()
    print u"1、include的所有module配置读取完毕"

    # 2、module的maven信息，并include的module在ext.deps[ ]中打开依赖
    moduleInfos = getModuleMavenInfo(includeModules)
    print u"2、includeModule的maven信息读取完毕，并在ext.deps[ ]中打开依赖"

    # 3、主工程build.gradle加入部署依赖
    mainModuleName = getMainModule(includeModules, moduleInfos)
    writeConfigurationsExcludesAndCompileToBuildGradle(ModuleInfo(mainModuleName, '', ''), moduleInfos)
    print u"3、主工程 %s build.gradle加入部署依赖" % mainModuleName

    # 4、往所有子module中的每一个dep都写入所有排除
    for m in moduleInfos:
        writeDepExcludesToBuildGradle(m, moduleInfos)
        print u"4、子工程 %s build.gradle加入DepExcludes" % m.name

    print u"部署完毕"

def getMainModule(includeModules, moduleInfos):
    for includeModule in includeModules:
        has = False
        for moduleInfo in moduleInfos:
            if includeModule == moduleInfo.name:
                has = True
        if not has:
            return includeModule

def getIncludeModule():
    """获取include中的所有module
    :return:
    """
    includeModules = []
    for line in open(file_settings):
        line = line.strip()
        if not (line.startswith('//') or line.startswith('/') or line.startswith('*')):
            ls = line.split('\"')
            moduleName = ls[1].replace(':', '')
            includeModules.append(moduleName)
    return includeModules

def getModuleMavenInfo(includeModules):
    """获取module的maven信息
    :param includeModules:
    :return:
    """
    moduleInfos = []
    start = False
    with open(file_build, "r") as file, open("%s.bak" % file_build, "w") as file_bak:
        for line in file:

            #1、把依赖本地module开关打开
            for includeModule in includeModules:
                if includeModule in line:
                    line = line.replace('rootProject.ext.proDeps', 'true')
                    break
            file_bak.write(line)

            # 2、马上进入解析 ext.deps[ ] 中的配置
            line = line.strip()
            lines = line.split(' ', 1)
            moduleName = lines[0]
            if not (line == "" or line.startswith('//') or line.startswith('/') or line.startswith('*')):
                if line.startswith('ext.deps'):
                    start = True
                    continue
                elif line.startswith(']') and start:
                    start = False
                # 开始解析
                if  start:
                    if moduleName in includeModules:
                        # 从build.gradle的deps配置中查出module的maven信息
                        matchObj = re.match(u".*'((com|cn)\.gome\.[^']*)'", lines[1], re.M|re.I)
                        if matchObj:
                            ga = matchObj.group(1)
                            gas = ga.split(':')
                            module = ModuleInfo(moduleName, gas[0], gas[1])
                            moduleInfos.append(module)
        file.close()
        file_bak.close()
        # 把新文件覆盖现文件
        os.remove(file_build)
        os.rename("%s.bak" % file_build, file_build)
    return moduleInfos

def writeDepExcludesToBuildGradle(moule, moduleInfos):
    """部署配置(局部exclude)到build.gradle
    :param moule: 配置此module中的依赖
    :param moduleInfos: 本工程include的module信息
    :return:
    """
    pass
    moduleBuildGradle = moule.getBuildFile()
    print u"    找到build.gradle文件: %s" % moduleBuildGradle
    moduleBuildGradle_bak = moduleBuildGradle + '.bak'
    moduleBuildGradle_new = moduleBuildGradle + '.new'

    with open(moduleBuildGradle_new, "w") as new_file:
        for line in open(moduleBuildGradle):
            # 先把本行数据写入
            new_file.write(line)
            if line.strip().startswith('transitive'):
                # 写入局部exclude
                configurations = []
                for m in moduleInfos:
                    if moule.name != m.name:# 排除自己的maven配置
                        configurations.append(m.exclude)
                new_file.writelines([configuration + '\n' for configuration in configurations])
                print u"    写入compile完毕"
    new_file.close()
    # 把目前的build文件备份，新生成的build文件替换原文件
    if os.path.exists(moduleBuildGradle_bak): os.remove(moduleBuildGradle_bak)
    os.rename(moduleBuildGradle, moduleBuildGradle_bak)
    os.rename(moduleBuildGradle_new, moduleBuildGradle)

def writeConfigurationsExcludesAndCompileToBuildGradle(moule, moduleInfos):
    """部署配置(ConfigurationsExcludes、Compile)到build.gradle
    :param moule: 配置此module中的依赖
    :param moduleInfos: 本工程include的module信息
    :return:
    """
    pass
    moduleBuildGradle = moule.getBuildFile()
    print u"    找到build.gradle文件: %s" % moduleBuildGradle
    moduleBuildGradle_bak = moduleBuildGradle + '.bak'
    moduleBuildGradle_new = moduleBuildGradle + '.new'

    with open(moduleBuildGradle_new, "w") as new_file:
        # 添加排除配置
        writeExcludesToBuildGradle(new_file, moule, moduleInfos)
        print u"    添加排除配置完毕"
        for line in open(moduleBuildGradle):
            # 先把本行数据写入
            new_file.write(line)
            # 添加依赖配置
            if line.strip().startswith('dependencies'):
                # 写入compile
                writeCompileToBuildGradle(new_file, moule, moduleInfos)
                print u"    写入compile完毕"
    new_file.close()
    # 把目前的build文件备份，新生成的build文件替换原文件
    if os.path.exists(moduleBuildGradle_bak): os.remove(moduleBuildGradle_bak)
    os.rename(moduleBuildGradle, moduleBuildGradle_bak)
    os.rename(moduleBuildGradle_new, moduleBuildGradle)

def writeExcludesToBuildGradle(new_file, curModule, moduleInfos):
    """往build.gradle中写入配置
     configurations{
       compile.exclude group: '', module: ''
     }
    :param new_file:
    :param curModule:
    :param moduleInfos: 本工程include的module信息
    :return:
    """
    configurations = []
    configurations.append('configurations{')
    for module in moduleInfos:
        if curModule.name != module.name:# 排除自己的maven配置
            configurations.append(module.compileExclude)
    configurations.append('}')
    new_file.writelines([configuration + '\n' for configuration in configurations])

#
def writeCompileToBuildGradle(new_file, curModule, moduleInfos):
    """往build.gradle中写入配置
    compile(deps.xxx){
      transitive = true
    }
    :param new_file:
    :param curModule:
    :param moduleInfos:
    :return:
    """
    compiles = []
    for module in moduleInfos:
        if curModule.name != module.name:# 排除自己的compile配置
            compiles.append(module.deps)
    new_file.writelines([compile + '\n' for compile in compiles])

# module 的信息（名字、groupId、artifactId）
class ModuleInfo(object):
    def __init__(self, name, groupId, artifactId):
        self.name = name
        self.groupId = groupId
        self.artifactId = artifactId
        self.deps = '    compile (deps.%s){\n        transitive = true\n    }' % name
        self.compileExclude = "    compile.exclude group: '%s', module: '%s'" % (groupId, artifactId)
        self.exclude = "        exclude group: '%s', module: '%s'" % (groupId, artifactId)

    def getBuildFile(self):
        if self.groupId == '':
            # 本modulInfo对象为主工程
            return os.path.join(dir_current, self.name, 'deps', 'gomedeps.gradle')
        else:
            return os.path.join(dir_current, self.name, file_build_gradle)

    def __str__(self):
        return 'name:%s  groupId:%s  artifactId:%s  \ndeps:\n%s  \nexclude:\n%s' % (self.name, self.groupId, self.artifactId, self.deps, self.exclude)
