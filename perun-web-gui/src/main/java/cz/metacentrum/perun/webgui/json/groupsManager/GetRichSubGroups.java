package cz.metacentrum.perun.webgui.json.groupsManager;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import cz.metacentrum.perun.webgui.client.PerunWebSession;
import cz.metacentrum.perun.webgui.client.UiElements;
import cz.metacentrum.perun.webgui.client.resources.SmallIcons;
import cz.metacentrum.perun.webgui.client.resources.TableSorter;
import cz.metacentrum.perun.webgui.json.*;
import cz.metacentrum.perun.webgui.json.keyproviders.GeneralKeyProvider;
import cz.metacentrum.perun.webgui.model.RichGroup;
import cz.metacentrum.perun.webgui.model.PerunError;
import cz.metacentrum.perun.webgui.widgets.AjaxLoaderImage;
import cz.metacentrum.perun.webgui.widgets.PerunTable;
import cz.metacentrum.perun.webgui.widgets.UnaccentMultiWordSuggestOracle;
import cz.metacentrum.perun.webgui.widgets.cells.CustomClickableInfoCellWithImageResource;

import java.util.ArrayList;

/**
 * GroupsManager/getRichSubGroupsWithAttributesByNames Method
 *
 * @author Vaclav Mach <374430@mail.muni.cz>
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class GetRichSubGroups implements JsonCallback, JsonCallbackTable<RichGroup>, JsonCallbackOracle<RichGroup> {

	// Session
	private PerunWebSession session = PerunWebSession.getInstance();
        // Attributes which we want to get 
        private ArrayList<String> attrNames;
	// Parent group id
	private int parentId;
	// Selection model
	final MultiSelectionModel<RichGroup> selectionModel = new MultiSelectionModel<RichGroup>(new GeneralKeyProvider<RichGroup>());
	// JSON URL
	static final private String JSON_URL = "groupsManager/getRichSubGroupsWithAttributesByNames";
	// Table data provider
	private ListDataProvider<RichGroup> dataProvider = new ListDataProvider<RichGroup>();
	// The table itself
	private PerunTable<RichGroup> table;
	// List in the table
	private ArrayList<RichGroup> list = new ArrayList<RichGroup>();
	// FIELD UPDATER - when user clicks on a row
	private FieldUpdater<RichGroup, String> tableFieldUpdater;
	// External events
	private JsonCallbackEvents events = new JsonCallbackEvents();
	// loader image
	private AjaxLoaderImage loaderImage = new AjaxLoaderImage();
	// oracle
	private UnaccentMultiWordSuggestOracle oracle = new UnaccentMultiWordSuggestOracle(":");
	private ArrayList<RichGroup> fullBackup = new ArrayList<RichGroup>();

	/**
	 * Creates a new instance of GroupsManager/getRichSubGroupsWithAttributesByNames method
	 *
	 * @param id Parent group id
	 */
	public GetRichSubGroups(int id, ArrayList<String> attrNames) {
		this.parentId = id;
                this.attrNames = attrNames;
	}

	/**
	 * Creates a new instance of GroupsManager/getRichSubGroupsWithAttributesByNames method with custom field updater
	 *
	 * @param id Parent group id
	 */
	public GetRichSubGroups(int id, JsonCallbackEvents events ) {
		this.parentId = id;
		this.events = events;
	}

	/**
	 * Retrieves data via RPC
	 */
	public void retrieveData() {
		String param = "group=" + this.parentId;
                
                if (!this.attrNames.isEmpty()) {
			// parse list
			for (String attrName : this.attrNames) {
				param += "&attrNames[]="+attrName;
			}
		}
                
		JsonClient js = new JsonClient();
		js.retrieveData(JSON_URL, param, this);
	}

	/**
	 * Returns the table with subgroups and custom field updater
	 * @return
	 */
	public CellTable<RichGroup> getTable(FieldUpdater<RichGroup, String> fu) {
		this.tableFieldUpdater = fu;
		return this.getTable();
	}

	/**
	 * Returns the table with subgroups.
	 * @return
	 */
	public CellTable<RichGroup> getTable() {

		// retrieve data
		retrieveData();

		// Table data provider.
		dataProvider = new ListDataProvider<RichGroup>(list);

		// Cell table
		table = new PerunTable<RichGroup>(list);
		table.setHyperlinksAllowed(false);

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(table);

		// Sorting
		ListHandler<RichGroup> columnSortHandler = new ListHandler<RichGroup>(dataProvider.getList());
		table.addColumnSortHandler(columnSortHandler);

		// table selection
		table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<RichGroup> createCheckboxManager());

		// set empty content & loader
		table.setEmptyTableWidget(loaderImage);
		loaderImage.setEmptyResultMessage("Group has no sub-groups.");

		// checkbox column column
		table.addCheckBoxColumn();
		table.addIdColumn("Group ID", tableFieldUpdater);
		table.addNameColumn(tableFieldUpdater);
		table.addDescriptionColumn(tableFieldUpdater);
                
                // Add a synchronization clicable icon column.
                Column<RichGroup, ImageResource> syncColumn = new Column<RichGroup, ImageResource>(
                    new CustomClickableInfoCellWithImageResource("click")) {
                    @Override
                    public ImageResource getValue(RichGroup object) {
                        if (object.isSyncEnabled()) {
                            return SmallIcons.INSTANCE.bulletGreenIcon();
                        } else {
                            return SmallIcons.INSTANCE.bulletRedIcon();
                        }
                    }
                    @Override
                    public String getCellStyleNames(Cell.Context context, RichGroup object) {
                            if (tableFieldUpdater != null) {
                                    return super.getCellStyleNames(context, object) + " pointer image-hover";
                            } else {
                                    return super.getCellStyleNames(context, object);
                            }
                    }
                };
                syncColumn.setFieldUpdater( new FieldUpdater<RichGroup, ImageResource>() {
                    @Override
                    public void update(int index, RichGroup object, ImageResource value) {
                        String html = "VO name: <b>"+object.getName()+"</b><br>";
                        html += "Sync. enabled: <b>"+object.isSyncEnabled()+"</b><br>";
                        if (object.isSyncEnabled()) {
                            html += "Sync. Interval: <b>"+object.getSynchronizationInterval()+"</b><br>";
                            html += "Last sync. state: <b>"+object.getLastSynchronizationState()+"</b><br>";
                            html += "Last sync. timestamp: <b>"+object.getLastSynchronizationTimestamp()+"</b><br>";
                            html += "Authoritative group: <b>"+object.getAuthoritativeGroup()+"</b><br>";
                        }
                        UiElements.generateInfo("VO synchronization info", html);
                    };
                });
                table.addColumn(syncColumn, "Synced");

		return table;

	}

	/**
	 * Sorts table by objects Name
	 */
	public void sortTable() {
		list = new TableSorter<RichGroup>().sortByService(getList());
		dataProvider.flush();
		dataProvider.refresh();
	}

	/**
	 * Add object as new row to table
	 *
	 * @param object Group to be added as new row
	 */
	public void addToTable(RichGroup object) {
		list.add(object);
		oracle.add(object.getName());
		dataProvider.flush();
		dataProvider.refresh();
	}

	/**
	 * Removes object as row from table
	 *
	 * @param object RichGroup to be removed as row
	 */
	public void removeFromTable(RichGroup object) {
		list.remove(object);
		selectionModel.getSelectedSet().remove(object);
		dataProvider.flush();
		dataProvider.refresh();
	}

	/**
	 * Clear all table content
	 */
	public void clearTable(){
		loaderImage.loadingStart();
		list.clear();
		fullBackup.clear();
		oracle.clear();
		selectionModel.clear();
		dataProvider.flush();
		dataProvider.refresh();
	}

	/**
	 * Clears list of selected items
	 */
	public void clearTableSelectedSet(){
		selectionModel.clear();
	}

	/**
	 * Return selected items from list
	 *
	 * @return return list of checked items
	 */
	public ArrayList<RichGroup> getTableSelectedList(){
		return JsonUtils.setToList(selectionModel.getSelectedSet());
	}

	/**
	 * Called, when an error occurs
	 */
	public void onError(PerunError error) {
		session.getUiElements().setLogErrorText("Error while loading Sub-Groups");
		loaderImage.loadingError(error);
		events.onError(error);
	}

	/**
	 * Called, when loading starts
	 */
	public void onLoadingStart() {
		session.getUiElements().setLogText("Loading Sub-Groups started.");
		events.onLoadingStart();
	}

	/**
	 * Called, when operation finishes successfully.
	 */
	public void onFinished(JavaScriptObject jso) {
		setList(JsonUtils.<RichGroup>jsoAsList(jso));
		sortTable();
		session.getUiElements().setLogText("Sub-Groups loaded: " + list.size());
		events.onFinished(jso);
		loaderImage.loadingFinished();
	}

	public void insertToTable(int index, RichGroup object) {
		list.add(index, object);
		oracle.add(object.getName());
		dataProvider.flush();
		dataProvider.refresh();
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
	}

	public void setCheckable(boolean checkable) {
		// TODO Auto-generated method stub
	}

	public void setList(ArrayList<RichGroup> list) {
		clearTable();
		this.list.addAll(list);
		for (RichGroup g : list) {
			oracle.add(g.getName());
		}
		dataProvider.flush();
		dataProvider.refresh();
	}

	public ArrayList<RichGroup> getList() {
		return this.list;
	}


	public UnaccentMultiWordSuggestOracle getOracle(){
		return this.oracle;
	}

	public void filterTable(String text){

		// store list only for first time
		if (fullBackup.isEmpty() || fullBackup == null) {
			fullBackup.addAll(list);
		}

		// always clear selected items
		selectionModel.clear();
		list.clear();

		if (text.equalsIgnoreCase("")) {
			list.addAll(fullBackup);
		} else {
			for (RichGroup grp : fullBackup){
				// store facility by filter
				if (grp.getName().toLowerCase().startsWith(text.toLowerCase()) ||
						grp.getName().toLowerCase().contains(":"+text.toLowerCase())) {
					list.add(grp);
						}
			}
		}

		if (list.isEmpty() && !text.isEmpty()) {
			loaderImage.setEmptyResultMessage("No sub-group matching '"+text+"' found.");
		} else {
			loaderImage.setEmptyResultMessage("Group has no sub-groups.");
		}

		dataProvider.flush();
		dataProvider.refresh();
		loaderImage.loadingFinished();
	}

	public void setOracle(UnaccentMultiWordSuggestOracle oracle) {
		this.oracle = oracle;
	}

}
