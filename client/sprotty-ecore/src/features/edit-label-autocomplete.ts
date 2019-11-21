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
import { RequestAction, ResponseAction, generateRequestId} from "sprotty/lib";
import { inject, injectable } from "inversify";
import { matchesKeystroke } from "sprotty/lib/utils/keyboard";
import { EditLabelUI } from "sprotty/lib";
import { GLSPActionDispatcher } from "@glsp/sprotty-client/lib";
import {TYPES} from "@glsp/sprotty-client/lib";

export class AttributeTypesAction  implements RequestAction<ReturnAttributeTypesAction> {
    static readonly KIND = 'getAttributeTypes';
    kind = AttributeTypesAction.KIND;
    constructor(public readonly requestId: string = generateRequestId()) { }
}

export class ReturnAttributeTypesAction implements ResponseAction {
    static readonly KIND = 'returnAttributeTypes';
    kind = ReturnAttributeTypesAction.KIND;
    types: string[];
    constructor(public readonly actions: string[], public readonly responseId: string = '') {
        this.types = actions;
    }
}

injectable()
export class EditLabelUIAutocomplete extends EditLabelUI {

    protected showAutocomplete: boolean = false;
    protected outerDiv: HTMLElement;
    protected listContainer: HTMLElement;
    protected currentFocus: number;
    protected types: string[] = [];


    constructor(@inject(TYPES.IActionDispatcher) protected actionDispatcher: GLSPActionDispatcher) {
        super();
    }

    protected initializeContents(containerElement: HTMLElement) {
        this.outerDiv = containerElement;
        super.initializeContents(containerElement);

        // request possible element types
        this.actionDispatcher.requestUntil(new AttributeTypesAction()).then(response => {
            if (response) {
                const action: ReturnAttributeTypesAction = <ReturnAttributeTypesAction> response;
                this.types = action.types;
            }
        });
    }

    protected handleKeyDown(event: KeyboardEvent) {
        super.handleKeyDown(event);

        if (matchesKeystroke(event, 'Space', 'ctrl')) {
            this.showAutocomplete = true;
            if (this.isAutoCompleteEnabled()) {
                this.createAutocomplete();
            }
        }

        this.updateAutocomplete(event);
    }

    protected validateLabelIfContentChange(event: KeyboardEvent, value: string) {
        if (this.isAutoCompleteEnabled() && this.previousLabelContent !== value) {
            // recreate autocomplete list if value changed
            this.createAutocomplete();
        }
        super.validateLabelIfContentChange(event, value);
    }

    protected updateAutocomplete(event: KeyboardEvent) {
        if (matchesKeystroke(event, 'ArrowDown')) {
            this.currentFocus++;
            this.addActive();
        } else if (matchesKeystroke(event, 'ArrowUp')) {
            this.currentFocus--;
            this.addActive();
        } else if (matchesKeystroke(event, 'Enter')) {
            event.preventDefault();
            if (this.currentFocus > -1) {
                if (this.listContainer) {
                    const children = this.listContainer.children;
                    (<HTMLElement>children[this.currentFocus]).click();
                }
            }
        }
    }

    protected createAutocomplete() {
        const input: String = this.inputElement.value;
        let val: String = "";
        if (input.includes(":")) {
            val = input.split(":")[1].trim();
        }

        this.closeAllLists();
        this.currentFocus = -1;

        this.listContainer = document.createElement("div");
        this.listContainer.setAttribute("id", this.inputElement.id + "autocomplete-list");
        this.listContainer.setAttribute("class", "autocomplete-items");
        this.outerDiv.appendChild(this.listContainer);

        // create autocomlete items starting with input
        for (let i = 0; i < this.types.length; i++) {
            if (this.types[i].substr(0, val.length).toLowerCase() === val.toLowerCase()) {
                const element = document.createElement("div");
                element.innerHTML = "<strong>" + this.types[i].substr(0, val.length) + "</strong>";
                element.innerHTML += this.types[i].substr(val.length);
                element.innerHTML += "<input type='hidden' value='" + this.types[i] + "'>";
                element.addEventListener("click", e => {
                    // change the type of the label
                    let name: String = this.inputElement.value;
                    if (name.includes(":")) {
                        name = name.split(":")[0];
                    }
                    this.inputElement.value = name + ": " + element.getElementsByTagName("input")[0].value;
                    this.closeAllLists();
                });
                this.listContainer.appendChild(element);
            }
        }
    }

    protected addActive() {
        if (!this.listContainer) return;
        this.removeActive();
        const children = this.listContainer.children;
        if (children.length > 0) {
            if (this.currentFocus >= children.length) this.currentFocus = 0;
            if (this.currentFocus < 0) this.currentFocus = (children.length - 1);
            children[this.currentFocus].classList.add("autocomplete-active");
        }
      }

      protected removeActive() {
        const children = this.listContainer.children;
        for (let i = 0; i < children.length; i++) {
            children[i].classList.remove("autocomplete-active");
        }
      }

      protected closeAllLists() {
        const x = this.outerDiv.getElementsByClassName("autocomplete-items");
        for (let i = 0; i < x.length; i++) {
            this.outerDiv.removeChild(x[i]);
         }
    }

    protected isAutoCompleteEnabled() {
        if (this.label) {
            return this.label.type === "node:attribute" && this.showAutocomplete;
        }
        return false;
    }

    public hide() {
        super.hide();
        this.showAutocomplete = false;
        this.closeAllLists();
    }
}