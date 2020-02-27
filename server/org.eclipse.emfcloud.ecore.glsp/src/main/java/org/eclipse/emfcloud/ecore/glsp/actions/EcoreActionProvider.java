/********************************************************************************
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.ecore.glsp.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.server.provider.DefaultActionProvider;


public class EcoreActionProvider extends DefaultActionProvider {

    Set<Action> ecoreActions = new HashSet<>();
    
    public EcoreActionProvider() {
        super();
        addEcoreActions();
    }

    private void addEcoreActions() {
        ecoreActions.add(new AttributeTypesAction());
    }

    @Override
	public Set<Action> getActions() {
        Set<Action> actions = new HashSet<>(super.getActions());
        actions.addAll(ecoreActions);
		return actions;
	}
}
