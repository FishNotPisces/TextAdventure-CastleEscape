import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.ResponseTransformer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

/**
 * This class provides functionalities to interact with an S3 bucket for saving and loading game states.
 * It uses AWS SDK for Java and GSON for JSON serialization/deserialization.
 */
public class S3bucket {

    private final S3Client s3;
    private final String bucketName;
    private final Gson gson;
    private boolean isCloudOn;

    /**
     * Constructs a S3bucket instance with the specified bucket name and AWS region.
     *
     * @param bucketName the name of the S3 bucket
     * @param region     the AWS region where the bucket is located
     * @param isCloudActive
     */
    public S3bucket(String bucketName, Region region, boolean isCloudActive) {
        this.isCloudOn = isCloudActive;
        if (isCloudOn) {
            String accessKeyId = getCredentials("key");
            String secretAccessKey = getCredentials("secretKey");

            if (accessKeyId == null || secretAccessKey == null) {
                throw new IllegalStateException("AWS credentials not found.");
            }

            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

            this.s3 = S3Client.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();
            this.bucketName = bucketName;
        }
        else {
            this.s3 = null;
            this.bucketName = null;
        }
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Item.class, new Item.ItemSerializer())
                .registerTypeAdapter(Item.class, new Item.ItemDeserializer())
                .registerTypeAdapter(Location.class, new Location.LocationSerializer())
                .registerTypeAdapter(Location.class, new Location.LocationDeserializer())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Checks if the credentials file exists.
     *
     * @return true if the credentials file exists, false otherwise
     * @throws IOException if an I/O error occurs
     */
    public static boolean checkCredentials() {
        FileReader tryFile;
        boolean checks = true;

        try {
            tryFile = new FileReader("src/config.dat");
            tryFile.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            checks = false;
        }
        return checks;
    }

    public static boolean checkCloudConnection() {
        boolean checks = true;

        try {
            // Retrieve the credentials and bucket name
            String bucketNameTest = getCredentials("bucketName");
            String accessKeyId = getCredentials("key");
            String secretAccessKey = getCredentials("secretKey");

            if (bucketNameTest == null || accessKeyId == null || secretAccessKey == null) {
                throw new IllegalStateException("AWS credentials or bucket name not found.");
            }

            // Create the S3Client with the retrieved credentials
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            S3Client s3 = S3Client.builder()
                    .region(Region.EU_NORTH_1)
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .build();

            // Attempt to list objects in the bucket to verify the connection
            s3.listObjectsV2(b -> b.bucket(bucketNameTest).maxKeys(1));
        } catch (S3Exception e) {
            checks = false;
            System.err.println("Cloud connection check failed: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            checks = false;
            System.err.println("Cloud connection check failed: " + e.getMessage());
            //e.printStackTrace();
        }
        return checks;
    }

    /**
     * Retrieves specific credentials from the credentials file.
     *
     * @param request the type of credential to retrieve ("key", "secretKey", "bucketName")
     * @return the requested credential as a string, or null if not found
     * @throws IOException if an I/O error occurs
     */
    public static String getCredentials(String request) {
        if (!checkCredentials()) return null;
        FileReader credentialsFile = null;
        try {
            credentialsFile = new FileReader("src/config.dat");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }

        Scanner credentials = new Scanner(credentialsFile);
        String requested = null;

        MessageDigest digest;
        byte[] hash = new byte[0];
        switch (request) {
            case "key":
                requested = credentials.nextLine();

                try {
                    digest = MessageDigest.getInstance("SHA-256");
                    hash = digest.digest(requested.getBytes(StandardCharsets.UTF_8));
                }
                catch (Exception e) {}
                String temp = Base64.getEncoder().encodeToString(hash);
                if (!temp.equals("bzyLhn1VroEOwby6OZtOCIDV4FSVa8XUtC1KQe9VMLM="))
                {
                    requested = null;
                }

                break;
            case "secretKey":
                credentials.nextLine();
                requested = credentials.nextLine();

                try {
                    digest = MessageDigest.getInstance("SHA-256");
                    hash = digest.digest(requested.getBytes(StandardCharsets.UTF_8));
                }
                catch (Exception e) {}
                String temp1 = Base64.getEncoder().encodeToString(hash);
                if (!temp1.equals("AgLbfbXiUzvS1Q9pHFdCkjvEXxRHNNWaLPkS6aAcA2Q="))
                {
                    requested = null;
                }

                break;
            case "bucketName":
                credentials.nextLine();
                credentials.nextLine();
                requested = credentials.nextLine();

                try {
                    digest = MessageDigest.getInstance("SHA-256");
                    hash = digest.digest(requested.getBytes(StandardCharsets.UTF_8));
                }
                catch (Exception e) {}
                String temp2 = Base64.getEncoder().encodeToString(hash);
                if (!temp2.equals("lkDlKJze4hQ5vtG2jsf+XXeiN19Go1mOuerH5kHbqTI="))
                {
                    requested = null;
                }

                break;
            default:
                break;
        }

        credentials.close();
        try {
            credentialsFile.close();
        } catch (IOException e) {

        }
        return requested;
    }


    /**
     * Saves the game state to an S3 bucket as a JSON file.
     *
     * @param fileName  the name of the JSON file to save
     * @param gameState the game state object to save
     */
    public String saveGameState(String fileName, Object gameState) {
        isCloudOn = checkCloudConnection();
        try {
            File jsonFile = new File(fileName);
            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(gameState, writer);
            }

            if(isCloudOn){
                s3.putObject(
                        PutObjectRequest.builder().bucket(bucketName).key(fileName).build(),
                        Paths.get(jsonFile.getPath())
                );
            }
            if (isCloudOn){
                System.out.println("Game state saved successfully.");
                return ("Game state saved successfully.");
            }else {
                System.out.println("Game state saved locally due to no internet connection.");
                return ("Game state saved locally due to no internet connection.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while saving the game");
            return "Error occurred while saving the game";
        }
    }

    /**
     * Loads the game state from an S3 bucket.
     *
     * @param <T>           the type of the game state object
     * @param keyName       the key name of the JSON file in the S3 bucket
     * @param gameStateClass the class of the game state object
     * @return the loaded game state object, or null if an error occurs
     */
    public <T> T loadGameState(String keyName, Class<T> gameStateClass) {
        isCloudOn = checkCloudConnection();
        try {
            File jsonFile;
            jsonFile = new File("downloaded-" + keyName);
            if(isCloudOn){

                if (jsonFile.exists()) {
                    Files.delete(jsonFile.toPath());
                }

                s3.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build(), ResponseTransformer.toFile(jsonFile.toPath()));
            }
            else {
                jsonFile = new File(keyName);
            }

            return gson.fromJson(Files.readString(jsonFile.toPath()), gameStateClass);
        } catch (Exception e) {
            System.err.println("Error loading game state: " + e.getMessage());
            return null;
        }
    }
}
