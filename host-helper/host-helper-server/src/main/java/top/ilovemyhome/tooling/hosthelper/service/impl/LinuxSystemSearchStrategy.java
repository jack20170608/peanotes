package top.ilovemyhome.tooling.hosthelper.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.commons.jdbi.page.Page;
import top.ilovemyhome.peanotes.commons.jdbi.page.impl.PageImpl;
import top.ilovemyhome.peanotes.commons.jdbi.page.impl.PageRequest;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchCriteria;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchResult;
import top.ilovemyhome.tooling.hosthelper.service.FileSearchStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LinuxSystemSearchStrategy implements FileSearchStrategy {

    @Override
    public Page<FileSearchResult> search(FileSearchCriteria searchCriteria, PageRequest pageRequest) {
        String command = linuxCommandBuilder.build(searchCriteria);
        List<FileSearchResult> results = new ArrayList<>();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean searchKeyWords = StringUtils.isNotBlank(searchCriteria.keywords());
            while ((line = reader.readLine()) != null) {
                logger.debug("Line: [{}].", line);
                FileSearchResult result = searchKeyWords ?
                    FileSearchResult.fromGrepResult(line)
                        .withHostLabel(searchCriteria.hostLabel())
                        .build()
                    : FileSearchResult.fromFindResult(line)
                        .withHostLabel(searchCriteria.hostLabel())
                        .build();
                results.add(result);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Command execution failed with exit code: {}", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error executing command: {}", command, e);
        }
        int startIndex = pageRequest.getOffset();
        int endIndex = Math.min(startIndex + pageRequest.getPageSize(), results.size());
        List<FileSearchResult> pageResults = results.subList(startIndex, endIndex);
        return new PageImpl<>(pageResults, pageRequest, results.size());
    }

    private LinuxCommandBuilder linuxCommandBuilder = new LinuxCommandBuilder();

    private static final Logger logger = LoggerFactory.getLogger(LinuxSystemSearchStrategy.class);
}
