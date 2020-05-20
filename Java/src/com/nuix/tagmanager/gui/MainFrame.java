package com.nuix.tagmanager.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.nuix.tagmanager.NuixConnection;
import com.nuix.tagmanager.Tag;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	
	private static Logger logger = Logger.getLogger(MainFrame.class);

	private JPanel contentPane;
	private TagTreeModel tagTreeModel;
	private JTree tagTree;
	private JPanel panel;
	private JButton btnChangeParentTag;
	private JButton btnDeleteSelectedTags;
	private JButton btnRefreshTree;
	private JPanel panel_1;
	private JLabel lblRegexNameFilter;
	private JTextField txtFilterString;
	private JButton btnFilterOnSelected;
	private JButton btnClearFilter;
	private JToolBar toolBar;
	private JLabel lblNotePipeCharacters;
	private JMenuBar menuBar;
	private JMenu mnModify;
	private JMenuItem mntmCopymoveTags;
	private JMenuItem mntmDeleteSelectedTags;

	public MainFrame() throws Exception {
		setTitle("Tag Manager");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/com/nuix/tagmanager/gui/tag_blue.png")));
		tagTreeModel = new TagTreeModel();
		tagTreeModel.reloadFromCase();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1024,768);
		setLocationRelativeTo(null);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnModify = new JMenu("Modify Tags");
		menuBar.add(mnModify);
		
		mntmCopymoveTags = new JMenuItem("Copy/Move Selected Tags...");
		mntmCopymoveTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				beginMoveTags();
			}
		});
		mnModify.add(mntmCopymoveTags);
		
		mntmDeleteSelectedTags = new JMenuItem("Delete Selected Tags");
		mntmDeleteSelectedTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				beginTagDeletion();
			}
		});
		mnModify.add(mntmDeleteSelectedTags);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{998, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 719, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.WEST;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(panel, gbc_panel);
		
		btnChangeParentTag = new JButton("Copy/Move Tags");
		btnChangeParentTag.setEnabled(false);
		btnChangeParentTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 beginMoveTags();
			}
		});
		panel.add(btnChangeParentTag);
		
		btnDeleteSelectedTags = new JButton("Delete Tags and Descendants");
		btnDeleteSelectedTags.setEnabled(false);
		btnDeleteSelectedTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				beginTagDeletion();
			}
		});
		panel.add(btnDeleteSelectedTags);
		
		btnFilterOnSelected = new JButton("Filter on Selected Tag");
		btnFilterOnSelected.setEnabled(false);
		btnFilterOnSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Tag t = getSelectedTags().get(0);
				txtFilterString.setText(t.getName().replaceAll("\\|", "\\\\|"));
			}
		});
		panel.add(btnFilterOnSelected);
		
		btnRefreshTree = new JButton("Reload Tags from Case");
		btnRefreshTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					tagTreeModel.reloadFromCase();
				} catch (Exception e) {
					CommonDialogs.showError(e.getMessage(),"Error Refreshing Tag Tree");
				}
				fullyExpandTree();
			}
		});
		panel.add(btnRefreshTree);
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		contentPane.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 300, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		lblRegexNameFilter = new JLabel("Filter Visible (regex):");
		GridBagConstraints gbc_lblRegexNameFilter = new GridBagConstraints();
		gbc_lblRegexNameFilter.insets = new Insets(0, 0, 0, 5);
		gbc_lblRegexNameFilter.anchor = GridBagConstraints.EAST;
		gbc_lblRegexNameFilter.gridx = 0;
		gbc_lblRegexNameFilter.gridy = 0;
		panel_1.add(lblRegexNameFilter, gbc_lblRegexNameFilter);
		
		txtFilterString = new JTextField();
		GridBagConstraints gbc_txtFilterString = new GridBagConstraints();
		gbc_txtFilterString.insets = new Insets(0, 0, 0, 5);
		gbc_txtFilterString.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFilterString.gridx = 1;
		gbc_txtFilterString.gridy = 0;
		panel_1.add(txtFilterString, gbc_txtFilterString);
		txtFilterString.setColumns(10);
		
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(0, 0, 0, 5);
		gbc_toolBar.gridx = 2;
		gbc_toolBar.gridy = 0;
		panel_1.add(toolBar, gbc_toolBar);
		
		btnClearFilter = new JButton("");
		btnClearFilter.setToolTipText("Clear Filter");
		toolBar.add(btnClearFilter);
		btnClearFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtFilterString.setText("");
			}
		});
		btnClearFilter.setIcon(new ImageIcon(MainFrame.class.getResource("/com/nuix/tagmanager/gui/cancel.png")));
		
		lblNotePipeCharacters = new JLabel("Note: | is a special character in regular expressions so they must be escaped with a slash, ex: \\|");
		lblNotePipeCharacters.setFont(new Font("Consolas", Font.PLAIN, 11));
		GridBagConstraints gbc_lblNotePipeCharacters = new GridBagConstraints();
		gbc_lblNotePipeCharacters.anchor = GridBagConstraints.WEST;
		gbc_lblNotePipeCharacters.insets = new Insets(0, 0, 5, 0);
		gbc_lblNotePipeCharacters.gridx = 0;
		gbc_lblNotePipeCharacters.gridy = 2;
		contentPane.add(lblNotePipeCharacters, gbc_lblNotePipeCharacters);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		tagTree = new JTree(tagTreeModel);
		scrollPane.setViewportView(tagTree);
		
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tagTree.getCellRenderer();
        Icon closedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/com/nuix/tagmanager/gui/tag_blue.png")));
        Icon openIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/com/nuix/tagmanager/gui/tag_blue.png")));
        Icon leafIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/com/nuix/tagmanager/gui/tag_green.png")));
        renderer.setClosedIcon(closedIcon);
        renderer.setOpenIcon(openIcon);
        renderer.setLeafIcon(leafIcon);
		
		setupListeners();
		fullyExpandTree();
	}
	
	private void beginMoveTags(){
		List<Tag> selectedTags = getSelectedTags();
		MoveTagsDialog dialog = new MoveTagsDialog(selectedTags);
		dialog.setVisible(true);
		if(dialog.getDialogResult() == true){
			try {
				selectedTags.get(0).changeParentTag(NuixConnection.getCurrentCase(), dialog.getNewParentTagName(), dialog.getCleanupOption());
				tagTreeModel.reloadFromCase();
			} catch (Exception e) {
				NuixConnection.logMessage(e.getMessage());
			}
		}
	}
	
	private void beginTagDeletion(){
		if(CommonDialogs.getConfirmation("Are you sure you wish to delete selected tags and their descendant tags?", "Delete Tags?")){
			List<Tag> selectedTags = getSelectedTags();
			try {
				Tag.deleteTags(NuixConnection.getCurrentCase(), selectedTags);
				tagTreeModel.reloadFromCase();
			} catch (Exception e) {
				CommonDialogs.showError(e.getMessage(), "Error Deleting Tags");
				logger.error("Error deleting tags: ",e);
			}
		}
	}
	
	private List<Tag> getSelectedTags(){
		TreePath[] paths = tagTree.getSelectionModel().getSelectionPaths();
		List<Tag> selectedTags = Arrays.stream(paths).map(p -> {
			Object component = p.getLastPathComponent();
			if(component instanceof TagTreeRoot){
				return null;
			} else {
				return (Tag)component;
			}
		}).filter(t -> t != null).collect(Collectors.toList());
		return selectedTags;
	}

	private void setupListeners() {
		tagTreeModel.addTreeModelListener(new TreeModelListener() {
			
			@Override
			public void treeStructureChanged(TreeModelEvent arg0) {
				fullyExpandTree();
			}
			
			@Override
			public void treeNodesRemoved(TreeModelEvent arg0) {
				fullyExpandTree();
			}
			
			@Override
			public void treeNodesInserted(TreeModelEvent arg0) {
				fullyExpandTree();
			}
			
			@Override
			public void treeNodesChanged(TreeModelEvent arg0) {
				fullyExpandTree();
			}
		});
		
		tagTree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				List<Tag> selectedTags = getSelectedTags();
				boolean selectedHaveSameParent = Tag.tagsHaveSameParent(selectedTags);
				
				boolean canMoveSelection = selectedHaveSameParent && selectedTags.size() > 0;
				btnChangeParentTag.setEnabled(canMoveSelection);
				mntmCopymoveTags.setEnabled(canMoveSelection);
				if(canMoveSelection){
					btnChangeParentTag.setToolTipText("All selected tags have same parent tag or be root tags.");
				} else {
					btnChangeParentTag.setToolTipText("Some selected tags have different parent tags.");
				}
				
				btnDeleteSelectedTags.setEnabled(selectedTags.size() > 0);
				btnFilterOnSelected.setEnabled(selectedTags.size() == 1);
			}
		});
		
		txtFilterString.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				tagTreeModel.setFilter(txtFilterString.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				tagTreeModel.setFilter(txtFilterString.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				tagTreeModel.setFilter(txtFilterString.getText());	
			}
		});
	}
	
	public void fullyExpandTree(){
		for (int i = 0; i < tagTree.getRowCount(); i++) {
		    tagTree.expandRow(i);
		}
	}
}
