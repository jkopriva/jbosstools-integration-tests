/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jst.reddeer.tern.ui;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.eclipse.ui.dialogs.PropertyPage;

/**
 * RedDeer implementation of Tern Modules Preferences page 
 * @author Pavol Srna
 *
 */
public class TernModulesPropertyPage extends PropertyPage {
	
	public TernModulesPropertyPage(ReferencedComposite referencedComposite) {
		super(referencedComposite ,"JavaScript", "Modules");
	}

}
