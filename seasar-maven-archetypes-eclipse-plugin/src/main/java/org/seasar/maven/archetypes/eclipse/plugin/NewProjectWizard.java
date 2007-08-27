package org.seasar.maven.archetypes.eclipse.plugin;

import java.io.File;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewProjectWizard extends Wizard implements INewWizard {
	private NewProjectWizardPage projectPage;
	private Shell shell;

	public NewProjectWizard() {
		setWindowTitle("New Project");
	}

	public boolean performFinish() {
		final String projectName = projectPage.getProjectName();

		// Create the Task strings
		final List<String> mavenTask = new ArrayList<String>();
		mavenTask.add("mvn.bat");
		mavenTask.add("archetype:create");
		mavenTask
				.add("-DremoteRepositories=http://maven.seasar.org/maven2/,https://www.seasar.org/maven/maven2-snapshot");
		mavenTask.add("-DarchetypeGroupId="
				+ projectPage.getProjectType().getGroupId());
		mavenTask.add("-DarchetypeArtifactId="
				+ projectPage.getProjectType().getArtifactId());
		if (projectPage.getProjectType().getVersion() != null) {
			mavenTask.add("-DarchetypeVersion="
					+ projectPage.getProjectType().getVersion());
		}

		mavenTask.add("-DgroupId=" + projectPage.getGroupID());
		mavenTask.add("-Dpackage=" + projectPage.getRootPackage());
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

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {

				try {
					try {
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
							System.out.print((char) c);
						}

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
							System.out.print((char) c);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					// Retreive the workspace root
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
							.getRoot();

					// Retreive the project handle
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
		shell = workbench.getActiveWorkbenchWindow().getShell();
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