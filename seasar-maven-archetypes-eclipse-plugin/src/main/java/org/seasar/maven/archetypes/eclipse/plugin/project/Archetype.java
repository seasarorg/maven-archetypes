package org.seasar.maven.archetypes.eclipse.plugin.project;

/**
 * Archetype��`
 */
public class Archetype {
	/** �\���� */
	private String name;
	/** �O���[�vID */
	private String groupId;
	/** �A�[�e�B�t�@�N�gID */
	private String artifactId;
	/** �o�[�W���� */
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