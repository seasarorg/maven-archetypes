package org.seasar.maven.archetypes.eclipse.plugin;

import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.IStatus.OK;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.eclipse.debug.ui.IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE;
import static org.eclipse.debug.ui.IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND;
import static org.eclipse.debug.ui.RefreshTab.ATTR_REFRESH_RECURSIVE;
import static org.eclipse.debug.ui.RefreshTab.ATTR_REFRESH_SCOPE;
import static org.eclipse.ui.externaltools.internal.model.IExternalToolConstants.ATTR_LOCATION;
import static org.eclipse.ui.externaltools.internal.model.IExternalToolConstants.ATTR_TOOL_ARGUMENTS;
import static org.eclipse.ui.externaltools.internal.model.IExternalToolConstants.ATTR_WORKING_DIRECTORY;
import static org.eclipse.ui.externaltools.internal.model.IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ExternalToolsHelper {

	/**
	 * Launches an External Tools Configuration based on the passed details.
	 * <p>
	 * The configuration can be persisted to the External Tools list if
	 * required.
	 * 
	 * @param selection
	 *            the current selection, or <code>null</code> if there is no
	 *            selection.
	 * @param name
	 *            the name of the External Tools Configuration (used only if
	 *            saving)
	 * @param location
	 *            the location of the External Tool
	 * @param workingDirectory
	 *            the working directory for the External Tools Configuration
	 * @param arguments
	 *            the arguments needed for the External Tool. Enclose an
	 *            argument containing spaces in double-quotes("). Use the space
	 *            character ( ) to seperate the arguments
	 * @param background
	 *            launch the External Tools Configuration in the background?
	 * @param outputToConsole
	 *            output the results to the console?
	 * @param refreshScope
	 *            the scope of the refresh (after launching the External Tools
	 *            Configuration)
	 * @param save
	 *            save the External Tools Configuration?
	 * @param monitor
	 *            progress monitor, or <code>null</code>. Since 3.0, this
	 *            parameter is ignored. A cancellable progress monitor is
	 *            provided by the Job framework. (Not entirely sure this is true -
	 *            CMB)
	 */
	@SuppressWarnings("restriction")
	public static void launch(ISelection selection, String name,
			String location, String workingDirectory, String arguments,
			boolean background, boolean outputToConsole, String refreshScope,
			boolean save, IProgressMonitor monitor) {

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE);

		// Create a working copy External Tools Configuration, of the "Program"
		// type
		ILaunchConfigurationWorkingCopy workingCopy = null;

		try {

			workingCopy = type.newInstance(null, name);
		} catch (CoreException exception) {

			IStatus status = new Status(ERROR, Activator.PLUGIN_ID, OK,
					exception.getMessage(), exception);
			DebugPlugin.getDefault().getLog().log(status);
		}

		// Set the Refersh attributes of the External Tools Configuration
		if (refreshScope != null) {

			workingCopy.setAttribute(ATTR_REFRESH_RECURSIVE, true);
			workingCopy.setAttribute(ATTR_REFRESH_SCOPE, refreshScope);
		}

		// Set the Location attribute of the External Tools Configuration
		workingCopy.setAttribute(ATTR_LOCATION, location);

		// Set the Working Directory attribute of the External Tools
		// Configuration, if it is null,
		// then use the Project's root directory
		if (workingDirectory != null) {

			workingCopy.setAttribute(ATTR_WORKING_DIRECTORY, workingDirectory);
		} else {

			IStructuredSelection structured = (IStructuredSelection) selection;
			IFile file = (IFile) structured.getFirstElement();
			workingCopy.setAttribute(ATTR_WORKING_DIRECTORY, file.getLocation()
					.toOSString());
		}

		// Set the Arguments attribute of the External Tools Configuration
		if (arguments != null)
			workingCopy.setAttribute(ATTR_TOOL_ARGUMENTS, arguments);

		// Set the Launch in Background attribute of the External Tools
		// Configuration
		workingCopy.setAttribute(ATTR_LAUNCH_IN_BACKGROUND, background);

		// Set the Output To Console atribute of the External Tools
		// Configuration
		workingCopy.setAttribute(ATTR_CAPTURE_IN_CONSOLE, outputToConsole);
		workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_CONSOLE_OUTPUT_ON",
				outputToConsole);
		workingCopy.setAttribute("org.eclipse.debug.core.capture_output",
				outputToConsole);

		// Launch the External Tools Configuration, saving if required
		try {

			if (save) {

				ILaunchConfiguration config = workingCopy.doSave();
				config.launch(RUN_MODE, monitor);
			} else {

				workingCopy.launch(RUN_MODE, monitor);
			}
		} catch (CoreException exception) {

			IStatus status = new Status(ERROR, Activator.PLUGIN_ID, OK,
					exception.getMessage(), exception);
			DebugPlugin.getDefault().getLog().log(status);
		}
	}

}
