package gcp.example.storage.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.spring.storage.GoogleStorageResource;

@RestController
public class WebController {
	
	@Value("${gcs-resource-test-bucket}")
	private Resource gcsFile;
	private GoogleStorageResource googleCloudStorageResource;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String readGcsFile() throws IOException {
		System.out.println("Found file "+this.gcsFile.getFilename());
		// System.out.println("URI "+this.gcsFile.getURI());
		// System.out.println("URI "+this.gcsFile.getURL());
		System.out.println("Last modified: "+this.gcsFile.lastModified());
		System.out.println("Content Length "+this.gcsFile.contentLength());
		
		googleCloudStorageResource = (GoogleStorageResource)this.gcsFile;
		if (googleCloudStorageResource.bucketExists()) {
			System.out.println("googleCloudStorageResource blob name -> " + googleCloudStorageResource.getBlobName());
			System.out.println("googleCloudStorageResource bucket name -> " + googleCloudStorageResource.getBucketName());
			System.out.println("googleCloudStorageResource file name -> " + googleCloudStorageResource.getFilename());
			System.out.println("googleCloudStorageResource storage location -> " + googleCloudStorageResource.getGoogleStorageLocation());
		}
		
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
	
	
}


