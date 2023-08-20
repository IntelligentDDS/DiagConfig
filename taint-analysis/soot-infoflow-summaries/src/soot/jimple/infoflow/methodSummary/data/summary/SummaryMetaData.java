package soot.jimple.infoflow.methodSummary.data.summary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Meta data for a collection of method summaries
 * 
 * @author Steven Arzt
 *
 */
public class SummaryMetaData {

	private final Set<String> exclusiveClasses = new HashSet<>();
	private final Set<String> exclusivePackages = new HashSet<>();
	private final Map<String, String> classToSuperclass = new HashMap<>();

	public SummaryMetaData() {
		//
	}

	/**
	 * Creates a new instance of the {@link SummaryMetaData} object as a copy of an
	 * existing instance
	 * 
	 * @param metaData The original {@link SummaryMetaData} instance to copy
	 */
	public SummaryMetaData(SummaryMetaData metaData) {
		if (metaData != null) {
			this.exclusiveClasses.addAll(metaData.exclusiveClasses);
			this.exclusivePackages.addAll(metaData.exclusivePackages);
		}
	}

	/**
	 * Gets all classes for which this summary model is exclusive, i.e., for which
	 * no flows shall be computed by FlowDroid, even if there is no summary
	 * 
	 * @return The classes for which this summary model is exclusive
	 */
	public Set<String> getExclusiveClasses() {
		return exclusiveClasses;
	}

	/**
	 * Gets all packages for which this summary model is exclusive, i.e., for which
	 * no flows shall be computed by FlowDroid, even if there is no summary
	 * 
	 * @return The packages for which this summary model is exclusive
	 */
	public Set<String> getExclusivePackages() {
		return exclusivePackages;
	}

	/**
	 * Merges the contents of the given meta data object into this object
	 * 
	 * @param original The original object to merge
	 */
	public void merge(SummaryMetaData original) {
		if (original != null) {
			this.exclusiveClasses.addAll(original.exclusiveClasses);
			this.exclusivePackages.addAll(original.exclusivePackages);
		}
	}

	/**
	 * Gets whether the summary model is exclusive for the given class
	 * 
	 * @param className The name of the class to check
	 * @return True if the summary model is exclusive for the given class, false
	 *         otherwise
	 */
	public boolean isClassExclusive(String className) {
		// Check for a direct match
		if (exclusiveClasses.contains(className))
			return true;

		// Check for package name matches
		String tempName = className;
		int idx;
		while (!tempName.isEmpty() && (idx = tempName.lastIndexOf(".")) >= 0) {
			tempName = tempName.substring(0, idx);
			if (exclusivePackages.contains(tempName))
				return true;
		}

		// We found no match
		return false;
	}

	/**
	 * Sets the superclass for the given class
	 * 
	 * @param name       The name of the class
	 * @param superclass The name of the superclass
	 */
	public void setSuperclass(String name, String superclass) {
		classToSuperclass.put(name, superclass);
	}

	/**
	 * Gets the name of the superclass of the given class
	 * 
	 * @param name The class name
	 * @return The name of the superclass of the given class
	 */
	public String getSuperclass(String name) {
		return classToSuperclass.get(name);
	}

	/**
	 * Merges this hierarchy data into the given summaries object
	 * 
	 * @param summaries The summaries into which to merge the hierarchy data
	 */
	public void mergeHierarchyData(ClassSummaries summaries) {
		for (String className : classToSuperclass.keySet()) {
			ClassMethodSummaries clazzSummaries = summaries.getOrCreateClassSummaries(className);
			if (!clazzSummaries.hasSuperclass())
				clazzSummaries.setSuperClass(classToSuperclass.get(className));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exclusiveClasses == null) ? 0 : exclusiveClasses.hashCode());
		result = prime * result + ((exclusivePackages == null) ? 0 : exclusivePackages.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SummaryMetaData other = (SummaryMetaData) obj;
		if (exclusiveClasses == null) {
			if (other.exclusiveClasses != null)
				return false;
		} else if (!exclusiveClasses.equals(other.exclusiveClasses))
			return false;
		if (exclusivePackages == null) {
			if (other.exclusivePackages != null)
				return false;
		} else if (!exclusivePackages.equals(other.exclusivePackages))
			return false;
		return true;
	}

}
