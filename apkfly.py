import os
import re
import sys


# This script is used to execute project commands in batches
# ~ python apkfly.py [upload]
# by qiudongchao
class apkfly(object):
    file_build_gradle = "build.gradle"
    dir_current = os.path.abspath(".")
    file_settings = os.path.join(dir_current, "settings.gradle")
    file_build = os.path.join(dir_current, file_build_gradle)
    dir_build = os.path.join(dir_current, "build")
    file_temp = os.path.join(dir_build, "apkfly-temp.txt")

    def __init__(self):
        if not os.path.exists(self.dir_build):
            os.mkdir(self.dir_build)

    # Verify that the current root project is legal
    def check_root_project(self):
        file_exist = os.path.exists(self.file_build) and os.path.exists(self.file_settings)
        is_file = os.path.isfile(self.file_settings) and os.path.isfile(self.file_build)
        return file_exist and is_file

    # Verify the sub project is legal
    def check_sub_project(self, sub_project):
        check_result = False
        if os.path.isdir(sub_project):
            if os.path.exists(os.path.join(self.dir_current, sub_project, self.file_build_gradle)):
                p = re.compile(r"^\d{3}-[A-Za-z0-9]+$")
                if p.match(sub_project):
                    check_result = True
        return check_result

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

    def write_temp(self, temp_list):
        try:
            temp_file = open(self.file_temp, "w")
            temp_file.write(','.join(temp_list))
        finally:
            temp_file.close()

    # execute sub project commands in batches
    def exec_sub_project(self, cmd):
        if self.check_root_project():
            print ">>>>>>start to running<<<<<<"
            temp_list = self.read_temp()
            run_flag = True
            sub_file_list = [x for x in os.listdir(self.dir_current) if self.check_sub_project(x)]
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


# main
if __name__ == '__main__':
    args = sys.argv
    apk = apkfly()
    if len(args) == 2:
        if args[1] == "upload":
            apk.exec_sub_project("uploadArchives")
        else:
            apk.exec_sub_project(args[1])
    else:
        apk.exec_sub_project("aR")
