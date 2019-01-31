/*******************************************************************************
 * Copyright (c) 2018 EclipseSource Muenchen GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 * 	EclipseSource Muenchen GmbH - initial API and implementation
 ******************************************************************************/
import { BaseGLSPClientContribution } from "glsp-theia-extension/lib/browser";
import { injectable } from "inversify";
import { EcoreLanguage } from "../common/ecore-language";
@injectable()
export class EcoreGLClientContribution extends BaseGLSPClientContribution {
    fileExtensions= [EcoreLanguage.FileExtension]
    readonly id = EcoreLanguage.Id
    readonly name = EcoreLanguage.Name
}