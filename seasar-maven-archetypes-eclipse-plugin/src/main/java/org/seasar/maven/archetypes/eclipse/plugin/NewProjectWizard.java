package org.seasar.maven.archetypes.eclipse.plugin;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
		final String application = "C:/tools/develop/maven-2.0.5/bin/mvn.bat";
		final String projectName = projectPage.getProjectName();

		// Create the Task strings
		final StringBuffer mavenTask = new StringBuffer();
		mavenTask.append("archetype:create");

		mavenTask.append(" -D").append("remoteRepositories").append("=");
		mavenTask
				.append("http://maven.seasar.org/maven2/,https://www.seasar.org/maven/maven2-snapshot");

		mavenTask.append(" -D").append("archetypeGroupId").append("=");
		mavenTask.append("org.seasar.maven-archetypes");

		mavenTask.append(" -D").append("archetypeArtifactId").append("=");
		mavenTask.append("maven-archetype-seasar-struts-mayaa");

		mavenTask.append(" -D").append("archetypeVersion").append("=");
		mavenTask.append("0.0.1");

		mavenTask.append(" -D").append("groupId").append("=").append(
				projectPage.getRootPackageName());

		mavenTask.append(" -D").append("artifactId").append("=");
		mavenTask.append(projectPage.getProjectName());

		mavenTask.append(" -D").append("version").append("=").append("1.0.0");

		final StringBuffer eclipseTask = new StringBuffer();
		eclipseTask.append("eclipse:eclipse");

		final String directory = projectPage.getLocationPath().toOSString();

		IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {

				try {

					doFinish(application, mavenTask.toString(), eclipseTask
							.toString(), directory, projectName, monitor);
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

	private void doFinish(String application, String mavenTask,
			String eclipseTask, String directory, String projectName,
			IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Creating Maven 2/Eclipse Project", 3);

		// Launch the External Tools Configuration for creating the Maven 2
		// Project
		monitor.setTaskName("Creating Maven 2 Project");

		ExternalToolsHelper.launch(null, "", application, directory, mavenTask,
				false, false, "${workspace}", false, null);

		monitor.worked(1);

		monitor.setTaskName("Creating Eclipse Project Setting");

		ExternalToolsHelper.launch(null, "", application, directory + "/"
				+ projectName, eclipseTask, false, false, "${workspace}", false,
				null);

		monitor.worked(1);

		// Import the Eclipse Project into the Workspace
		monitor.setTaskName("Importing the Eclipse Project into the Workspace");

		// Retreive the workspace root
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// Retreive the project handle
		IProject project = root.getProject(projectName);

		// Create project resources in Eclipse
		project.create(null);

		// Open up this newly-created project in Eclipse
		project.open(null);

		monitor.worked(1);
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