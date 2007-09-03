package org.seasar.maven.archetypes.eclipse.plugin.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * ägí£Çì«Ç›çûÇﬁÅB
 */
public class ExtensionLoader {
	public static final String ARCHETYPE_EXTENSION_POINT_ID = Activator.PLUGIN_ID
			+ ".archetype";
	public static final String REMOTEREPOSITORY_EXTENSION_POINT_ID = Activator.PLUGIN_ID
			+ ".remoteRepository";
	private Map<String, Archetype> archetypes = new TreeMap<String, Archetype>();
	private List<String> remoteRepositories = new ArrayList<String>();

	public void loadExtension() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		loadArchetypeExtension(registry);
		loadRemoteRepositoryExtension(registry);
	}

	/**
	 * ArchetypeÇÃägí£Çì«Ç›çûÇﬁÅB
	 */
	private void loadArchetypeExtension(IExtensionRegistry registry) {
		IExtensionPoint point = registry
				.getExtensionPoint(ARCHETYPE_EXTENSION_POINT_ID);
		if (point == null) {
			return;
		}

		IExtension[] extensions = point.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension
					.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				if ("archetype".equals(configurationElement.getName())) {
					String artifactId = configurationElement
							.getAttribute("artifactId");
					String groupId = configurationElement
							.getAttribute("groupId");
					String version = configurationElement
							.getAttribute("version");
					String name = configurationElement.getAttribute("name");
					Archetype archetype = new Archetype();
					archetype.setArtifactId(artifactId);
					archetype.setGroupId(groupId);
					archetype.setVersion(version);
					archetype.setName(name);

					archetypes.put(archetype.getName(), archetype);
				}
			}
		}
	}

	/**
	 * RemoteRepositoryÇÃägí£Çì«Ç›çûÇﬁ
	 */
	private void loadRemoteRepositoryExtension(IExtensionRegistry registry) {
		IExtensionPoint point = registry
				.getExtensionPoint(REMOTEREPOSITORY_EXTENSION_POINT_ID);
		if (point == null) {
			return;
		}

		IExtension[] extensions = point.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension
					.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				if ("remoteRepository".equals(configurationElement.getName())) {
					String uri = configurationElement.getAttribute("uri");
					remoteRepositories.add(uri);
				}
			}
		}
	}

	public Map<String, Archetype> getArchetypes() {
		return archetypes;
	}

	public List<String> getRemoteRepositories() {
		return remoteRepositories;
	}

	public void setRemoteRepositories(List<String> remoteRepositories) {
		this.remoteRepositories = remoteRepositories;
	}

}
