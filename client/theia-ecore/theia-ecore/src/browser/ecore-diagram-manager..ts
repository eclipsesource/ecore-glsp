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
import { OpenerOptions } from "@theia/core/lib/browser";
import URI from "@theia/core/lib/common/uri";
import { EditorManager, EditorPreferences } from "@theia/editor/lib/browser";
import { GLSPClientContribution, GLSPDiagramManager, GLSPTheiaSprottyConnector, GLSPDiagramWidget } from "glsp-theia-extension/lib/browser";
import { inject, injectable } from "inversify";
import { DiagramWidgetRegistry, TheiaFileSaver, DiagramWidgetFactory, DiagramWidgetOptions } from "theia-glsp/lib";
import { EcoreLanguage } from "../common/ecore-language";
import { EcoreGLClientContribution } from "./ecore-glclient-contribution";
import { RequestModelAction } from "glsp-sprotty/lib";



@injectable()
export class EcoreDiagramManager extends GLSPDiagramManager {
    readonly diagramType = EcoreLanguage.DiagramType;
    readonly iconClass = "fa fa-project-diagram";
    readonly label = EcoreLanguage.Label + " Editor";

    private _diagramConnector: GLSPTheiaSprottyConnector;

    constructor(
        @inject(EcoreGLClientContribution)
        readonly languageClientContribution: GLSPClientContribution,
        @inject(TheiaFileSaver)
        readonly theiaFileSaver: TheiaFileSaver,
        @inject(EditorManager)
        readonly editorManager: EditorManager,
        @inject(DiagramWidgetRegistry)
        readonly diagramWidgetRegistry: DiagramWidgetRegistry) {
        super();

    }

    canHandle(uri: URI, options?: OpenerOptions | undefined): number {	
        if (uri.path.ext.endsWith("ecorediagram"))	
            return 1001	
        return 10	
    }

    get diagramConnector() {
        if (!this._diagramConnector) {
            this._diagramConnector = new GLSPTheiaSprottyConnector(
                this.languageClientContribution,
                this.theiaFileSaver,
                this.editorManager,
                this.diagramWidgetRegistry)

        }
        return this._diagramConnector
    }

    protected get diagramWidgetFactory(): DiagramWidgetFactory {
        return options => new EcoreGLSPDiagramWidget(options, this.editorPreferences);
    }
}

class EcoreGLSPDiagramWidget extends GLSPDiagramWidget{
    constructor(options: DiagramWidgetOptions, readonly editorPreferences: EditorPreferences){
        super(options,editorPreferences)
    }

    protected sendInitialRequestMessages() {
        // this.actionDispatcher.dispatch(new RequestTypeHintsAction());
        this.actionDispatcher.dispatch(new RequestModelAction({
            sourceUri: decodeURIComponent(this.uri.toString()),
            diagramType: this.diagramType,
            needsClientLayout: 'true',
            needsServerLayout: 'true'
        }))
        // this.actionDispatcher.dispatch(new RequestOperationsAction());
    }
}

