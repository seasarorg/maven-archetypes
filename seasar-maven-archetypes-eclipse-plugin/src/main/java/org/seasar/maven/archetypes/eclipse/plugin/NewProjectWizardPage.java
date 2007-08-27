package org.seasar.maven.archetypes.eclipse.plugin;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

	private Combo archetypes;

	private Map<String, Archetype> archetypeMap = new TreeMap<String, Archetype>();

	@SuppressWarnings("unchecked")
	public NewProjectWizardPage() {
		super("SeasarMavenArchetypesProjectWizard");
		archetypeMap = Activator.getDefault().getExtensionLoader()
				.getArchetypes();
	}

	/**
	 * ページの構築
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite composite = (Composite) getControl();
		createProjectProperty(composite);
		createArchetype(composite);
	}

	/**
	 * プロジェクトの設定入力部分の構築
	 * 
	 * @param parent
	 */
	private void createProjectProperty(Composite parent) {
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

		label = new Label(composite, SWT.NONE);
		label.setText("バージョン");
		label.setFont(parent.getFont());

		this.version = new Text(composite, SWT.BORDER);
		this.version.setLayoutData(data);
		this.version.setFont(parent.getFont());
	}

	/**
	 * Archetypeの選択部分を構築
	 * 
	 * @param parent
	 */
	private void createArchetype(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Archetype");
		label.setFont(parent.getFont());

		this.archetypes = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		this.archetypes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.archetypes.setItems(archetypeMap.keySet().toArray(new String[0]));
		this.archetypes.select(0);
		this.archetypes.pack();
	}

	/**
	 * 入力チェック
	 */
	protected boolean validatePage() {
		return super.validatePage() ? validateRootPackageName().trim().equals(
				"") : false;
	}

	/**
	 * パッケージ名の入力チェック
	 * 
	 * @return
	 */
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

	/**
	 * グループIDを取得
	 * 
	 * @return グループID
	 */
	public String getGroupID() {
		if (groupId == null) {
			return "";
		}
		return groupId.getText();
	}

	/**
	 * ルートパッケージを取得
	 * 
	 * @return ルートパッケージ
	 */
	public String getRootPackage() {
		if (rootPackage == null) {
			return "";
		}
		return rootPackage.getText();
	}

	/**
	 * Archetypeを取得
	 * 
	 * @return Archetype
	 */
	public Archetype getArchetype() {
		return archetypeMap.get(this.archetypes.getText());
	}

	/**
	 * バージョンを取得
	 * 
	 * @return バージョン
	 */
	public String getVersion() {
		if (version == null) {
			return "";
		}
		return version.getText();
	}
}