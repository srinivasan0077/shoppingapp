package com.shoppingapp.utils;


import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;


public class AWSS3StorageService {

	private static AmazonS3 s3client;
	private static String bucketName="vijiyaent-ss3";
	private static final Logger logger=LogManager.getLogger(AWSS3StorageService.class);
	
	static {
		AWSCredentials credentials = new BasicAWSCredentials(
                System.getProperty("awss3_access_key"),
                System.getProperty("awss3_secret_key")
        );
		
		s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
	}
	
	public static AmazonS3 getAWSS3Client() {
		return s3client;
	}
	
	public static String uploadFileAndGetURL(MultipartFile file,String keyName) throws Exception {
		byte fileBytes[]=file.getBytes();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(fileBytes.length);
		metadata.setContentType(file.getContentType());
		metadata.setCacheControl("max-age=259200,must-revalidate");
		s3client.putObject(new PutObjectRequest(bucketName, keyName, file.getInputStream(), metadata));
		return s3client.getUrl(bucketName, keyName).toString();
	}
	
	public static void deleteFile(String keyName){
		try {
		   s3client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
		}catch (Exception e) {
			logger.log(Level.ERROR,"Exception while deleting image with image key ["+keyName+"]");
			logger.log(Level.ERROR, ExceptionCause.getStackTrace(e));
		}
	}
	
	public static S3Object getImageStream(String keyName)throws Exception {
		S3Object object = s3client.getObject(new GetObjectRequest(bucketName, keyName));
        return object;
	}
	
	public static void main(String args[]) {
		
		List<Bucket> buckets=AWSS3StorageService.getAWSS3Client().listBuckets();
		for (Bucket bucket : buckets) {
		    System.out.println(bucket.getName());
		}
		
	}
}
