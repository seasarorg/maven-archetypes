package org.seasar.maven.archetypes.eclipse.plugin.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ProjectRefreshAction implements IObjectActionDelegate {
	private IWorkbenchPart targetPart;

	private IProject project;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object obj = ss.getFirstElement();
			if (obj instanceof IResource) {
				project = ((IResource) obj).getProject();
			}
		} else {
			project = null;
		}
	}

	public void run(IAction action) {
		String path = project.getRawLocation().toOSString();

		// mvn archetype:createを実行
		ProcessBuilder processBuilder = new ProcessBuilder("mvn.bat",
				"eclipse:eclipse");
		processBuilder.directory(new File(path));
		Process process;
		try {
			process = processBuilder.start();
			InputStream stream = process.getInputStream();
			while (true) {
				int c = stream.read();
				if (c == -1) {
					stream.close();
					break;
				}
				System.out.print((char) c);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
