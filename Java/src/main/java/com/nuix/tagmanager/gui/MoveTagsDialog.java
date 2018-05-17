package com.nuix.tagmanager.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.nuix.tagmanager.Tag;
import com.nuix.tagmanager.TagCleanupOption;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Toolkit;

@SuppressWarnings("serial")
public class MoveTagsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private boolean dialogResult = false;
	private JTextField txtCurrentParentTag;
	private JTable selectedTagsTable;
	private JTable affectedTagsTable;
	private JTextField txtUserSpecifiedParent;
	private JComboBox<TagCleanupOption> cleanupOptionCombo;
	
	private TagTableModel selectedTagsTableModel = new TagTableModel();
	private TagTableModel affectedTagsTableModel = new TagTableModel();
	private JRadioButton rdbtnSpecifyNewParent;
	private JRadioButton rdbtnUseExistingTag;
	private JComboBox<String> comboExistingParentTags;

	public MoveTagsDialog(List<Tag> selectedTags) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MoveTagsDialog.class.getResource("/com/nuix/tagmanager/gui/tag_blue.png")));
		selectedTagsTableModel.setTags(selectedTags);
		affectedTagsTableModel.setTags(Tag.findTagsAndDescendants(selectedTags));
		
		ButtonGroup group1 = new ButtonGroup();
		
		setModal(true);
		setTitle("Change Parent Tag");
		setSize(800,600);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 100, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
			gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
			gbc_tabbedPane.gridwidth = 4;
			gbc_tabbedPane.fill = GridBagConstraints.BOTH;
			gbc_tabbedPane.gridx = 0;
			gbc_tabbedPane.gridy = 0;
			contentPanel.add(tabbedPane, gbc_tabbedPane);
			{
				JPanel affectedTagsPanel = new JPanel();
				tabbedPane.addTab("Affected Tags", null, affectedTagsPanel, null);
				affectedTagsPanel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					affectedTagsPanel.add(scrollPane, BorderLayout.CENTER);
					{
						affectedTagsTable = new JTable(affectedTagsTableModel);
						affectedTagsTable.setFillsViewportHeight(true);
						scrollPane.setViewportView(affectedTagsTable);
					}
				}
			}
			{
				JPanel selectedTagsPanel = new JPanel();
				tabbedPane.addTab("Selected Tags", null, selectedTagsPanel, null);
				selectedTagsPanel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					selectedTagsPanel.add(scrollPane, BorderLayout.CENTER);
					{
						selectedTagsTable = new JTable(selectedTagsTableModel);
						selectedTagsTable.setFillsViewportHeight(true);
						scrollPane.setViewportView(selectedTagsTable);
					}
				}
			}
		}
		{
			JLabel lblCurrentParentTag = new JLabel("Current Parent Tag:");
			GridBagConstraints gbc_lblCurrentParentTag = new GridBagConstraints();
			gbc_lblCurrentParentTag.insets = new Insets(0, 0, 5, 5);
			gbc_lblCurrentParentTag.anchor = GridBagConstraints.EAST;
			gbc_lblCurrentParentTag.gridx = 0;
			gbc_lblCurrentParentTag.gridy = 1;
			contentPanel.add(lblCurrentParentTag, gbc_lblCurrentParentTag);
		}
		{
			txtCurrentParentTag = new JTextField();
			txtCurrentParentTag.setEditable(false);
			GridBagConstraints gbc_txtCurrentParentTag = new GridBagConstraints();
			gbc_txtCurrentParentTag.gridwidth = 3;
			gbc_txtCurrentParentTag.insets = new Insets(0, 0, 5, 0);
			gbc_txtCurrentParentTag.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCurrentParentTag.gridx = 1;
			gbc_txtCurrentParentTag.gridy = 1;
			contentPanel.add(txtCurrentParentTag, gbc_txtCurrentParentTag);
			txtCurrentParentTag.setColumns(10);
			String parentName = "";
			if(!selectedTags.get(0).isRootTag()){
				parentName = selectedTags.get(0).getParent().getName();
			}
			txtCurrentParentTag.setText(parentName);
		}
		{
			JLabel lblNewParentTag = new JLabel("New Parent Tag:");
			GridBagConstraints gbc_lblNewParentTag = new GridBagConstraints();
			gbc_lblNewParentTag.gridheight = 2;
			gbc_lblNewParentTag.anchor = GridBagConstraints.EAST;
			gbc_lblNewParentTag.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewParentTag.gridx = 0;
			gbc_lblNewParentTag.gridy = 2;
			contentPanel.add(lblNewParentTag, gbc_lblNewParentTag);
		}
		{
			rdbtnSpecifyNewParent = new JRadioButton("Specify New Parent Tag");
			
			group1.add(rdbtnSpecifyNewParent);
			rdbtnSpecifyNewParent.setSelected(true);
			GridBagConstraints gbc_rdbtnSpecifyNewParent = new GridBagConstraints();
			gbc_rdbtnSpecifyNewParent.insets = new Insets(0, 0, 5, 5);
			gbc_rdbtnSpecifyNewParent.anchor = GridBagConstraints.WEST;
			gbc_rdbtnSpecifyNewParent.gridx = 1;
			gbc_rdbtnSpecifyNewParent.gridy = 2;
			contentPanel.add(rdbtnSpecifyNewParent, gbc_rdbtnSpecifyNewParent);
		}
		{
			txtUserSpecifiedParent = new JTextField();
			GridBagConstraints gbc_txtUserSpecifiedParent = new GridBagConstraints();
			gbc_txtUserSpecifiedParent.gridwidth = 2;
			gbc_txtUserSpecifiedParent.insets = new Insets(0, 0, 5, 0);
			gbc_txtUserSpecifiedParent.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtUserSpecifiedParent.gridx = 2;
			gbc_txtUserSpecifiedParent.gridy = 2;
			contentPanel.add(txtUserSpecifiedParent, gbc_txtUserSpecifiedParent);
			txtUserSpecifiedParent.setColumns(10);
		}
		{
			rdbtnUseExistingTag = new JRadioButton("Use Existing Tag");
			
			group1.add(rdbtnUseExistingTag);
			GridBagConstraints gbc_rdbtnUseExistingTag = new GridBagConstraints();
			gbc_rdbtnUseExistingTag.insets = new Insets(0, 0, 5, 5);
			gbc_rdbtnUseExistingTag.anchor = GridBagConstraints.WEST;
			gbc_rdbtnUseExistingTag.gridx = 1;
			gbc_rdbtnUseExistingTag.gridy = 3;
			contentPanel.add(rdbtnUseExistingTag, gbc_rdbtnUseExistingTag);
		}
		{
			comboExistingParentTags = new JComboBox<String>();
			comboExistingParentTags.setEnabled(false);
			GridBagConstraints gbc_comboExistingParentTags = new GridBagConstraints();
			gbc_comboExistingParentTags.gridwidth = 2;
			gbc_comboExistingParentTags.insets = new Insets(0, 0, 5, 0);
			gbc_comboExistingParentTags.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboExistingParentTags.gridx = 2;
			gbc_comboExistingParentTags.gridy = 3;
			contentPanel.add(comboExistingParentTags, gbc_comboExistingParentTags);
		}
		{
			JLabel lblCleanupMethod = new JLabel("Old Tag Handling:");
			GridBagConstraints gbc_lblCleanupMethod = new GridBagConstraints();
			gbc_lblCleanupMethod.anchor = GridBagConstraints.EAST;
			gbc_lblCleanupMethod.insets = new Insets(0, 0, 0, 5);
			gbc_lblCleanupMethod.gridx = 0;
			gbc_lblCleanupMethod.gridy = 4;
			contentPanel.add(lblCleanupMethod, gbc_lblCleanupMethod);
		}
		{
			cleanupOptionCombo = new JComboBox<TagCleanupOption>();
			for(TagCleanupOption opt : TagCleanupOption.values()){
				cleanupOptionCombo.addItem(opt);
			}
			cleanupOptionCombo.setSelectedIndex(2);
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.gridwidth = 2;
			gbc_comboBox.insets = new Insets(0, 0, 0, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 1;
			gbc_comboBox.gridy = 4;
			contentPanel.add(cleanupOptionCombo, gbc_comboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialogResult = true;
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialogResult = false;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		for(Tag t : Tag.getAllTags()){
			comboExistingParentTags.addItem(t.getName());	
		}
		comboExistingParentTags.setSelectedIndex(0);
		
		rdbtnSpecifyNewParent.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				txtUserSpecifiedParent.setEnabled(rdbtnSpecifyNewParent.isSelected());
			}
		});
		
		rdbtnUseExistingTag.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				comboExistingParentTags.setEnabled(rdbtnUseExistingTag.isSelected());
			}
		});
	}

	public boolean getDialogResult() {
		return dialogResult;
	}

	public String getNewParentTagName(){
		if(rdbtnUseExistingTag.isSelected()){
			return (String)comboExistingParentTags.getSelectedItem();
		} else {
			return txtUserSpecifiedParent.getText();
		}
	}
	
	public TagCleanupOption getCleanupOption(){
		return (TagCleanupOption)cleanupOptionCombo.getSelectedItem();
	}
}
