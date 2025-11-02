package com.sky.utils;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
public class GitHubBedUtil {

    @Value("${github-bed.token}")
    private String token;
    @Value("${github-bed.repo}")
    private String repo;
    @Value("${github-bed.branch}")
    private String branch;
    @Value("${github-bed.path:}")
    private String path;
    @Value("${github-bed.cdn}")
    private String cdn;

    public String upload(byte[] bytes, String fileName) throws IOException {
        String fullPath = (path.isEmpty() ? fileName : path + "/" + fileName).trim();

        GitHub github = new GitHubBuilder().withOAuthToken(token).build();
        GHRepository repository = github.getRepository(repo);

        try {
            GHContent old = repository.getFileContent(fullPath, branch);
            repository.createContent()
                    .branch(branch)
                    .path(fullPath)
                    .content(bytes)          // ✅ 直接字节
                    .message("Add " + fileName)
                    .sha(old.getSha())
                    .commit();
        } catch (GHFileNotFoundException e) {
            repository.createContent()
                    .branch(branch)
                    .path(fullPath)
                    .content(bytes)          // ✅ 直接字节
                    .message("Add " + fileName)
                    .commit();
        }

        return "https://cdn.jsdelivr.net/gh/" + repo + "@" + branch + "/" + fullPath;
    }
}