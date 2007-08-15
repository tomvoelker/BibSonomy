package webdav;

import java.util.Hashtable;
import java.util.Vector;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.slide.common.AbstractServiceBase;
import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.ServiceConnectionFailedException;
import org.apache.slide.common.ServiceDisconnectionFailedException;
import org.apache.slide.common.ServiceParameterErrorException;
import org.apache.slide.common.ServiceParameterMissingException;
import org.apache.slide.common.ServiceResetFailedException;
import org.apache.slide.common.Uri;
import org.apache.slide.content.NodeRevisionContent;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionDescriptors;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionAlreadyExistException;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.content.RevisionNotFoundException;
import org.apache.slide.store.ContentStore;
import org.apache.slide.store.NodeStore;
import org.apache.slide.store.RevisionDescriptorStore;
import org.apache.slide.store.RevisionDescriptorsStore;
import org.apache.slide.structure.ActionNode;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;
import org.apache.slide.structure.SubjectNode;

import webdav.helper.FileHelper;
import webdav.tree.DirectoryTree;
import webdav.tree.impl.SimpleDirectoryTree;

/**
 * This class should be the superclass for all implementations of Slide-stores.
 * 
 * @author Christian Schenk
 */
public abstract class AbstractTagStore extends AbstractServiceBase implements ContentStore, NodeStore,
		RevisionDescriptorStore, RevisionDescriptorsStore {

	/** Parameter to the directory of uploaded files */
	private final String ROOT_PARAM = "rootpath";
	/** Value of the rootpath-parameter */
	protected String ROOT_PATH;
	/** The directory-tree is saved here */
	protected DirectoryTree dirTree;

	public void setParameters(final Hashtable parameters) throws ServiceParameterErrorException,
			ServiceParameterMissingException {
		log("setParameters(" + parameters + ")");

    if (parameters.get(this.ROOT_PARAM) == null) throw new ServiceParameterErrorException(this, this.ROOT_PARAM);
    this.ROOT_PATH = (String) parameters.get(this.ROOT_PARAM);
    FileHelper.createDirectory(this.ROOT_PATH);

    this.dirTree = new SimpleDirectoryTree(this.ROOT_PATH);
	}

	// ==== Helper Methods ================================

	protected Class determineObjectNodeType(final String uri) {
		Class type;
		if (uri.startsWith("/actions")) {
			type = ActionNode.class;
		} else {
			type = SubjectNode.class;
		}
		return type;
	}

	// ==== ContentStore Methods ================================

	public abstract NodeRevisionContent retrieveRevisionContent(Uri uri,
			NodeRevisionDescriptor revisionDescriptor) throws ServiceAccessException, RevisionNotFoundException;

	public abstract void createRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
			NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionAlreadyExistException;

	public abstract void storeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
			NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionNotFoundException;

	public void removeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor)
			throws ServiceAccessException {
		log("removeRevisionContent(" + uri + ")");
	}

	// ==== NodeStore Methods ================================

	public abstract void storeObject(Uri uri, ObjectNode object) throws ServiceAccessException,
			ObjectNotFoundException;

	public abstract void createObject(Uri uri, ObjectNode object) throws ServiceAccessException,
			ObjectAlreadyExistsException;

	public abstract void removeObject(Uri uri, ObjectNode object) throws ServiceAccessException,
			ObjectNotFoundException;

	public abstract ObjectNode retrieveObject(Uri uri) throws ServiceAccessException, ObjectNotFoundException;

	// ==== RevisionDescriptorsStore Methods ================================

	public NodeRevisionDescriptors retrieveRevisionDescriptors(Uri uri) throws ServiceAccessException,
			RevisionDescriptorNotFoundException {
		log("retrieveRevisionDescriptors(" + uri + ")");

		final NodeRevisionNumber rev = new NodeRevisionNumber(1, 0);

		final Hashtable<String, NodeRevisionNumber> workingRevisions = new Hashtable<String, NodeRevisionNumber>();
		workingRevisions.put("main", rev);

		final Hashtable<String, NodeRevisionNumber> latestRevisionNumbers = new Hashtable<String, NodeRevisionNumber>();
		latestRevisionNumbers.put("main", rev);

		final Hashtable<NodeRevisionNumber, Vector> branches = new Hashtable<NodeRevisionNumber, Vector>();
		branches.put(rev, new Vector());

		return new NodeRevisionDescriptors(uri.toString(), rev, workingRevisions, latestRevisionNumbers, branches, false);
	}

	public void createRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors)
			throws ServiceAccessException {
		log("createRevisionDescriptors(" + uri + ")");
	}

	public void storeRevisionDescriptors(Uri uri, NodeRevisionDescriptors revisionDescriptors)
			throws ServiceAccessException, RevisionDescriptorNotFoundException {
		log("storeRevisionDescriptors(" + uri + ")");
	}

	public void removeRevisionDescriptors(Uri uri) throws ServiceAccessException {
		log("removeRevisionDescriptors(" + uri + ")");
	}

	// ==== RevisionDescriptorStore Methods ================================

	public void createRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor)
			throws ServiceAccessException {
		log("createRevisionDescriptor(" + uri + ")");
	}

	public void storeRevisionDescriptor(Uri uri, NodeRevisionDescriptor revisionDescriptor)
			throws ServiceAccessException, RevisionDescriptorNotFoundException {
		log("storeRevisionDescriptor(" + uri + ")");
	}

	public void removeRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber)
			throws ServiceAccessException {
		log("removeRevisionDescriptor(" + uri + ")");
	}

	public abstract NodeRevisionDescriptor retrieveRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber)
			throws ServiceAccessException, RevisionDescriptorNotFoundException;

	protected void log(String msg) {
		//if (getLogger() == null)
			//System.out.println(this.getClass().getName() + ": " + msg);
		//else
			//getLogger().log(msg, this.getClass().getName(), Logger.DEBUG);
	}

	/*
	 * AbstractServiceBase
	 */
	public void connect() throws ServiceConnectionFailedException {}

	public void reset() throws ServiceResetFailedException {}

	public void disconnect() throws ServiceDisconnectionFailedException {}

	public boolean isConnected() throws ServiceAccessException {
		return true;
	}

	public int getTransactionTimeout() throws XAException {
		return 0;
	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
		return false;
	}

	public boolean isSameRM(XAResource rm) throws XAException {
		return false;
	}

	public Xid[] recover(int flag) throws XAException {
		return new Xid[0];
	}

	public int prepare(Xid txId) throws XAException {
		return XA_OK;
		//return XA_RDONLY;
	}

	public void forget(Xid txId) throws XAException {}

	public void rollback(Xid txId) throws XAException {}

	public void end(Xid txId, int flags) throws XAException {}

	public void start(Xid txId, int flags) throws XAException {}

	public void commit(Xid txId, boolean onePhase) throws XAException {}
}