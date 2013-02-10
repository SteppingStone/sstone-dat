/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.saf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdesktop.application.SingleFrameApplication;

/**
 * @author Greg Orlowski
 */
public abstract class ApplicationBase extends SingleFrameApplication {

    protected ApplicationBase() {
        super();
        useSymmetricResourcePackageStructure();
        configureGlobaleMessageBundle();

        // If we need more resourceconverters, we could register them here or further down
        // ResourceConverter.register(new CharacterResourceConverter());

        // getContext().getResourceMap().injectFields(target);
    }

    /**
     * I prefer keeping resources in the same package space. Use maven's src/main/resources to
     * separate SOURCE FOLDERS, but preserving a symmetric, parallel package hierarchy is, IMHO,
     * easier to manage.
     */
    protected void useSymmetricResourcePackageStructure() {
        getContext().getResourceManager().setResourceFolder(null);
    }

    /**
     * Storing message string translations with other resource properties is convenient for
     * programmers but can confuse translators. To simplify the process of non-programmers
     * translating the messages, we store all strings for a given locale in a single properties file
     * called i18n/MessageBundle_LOCALE_CODE.properties
     */
    protected void configureGlobaleMessageBundle() {
        /*
         * We have to ensure that the application class has been set before we can explicitly set
         * the message bundle chain because otherwise getApplicationBundleNames() may not yet
         * include the application class in the chain.
         */
        getContext().setApplicationClass(getClass());

        List<String> appBundleNames = getContext().getResourceManager().getApplicationBundleNames();
        List<String> mutableList = new ArrayList<String>(appBundleNames);
        mutableList.add(0, "i18n.MessageBundle");
        getContext().getResourceManager().setApplicationBundleNames(Collections.unmodifiableList(mutableList));
    }

}
