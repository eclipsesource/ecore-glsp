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
package com.eclipsesource.glsp.ecore.util;

import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;
import com.eclipsesource.glsp.graph.GCompartment;
import com.eclipsesource.glsp.graph.GraphFactory;
import com.eclipsesource.glsp.graph.builder.AbstractGCompartmentBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GLabelBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GLayoutOptionsBuilder;

public class EcoreBuilder {

	public static class IconBuilder extends AbstractGCompartmentBuilder<GCompartment, IconBuilder> {
		private String label;

		public IconBuilder() {
			super(Types.ICON);
			layout("stack");
			layoutOptions(new GLayoutOptionsBuilder() //
					.hAlign("center") //
					.resizeContainer(false) //
					.build()); //
		}

		public IconBuilder label(String label) {
			this.label = label;
			return self();
		}

		@Override
		protected void setProperties(GCompartment comp) {
			add(new GLabelBuilder(Types.LABEL_ICON) //
					.text(label) //
					.build());
			super.setProperties(comp);
		}

		@Override
		protected GCompartment instantiate() {
			return GraphFactory.eINSTANCE.createGCompartment();
		}

		@Override
		protected IconBuilder self() {
			return this;
		}
	}
}
