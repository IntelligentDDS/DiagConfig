class ConfigOption:
    def __init__(self, name):
        self.name = name
        self.controlTypeCounting = {'directlyNewArrSink': 0, 'ifNewArrSink': 0, 'loopNewArrSink': 0,
                                    'directlyIoSink': 0, 'ifIoSink': 0, 'loopIoSink': 0,
                                    'directlyLockSink': 0, 'ifLockSink': 0, 'loopLockSink': 0,
                                    'directlyThreadSink': 0, 'ifThreadSink': 0, 'loopThreadSink': 0
                                    }
        self.originalStmtCollection = {}

    def countFeature(self, str):
        if str in self.originalStmtCollection:
            self.originalStmtCollection[str] += 1
        else:
            self.originalStmtCollection[str] = 1

    def addFeatue(self, str):
        self.originalStmtCollection[str] = 0

    def countType(self, str):
        self.controlTypeCounting[str] += 1

    def getOptionName(self):
        return self.name
