package com.example;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.WebClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/proxy")
public class ProxyResource {

    @Inject
    Vertx vertx;

    @GET
    @Path("/")
    public CompletionStage<Response> root() {
        return proxy("");
    }

    @GET
    @Path("{path:.*}")
    public CompletionStage<Response> proxy(@PathParam("path") String path) {
        if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".gif") || path.endsWith(".svg")) {
            return fetchImageFromPath(path);
        }
        return fetchContentFromPath(path).thenApply(Response::ok).thenApply(Response.ResponseBuilder::build);
    }

    private CompletionStage<String> fetchContentFromPath(String path) {
        WebClientOptions options = new WebClientOptions().setFollowRedirects(false);
        WebClient client = WebClient.create(vertx, options);

        return client.getAbs("https://quarkus.io/" + path)
                .send()
                .toCompletionStage()
                .thenCompose(response -> {
                    if (response.statusCode() == 301 || response.statusCode() == 302) {
                        String location = response.getHeader("Location");
                        String newPath = location.replace("https://quarkus.io/", "");
                        return fetchContentFromPath(newPath);
                    }
                    return CompletableFuture.completedFuture(modifyContent(response.bodyAsString()));
                });
    }

    private String modifyContent(String content) {
        Pattern pattern = Pattern.compile("\\b\\w{6}\\b(?![^<]*>)");
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group() + "™");
        }
        matcher.appendTail(sb);

        String modifiedContent = sb.toString();

        modifiedContent = modifiedContent.replaceAll("href=\"/(?!proxy/)", "href=\"/proxy/");
        modifiedContent = modifiedContent.replaceAll("src=\"/(?!proxy/)", "src=\"/proxy/");

        modifiedContent = modifiedContent.replaceAll("href=\"https://quarkus.io/", "href=\"/proxy/");
        modifiedContent = modifiedContent.replaceAll("src=\"https://quarkus.io/", "src=\"/proxy/");

        modifiedContent = modifiedContent.replaceAll("<meta http-equiv=\"Content-Security-Policy\"[^>]+>", "");

        return modifiedContent;
    }

    private CompletionStage<Response> fetchImageFromPath(String path) {
        WebClientOptions options = new WebClientOptions().setFollowRedirects(true);
        WebClient client = WebClient.create(vertx, options);

        return client.getAbs("https://quarkus.io/" + path)
                .send()
                .toCompletionStage()
                .thenApply(response -> {
                    byte[] imageData = response.body().getBytes();
                    String contentType = response.getHeader("Content-Type");
                    return Response.ok(imageData).type(contentType).build();
                });
    }
}