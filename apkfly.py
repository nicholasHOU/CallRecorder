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

    def __init__(self):
        pass

    # Verify that the current root project is legal
    def check_root_project(self):
        file_exist = os.path.exists(self.file_build) and os.path.exists(self.file_settings)
        is_file = os.path.isfile(self.file_settings) and os.path.isfile(self.file_build)
        return file_exist and is_file

    # Verify the sub project is legal
    def check_sub_project(self, sub_project):
        if os.path.isdir(sub_project):
            if os.path.exists(os.path.join(self.dir_current, sub_project, self.file_build_gradle)):
                p = re.compile(r"^\d{3}-[A-Za-z0-9]+$")
                if p.match(sub_project):
                    return True
                else:
                    return False
            else:
                return False
        else:
            return False

    # execute sub project commands in batches
    def exec_sub_project(self, cmd):
        if self.check_root_project():
            print ">>>>>>start to running<<<<<<"
            sub_file_list = [x for x in os.listdir(self.dir_current) if self.check_sub_project(x)]
            for sub_file in sub_file_list:
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
                else:
                    print ">>>Error project:%s" % sub_file
                    break
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
