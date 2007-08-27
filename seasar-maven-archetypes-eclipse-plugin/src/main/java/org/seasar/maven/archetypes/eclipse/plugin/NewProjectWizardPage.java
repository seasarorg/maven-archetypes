package org.seasar.maven.archetypes.eclipse.plugin;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class NewProjectWizardPage extends WizardNewProjectCreationPage {

	private Text groupId;
	private Text rootPackage;
	private Text version;

	private Combo projectType;

	private Map<String, Archetype> selectedProjectTypes = new TreeMap<String, Archetype>();

	/**
	 * @param pageName
	 */
	@SuppressWarnings("unchecked")
	public NewProjectWizardPage() {
		super("SeasarMavenArchetypesProjectWizard");
		selectedProjectTypes = Activator.getDefault().getExtensionLoader()
				.getArchetypes();
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite composite = (Composite) getControl();
		createGroupId(composite);
		createProjectType(composite);
	}

	private void createGroupId(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(composite, SWT.NONE);
		label.setText("グループID");
		label.setFont(parent.getFont());

		this.groupId = new Text(composite, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 250;
		this.groupId.setLayoutData(data);
		this.groupId.setFont(parent.getFont());

		label = new Label(composite, SWT.NONE);
		label.setText("バージョン");
		label.setFont(parent.getFont());

		this.version = new Text(composite, SWT.BORDER);
		this.version.setLayoutData(data);
		this.version.setFont(parent.getFont());

		label = new Label(composite, SWT.NONE);
		label.setText("ルートパッケージ");
		label.setFont(parent.getFont());

		this.rootPackage = new Text(composite, SWT.BORDER);
		this.rootPackage.setLayoutData(data);
		this.rootPackage.setFont(parent.getFont());
		this.rootPackage.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				boolean is = validatePage();
				if (is == false) {
					setErrorMessage(validateRootPackageName());
				}
				setPageComplete(is);
			}
		});
	}

	private void createProjectType(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(composite, SWT.NONE);
		label.setText("プロジェクトタイプ");
		label.setFont(parent.getFont());

		this.projectType = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		this.projectType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.projectType.setItems(getProjectTypes());
		this.projectType.select(0);
		this.projectType.pack();
	}

	private String[] getProjectTypes() {
		return selectedProjectTypes.keySet().toArray(new String[0]);
	}

	protected boolean validatePage() {
		return super.validatePage() ? validateRootPackageName().trim().equals(
				"") : false;
	}

	protected String validateRootPackageName() {
		String name = getRootPackage();
		if (name.trim().equals("")) {
			return "パッケージ名が空です";
		}
		IStatus val = JavaConventions.validatePackageName(name);
		if (val.getSeverity() == IStatus.ERROR
				|| val.getSeverity() == IStatus.WARNING) {
			return "グループIDが不正です";
		}
		return "";
	}

	public String getGroupID() {
		if (groupId == null) {
			return "";
		}
		return groupId.getText();
	}

	public String getRootPackage() {
		if (rootPackage == null) {
			return "";
		}
		return rootPackage.getText();
	}

	public Archetype getProjectType() {
		return selectedProjectTypes.get(this.projectType.getText());
	}

	public String getVersion() {
		if (version == null) {
			return "";
		}
		return version.getText();
	}

	public static class Archetype {
		private String name;
		private String groupId;
		private String artifactId;
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
}