package com.taeschma.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.taeschma.domain.BinaryFile;
import com.taeschma.repository.BinaryFileRepository;
import com.taeschma.service.DropboxService;

/**
 *
 * @author marco
 */

@Controller
public class IndexController {
	private static final Logger log = LoggerFactory.getLogger(IndexController.class);
	private BinaryFileRepository binaryFileRepository;
	
	public IndexController(BinaryFileRepository binaryFileRepository) {
		super();
		this.binaryFileRepository = binaryFileRepository;
	}

	@RequestMapping(value = "/")
	public String index(Model model) 
	{
		int listSize = 0;
		List<BinaryFile> list = binaryFileRepository.findAll();
		if (list!=null) {
			listSize =list.size();
		}
		model.addAttribute("listSize", listSize);
		model.addAttribute("images", list);
		return "index";
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadFile(@RequestParam(value = "id") String uuid, HttpServletResponse response)
            throws FileNotFoundException, IOException
    {
		log.debug("ID: " + uuid);
		File file = new File("/tmp/images/" + uuid);
        FileSystemResource resource = new FileSystemResource(file);
        response.setContentLength((int) resource.contentLength());
        response.setContentType("image/jpg");
        response.setHeader("Content-disposition", "attachment;filename=" + resource.getFilename());
        IOUtils.copyLarge(resource.getInputStream(), response.getOutputStream());
        response.flushBuffer();
    }
	
}
