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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.eclipsesource.glsp.ecore.enotation.Diagram;
import com.eclipsesource.glsp.ecore.enotation.NotationElement;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.impl.GModelIndexImpl;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class EcoreModelIndex extends GModelIndexImpl {
	private BiMap<String, EObject> semanticIndex;
	private Map<EObject, NotationElement> notationIndex;
	private Set<String> bidirectionalReferences;

	private EcoreModelIndex(EObject target) {
		super(target);
		semanticIndex = HashBiMap.create();
		notationIndex = new HashMap<>();
		bidirectionalReferences = new HashSet<>();
	}

	public static EcoreModelIndex get(GModelElement element) {
		EObject root = EcoreUtil.getRootContainer(element);
		EcoreModelIndex existingIndex = (EcoreModelIndex) EcoreUtil.getExistingAdapter(root, EcoreModelIndex.class);
		return Optional.ofNullable(existingIndex).orElseGet(() -> (create(element)));
	}

	public static EcoreModelIndex create(GModelElement element) {
		return new EcoreModelIndex(EcoreUtil.getRootContainer(element));
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return super.isAdapterForType(type) || EcoreModelIndex.class.equals(type);
	}

	public void indexSemantic(String id, EObject semanticElement) {
		semanticIndex.putIfAbsent(id, semanticElement);
	}

	public void indexNotation(NotationElement notationElement) {
		EObject semanticElement = notationElement.getSemanticElement().getResolvedElement();
		notationIndex.put(semanticElement, notationElement);
		semanticIndex.inverse().putIfAbsent(semanticElement, UUID.randomUUID().toString());

		if (notationElement instanceof Diagram) {
			((Diagram) notationElement).getElements().forEach(this::indexNotation);
		}
	}

	public Optional<EObject> getSemantic(String id) {
		return Optional.ofNullable(semanticIndex.get(id));
	}

	public Optional<String> getSemanticId(EObject semanticElement) {
		return Optional.ofNullable(semanticIndex.inverse().get(semanticElement));
	}

	public <T extends EObject> Optional<T> getSemantic(String id, Class<T> clazz) {
		return safeCast(Optional.ofNullable(semanticIndex.get(id)), clazz);
	}

	public Optional<EObject> getSemantic(GModelElement gModelElement) {
		return getSemantic(gModelElement.getId());
	}

	public <T extends EObject> Optional<T> getSemantic(GModelElement gModelElement, Class<T> clazz) {
		return getSemantic(gModelElement.getId(), clazz);
	}

	public Optional<NotationElement> getNotation(EObject semanticElement) {
		return Optional.ofNullable(notationIndex.get(semanticElement));
	}

	public <T extends NotationElement> Optional<T> getNotation(EObject semanticElement, Class<T> clazz) {
		return safeCast(getNotation(semanticElement), clazz);
	}

	public Optional<NotationElement> getNotation(String id) {
		return getSemantic(id).flatMap(this::getNotation);
	}

	public <T extends NotationElement> Optional<T> getNotation(String id, Class<T> clazz) {
		return safeCast(getNotation(id), clazz);
	}

	public Optional<NotationElement> getNotation(GModelElement gModelElement) {
		return getNotation(gModelElement.getId());
	}

	public <T extends NotationElement> Optional<T> getNotation(GModelElement element, Class<T> clazz) {
		return safeCast(getNotation(element), clazz);
	}

	private <T> Optional<T> safeCast(Optional<?> toCast, Class<T> clazz) {
		return toCast.filter(clazz::isInstance).map(clazz::cast);
	}

	public String add(EObject eObject) {
		if (eObject instanceof GModelElement) {
			return ((GModelElement) eObject).getId();
		}
		String id = null;
		if (eObject instanceof NotationElement) {
			EObject semanticElement = ((NotationElement) eObject).getSemanticElement().getResolvedElement();
			id = add(semanticElement);
			notationIndex.putIfAbsent(semanticElement, (NotationElement) eObject);
		} else {
			id = getSemanticId(eObject).orElse(null);
			if (id == null) {
				id = UUID.randomUUID().toString();
				indexSemantic(id, eObject);
			}

		}
		return id;

	}

	public void remove(EObject eObject) {
		if (eObject instanceof NotationElement) {
			EObject semanticElement = ((NotationElement) eObject).getSemanticElement().getResolvedElement();
			notationIndex.remove(semanticElement);
			remove(semanticElement);
			return;
		} else if (eObject instanceof GModelElement) {
			// do nothing;
			return;
		}
		semanticIndex.inverse().remove(eObject);
	}
	
	public Set<String> getBidirectionalReferences() {
		return bidirectionalReferences;
	}
}
