package edu.qhjy.util;

import edu.qhjy.util.cache.DictCacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictStartupLoader implements CommandLineRunner {

    private final DictCacheUtil dictCacheUtil;

    @Override
    public void run(String... args) {
        dictCacheUtil.loadAllDicts();
    }
}