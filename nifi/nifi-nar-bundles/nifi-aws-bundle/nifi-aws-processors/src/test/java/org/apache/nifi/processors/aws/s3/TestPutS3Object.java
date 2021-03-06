package org.apache.nifi.processors.aws.s3;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.s3.model.StorageClass;

@Ignore("For local testing only - interacts with S3 so the credentials file must be configured and all necessary buckets created")
public class TestPutS3Object {

    private final String CREDENTIALS_FILE = System.getProperty("user.home") + "/aws-credentials.properties";
    
    @Test
    public void testSimplePut() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.CREDENTAILS_FILE, CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.BUCKET, "test-bucket-00000000-0000-0000-0000-123456789012");
        runner.setProperty(PutS3Object.EXPIRATION_RULE_ID, "Expire Quickly");
        Assert.assertTrue( runner.setProperty("x-custom-prop", "hello").isValid() );
        
        for (int i=0; i < 3; i++) {
            final Map<String, String> attrs = new HashMap<>();
            attrs.put("filename", String.valueOf(i) + ".txt");
            runner.enqueue(Paths.get("src/test/resources/hello.txt"), attrs);
        }
        runner.run(3);
        
        runner.assertAllFlowFilesTransferred(PutS3Object.REL_SUCCESS, 3);
    }
    
    @Test
    public void testPutInFolder() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.BUCKET, "test-bucket-00000000-0000-0000-0000-123456789012");
        runner.setProperty(PutS3Object.CREDENTAILS_FILE, CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.EXPIRATION_RULE_ID, "Expire Quickly");
        Assert.assertTrue( runner.setProperty("x-custom-prop", "hello").isValid() );
        
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/1.txt");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attrs);
        runner.run();
        
        runner.assertAllFlowFilesTransferred(PutS3Object.REL_SUCCESS, 1);
    }


    @Test
    public void testStorageClass() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.BUCKET, "test-bucket-00000000-0000-0000-0000-123456789012");
        runner.setProperty(PutS3Object.CREDENTAILS_FILE, CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.STORAGE_CLASS, StorageClass.ReducedRedundancy.name());
        Assert.assertTrue( runner.setProperty("x-custom-prop", "hello").isValid() );
        
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/2.txt");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attrs);
        runner.run();
        
        runner.assertAllFlowFilesTransferred(PutS3Object.REL_SUCCESS, 1);
    }

    @Test
    public void testPermissions() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new PutS3Object());
        runner.setProperty(PutS3Object.BUCKET, "test-bucket-00000000-0000-0000-0000-123456789012");
        runner.setProperty(PutS3Object.CREDENTAILS_FILE, CREDENTIALS_FILE);
        runner.setProperty(PutS3Object.FULL_CONTROL_USER_LIST, "28545acd76c35c7e91f8409b95fd1aa0c0914bfa1ac60975d9f48bc3c5e090b5");
        
        final Map<String, String> attrs = new HashMap<>();
        attrs.put("filename", "folder/4.txt");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attrs);
        runner.run();
        
        runner.assertAllFlowFilesTransferred(PutS3Object.REL_SUCCESS, 1);
    }

}
