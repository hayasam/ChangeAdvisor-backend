package ch.uzh.ifi.seal.changeadvisor.source.parser;

import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.CompilationUnitBean;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.PackageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * File System parser for java projects.
 * Created by alexanderhofmann on 14.07.17.
 */
@Component
public class FSProjectParser {

    private static final Logger logger = LoggerFactory.getLogger(FSProjectParser.class);

    /**
     * Given a project root it parses the various packages and compilation units.
     *
     * @param projectRoot {@link Path} to project root.
     * @return list of package beans {@link PackageBean}.
     */
    public List<PackageBean> parse(Path projectRoot, boolean parallel) {
        DirectoryCrawler crawler = new DirectoryCrawler();
        List<Path> projectFiles = crawler.explore(projectRoot);

        Map<String, PackageBean> packageMap;
        Stream<Path> pathStream;

        if (parallel) {
            packageMap = new ConcurrentHashMap<>();
            pathStream = projectFiles.parallelStream();
        } else {
            packageMap = new HashMap<>();
            pathStream = projectFiles.stream();
        }

        pathStream.forEach(file -> {
            try {
                CompilationUnitBean compilationUnit = CompilationUnitBean.fromPath(file);
                final String packageName = compilationUnit.getPackageName();

                PackageBean packageBean = packageMap.computeIfAbsent(packageName, PackageBean::new);
                packageBean.addCompilationUnit(compilationUnit);

            } catch (IOException e) {
                logger.error(String.format("Failed to parse file at path: %s", file.toString()), e);
            }
        });

        return new ArrayList<>(packageMap.values());
    }
}
