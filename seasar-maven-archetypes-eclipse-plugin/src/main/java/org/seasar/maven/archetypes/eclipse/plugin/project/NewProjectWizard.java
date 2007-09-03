package org.seasar.maven.archetypes.eclipse.plugin.project;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Maven2��Archetype�𗘗p�����V�K�v���W�F�N�g�E�B�U�[�h
 */
public class NewProjectWizard extends Wizard implements INewWizard {
	private NewProjectWizardPage projectPage;

	public NewProjectWizard() {
		setWindowTitle("Maven2 Archetype Project");
	}

	/**
	 * �v���W�F�N�g���쐬
	 */
	public boolean performFinish() {
		final String projectName = projectPage.getProjectName();

		final List<String> mavenTask = new ArrayList<String>();

		// TODO:Windows�ȊO���l��
		mavenTask.add("mvn.bat");
		mavenTask.add("archetype:create");

		StringBuilder stringBuilder = new StringBuilder();
		for (String string : Activator.getDefault().getExtensionLoader()
				.getRemoteRepositories()) {
			stringBuilder.append(string + ",");
		}

		mavenTask.add("-DremoteRepositories=" + stringBuilder);
		mavenTask.add("-DarchetypeGroupId="
				+ projectPage.getArchetype().getGroupId());
		mavenTask.add("-DarchetypeArtifactId="
				+ projectPage.getArchetype().getArtifactId());
		if (projectPage.getArchetype().getVersion() != null) {
			mavenTask.add("-DarchetypeVersion="
					+ projectPage.getArchetype().getVersion());
		}

		mavenTask.add("-DgroupId=" + projectPage.getGroupID());
		mavenTask.add("-DartifactId=" + projectPage.getProjectName());

		if (projectPage.getVersion() != null) {
			mavenTask.add("-Dversion=" + projectPage.getVersion());
		}

		final List<String> eclipseTask = new ArrayList<String>();
		eclipseTask.add("mvn.bat");
		eclipseTask.add("eclipse:eclipse");

		final IPath directory;
		final boolean defaultLocation;
		if (projectPage.getLocationPath().equals(Platform.getLocation())) {
			directory = Path.fromOSString(projectPage.getLocationPath()
					.toOSString()
					+ "/" + projectName);
			defaultLocation = true;
		} else {
			directory = projectPage.getLocationPath();
			defaultLocation = false;
		}

		// TODO:���s���̕\�����킩��悤�ɂ���
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {

				try {
					try {
						// mvn archetype:create�����s
						ProcessBuilder processBuilder = new ProcessBuilder(
								mavenTask.toArray(new String[0]));
						processBuilder.directory(directory.toFile()
								.getParentFile());
						Process process = processBuilder.start();
						InputStream stream = process.getInputStream();
						while (true) {
							int c = stream.read();
							if (c == -1) {
								stream.close();
								break;
							}
							// TODO:�R���\�[���o�͂ɕύX
							System.out.print((char) c);
						}

						// mvn eclipse:eclipse�����s
						processBuilder.command(eclipseTask
								.toArray(new String[0]));
						processBuilder.directory(directory.toFile());
						process = processBuilder.start();
						stream = process.getInputStream();
						while (true) {
							int c = stream.read();
							if (c == -1) {
								stream.close();
								break;
							}
							// TODO:�R���\�[���o�͂ɕύX
							System.out.print((char) c);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
							.getRoot();
					IProject project = root.getProject(projectName);

					if (defaultLocation) {
						project.create(monitor);
					} else {
						IProjectDescription desc = project.getWorkspace()
								.newProjectDescription(project.getName());
						desc.setLocation(directory);
						project.create(desc, monitor);
					}

					// Open up this newly-created project in Eclipse
					project.open(monitor);

					monitor.worked(1);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};

		try {

			getContainer().run(true, true, op);
		} catch (InterruptedException e) {

			return false;
		} catch (InvocationTargetException e) {

			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	public void addPages() {
		projectPage = new NewProjectWizardPage();
		addPage(projectPage);
		super.addPages();
	}

	public boolean canFinish() {
		return projectPage.isPageComplete();
	}
}