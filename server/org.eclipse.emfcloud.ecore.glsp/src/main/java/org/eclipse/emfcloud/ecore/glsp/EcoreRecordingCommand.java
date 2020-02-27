/*******************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *  
 *   This program and the accompanying materials are made available under the
 *   terms of the Eclipse Public License v. 2.0 which is available at
 *   http://www.eclipse.org/legal/epl-2.0.
 *  
 *   This Source Code may also be made available under the following Secondary
 *   Licenses when the conditions for such availability set forth in the Eclipse
 *   Public License v. 2.0 are satisfied: GNU General Public License, version 2
 *   with the GNU Classpath Exception which is available at
 *   https://www.gnu.org/software/classpath/license.html.
 *  
 *   SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ******************************************************************************/
package org.eclipse.emfcloud.ecore.glsp;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.ecore.change.ChangeDescription;
import org.eclipse.emf.ecore.change.util.ChangeRecorder;

public class EcoreRecordingCommand extends AbstractCommand {

	private Runnable runnable;
	private ChangeDescription change;
	private EcoreEditorContext context;

	public EcoreRecordingCommand(EcoreEditorContext context, String label, Runnable runnable) {
		super(label);
		this.context = context;
		this.runnable = runnable;
	}

	@Override
	protected boolean prepare() {
		return change == null;
	}

	@Override
	public void execute() {
		ChangeRecorder recorder = new ChangeRecorder(context.getResourceManager().getEditingDomain().getResourceSet());
		try {
			runnable.run();
		} finally {
			change = recorder.endRecording();
			recorder.dispose();
			runnable = null;
		}
	}

	@Override
	public boolean canUndo() {
		return change != null;
	}

	@Override
	public void undo() {
		applyChanges();
	}

	@Override
	public void redo() {
		applyChanges();
	}

	private void applyChanges() {
		EcoreModelIndex index = context.getModelState().getIndex();
		change.getObjectsToDetach().forEach(index::remove);
		change.getObjectsToAttach().forEach(index::add);
		change.applyAndReverse();
	}

}