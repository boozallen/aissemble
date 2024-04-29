#!/usr/bin/env python3

###
# #%L
# AIOps Foundation::DevOps
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import inspect
import json
import os
import sys
from shutil import copyfile
from os.path import isfile


class Term:
    """Just a utility class for controlling terminal colors"""
    END = '\033[0m'
    BOLD = '\033[1m'
    DIM = '\033[2m'  # fg is bg color but darker and desaturated
    ITALIC = '\033[3m'
    UNDERLINE = '\033[4m'
    BLINK = '\033[5m'
    INVERT = '\033[7m'  # fg and bg colors swapped

    class Fg:
        BLACK = '\033[30m'
        RED = '\033[31m'
        GREEN = '\033[32m'
        YELLOW = '\033[33m'
        BLUE = '\033[34m'
        MAGENTA = '\033[35m'
        CYAN = '\033[36m'
        WHITE = '\033[37m'

        class Bright:
            BLACK = '\033[90m'
            RED = '\033[91m'
            GREEN = '\033[92m'
            YELLOW = '\033[93m'
            BLUE = '\033[94m'
            MAGENTA = '\033[95m'
            CYAN = '\033[96m'
            WHITE = '\033[97m'

    class Bg:
        BLACK = '\033[40m'
        RED = '\033[41m'
        GREEN = '\033[42m'
        YELLOW = '\033[43m'
        BLUE = '\033[44m'
        MAGENTA = '\033[45m'
        CYAN = '\033[46m'
        WHITE = '\033[47m'

        class Bright:
            BLACK = '\033[100m'
            RED = '\033[101m'
            GREEN = '\033[102m'
            YELLOW = '\033[103m'
            BLUE = '\033[104m'
            MAGENTA = '\033[105m'
            CYAN = '\033[106m'
            WHITE = '\033[107m'

    @classmethod
    def printTest(cls):
        """Prints out all color possibilities for easier selection"""
        Term.__printClass__(cls)

    @staticmethod
    def __printClass__(cls, prefix='', colors=None):
        toplevel = False
        if colors is None:
            toplevel = True
            colors = {}
        prefix += cls.__name__ + '.'
        for name in dir(cls):
            if not name.startswith('__') and name != 'END':
                value = getattr(cls, name, None)
                if inspect.isclass(value):
                    Term.__printClass__(value, prefix, colors)
                elif isinstance(value, str):
                    colors[prefix + name] = value
        if toplevel:
            for color in sorted(colors, key=(lambda x: ('C' if 'Bg' in x else 'B' if 'Fg' in x else 'A')
                                                       + x.rsplit('.', 1)[1]
                                                       + str(x.count('.')))):
                Term.__printValue__(color, colors[color])

    @staticmethod
    def __printValue__(name, value):
        print(value, name.ljust(25), Term.END)


class Target:
    def __init__(self, name, outputfile, templatefile):
        self.name = name
        self.outputfile = outputfile
        self.templatefile = templatefile

    def getNameWidth(self):
        return len(self.name)

    def getOutputWidth(self):
        return len(self.outputfile)

    def getTemplateWidth(self):
        return len(self.templatefile)

    def __lt__(self, other):
        return self.templatefile < other.templatefile

    @classmethod
    def fromJson(cls, json):
        return cls(json['name'], json['outputFile'], removeprefix(json['templateName'], 'templates/'))


class GeneratorDetails:
    START_FLAG = '    /*--~-~-~~\n'
    UNUSED_LN = '    //--- ALERT: This generator is probably not used by any targets in targets.json\n'

    name: str
    targetwidth: int
    outputwidth: int
    templatewidth: int
    targets: list

    def __init__(self, name):
        self.name = name
        self.targetwidth = 0
        self.outputwidth = 0
        self.templatewidth = 0
        self.targets = []

    def add_target(self, target: Target):
        self.targets.append(target)
        if target.getNameWidth() > self.targetwidth:
            self.targetwidth = target.getNameWidth()
        if target.getOutputWidth() > self.outputwidth:
            self.outputwidth = target.getOutputWidth()
        if target.getTemplateWidth() > self.templatewidth:
            self.templatewidth = target.getTemplateWidth()

    def getheader(self):
        lines = [GeneratorDetails.START_FLAG,
                 f'     * Usages:\n',
                 f'     * | {"Target".ljust(self.targetwidth)}  | {"Template".ljust(self.templatewidth)}  | {"Generated File".ljust(self.outputwidth)}  |\n',
                 f'     * |{"-"*(self.targetwidth+3)}|{"-"*(self.templatewidth+3)}|{"-"*(self.outputwidth+3)}|\n']
        return lines

    def get_usage_notes(self):
        lines = self.getheader()
        targetNames = []
        for target in sorted(self.targets):
            if target.name in targetNames:
                print('[', Term.Fg.RED, Term.BOLD, 'ERROR', Term.END, '] ',
                      'Duplicate target found in targets.json! ',
                      Term.Bg.MAGENTA, Term.Fg.Bright.WHITE, Term.BOLD, target.name, Term.END, sep='')
            else:
                targetNames.append(target.name)
            # append the duplicate line regardless as another flag to the dev that they missed a duplicate
            lines.append(f'     * | {target.name.ljust(self.targetwidth)}  | {target.templatefile.ljust(self.templatewidth)}  |'
                         f' {target.outputfile.ljust(self.outputwidth)}  |\n')
        lines.append('     */\n\n')
        return lines

def removeprefix(string, prefix):
    if string.startswith(prefix):
        string = string[len(prefix):]
    return string

def removesuffix(string, suffix):
    if string.endswith(suffix):
        string = string[:len(string)-len(suffix)]
    return string

def read_targets(module_root):
    """Reads targets.json into a JSON structure"""
    with open(module_root + '/src/main/resources/targets.json', 'r') as targetsFile:
        targets_str = targetsFile.read()
        return json.JSONDecoder().decode(targets_str)


def map_generators(targets_json) -> dict:
    """Maps JSON structure to classes"""
    generators = {}
    for target in targets_json:
        name = target['generator']
        generator = generators.setdefault(name, GeneratorDetails(name))
        generator.add_target(Target.fromJson(target))
    return generators


def mark_unused(javaroot, generators: dict):
    """Adds an alert message to any Java class ending with Generator that is not present in the generators map"""
    unused = []
    for root, dirs, files in os.walk(javaroot):
        for filename in files:
            fullpath = os.path.join(root, filename)
            relative = removeprefix(fullpath, javaroot)
            classname = removesuffix(relative.replace('/', '.'), '.java')
            if classname.endswith('Generator') and classname not in generators:
                if add_unused_warning(fullpath):
                    unused.append(classname.rsplit('.', 1)[1])
    return unused


def mark_usages(javaroot, generators: dict):
    """Adds a comment block indicating the template and output file names generated by each generator"""
    for generator in generators.values():
        path = javaroot + generator.name.replace('.', '/') + '.java'
        add_usages(generator, path)
        # replace java class with new file
        copyfile(path + '.tmp', path)
        os.remove(path + '.tmp')


def add_unused_warning(path) -> bool:
    """Adds the ALERT unused line to the given file if it is a concrete class. Returns true if the line was added"""
    concrete = False
    with open(path, 'r') as javaFile:
        with open(path + '.tmp', 'w') as tempFile:
            concrete = copy_to_class_line(javaFile, tempFile)
            if concrete:
                tempFile.writelines(GeneratorDetails.UNUSED_LN)
                skip_existing(javaFile, tempFile)
                copy_remaining(javaFile, tempFile)
    if concrete:
        copyfile(path + '.tmp', path)
    os.remove(path + '.tmp')
    return concrete


def add_usages(generator, path):
    """Adds the usage comment block for the given generator to the given file"""
    with open(path, 'r') as javaFile:
        with open(path + '.tmp', 'w') as tempFile:
            copy_to_class_line(javaFile, tempFile)

            tempFile.writelines(generator.get_usage_notes())

            skip_existing(javaFile, tempFile)
            copy_remaining(javaFile, tempFile)


def copy_to_class_line(javaFile, tempFile) -> bool:
    """Copies lines from javaFile to tempFile until the class declaration line is read"""
    current_line = javaFile.readline()
    while current_line and not (current_line.startswith('public class')
               or current_line.startswith('public abstract class')
               or current_line.startswith('public interface')):
        tempFile.write(current_line)
        current_line = javaFile.readline()
    tempFile.write(current_line)
    if not current_line:
        print('[', Term.Fg.RED, Term.BOLD, 'ERROR', Term.END, '] ', f"Invalid generator class file: {javaFile.name}", sep='')
        exit(1)
    return current_line.startswith('public class')


def skip_existing(javaFile, tempFile):
    """Reads and ignores lines from tempFile if they are part of a usage comment or unused alert"""
    # if a usage comment exists in original file, skip it
    current_line = javaFile.readline()
    if current_line == GeneratorDetails.START_FLAG:
        while current_line and '*/' not in current_line:
            current_line = javaFile.readline()
        if not current_line:
            print('[', Term.Fg.RED, Term.BOLD, 'ERROR', Term.END, '] ', f"Invalid generator class file: {javaFile.name}", sep='')
            exit(1)
        # consume blank space after comment
        trailing = javaFile.readline()
        if trailing != '\n':
            tempFile.write(trailing)
    elif current_line != GeneratorDetails.UNUSED_LN:
        tempFile.write(current_line)


def copy_remaining(javaFile, tempFile):
    """Efficiently copies all remaining data from javaFile to tempFile"""
    data = javaFile.read(1024)
    while data:
        tempFile.write(data)
        data = javaFile.read(1024)


def update_generators():
    module_root = sys.argv[1]
    javaroot = module_root + "/src/main/java/"
    targets_json = read_targets(module_root)
    generators = map_generators(targets_json)

    unused = mark_unused(javaroot, generators)
    for cls in unused:
        print('[', Term.Fg.YELLOW, Term.BOLD, 'WARNING', Term.END, '] ',
              'The generator class ',
              Term.Bg.MAGENTA, Term.Fg.Bright.WHITE, Term.BOLD, cls, Term.END,
              ' is not used by any target in targets.json!!', sep='')
    mark_usages(javaroot, generators)


if __name__ == '__main__':
    update_generators()
