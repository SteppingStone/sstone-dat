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
package org.edc.sstone.dat.component.view.component.form;

import org.edc.sstone.project.ResourceType;
import org.edc.sstone.record.writer.model.ComponentPresentation;
import org.edc.sstone.record.writer.model.ComponentRecordWriter;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;
import org.edc.sstone.record.writer.model.MenuItemRecordWriter;
import org.edc.sstone.record.writer.model.QuestionRecordWriter;
import org.edc.sstone.record.writer.model.ResourceComponentRecordWriter;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;
import org.edc.sstone.record.writer.model.TextAreaComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ComponentFormFactory {

    public ComponentEditorPanel getComponentEditorPanel(IComponentRecordWriter rw) {
        MenuItemRecordWriter<?> mirw = null;

        LabeledComponent menuItemEditor = null;
        LabeledComponent menuItemStyleEditor = null;
        LabeledComponent componentEditorForm = null;
        LabeledComponent branchStyleEditor = null;
        LabeledComponent componentStyleForm = null;

        if (rw instanceof MenuItemRecordWriter<?>) {
            mirw = (MenuItemRecordWriter<?>) rw;
            menuItemEditor = new MenuItemComponentEditorPanel<MenuItemRecordWriter<?>>(mirw);
            /*
             * For module headers, we cannot change the menu item style (at least in v1.0). Maybe
             * down-the-road this could be implemented, but would slow down the rendering of the
             * project/module-selection screen.
             */
            if (!mirw.isModuleHeader()) {
                menuItemStyleEditor = styleForm("componentEditorPanel.menuItemStyleTab", mirw);
            }
            rw = mirw.getChild();
            branchStyleEditor = new BranchStyleEditorForm<MenuItemRecordWriter<?>>(mirw);
        }

        ComponentPresentation cType = rw.getPresentation();
        String componentStyleTabLabelKey = null;

        switch (cType) {
            case MenuScreen:
            case ContentScreen:
            case AnimatedScreen:
            case QuestionScreen:
                componentStyleTabLabelKey = "componentEditorPanel.screenStyleTab";
                componentEditorForm = new ScreenEditorForm<ScreenRecordWriter<?>>((ScreenRecordWriter<?>) rw);
                break;

            case AudioScreen:
                componentStyleTabLabelKey = "componentEditorPanel.screenStyleTab";
                componentEditorForm = new AudioScreenEditorForm<ScreenRecordWriter<?>>((ScreenRecordWriter<?>) rw);
                break;

            case TextArea:
            case LetterReader:
            case WordReader:
                componentEditorForm = new TextAreaFormPanel<TextAreaComponentRecordWriter<?>>(
                        (TextAreaComponentRecordWriter<?>) rw);
                break;

            case SyllableReader:
                componentEditorForm = new SyllableReaderFormPanel<TextAreaComponentRecordWriter<?>>(
                        (TextAreaComponentRecordWriter<?>) rw);
                break;

            case Question:
                componentEditorForm = new QuestionComponentForm<QuestionRecordWriter<?>>((QuestionRecordWriter<?>) rw);
                break;

            case ImagePanel:
                componentEditorForm = new ResourceComponentForm<ResourceComponentRecordWriter<?>>(
                        (ResourceComponentRecordWriter<?>) rw,
                        ResourceType.PanelImage);
                break;
        }

        if (rw instanceof ComponentRecordWriter<?>) {
            componentStyleForm = styleForm(componentStyleTabLabelKey, (ComponentRecordWriter<?>) rw);
        }

        return tabs(menuItemEditor, menuItemStyleEditor, componentEditorForm, branchStyleEditor, componentStyleForm);
    }

    protected StyleEditorForm<?> styleForm(ComponentRecordWriter<?> crw) {
        return styleForm(null, crw);
    }

    protected StyleEditorForm<?> styleForm(String tabLabelKey, ComponentRecordWriter<?> crw) {
        return new StyleEditorForm<ComponentRecordWriter<?>>(tabLabelKey, crw);
    }

    protected ComponentEditorPanel tabs(LabeledComponent... tabs) {
        return new ComponentEditorPanel(tabs);
    }
}
