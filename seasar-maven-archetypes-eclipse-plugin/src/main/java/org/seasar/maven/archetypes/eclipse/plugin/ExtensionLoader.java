package org.seasar.maven.archetypes.eclipse.plugin;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * 拡張ポイントを読み込む。
 */
public class ExtensionLoader {
	public static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID
			+ ".archetype";

	private Map<String, Archetype> archetypes = new TreeMap<String, Archetype>();

	public void loadExtension() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT_ID);
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

	public Map<String, Archetype> getArchetypes() {
		return archetypes;
	}
}
