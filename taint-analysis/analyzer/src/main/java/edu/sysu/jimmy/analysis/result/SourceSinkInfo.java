package edu.sysu.jimmy.analysis.result;

import java.util.Set;

public class SourceSinkInfo {
    private String packageName;
    private String className;
    private String methodSignature;
    private int javaLine;
    private int bytecodeIndex;
    private String operationType;
    private Set<String> options;
    private int optionCount;
    private String originalStmt;
    private Set<String> mayValues;

    public SourceSinkInfo(String packageName, String className, String methodSignature, int javaLine, int bytecodeIndex, String operationType, Set<String> options) {
        this.packageName = packageName;
        this.className = className;
        this.methodSignature = methodSignature;
        this.javaLine = javaLine;
        this.bytecodeIndex = bytecodeIndex;
        this.operationType = operationType;
        this.options = options;
        this.optionCount = this.options.size();
    }

    public SourceSinkInfo(String packageName, String className, String methodSignature, int javaLine, int bytecodeIndex, String operationType, Set<String> options, String originalStmt) {
        this.packageName = packageName;
        this.className = className;
        this.methodSignature = methodSignature;
        this.javaLine = javaLine;
        this.bytecodeIndex = bytecodeIndex;
        this.operationType = operationType;
        this.options = options;
        this.originalStmt = originalStmt;
        this.optionCount = this.options.size();

    }
    public SourceSinkInfo(String packageName, String className, String methodSignature, int javaLine, int bytecodeIndex, String operationType, Set<String> options, String originalStmt,Set<String>mayValues) {
        this.packageName = packageName;
        this.className = className;
        this.methodSignature = methodSignature;
        this.javaLine = javaLine;
        this.bytecodeIndex = bytecodeIndex;
        this.operationType = operationType;
        this.options = options;
        this.originalStmt = originalStmt;
        this.mayValues = mayValues;
        this.optionCount = this.options.size();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceSinkInfo that = (SourceSinkInfo) o;

        if (javaLine != that.javaLine) return false;
        if (bytecodeIndex != that.bytecodeIndex) return false;
        if (optionCount != that.optionCount) return false;
        if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (methodSignature != null ? !methodSignature.equals(that.methodSignature) : that.methodSignature != null)
            return false;
        if (operationType != null ? !operationType.equals(that.operationType) : that.operationType != null)
            return false;
        if (options != null ? !options.equals(that.options) : that.options != null) return false;
        if (originalStmt != null ? !originalStmt.equals(that.originalStmt) : that.originalStmt != null) return false;
        return mayValues != null ? mayValues.equals(that.mayValues) : that.mayValues == null;
    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (methodSignature != null ? methodSignature.hashCode() : 0);
        result = 31 * result + javaLine;
        result = 31 * result + bytecodeIndex;
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + optionCount;
        result = 31 * result + (originalStmt != null ? originalStmt.hashCode() : 0);
        result = 31 * result + (mayValues != null ? mayValues.hashCode() : 0);
        return result;
    }
    /*    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceSinkInfo that = (SourceSinkInfo) o;

        if (javaLine != that.javaLine) return false;
        if (bytecodeIndex != that.bytecodeIndex) return false;
        if (optionCount != that.optionCount) return false;
        if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (methodSignature != null ? !methodSignature.equals(that.methodSignature) : that.methodSignature != null)
            return false;
        if (operationType != null ? !operationType.equals(that.operationType) : that.operationType != null)
            return false;
        if (options != null ? !options.equals(that.options) : that.options != null) return false;
        return originalStmt != null ? originalStmt.equals(that.originalStmt) : that.originalStmt == null;
    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (methodSignature != null ? methodSignature.hashCode() : 0);
        result = 31 * result + javaLine;
        result = 31 * result + bytecodeIndex;
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + optionCount;
        result = 31 * result + (originalStmt != null ? originalStmt.hashCode() : 0);
        return result;
    }*/
/*    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceSinkInfo that = (SourceSinkInfo) o;

        if (javaLine != that.javaLine) return false;
        if (bytecodeIndex != that.bytecodeIndex) return false;
        if (optionCount != that.optionCount) return false;
        if (!packageName.equals(that.packageName)) return false;
        if (!className.equals(that.className)) return false;
        if (!methodSignature.equals(that.methodSignature)) return false;
        if (!operationType.equals(that.operationType)) return false;
        return options.equals(that.options);
    }

    @Override
    public int hashCode() {
        int result = packageName.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + methodSignature.hashCode();
        result = 31 * result + javaLine;
        result = 31 * result + bytecodeIndex;
        result = 31 * result + operationType.hashCode();
        result = 31 * result + options.hashCode();
        result = 31 * result + optionCount;
        return result;
    }*/

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public int getJavaLine() {
        return javaLine;
    }

    public void setJavaLine(int javaLine) {
        this.javaLine = javaLine;
    }

    public int getBytecodeIndex() {
        return bytecodeIndex;
    }

    public void setBytecodeIndex(int bytecodeIndex) {
        this.bytecodeIndex = bytecodeIndex;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Set<String> getOptions() {
        return options;
    }

    public void setOptions(Set<String> options) {
        this.options = options;
    }

    public int getOptionCount() {
        return optionCount;
    }

    public void setOptionCount(int optionCount) {
        this.optionCount = optionCount;
    }

    public String getOriginalStmt() {
        return originalStmt;
    }

    public void setOriginalStmt(String originalStmt) {
        this.originalStmt = originalStmt;
    }

    public Set<String> getMayValues() {
        return mayValues;
    }

    public void setMayValues(Set<String> mayValues) {
        this.mayValues = mayValues;
    }

    public SourceSinkInfo(String packageName, String className, String methodSignature, int javaLine, int bytecodeIndex, String operationType, Set<String> options, int optionCount, String originalStmt, Set<String> mayValues) {
        this.packageName = packageName;
        this.className = className;
        this.methodSignature = methodSignature;
        this.javaLine = javaLine;
        this.bytecodeIndex = bytecodeIndex;
        this.operationType = operationType;
        this.options = options;
        this.optionCount = optionCount;
        this.originalStmt = originalStmt;
        this.mayValues = mayValues;
    }

    public SourceSinkInfo() {
    }
}
