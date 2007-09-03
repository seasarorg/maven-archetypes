package org.seasar.maven.archetypes.eclipse.plugin.project;

/**
 * Archetype定義
 */
public class Archetype {
	/** 表示名 */
	private String name;
	/** グループID */
	private String groupId;
	/** アーティファクトID */
	private String artifactId;
	/** バージョン */
	private String version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}