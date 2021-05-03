package org.bibsonomy.util;

import org.bibsonomy.services.filesystem.ProfilePictureLogic;
import org.bibsonomy.util.file.FileUtil;

import java.io.File;

public class ProfileUtils {

    private static final String FILE_EXTENSION = ProfilePictureLogic.FILE_EXTENSION;

    private String path;

    public ProfileUtils(String path) {
        this.path = path;
    }

    public boolean hasProfilePicture(final String username) {
        final String fileName = StringUtils.getMD5Hash(username) + FILE_EXTENSION;
        final String filePath = FileUtil.getFilePath(this.path, fileName);
        final File profilePicture = new File(filePath);

        return profilePicture.exists();
    }
}
