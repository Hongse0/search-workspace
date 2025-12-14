package com.sy.side.search;

import com.sy.side.search.infrastructure.krx.client.KrxListedInfoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KrxApiSmokeTestRunner implements CommandLineRunner {

    private final KrxListedInfoClient client;

    @Override
    public void run(String... args) {
        String basDt = "20220919";
        var res = client.fetch(basDt, 1);

        System.out.println("resultCode=" + res.response().header().resultCode());
        System.out.println("resultMsg=" + res.response().header().resultMsg());
        System.out.println("totalCount=" + res.response().body().totalCount());

        var items = res.response().body().items().item();
        System.out.println("items size=" + (items == null ? 0 : items.size()));

        if (items != null && !items.isEmpty()) {
            System.out.println("first=" + items.get(0));
        }
    }
}
