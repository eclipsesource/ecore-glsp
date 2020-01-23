/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package com.eclipsesource.glsp.ecore;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.glsp.api.jsonrpc.GLSPServerException;
import org.eclipse.glsp.api.utils.ClientOptions;
import com.eclipsesource.glsp.ecore.enotation.EnotationPackage;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;

public class ResourceManager {
	public static final String ECORE_EXTENSION = ".ecore";
	public static final String NOTATION_EXTENSION = ".enotation";

	private static Logger LOG = Logger.getLogger(ResourceManager.class);

	private ResourceSet resourceSet;
	private String baseSourceUri;
	private EcoreFacade ecoreFacade;
	private EditingDomain editingDomain;

	public ResourceManager(EcoreModelState modelState) {
		String sourceURI = ClientOptions.getValue(modelState.getClientOptions(), ClientOptions.SOURCE_URI)
				.orElseThrow(() -> new GLSPServerException("No source uri given to load model!"));
		if (!sourceURI.endsWith(ECORE_EXTENSION) && !sourceURI.endsWith(NOTATION_EXTENSION)) {
			throw new GLSPServerException("Could not setup ResourceManager: \n Invalid file extension: " + sourceURI);
		}

		this.baseSourceUri = sourceURI.substring(0, sourceURI.lastIndexOf('.'));
		this.resourceSet = setupResourceSet();
		createEcoreFacade(modelState.getIndex());
	}

	protected ResourceSet setupResourceSet() {
		editingDomain = new AdapterFactoryEditingDomain(new EcoreAdapterFactory(), new BasicCommandStack());
		ResourceSet resourceSet = editingDomain.getResourceSet();
		resourceSet.getPackageRegistry().put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(EnotationPackage.eINSTANCE.getNsURI(), EnotationPackage.eINSTANCE);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		return resourceSet;
	}

	public EditingDomain getEditingDomain() {
		return this.editingDomain;
	}

	public EcoreFacade getEcoreFacade() {
		return ecoreFacade;
	}

	protected EcoreFacade createEcoreFacade(EcoreModelIndex modelIndex) {
		try {
			Resource semanticResource = loadResource(convertToFile(getSemanticURI()));
			Resource notationResource = loadResource(convertToFile(getNotationURI()));
			ecoreFacade = new EcoreFacade(semanticResource, notationResource, modelIndex);
			return ecoreFacade;
		} catch (IOException e) {
			LOG.error(e);
			throw new GLSPServerException("Error during mode loading", e);
		}
	}

	public String getSemanticURI() {
		return baseSourceUri + ECORE_EXTENSION;
	}

	public String getNotationURI() {
		return baseSourceUri + NOTATION_EXTENSION;
	}

	private File convertToFile(String sourceURI) {
		if (sourceURI != null) {
			return new File(sourceURI);
		}
		return null;
	}

	private Resource loadResource(File file) throws IOException {
		Resource resource = createResource(file.getAbsolutePath());
		if (file.exists()) {
			resource.load(Collections.EMPTY_MAP);
		}
		return resource;
	}

	private Resource createResource(String path) {
		return resourceSet.createResource(URI.createFileURI(path));
	}

	public void save() {
		try {
			ecoreFacade.getSemanticResource().save(Collections.EMPTY_MAP);
			ecoreFacade.getNotationResource().save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			throw new GLSPServerException("Could not save notation resource", e);
		}
	}

}
