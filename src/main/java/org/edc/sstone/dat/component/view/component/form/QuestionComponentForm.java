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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.edc.sstone.dat.event.ComponentModelChangeListener;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.record.writer.model.QuestionRecordWriter;
import org.jdesktop.application.Action;

/**
 * @author Greg Orlowski
 */
public class QuestionComponentForm<R extends QuestionRecordWriter<?>> extends ComponentForm<R> {

    private static final long serialVersionUID = -196993676094906723L;
    private List<AnswerRow> answerRows;
    private AnswersPanel answersPanel;

    public QuestionComponentForm(R recordWriter) {
        super(recordWriter);
    }

    @Override
    protected void initModel() {
        // We must ensure answerRows is non-null here rather than in the ctor b/c this
        // method is called in the parent ctor BEFORE this classes' ctor is called. Yes, this
        // is very fragile. I should refactor the ctors
        if (answerRows == null) {
            answerRows = new ArrayList<AnswerRow>();
        }
        populateAnswerRowModel();
    }

    protected void addFormComponents() {
        JTextArea questionField = new JTextArea(getTextFieldWidth(recordWriter.getQuestion()), 40);
        DATUtil.bind(questionField)
                .withChangeListeners(new ComponentModelChangeListener())
                .toBean(recordWriter, "question");

        answersPanel = new AnswersPanel();

        addToComponentForm("questionComponentForm.label.question", new JScrollPane(questionField));
        addToComponentForm("questionComponentForm.label.answers", answersPanel);
    }

    private void populateAnswerRowModel() {
        int i = 0;
        answerRows.clear();
        for (String answer : recordWriter.getAnswerArray()) {
            answerRows.add(new AnswerRow(answer, recordWriter, i++));
        }
    }

    @Action
    public void deleteRow(ActionEvent evt) {
        for (int i = 0; i < answerRows.size(); i++) {
            AnswerRow ar = answerRows.get(i);
            if (ar.deleteAnswerButton == evt.getSource()) {
                if (recordWriter.getCorrectAnswerIndex() == i) {
                    recordWriter.setCorrectAnswerIndex(0);
                }
                List<String> answers = recordWriter.getAnswers();
                answers.remove(i);
                recordWriter.setAnswers(answers.toArray());
                answersPanel.refresh();
                DATUtil.markDirty();
                break;
            }
        }
    }

    @Action
    public void addAnswer() {
        List<String> answers = recordWriter.getAnswers();
        answers.add("");
        recordWriter.setAnswers(answers.toArray());
        DATUtil.markDirty();
        answersPanel.refresh();
    }

    @Action
    public void setCorrectAnswer(ActionEvent evt) {
        for (int i = 0; i < answerRows.size(); i++) {
            if (answerRows.get(i).correctAnswerSelector == evt.getSource()) {
                recordWriter.setAnswerIndex(i);
                break;
            }
        }
    }

    protected class AnswersPanel extends JPanel {
        private static final long serialVersionUID = 7695407779854305150L;
        GridBagConstraints gbc;
        private ButtonGroup correctAnswerRadioGroup = new ButtonGroup();
        private final JButton addAnswerButton;

        protected AnswersPanel() {
            setLayout(new GridBagLayout());

            addAnswerButton = new JButton();
            addAnswerButton.setName("questionComponentForm.addAnswerButton");
            SAFUtil.injectComponents(addAnswerButton);
            addAnswerButton.addActionListener(SAFUtil.action(QuestionComponentForm.this, "addAnswer"));

            layoutRows();
        }

        protected void refresh() {
            populateAnswerRowModel();
            for (Enumeration<AbstractButton> e = correctAnswerRadioGroup.getElements(); e.hasMoreElements();) {
                correctAnswerRadioGroup.remove(e.nextElement());
            }
            for (Component c : getComponents()) {
                remove(c);
            }
            revalidate();

            layoutRows();
            revalidate();
        }

        private void layoutRows() {
            gbc = gbc();
            for (int i = 0; i < answerRows.size(); i++) {
                AnswerRow row = answerRows.get(i);
                gbc.gridy = i;
                gbc.gridx = 0;
                addRowElement(new JScrollPane(row.answerField));
                correctAnswerRadioGroup.add(row.correctAnswerSelector);
                addRowElement(row.correctAnswerSelector);
                addRowElement(row.deleteAnswerButton);
            }

            int answerIdx = recordWriter.getCorrectAnswerIndex();
            if (answerRows.size() > 0) {
                if (answerIdx >= answerRows.size()) {
                    recordWriter.setCorrectAnswerIndex(0);
                    answerIdx = 0;
                }
                correctAnswerRadioGroup.setSelected(answerRows.get(answerIdx).correctAnswerSelector.getModel(), true);
            }

            gbc.gridy++;
            gbc.gridx = 0;
            gbc.weighty = 0.5;
            add(addAnswerButton, gbc);
        }

        protected void addRowElement(Component c) {
            gbc.fill = gbc.gridx == 0 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
            add(c, gbc);
            gbc.gridx++;
        }

        private GridBagConstraints gbc() {
            GridBagConstraints gbc = new GridBagConstraints();
            int margin = 12;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.insets = new Insets(0, 0, margin, margin * 2);
            gbc.weightx = 0.5;
            gbc.weighty = 0.00001;
            return gbc;
        }
    }

    protected class AnswerRow {

        protected final JTextArea answerField;
        protected final JRadioButton correctAnswerSelector;
        protected final JButton deleteAnswerButton;

        protected AnswerRow(String answer, Object bean, int idx) {
            answerField = new JTextArea(4, 40);
            DATUtil.bind(answerField).toBean(bean, "answerArray[" + idx + "]");

            correctAnswerSelector = new JRadioButton();
            correctAnswerSelector.addActionListener(SAFUtil.action(form(), "setCorrectAnswer"));

            deleteAnswerButton = new JButton(SAFUtil.getIcon("deleteComponentButton.icon"));
            deleteAnswerButton.addActionListener(SAFUtil.action(form(), "deleteRow"));
        }

        protected QuestionComponentForm<?> form() {
            return QuestionComponentForm.this;
        }
    }
}
