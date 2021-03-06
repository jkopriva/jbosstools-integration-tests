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
package org.jboss.tools.aerogear.reddeer.cordovasim;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.jboss.tools.browsersim.reddeer.BrowserSimLauncher;
import org.jboss.tools.browsersim.reddeer.BrowserSimException;
import org.jboss.tools.browsersim.reddeer.SimLauncher;
import org.jboss.tools.cordovasim.rmi.CordovasimUtil;
import org.jboss.tools.cordovasim.rmi.ICordovasimHandler;
import org.osgi.framework.Bundle;

public class CordovaSimLauncher extends SimLauncher{
	
	public static final String CORDOVASIM_API_BUNDLE = "org.jboss.tools.cordovasim.rmi";
	private static final String CORDOVASIM_MAIN_CLASS="org.jboss.tools.cordovasim.rmi.CordovasimUtil";

	public ICordovasimHandler launchCordovaSim(ContextMenuItem menuItem) {
		launchSimWithRMI(getBundles(), CORDOVASIM_MAIN_CLASS, menuItem, null);
		CordovaSimIsRunning isRunning = new CordovaSimIsRunning(CordovasimUtil.CS_HANDLER);
		new WaitUntil(isRunning, TimePeriod.LONG);
		return isRunning.getHandler();
	}
	
	private List<Bundle> getBundles() {
		Bundle csAPI = Platform.getBundle(CORDOVASIM_API_BUNDLE);
		Bundle bsAPI = Platform.getBundle(BrowserSimLauncher.BROWSERSIM_API_BUNDLE);
		Bundle reddeerCommon = Platform.getBundle("org.eclipse.reddeer.common");
		Bundle reddeerCore = Platform.getBundle("org.eclipse.reddeer.core");
		Bundle reddeerSwt = Platform.getBundle("org.eclipse.reddeer.swt");
		List<Bundle> bundles = new ArrayList<>();
		bundles.add(csAPI);
		bundles.add(bsAPI);
		bundles.add(reddeerCommon);
		bundles.add(reddeerCore);
		bundles.add(reddeerSwt);
		return bundles;
	}

	public static void stopCordovasim() {
		stopSim();
	}
	
	public class CordovaSimIsRunning extends AbstractWaitCondition {

		private Registry registry;
		private String handlerName;
		private ICordovasimHandler handler = null;

		public CordovaSimIsRunning(String handlerName) {
			try {
				this.handlerName = handlerName;
				this.registry = LocateRegistry.getRegistry();
			} catch (RemoteException e) {
				throw new BrowserSimException("Unable to get registry", e);
			}
		}

		public boolean test() {
			try {
				handler = (ICordovasimHandler) registry.lookup(handlerName);
			} catch (AccessException e1) {
				throw new BrowserSimException("access violated", e1);
			} catch (RemoteException e1) {
				throw new BrowserSimException("remote exception", e1);
			} catch (NotBoundException e1) {
				return false;
			}

			try {
				return handler.isStarted();
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}
		}

		public ICordovasimHandler getHandler() {
			return handler;
		}

		@Override
		public String errorMessageUntil() {
			ConsoleView cw = new ConsoleView();
			cw.open();
			String text = cw.getConsoleText();
			return "Error in console: " + text;
		}
		
		@Override
		public String errorMessageWhile() {
			return "CordovaSim is still running";
		}

	}

}
