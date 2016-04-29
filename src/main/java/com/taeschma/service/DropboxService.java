package com.taeschma.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.taeschma.domain.BinaryFile;
import com.taeschma.repository.BinaryFileRepository;

@Service
public class DropboxService {
	private static final Logger log = LoggerFactory.getLogger(DropboxService.class);

	@Value("${dropbox.access.token}")
	private String accessToken;

	@Value("${dropbox.image.directory}")
	private String dropboxImgDir;

	@Value("${dropbox.client.id}")
	private String clientId;

	@Value("dropbox.client.user.locale")
	private String userLocale;

	@Value("${locale.image.directory}")
	private String localeImageDir;

	private DbxRequestConfig requestConfig;

	private BinaryFileRepository binaryFileRepository;

	public DropboxService(BinaryFileRepository binaryFileRepository) {
		super();
		this.binaryFileRepository = binaryFileRepository;
	}

	public void syncFiles() {

		DbxClientV2 client = new DbxClientV2(getRequestConfig(), accessToken);
		// Get current account info
		try {

			// Get files and folder metadata from Dropbox root directory
			ListFolderResult result = client.files().listFolder(dropboxImgDir);
			
			if (result==null) {
				log.debug("No entries!");
			}
			
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					String currentName = metadata.getName();
					log.debug("currentFileName: " + currentName);
					List<BinaryFile> findByFileName = binaryFileRepository.findByFileName(currentName);
					log.debug("fbf: " + findByFileName);
					if (findByFileName == null || findByFileName.size() < 1) {
						saveFile(metadata.getName());
						log.info("File saved: " + currentName);
					} else {
						log.debug("File already exist: " + currentName);
					}
				}

				if (!result.getHasMore()) {
					break;
				}

				result = client.files().listFolderContinue(result.getCursor());
			}

		} catch (DbxException e) {
			log.error(e.toString());
			e.printStackTrace();
		}

	}

	protected void saveFile(String name) {
		DbxClientV1 client = new DbxClientV1(getRequestConfig(), accessToken);
		try {
			FileOutputStream targetFile = new FileOutputStream(new File(localeImageDir + "/" + name));
			log.debug("IMG_DIR + name: " + localeImageDir + "/" + name);

			DbxEntry.File downloadedFile = client.getFile(dropboxImgDir + "/" + name, null, targetFile);
			log.debug("Metadata: " + downloadedFile.toString());
			targetFile.close();

			if (downloadedFile.isFile()) {
				BinaryFile newFile = new BinaryFile();

				log.debug("Has Thumbnail: " + downloadedFile.mightHaveThumbnail);
				log.debug("Photo-Location: " + downloadedFile.photoInfo.location);
				log.debug("Photo-DateTime: " + downloadedFile.photoInfo.timeTaken);

				newFile.setFileName(name);
				newFile.setFileSize(downloadedFile.numBytes);

				Date lastModified = downloadedFile.lastModified;
				LocalDateTime ldt = LocalDateTime.ofInstant(lastModified.toInstant(), ZoneId.systemDefault());
				newFile.setLastModified(ldt);
				
				binaryFileRepository.save(newFile);

			}
		} catch (IOException | DbxException e) {
			e.printStackTrace();
		}

	}

	protected DbxRequestConfig getRequestConfig() {
		if (this.requestConfig == null) {
			this.requestConfig = new DbxRequestConfig(clientId, userLocale);
		}
		return this.requestConfig;
	}
}
