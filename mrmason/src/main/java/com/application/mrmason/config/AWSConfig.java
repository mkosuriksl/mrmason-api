package com.application.mrmason.config;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.entity.AdminSecurityEntity;
import com.application.mrmason.repository.AdminSecurityRepository;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Configuration
public class AWSConfig {

	@Autowired
	private AdminSecurityRepository adminSecurityRepository;

	private String awsAccessKey;

	private String awsSecretKey;

	private String region;

	private String BUCKET_NAME;

	private String BASE_URL;

	@Bean
	public S3Client s3Client() {

		Optional<AdminSecurityEntity> adminSecurityOptional = adminSecurityRepository.findById(1);
		if (adminSecurityOptional.isPresent()) {
			AdminSecurityEntity adminSecurity = adminSecurityOptional.get();
			awsAccessKey = adminSecurity.getAwsAccessKey();
			awsSecretKey = adminSecurity.getAwsSecretKey();
			region = adminSecurity.getRegion();
			BUCKET_NAME = adminSecurity.getBucketName();
			BASE_URL = adminSecurity.getBaseUrl();
		}
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);

		return S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds))
				.region(Region.of(region)).build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		Optional<AdminSecurityEntity> adminSecurityOptional = adminSecurityRepository.findById(1);
		if (adminSecurityOptional.isPresent()) {
			AdminSecurityEntity adminSecurity = adminSecurityOptional.get();
			awsAccessKey = adminSecurity.getAwsAccessKey();
			awsSecretKey = adminSecurity.getAwsSecretKey();
		}
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
		return S3Presigner.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds))
				.region(Region.of(region)).build();
	}

	public String getUrl(String key) {
		if (key != null && !key.isEmpty()) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(BUCKET_NAME).key(key).build();
			GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
					.signatureDuration(Duration.ofHours(1)).getObjectRequest(getObjectRequest).build();
			PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner()
					.presignGetObject(getObjectPresignRequest);
			return presignedGetObjectRequest.url().toString();
		}

		return key;
	}

	public boolean uploadFileToS3Bucket(Map<String, MultipartFile> docMap) {
		log.info(">> Image uploaded uploadFileToS3Bucket");
		for (Map.Entry<String, MultipartFile> entry : docMap.entrySet()) {
			MultipartFile file = entry.getValue();
			String fullPath = entry.getKey();
			try {
				PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(BUCKET_NAME).key(fullPath)
						.contentType(file.getContentType()).build();

				RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
				S3Client s3Client = s3Client();
				s3Client.putObject(putObjectRequest, requestBody);
				log.warn(">> Image uploaded");
			} catch (Exception e) {
				e.printStackTrace();
				log.warn(">> Image uploading getting failed ({}) ");
			}
		}
		return true;
	}

	public String uploadFileToS3Bucket(String fullPath, MultipartFile file) {
		log.info(">> Image uploaded uploadFileToS3Bucket({})", fullPath);
		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(BUCKET_NAME).key(fullPath)
					.contentType(file.getContentType()).build();

			RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
			S3Client s3Client = s3Client();
			s3Client.putObject(putObjectRequest, requestBody);
			log.warn(">> Image uploaded");
			return "https://" + BUCKET_NAME + ".s3." + region + ".amazonaws.com/" + fullPath;
//			return getImageUrl(fullPath);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn(">> Image uploading getting failed ({}) ");
		}
		return null;
	}

	public String getImageUrl(String imagePath) {
		log.info("get Image url");
		return BASE_URL + imagePath;

	}

	public String deleteImages(String imageName) {
		log.info("Delete S3 image: " + imageName);
		// s3Client().deleteBucket(BUCKET_NAME+imageName)

		return "Delete S3 image";
	}
	
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
