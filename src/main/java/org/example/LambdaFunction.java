package org.example;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.Runtime;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LambdaFunction {

    private final LambdaClient lambdaClient;

    public LambdaFunction() {
        this.lambdaClient = LambdaClient.builder()
                .region(Region.US_EAST_2)
                .build();
    }

    public void createFunction (String functionName, String handler, String role,
                                String zipFilePath, int memorySize, int timeout) {
        try {
            SdkBytes file = SdkBytes.fromByteArray(Files.readAllBytes(Paths.get(zipFilePath)));
            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .functionName(functionName)
                    .description("Created through aws sdk java")
                    .code(FunctionCode.builder().zipFile(file).build())
                    .handler(handler)
                    .runtime(Runtime.JAVA21)
                    .role(role)
                    .memorySize(memorySize)
                    .timeout(timeout)
                    .build();

            CreateFunctionResponse functionResponse = lambdaClient.createFunction(functionRequest);
            System.out.println("The function ARN is "+ functionResponse.functionArn());
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    //List all the lambda functions (Java

    public void listFunctions() {
        try {
            ListFunctionsResponse functionResult = lambdaClient.listFunctions();
            List<FunctionConfiguration> list = functionResult.functions();
            for (FunctionConfiguration configuration : list) {
                System.out.println("The function name is "+ configuration.functionName());
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void deleteFunction(String functionName) {
        try {
            DeleteFunctionRequest deleteRequest = DeleteFunctionRequest.builder()
                    .functionName(functionName)
                    .build();
            lambdaClient.deleteFunction(deleteRequest);
            System.out.println("Lambda function deleted: " + functionName);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        LambdaFunction lambdaFunction = new LambdaFunction();
        lambdaFunction.createFunction(
                "JavaFunction",
                "org.example.Handler::handleRequest",
                System.getenv("ROLE"),
                System.getenv("PATH"),
                512,
                60
        );
        lambdaFunction.listFunctions();
        //lambdaFunction.deleteFunction("JavaFunction");
    }
}