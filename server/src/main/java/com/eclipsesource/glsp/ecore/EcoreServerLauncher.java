/*******************************************************************************
 * Copyright (c) 2018 EclipseSource Services GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 * 	Tobias Ortmayr - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.glsp.ecore;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.elk.alg.layered.options.LayeredMetaDataProvider;

import com.eclipsesource.glsp.layout.ElkLayoutEngine;
import com.eclipsesource.glsp.server.launch.DefaultGLSPServerLauncher;
import com.eclipsesource.glsp.server.launch.GLSPServerLauncher;

public class EcoreServerLauncher {

	public static void main(String[] args) {
		ElkLayoutEngine.initialize(new LayeredMetaDataProvider());
		BasicConfigurator.configure();
		GLSPServerLauncher launcher = new DefaultGLSPServerLauncher(new EcoreGLSPModule());

		launcher.start("localhost", 5007);

	}
}
