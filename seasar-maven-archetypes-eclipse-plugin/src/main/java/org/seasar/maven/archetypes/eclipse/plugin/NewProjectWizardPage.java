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

	private Text rootPkgName;

	private Combo projectType;

	private Map<String, String> selectedProjectTypes = new TreeMap<String, String>();

	private Button useDefaultJre;

	private Button selectJre;

	private Combo enableJres;

	/**
	 * @param pageName
	 */
	@SuppressWarnings("unchecked")
	public NewProjectWizardPage() {
		super("SeasarMavenArchetypesProjectWizard");
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite composite = (Composite) getControl();
		createRootPackage(composite);
		createProjectType(composite);
	}

	private void createRootPackage(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(composite, SWT.NONE);
		label.setText("グループID");
		label.setFont(parent.getFont());

		this.rootPkgName = new Text(composite, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 250;
		this.rootPkgName.setLayoutData(data);
		this.rootPkgName.setFont(parent.getFont());
		this.rootPkgName.addListener(SWT.Modify, new Listener() {
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
		return super.validatePage() ? !validateRootPackageName().trim().equals(
				"") : false;
	}

	protected String validateRootPackageName() {
		String name = getRootPackageName();
		if (name.trim().equals("")) {
			return "パッケージ名が空です";
		}
		IStatus val = JavaConventions.validatePackageName(name);
		if (val.getSeverity() == IStatus.ERROR
				|| val.getSeverity() == IStatus.WARNING) {
			return "パッケージ名がいまいち";
		}
		return null;
	}

	public String getRootPackageName() {
		if (rootPkgName == null) {
			return "";
		}
		return rootPkgName.getText();
	}

	public String getRootPackagePath() {
		return getRootPackageName().replace('.', '/');
	}

	public String getProjectTypeKey() {
		return (String) selectedProjectTypes.get(this.projectType.getText());
	}
}