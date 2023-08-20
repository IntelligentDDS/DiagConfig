class SubjectSysInfo:
    base_path = '../taint-analysis/subjectSys/output'
    subject_sys_name = ['h2', 'sunflow', 'catena', 'prevayler', 'batik', 'kanzi', 'dconverter', 'cassandra']
    train_sys_name = ['h2', 'sunflow', 'catena', 'prevayler', 'batik']
    test_sys_name = [ 'kanzi', 'dconverter']
    ground_truth = {}


for sub_sys in SubjectSysInfo.subject_sys_name:
    file = open('./optionfeature/groundTruth/' + sub_sys + '.txt')
    op_list = [line.strip('\n') for line in file]
    # print(op_list)
    file.close()
    SubjectSysInfo.ground_truth[sub_sys] = op_list
