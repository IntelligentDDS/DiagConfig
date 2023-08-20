import json
import os
import pandas as pd
import re

from SubjectSysInfo import SubjectSysInfo
from optionfeature.ConfigOption import ConfigOption
# print(SubjectSysInfo.ground_truth)
file_sep = os.sep
feature_set = set()


def feature_patter_re(original_stmt):
    str = re.search('<.*:', original_stmt)
    if str is not None:
        return str.group(0)[1:-1]
    str = re.search('new.*array', original_stmt)
    if str is not None:
        return str.group(0)
    if 'entermonitor' in original_stmt:
        return 'entermonitor'
    return original_stmt


results = {}

# parse json
for sys_name in SubjectSysInfo.subject_sys_name:
    file_list = os.listdir(
        SubjectSysInfo.base_path + file_sep + sys_name + file_sep + '20480m' + file_sep)
    optionList = {}
    for file in file_list:
        if os.path.splitext(file)[1] == '.json':
            with open(SubjectSysInfo.base_path + file_sep +
                      sys_name + file_sep + '20480m' + file_sep + file, 'r') as f:
                optionName = (os.path.basename(f.name)[:-5])
                data = json.load(f)
                for obj in data:
                    # optionName = obj['options'][0]
                    if optionName in optionList:
                        temp_Option = optionList[optionName]
                    else:
                        temp_Option = ConfigOption(optionName)
                    temp_Option.countType(obj['operationType'])
                    originalStmt = obj['originalStmt']
                    feature_str = feature_patter_re(originalStmt)
                    temp_Option.countFeature(feature_str)
                    feature_set.add(feature_str)
                    if optionName not in optionList:
                        optionList[optionName] = temp_Option
    results[sys_name] = optionList

# print(feature_set)
# Feature Alignment
for sys_name in SubjectSysInfo.subject_sys_name:
    for temp_Option in results[sys_name].values():
        for single_feature in feature_set:
            if single_feature not in temp_Option.originalStmtCollection:
                temp_Option.originalStmtCollection[single_feature] = 0

# format feature vector
for sys_name in SubjectSysInfo.subject_sys_name:
    result_list = []
    col_name = None
    gt = SubjectSysInfo.ground_truth[sys_name]
    for temp_Option in results[sys_name].values():
        ordered_key = sorted(temp_Option.originalStmtCollection)
        feature_list = [temp_Option.originalStmtCollection[k]
                        for k in ordered_key]
        ordered_key = list(
            temp_Option.controlTypeCounting.keys()) + ordered_key
        ordered_key.insert(0, 'option_name')
        ordered_key.append('perf_affected')
        feature_list = list(
            temp_Option.controlTypeCounting.values()) + feature_list
        feature_list.insert(0, temp_Option.name)
        if temp_Option.name in gt:
            feature_list.append(1)
        else:
            feature_list.append(-1)
        result_list.append(feature_list)
        if col_name is None:
            col_name = ordered_key.copy()
    dataframe = pd.DataFrame(result_list, columns=col_name)
    dataframe.to_csv('./optionfeature' + file_sep + 'optioncollection' + file_sep + sys_name + ".csv", index=False,
                     sep=',')
