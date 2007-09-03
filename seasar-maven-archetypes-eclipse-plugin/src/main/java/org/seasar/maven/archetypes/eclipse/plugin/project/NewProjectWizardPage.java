package org.seasar.maven.archetypes.eclipse.plugin.project;

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

/**
 * Maven2��Archetype�𗘗p�����V�K�v���W�F�N�g�E�B�U�[�h�̐ݒ�y�[�W
 */
public class NewProjectWizardPage extends WizardNewProjectCreationPage {

	private Text groupId;
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
	 * �y�[�W�̍\�z
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		Composite composite = (Composite) getControl();
		createProjectProperty(composite);
		createArchetype(composite);
	}

	/**
	 * �v���W�F�N�g�̐ݒ���͕����̍\�z
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
		label.setText("�O���[�vID");
		label.setFont(parent.getFont());

		this.groupId = new Text(composite, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 250;
		this.groupId.setLayoutData(data);
		this.groupId.setFont(parent.getFont());

		this.groupId.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				boolean is = validatePage();
				setPageComplete(is);
			}
		});

		label = new Label(composite, SWT.NONE);
		label.setText("�o�[�W����");
		label.setFont(parent.getFont());

		this.version = new Text(composite, SWT.BORDER);
		this.version.setLayoutData(data);
		this.version.setFont(parent.getFont());
	}

	/**
	 * Archetype�̑I�𕔕����\�z
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
	 * ���̓`�F�b�N
	 */
	protected boolean validatePage() {
		return super.validatePage() && validateGroupId();
	}

	/**
	 * �v���W�F�N�g���̓��̓`�F�b�N
	 * 
	 * @return
	 */
	protected boolean validateProjectName() {
		String name = getProjectName();
		if (name.trim().equals("")) {
			setErrorMessage("�v���W�F�N�g������ł�");
			return false;
		}
		return true;
	}

	/**
	 * �p�b�P�[�W���̓��̓`�F�b�N
	 * 
	 * @return
	 */
	protected boolean validateGroupId() {
		String name = getGroupID();
		if (name.trim().equals("")) {
			setErrorMessage("�O���[�vID����ł�");
			return false;
		}
		IStatus val = JavaConventions.validatePackageName(name);
		if (val.getSeverity() == IStatus.ERROR
				|| val.getSeverity() == IStatus.WARNING) {
			setErrorMessage("�O���[�vID���s���ł�");
			return false;
		}
		return true;
	}

	/**
	 * �O���[�vID���擾
	 * 
	 * @return �O���[�vID
	 */
	public String getGroupID() {
		if (groupId == null) {
			return "";
		}
		return groupId.getText();
	}

	/**
	 * Archetype���擾
	 * 
	 * @return Archetype
	 */
	public Archetype getArchetype() {
		return archetypeMap.get(this.archetypes.getText());
	}

	/**
	 * �o�[�W�������擾
	 * 
	 * @return �o�[�W����
	 */
	public String getVersion() {
		if (version == null) {
			return "";
		}
		return version.getText();
	}
}