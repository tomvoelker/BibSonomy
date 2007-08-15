package webdav;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.Uri;
import org.apache.slide.content.NodeRevisionContent;
import org.apache.slide.content.NodeRevisionDescriptor;
import org.apache.slide.content.NodeRevisionNumber;
import org.apache.slide.content.RevisionAlreadyExistException;
import org.apache.slide.content.RevisionDescriptorNotFoundException;
import org.apache.slide.content.RevisionNotFoundException;
import org.apache.slide.structure.ObjectAlreadyExistsException;
import org.apache.slide.structure.ObjectNode;
import org.apache.slide.structure.ObjectNotFoundException;

import webdav.beans.TagNode;
import webdav.helper.IdiomHelper;
import webdav.helper.PathHelper;
import webdav.helper.SlideHelper;

/**
 * This class implements a Slide-store. Because it relies on an implementation of
 * {@link webdav.tree.DirectoryTree} it doesn't make live queries to the database on every request.
 * This initial implementation did it this way, because we couldn't figure out how to circumvent the
 * internal cache (ehcache) of Slide. The problem is that the cache retrieves (probably an old)
 * value instead of asking us here and calling our methods.
 * 
 * @author Christian Schenk
 */
public class TagStore extends AbstractTagStore {

	private final String NO_CONTENT = "Nothing here";
	private final long NO_CONTENT_LENGTH = this.NO_CONTENT.getBytes().length;

	/*
	 * NodeStore
	 */
	public void createObject(Uri uri, ObjectNode object) throws ServiceAccessException,	ObjectAlreadyExistsException {
		log("createObject(" + uri + ")");
		final TagNode node = this.dirTree.getNode(uri.toString());
    if (node != null) throw new ObjectAlreadyExistsException(uri.toString());
    this.dirTree.addNode(uri.toString());
	}

	public void removeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
		log("removeObject(" + uri + ")");
		final TagNode node = this.dirTree.getNode(uri.toString());
		if (node == null) throw new ObjectNotFoundException(uri);
    if (node.isCollection()) { // should be here: || !this.dirTree.isPathWriteable(uri.toString())) {
			throw new ServiceAccessException(this, "Can't delete this (" + uri.toString() + ")");
		}
    this.dirTree.removeNode(uri.toString());
	}

	public ObjectNode retrieveObject(Uri uri) throws ServiceAccessException, ObjectNotFoundException {
		log("retrieveObject uri: '" + uri.toString() + "'");
		if (this.dirTree.getNode(uri.toString()) == null) throw new ObjectNotFoundException(uri);

		final Class type = this.determineObjectNodeType(uri.toString());
		final ObjectNode result = SlideHelper.getNodeByType(uri.toString(), type);
		final List<TagNode> children = this.dirTree.getChildren(uri.toString());
		if (children != null) {
			for (final TagNode node : children) {
				result.addChild(SlideHelper.getNodeByType(PathHelper.buildPath(uri.toString(), node.getName()), type));
			}
		}
		return result;
	}

	public void storeObject(Uri uri, ObjectNode object) throws ServiceAccessException, ObjectNotFoundException {
		log("storeObject(" + uri + ")");
		final TagNode node = this.dirTree.getNode(uri.toString());
    if (node == null) throw new ObjectNotFoundException(uri);
	}

	/*
	 * ContentStore
	 */
	public void createRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
			NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionAlreadyExistException {
		log("createRevisionContent(" + uri + ")");
		final TagNode node = this.dirTree.getNode(uri.toString());
    if (node != null) throw new RevisionAlreadyExistException(uri.toString(), revisionDescriptor.getRevisionNumber());
    this.addNode(uri, revisionContent);
	}

	public void storeRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor,
			NodeRevisionContent revisionContent) throws ServiceAccessException, RevisionNotFoundException {
		log("storeRevisionContent : uri: '" + uri.toString() + "'");
		final TagNode node = this.dirTree.getNode(uri.toString());
    if (node == null) throw new RevisionNotFoundException(uri.toString(), revisionDescriptor.getRevisionNumber());
		this.addNode(uri, revisionContent);
	}

	private void addNode(final Uri uri, final NodeRevisionContent revisionContent) throws ServiceAccessException {
		if (uri.toString().startsWith("/files/")) {
			try {
				if (this.dirTree.isPathWriteable(uri.toString())) {
					this.dirTree.addNode(uri.toString(), revisionContent.streamContent());
				} else {
					throw new ServiceAccessException(this, "You can't upload a file here. Please remove the created file by yourself, because we don't until the next update.");
				}
			} catch (final IOException ex) {
				throw new ServiceAccessException(this, ex);
			}
		}
	}

	public NodeRevisionContent retrieveRevisionContent(Uri uri, NodeRevisionDescriptor revisionDescriptor)
			throws ServiceAccessException, RevisionNotFoundException {
		log("retrieveRevisionContent: uri: '" + uri.toString()+ "'");
		final TagNode node = this.dirTree.getNode(uri.toString());
		if (node == null) throw new RevisionNotFoundException(uri.toString(), revisionDescriptor.getRevisionNumber());
		log("CONTENT: retrieveRevisionContent: ("+node.getContentLength()+")");

		final NodeRevisionContent nrc = new NodeRevisionContent();

		if (node.getContent() != null) {
			nrc.setContent(node.getContent().getBytes());
		} else if (node.getFileName() != null) {
			try {
				final InputStream is = new FileInputStream(node.getFileName());
				nrc.setContent(is);
			} catch (final FileNotFoundException ex) {
				throw new RevisionNotFoundException(uri.toString(), revisionDescriptor.getRevisionNumber());
			}
		} else {
			nrc.setContent(this.NO_CONTENT.getBytes());
		}

		return nrc;
	}

	/*
	 * RevisionDescriptorStore
	 */
	public NodeRevisionDescriptor retrieveRevisionDescriptor(Uri uri, NodeRevisionNumber revisionNumber)
			throws ServiceAccessException, RevisionDescriptorNotFoundException {
		log("retrieveRevisionDescriptor : uri: '" + uri.toString() + "' number: "	+ revisionNumber.toString());

		final TagNode node = dirTree.getNode(uri.toString());
		if (node == null) throw new RevisionDescriptorNotFoundException(uri.toString());

		final NodeRevisionDescriptor descriptor = SlideHelper.getFlyweightNodeRevisionDescriptor();

		if (node.isCollection()) {
			descriptor.setResourceType(NodeRevisionDescriptor.COLLECTION_TYPE);
		} else {
			descriptor.removeProperty(NodeRevisionDescriptor.RESOURCE_TYPE);

			final long contentLength = IdiomHelper.getTernaryExp(node.hasContent(), node.getContentLength(), this.NO_CONTENT_LENGTH);
			descriptor.setContentLength(contentLength);
			log("CONTENT: retrieveRevisionDescriptor: (" + node.getContentLength() + ")");
		}

		return descriptor;
	}
}