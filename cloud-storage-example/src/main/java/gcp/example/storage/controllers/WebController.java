package gcp.example.storage.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.storage.GoogleStorageResource;

@RestController
public class WebController {
	
	@Value("${gcs-resource-test-bucket}")
	private Resource gcsFile;
	private GoogleStorageResource googleCloudStorageResource;
	
	Map<String,String> resourceProperties = new HashMap<>();

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String readGcsFile() throws IOException {
		System.out.println("Found file "+this.gcsFile.getFilename());
		// System.out.println("URI "+this.gcsFile.getURI());
		// System.out.println("URI "+this.gcsFile.getURL());
		System.out.println("Last modified: "+this.gcsFile.lastModified());
		System.out.println("Content Length "+this.gcsFile.contentLength());
		
		return StreamUtils.copyToString(
				this.gcsFile.getInputStream(),
				Charset.defaultCharset()) + "\n";
		
		
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	String writeGcs(@RequestBody String data) throws IOException {
		try (OutputStream os = ((WritableResource) this.gcsFile).getOutputStream()) {
			os.write(data.getBytes());
		}
		return "file was updated\n";
	}
	
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	String getDetails() throws IOException {
		
		googleCloudStorageResource = (GoogleStorageResource)this.gcsFile;
		
		if (googleCloudStorageResource.bucketExists()) {
			
			resourceProperties.put("googleCloudStorageResource.getBlobName", googleCloudStorageResource.getBlobName());
			resourceProperties.put("googleCloudStorageResource.getBucketName", googleCloudStorageResource.getBucketName());
			resourceProperties.put("googleCloudStorageResource.getFileName", googleCloudStorageResource.getFilename());
			resourceProperties.put("googleCloudStorageResource.getStorageLocation", googleCloudStorageResource.getGoogleStorageLocation().toString());
		}
		
		String json = new ObjectMapper().writeValueAsString(resourceProperties);
		return json;		
	}
	
	
}


